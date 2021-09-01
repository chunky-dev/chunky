package se.llbit.chunky.world;

import se.llbit.chunky.chunk.ChunkData;
import se.llbit.chunky.chunk.GenericChunkData;
import se.llbit.chunky.ui.ProgressTracker;
import se.llbit.chunky.world.region.EmptyRegion;
import se.llbit.chunky.world.region.ImposterCubicRegion;
import se.llbit.chunky.world.region.Region;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

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
  public ChunkData createChunkData() {
    return new GenericChunkData();
  }

  @Override
  public Region createRegion(ChunkPosition pos) {
    return new ImposterCubicRegion(pos, this);
  }

  /**
   * @param pos Region position
   * @return The region at the given position
   */
  public synchronized Region getRegion(ChunkPosition pos) {
    if (regionMap.containsKey(pos)) {
      return regionMap.get(pos);
    } else {
      // check if the region is present in the world directory
      Region region = EmptyRegion.instance;
      if (regionExists(pos)) {
        region = new ImposterCubicRegion(pos, this);
      }
      setRegion(pos, region);
      return region;
    }
  }

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

  /** Called when a new region has been discovered by the region parser. */
  public void regionDiscovered(ChunkPosition pos) {
    synchronized (this) {
      Region region = regionMap.get(pos);
      if (region == null) {
        region = new ImposterCubicRegion(pos, this);
        regionMap.put(pos, region);
      }
    }
  }

  public synchronized void exportChunksToZip(File target, Collection<ChunkPosition> chunks, ProgressTracker progress)
      throws IOException {
    throw new UnsupportedOperationException("Not implemented by cubicchunks worlds");
  }

  public synchronized void exportWorldToZip(File target, ProgressTracker progress) throws IOException {
    throw new UnsupportedOperationException("Not implemented by cubicchunks worlds");
  }
}
