package se.llbit.chunky.world.java;

import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import se.llbit.chunky.map.MapView;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.world.*;
import se.llbit.chunky.world.region.*;
import se.llbit.math.Vector3;
import se.llbit.math.Vector3i;
import se.llbit.util.annotation.Nullable;

import java.io.File;
import java.util.Optional;
import java.util.Set;

public class JavaDimension extends Dimension {
  protected final JavaWorld world;
  protected final Long2ObjectMap<Region> regionMap = new Long2ObjectOpenHashMap<>();

  /**
   * @param world
   * @param dimensionId
   * @param dimensionDirectory Minecraft world directory.
   * @param playerEntities
   */
  protected JavaDimension(JavaWorld world, Identifier dimensionId, File dimensionDirectory, Set<PlayerEntityData> playerEntities) {
    super(dimensionId, dimensionDirectory, playerEntities);
    this.world = world;
  }

  @Override
  public RegionChangeWatcher createRegionChangeWatcher(WorldMapLoader worldMapLoader, MapView mapView) {
    return new MCRegionChangeWatcher(worldMapLoader, mapView);
  }

  @Override
  public Region createRegion(RegionPosition pos) {
    return new MCRegion(pos, this);
  }

  /**
   * Set the region for the given position.
   */
  public synchronized void setRegion(RegionPosition pos, Region region) {
    regionMap.put(pos.getLong(), region);
  }

  @Override
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

  @Override
  public Region getRegionWithinRange(RegionPosition pos, int yMin, int yMax) {
    return getRegion(pos);
  }

  @Override
  public boolean regionExists(RegionPosition pos) {
    File regionFile = new File(getRegionDirectory(), pos.getMcaName());
    return regionFile.exists();
  }

  @Override
  public boolean regionExistsWithinRange(RegionPosition pos, int minY, int maxY) {
    return this.regionExists(pos);
  }

  @Override
  public IntIntPair heightRange() {
    return this.world.versionId >= JavaWorld.VERSION_21W06A ?
      new IntIntImmutablePair(-64, 320) :
      new IntIntImmutablePair(0, 256);
  }


  @Override
  public synchronized Chunk getChunk(ChunkPosition pos) {
    return getRegion(pos.getRegionPosition()).getChunk(pos);
  }

  @Override
  public synchronized boolean reloadPlayerData() {
    return this.world.reloadPlayerData();
  }

  @Override
  public synchronized Optional<Vector3> getPlayerPos() {
    if (!playerEntities.isEmpty()) {
      return world.getSingleplayerPlayerUuid()
        .flatMap(uuid -> playerEntities.stream()
          .filter(player -> player.uuid.equals(uuid))
          .map(pos -> new Vector3(pos.x, pos.y, pos.z))
          .findFirst());
    } else {
      return Optional.empty();
    }
  }

  public void setSpawnPos(@Nullable Vector3i spawnPos) {
    this.spawnPos = spawnPos;
  }

  /**
   * Called when a new region has been discovered by the region parser.
   */
  public void regionDiscovered(RegionPosition pos) {
    synchronized (this) {
      regionMap.computeIfAbsent(pos.getLong(), p -> createRegion(pos));
    }
  }

  /**
   * Get the data directory for the given dimension.
   *
   * @return File object pointing to the data directory
   */
  protected synchronized File getDimensionDirectory() {
    return dimensionDirectory;
  }

  /**
   * @return File object pointing to the region file directory
   */
  public synchronized File getRegionDirectory() {
    return new File(getDimensionDirectory(), "region");
  }
}
