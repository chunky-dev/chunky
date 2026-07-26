package se.llbit.chunky.world.bedrock;

import io.github.notstirred.leveldb_ffi.LevelDBException;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import se.llbit.chunky.chunk.*;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.Dimension;
import se.llbit.chunky.world.biome.BiomePalette;
import se.llbit.nbt.*;
import se.llbit.util.Mutable;
import se.llbit.util.annotation.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Optional;

public class BedrockChunk extends Chunk {

  public BedrockChunk(ChunkPosition pos, BedrockDimension dimension) {
    super(pos, dimension);
  }

  @Override
  public boolean loadChunk(@NotNull Mutable<ChunkData> chunkData, int yMin, int yMax) {
    return false;
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

    // A great resource on bedrock's binary formats: https://github.com/Team-Lodestone/Documentation/tree/main/Bedrock/LevelDB_Output_Array_Formats

    for (byte subchunkIdx = 0; subchunkIdx < 16; subchunkIdx++) {
      // Create subchunk key
      boolean dimensionIsOverworld = this.dimension.getDimensionId().equals(Dimension.Identifier.OVERWORLD);
      int subChunkKeySize = dimensionIsOverworld ? 10 : 14;
      ByteBuffer byteBuffer = ByteBuffer.allocate(subChunkKeySize).order(ByteOrder.LITTLE_ENDIAN)
        .putInt(this.position.x).putInt(this.position.z);
      if (!dimensionIsOverworld) {
        byteBuffer.putInt(switch (this.dimension.getDimensionId().getNamespacedName()) { // TODO in Java 21+ we can use `switch (dimensionId)` here
          case "minecraft:the_nether" -> 1;
          case "minecraft:the_end" -> 2;
          default -> throw new RuntimeException("Unsupported dimension in Bedrock world"); // TODO: should this throw?
        });
      }
      byteBuffer.put((byte) 0x2f);
      byteBuffer.put(subchunkIdx);

      try {
        BedrockDimension dim = (BedrockDimension) this.dimension;
        Optional<byte[]> dbValue = dim.getDbValue(byteBuffer.array());
        if (dbValue.isEmpty()) {
          return;
        }
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
          ChunkData chunkData = reuseChunkData.get();

          int b = value.getInt();

          Tag[] subpalette = new Tag[b];
//          int bufPos = value.position();
//          ByteBuffer allocate = ByteBuffer.allocate(value.capacity()).order(ByteOrder.LITTLE_ENDIAN);
//          allocate.put(value);
//          allocate.position(bufPos);
//          value.position(bufPos);
//          Tag tag = NamedTag.read(new LittleEndianDataInputStream(new DataInputStream(new BedrockDimension.ByteBufferBackedInputStream(value))));
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
