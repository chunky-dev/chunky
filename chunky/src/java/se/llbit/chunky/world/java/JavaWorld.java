package se.llbit.chunky.world.java;

import se.llbit.chunky.world.*;
import se.llbit.log.Log;
import se.llbit.math.Vector3i;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.Tag;
import se.llbit.util.MinecraftText;
import se.llbit.util.UuidUtil;
import se.llbit.util.annotation.NotNull;
import se.llbit.util.annotation.Nullable;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class JavaWorld extends World {
  /** The currently supported NBT version of level.dat files. */
  public static final int NBT_VERSION = 19133;

  /** Minimum level.dat data version of tall worlds (21w06a). */
  public static final int VERSION_21W06A = 2694;
  public static final int VERSION_1_12_2 = 1343;

  protected int versionId;

  /**
   * In a java world spawn position is per-world and not per-dimension, so we store it here.
   */
  protected final Vector3i spawnPos;

  /**
   * In a java world player data is per-world and not per-dimension, so we store it here.
   */
  protected final Collection<PlayerEntityData> playerEntities;

  protected UUID singleplayerPlayerUuid;

  public Optional<UUID> getSingleplayerPlayerUuid() {
    return Optional.ofNullable(singleplayerPlayerUuid);
  }

  /** Timestamp of when player data was last loaded. */
  protected long playerDataTimestamp;

  /**
   * @param levelName      name of the world (not the world directory).
   * @param worldDirectory Minecraft world directory.
   * @param seed
   * @param timestamp
   */
  protected JavaWorld(String levelName, File worldDirectory, long seed, long timestamp, int versionId, Set<PlayerEntityData> playerEntities, Vector3i spawnPos) {
    super(levelName, worldDirectory, seed);
    this.versionId = versionId;
    this.playerEntities = playerEntities;
    this.spawnPos = spawnPos;
    this.playerDataTimestamp = timestamp;
  }

  /**
   * Parse player location and level name.
   *
   * @return {@code true} if the world data was loaded
   */
  public static World loadWorld(File worldDirectory, LoggedWarnings warnings) {
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

      Set<PlayerEntityData> playerEntities = getPlayerEntityData(worldDirectory, player);

      boolean haveSpawnPos = !(spawnX.isError() || spawnY.isError() || spawnZ.isError());
      Vector3i spawnPos = new Vector3i();
      if (haveSpawnPos) {
        spawnPos = new Vector3i(spawnX.intValue(0), spawnY.intValue(0), spawnZ.intValue(0));
      }

      JavaWorld world = new JavaWorld(levelName, worldDirectory, seed, modtime, versionId.intValue(), playerEntities, spawnPos);
      world.gameMode = gameType.intValue(0);

      if (singleplayerUuid.isIntArray(4)) {
        world.singleplayerPlayerUuid = UuidUtil.intsToUuid(singleplayerUuid.intArray());
      } else if (!player.isError()) {
        world.singleplayerPlayerUuid = PlayerEntityData.getUuid(player);
      }

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

  @Override
  public Set<Dimension.Identifier> getAvailableDimensions() {
    return Set.of(Dimension.Identifier.OVERWORLD,
      Dimension.Identifier.THE_NETHER,
      Dimension.Identifier.THE_END
    );
  }

  @Override
  public Optional<Dimension.Identifier> getDefaultDimension() {
    return Optional.empty();
  }

  @Override
  public Dimension loadDimension(Dimension.Identifier dimensionId) {
    currentDimension = loadDimension(
      this,
      this.worldDirectory,
      dimensionId,
      this.playerEntities.stream().filter(player -> player.dimension.equals(dimensionId)).collect(Collectors.toSet()),
      this.spawnPos
    );
    currentDimension.reloadPlayerData();
    return currentDimension;
  }

  @NotNull
  private static Dimension loadDimension(JavaWorld world, File worldDirectory, Dimension.Identifier dimensionId, Set<PlayerEntityData> playerEntities, @Nullable Vector3i spawnPos) {
    File dimensionDirectory = Path.of(worldDirectory.getPath(), "dimensions", dimensionId.namespace(), dimensionId.name()).toFile();
    if (dimensionDirectory.exists()) {
      // 26.1-snapshot-6 or later
      return new JavaDimension(world, dimensionId, dimensionDirectory, playerEntities, spawnPos);
    }

    dimensionDirectory = switch (dimensionId.getNamespacedName()) { // TODO in Java 21+ we can use `switch (dimensionId)` here
      case "minecraft:the_nether" -> new File(worldDirectory, "DIM-1");
      case "minecraft:the_end" -> new File(worldDirectory, "DIM1");
      default -> worldDirectory;
    };
    if (new File(dimensionDirectory, "region3d").exists()) {
      return new CubicDimension(world, dimensionId, dimensionDirectory, playerEntities, spawnPos);
    } else {
      return new JavaDimension(world, dimensionId, dimensionDirectory, playerEntities, spawnPos);
    }
  }

  @NotNull
  private static Set<PlayerEntityData> getPlayerEntityData(File worldDirectory, Tag player) {
    Set<PlayerEntityData> playerEntities = new HashSet<>();
    if (!player.isError()) {
      playerEntities.add(new PlayerEntityData(player));
    }
    loadAdditionalPlayers(worldDirectory, playerEntities);
    return playerEntities;
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
   * Reload player data for the current dimension. This method is not in Dimension because players are per-world, not per-dimension
   * @return {@code true} if player data was reloaded.
   */
  synchronized boolean reloadPlayerData() {
    if (worldDirectory == null) {
      return false;
    }
    File worldFile = new File(worldDirectory, "level.dat");
    long lastModified = worldFile.lastModified();
    if (lastModified == playerDataTimestamp) {
      return false;
    }
    Log.infof("world %s: timestamp updated: reading player data", levelName);
    playerDataTimestamp = lastModified;

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

      this.playerEntities.clear();
      this.playerEntities.addAll(getPlayerEntityData(worldDirectory, player));
    } catch (IOException e) {
      Log.infof("Could not read the level.dat file for world %s while trying to reload player data!", levelName);
      return false;
    }
    return true;
  }

  public static boolean isWorldDirectory(File worldDir) {
    if (worldDir != null && worldDir.isDirectory()) {
      File levelDat = new File(worldDir, "level.dat");
      return levelDat.exists() && levelDat.isFile();
    }
    return false;
  }
}
