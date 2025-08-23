package se.llbit.chunky.world;

import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import se.llbit.chunky.map.MapView;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.world.region.*;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class JavaDimension extends Dimension {
  protected final Long2ObjectMap<Region> regionMap = new Long2ObjectOpenHashMap<>();
  protected final File dimensionDirectory;

  /**
   * @param dimensionDirectory Minecraft world directory.
   * @param timestamp
   */
  protected JavaDimension(JavaWorld world, String dimensionId, File dimensionDirectory, Set<PlayerEntityData> playerEntities, long timestamp) {
    super(world, dimensionId, playerEntities, timestamp);
    this.dimensionDirectory = dimensionDirectory;
  }

  /**
   * Reload player data.
   * @return {@code true} if player data was reloaded.
   */
  public synchronized boolean reloadPlayerData() {
    boolean changed = ((JavaWorld) this.world).reloadPlayerData();
    if (changed) {
      this.setPlayerEntities(((JavaWorld) this.world).playerEntities.stream()
        .filter(player -> player.dimension.equals(this.id()))
        .collect(Collectors.toSet()));
    }
    return changed;
  }

  /**
   * @return The chunk at the given position
   */
  public synchronized Chunk getChunk(ChunkPosition pos) {
    return getRegion(pos.getRegionPosition()).getChunk(pos);
  }

  @Override
  public IntIntPair heightRange() {
    return ((JavaWorld) this.world).versionId >= JavaWorld.VERSION_21W06A ?
      new IntIntImmutablePair(-64, 320) :
      new IntIntImmutablePair(0, 256);
  }


  public Region createRegion(RegionPosition pos) {
    return new MCRegion(pos, this);
  }

  public RegionChangeWatcher createRegionChangeWatcher(WorldMapLoader worldMapLoader, MapView mapView) {
    return new MCRegionChangeWatcher(worldMapLoader, mapView);
  }

  @Override
  public boolean chunkChangedSince(ChunkPosition chunkPosition, int timestamp) {
    Region region = regionMap.get(chunkPosition.getRegionPosition().getLong());
    return region.chunkChangedSince(chunkPosition, timestamp);
  }

  /** Called when a new region has been discovered by the region parser. */
  public void regionDiscovered(RegionPosition pos) {
    synchronized (this) {
      regionMap.computeIfAbsent(pos.getLong(), p -> createRegion(pos));
    }
  }

  /**
   * @param pos Region position
   * @return The region at the given position
   */
  public synchronized Region getRegion(RegionPosition pos) {
    return regionMap.computeIfAbsent(pos.getLong(), p -> {
      // check if the region is present in the world directory
      Region region = EmptyRegion.instance;
      if (regionExists(pos)) {
        region = createRegion(pos);
      }
      return region;
    });
  }

  public Region getRegionWithinRange(RegionPosition pos, int yMin, int yMax) {
    return getRegion(pos);
  }

  /** Set the region for the given position. */
  public synchronized void setRegion(RegionPosition pos, Region region) {
    regionMap.put(pos.getLong(), region);
  }

  /**
   * @param pos region position
   * @return {@code true} if a region file exists for the given position
   */
  public boolean regionExists(RegionPosition pos) {
    File regionFile = new File(getRegionDirectory(), pos.getMcaName());
    return regionFile.exists();
  }

  /**
   * @param pos Position of the region to load
   * @param minY Minimum block Y (inclusive)
   * @param maxY Maximum block Y (exclusive)
   * @return Whether the region exists
   */
  public boolean regionExistsWithinRange(RegionPosition pos, int minY, int maxY) {
    return this.regionExists(pos);
  }

  /**
   * @return File object pointing to the region file directory
   */
  public synchronized File getRegionDirectory() {
    return new File(getDimensionDirectory(), "region");
  }

  public Date getLastModified() {
    return new Date(this.dimensionDirectory.lastModified());
  }

  /**
   * Get the data directory for the given dimension.
   *
   * @return File object pointing to the data directory
   */
  protected synchronized File getDimensionDirectory() {
    return dimensionDirectory;
  }

  @Override
  public String toString() {
    return dimensionDirectory.getName() ;
  }
}
