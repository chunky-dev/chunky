package se.llbit.chunky.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.chunk.ChunkData;
import se.llbit.chunky.chunk.GenericChunkData;
import se.llbit.chunky.chunk.SimpleChunkData;
import se.llbit.chunky.map.MapView;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.entity.PlayerEntity;
import se.llbit.chunky.world.listeners.ChunkDeletionListener;
import se.llbit.chunky.world.listeners.ChunkTopographyListener;
import se.llbit.chunky.world.listeners.ChunkUpdateListener;
import se.llbit.chunky.world.region.*;
import se.llbit.math.Vector3;
import se.llbit.math.Vector3i;
import se.llbit.util.annotation.Nullable;

import java.io.File;
import java.util.*;

/**
 *
 */
public class Dimension {
  private final World world;

  protected final Long2ObjectMap<Region> regionMap = new Long2ObjectOpenHashMap<>();

  protected final File dimensionDirectory;
  private Set<PlayerEntityData> playerEntities;

  private final Heightmap heightmap = new Heightmap();

  private final int dimensionId;

  private final Collection<ChunkDeletionListener> chunkDeletionListeners = new LinkedList<>();
  private final Collection<ChunkTopographyListener> chunkTopographyListeners = new LinkedList<>();
  private final Collection<ChunkUpdateListener> chunkUpdateListeners = new LinkedList<>();

  private Vector3i spawnPos = null;

  /** Timestamp for level.dat when player data was last loaded. */
  private long timestamp;

  /**
   * @param dimensionDirectory Minecraft world directory.
   * @param timestamp
   */
  protected Dimension(World world, int dimensionId, File dimensionDirectory, Set<PlayerEntityData> playerEntities, long timestamp) {
    this.world = world;
    this.dimensionId = dimensionId;
    this.dimensionDirectory = dimensionDirectory;
    this.playerEntities = playerEntities;
    this.timestamp = timestamp;
  }

  public int getDimensionId() {
    return dimensionId;
  }

   /**
   * Reload player data.
   * @return {@code true} if player data was reloaded.
   */
  public synchronized boolean reloadPlayerData() {
    return this.world.reloadPlayerData();
  }

  /** Add a chunk deletion listener. */
  public void addChunkDeletionListener(ChunkDeletionListener listener) {
    synchronized (chunkDeletionListeners) {
      chunkDeletionListeners.add(listener);
    }
  }

  /** Add a region discovery listener. */
  public void addChunkUpdateListener(ChunkUpdateListener listener) {
    synchronized (chunkUpdateListeners) {
      chunkUpdateListeners.add(listener);
    }
  }

  private void fireChunkDeleted(ChunkPosition chunk) {
    synchronized (chunkDeletionListeners) {
      for (ChunkDeletionListener listener : chunkDeletionListeners)
        listener.chunkDeleted(chunk);
    }
  }

  /**
   * @return The chunk at the given position
   */
  public synchronized Chunk getChunk(ChunkPosition pos) {
    return getRegion(pos.getRegionPosition()).getChunk(pos);
  }

  /**
   * Returns a ChunkData instance that is compatible with the given chunk version.
   * The provided ChunkData instance may or may not be re-used.
   */
  public ChunkData createChunkData(@Nullable ChunkData chunkData, int chunkVersion) {
    if(chunkVersion >= World.VERSION_21W06A) {
      if(chunkData instanceof GenericChunkData) {
        return chunkData;
      }
      return new GenericChunkData();
    } else {
      if(chunkData instanceof SimpleChunkData) {
        return chunkData;
      }
      return new SimpleChunkData();
    }
  }

  public Region createRegion(ChunkPosition pos) {
    return new MCRegion(pos, this);
  }

  public RegionChangeWatcher createRegionChangeWatcher(WorldMapLoader worldMapLoader, MapView mapView) {
    return new MCRegionChangeWatcher(worldMapLoader, mapView);
  }

  /**
   * @param pos Region position
   * @return The region at the given position
   */
  public synchronized Region getRegion(ChunkPosition pos) {
    return regionMap.computeIfAbsent(pos.getLong(), p -> {
      // check if the region is present in the world directory
      Region region = EmptyRegion.instance;
      if (regionExists(pos)) {
        region = createRegion(pos);
      }
      return region;
    });
  }

  public Region getRegionWithinRange(ChunkPosition pos, int yMin, int yMax) {
    return getRegion(pos);
  }

  /** Set the region for the given position. */
  public synchronized void setRegion(ChunkPosition pos, Region region) {
    regionMap.put(pos.getLong(), region);
  }

  /**
   * @param pos region position
   * @return {@code true} if a region file exists for the given position
   */
  public boolean regionExists(ChunkPosition pos) {
    File regionFile = new File(getRegionDirectory(), MCRegion.getFileName(pos));
    return regionFile.exists();
  }

  /**
   * @param pos Position of the region to load
   * @param minY Minimum block Y (inclusive)
   * @param maxY Maximum block Y (exclusive)
   * @return Whether the region exists
   */
  public boolean regionExistsWithinRange(ChunkPosition pos, int minY, int maxY) {
    return this.regionExists(pos);
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

  /**
   * Get the current player position as an optional vector.
   *
   * <p>The result is empty if this is not a single player world.
   */
  public synchronized Optional<Vector3> getPlayerPos() {
    if (!playerEntities.isEmpty()) {
      PlayerEntityData pos = playerEntities.iterator().next();
      return Optional.of(new Vector3(pos.x, pos.y, pos.z));
    } else {
      return Optional.empty();
    }
  }

  /**
   * @return The chunk heightmap
   */
  public Heightmap getHeightmap() {
    return heightmap;
  }

  /** Called when a new region has been discovered by the region parser. */
  public void regionDiscovered(ChunkPosition pos) {
    synchronized (this) {
      regionMap.computeIfAbsent(pos.getLong(), p -> createRegion(pos));
    }
  }

  /** Notify region update listeners. */
  private void fireChunkUpdated(ChunkPosition chunk) {
    synchronized (chunkUpdateListeners) {
      for (ChunkUpdateListener listener : chunkUpdateListeners) {
        listener.chunkUpdated(chunk);
      }
    }
  }

  /** Notify region update listeners. */
  private void fireRegionUpdated(ChunkPosition region) {
    synchronized (chunkUpdateListeners) {
      for (ChunkUpdateListener listener : chunkUpdateListeners) {
        listener.regionUpdated(region);
      }
    }
  }

  @Override public String toString() {
    return dimensionDirectory.getName() ;
  }

  /** Called when a chunk has been updated. */
  public void chunkUpdated(ChunkPosition chunk) {
    fireChunkUpdated(chunk);
  }

  /** Called when a chunk has been updated. */
  public void regionUpdated(ChunkPosition region) {
    fireRegionUpdated(region);
  }

  /** Add a chunk discovery listener */
  public void addChunkTopographyListener(ChunkTopographyListener listener) {
    synchronized (chunkTopographyListeners) {
      chunkTopographyListeners.add(listener);
    }
  }

  /** Remove a chunk discovery listener */
  public void removeChunkTopographyListener(ChunkTopographyListener listener) {
    synchronized (chunkTopographyListeners) {
      chunkTopographyListeners.remove(listener);
    }
  }

  /**
   * Notifies listeners that the height gradient of a chunk may have changed.
   *
   * @param chunk The chunk
   */
  public void chunkTopographyUpdated(Chunk chunk) {
    for (ChunkTopographyListener listener : chunkTopographyListeners) {
      listener.chunksTopographyUpdated(chunk);
    }
  }

  public Optional<Vector3i> getSpawnPosition() {
    return Optional.ofNullable(this.spawnPos);
  }

  public void setSpawnPos(@Nullable Vector3i spawnPos) {
    this.spawnPos = spawnPos;
  }

  /**
   * Called when chunks have been deleted from this world.
   * Triggers the chunk deletion listeners.
   *
   * @param pos Position of deleted chunk
   */
  public void chunkDeleted(ChunkPosition pos) {
    fireChunkDeleted(pos);
  }

  public Date getLastModified() {
    return new Date(this.dimensionDirectory.lastModified());
  }

  /**
   * Load entities from world the file.
   * This is usually the single player entity in a local save.
   */
  public synchronized Collection<PlayerEntity> getPlayerEntities() {
    Collection<PlayerEntity> list = new LinkedList<>();
    if (PersistentSettings.getLoadPlayers()) {
      for (PlayerEntityData data : playerEntities) {
        list.add(new PlayerEntity(data));
      }
    }
    return list;
  }

  public synchronized Collection<PlayerEntityData> getPlayerPositions() {
    return Collections.unmodifiableSet(playerEntities);
  }

  public synchronized void setPlayerEntities(Set<PlayerEntityData> playerEntities) {
    this.playerEntities = playerEntities;
  }
}
