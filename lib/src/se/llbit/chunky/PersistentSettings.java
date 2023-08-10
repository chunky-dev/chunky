/* Copyright (c) 2012-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2012-2021 Chunky contributors
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
package se.llbit.chunky;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import se.llbit.chunky.renderer.RenderOptions;
import se.llbit.chunky.resources.SettingsDirectory;
import se.llbit.fxutil.WindowPosition;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;

/**
 * Utility class for managing global Chunky settings.
 */
public final class PersistentSettings {

  public static JsonSettings settings = new JsonSettings();

  public static final String SETTINGS_FILE = "chunky.json";
  public static final String DEFAULT_SCENE_DIRECTORY_NAME = "scenes";

  public static final double DEFAULT_WATER_OPACITY = .42;
  public static final double DEFAULT_WATER_VISIBILITY = 9;

  public static final double DEFAULT_WATER_RED = 0.03;
  public static final double DEFAULT_WATER_GREEN = 0.13;
  public static final double DEFAULT_WATER_BLUE = 0.16;

  private static final double WAVELENGTH_RED = 650;
  private static final double WAVELENGTH_GREEN = 570;
  private static final double WAVELENGTH_BLUE = 475;

  public static final double W = Math.pow(WAVELENGTH_BLUE / 1e3, 4);
  public static final double RM = 0.2;

  // fog color scaled proportional to 1/wavelength^4 (rayleigh scatter)
  public static final double DEFAULT_FOG_RED =
      RM + (1 - RM) * W * Math.pow(WAVELENGTH_RED / 1e3, -4);
  public static final double DEFAULT_FOG_GREEN =
      RM + (1 - RM) * W * Math.pow(WAVELENGTH_GREEN / 1e3, -4);
  public static final double DEFAULT_FOG_BLUE = 1;

  public static final int DEFAULT_RAY_DEPTH = 5;
  public static final int DEFAULT_SPP_TARGET = 1000;

  public static final int DEFAULT_DIMENSION = 0;

  /**
   * Default canvas width.
   */
  public static final int DEFAULT_3D_CANVAS_WIDTH = 400;

  /**
   * Default canvas height.
   */
  public static final int DEFAULT_3D_CANVAS_HEIGHT = 400;


  private static File settingsDir;
  private static File cacheDir;
  private static File settingsFile;

  private PersistentSettings() {
  }

  static {
    File directory = SettingsDirectory.getSettingsDirectory();
    if (directory == null) {
      directory = SettingsDirectory.getHomeDirectory();
    }
    changeSettingsDirectory(directory);
    load();
  }

  public static void changeSettingsDirectory(File directory) {
    settingsDir = directory;
    settingsFile = new File(settingsDir, SETTINGS_FILE);
    cacheDir = new File(settingsDir, "cache");
  }

  private static void load() {
    settings.load(settingsFile);
    migrateOldSettings();
  }

  private static void migrateOldSettings() {
    String lastTexturePack = settings.getString("lastTexturePack", null);
    if(lastTexturePack != null) {
      setLastTexturePack(lastTexturePack);
      // TODO: Remove legacy setting in 2.6.0
//      settings.removeSetting("lastTexturePack");
    }
  }

  public static void reload() {
    load();
  }

  public static void save() {
    settings.save(settingsDir, settingsFile);
  }

  /**
   * Note: must not be called before configuring the settings directory
   * via the first-time setup dialog in the launcher.
   * @return The directory where Chunky settings are stored
   */
  public static File settingsDirectory() {
    return settingsDir;
  }

  public static File cacheDirectory() {
    return cacheDir;
  }

  /**
   * @return The default scene directory
   */
  public static File getSceneDirectory() {
    String defaultPath = new File(settingsDir, DEFAULT_SCENE_DIRECTORY_NAME).getAbsolutePath();
    return new File(settings.getString("sceneDirectory", defaultPath));
  }

  /**
   * @return Default number of render threads
   */
  public static int getRenderThreadCount() {
    return settings.getInt("numThreads", RenderOptions.RENDER_THREAD_COUNT_DEFAULT);
  }

  /**
   * @return Default number of map loading threads
   */
  public static int getMapLoadingThreadCount() {
    return getRenderThreadCount(); // TODO
  }

  /**
   * Set default number of render threads.
   */
  public static void setRenderThreadCount(int renderThreadCount) {
    renderThreadCount = Math.max(RenderOptions.RENDER_THREAD_COUNT_MIN, renderThreadCount);
    renderThreadCount = Math.min(RenderOptions.RENDER_THREAD_COUNT_MAX, renderThreadCount);
    settings.setInt("numThreads", renderThreadCount);
    save();
  }

  /**
   * @return CPU load setting
   */
  public static int getCPULoad() {
    return settings.getInt("cpuLoad", RenderOptions.TARGET_CPU_LOAD_PERCENTAGE);
  }

  /**
   * Change the default CPU load.
   */
  public static void setCPULoad(int cpuLoad) {
    cpuLoad = Math.max(1, cpuLoad);
    cpuLoad = Math.min(100, cpuLoad);
    settings.setInt("cpuLoad", cpuLoad);
    save();
  }

  public static void setLastWorld(File worldDirectory) {
    settings.setString("lastWorld", worldDirectory.getAbsolutePath());
    save();
  }

  /** @return the world directory of the previously loaded world. */
  public static File getLastWorld() {
    String lastWorld = settings.getString("lastWorld", "");
    return lastWorld.isEmpty() ? null : new File(lastWorld);
  }

  public static void setSkinDirectory(File directory) {
    settings.setString("skinDirectory", directory.getAbsolutePath());
    save();
  }

  public static String getSkinDirectory() {
    return settings.getString("skinDirectory", "");
  }

  /**
   * please use {@link PersistentSettings#setEnabledResourcePacks(File...)}
   */
  @Deprecated
  public static void setLastTexturePack(String path) {
    setEnabledResourcePacks(
      parseResourcePackPaths(path).toArray(new File[0])
    );
    save();
  }

  /**
   * please use {@link PersistentSettings#getEnabledResourcePacks()}
   */
  @Deprecated
  public static String getLastTexturePack() {
    return getEnabledResourcePacks().stream()
      .map(File::toString)
      .collect(Collectors.joining(File.pathSeparator));
  }

  /**
   * helper method for parsing resource pack file configurations
   * @param paths paths separated by {@link File#pathSeparator}
   * @return list of files of all paths which could be converted to files,
   *         can contain non-existing or invalid resource packs
   */
  public static List<File> parseResourcePackPaths(String paths) {
    return parseResourcePackPaths(Arrays.stream(paths.split(File.pathSeparator)));
  }

  /**
   * helper method for converting a list of possible resource pack paths to a list of files
   * @return list of files of all paths which could be converted to files,
   *         can contain non-existing or invalid resource packs
   */
  public static List<File> parseResourcePackPaths(Stream<String> paths) {
    return paths
      .map(String::trim)
      .map(pathString -> pathString.isEmpty() ? null : new File(pathString))
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }

  public static void setEnabledResourcePacks(File... enabledTexturePacks) {
    JsonArray array = new JsonArray(enabledTexturePacks.length);
    for(File texturePackFile : enabledTexturePacks) {
      array.add(texturePackFile.toString());
    }
    settings.set("enabledResourcePacks", array);
    // TODO: Remove legacy setting in 2.6.0
    settings.setString("lastTexturePack", getLastTexturePack());
    save();
  }

  public static List<File> getEnabledResourcePacks() {
    return parseResourcePackPaths(
      settings.get("enabledResourcePacks").array().elements.stream()
        .map(jsonValue -> jsonValue.stringValue(""))
    );
  }

  public static void setRayDepth(int rayDepth) {
    settings.setInt("rayDepth", rayDepth);
    save();
  }

  public static int getSppTargetDefault() {
    return settings.getInt("sppTargetDefault", DEFAULT_SPP_TARGET);
  }

  /**
   * @return the default configured ray depth
   */
  public static int getRayDepthDefault() {
    return settings.getInt("rayDepth", DEFAULT_RAY_DEPTH);
  }

  public static int get3DCanvasHeight() {
    return settings.getInt("3dcanvas.height", DEFAULT_3D_CANVAS_HEIGHT);
  }

  public static int get3DCanvasWidth() {
    return settings.getInt("3dcanvas.width", DEFAULT_3D_CANVAS_WIDTH);
  }

  public static void setSceneDirectory(File dir) {
    settings.setString("sceneDirectory", dir.getAbsolutePath());
    save();
  }

  public static boolean containsKey(String key) {
    return settings.containsKey(key);
  }

  public static void setSppTargetDefault(int targetSPP) {
    settings.setInt("sppTargetDefault", targetSPP);
    save();
  }

  public static void set3DCanvasSize(int width, int height) {
    settings.setInt("3dcanvas.width", width);
    settings.setInt("3dcanvas.height", height);
    save();
  }

  public static String getMinecraftDirectory() {
    return settings.getString("minecraftDir", "");
  }

  public static void setMinecraftDirectory(String path) {
    settings.setString("minecraftDir", path);
    save();
  }

  public static void setStringOption(String name, String value) {
    settings.setString(name, value);
    save();
  }

  public static void setIntOption(String name, int value) {
    settings.setInt(name, value);
    save();
  }

  public static void resetOption(String name) {
    settings.removeSetting(name);
    save();
  }

  public static boolean getFollowPlayer() {
    return settings.getBool("followPlayer", false);
  }

  public static void setFollowPlayer(boolean value) {
    settings.setBool("followPlayer", value);
    save();
  }

  public static boolean getFollowCamera() {
    return settings.getBool("followCamera", false);
  }

  public static void setFollowCamera(boolean value) {
    settings.setBool("followCamera", value);
    save();
  }

  public static void setStillWater(boolean value) {
    settings.setBool("stillWater", value);
    save();
  }

  public static boolean getStillWater() {
    return settings.getBool("stillWater", false);
  }

  public static void setWaterOpacity(double value) {
    settings.setDouble("waterOpacity", value);
    save();
  }

  public static double getWaterOpacity() {
    return settings.getDouble("waterOpacity", DEFAULT_WATER_OPACITY);
  }

  public static void setWaterVisibility(double value) {
    settings.setDouble("waterVisibility", value);
    save();
  }

  public static double getWaterVisibility() {
    return settings.getDouble("waterVisibility", DEFAULT_WATER_VISIBILITY);
  }

  public static void setUseCustomWaterColor(boolean value) {
    settings.setBool("useCustomWaterColor", value);
    save();
  }

  public static boolean getUseCustomWaterColor() {
    return settings.getBool("useCustomWaterColor", false);
  }

  public static void setWaterColor(double red, double green, double blue) {
    settings.setDouble("waterColorRed", red);
    settings.setDouble("waterColorGreen", green);
    settings.setDouble("waterColorBlue", blue);
    save();
  }

  public static double getWaterColorRed() {
    return settings.getDouble("waterColorRed", DEFAULT_WATER_RED);
  }

  public static double getWaterColorGreen() {
    return settings.getDouble("waterColorGreen", DEFAULT_WATER_GREEN);
  }

  public static double getWaterColorBlue() {
    return settings.getDouble("waterColorBlue", DEFAULT_WATER_BLUE);
  }

  /** Set the default fog color. */
  public static void setFogColor(double red, double green, double blue) {
    settings.setDouble("fogColorRed", red);
    settings.setDouble("fogColorGreen", green);
    settings.setDouble("fogColorBlue", blue);
    save();
  }

  public static double getFogColorRed() {
    return settings.getDouble("fogColorRed", DEFAULT_FOG_RED);
  }

  public static double getFogColorGreen() {
    return settings.getDouble("fogColorGreen", DEFAULT_FOG_GREEN);
  }

  public static double getFogColorBlue() {
    return settings.getDouble("fogColorBlue", DEFAULT_FOG_BLUE);
  }

  public static void setSingleColorTextures(boolean value) {
    settings.setBool("singleColorTextures", value);
    save();
  }

  public static boolean getSingleColorTextures() {
    return settings.getBool("singleColorTextures", false);
  }

  public static void setDimension(int value) {
    settings.setInt("dimension", value);
    save();
  }

  public static int getDimension() {
    return settings.getInt("dimension", DEFAULT_DIMENSION);
  }

  public static boolean getLoadPlayers() {
    return settings.getBool("loadPlayers", true);
  }

  public static void setLoadPlayers(boolean value) {
    settings.setBool("loadPlayers", value);
    save();
  }

  public static boolean getLoadArmorStands() {
    return settings.getBool("loadArmorStands", true);
  }

  public static void setLoadArmorStands(boolean value) {
    settings.setBool("loadArmorStands", value);
    save();
  }

  public static boolean getLoadBooks() {
    return settings.getBool("loadBooks", true);
  }

  public static void setLoadBooks(boolean value) {
    settings.setBool("loadBooks", value);
    save();
  }

  public static boolean getLoadPaintings() {
    return settings.getBool("loadPaintings", true);
  }

  public static void setLoadPaintings(boolean value) {
    settings.setBool("loadPaintings", value);
    save();
  }

  public static boolean getLoadBeaconBeams() {
    return settings.getBool("loadBeaconBeams", true);
  }

  public static void setLoadBeaconBeams(boolean value) {
    settings.setBool("loadBeaconBeams", value);
    save();
  }

  public static boolean getLoadOtherEntities() {
    return settings.getBool("loadOtherEntities", true);
  }

  public static void setLoadOtherEntities(boolean value) {
    settings.setBool("loadOtherEntities", value);
    save();
  }

  public static boolean drawUnknownBlocks() {
    return settings.getBool("drawUnknownBlocks", false);
  }

  /**
   * Get the plugin load order.
   * @return a JSON array on the plugins to load, specified by
   * Jar file name.
   */
  public static JsonArray getPlugins() {
    return settings.get("plugins").array();
  }

  public static void setPlugins(JsonValue value) {
    settings.set("plugins", value);
    save();
  }

  public static void setOctreeImplementation(String implementation) {
    settings.setString("octreeImplementation", implementation);
    save();
  }

  public static String getOctreeImplementation() {
    return settings.getString("octreeImplementation", "PACKED");
  }

  public static void setBvhMethod(String method) {
    settings.setString("bvhMethod", method);
    save();
  }

  public static String getBvhMethod() {
    return settings.getString("bvhMethod", "SAH_MA");
  }

  public static void setBiomeStructureImplementation(String implementation) {
    settings.setString("biomeStructureImplementation", implementation);
    save();
  }

  public static String getBiomeStructureImplementation() {
    return settings.getString("biomeStructureImplementation", "WORLD_TEXTURE_2D");
  }


  public static void setGridSizeDefault(int value) {
    settings.setInt("gridSize", value);
    save();
  }

  public static int getGridSizeDefault() {
    return settings.getInt("gridSize", 10);
  }

  public static void setPreventNormalEmitterWithSampling(boolean value) {
    settings.setBool("preventNormalEmitterWithSampling", value);
    save();
  }

  public static boolean getPreventNormalEmitterWithSampling() {
    return settings.getBool("preventNormalEmitterWithSampling", false);
  }

  /**
   * Checks if Chunky should try to load the default textures from the latest Minecraft version it can find if they
   * are not found in the selected resource packs.
   * For deterministic renders (independent of installed Minecraft versions), this option should be enabled.
   */
  public static boolean getDisableDefaultTextures() {
    return settings.getBool("disableDefaultTextures", false);
  }

  public static void setDisableDefaultTextures(boolean value) {
    settings.setBool("disableDefaultTextures", value);
    save();
  }

  public static int getCanvasScale() {
    return settings.getInt("canvasScale", 100);
  }

  public static void setCanvasScale(int scale) {
    settings.setInt("canvasScale", scale);
    save();
  }

  public static boolean getCanvasFitToScreen() {
    return settings.getBool("canvasFitToScreen", true);
  }

  public static void setCanvasFitToScreen(boolean fitToScreen) {
    settings.setBool("canvasFitToScreen", fitToScreen);
    save();
  }

  public static WindowPosition getPreviousWindowPosition() {
    JsonValue windowX = settings.get("window.x");
    JsonValue windowY = settings.get("window.y");
    JsonValue windowWidth = settings.get("window.width");
    JsonValue windowHeight = settings.get("window.height");
    JsonValue windowMaximized = settings.get("window.maximized");

    if (!windowX.isUnknown() && !windowY.isUnknown() && !windowWidth.isUnknown()
        && !windowHeight.isUnknown() && !windowMaximized.isUnknown()) {
      return new WindowPosition(
          windowX.doubleValue(0),
          windowY.doubleValue(0),
          windowWidth.doubleValue(1800),
          windowHeight.doubleValue(1000),
          windowMaximized.boolValue(false));
    }
    return null;
  }

  public static void setWindowPosition(WindowPosition position) {
    settings.setDouble("window.x", position.getX());
    settings.setDouble("window.y", position.getY());
    settings.setDouble("window.width", position.getWidth());
    settings.setDouble("window.height", position.getHeight());
    settings.setBool("window.maximized", position.isMaximized());
    save();
  }

  public static JsonArray getTableSortConfig(String table) {
    if (!settings.get("tables").isObject()) {
      return null;
    }
    JsonObject tables = settings.get("tables").asObject();
    if (!tables.get(table).isObject()) {
      return null;
    }
    return settings.get("tables").asObject().get(table).asObject().get("sort").asArray();
  }

  public static void setTableSortConfig(String table, JsonArray config) {
    if (!settings.get("tables").isObject()) {
      settings.set("tables", new JsonObject());
    }
    JsonObject tables = settings.get("tables").asObject();
    if (!tables.get(table).isObject()) {
      tables.set(table, new JsonObject());
    }
    tables.get(table).asObject().set("sort", config);
    save();
  }
}
