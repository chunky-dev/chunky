package se.llbit.chunky.world.region;

import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import se.llbit.chunky.world.*;
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
  private static final int DIAMETER_IN_CUBIC_REGIONS = 2;
  private static final int DIAMETER_IN_VANILLA_CHUNKS = 32;
  private static final int CHUNKS_COUNT = DIAMETER_IN_VANILLA_CHUNKS*DIAMETER_IN_VANILLA_CHUNKS;

  /** <p>Map representing region Y to a {@link ImposterCubicRegion#DIAMETER_IN_CUBIC_REGIONS}*{@link ImposterCubicRegion#DIAMETER_IN_CUBIC_REGIONS}
   * array of CubicRegion objects</p> */
  private final Int2ReferenceOpenHashMap<CubicRegion112[]> internalRegions = new Int2ReferenceOpenHashMap<>();

  private final Chunk[] chunks = new Chunk[CHUNKS_COUNT];
  private final ChunkPosition position;
  private final ChunkPosition min3drPosition;
  private final World world;

  /** Cubes don't have a timestamp, the hacky solution is to average the region timestamps.
   * One timestamp per region column */
  private final int[] averageTimestamp = new int[DIAMETER_IN_CUBIC_REGIONS*DIAMETER_IN_CUBIC_REGIONS];
  /** Whether any regions have been updated since {@link ImposterCubicRegion#averageTimestamp} was calculated.
   * One flag per region column */
  private final boolean[] anyUpdated = new boolean[DIAMETER_IN_CUBIC_REGIONS*DIAMETER_IN_CUBIC_REGIONS];

  public ImposterCubicRegion(ChunkPosition pos, World world) {
    this.world = world;
    position = pos;
    min3drPosition = ChunkPosition.get(mcRegionToMinCubicRegion(pos.x), mcRegionToMinCubicRegion(pos.z));
    for (int z = 0; z < DIAMETER_IN_VANILLA_CHUNKS; ++z) {
      for (int x = 0; x < DIAMETER_IN_VANILLA_CHUNKS; ++x) {
        chunks[x + z * 32] = EmptyChunk.INSTANCE;
      }
    }
  }

  private static int getRegionIndex(int globalRegionX, int globalRegionZ) {
    return (globalRegionX & 1) + (globalRegionZ & 1) * DIAMETER_IN_CUBIC_REGIONS;
  }

  private static int cubeToCubicRegion(int cubeVal) {
    return cubeVal >> 4;
  }

  private static int mcRegionToChunk(int mcRegionVal, int localCubeVal) {
    return (mcRegionVal << 5) + localCubeVal;
  }

  private static int cubicRegionToCube(int regionVal, int localCubeVal) {
    return (regionVal << 4) + localCubeVal;
  }

  private static int mcRegionToMinCubicRegion(int regionVal) {
    return regionVal << 1;
  }

  private int getTimestamp(int localRegionX, int localRegionZ) {
    int index = getRegionIndex(localRegionX, localRegionZ);
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
   * @param minY the minimum requested Y to be loaded. This does NOT need to be respected by the implementation
   * @param maxY the maximum requested Y to be loaded. This does NOT need to be respected by the implementation
   */
  @Override
  public synchronized void parse(int minY, int maxY) {
    int minRegionY = minY >> 8;
    int maxRegionY = maxY >> 8;

    //Discover regions for requested range
    boolean anyFound = false;
    IntOpenHashSet regionYPositions = new IntOpenHashSet();
    for (int y = minRegionY; y <= maxRegionY; y++) {
      CubicRegion112[] regionsForPosition = internalRegions.computeIfAbsent(y, (yPos) -> new CubicRegion112[DIAMETER_IN_CUBIC_REGIONS * DIAMETER_IN_CUBIC_REGIONS]);

      for (int localZ = 0; localZ < DIAMETER_IN_CUBIC_REGIONS; localZ++) {
        for (int localX = 0; localX < DIAMETER_IN_CUBIC_REGIONS; localX++) {
          String fileName = String.format("%d.%d.%d.3dr", min3drPosition.x + localX, y, min3drPosition.z + localZ);
          File regionFile = new File(world.getRegionDirectory(), fileName);

          int regionIndex = localX + localZ * DIAMETER_IN_CUBIC_REGIONS;

          if(regionFile.isFile()) {
            anyFound = true;
            regionYPositions.add(y);

            //Create required regions
            if(regionsForPosition[regionIndex] == null) {
              regionsForPosition[regionIndex] = new CubicRegion112(min3drPosition.x + localX, y, min3drPosition.z + localZ, fileName);
            }
          } else {
            regionsForPosition[regionIndex] = null; //region doesn't exist, ensure is null
          }
        }
      }
    }

    if(!anyFound) {
      internalRegions.clear();
      return;
    }

    //Clear currently loaded regions that are no longer required
    for (Integer boxedYPos : internalRegions.keySet()) {
      int yPos = boxedYPos;
      if(!regionYPositions.contains(yPos)) {
        internalRegions.remove(yPos);
      }
    }

    for (CubicRegion112[] cubicRegions : internalRegions.values()) {
      for (CubicRegion112 cubicRegion : cubicRegions) {
        if(cubicRegion != null)
          cubicRegion.parse();
      }
    }

    //Mark columns with cubes
    BitSet cubeExistsInColumn = new BitSet(CHUNKS_COUNT);
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
                  cubeExistsInColumn.set(chunkIndex, true);
                }
              }
            }
          }
        }
      }
    }

    for (int localZ = 0; localZ < DIAMETER_IN_VANILLA_CHUNKS; localZ++) {
      for (int localX = 0; localX < DIAMETER_IN_VANILLA_CHUNKS; localX++) {
        ChunkPosition pos = ChunkPosition.get(mcRegionToChunk(position.x, localX), mcRegionToChunk(position.z, localZ));
        Chunk chunk = getChunk(localX, localZ);
        if (cubeExistsInColumn.get(localX + localZ*DIAMETER_IN_VANILLA_CHUNKS)) {
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

    world.regionUpdated(position);
  }

  @Override
  public ChunkPosition getPosition() {
    return null;
  }

  public synchronized Map<Integer, Map<String, Tag>> getCubeTagsInColumn(ChunkPosition position, Set<String> request, Mutable<Integer> regionTimestamp) {
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

    regionTimestamp.set(getTimestamp(cubeToCubicRegion(position.x), cubeToCubicRegion(position.z)));
    return cubeTagsInColumn;
  }

  /**
   * This is quite an expensive method call in a cubicchunks world, as it has to check all parsed regions in the column
   */
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

  @Override
  public synchronized boolean chunkChangedSince(ChunkPosition chunkPos, int timestamp) {
    return getTimestamp(cubeToCubicRegion(chunkPos.x), cubeToCubicRegion(chunkPos.z)) != timestamp;
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
        if (length < 2 * SECTOR_SIZE_BYTES) {
          System.err.println("Missing header in region file!");
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
        System.err.println("Failed to read region (" + fileName + "): " + e.getMessage());
      }
    }

    public Map<Integer, Map<String, Tag>> getCubeTagsInColumn(int localX, int localZ, Set<String> request) {
      Map<Integer, Map<String, Tag>> tagMapsByLocalY = new HashMap<>();

      try (RandomAccessFile file = new RandomAccessFile(new File(world.getRegionDirectory(), this.fileName), "r")) {
        for (int localY = 0; localY < DIAMETER_IN_CUBES; localY++) {
          try {
            int index = ((localX) << LOC_BITS * 2) | ((localY) << LOC_BITS) | (localZ);
            long length = file.length();
            if (length < SECTOR_SIZE_BYTES * 2) {
              System.err.println("Missing header in region file!");
            }

            file.seek(4L * index);

            int loc = file.readInt();
            int sectorCount = loc & 0xFF;
            int sectorOffset = loc >>> 8;

            if (loc == 0 || sectorCount == 0 || sectorOffset == 0) {
              continue;
            }

            if (length < (long)sectorOffset * SECTOR_SIZE + Integer.BYTES) {
              System.err.printf("Cube (%d, %d, %d) is outside of region file %s! Expected chunk data at offset %d but file length is %d%n",
                cubicRegionToCube(regionX, localX), cubicRegionToCube(regionY, localY), cubicRegionToCube(regionZ, localZ), fileName, sectorOffset * SECTOR_SIZE_BYTES, length);
              continue;
            }

            file.seek((long)sectorOffset * SECTOR_SIZE);
            int dataLength = file.readInt();

            if (dataLength > sectorCount * SECTOR_SIZE) {
              System.err.printf("Corrupted region file %s for local cube (%d, %d, %d). Expected data size max %d but found %d%n",
                fileName, cubicRegionToCube(regionX, localX), cubicRegionToCube(regionY, localY), cubicRegionToCube(regionZ, localZ), sectorCount * SECTOR_SIZE, dataLength);
              continue;
            }

            if (length < (long)sectorOffset * SECTOR_SIZE + Integer.BYTES + dataLength) {
              System.err.printf("Cube (%d, %d, %d) is outside of region file %s! Expected %d bytes at offset %d but file length is %d%n",
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
              System.err.println("Failed to read cube: " + e.getMessage());
              //not returning here, as other cubes may be valid within the region file, though unlikely
            }
          } catch (RuntimeException e) {
            e.printStackTrace(System.err);
            System.err.println("Failed to read cube: " + e.getMessage());
          }
        }
      } catch (IOException e) {
        System.err.println("Failed to read region (" + fileName + "): " + e.getMessage());
      }
      return tagMapsByLocalY;
    }
  }
}