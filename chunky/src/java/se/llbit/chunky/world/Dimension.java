package se.llbit.chunky.world;

import it.unimi.dsi.fastutil.ints.IntIntPair;
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
import se.llbit.chunky.world.region.RegionChangeWatcher;
import se.llbit.math.Vector3;
import se.llbit.math.Vector3i;
import se.llbit.util.annotation.Nullable;

import java.util.*;

/**
 *
 */
public abstract class Dimension {
  protected final World world;
  private Set<PlayerEntityData> playerEntities;

  private final Heightmap heightmap = new Heightmap();

  private final String dimensionId;

  private final Collection<ChunkDeletionListener> chunkDeletionListeners = new LinkedList<>();
  private final Collection<ChunkTopographyListener> chunkTopographyListeners = new LinkedList<>();
  private final Collection<ChunkUpdateListener> chunkUpdateListeners = new LinkedList<>();

  private Vector3i spawnPos = null;

  /** Timestamp for level.dat when player data was last loaded. */
  private long timestamp;

  /**
   * @param timestamp
   */
  protected Dimension(World world, String dimensionId, Set<PlayerEntityData> playerEntities, long timestamp) {
    this.world = world;
    this.dimensionId = dimensionId;
    this.playerEntities = playerEntities;
    this.timestamp = timestamp;
  }

  /**
   * @return A user presentable name of the dimension
   */
  public abstract String getName();

  /**
   * @return The dimension id, such as: {@code minecraft:overworld} (See {@link World#OVERWORLD_DIMENSION_ID})
   */
  public String getId() {
    return dimensionId;
  }

   /**
   * Reload player data.
   * @return {@code true} if player data was reloaded.
   */
  public abstract boolean reloadPlayerData();

  /**
   * @return The chunk at the given position
   */
  public abstract Chunk getChunk(ChunkPosition pos);

  /**
   * Returns a ChunkData instance that is compatible with the given chunk version.
   * The provided ChunkData instance may or may not be re-used.
   */
  public ChunkData createChunkData(@Nullable ChunkData chunkData, int minY, int maxY) {
    if (minY >= 0 && maxY <= 255) {
      if (chunkData instanceof SimpleChunkData) {
        return chunkData;
      }
      return new SimpleChunkData();
    }

    if (chunkData instanceof GenericChunkData) {
      return chunkData;
    }
    return new GenericChunkData();
  }

  /**
   * WARNING: In some dimensions this could be from {@link Integer#MIN_VALUE} to {@link Integer#MAX_VALUE}
   * <p>
   * Lower bound is inclusive, upper is exclusive
   *
   * @return The height range of the dimension.
   */
  public abstract IntIntPair heightRange();

  public abstract RegionChangeWatcher createRegionChangeWatcher(WorldMapLoader worldMapLoader, MapView mapView);


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

  /** Add a chunk deletion listener. */
  public void addChunkDeletionListener(ChunkDeletionListener listener) {
    synchronized (chunkDeletionListeners) {
      chunkDeletionListeners.add(listener);
    }
  }

  /**
   * Called when chunks have been deleted from this world.
   * Triggers the chunk deletion listeners.
   *
   * @param pos Position of deleted chunk
   */
  public void chunkDeleted(ChunkPosition pos) {
    synchronized (chunkDeletionListeners) {
      for (ChunkDeletionListener listener : chunkDeletionListeners)
        listener.chunkDeleted(pos);
    }
  }

  /** Add a region discovery listener. */
  public void addChunkUpdateListener(ChunkUpdateListener listener) {
    synchronized (chunkUpdateListeners) {
      chunkUpdateListeners.add(listener);
    }
  }

  /** Called when a chunk has been updated. */
  public void chunkUpdated(ChunkPosition chunk) {
    synchronized (chunkUpdateListeners) {
      for (ChunkUpdateListener listener : chunkUpdateListeners) {
        listener.chunkUpdated(chunk);
      }
    }
  }

  /** Called when a chunk has been updated. */
  public void regionUpdated(RegionPosition region) {
    synchronized (chunkUpdateListeners) {
      for (ChunkUpdateListener listener : chunkUpdateListeners) {
        listener.regionUpdated(region);
      }
    }
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

  public abstract boolean regionExistsWithinRange(RegionPosition regionPos, int yMin, int yMax);

  public Optional<Vector3i> getSpawnPosition() {
    return Optional.ofNullable(this.spawnPos);
  }

  public void setSpawnPos(@Nullable Vector3i spawnPos) {
    this.spawnPos = spawnPos;
  }

  public abstract boolean chunkChangedSince(ChunkPosition chunkPosition, int timestamp);

  public abstract Date getLastModified();

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
