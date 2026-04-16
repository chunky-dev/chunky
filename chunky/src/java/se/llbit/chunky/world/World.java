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

import se.llbit.log.Log;
import se.llbit.math.Vector3i;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.Tag;
import se.llbit.util.MinecraftText;
import se.llbit.util.UuidUtil;
import se.llbit.util.annotation.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

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

  /** Default sea water level. */
  public static final int SEA_LEVEL = 63;

  /** Minimum level.dat data version of tall worlds (21w06a). */
  public static final int VERSION_21W06A = 2694;
  public static final int VERSION_1_12_2 = 1343;

  private final File worldDirectory;

  protected Dimension currentDimension;

  private final String levelName;
  private int gameMode = 0;
  private final long seed;

  private int versionId;

  /** Timestamp for level.dat when player data was last loaded. */
  private long timestamp;

  private UUID singleplayerPlayerUuid;

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

  public void loadDimension(Dimension.Identifier dimensionId) {
    currentDimension = loadDimension(this, this.worldDirectory, dimensionId, Collections.emptySet());
    currentDimension.reloadPlayerData();
  }

  /**
   * Parse player location and level name.
   *
   * @return {@code true} if the world data was loaded
   */
  public static World loadWorld(File worldDirectory, Dimension.Identifier dimensionId, LoggedWarnings warnings) {
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
      request.add(".Data.singleplayer_uuid");
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
      Tag singleplayerUuid = result.get(".Data.singleplayer_uuid");
      Tag gameType = result.get(".Data.GameType");
      Tag randomSeed = result.get(".Data.RandomSeed");
      levelName = MinecraftText.removeFormatChars(result.get(".Data.LevelName").stringValue(levelName));

      long seed = randomSeed.longValue(0);

      Set<PlayerEntityData> playerEntities = getPlayerEntityData(worldDirectory, dimensionId, player);

      World world = new World(levelName, worldDirectory, seed, modtime);
      world.gameMode = gameType.intValue(0);
      world.versionId = versionId.intValue();
      if (singleplayerUuid.isIntArray(4)) {
        world.singleplayerPlayerUuid = UuidUtil.intsToUuid(singleplayerUuid.intArray());
      } else if (!player.isError()) {
        world.singleplayerPlayerUuid = PlayerEntityData.getUuid(player);
      }

      Dimension dimension = loadDimension(world, worldDirectory, dimensionId, playerEntities);

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
  private static Dimension loadDimension(World world, File worldDirectory, Dimension.Identifier dimensionId, Set<PlayerEntityData> playerEntities) {
    File dimensionDirectory = Path.of(worldDirectory.getPath(), "dimensions", dimensionId.namespace(), dimensionId.name()).toFile();
    if (dimensionDirectory.exists()) {
      // 26.1-snapshot-6 or later
      return new Dimension(world, dimensionId, dimensionDirectory, playerEntities);
    }

    dimensionDirectory = switch (dimensionId.getNamespacedName()) { // TODO in Java 21+ we can use `switch (dimensionId)` here
      case "minecraft:the_nether" -> new File(worldDirectory, "DIM-1");
      case "minecraft:the_end" -> new File(worldDirectory, "DIM1");
      default -> worldDirectory;
    };
    if (new File(dimensionDirectory, "region3d").exists()) {
      return new CubicDimension(world, dimensionId, dimensionDirectory, playerEntities);
    } else {
      return new Dimension(world, dimensionId, dimensionDirectory, playerEntities);
    }
  }

  @NotNull
  private static Set<PlayerEntityData> getPlayerEntityData(File worldDirectory, Dimension.Identifier dimensionId, Tag player) {
    Set<PlayerEntityData> playerEntities = new HashSet<>();
    if (!player.isError()) {
      playerEntities.add(new PlayerEntityData(player));
    }
    loadAdditionalPlayers(worldDirectory, playerEntities);
    // Filter for the players only within the requested dimension
    playerEntities = playerEntities.stream().filter(playerData -> playerData.dimension.equals(dimensionId)).collect(Collectors.toSet());
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
      request.add(".Data.singleplayer_uuid");
      Map<String, Tag> result = NamedTag.quickParse(in, request);
      Tag player = result.get(".Data.Player");
      Tag singleplayerUuid = result.get(".Data.singleplayer_uuid");
      if (singleplayerUuid.isIntArray(4)) {
        singleplayerPlayerUuid = UuidUtil.intsToUuid(singleplayerUuid.intArray());
      } else if (!player.isError()) {
        singleplayerPlayerUuid = PlayerEntityData.getUuid(player);
      }

      currentDimension.setPlayerEntities(getPlayerEntityData(worldDirectory, currentDimension.getDimensionId(), player));
    } catch (IOException e) {
      Log.infof("Could not read the level.dat file for world %s while trying to reload player data!", levelName);
      return false;
    }
    return true;
  }

  private static void loadAdditionalPlayers(File worldDirectory, Set<PlayerEntityData> playerEntities) {
    loadPlayerData(new File(worldDirectory, "players"), playerEntities);
    loadPlayerData(new File(worldDirectory, "playerdata"), playerEntities);
    loadPlayerData(new File(new File(worldDirectory, "players"), "data"), playerEntities); // 26.1-snapshot-6 or later
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

  public Optional<UUID> getSingleplayerPlayerUuid() {
    return Optional.ofNullable(singleplayerPlayerUuid);
  }

  /**
   * @return The world directory
   */
  public File getWorldDirectory() {
    return worldDirectory;
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
    return switch (gameMode) {
      case 0 -> "Survival";
      case 1 -> "Creative";
      case 2 -> "Adventure";
      default -> "Unknown";
    };
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
    for (File resourcepacksDirectory : new File[]{getWorldDirectory(), new File(getWorldDirectory(), "resourcepacks")}) {
      if (resourcepacksDirectory.isDirectory()) {
        File resourcePack = new File(resourcepacksDirectory, "resources.zip");
    if (resourcePack.isFile()) {
      return Optional.of(resourcePack);
    }
        resourcePack = new File(resourcepacksDirectory, "resources");
    if (resourcePack.isDirectory()) {
      return Optional.of(resourcePack);
        }
      }
    }
    return Optional.empty();
  }
}
