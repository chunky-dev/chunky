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

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
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
public class JavaWorld extends World {

  /** The currently supported NBT version of level.dat files. */
  public static final int NBT_VERSION = 19133;

  /** Minimum level.dat data version of tall worlds (21w06a). */
  public static final int VERSION_21W06A = 2694;
  public static final int VERSION_1_12_2 = 1343;

  /** Nether dimension index. */
  public static final int NETHER_DIMENSION_IDX = -1;

  /** Overworld dimension index. */
  public static final int OVERWORLD_DIMENSION_IDX = 0;

  /** End dimension index. */
  public static final int END_DIMENSION_IDX = 1;

  public static final Map<String, Integer> VANILLA_DIMENSION_ID_TO_IDX = Collections.unmodifiableMap(new Object2IntOpenHashMap<>(
    new String[] { NETHER_DIMENSION_ID, OVERWORLD_DIMENSION_ID, END_DIMENSION_ID },
    new int[] { NETHER_DIMENSION_IDX, OVERWORLD_DIMENSION_IDX, END_DIMENSION_IDX }
  ));

  public static final Map<Integer, String> VANILLA_DIMENSION_IDX_TO_ID = Collections.unmodifiableMap(new Int2ObjectOpenHashMap<>(
    new int[] { NETHER_DIMENSION_IDX, OVERWORLD_DIMENSION_IDX, END_DIMENSION_IDX },
    new String[] { NETHER_DIMENSION_ID, OVERWORLD_DIMENSION_ID, END_DIMENSION_ID }
  ));

  protected int versionId;

  /**
   * In a java world player data is per-world and not per-dimension, so we store it here.
   */
  protected final Set<PlayerEntityData> playerEntities;

  /**
   * In a java world spawn position is per-world and not per-dimension, so we store it here.
   */
  protected final Vector3i spawnPos;

  /**
   * @param levelName name of the world (not the world directory).
   * @param worldDirectory Minecraft world directory.
   * @param seed
   * @param timestamp
   */
  protected JavaWorld(String levelName, File worldDirectory, long seed, long timestamp, Set<PlayerEntityData> playerEntities, Vector3i spawnPos) {
    super(levelName, worldDirectory, seed, timestamp);
    this.playerEntities = playerEntities;
    this.spawnPos = spawnPos;
  }

  @Override
  public Set<String> availableDimensions() {
    return new ObjectArraySet<>(VANILLA_DIMENSION_ID_TO_IDX.keySet());
  }

  @Override
  public Optional<String> defaultDimension() {
    return Optional.of(OVERWORLD_DIMENSION_ID);
  }

  public Dimension loadDimension(String dimension) {
    currentDimension = loadDimension(
      this,
      this.worldDirectory,
      dimension,
      -1,
      this.playerEntities.stream().filter(player -> player.dimension.equals(dimension)).collect(Collectors.toSet())
    );
    return currentDimension;
  }

  /**
   * Parse player location and level name.
   *
   * @return {@code true} if the world data was loaded
   */
  public static World loadWorld(File worldDirectory, LoggedWarnings warnings) {
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
      Tag spawnX = player.get("SpawnX"); // TODO: not sure what to do with spawn location now. I guess for java worlds: the world should store it and the dimension should set it in the map view when loaded...?
      Tag spawnY = player.get("SpawnY");
      Tag spawnZ = player.get("SpawnZ");
      Tag gameType = result.get(".Data.GameType");
      Tag randomSeed = result.get(".Data.RandomSeed");
      levelName = MinecraftText.removeFormatChars(result.get(".Data.LevelName").stringValue(levelName));

      long seed = randomSeed.longValue(0);

      Set<PlayerEntityData> playerEntities = getPlayerEntityData(worldDirectory, player);

      boolean haveSpawnPos = !(spawnX.isError() || spawnY.isError() || spawnZ.isError());
      Vector3i spawnPos;
      if (haveSpawnPos) {
        spawnPos = new Vector3i(spawnX.intValue(0), spawnY.intValue(0), spawnZ.intValue(0));
      } else {
        spawnPos = new Vector3i(0, 0, 0);
      }

      JavaWorld world = new JavaWorld(levelName, worldDirectory, seed, modtime, playerEntities, spawnPos);
      world.gameMode = gameType.intValue(0);
      world.versionId = versionId.intValue();

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
  protected static JavaDimension loadDimension(JavaWorld world, File worldDirectory, String dimensionId, long modtime, Set<PlayerEntityData> playerEntities) {
    JavaDimension dimension;
    File dimensionDirectory = dimensionId.equals(JavaWorld.OVERWORLD_DIMENSION_ID) ? worldDirectory : new File(worldDirectory, "DIM" + JavaWorld.VANILLA_DIMENSION_ID_TO_IDX.get(dimensionId));
    if (new File(dimensionDirectory, "region3d").exists()) {
      dimension = new CubicDimension(world, dimensionId, dimensionDirectory, playerEntities, modtime);
    } else {
      dimension = new JavaDimension(world, dimensionId, dimensionDirectory, playerEntities, modtime);
    }
    return dimension;
  }

  @NotNull
  static Set<PlayerEntityData> getPlayerEntityData(File worldDirectory, Tag player) {
    Set<PlayerEntityData> playerEntities = new HashSet<>();
    if (!player.isError()) {
      playerEntities.add(new PlayerEntityData(player));
    }
    loadAdditionalPlayers(worldDirectory, playerEntities);
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
      this.playerEntities.clear();
      this.playerEntities.addAll(getPlayerEntityData(worldDirectory, player));
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

  protected synchronized File getDataDirectory(int dimension) {
    return dimension == 0 ?
      worldDirectory :
      new File(worldDirectory, "DIM" + dimension);
  }

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
    if (this.currentDimension == EmptyDimension.INSTANCE) {
      return;
    }
    JavaDimension currentDim = (JavaDimension) this.currentDimension;

    Map<RegionPosition, Set<ChunkPosition>> regionMap = new HashMap<>();

    for (ChunkPosition chunk : chunks) {
      RegionPosition regionPosition = chunk.getRegionPosition();
      Set<ChunkPosition> chunkSet = regionMap.computeIfAbsent(regionPosition, k -> new HashSet<>());
      chunkSet.add(new ChunkPosition(chunk.x & 31, chunk.z & 31));
    }

    int work = 0;
    progress.setJobSize(regionMap.size() + 1);

    String regionDirectory =
      currentDim.id().equals(JavaWorld.OVERWORLD_DIMENSION_ID) ? currentDim.getDimensionDirectory().getName() :
        currentDim.getDimensionDirectory().getName() + "/DIM" + JavaWorld.VANILLA_DIMENSION_ID_TO_IDX.get(currentDim.id());
    regionDirectory += "/region";

    try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(target))) {
      writeLevelDatToZip(zout);
      progress.setProgress(++work);

      for (Map.Entry<RegionPosition, Set<ChunkPosition>> entry : regionMap.entrySet()) {

        if (progress.isInterrupted())
          break;

        RegionPosition region = entry.getKey();

        appendRegionToZip(zout, currentDim.getRegionDirectory(), region,
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
    File overworld = getRegionDirectory(OVERWORLD_DIMENSION_IDX);
    WorldScanner.findExistingChunks(overworld, operator);
    WorldScanner.findExistingChunks(getRegionDirectory(NETHER_DIMENSION_IDX), operator);
    WorldScanner.findExistingChunks(getRegionDirectory(END_DIMENSION_IDX), operator);

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

}
