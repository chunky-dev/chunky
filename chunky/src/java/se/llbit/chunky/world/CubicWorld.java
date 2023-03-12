package se.llbit.chunky.world;

import se.llbit.chunky.chunk.ChunkData;
import se.llbit.chunky.chunk.GenericChunkData;
import se.llbit.chunky.chunk.biome.BiomeData2d;
import se.llbit.chunky.map.MapView;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.ui.ProgressTracker;
import se.llbit.chunky.world.region.*;
import se.llbit.log.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import static se.llbit.chunky.world.region.ImposterCubicRegion.blockToCube;
import static se.llbit.chunky.world.region.ImposterCubicRegion.cubeToCubicRegion;

public class CubicWorld extends World {

  /**
   * @param levelName      name of the world (not the world directory).
   * @param worldDirectory Minecraft world directory.
   * @param dimension      the dimension to load.
   * @param playerEntities
   * @param haveSpawnPos
   * @param seed
   * @param timestamp
   */
  protected CubicWorld(String levelName, File worldDirectory, int dimension, Set<PlayerEntityData> playerEntities,
      boolean haveSpawnPos, long seed, long timestamp) {
    super(levelName, worldDirectory, dimension, playerEntities, haveSpawnPos, seed, timestamp);
  }

  /**
   * @return File object pointing to the region file directory
   */
  public synchronized File getRegionDirectory() {
    return new File(getDataDirectory(), "region3d");
  }

  /**
   * @return File object pointing to the region file directory for
   * the given dimension
   */
  protected synchronized File getRegionDirectory(int dimension) {
    return new File(getDataDirectory(dimension), "region3d");
  }

  @Override
  public ChunkData createChunkData(ChunkData chunkData, int chunkVersion) {
    if (chunkData instanceof GenericChunkData) {
      return chunkData;
    }
    GenericChunkData genericChunkData = new GenericChunkData();
    genericChunkData.setBiomeData(new BiomeData2d()); //TODO: CubicChunks biomes support
    return genericChunkData;
  }

  @Override
  public Region createRegion(ChunkPosition pos) {
    return new ImposterCubicRegion(pos, this);
  }

  @Override
  public RegionChangeWatcher createRegionChangeWatcher(WorldMapLoader worldMapLoader, MapView mapView) {
    return new CubicRegionChangeWatcher(worldMapLoader, mapView);
  }

  /**
   * @param pos Region position
   * @return The region at the given position
   */
  public synchronized Region getRegion(ChunkPosition pos) {
    return regionMap.computeIfAbsent(pos.getLong(), (p) -> {
      // check if the region is present in the world directory
      Region region = EmptyRegion.instance;
      if (regionExists(pos)) {
        region = createRegion(pos);
      }
      return region;
    });
  }

  public synchronized Region getRegionWithinRange(ChunkPosition pos, int minY, int maxY) {
    return regionMap.computeIfAbsent(pos.getLong(), p -> {
      // check if the region is present in the world directory
      Region region = EmptyRegion.instance;
      if (regionExistsWithinRange(pos, minY, maxY)) {
        region = createRegion(pos);
      }
      return region;
    });
  }

  /** no choice but to iterate over every file in the directory */
  @Override
  public boolean regionExists(ChunkPosition pos) {
    File regionDirectory = getRegionDirectory();
    try {
      Stream<Path> list = Files.list(regionDirectory.toPath());
      return list.anyMatch(path -> {
        String[] split = path.getFileName().toString().split("[.]");
        if(split.length == 4) {
          try {
            int x = Integer.parseInt(split[0]);
            int z = Integer.parseInt(split[2]);
            return pos.x == x >> 1 && pos.z == z >> 1;
          } catch (NumberFormatException ignored) { }
        }
        return false;
      });
    } catch (IOException e) {
      return false;
    }
  }

  @Override
  public boolean regionExistsWithinRange(ChunkPosition pos, int minY, int maxY) {
    int cubicRegionX = pos.x << 1;
    int cubicRegionZ = pos.z << 1;

    File regionDirectory = getRegionDirectory();
    int minRegionY = cubeToCubicRegion(blockToCube(minY));
    int maxRegionY = cubeToCubicRegion(blockToCube(maxY - 1));
    for (int y = minRegionY; y <= maxRegionY; y++) {
      for (int localX = 0; localX < ImposterCubicRegion.DIAMETER_IN_CUBIC_REGIONS; localX++) {
        for (int localZ = 0; localZ < ImposterCubicRegion.DIAMETER_IN_CUBIC_REGIONS; localZ++) {
          File file = new File(regionDirectory, ImposterCubicRegion.get3drNameForPos(cubicRegionX + localX, y, cubicRegionZ + localZ));
          if (file.exists())
            return true;
        }
      }
    }
    return false;
  }

  /** Called when a new region has been discovered by the region parser. */
  public void regionDiscovered(ChunkPosition pos) {
    synchronized (this) {
      regionMap.computeIfAbsent(pos.getLong(), (p) -> createRegion(pos));
    }
  }

  public synchronized void exportChunksToZip(File target, Collection<ChunkPosition> chunks, ProgressTracker progress)
      throws IOException {
    Log.warn("Not implemented by cubicchunks worlds");
  }

  public synchronized void exportWorldToZip(File target, ProgressTracker progress) throws IOException {
    Log.warn("Not implemented by cubicchunks worlds");
  }
}
