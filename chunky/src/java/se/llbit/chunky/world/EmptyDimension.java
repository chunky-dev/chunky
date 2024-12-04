package se.llbit.chunky.world;

import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import se.llbit.chunky.map.MapView;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.world.region.MCRegionChangeWatcher;
import se.llbit.chunky.world.region.RegionChangeWatcher;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;

public class EmptyDimension extends Dimension {
  public static final EmptyDimension INSTANCE = new EmptyDimension();

  private EmptyDimension() {
    super(EmptyWorld.INSTANCE, 0, null, Collections.emptySet(), -1);
  }

  @Override
  public boolean reloadPlayerData() {
    return false;
  }

  @Override
  public Chunk getChunk(ChunkPosition pos) {
    return EmptyRegionChunk.INSTANCE;
  }

  @Override
  public IntIntPair heightRange() {
    return new IntIntImmutablePair(0, 0);
  }

  @Override
  public RegionChangeWatcher createRegionChangeWatcher(WorldMapLoader worldMapLoader, MapView mapView) {
    return new MCRegionChangeWatcher(worldMapLoader, mapView);
  }

  @Override public String toString() {
    return "[empty dimension]";
  }

  @Override
  public boolean regionExistsWithinRange(RegionPosition regionPos, int yMin, int yMax) {
    return false;
  }

  @Override
  public boolean chunkChangedSince(ChunkPosition chunkPosition, int timestamp) {
    return false;
  }

  @Override
  public Date getLastModified() {
    return Date.from(Instant.EPOCH);
  }
}