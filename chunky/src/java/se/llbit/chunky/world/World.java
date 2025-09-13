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

import se.llbit.chunky.ui.ProgressTracker;
import se.llbit.chunky.world.region.MCRegion;
import se.llbit.log.Log;
import se.llbit.math.Vector3i;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.Tag;
import se.llbit.util.MinecraftText;
import se.llbit.util.Pair;
import se.llbit.util.annotation.NotNull;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
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

  /** The currently supported NBT version of level.dat files. */
  public static final int NBT_VERSION = 19133;

  /** Overworld dimension index. */
  public static final int OVERWORLD_DIMENSION = 0;

  /** Nether dimension index. */
  public static final int NETHER_DIMENSION = -1;

  /** End dimension index. */
  public static final int END_DIMENSION = 1;

  /** Default sea water level. */
  public static final int SEA_LEVEL = 63;

  /** Minimum level.dat data version of tall worlds (21w06a). */
  public static final int VERSION_21W06A = 2694;
  public static final int VERSION_1_12_2 = 1343;

  private final File worldDirectory;

  protected Dimension currentDimension;
  protected int currentDimensionId;

  private final String levelName;
  private int gameMode = 0;
  private final long seed;

  private int versionId;

  /** Timestamp for level.dat when player data was last loaded. */
  private long timestamp;

  /**
   * @param levelName name of the world (not the world directory).
   * @param worldDirectory Minecraft world directory.
   * @param seed
   * @param timestamp
   */
  protected World(String levelName, File worldDirectory, long seed, long timestamp) {
    this.levelName = levelName;
    this.worldDirectory = worldDirectory;
    this.seed = seed;
    this.timestamp = timestamp;
  }

  public enum LoggedWarnings {
    NORMAL,
    SILENT
  }

  public void loadDimension(int dimensionId) {
    currentDimension = loadDimension(this, this.worldDirectory, dimensionId, -1, Collections.emptySet());
    currentDimension.reloadPlayerData();
  }

  /**
   * Parse player location and level name.
   *
   * @return {@code true} if the world data was loaded
   */
  public static World loadWorld(File worldDirectory, int dimensionId, LoggedWarnings warnings) {
    if (worldDirectory == null) {
      return EmptyWorld.INSTANCE;
    }
    String levelName = worldDirectory.getName(); // Default level name.
    File worldFile = new File(worldDirectory, "level.dat");
    long modtime = worldFile.lastModified();
    try (FileInputStream fin = new FileInputStream(worldFile);
        InputStream gzin = new GZIPInputStream(fin);
        DataInputStream in = new DataInputStream(gzin)) {
      Set<String> request = new HashSet<>();
      request.add(".Data.version");
      request.add(".Data.Version.Id");
      request.add(".Data.RandomSeed");
      request.add(".Data.Player");
      request.add(".Data.LevelName");
      request.add(".Data.GameType");
      request.add(".Data.isCubicWorld");
      Map<String, Tag> result = NamedTag.quickParse(in, request);

      Tag version = result.get(".Data.version");
      if (warnings == LoggedWarnings.NORMAL && version.intValue() != NBT_VERSION) {
        Log.warnf("The world format for the world %s is not supported by Chunky.\n" + "Will attempt to load the world anyway.",
            levelName);
      }
      Tag versionId = result.get(".Data.Version.Id");
      Tag player = result.get(".Data.Player");
      Tag spawnX = player.get("SpawnX");
      Tag spawnY = player.get("SpawnY");
      Tag spawnZ = player.get("SpawnZ");
      Tag gameType = result.get(".Data.GameType");
      Tag randomSeed = result.get(".Data.RandomSeed");
      levelName = MinecraftText.removeFormatChars(result.get(".Data.LevelName").stringValue(levelName));

      long seed = randomSeed.longValue(0);

      Set<PlayerEntityData> playerEntities = getPlayerEntityData(worldDirectory, dimensionId, player);

      World world = new World(levelName, worldDirectory, seed, modtime);
      world.gameMode = gameType.intValue(0);
      world.versionId = versionId.intValue();

      Dimension dimension = loadDimension(world, worldDirectory, dimensionId, modtime, playerEntities);

      boolean haveSpawnPos = !(spawnX.isError() || spawnY.isError() || spawnZ.isError());
      if (haveSpawnPos) {
        dimension.setSpawnPos(new Vector3i(spawnX.intValue(0), spawnY.intValue(0), spawnZ.intValue(0)));
      }

      world.currentDimension = dimension;

      return world;
    } catch (FileNotFoundException e) {
      if (warnings == LoggedWarnings.NORMAL) {
        Log.infof("Could not find level.dat file for world %s!", levelName);
      }
    } catch (IOException e) {
      if (warnings == LoggedWarnings.NORMAL) {
        Log.infof("Could not read the level.dat file for world %s!", levelName);
      }
    }
    return EmptyWorld.INSTANCE;
  }

  @NotNull
  private static Dimension loadDimension(World world, File worldDirectory, int dimensionId, long modtime, Set<PlayerEntityData> playerEntities) {
    Dimension dimension;
    File dimensionDirectory = dimensionId == 0 ? worldDirectory : new File(worldDirectory, "DIM" + dimensionId);
    if (new File(dimensionDirectory, "region3d").exists()) {
      dimension = new CubicDimension(world, dimensionId, dimensionDirectory, playerEntities, modtime);
    } else {
      dimension = new Dimension(world, dimensionId, dimensionDirectory, playerEntities, modtime);
    }
    return dimension;
  }

  @NotNull
  private static Set<PlayerEntityData> getPlayerEntityData(File worldDirectory, int dimensionId, Tag player) {
    Set<PlayerEntityData> playerEntities = new HashSet<>();
    if (!player.isError()) {
      playerEntities.add(new PlayerEntityData(player));
    }
    loadAdditionalPlayers(worldDirectory, playerEntities);
    // Filter for the players only within the requested dimension
    playerEntities = playerEntities.stream().filter(playerData -> playerData.dimension == dimensionId).collect(Collectors.toSet());
    return playerEntities;
  }

  /**
   * Reload player data for the current dimension. This method is not in Dimension because players are per-world, not per-dimension
   * @return {@code true} if player data was reloaded.
   */
  synchronized boolean reloadPlayerData() {
    if (worldDirectory == null) {
      return false;
    }
    File worldFile = new File(worldDirectory, "level.dat");
    long lastModified = worldFile.lastModified();
    if (lastModified == timestamp) {
      return false;
    }
    Log.infof("world %s: timestamp updated: reading player data", levelName);
    timestamp = lastModified;

    try (FileInputStream fin = new FileInputStream(worldFile);
         InputStream gzin = new GZIPInputStream(fin);
         DataInputStream in = new DataInputStream(gzin)) {
      Set<String> request = new HashSet<>();
      request.add(".Data.Player");
      Map<String, Tag> result = NamedTag.quickParse(in, request);
      Tag player = result.get(".Data.Player");

      currentDimension.setPlayerEntities(getPlayerEntityData(worldDirectory, currentDimensionId, player));
    } catch (IOException e) {
      Log.infof("Could not read the level.dat file for world %s while trying to reload player data!", levelName);
      return false;
    }
    return true;
  }

  private static void loadAdditionalPlayers(File worldDirectory, Set<PlayerEntityData> playerEntities) {
    loadPlayerData(new File(worldDirectory, "players"), playerEntities);
    loadPlayerData(new File(worldDirectory, "playerdata"), playerEntities);
  }

  private static void loadPlayerData(File playerdata, Set<PlayerEntityData> playerEntities) {
    if (playerdata.isDirectory()) {
      File[] players = playerdata.listFiles();
      if (players != null) {
        for (File player : players) {
          try (DataInputStream in = new DataInputStream(
              new GZIPInputStream(new FileInputStream(player)))) {
            playerEntities.add(new PlayerEntityData(NamedTag.read(in).unpack()));
          } catch (IOException e) {
            Log.infof("Could not read player data file '%s'", player.getAbsolutePath());
          }
        }
      }
    }
  }

  /**
   * @return The current dimension
   */
  public synchronized Dimension currentDimension() {
    return this.currentDimension;
  }

  /**
   * @return The current dimension
   */
  public synchronized int currentDimensionId() {
    return this.currentDimensionId;
  }


  /**
   * @return The world directory
   */
  public File getWorldDirectory() {
    return worldDirectory;
  }

  /**
   * @deprecated Use {@link World#currentDimension()} -> {@link Dimension#getDimensionDirectory()} ()}. Removed once there are no more usages
   */
  @Deprecated
  protected synchronized File getDataDirectory(int dimension) {
    return dimension == 0 ?
      worldDirectory :
      new File(worldDirectory, "DIM" + dimension);
  }

  /**
    @deprecated Use {@link World#currentDimension()} -> {@link Dimension#getRegionDirectory()}. Removed once there are no more usages
   */
  @Deprecated
  protected synchronized File getRegionDirectory(int dimension) {
    return new File(getDataDirectory(dimension), "region");
  }


  /**
   * Export the given chunks to a Zip archive.
   * The Zip arhive is written without compression since the chunks are
   * already compressed with GZip.
   *
   * @throws IOException
   */
  public synchronized void exportChunksToZip(File target, Collection<ChunkPosition> chunks,
                                             ProgressTracker progress) throws IOException {

    Map<RegionPosition, Set<ChunkPosition>> regionMap = new HashMap<>();

    for (ChunkPosition chunk : chunks) {
      RegionPosition regionPosition = chunk.getRegionPosition();
      Set<ChunkPosition> chunkSet = regionMap.computeIfAbsent(regionPosition, k -> new HashSet<>());
      chunkSet.add(new ChunkPosition(chunk.x & 31, chunk.z & 31));
    }

    int work = 0;
    progress.setJobSize(regionMap.size() + 1);

    String regionDirectory =
      currentDimensionId == 0 ? currentDimension().getDimensionDirectory().getName() :
        currentDimension().getDimensionDirectory().getName() + "/DIM" + currentDimensionId;
    regionDirectory += "/region";

    try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(target))) {
      writeLevelDatToZip(zout);
      progress.setProgress(++work);

      for (Map.Entry<RegionPosition, Set<ChunkPosition>> entry : regionMap.entrySet()) {

        if (progress.isInterrupted())
          break;

        RegionPosition region = entry.getKey();

        appendRegionToZip(zout, currentDimension.getRegionDirectory(), region,
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

    final Collection<Pair<File, RegionPosition>> regions = new LinkedList<>();

    WorldScanner.Operator operator = (regionDirectory, x, z) ->
        regions.add(new Pair<>(regionDirectory, new RegionPosition(x, z)));
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

      for (Pair<File, RegionPosition> region : regions) {

        if (progress.isInterrupted()) {
          break;
        }

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
    try (FileInputStream in = new FileInputStream(levelDat)) {
      zout.putNextEntry(new ZipEntry(worldDirectory.getName() + "/" + "level.dat"));
      byte[] buf = new byte[4096];
      int len;
      while ((len = in.read(buf)) > 0) {
        zout.write(buf, 0, len);
      }
      zout.closeEntry();
    }
  }

  private void appendRegionToZip(ZipOutputStream zout, File regionDirectory,
      RegionPosition regionPos, String regionZipFileName, Set<ChunkPosition> chunks)
      throws IOException {

    zout.putNextEntry(new ZipEntry(regionZipFileName));
    MCRegion.writeRegion(regionDirectory, regionPos, new DataOutputStream(zout), chunks);
    zout.closeEntry();
  }

  @Override public String toString() {
    return levelName + " (" + worldDirectory.getName() + ")";
  }

  /** The name of this world (not the world directory name). */
  public String levelName() {
    return levelName;
  }

  public int getVersionId() {
    return versionId;
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
    // Compares world names and directories.
    return toString().compareToIgnoreCase(o.toString());
  }

  public long getSeed() {
    return seed;
  }

  public Date getLastModified() {
    return new Date(this.worldDirectory.lastModified());
  }

  /**
   * Get the resource pack that is bundled with this world, i.e. the contained resourced directory or resources.zip.
   *
   * @return Resource pack file/directory or empty optional if this world has no bundled resource pack
   */
  public Optional<File> getResourcePack() {
    File resourcePack = new File(getWorldDirectory(), "resources.zip");
    if (resourcePack.isFile()) {
      return Optional.of(resourcePack);
    }
    resourcePack = new File(getWorldDirectory(), "resources");
    if (resourcePack.isDirectory()) {
      return Optional.of(resourcePack);
    }
    return Optional.empty();
  }
}
