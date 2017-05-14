/* Copyright (c) 2010-2015 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.chunky.world;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.ui.ProgressTracker;
import se.llbit.chunky.world.entity.PlayerEntity;
import se.llbit.chunky.world.listeners.ChunkDeletionListener;
import se.llbit.chunky.world.listeners.ChunkTopographyListener;
import se.llbit.chunky.world.listeners.ChunkUpdateListener;
import se.llbit.log.Log;
import se.llbit.math.Vector3;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.Tag;
import se.llbit.util.Pair;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * The World class contains information about the currently viewed world.
 * It has a map of all chunks in the world and is responsible for parsing
 * chunks when needed. All rendering is done through the WorldRenderer class.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class World implements Comparable<World> {

  /**
   * The currently supported NBT version of level.dat files.
   */
  public static final int NBT_VERSION = 19133;

  /**
   * Overworld dimension index
   */
  public static final int OVERWORLD_DIMENSION = 0;

  /**
   * Nether dimension index
   */
  public static final int NETHER_DIMENSION = -1;

  /**
   * End dimension index
   */
  public static final int END_DIMENSION = 1;

  /**
   * Default sea level. Used to be 64.
   */
  public static final int SEA_LEVEL = 63;
  private static final int DEFAULT_LAYER = SEA_LEVEL;

  private final Map<ChunkPosition, Region> regionMap = new HashMap<>();

  private int currentLayer = DEFAULT_LAYER;
  private File worldDirectory = null;
  private final Set<PlayerEntityData> playerEntities = new HashSet<>();
  private boolean haveSpawnPos = false;
  private int playerDimension = 0;
  private int dimension;

  private final Heightmap heightmap = new Heightmap();

  private String levelName = "unknown";

  private final Collection<ChunkDeletionListener> chunkDeletionListeners = new LinkedList<>();
  private final Collection<ChunkTopographyListener> chunkTopographyListeners = new LinkedList<>();
  private final Collection<ChunkUpdateListener> chunkUpdateListeners = new LinkedList<>();
  private int spawnX;
  private int spawnY;
  private int spawnZ;

  private int gameMode = 0;

  private long seed;

  private long timestamp = 0;

  /**
   * Create new world.
   */
  public World(File worldDir, boolean logWarnings) {
    this.worldDirectory = worldDir;
    this.levelName = worldDir.getName();
    loadAdditionalData(logWarnings);
  }

  protected World() {
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
   * Add a region discovery listener.
   */
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
   * Set current dimension.
   */
  public synchronized void setDimension(int dimension) {
    this.dimension = dimension;
  }

  /**
   * Parse player location and level name.
   *
   * @return {@code true} if the world data was loaded
   */
  public synchronized boolean loadAdditionalData(boolean logWarnings) {
    try {
      File worldFile = new File(worldDirectory, "level.dat");
      long modtime = worldFile.lastModified();
      if (timestamp == modtime) {
        return false;
      }
      timestamp = modtime;
      DataInputStream in = new DataInputStream(new GZIPInputStream(new FileInputStream(worldFile)));
      Set<String> request = new HashSet<>();
      request.add(".Data.version");
      request.add(".Data.RandomSeed");
      request.add(".Data.Player");
      request.add(".Data.LevelName");
      request.add(".Data.GameType");
      Map<String, Tag> result = NamedTag.quickParse(in, request);

      Tag version = result.get(".Data.version");
      if (logWarnings && version.intValue() != NBT_VERSION) {
        Log.warn("The world format for the world " + levelName + " is not supported by Chunky.\n"
            + "Will attempt to load the world anyway.");
      }
      Tag player = result.get(".Data.Player");
      Tag spawnX = player.get("SpawnX");
      Tag spawnY = player.get("SpawnY");
      Tag spawnZ = player.get("SpawnZ");
      Tag gameType = result.get(".Data.GameType");
      Tag randomSeed = result.get(".Data.RandomSeed");

      playerDimension = player.get("Dimension").intValue();

      gameMode = gameType.intValue(0);
      seed = randomSeed.longValue(0);

      playerEntities.clear();

      if (!player.isError()) {
        playerEntities.add(new PlayerEntityData(player));
      }

      int playerY = playerLocY();
      if (playerY >= 0 && playerY <= Chunk.Y_MAX) {
        currentLayer = playerY;
      }

      loadAdditionalPlayers();

      this.spawnX = spawnX.intValue();
      this.spawnY = spawnY.intValue();
      this.spawnZ = spawnZ.intValue();
      haveSpawnPos = !(spawnX.isError() || spawnY.isError() || spawnZ.isError());

      levelName = result.get(".Data.LevelName").stringValue(levelName);

      in.close();
      return true;

    } catch (FileNotFoundException e) {
      if (logWarnings) {
        Log.info("Could not find level.dat file for the world '" + levelName + "'!");
      }
    } catch (IOException e) {
      if (logWarnings) {
        Log.info("Could not read the level.dat file for the world '" + levelName + "'!");
      }
    }
    return false;
  }

  private void loadAdditionalPlayers() {
    loadAdditionalPlayers(new File(worldDirectory, "players"));
    loadAdditionalPlayers(new File(worldDirectory, "playerdata"));
  }

  private void loadAdditionalPlayers(File playerdata) {
    if (playerdata.isDirectory()) {
      File[] players = playerdata.listFiles();
      if (players != null) {
        for (File player : players) {
          try (DataInputStream in = new DataInputStream(
              new GZIPInputStream(new FileInputStream(player)))) {
            playerEntities.add(new PlayerEntityData(NamedTag.read(in).unpack()));
            in.close();
          } catch (IOException e) {
            Log.infof("Could not read player data file '%s'", player.getAbsolutePath());
          }
        }
      }
    }
  }

  /**
   * @return The chunk at the given position
   */
  public synchronized Chunk getChunk(ChunkPosition pos) {
    return getRegion(pos.getRegionPosition()).getChunk(pos);
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
        region = new Region(pos, this);
      }
      setRegion(pos, region);
      return region;
    }
  }

  /**
   * Set the region for the given position.
   */
  public synchronized void setRegion(ChunkPosition pos, Region region) {
    regionMap.put(pos, region);
  }

  /**
   * @param pos region position
   * @return {@code true} if a region file exists for the given position
   */
  public boolean regionExists(ChunkPosition pos) {
    File regionFile = new File(getRegionDirectory(), Region.getFileName(pos));
    return regionFile.exists();
  }

  /**
   * Set the current layer.
   */
  public synchronized void setCurrentLayer(int layer) {
    if (layer != currentLayer) {
      currentLayer = layer;
    }
  }

  /**
   * @return The current layer
   */
  public synchronized int currentLayer() {
    return currentLayer;
  }

  /**
   * Get the data directory for the given dimension
   *
   * @param dimension the dimension
   * @return File object pointing to the data directory
   */
  public synchronized File getDataDirectory(int dimension) {
    return dimension == 0 ?
        worldDirectory :
        new File(worldDirectory, "DIM" + dimension);
  }

  /**
   * Get the data directory for the current dimension
   *
   * @return File object pointing to the data directory
   */
  public synchronized File getDataDirectory() {
    return getDataDirectory(dimension);
  }

  /**
   * @return File object pointing to the region file directory
   */
  public synchronized File getRegionDirectory() {
    return new File(getDataDirectory(), "region");
  }

  /**
   * @return File object pointing to the region file directory for
   * the given dimension
   */
  public synchronized File getRegionDirectory(int dimension) {
    return new File(getDataDirectory(dimension), "region");
  }

  /**
   * @return vector with player position, or {@code null} if not available
   */
  public synchronized Vector3 playerPos() {
    if (!playerEntities.isEmpty() && playerDimension == dimension) {
      PlayerEntityData pos = playerEntities.iterator().next();
      return new Vector3(pos.x, pos.y, pos.z);
    } else {
      return null;
    }
  }

  /**
   * @return <code>true</code> if there is spawn position information
   */
  public synchronized boolean haveSpawnPos() {
    return haveSpawnPos && playerDimension == 0;
  }

  /**
   * @return The current dimension
   */
  public synchronized int currentDimension() {
    return dimension;
  }

  /**
   * @return Player Y location, or -1 if not available
   */
  public synchronized int playerLocY() {
    PlayerEntityData player = getPlayerPosition();
    if (player != null) {
      return (int) (player.y - 0.5);
    }
    return -1;
  }

  /**
   * @return The chunk heightmap
   */
  public Heightmap heightmap() {
    return heightmap;
  }

  /**
   * @return The world director
   */
  public File getWorldDirectory() {
    return worldDirectory;
  }

  /**
   * Called when a new region has been discovered by the region parser.
   */
  public void regionDiscovered(ChunkPosition pos) {
    synchronized (this) {
      Region region = regionMap.get(pos);
      if (region == null) {
        region = new Region(pos, this);
        regionMap.put(pos, region);
      }
    }
  }

  /**
   * Notify region update listeners.
   */
  private void fireChunkUpdated(ChunkPosition chunk) {
    synchronized (chunkUpdateListeners) {
      for (ChunkUpdateListener listener : chunkUpdateListeners) {
        listener.chunkUpdated(chunk);
      }
    }
  }

  /**
   * Notify region update listeners.
   */
  private void fireRegionUpdated(ChunkPosition region) {
    synchronized (chunkUpdateListeners) {
      for (ChunkUpdateListener listener : chunkUpdateListeners) {
        listener.regionUpdated(region);
      }
    }
  }

  /**
   * Clear the region map and remove all listeners.
   */
  public synchronized void dispose() {
    regionMap.clear();

    synchronized (chunkDeletionListeners) {
      chunkDeletionListeners.clear();
    }
    synchronized (chunkUpdateListeners) {
      chunkUpdateListeners.clear();
    }
  }

  /**
   * Export the given chunks to a Zip archive.
   * The Zip arhive is written without compression since the chunks are
   * already compressed with GZip.
   *
   * @throws IOException
   */
  public synchronized void exportChunksToZip(File target, Collection<ChunkPosition> chunks,
      int dimension, ProgressTracker progress) throws IOException {

    Map<ChunkPosition, Set<ChunkPosition>> regionMap = new HashMap<>();

    for (ChunkPosition chunk : chunks) {

      ChunkPosition regionPosition = chunk.regionPosition();
      Set<ChunkPosition> chunkSet = regionMap.get(regionPosition);
      if (chunkSet == null) {
        chunkSet = new HashSet<>();
        regionMap.put(regionPosition, chunkSet);
      }
      chunkSet.add(ChunkPosition.get(chunk.x & 31, chunk.z & 31));
    }

    int work = 0;
    progress.setJobSize(regionMap.size() + 1);

    String regionDirectory =
        dimension == 0 ? worldDirectory.getName() : worldDirectory.getName() + "/DIM" + dimension;
    regionDirectory += "/region";

    try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(target))) {
      writeLevelDatToZip(zout);
      progress.setProgress(++work);

      for (Map.Entry<ChunkPosition, Set<ChunkPosition>> entry : regionMap.entrySet()) {

        if (progress.isInterrupted())
          break;

        ChunkPosition region = entry.getKey();

        appendRegionToZip(zout, getRegionDirectory(dimension), region,
            regionDirectory + "/" + region.getMcaName(), entry.getValue());

        progress.setProgress(++work);
      }
    }
  }

  /**
   * Export the world to a zip file. The chunks which are included
   * depends on the selected chunks. If any chunks are selected, then
   * only those chunks are exported. If no chunks are selected then all
   * chunks are exported.
   *
   * @throws IOException
   */
  public synchronized void exportWorldToZip(File target, ProgressTracker progress)
      throws IOException {
    System.out.println("exporting all dimensions to " + target.getName());

    final Collection<Pair<File, ChunkPosition>> regions = new LinkedList<>();
    regions.clear();

    WorldScanner.Operator operator = (regionDirectory, x, z) ->
        regions.add(new Pair<>(regionDirectory, ChunkPosition.get(x, z)));
    // TODO make this more dynamic
    File overworld = getRegionDirectory(OVERWORLD_DIMENSION);
    WorldScanner.findExistingChunks(overworld, operator);
    WorldScanner.findExistingChunks(getRegionDirectory(NETHER_DIMENSION), operator);
    WorldScanner.findExistingChunks(getRegionDirectory(END_DIMENSION), operator);

    int work = 0;
    progress.setJobSize(regions.size() + 1);

    try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(target))) {
      writeLevelDatToZip(zout);
      progress.setProgress(++work);

      for (Pair<File, ChunkPosition> region : regions) {

        if (progress.isInterrupted())
          break;

        String regionDirectory = (region.thing1 == overworld) ?
            worldDirectory.getName() :
            worldDirectory.getName() + "/" + region.thing1.getParentFile().getName();
        regionDirectory += "/region";
        appendRegionToZip(zout, region.thing1, region.thing2,
            regionDirectory + "/" + region.thing2.getMcaName(), null);

        progress.setProgress(++work);
      }
    }
  }

  /**
   * Write this worlds level.dat file to a ZipOutputStream.
   *
   * @throws IOException
   */
  private void writeLevelDatToZip(ZipOutputStream zout) throws IOException {
    File levelDat = new File(worldDirectory, "level.dat");
    FileInputStream in = new FileInputStream(levelDat);
    zout.putNextEntry(new ZipEntry(worldDirectory.getName() + "/" + "level.dat"));
    byte[] buf = new byte[4096];
    int len;
    while ((len = in.read(buf)) > 0) {
      zout.write(buf, 0, len);
    }
    zout.closeEntry();
    in.close();
  }

  private void appendRegionToZip(ZipOutputStream zout, File regionDirectory,
      ChunkPosition regionPos, String regionZipFileName, Set<ChunkPosition> chunks)
      throws IOException {

    zout.putNextEntry(new ZipEntry(regionZipFileName));
    Region.writeRegion(regionDirectory, regionPos, new DataOutputStream(zout), chunks);
    zout.closeEntry();
  }

  @Override public String toString() {
    return levelName + " (" + worldDirectory.getName() + ")";
  }

  /**
   * @return The name of the world, not the actual world directory
   */
  public String levelName() {
    return levelName;
  }

  /**
   * Called when a chunk has been updated.
   */
  public void chunkUpdated(ChunkPosition chunk) {
    fireChunkUpdated(chunk);
  }

  /**
   * Called when a chunk has been updated.
   */
  public void regionUpdated(ChunkPosition region) {
    fireRegionUpdated(region);
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
   * Notifies listeners that the height gradient of a chunk may have changed.
   *
   * @param chunk The chunk
   */
  public void chunkTopographyUpdated(Chunk chunk) {
    for (ChunkTopographyListener listener : chunkTopographyListeners) {
      listener.chunksTopographyUpdated(chunk);
    }
  }

  /**
   * @return The spawn Z position
   */
  public double spawnPosZ() {
    return spawnZ;
  }

  /**
   * @return The spawn Y position
   */
  public double spawnPosY() {
    return spawnY;
  }

  /**
   * @return The spawn X position
   */
  public double spawnPosX() {
    return spawnX;
  }

  /**
   * @return <code>true</code> if the given directory exists and
   * contains a level.dat file
   */
  public static boolean isWorldDir(File worldDir) {
    if (worldDir != null && worldDir.isDirectory()) {
      File levelDat = new File(worldDir, "level.dat");
      return levelDat.exists() && levelDat.isFile();
    }
    return false;
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

  /**
   * Clear the chunk map and reload the additional data.
   */
  public void reload() {
    regionMap.clear();
    loadAdditionalData(true);
  }

  /**
   * @return String describing the game-mode of this world
   */
  public String gameMode() {
    switch (gameMode) {
      case 0:
        return "Survival";
      case 1:
        return "Creative";
      case 2:
        return "Adventure";
      default:
        return "Unknown";
    }
  }

  @Override public int compareTo(World o) {
    // Just compare the world names.
    return toString().compareToIgnoreCase(o.toString());
  }

  public long getSeed() {
    return seed;
  }

  /**
   * Load entities from world the file.
   * This is usually the single player entity in a local save.
   */
  public synchronized Collection<PlayerEntity> playerEntities() {
    Collection<PlayerEntity> list = new LinkedList<>();
    if (PersistentSettings.getLoadPlayers()) {
      for (PlayerEntityData data : playerEntities) {
        list.add(new PlayerEntity(data.uuid, new Vector3(data.x, data.y, data.z), data.yaw,
            data.pitch));
      }
    }
    return list;
  }

  public synchronized PlayerEntityData getPlayerPosition() {
    if (playerEntities.isEmpty()) {
      return null;
    } else {
      return playerEntities.iterator().next();
    }
  }

  public synchronized Collection<PlayerEntityData> getPlayerPositions() {
    return Collections.unmodifiableSet(playerEntities);
  }
}
