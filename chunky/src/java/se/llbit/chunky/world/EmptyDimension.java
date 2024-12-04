package se.llbit.chunky.world;

import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import se.llbit.chunky.map.MapView;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.world.region.EmptyRegion;
import se.llbit.chunky.world.region.Region;
import se.llbit.chunky.world.region.RegionChangeWatcher;
import se.llbit.math.Vector3;

import java.util.Collections;
import java.util.Optional;

public class EmptyDimension extends Dimension {
  public static final EmptyDimension INSTANCE = new EmptyDimension();

  private EmptyDimension() {
    super(Dimension.Identifier.OVERWORLD, null, Collections.emptySet());
  }

  @Override
  public Chunk getChunk(ChunkPosition pos) {
    return EmptyChunk.INSTANCE;
  }

  @Override
  public Region createRegion(RegionPosition pos) {
    return EmptyRegion.instance;
  }

  @Override
  public RegionChangeWatcher createRegionChangeWatcher(WorldMapLoader worldMapLoader, MapView mapView) {
    return new RegionChangeWatcher(worldMapLoader, mapView, "Empty Region Change Watcher") {
      @Override
      public void run() {}
    };
  }

  @Override
  public Region getRegion(RegionPosition pos) {
    return EmptyRegion.instance;
  }

  @Override
  public Region getRegionWithinRange(RegionPosition pos, int yMin, int yMax) {
    return EmptyRegion.instance;
  }

  @Override
  public boolean regionExists(RegionPosition pos) {
    return false;
  }

  @Override
  public boolean regionExistsWithinRange(RegionPosition pos, int minY, int maxY) {
    return false;
  }

  @Override
  public IntIntPair heightRange() {
    return new IntIntImmutablePair(0, 0);
  }

  @Override
  public String toString() {
    return "[empty dimension]";
  }

  @Override
  public boolean reloadPlayerData() {
    return false;
  }

  @Override
  public Optional<Vector3> getPlayerPos() {
    return Optional.empty();
  }
}
