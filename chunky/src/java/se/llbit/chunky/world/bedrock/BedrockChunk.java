package se.llbit.chunky.world.bedrock;

import io.github.notstirred.leveldb_ffi.LevelDBException;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import se.llbit.chunky.chunk.*;
import se.llbit.chunky.map.BiomeLayer;
import se.llbit.chunky.map.SurfaceLayer;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.Dimension;
import se.llbit.chunky.world.EmptyChunk;
import se.llbit.chunky.world.biome.ArrayBiomePalette;
import se.llbit.chunky.world.biome.BiomePalette;
import se.llbit.log.Log;
import se.llbit.nbt.*;
import se.llbit.util.Mutable;
import se.llbit.util.annotation.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class BedrockChunk extends Chunk {
  private boolean renderedToMap = false;

  public BedrockChunk(ChunkPosition pos, BedrockDimension dimension) {
    super(pos, dimension);
  }

  @Override
  public boolean loadChunk(@NotNull Mutable<ChunkData> chunkDataMutable, int yMin, int yMax) {
    if (renderedToMap) {
      return false;
    }
    renderedToMap = true;

    BlockPalette palette = new BlockPalette();
    palette.unsynchronize();
    BiomePalette biomePalette = new ArrayBiomePalette();

    chunkDataMutable.set(this.dimension.createChunkData(chunkDataMutable.get(), 0, 256));
    try {
      ChunkData chunkData = chunkDataMutable.get();
      boolean readData = readChunkData(chunkData, palette, biomePalette, yMin, yMax);
      if (!readData) {
        ((BedrockDimension) dimension).setChunk(position, EmptyChunk.INSTANCE);
        return false;
      }
      readBiomeData(chunkData, biomePalette, yMin, yMax);

      int[] heightmapData = new int[Chunk.X_MAX * Chunk.Z_MAX];
      Arrays.fill(heightmapData, 256);

      biomes = new BiomeLayer(chunkData, biomePalette);
      surface = new SurfaceLayer(dimension.getDimensionId(), chunkData, palette, biomePalette, yMin, yMax, heightmapData);
      updateHeightmap(dimension.getHeightmap(), this.position, chunkData, heightmapData, palette, yMax);
      queueTopography();
    } catch (ChunkLoadingException e) {
      Log.warn(String.format("Failed to load chunk %s", position), e);
    }

    return true;
  }

  private void readBiomeData(ChunkData chunkData, BiomePalette biomePalette, int yMin, int yMax) {
    byte Data3D = 0x2B;

    // TODO: biome data

  }

  public static int ceilDiv(int x, int y) {
    final int q = x / y;
    // if the signs are the same and modulo not zero, round up
    if ((x ^ y) >= 0 && (q * y != x)) {
      return q + 1;
    }
    return q;
  }

  @Override
  public void getChunkData(@NotNull Mutable<ChunkData> reuseChunkData, BlockPalette palette, BiomePalette biomePalette, int minY, int maxY) throws ChunkLoadingException {
    if (reuseChunkData.get() == null) {
      reuseChunkData.set(new GenericChunkData());
    } else {
      reuseChunkData.get().clear();
    }

    readChunkData(reuseChunkData.get(), palette, biomePalette, minY, maxY);
  }

  public Optional<byte[]> readSubChunk(ChunkPosition pos, byte subChunkIdx) throws LevelDBException {
    // Create subchunk key
    boolean dimensionIsOverworld = dimension.getDimensionId().equals(Dimension.Identifier.OVERWORLD);
    int subChunkKeySize = dimensionIsOverworld ? 10 : 14;
    ByteBuffer byteBuffer = ByteBuffer.allocate(subChunkKeySize).order(ByteOrder.LITTLE_ENDIAN)
      .putInt(pos.x)
      .putInt(pos.z);

    if (!dimensionIsOverworld) {
      byteBuffer.putInt(switch (dimension.getDimensionId().getNamespacedName()) {
        case "minecraft:the_nether" -> 1;
        case "minecraft:the_end" -> 2;
        default -> throw new RuntimeException("Unsupported dimension in Bedrock world"); // TODO: should this throw?
      });
    }
    byteBuffer.put((byte) 0x2f);
    byteBuffer.put(subChunkIdx);
    return ((BedrockDimension) dimension).getDbValue(byteBuffer.array());
  }

  private boolean readChunkData(ChunkData chunkData, BlockPalette palette, BiomePalette biomePalette, int minY, int maxY) throws ChunkLoadingException {
    // A great resource on bedrock's binary formats: https://github.com/Team-Lodestone/Documentation/tree/main/Bedrock/LevelDB_Output_Array_Formats

    boolean dataPresent = false;
    for (byte subchunkIdx = 0; subchunkIdx < 16; subchunkIdx++) {
      try {
        Optional<byte[]> dbValue = readSubChunk(this.position, subchunkIdx);
        if (dbValue.isEmpty()) {
          continue;
        }
        dataPresent = true;
        ByteBuffer value = ByteBuffer.wrap(dbValue.get()).order(ByteOrder.LITTLE_ENDIAN);

        // Parse subchunk
        int version = value.get();
        int numStorages = value.get();
        int yIndex = value.get();

        for (int storage = 0; storage < numStorages; storage++) {
          int packed = value.get();
          boolean isRuntime = (packed & 1) != 0;
          assert !isRuntime : "Runtime state on disk?!";
          int bitsPerBlock = packed >> 1;
          int mask = (1 << bitsPerBlock)-1;

          int blocksPerWord = 32 / bitsPerBlock;
          int wordCount = ceilDiv(4096, blocksPerWord);

          ByteBuffer blockData = value.slice().order(ByteOrder.LITTLE_ENDIAN);
          value.position(value.position() + wordCount * 4);

          int b = value.getInt();

          Tag[] subpalette = new Tag[b];
          NBTInputStream tags = NbtUtils.createReaderLE(new BedrockDimension.ByteBufferBackedInputStream(value));

          for (int i = 0; i < b; i++) {
            NbtMap compound = (NbtMap) tags.readTag();
            String name = compound.getString("name");
            subpalette[i] = new CompoundTag(List.of(new NamedTag("Name", new StringTag(name))));
          }

          int u = 0;
          for (int j = 0; j < wordCount; j++) {
            int temp = blockData.getInt();

            for (int k = 0; k < blocksPerWord && u < 4096; k++) {
              int x = (u >> 8) & 0xf;
              int y = u & 0xf;
              int z = (u >> 4) & 0xf;
              int pos = x + 16 * y + 256 * z;

              int subpaletteIdx = (temp & mask);
              chunkData.setBlockAt(x, 16 * yIndex + y, z, palette.put(subpalette[subpaletteIdx]));

              temp >>= bitsPerBlock;
              u++;
            }
          }

          yIndex += 1;
        }

      } catch (LevelDBException | IOException e) {
        throw new ChunkLoadingException("Exception thrown when loading chunk " + this.position, e);
      }
    }
    return dataPresent;
  }

  public static Tag read(DataInputStream in) {
    try {
      byte type = in.readByte();
      if (type == 0) {
        return Tag.END;
      } else {
        SpecificTag name = StringTag.read(in);
        SpecificTag payload = SpecificTag.read(type, in);
        return new NamedTag(name.stringValue(), payload);
      }
    } catch (IOException e) {
      return new ErrorTag("IOException while reading tag type:\n" + e.getMessage());
    }
  }
}
