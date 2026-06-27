package se.llbit.chunky.world.bedrock;

import io.github.notstirred.leveldb_ffi.LevelDBException;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import se.llbit.chunky.chunk.*;
import se.llbit.chunky.chunk.biome.BiomeData;
import se.llbit.chunky.chunk.biome.GenericBiomeData3d;
import se.llbit.chunky.map.BiomeLayer;
import se.llbit.chunky.map.SurfaceLayer;
import se.llbit.chunky.world.*;
import se.llbit.chunky.world.biome.ArrayBiomePalette;
import se.llbit.chunky.world.biome.Biome;
import se.llbit.chunky.world.biome.BiomePalette;
import se.llbit.chunky.world.biome.Biomes;
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
  private final byte Data3D_KEY = 0x2b;
  private final byte Version_KEY = 0x2c;
  private final byte SubChunkPrefix_KEY = 0x2f;
  private final byte BlockEntity_KEY = 0x31;
  private final byte Entity_KEY = 0x32;

  private static final Int2ObjectOpenHashMap<Biome> bedrockBiomesById = new Int2ObjectOpenHashMap<>();

  /**
   * Bedrock has no chunk timestamps like java, so we render to the map once.
   *
   * Additionally chunky should NEVER support having a bedrock world open in MC and itself,
   * leveldb doesn't support this.
   */
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
        ((BedrockDimension) dimension).setChunk(position, EmptyRegionChunk.INSTANCE);
        return false;
      }
      readData3D(chunkData, palette, biomePalette, yMax);

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
    readData3D(reuseChunkData.get(), palette, biomePalette, maxY);
  }

  public Optional<byte[]> readSubChunk(ChunkPosition pos, byte subChunkIdx) throws LevelDBException {
    ByteBuffer byteBuffer = createDBKey(pos, SubChunkPrefix_KEY);
    byteBuffer.put(subChunkIdx);
    return ((BedrockDimension) dimension).getDbValue(byteBuffer.array());
  }

  private Optional<byte[]> readDBValue(ChunkPosition pos, byte key) throws LevelDBException {
    return ((BedrockDimension) dimension).getDbValue(createDBKey(pos, key).array());
  }

  @NotNull
  private ByteBuffer createDBKey(ChunkPosition pos, byte keyType) {
    boolean dimensionIsOverworld = dimension.getDimensionId() == Dimension.Identifier.OVERWORLD;
    int keySize = 9; // minimum key size, XZ+KEY
    if (!dimensionIsOverworld)
      keySize += 4; // +4 bytes for dimension ID
    if (keyType == SubChunkPrefix_KEY) {
      keySize++; // +1 byte for subchunk index
    }

    ByteBuffer byteBuffer = ByteBuffer.allocate(keySize).order(ByteOrder.LITTLE_ENDIAN)
      .putInt(pos.x)
      .putInt(pos.z);

    if (!dimensionIsOverworld) {
      byteBuffer.putInt(switch (dimension.getDimensionId().getNamespacedName()) {
        case "minecraft:the_nether" -> 1;
        case "minecraft:the_end" -> 2;
        default -> throw new RuntimeException("Unsupported dimension in Bedrock world"); // TODO: should this throw?
      });
    }
    byteBuffer.put(keyType);
    return byteBuffer;
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

          if (bitsPerBlock == 0) { // all-same subchunk
            value.position(value.position() + 4); // no palette or other data exists, guessing this means an all-air chunk
            continue;
          }

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

  private void readData3D(ChunkData chunkData, BlockPalette palette, BiomePalette biomePalette, int maxY) {
    try {
      Optional<byte[]> bytes = readDBValue(position, Data3D_KEY);
      if (bytes.isEmpty()) {
        return;
      }

      ByteBuffer data3d = ByteBuffer.wrap(bytes.get()).order(ByteOrder.LITTLE_ENDIAN);
      // HEIGHTMAP:
      int[] heightmapData = new int[16*16];
      for (int x = 0; x < Chunk.X_MAX; x++) {
        for (int z = 0; z < Chunk.Z_MAX; z++) {
          heightmapData[x * Chunk.Z_MAX + z] = data3d.getShort();
        }
      }
      updateHeightmap(this.dimension.getHeightmap(), position, chunkData, heightmapData, palette, maxY);

      // BIOMES:
      BiomeData biomeDataStorage = new GenericBiomeData3d();
      chunkData.setBiomeData(biomeDataStorage);
      for (int paletteIdx = 0; paletteIdx < 24; paletteIdx++) {
        int packed = data3d.get() & 0xff; // & because java and unsigned is dumb.
        int isRuntime = (packed & 1);
        assert isRuntime == 1 : "Biomes are currently only stored as runtime IDs";
        int bitsPerBlock = packed >> 1;
        int mask = (1 << bitsPerBlock)-1;

        if (bitsPerBlock == 127) { // null subchunk
          continue;
        }
        if (bitsPerBlock == 0) { // all-same subchunk
          int singleBiome = data3d.getInt();

          Biome biome = bedrockBiomesById.get(singleBiome);
          for (int x = 0; x < Chunk.X_MAX; x++) {
            for (int z = 0; z < Chunk.Z_MAX; z++) {
              for (int y = 0; y < Chunk.SECTION_Y_MAX; y++) {
                biomeDataStorage.setBiomeAt(x, 16 * paletteIdx + y, z, biomePalette.put(biome));
              }
            }
          }
          break;
        }

        int blocksPerWord = 32 / bitsPerBlock;
        int wordCount = ceilDiv(4096, blocksPerWord);

        ByteBuffer biomeData = data3d.slice().order(ByteOrder.LITTLE_ENDIAN);
        data3d.position(data3d.position() + wordCount * 4);

        int paletteSize = data3d.getInt();
        int[] subpalette = new int[paletteSize];
        for (int i = 0; i < paletteSize; i++) {
          subpalette[i] = data3d.getInt();
        }

        int u = 0;
        for (int j = 0; j < wordCount; j++) {
          int temp = biomeData.getInt();

          for (int k = 0; k < blocksPerWord && u < 4096; k++) {
            int x = (u >> 8) & 0xf;
            int y = u & 0xf;
            int z = (u >> 4) & 0xf;
            int pos = x + 16 * y + 256 * z;

            int subpaletteIdx = (temp & mask);

            Biome biome = bedrockBiomesById.get(subpalette[subpaletteIdx]);
            biomeDataStorage.setBiomeAt(x, 16 * paletteIdx + y, z, biomePalette.put(biome));

            temp >>= bitsPerBlock;
            u++;
          }
        }
      }
    } catch (LevelDBException e) {
      Log.error("Exception thrown when loading chunk DATA3D " + this.position, e);
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

  static {
    // These IDs can and do differ between bedrock versions, we may need to parse the behaviour pack(?) from the game files to do this properly
    // but jank for now!
    bedrockBiomesById.put(0, Biomes.biomesByResourceLocation.getOrDefault("minecraft:ocean", Biomes.unknown));
    bedrockBiomesById.put(1, Biomes.biomesByResourceLocation.getOrDefault("minecraft:plains", Biomes.unknown));
    bedrockBiomesById.put(2, Biomes.biomesByResourceLocation.getOrDefault("minecraft:desert", Biomes.unknown));
    bedrockBiomesById.put(3, Biomes.biomesByResourceLocation.getOrDefault("minecraft:extreme_hills", Biomes.unknown));
    bedrockBiomesById.put(4, Biomes.biomesByResourceLocation.getOrDefault("minecraft:forest", Biomes.unknown));
    bedrockBiomesById.put(5, Biomes.biomesByResourceLocation.getOrDefault("minecraft:taiga", Biomes.unknown));
    bedrockBiomesById.put(6, Biomes.biomesByResourceLocation.getOrDefault("minecraft:swampland", Biomes.unknown));
    bedrockBiomesById.put(7, Biomes.biomesByResourceLocation.getOrDefault("minecraft:river", Biomes.unknown));
    bedrockBiomesById.put(8, Biomes.biomesByResourceLocation.getOrDefault("minecraft:hell", Biomes.unknown));
    bedrockBiomesById.put(9, Biomes.biomesByResourceLocation.getOrDefault("minecraft:the_end", Biomes.unknown));
    bedrockBiomesById.put(10, Biomes.biomesByResourceLocation.getOrDefault("minecraft:frozen_ocean", Biomes.unknown));
    bedrockBiomesById.put(11, Biomes.biomesByResourceLocation.getOrDefault("minecraft:frozen_river", Biomes.unknown));
    bedrockBiomesById.put(12, Biomes.biomesByResourceLocation.getOrDefault("minecraft:ice_plains", Biomes.unknown));
    bedrockBiomesById.put(13, Biomes.biomesByResourceLocation.getOrDefault("minecraft:ice_mountains", Biomes.unknown));
    bedrockBiomesById.put(14, Biomes.biomesByResourceLocation.getOrDefault("minecraft:mushroom_island", Biomes.unknown));
    bedrockBiomesById.put(15, Biomes.biomesByResourceLocation.getOrDefault("minecraft:mushroom_island_shore", Biomes.unknown));
    bedrockBiomesById.put(16, Biomes.biomesByResourceLocation.getOrDefault("minecraft:beach", Biomes.unknown));
    bedrockBiomesById.put(17, Biomes.biomesByResourceLocation.getOrDefault("minecraft:desert_hills", Biomes.unknown));
    bedrockBiomesById.put(18, Biomes.biomesByResourceLocation.getOrDefault("minecraft:forest_hills", Biomes.unknown));
    bedrockBiomesById.put(19, Biomes.biomesByResourceLocation.getOrDefault("minecraft:taiga_hills", Biomes.unknown));
    bedrockBiomesById.put(20, Biomes.biomesByResourceLocation.getOrDefault("minecraft:extreme_hills_edge", Biomes.unknown));
    bedrockBiomesById.put(21, Biomes.biomesByResourceLocation.getOrDefault("minecraft:jungle", Biomes.unknown));
    bedrockBiomesById.put(22, Biomes.biomesByResourceLocation.getOrDefault("minecraft:jungle_hills", Biomes.unknown));
    bedrockBiomesById.put(23, Biomes.biomesByResourceLocation.getOrDefault("minecraft:jungle_edge", Biomes.unknown));
    bedrockBiomesById.put(24, Biomes.biomesByResourceLocation.getOrDefault("minecraft:deep_ocean", Biomes.unknown));
    bedrockBiomesById.put(25, Biomes.biomesByResourceLocation.getOrDefault("minecraft:stone_beach", Biomes.unknown));
    bedrockBiomesById.put(26, Biomes.biomesByResourceLocation.getOrDefault("minecraft:cold_beach", Biomes.unknown));
    bedrockBiomesById.put(27, Biomes.biomesByResourceLocation.getOrDefault("minecraft:birch_forest", Biomes.unknown));
    bedrockBiomesById.put(28, Biomes.biomesByResourceLocation.getOrDefault("minecraft:birch_forest_hills", Biomes.unknown));
    bedrockBiomesById.put(29, Biomes.biomesByResourceLocation.getOrDefault("minecraft:roofed_forest", Biomes.unknown));
    bedrockBiomesById.put(30, Biomes.biomesByResourceLocation.getOrDefault("minecraft:cold_taiga", Biomes.unknown));
    bedrockBiomesById.put(31, Biomes.biomesByResourceLocation.getOrDefault("minecraft:cold_taiga_hills", Biomes.unknown));
    bedrockBiomesById.put(32, Biomes.biomesByResourceLocation.getOrDefault("minecraft:mega_taiga", Biomes.unknown));
    bedrockBiomesById.put(33, Biomes.biomesByResourceLocation.getOrDefault("minecraft:mega_taiga_hills", Biomes.unknown));
    bedrockBiomesById.put(34, Biomes.biomesByResourceLocation.getOrDefault("minecraft:extreme_hills_plus_trees", Biomes.unknown));
    bedrockBiomesById.put(35, Biomes.biomesByResourceLocation.getOrDefault("minecraft:savanna", Biomes.unknown));
    bedrockBiomesById.put(36, Biomes.biomesByResourceLocation.getOrDefault("minecraft:savanna_plateau", Biomes.unknown));
    bedrockBiomesById.put(37, Biomes.biomesByResourceLocation.getOrDefault("minecraft:mesa", Biomes.unknown));
    bedrockBiomesById.put(38, Biomes.biomesByResourceLocation.getOrDefault("minecraft:mesa_plateau", Biomes.unknown));
    bedrockBiomesById.put(39, Biomes.biomesByResourceLocation.getOrDefault("minecraft:mesa_plateau_stone", Biomes.unknown));
    bedrockBiomesById.put(40, Biomes.biomesByResourceLocation.getOrDefault("minecraft:warm_ocean", Biomes.unknown));
    bedrockBiomesById.put(41, Biomes.biomesByResourceLocation.getOrDefault("minecraft:deep_warm_ocean", Biomes.unknown));
    bedrockBiomesById.put(42, Biomes.biomesByResourceLocation.getOrDefault("minecraft:lukewarm_ocean", Biomes.unknown));
    bedrockBiomesById.put(43, Biomes.biomesByResourceLocation.getOrDefault("minecraft:deep_lukewarm_ocean", Biomes.unknown));
    bedrockBiomesById.put(44, Biomes.biomesByResourceLocation.getOrDefault("minecraft:cold_ocean", Biomes.unknown));
    bedrockBiomesById.put(45, Biomes.biomesByResourceLocation.getOrDefault("minecraft:deep_cold_ocean", Biomes.unknown));
    bedrockBiomesById.put(46, Biomes.biomesByResourceLocation.getOrDefault("minecraft:deep_frozen_ocean", Biomes.unknown));
    bedrockBiomesById.put(47, Biomes.biomesByResourceLocation.getOrDefault("minecraft:legacy_frozen_ocean", Biomes.unknown));
    bedrockBiomesById.put(48, Biomes.biomesByResourceLocation.getOrDefault("minecraft:bamboo_jungle", Biomes.unknown));
    bedrockBiomesById.put(49, Biomes.biomesByResourceLocation.getOrDefault("minecraft:bamboo_jungle_hills", Biomes.unknown));
    bedrockBiomesById.put(129, Biomes.biomesByResourceLocation.getOrDefault("minecraft:sunflower_plains", Biomes.unknown));
    bedrockBiomesById.put(130, Biomes.biomesByResourceLocation.getOrDefault("minecraft:desert_mutated", Biomes.unknown));
    bedrockBiomesById.put(131, Biomes.biomesByResourceLocation.getOrDefault("minecraft:extreme_hills_mutated", Biomes.unknown));
    bedrockBiomesById.put(132, Biomes.biomesByResourceLocation.getOrDefault("minecraft:flower_forest", Biomes.unknown));
    bedrockBiomesById.put(133, Biomes.biomesByResourceLocation.getOrDefault("minecraft:taiga_mutated", Biomes.unknown));
    bedrockBiomesById.put(134, Biomes.biomesByResourceLocation.getOrDefault("minecraft:swampland_mutated", Biomes.unknown));
    bedrockBiomesById.put(140, Biomes.biomesByResourceLocation.getOrDefault("minecraft:ice_plains_spikes", Biomes.unknown));
    bedrockBiomesById.put(149, Biomes.biomesByResourceLocation.getOrDefault("minecraft:jungle_mutated", Biomes.unknown));
    bedrockBiomesById.put(151, Biomes.biomesByResourceLocation.getOrDefault("minecraft:jungle_edge_mutated", Biomes.unknown));
    bedrockBiomesById.put(155, Biomes.biomesByResourceLocation.getOrDefault("minecraft:birch_forest_mutated", Biomes.unknown));
    bedrockBiomesById.put(156, Biomes.biomesByResourceLocation.getOrDefault("minecraft:birch_forest_hills_mutated", Biomes.unknown));
    bedrockBiomesById.put(157, Biomes.biomesByResourceLocation.getOrDefault("minecraft:roofed_forest_mutated", Biomes.unknown));
    bedrockBiomesById.put(158, Biomes.biomesByResourceLocation.getOrDefault("minecraft:cold_taiga_mutated", Biomes.unknown));
    bedrockBiomesById.put(160, Biomes.biomesByResourceLocation.getOrDefault("minecraft:redwood_taiga_mutated", Biomes.unknown));
    bedrockBiomesById.put(161, Biomes.biomesByResourceLocation.getOrDefault("minecraft:redwood_taiga_hills_mutated", Biomes.unknown));
    bedrockBiomesById.put(162, Biomes.biomesByResourceLocation.getOrDefault("minecraft:extreme_hills_plus_trees_mutated", Biomes.unknown));
    bedrockBiomesById.put(163, Biomes.biomesByResourceLocation.getOrDefault("minecraft:savanna_mutated", Biomes.unknown));
    bedrockBiomesById.put(164, Biomes.biomesByResourceLocation.getOrDefault("minecraft:savanna_plateau_mutated", Biomes.unknown));
    bedrockBiomesById.put(165, Biomes.biomesByResourceLocation.getOrDefault("minecraft:mesa_bryce", Biomes.unknown));
    bedrockBiomesById.put(166, Biomes.biomesByResourceLocation.getOrDefault("minecraft:mesa_plateau_mutated", Biomes.unknown));
    bedrockBiomesById.put(167, Biomes.biomesByResourceLocation.getOrDefault("minecraft:mesa_plateau_stone_mutated", Biomes.unknown));
    bedrockBiomesById.put(178, Biomes.biomesByResourceLocation.getOrDefault("minecraft:soulsand_valley", Biomes.unknown));
    bedrockBiomesById.put(179, Biomes.biomesByResourceLocation.getOrDefault("minecraft:crimson_forest", Biomes.unknown));
    bedrockBiomesById.put(180, Biomes.biomesByResourceLocation.getOrDefault("minecraft:warped_forest", Biomes.unknown));
    bedrockBiomesById.put(181, Biomes.biomesByResourceLocation.getOrDefault("minecraft:basalt_deltas", Biomes.unknown));
    bedrockBiomesById.put(182, Biomes.biomesByResourceLocation.getOrDefault("minecraft:jagged_peaks", Biomes.unknown));
    bedrockBiomesById.put(183, Biomes.biomesByResourceLocation.getOrDefault("minecraft:frozen_peaks", Biomes.unknown));
    bedrockBiomesById.put(184, Biomes.biomesByResourceLocation.getOrDefault("minecraft:snowy_slopes", Biomes.unknown));
    bedrockBiomesById.put(185, Biomes.biomesByResourceLocation.getOrDefault("minecraft:grove", Biomes.unknown));
    bedrockBiomesById.put(186, Biomes.biomesByResourceLocation.getOrDefault("minecraft:meadow", Biomes.unknown));
    bedrockBiomesById.put(187, Biomes.biomesByResourceLocation.getOrDefault("minecraft:lush_caves", Biomes.unknown));
    bedrockBiomesById.put(188, Biomes.biomesByResourceLocation.getOrDefault("minecraft:dripstone_caves", Biomes.unknown));
    bedrockBiomesById.put(189, Biomes.biomesByResourceLocation.getOrDefault("minecraft:stony_peaks", Biomes.unknown));
    bedrockBiomesById.put(190, Biomes.biomesByResourceLocation.getOrDefault("minecraft:deep_dark", Biomes.unknown));
    bedrockBiomesById.put(191, Biomes.biomesByResourceLocation.getOrDefault("minecraft:mangrove_swamp", Biomes.unknown));
    bedrockBiomesById.put(192, Biomes.biomesByResourceLocation.getOrDefault("minecraft:cherry_groves", Biomes.unknown));
  }
}
