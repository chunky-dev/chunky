package se.llbit.chunky.world.region;

import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import se.llbit.chunky.world.*;
import se.llbit.log.Log;
import se.llbit.nbt.ErrorTag;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.Tag;
import se.llbit.util.Mutable;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * <p>CubicChunks regions are 16*16*16, so this imposter region represents an infinitely tall 2x2 column of all regions
 * within the position it is assigned</p>
 */
public class ImposterCubicRegion implements Region {
  public static final int DIAMETER_IN_CUBIC_REGIONS = 2;
  private static final int DIAMETER_IN_VANILLA_CHUNKS = 32;
  private static final int CHUNKS_COUNT = DIAMETER_IN_VANILLA_CHUNKS*DIAMETER_IN_VANILLA_CHUNKS;

  /** <p>Map representing region Y to a {@link ImposterCubicRegion#DIAMETER_IN_CUBIC_REGIONS}*{@link ImposterCubicRegion#DIAMETER_IN_CUBIC_REGIONS}
   * array of CubicRegion objects</p> */
  private final Int2ReferenceOpenHashMap<CubicRegion112[]> internalRegions = new Int2ReferenceOpenHashMap<>();

  private final Chunk[] chunks = new Chunk[CHUNKS_COUNT];
  /** The MC region position of this imposter */
  private final ChunkPosition mcRegionPos;
  /** The minimum cubic region position of this imposter */
  private final ChunkPosition min3drPosition;

  private int minRegionY = Integer.MAX_VALUE;
  private int maxRegionY = Integer.MIN_VALUE;

  private final World world;

  /**
   * Cubes don't have a timestamp, the hacky solution is to average the region timestamps.
   * One timestamp per region column.
   * {@link ImposterCubicChunk} timestamps use this their region column's value, as that's the only known time for the chunk
   */
  private final int[] averageTimestamp = new int[DIAMETER_IN_CUBIC_REGIONS*DIAMETER_IN_CUBIC_REGIONS];
  /** Whether any regions have been updated since {@link ImposterCubicRegion#averageTimestamp} was calculated.
   * One flag per region column */
  private final boolean[] anyUpdated = new boolean[DIAMETER_IN_CUBIC_REGIONS*DIAMETER_IN_CUBIC_REGIONS];

  public ImposterCubicRegion(ChunkPosition pos, World world) {
    this.world = world;
    mcRegionPos = pos;
    min3drPosition = ChunkPosition.get(mcRegionToMinCubicRegion(pos.x), mcRegionToMinCubicRegion(pos.z));
    for (int z = 0; z < DIAMETER_IN_VANILLA_CHUNKS; ++z) {
      for (int x = 0; x < DIAMETER_IN_VANILLA_CHUNKS; ++x) {
        chunks[x + z * 32] = EmptyChunk.INSTANCE;
      }
    }
  }

  /**
   * return the {@link ImposterCubicRegion#internalRegions} index of the region at the given position
   * @param regionX can be local or global
   * @param regionZ can be local or global
   */
  private static int getRegionIndex(int regionX, int regionZ) {
    return (regionX & 1) + (regionZ & 1) * DIAMETER_IN_CUBIC_REGIONS;
  }

  /** Convert a single dimension of a block coordinate to a cube coordinate */
  public static int blockToCube(int blockVal) {
    return blockVal >> 4;
  }

  /** Convert a single dimension of a cube coordinate to a cubic region coordinate */
  public static int cubeToCubicRegion(int cubeVal) {
    return cubeVal >> 4;
  }

  /** Convert a single dimension of a mc region to a chunk coordinate */
  private static int mcRegionToChunk(int mcRegionVal, int localCubeVal) {
    return (mcRegionVal << 5) + localCubeVal;
  }

  /** Convert a single dimension of a cubic region to a cube coordinate */
  private static int cubicRegionToCube(int regionVal, int localCubeVal) {
    return (regionVal << 4) + localCubeVal;
  }

  /** Convert a single dimension of a mc region to the minimum local position cubic region coordinate */
  private static int mcRegionToMinCubicRegion(int regionVal) {
    return regionVal << 1;
  }

  /**
   * All parameters are cubic region positions
   * @return the region file name for this position
   */
  public static String get3drNameForPos(int regionX, int regionY, int regionZ) {
    return String.format("%d.%d.%d.3dr", regionX, regionY, regionZ);
  }

  /**
   * return the average timestamp since the last call to {@link ImposterCubicRegion#parse}
   * @param regionX can be local or global
   * @param regionZ can be local or global
   * */
  private int getAverageTimestampForRegion(int regionX, int regionZ) {
    int index = getRegionIndex(regionX, regionZ);
    if (!anyUpdated[index])
      return averageTimestamp[index];

    int timestampSum = 0;
    ReferenceCollection<CubicRegion112[]> values = internalRegions.values();
    for (CubicRegion112[] regions : values) {
      CubicRegion112 region = regions[index];
      if(region != null)
        timestampSum += region.regionTimestamp;
    }

    this.averageTimestamp[index] = timestampSum / values.size();
    return averageTimestamp[index];
  }

  @Override
  public Chunk getChunk(int x, int z) {
    return chunks[(x & DIAMETER_IN_VANILLA_CHUNKS-1) + (z & DIAMETER_IN_VANILLA_CHUNKS-1)*DIAMETER_IN_VANILLA_CHUNKS];
  }

  public void setChunk(int x, int z, Chunk chunk) {
    chunks[(x & DIAMETER_IN_VANILLA_CHUNKS-1) + (z & DIAMETER_IN_VANILLA_CHUNKS-1)*DIAMETER_IN_VANILLA_CHUNKS] = chunk;
  }

  /**
   * Parse the region file to discover chunks.
   * @param minY the minimum requested block Y to be loaded. This does NOT need to be respected by the implementation
   * @param maxY the maximum requested block Y to be loaded. This does NOT need to be respected by the implementation
   */
  @Override
  public synchronized void parse(int minY, int maxY) {
    //prevent weird race condition if map view is still loading regions when loading chunks into scene
    minRegionY = Math.min(minRegionY, minY >> 8);
    maxRegionY = Math.max(maxRegionY, maxY >> 8);

    //Remove regions out of parse range
    ObjectIterator<Int2ReferenceMap.Entry<CubicRegion112[]>> regionLayerIterator = internalRegions.int2ReferenceEntrySet().fastIterator();
    while(regionLayerIterator.hasNext()) {
      int regionY = regionLayerIterator.next().getIntKey();
      if(regionY < minRegionY || regionY > maxRegionY) {
        regionLayerIterator.remove();
      }
    }

    if(!hasRegionInRangeChanged(minRegionY, maxRegionY))
      return; //Nothing changed, we don't need to reparse

    discoverRegionsInRange(minRegionY, maxRegionY);

    //Parse all known regions
    for (CubicRegion112[] cubicRegions : internalRegions.values()) {
      for (CubicRegion112 cubicRegion : cubicRegions) {
        if(cubicRegion != null)
          cubicRegion.parse();
      }
    }
    //Mark any columns that have loaded
    BitSet columnsWithCubes = markColumnsWithCubes();

    //Create marked any columns, delete any unmarked
    for (int localZ = 0; localZ < DIAMETER_IN_VANILLA_CHUNKS; localZ++) {
      for (int localX = 0; localX < DIAMETER_IN_VANILLA_CHUNKS; localX++) {
        ChunkPosition pos = ChunkPosition.get(mcRegionToChunk(mcRegionPos.x, localX), mcRegionToChunk(mcRegionPos.z, localZ));
        Chunk chunk = getChunk(localX, localZ);
        if (columnsWithCubes.get(localX + localZ*DIAMETER_IN_VANILLA_CHUNKS)) {
          if(chunk.isEmpty()) {
            chunk = new ImposterCubicChunk(pos, world);
            setChunk(localX, localZ, chunk);
          }
        } else {
          if (!chunk.isEmpty()) {
            world.chunkDeleted(pos);
            setChunk(localX, localZ, EmptyChunk.INSTANCE);
          }
        }
      }
    }

    world.regionUpdated(mcRegionPos);
  }

  /**
   * Create {@link CubicRegion112}s within specified range with a region file, remove any existing who's files have been removed
   * @param minRegionY minimum cubicregion Y position to search to
   * @param maxRegionY maximum cubicregion Y position to search to
   */
  private void discoverRegionsInRange(int minRegionY, int maxRegionY) {
    for (int regionY = minRegionY; regionY <= maxRegionY; regionY++) {
      CubicRegion112[] regionsForPosition = internalRegions.computeIfAbsent(regionY, (yPos) -> new CubicRegion112[DIAMETER_IN_CUBIC_REGIONS * DIAMETER_IN_CUBIC_REGIONS]);

      for (int localZ = 0; localZ < DIAMETER_IN_CUBIC_REGIONS; localZ++) {
        for (int localX = 0; localX < DIAMETER_IN_CUBIC_REGIONS; localX++) {
          String fileName = get3drNameForPos(min3drPosition.x + localX, regionY, min3drPosition.z + localZ);
          File regionFile = new File(world.getRegionDirectory(), fileName);

          int regionIndex = localX + localZ * DIAMETER_IN_CUBIC_REGIONS;

          if(regionFile.isFile()) {
            //Create required regions
            if(regionsForPosition[regionIndex] == null) {
              regionsForPosition[regionIndex] = new CubicRegion112(min3drPosition.x + localX, regionY, min3drPosition.z + localZ, fileName);
            }
          } else {
            regionsForPosition[regionIndex] = null; //region doesn't exist, ensure is null
          }
        }
      }
    }
  }

  /**
   * For all known regions, mark if a cube exists within a given column
   * @return bitset with any column containing a cube being marked {@code true}
   */
  private BitSet markColumnsWithCubes() {
    BitSet columnsWithCubes = new BitSet(CHUNKS_COUNT);
    for (CubicRegion112[] cubicRegions : internalRegions.values()) {
      for (int regionLocalZ = 0; regionLocalZ < DIAMETER_IN_CUBIC_REGIONS; regionLocalZ++) {
        for (int regionLocalX = 0; regionLocalX < DIAMETER_IN_CUBIC_REGIONS; regionLocalX++) {
          CubicRegion112 cubicRegion = cubicRegions[regionLocalX + regionLocalZ * DIAMETER_IN_CUBIC_REGIONS];
          if(cubicRegion == null)
            continue;

          for (int cubeX = 0; cubeX < CubicRegion112.DIAMETER_IN_CUBES; cubeX++) {
            for (int cubeY = 0; cubeY < CubicRegion112.DIAMETER_IN_CUBES; cubeY++) {
              for (int cubeZ = 0; cubeZ < CubicRegion112.DIAMETER_IN_CUBES; cubeZ++) {
                int index = ((cubeX) << CubicRegion112.LOC_BITS*2) | ((cubeY) << CubicRegion112.LOC_BITS) | (cubeZ);
                if(cubicRegion.presentCubes.get(index)) {
                  int chunkIndex = (regionLocalX * CubicRegion112.DIAMETER_IN_CUBES + cubeX) + (regionLocalZ * CubicRegion112.DIAMETER_IN_CUBES + cubeZ) * DIAMETER_IN_VANILLA_CHUNKS;
                  columnsWithCubes.set(chunkIndex, true);
                }
              }
            }
          }
        }
      }
    }
    return columnsWithCubes;
  }

  @Override
  public ChunkPosition getPosition() {
    return mcRegionPos;
  }

  /**
   * Read all known region files (found in {@link ImposterCubicRegion#parse}, return all cubes within the specified chunk position
   * @param position chunk position requested
   * @param request NBT tags requested
   * @param existingDataTimestamp existing timestamp
   * @return cube NBT by cube Y position
   */
  public synchronized Map<Integer, Map<String, Tag>> getCubeTagsInColumn(ChunkPosition position, Set<String> request, Mutable<Integer> existingDataTimestamp) {
    Map<Integer, Map<String, Tag>> cubeTagsInColumn = new HashMap<>();

    int regionIndex = (cubeToCubicRegion(position.x) & 1) + ((cubeToCubicRegion(position.z) & 1) * DIAMETER_IN_CUBIC_REGIONS);
    for (CubicRegion112[] regions : internalRegions.values()) {
      CubicRegion112 regionForPos = regions[regionIndex];
      if(regionForPos == null)
        continue;

      Map<Integer, Map<String, Tag>> localCubeTagsInColumn = regionForPos.getCubeTagsInColumn(position.x & (CubicRegion112.DIAMETER_IN_CUBES - 1), position.z & (CubicRegion112.DIAMETER_IN_CUBES - 1), request);
      localCubeTagsInColumn.forEach((localY, cubeTag) -> {
        for (String key : request) {
          if(!cubeTag.containsKey(key)) {
            cubeTag.put(key, new ErrorTag(""));
          }
        }
        cubeTagsInColumn.put(cubicRegionToCube(regionForPos.regionY, localY), cubeTag);
      });
    }

    existingDataTimestamp.set(getAverageTimestampForRegion(cubeToCubicRegion(position.x), cubeToCubicRegion(position.z)));
    return cubeTagsInColumn;
  }

  /** This is quite an expensive method call in a cubicchunks world, as it has to check all parsed regions in the column */
  @Override
  public synchronized boolean hasChanged() {
    for (CubicRegion112[] regions : internalRegions.values()) {
      for (CubicRegion112 region : regions) {
        if(region != null) {
          File file = new File(world.getRegionDirectory(), region.fileName);
          long lastModified = file.lastModified();
          if (lastModified != region.regionTimestamp) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * @param minRegionY minimum cubicregion Y to search in
   * @param maxRegionY maximum cubicregion Y to search in
   * @return return if any (not just known) regions differ within the range
   */
  public boolean hasRegionInRangeChanged(int minRegionY, int maxRegionY) {
    for (int yPos = minRegionY; yPos <= maxRegionY; yPos++) {
      CubicRegion112[] regions = internalRegions.get(yPos);

      if (regions != null) { //check if any existing layers have changed
        for (int localRegionX = 0; localRegionX < DIAMETER_IN_CUBIC_REGIONS; localRegionX++) {
          for (int localRegionZ = 0; localRegionZ < DIAMETER_IN_CUBIC_REGIONS; localRegionZ++) {
            CubicRegion112 region = regions[getRegionIndex(localRegionX, localRegionZ)];
            if (region != null) { //check known region
              File file = new File(world.getRegionDirectory(), region.fileName);
              long lastModified = file.lastModified();
              if (lastModified != region.regionTimestamp) {
                return true;
              }
            } else { //check unknown region
              File file = new File(world.getRegionDirectory(), get3drNameForPos(min3drPosition.x + localRegionX, yPos, min3drPosition.z + localRegionZ));
              if(file.exists())
                return true;
            }
          }
        }
      } else { //check if any unknown layers exist
        for (int localRegionX = 0; localRegionX < DIAMETER_IN_CUBIC_REGIONS; localRegionX++) {
          for (int localRegionZ = 0; localRegionZ < DIAMETER_IN_CUBIC_REGIONS; localRegionZ++) {
            File file = new File(world.getRegionDirectory(), get3drNameForPos(min3drPosition.x + localRegionX, yPos, min3drPosition.z + localRegionZ));
            if(file.exists())
              return true;
          }
        }
      }
    }
    return false;
  }

  @Override
  public synchronized boolean chunkChangedSince(ChunkPosition chunkPos, int timestamp) {
    return getAverageTimestampForRegion(cubeToCubicRegion(chunkPos.x), cubeToCubicRegion(chunkPos.z)) != timestamp;
  }

  @Override public Iterator<Chunk> iterator() {
    return new Iterator<Chunk>() {
      private int index = 0;

      @Override public boolean hasNext() {
        return index < CHUNKS_COUNT;
      }

      @Override public Chunk next() {
        return chunks[index++];
      }

      @Override public void remove() {
        chunks[index] = EmptyChunk.INSTANCE;
      }
    };
  }

  /**
   * An implementation to parse the 1.12 CubicChunks region format
   */
  private class CubicRegion112 {
    /** X,Y, and Z diameter in chunks */
    private static final int DIAMETER_IN_CUBES = 16;
    private static final int CUBES_COUNT = DIAMETER_IN_CUBES*DIAMETER_IN_CUBES*DIAMETER_IN_CUBES;

    private static final int LOC_BITS = 4;
    private static final int SECTOR_SIZE = 512;
    private static final int SECTOR_SIZE_BYTES = 16384;

    /** 3dr position of the region */
    private final int regionX, regionY, regionZ;
    private final String fileName;
    private long regionTimestamp = 0;

    private final BitSet presentCubes = new BitSet(CUBES_COUNT);

    private CubicRegion112(int x, int y, int z, String fileName) {
      this.regionX = x;
      this.regionY = y;
      this.regionZ = z;
      this.fileName = fileName;
    }

    public void parse() {
      File regionFile = new File(world.getRegionDirectory(), fileName);
      long modtime = regionFile.lastModified();
      if (regionTimestamp == modtime) {
        return;
      }
      regionTimestamp = modtime;
      anyUpdated[getRegionIndex(regionX, regionZ)] = true;
      try (RandomAccessFile file = new RandomAccessFile(regionFile, "r")) {
        long length = file.length();
        if (length < SECTOR_SIZE_BYTES) {
          Log.warn("Missing header in region file!");
          return;
        }

        for (int x = 0; x < DIAMETER_IN_CUBES; x++) {
          for (int y = 0; y < DIAMETER_IN_CUBES; ++y) {
            for (int z = 0; z < DIAMETER_IN_CUBES; ++z) {
              int loc = file.readInt();
              int index = ((x) << LOC_BITS*2) | ((y) << LOC_BITS) | (z);
              presentCubes.set(index, loc != 0);
            }
          }
        }

      } catch (IOException e) {
        Log.warn("Failed to read region (" + fileName + "): " + e.getMessage());
      }
    }

    public Map<Integer, Map<String, Tag>> getCubeTagsInColumn(int localX, int localZ, Set<String> request) {
      Map<Integer, Map<String, Tag>> tagMapsByLocalY = new HashMap<>();

      try (RandomAccessFile file = new RandomAccessFile(new File(world.getRegionDirectory(), this.fileName), "r")) {
        long length = file.length();
        if (length < SECTOR_SIZE_BYTES) {
          Log.warn("Missing header in region file!");
          return tagMapsByLocalY;
        }

        for (int localY = 0; localY < DIAMETER_IN_CUBES; localY++) {
          try {
            int index = ((localX) << LOC_BITS * 2) | ((localY) << LOC_BITS) | (localZ);

            file.seek(4L * index);

            int loc = file.readInt();
            int sectorCount = loc & 0xFF;
            int sectorOffset = loc >>> 8;

            if (loc == 0 || sectorCount == 0 || sectorOffset == 0) {
              continue;
            }

            if (length < (long)sectorOffset * SECTOR_SIZE + Integer.BYTES) {
              Log.warnf("Cube (%d, %d, %d) is outside of region file %s! Expected chunk data at offset %d but file length is %d%n",
                cubicRegionToCube(regionX, localX), cubicRegionToCube(regionY, localY), cubicRegionToCube(regionZ, localZ), fileName, sectorOffset * SECTOR_SIZE_BYTES, length);
              continue;
            }

            file.seek((long)sectorOffset * SECTOR_SIZE);
            int dataLength = file.readInt();

            if (dataLength > sectorCount * SECTOR_SIZE) {
              Log.warnf("Corrupted region file %s for local cube (%d, %d, %d). Expected data size max %d but found %d%n",
                fileName, cubicRegionToCube(regionX, localX), cubicRegionToCube(regionY, localY), cubicRegionToCube(regionZ, localZ), sectorCount * SECTOR_SIZE, dataLength);
              continue;
            }

            if (length < (long)sectorOffset * SECTOR_SIZE + Integer.BYTES + dataLength) {
              Log.warnf("Cube (%d, %d, %d) is outside of region file %s! Expected %d bytes at offset %d but file length is %d%n",
                cubicRegionToCube(regionX, localX), cubicRegionToCube(regionY, localY), cubicRegionToCube(regionZ, localZ), fileName, dataLength, sectorOffset * SECTOR_SIZE, length);
              continue;
            }

            file.seek((long)sectorOffset * SECTOR_SIZE + Integer.BYTES);

            ByteBuffer cubeData = ByteBuffer.allocate(dataLength);
            file.read(cubeData.array());
            cubeData.flip();

            try {
              DataInputStream in = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(cubeData.array())));
              Map<String, Tag> value = NamedTag.quickParse(in, new HashSet<>(request));
              tagMapsByLocalY.put(localY, value);
            } catch (IOException e) {
              e.printStackTrace(System.err);
              Log.warn("Failed to read cube: " + e.getMessage());
              //not returning here, as other cubes may be valid within the region file, though unlikely
            }
          } catch (RuntimeException e) {
            e.printStackTrace(System.err);
            Log.warn("Failed to read cube: " + e.getMessage());
          }
        }
      } catch (IOException e) {
        Log.warn("Failed to read region (" + fileName + "): " + e.getMessage());
      }
      return tagMapsByLocalY;
    }
  }
}
