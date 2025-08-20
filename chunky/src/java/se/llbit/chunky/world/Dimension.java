package se.llbit.chunky.world;

import it.unimi.dsi.fastutil.ints.IntIntPair;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.chunk.ChunkData;
import se.llbit.chunky.chunk.GenericChunkData;
import se.llbit.chunky.chunk.SimpleChunkData;
import se.llbit.chunky.entity.PlayerEntity;
import se.llbit.chunky.map.MapView;
import se.llbit.chunky.map.WorldMapLoader;
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
public abstract class Dimension {
  public record Identifier(String namespace, String name) {
    public static Identifier OVERWORLD = new Identifier("minecraft", "overworld");
    public static Identifier THE_NETHER = new Identifier("minecraft", "the_nether");
    public static Identifier THE_END = new Identifier("minecraft", "the_end");

    public static Identifier fromNamespacedName(String name) {
      return switch (name) {
        case "minecraft:overworld" -> OVERWORLD;
        case "minecraft:the_end" -> THE_END;
        case "minecraft:the_nether" -> THE_NETHER;
        default -> {
          String[] parts = name.split(":", 2);
          if (parts.length != 2) {
            System.out.println("BANG BANG!!" + name);
            throw new IllegalArgumentException("Bad dimension name: " + name);
          }
          yield new Identifier(parts[0], parts[1]);
        }
      };
    }

    public static Identifier fromLegacyId(int id) {
      return switch (id) {
        case -1 -> THE_NETHER;
        case 1 -> THE_END;
        default -> OVERWORLD;
      };
    }

    public String getNamespacedName() {
      return namespace + ":" + name;
    }

    @Override
    public String toString() {
      return getNamespacedName();
    }
  }

  protected final File dimensionDirectory;

  protected final Heightmap heightmap = new Heightmap();

  protected final Identifier dimensionId;

  @Nullable protected final Vector3i spawnPos;
  protected final Set<PlayerEntityData> playerEntities;

  protected final Collection<ChunkDeletionListener> chunkDeletionListeners = new LinkedList<>();
  protected final Collection<ChunkTopographyListener> chunkTopographyListeners = new LinkedList<>();
  protected final Collection<ChunkUpdateListener> chunkUpdateListeners = new LinkedList<>();

  /**
   * @param dimensionDirectory Minecraft world directory.
   */
  protected Dimension(Identifier dimensionId, File dimensionDirectory, Set<PlayerEntityData> playerEntities, @Nullable Vector3i spawnPos) {
    this.dimensionId = dimensionId;
    this.playerEntities = playerEntities;
    this.dimensionDirectory = dimensionDirectory;
    this.spawnPos = spawnPos;
  }

  /**
   * @return A user presentable name of the dimension
   */
  public abstract String getName();

  public Identifier getDimensionId() {
    return dimensionId;
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

  public abstract Region createRegion(RegionPosition pos);

  public abstract RegionChangeWatcher createRegionChangeWatcher(WorldMapLoader worldMapLoader, MapView mapView);

  /**
   * @param pos Region position
   * @return The region at the given position
   */
  public abstract Region getRegion(RegionPosition pos);

  public abstract Region getRegionWithinRange(RegionPosition pos, int yMin, int yMax);

  /**
   * @param pos region position
   * @return {@code true} if a region file exists for the given position
   */
  public abstract boolean regionExists(RegionPosition pos);

  /**
   * @param pos  Position of the region to load
   * @param minY Minimum block Y (inclusive)
   * @param maxY Maximum block Y (exclusive)
   * @return Whether the region exists
   */
  public abstract boolean regionExistsWithinRange(RegionPosition pos, int minY, int maxY);

  /**
   * WARNING: In some dimensions this could be from {@link Integer#MIN_VALUE} to {@link Integer#MAX_VALUE}
   * <p>
   * Lower bound is inclusive, upper is exclusive
   *
   * @return The height range of the dimension.
   */
  public abstract IntIntPair heightRange();

  /**
   * @return The chunk heightmap
   */
  public Heightmap getHeightmap() {
    return heightmap;
  }

  public Optional<Vector3i> getSpawnPosition() {
    return Optional.ofNullable(this.spawnPos);
  }

  public Date getLastModified() {
    return new Date(this.dimensionDirectory.lastModified());
  }

  /**
   * Reload player data.
   *
   * @return {@code true} if player data was reloaded.
   */
  public abstract boolean reloadPlayerData();

  /**
   * Get the current player position as an optional vector.
   *
   * <p>The result is empty if this is not a single player world.
   */
  public abstract Optional<Vector3> getPlayerPos();

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

  /**
   * Add a chunk deletion listener.
   */
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

  /**
   * Add a region discovery listener.
   */
  public void addChunkUpdateListener(ChunkUpdateListener listener) {
    synchronized (chunkUpdateListeners) {
      chunkUpdateListeners.add(listener);
    }
  }

  /**
   * Called when a chunk has been updated.
   */
  public void chunkUpdated(ChunkPosition chunk) {
    synchronized (chunkUpdateListeners) {
      for (ChunkUpdateListener listener : chunkUpdateListeners) {
        listener.chunkUpdated(chunk);
      }
    }
  }

  /**
   * Called when a chunk has been updated.
   */
  public void regionUpdated(RegionPosition region) {
    synchronized (chunkUpdateListeners) {
      for (ChunkUpdateListener listener : chunkUpdateListeners) {
        listener.regionUpdated(region);
      }
    }
  }

  /**
   * Add a chunk discovery listener
   */
  public void addChunkTopographyListener(ChunkTopographyListener listener) {
    synchronized (chunkTopographyListeners) {
      chunkTopographyListeners.add(listener);
    }
  }

  /**
   * Remove a chunk discovery listener
   */
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
}
