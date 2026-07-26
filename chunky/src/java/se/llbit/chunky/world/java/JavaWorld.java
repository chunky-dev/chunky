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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class JavaWorld extends World {
  /** The currently supported NBT version of level.dat files. */
  public static final int NBT_VERSION = 19133;

  /** Minimum level.dat data version of tall worlds (21w06a). */
  public static final int VERSION_21W06A = 2694;
  public static final int VERSION_1_12_2 = 1343;

  protected final int versionId;

  /**
   * In a java world spawn position is per-world and not per-dimension, so we store it here.
   */
  protected final Vector3i spawnPos;

  /**
   * In a java world player data is per-world and not per-dimension, so we store it here.
   */
  protected final Collection<PlayerEntityData> playerEntities;

  protected UUID singleplayerPlayerUuid;

  /** Timestamp of when player data was last loaded. */
  protected long playerDataTimestamp;

  protected JavaWorld(Info info, long timestamp, int versionId, Set<PlayerEntityData> playerEntities, Vector3i spawnPos) {
    super(info);
    this.versionId = versionId;
    this.playerEntities = playerEntities;
    this.spawnPos = spawnPos;
    this.playerDataTimestamp = timestamp;
  }

  public static Optional<World.Info> loadWorldInfo(@NotNull Path worldDirectory, LoggedWarnings warnings, JavaWorldFormat format) {
    return readWorldData(worldDirectory, warnings, data -> {
      Tag gameType = data.tags.get(".Data.GameType");
      String gameMode = switch (gameType.intValue(0)) {
        case 0 -> "Survival";
        case 1 -> "Creative";
        case 2 -> "Adventure";
        default -> "Unknown";
      };
      Tag randomSeed = data.tags.get(".Data.RandomSeed");

      String levelName = MinecraftText.removeFormatChars(data.tags.get(".Data.LevelName").stringValue(data.levelName));

      long seed = randomSeed.longValue(0);

      return new Info(levelName, worldDirectory, data.modTime, seed, gameMode, format);
    });
  }

  public static World loadWorld(Info info, LoggedWarnings warnings) {
    return readWorldData(info.path(), warnings, data -> {
      Tag versionId = data.tags.get(".Data.Version.Id");

      Tag player = data.tags.get(".Data.Player");
      Set<PlayerEntityData> playerEntities = getPlayerEntityData(info.path(), player);

      Tag spawnX = player.get("SpawnX");
      Tag spawnY = player.get("SpawnY");
      Tag spawnZ = player.get("SpawnZ");
      boolean hasSpawnPos = !(spawnX.isError() || spawnY.isError() || spawnZ.isError());
      Vector3i spawnPos = new Vector3i();
      if (hasSpawnPos) {
        spawnPos = new Vector3i(spawnX.intValue(0), spawnY.intValue(0), spawnZ.intValue(0));
      }

      JavaWorld world = new JavaWorld(info, data.modTime, versionId.intValue(), playerEntities, spawnPos);

      Tag singleplayerUuid = data.tags.get(".Data.singleplayer_uuid");
      if (singleplayerUuid.isIntArray(4)) {
        world.singleplayerPlayerUuid = UuidUtil.intsToUuid(singleplayerUuid.intArray());
      } else if (!player.isError()) {
        world.singleplayerPlayerUuid = PlayerEntityData.getUuid(player);
      }

      return (World) world;
    }).orElse(EmptyWorld.INSTANCE);
  }

  @Override
  public Set<Dimension.Identifier> getAvailableDimensions() {
    return Set.of(Dimension.Identifier.OVERWORLD, // TODO: return the actual set of dimensions on disk.
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
      getInfo().path(),
      dimensionId,
      this.playerEntities.stream().filter(player -> player.dimension.equals(dimensionId)).collect(Collectors.toSet()),
      this.spawnPos
    );
    currentDimension.reloadPlayerData();
    return currentDimension;
  }

  @NotNull
  private static Dimension loadDimension(JavaWorld world, Path worldDirectory, Dimension.Identifier dimensionId, Set<PlayerEntityData> playerEntities, @Nullable Vector3i spawnPos) {
    Path dimensionDirectory = worldDirectory.resolve("dimensions").resolve(dimensionId.namespace()).resolve(dimensionId.name());
    if (Files.exists(dimensionDirectory)) {
      // 26.1-snapshot-6 or later
      return new JavaDimension(world, dimensionId, dimensionDirectory, playerEntities, spawnPos);
    }

    dimensionDirectory = switch (dimensionId.getNamespacedName()) { // TODO in Java 21+ we can use `switch (dimensionId)` here
      case "minecraft:the_nether" -> worldDirectory.resolve("DIM-1");
      case "minecraft:the_end" -> worldDirectory.resolve("DIM1");
      default -> worldDirectory;
    };
    if (Files.isDirectory(dimensionDirectory.resolve("region3d"))) {
      return new CubicDimension(world, dimensionId, dimensionDirectory, playerEntities, spawnPos);
    } else {
      return new JavaDimension(world, dimensionId, dimensionDirectory, playerEntities, spawnPos);
    }
  }

  public Optional<UUID> getSingleplayerPlayerUuid() {
    return Optional.ofNullable(singleplayerPlayerUuid);
  }

  @NotNull
  private static Set<PlayerEntityData> getPlayerEntityData(Path worldDirectory, Tag player) {
    Set<PlayerEntityData> playerEntities = new HashSet<>();
    if (!player.isError()) {
      playerEntities.add(new PlayerEntityData(player));
    }
    loadAdditionalPlayers(worldDirectory, playerEntities);
    return playerEntities;
  }

  private static void loadAdditionalPlayers(Path worldDirectory, Set<PlayerEntityData> playerEntities) {
    loadPlayerData(worldDirectory.resolve("players"), playerEntities);
    loadPlayerData(worldDirectory.resolve("playerdata"), playerEntities);
    loadPlayerData(worldDirectory.resolve("players").resolve("data"), playerEntities); // 26.1-snapshot-6 or later
  }

  private static void loadPlayerData(Path playerDataDirectory, Set<PlayerEntityData> playerEntities) {
    if (Files.isDirectory(playerDataDirectory)) {
      try (DirectoryStream<Path> paths = Files.newDirectoryStream(playerDataDirectory)) {
        for (Path player : paths) {
          try (DataInputStream in = new DataInputStream(
            new GZIPInputStream(new FileInputStream(player.toFile())))) {
            playerEntities.add(new PlayerEntityData(NamedTag.read(in).unpack()));
          } catch (IOException e) {
            Log.infof("Could not read player data file '%s'", player.toAbsolutePath());
          }
        }
      } catch (IOException e) {
        Log.infof("Could not list player data directory '%s'", playerDataDirectory.toAbsolutePath());
      }
    }
  }

  /**
   * Reload player data for the current dimension. This method is not in Dimension because players are per-world, not per-dimension
   * @return {@code true} if player data was reloaded.
   */
  synchronized boolean reloadPlayerData() {
    Path worldFile = getInfo().path().resolve("level.dat");
    long lastModified;
    try {
      lastModified = Files.getLastModifiedTime(worldFile).toMillis();
    } catch (IOException e) {
      return false;
    }
    if (lastModified == playerDataTimestamp) {
      return false;
    }
    Log.infof("world %s: timestamp updated: reading player data", getInfo().name());
    playerDataTimestamp = lastModified;

    try (FileInputStream fin = new FileInputStream(worldFile.toFile());
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
      this.playerEntities.addAll(getPlayerEntityData(getInfo().path(), player));
    } catch (IOException e) {
      Log.infof("Could not read the level.dat file for world %s while trying to reload player data!", getInfo().name());
      return false;
    }
    return true;
  }

  private record WorldData(String levelName, long modTime, Map<String, Tag> tags) {}
  private static <T> Optional<T> readWorldData(@NotNull Path worldDirectory, LoggedWarnings warnings, Function<WorldData, T> consumer) {
    String levelName = worldDirectory.getFileName().toString();
    Path levelDat = worldDirectory.resolve("level.dat");
    try (FileInputStream fin = new FileInputStream(levelDat.toFile());
         InputStream gzin = new GZIPInputStream(fin);
         DataInputStream in = new DataInputStream(gzin)) {
      long modtime = Files.getLastModifiedTime(levelDat).toMillis();
      Set<String> request = new HashSet<>();
      request.add(".Data.version");
      request.add(".Data.Version.Id");
      request.add(".Data.RandomSeed");
      request.add(".Data.Player");
      request.add(".Data.singleplayer_uuid");
      request.add(".Data.LevelName");
      request.add(".Data.GameType");

      Map<String, Tag> result = NamedTag.quickParse(in, request);

      Tag version = result.get(".Data.version");
      if (warnings == LoggedWarnings.NORMAL && version.intValue() != NBT_VERSION) {
        Log.warnf("The world format for the world %s is not supported by Chunky.\n" + "Will attempt to load the world anyway.",
          levelName);
      }

      return Optional.of(consumer.apply(new WorldData(levelName, modtime, result)));
    } catch (FileNotFoundException e) {
      if (warnings == LoggedWarnings.NORMAL) {
        Log.infof("Could not find level.dat file for world %s!", levelName);
      }
    } catch (IOException e) {
      if (warnings == LoggedWarnings.NORMAL) {
        Log.infof("Could not read the level.dat file for world %s!", levelName);
      }
    }
    return Optional.empty();
  }
}
