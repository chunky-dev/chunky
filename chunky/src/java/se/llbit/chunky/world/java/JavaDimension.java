package se.llbit.chunky.world.java;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import se.llbit.chunky.map.MapView;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.world.*;
import se.llbit.chunky.world.java.region.JavaRegion;
import se.llbit.chunky.world.java.region.JavaRegionChangeWatcher;
import se.llbit.chunky.world.region.*;
import se.llbit.math.Vector3;
import se.llbit.math.Vector3i;
import se.llbit.util.annotation.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class JavaDimension extends Dimension {
  protected final JavaWorld world;
  protected final Long2ObjectMap<Region> regionMap = new Long2ObjectOpenHashMap<>();

  /**
   * @param world
   * @param dimensionId
   * @param dimensionDirectory Minecraft world directory.
   * @param playerEntities
   */
  protected JavaDimension(JavaWorld world, Identifier dimensionId, Path dimensionDirectory, Set<PlayerEntityData> playerEntities, @Nullable Vector3i spawnPos) {
    super(dimensionId, dimensionDirectory, playerEntities, spawnPos);
    this.world = world;
  }

  @Override
  public RegionChangeWatcher createRegionChangeWatcher(WorldMapLoader worldMapLoader, MapView mapView) {
    return new JavaRegionChangeWatcher(worldMapLoader, mapView);
  }

  @Override
  public Region createRegion(RegionPosition pos) {
    return new JavaRegion(pos, this);
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
      if (hasRegion(pos)) {
        region = createRegion(pos);
      }
      return region;
    });
  }

  @Override
  public Region getRegionWithinRange(RegionPosition pos, HeightRange heightRange) {
    return getRegion(pos);
  }

  @Override
  public boolean hasRegion(RegionPosition pos) {
    File regionFile = new File(getRegionDirectory(), pos.getMcaName());
    return regionFile.exists();
  }

  @Override
  public boolean hasRegionWithinRange(RegionPosition pos, HeightRange heightRange) {
    return this.hasRegion(pos);
  }

  @Override
  public HeightRange heightRange() {
    return this.world.versionId >= JavaWorld.VERSION_21W06A ?
      new HeightRange(-64, 320) :
      new HeightRange(0, 256);
  }


  @Override
  public synchronized Chunk getChunk(ChunkPosition pos) {
    return getRegion(pos.getRegionPosition()).getChunk(pos);
  }

  @Override
  public synchronized boolean reloadPlayerData() {
    boolean changed = this.world.reloadPlayerData();
    if (changed) {
      this.playerEntities.clear();
      this.playerEntities.addAll(this.world.playerEntities.stream()
        .filter(player -> player.dimension.equals(this.dimensionId))
        .collect(Collectors.toSet()));
    }
    return changed;
  }

  @Override
  public synchronized Optional<Vector3> getPlayerPos() {
    if (!this.playerEntities.isEmpty()) {
      return world.getSingleplayerPlayerUuid()
        .flatMap(uuid -> this.playerEntities.stream()
          .filter(player -> player.uuid.equals(uuid))
          .map(pos -> new Vector3(pos.x, pos.y, pos.z))
          .findFirst());
    } else {
      return Optional.empty();
    }
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
   * @return File object pointing to the region file directory
   */
  public synchronized File getRegionDirectory() {
    return dimensionDirectory.resolve("region").toFile();
  }

  @Override
  public String getName() {
    return dimensionDirectory.getFileName().toString();
  }
}
