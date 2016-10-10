/* Copyright (c) 2013-2015 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer.scene;

import org.jastadd.util.PrettyPrinter;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.OutputMode;
import se.llbit.chunky.renderer.Postprocess;
import se.llbit.chunky.renderer.Refreshable;
import se.llbit.chunky.renderer.RenderMode;
import se.llbit.chunky.renderer.ResetReason;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonMember;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonParser.SyntaxError;
import se.llbit.json.JsonValue;
import se.llbit.math.Vector3;
import se.llbit.util.JSONifiable;
import se.llbit.util.ZipExport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Basic scene description.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class SceneDescription implements Refreshable, JSONifiable {
  public static final String EXTENSION = ".json";

  /**
   * The current Scene Description Format (SDF) version.
   */
  public static final int SDF_VERSION = 7;

  public int sdfVersion = -1;
  public String name = "default";

  /**
   * Canvas width.
   */
  public int width;

  /**
   * Canvas height.
   */
  public int height;
  protected double exposure = Scene.DEFAULT_EXPOSURE;
  public Postprocess postprocess = Postprocess.DEFAULT;
  public OutputMode outputMode = OutputMode.DEFAULT;
  public long renderTime;

  /**
   * Current SPP for the scene.
   */
  public int spp = 0;

  /**
   * Target SPP for the scene.
   */
  protected int sppTarget = PersistentSettings.getSppTargetDefault();

  /**
   * Recursive ray depth limit (not including Russian Roulette).
   */
  protected int rayDepth = PersistentSettings.getRayDepthDefault();

  protected final Sky sky = new Sky(this);
  protected final Camera camera = new Camera(this);
  protected final Sun sun = new Sun(this);

  protected String worldPath = "";
  protected int worldDimension = 0;

  protected RenderMode mode = RenderMode.PREVIEW;

  protected int dumpFrequency = Scene.DEFAULT_DUMP_FREQUENCY;
  protected boolean saveSnapshots = false;

  protected boolean emittersEnabled = Scene.DEFAULT_EMITTERS_ENABLED;
  protected double emitterIntensity = Scene.DEFAULT_EMITTER_INTENSITY;
  protected boolean sunEnabled = true;

  /**
   * Water opacity modifier.
   */
  protected double waterOpacity = PersistentSettings.getWaterOpacity();
  protected double waterVisibility = PersistentSettings.getWaterVisibility();
  protected int waterHeight = PersistentSettings.getWaterHeight();
  protected boolean stillWater = PersistentSettings.getStillWater();
  protected boolean useCustomWaterColor = PersistentSettings.getUseCustomWaterColor();
  protected final Vector3 waterColor =
      new Vector3(PersistentSettings.getWaterColorRed(), PersistentSettings.getWaterColorGreen(),
          PersistentSettings.getWaterColorBlue());
  protected final Vector3 fogColor =
      new Vector3(PersistentSettings.getFogColorRed(), PersistentSettings.getFogColorGreen(),
          PersistentSettings.getFogColorBlue());

  /**
   * Enables fast fog algorithm
   */
  protected boolean fastFog = true;

  /**
   * Fog thickness.
   */
  protected double fogDensity;

  protected boolean biomeColors = true;
  protected boolean transparentSky = false;

  protected boolean renderActors = true;

  protected Collection<ChunkPosition> chunks = new ArrayList<>();

  protected JsonObject cameraPresets = new JsonObject();
  /**
   * Indicates if the render should be forced to reset.
   */
  protected ResetReason resetReason = ResetReason.NONE;

  /**
   * Parse the scene description from a JSON file.
   *
   * @param in input stream - will be closed
   */
  public void loadDescription(InputStream in) throws IOException {
    try (JsonParser parser = new JsonParser(in)) {
      JsonObject desc = parser.parse().object();
      fromJson(desc);
    } catch (SyntaxError e) {
      throw new IOException("JSON syntax error");
    }
  }

  /**
   * Load the scene description from a JSON file.
   *
   * @param out output stream - will be closed
   */
  public void saveDescription(OutputStream out) throws IOException {
    PrettyPrinter pp = new PrettyPrinter("  ", new PrintStream(out));
    toJson().prettyPrint(pp);
    out.close();
  }

  @Override public synchronized JsonObject toJson() {
    JsonObject desc = new JsonObject();
    desc.add("sdfVersion", SDF_VERSION);
    desc.add("name", name);
    desc.add("width", width);
    desc.add("height", height);
    desc.add("exposure", exposure);
    desc.add("postprocess", postprocess.name());
    desc.add("outputMode", outputMode.name());
    desc.add("renderTime", renderTime);
    desc.add("spp", spp);
    desc.add("sppTarget", sppTarget);
    desc.add("rayDepth", rayDepth);
    desc.add("pathTrace", mode != RenderMode.PREVIEW);
    desc.add("dumpFrequency", dumpFrequency);
    desc.add("saveSnapshots", saveSnapshots);

    desc.add("emittersEnabled", emittersEnabled);
    desc.add("emitterIntensity", emitterIntensity);
    desc.add("sunEnabled", sunEnabled);
    desc.add("stillWater", stillWater);
    desc.add("waterOpacity", waterOpacity);
    desc.add("waterVisibility", waterVisibility);
    desc.add("useCustomWaterColor", useCustomWaterColor);
    if (useCustomWaterColor) {
      JsonObject colorObj = new JsonObject();
      colorObj.add("red", waterColor.x);
      colorObj.add("green", waterColor.y);
      colorObj.add("blue", waterColor.z);
      desc.add("waterColor", colorObj);
    }
    JsonObject fogColorObj = new JsonObject();
    fogColorObj.add("red", fogColor.x);
    fogColorObj.add("green", fogColor.y);
    fogColorObj.add("blue", fogColor.z);
    desc.add("fogColor", fogColorObj);
    desc.add("fastFog", fastFog);
    desc.add("biomeColorsEnabled", biomeColors);
    desc.add("transparentSky", transparentSky);
    desc.add("fogDensity", fogDensity);
    desc.add("waterHeight", waterHeight);
    desc.add("renderActors", renderActors);

    // save world info
    if (!worldPath.isEmpty()) {
      JsonObject world = new JsonObject();
      world.add("path", worldPath);
      world.add("dimension", worldDimension);
      desc.add("world", world);
    }

    desc.add("camera", camera.toJson());
    desc.add("sun", sun.toJson());
    desc.add("sky", sky.toJson());

    desc.add("cameraPresets", cameraPresets.fullCopy());

    JsonArray chunkList = new JsonArray();
    for (ChunkPosition pos : chunks) {
      JsonArray chunk = new JsonArray();
      chunk.add(pos.x);
      chunk.add(pos.z);
      chunkList.add(chunk);
    }
    desc.add("chunkList", chunkList);

    return desc;
  }

  @Override public synchronized void fromJson(JsonObject desc) {
    setResetReason(ResetReason.SCENE_LOADED);

    sdfVersion = desc.get("sdfVersion").intValue(-1);
    name = desc.get("name").stringValue("unknown");
    width = desc.get("width").intValue(Scene.MIN_CANVAS_WIDTH);
    height = desc.get("height").intValue(Scene.MIN_CANVAS_HEIGHT);
    exposure = desc.get("exposure").doubleValue(Scene.DEFAULT_EXPOSURE);
    postprocess = Postprocess.get(desc.get("postprocess").stringValue(""));
    outputMode = OutputMode.get(desc.get("outputMode").stringValue(""));
    sppTarget = desc.get("sppTarget").intValue(PersistentSettings.getSppTargetDefault());
    rayDepth = desc.get("rayDepth").intValue(PersistentSettings.getRayDepthDefault());
    boolean pathTrace = desc.get("pathTrace").boolValue(false);
    if (pathTrace) {
      mode = RenderMode.PAUSED;
    } else {
      mode = RenderMode.PREVIEW;
    }
    dumpFrequency = desc.get("dumpFrequency").intValue(Scene.DEFAULT_DUMP_FREQUENCY);
    saveSnapshots = desc.get("saveSnapshots").boolValue(false);

    emittersEnabled = desc.get("emittersEnabled").boolValue(Scene.DEFAULT_EMITTERS_ENABLED);
    emitterIntensity = desc.get("emitterIntensity").doubleValue(Scene.DEFAULT_EMITTER_INTENSITY);
    sunEnabled = desc.get("sunEnabled").boolValue(true);
    stillWater = desc.get("stillWater").boolValue(false);
    waterOpacity = desc.get("waterOpacity").doubleValue(PersistentSettings.getWaterOpacity());
    waterVisibility =
        desc.get("waterVisibility").doubleValue(PersistentSettings.getWaterVisibility());
    useCustomWaterColor =
        desc.get("useCustomWaterColor").boolValue(PersistentSettings.getUseCustomWaterColor());
    if (useCustomWaterColor) {
      JsonObject colorObj = desc.get("waterColor").object();
      waterColor.x = colorObj.get("red").doubleValue(PersistentSettings.getWaterColorRed());
      waterColor.y = colorObj.get("green").doubleValue(PersistentSettings.getWaterColorGreen());
      waterColor.z = colorObj.get("blue").doubleValue(PersistentSettings.getWaterColorBlue());
    }
    JsonObject fogColorObj = desc.get("fogColor").object();
    fogColor.x = fogColorObj.get("red").doubleValue(PersistentSettings.getFogColorRed());
    fogColor.y = fogColorObj.get("green").doubleValue(PersistentSettings.getFogColorGreen());
    fogColor.z = fogColorObj.get("blue").doubleValue(PersistentSettings.getFogColorBlue());
    fastFog = desc.get("fastFog").boolValue(true);
    biomeColors = desc.get("biomeColorsEnabled").boolValue(true);
    transparentSky = desc.get("transparentSky").boolValue(false);
    fogDensity = desc.get("fogDensity").doubleValue(Scene.DEFAULT_FOG_DENSITY);
    waterHeight = desc.get("waterHeight").intValue(0);
    renderActors = desc.get("renderActors").boolValue(true);

    // load world info
    JsonObject world = desc.get("world").object();
    worldPath = world.get("path").stringValue("");
    worldDimension = world.get("dimension").intValue(0);

    camera.fromJson(desc.get("camera").object());
    sun.fromJson(desc.get("sun").object());
    sky.fromJson(desc.get("sky").object());

    cameraPresets = desc.get("cameraPresets").object();

    // Read these after loading camera, sun, sky because they refresh the scene.
    spp = desc.get("spp").intValue(0);
    renderTime = desc.get("renderTime").longValue(0);

    chunks.clear();
    JsonArray chunkList = desc.get("chunkList").array();
    for (JsonValue elem : chunkList.getElementList()) {
      JsonArray chunk = elem.array();
      int x = chunk.get(0).intValue(Integer.MAX_VALUE);
      int z = chunk.get(1).intValue(Integer.MAX_VALUE);
      if (x != Integer.MAX_VALUE && z != Integer.MAX_VALUE) {
        chunks.add(ChunkPosition.get(x, z));
      }
    }
  }

  /**
   * Called when the scene description has been altered in a way that
   * forces the rendering to restart.
   */
  @Override public synchronized void refresh() {
    if (mode == RenderMode.PAUSED) {
      mode = RenderMode.RENDERING;
    }
    spp = 0;
    renderTime = 0;
    setResetReason(ResetReason.SETTINGS_CHANGED);
    notifyAll();
  }

  /**
   * @return The sun state object.
   */
  public Sun sun() {
    return sun;
  }

  /**
   * @return The sky state object.
   */
  public Sky sky() {
    return sky;
  }

  /**
   * @return The camera state object.
   */
  public Camera camera() {
    return camera;
  }

  /**
   * Delete all scene files from the scene directory, leaving only
   * snapshots untouched.
   */
  public void delete(File sceneDir) {
    String[] extensions =
        {".json", ".dump", ".octree", ".foliage", ".grass", ".json.backup", ".dump.backup",};
    for (String extension : extensions) {
      File file = new File(sceneDir, name + extension);
      if (file.isFile()) {
        //noinspection ResultOfMethodCallIgnored
        file.delete();
      }
    }
  }

  /**
   * Export the scene to a zip file.
   */
  public void exportToZip(File targetFile) {
    String[] extensions = {".json", ".dump", ".octree", ".foliage", ".grass",};
    ZipExport.zip(targetFile, PersistentSettings.getSceneDirectory(), name, extensions);
  }

  public void saveCameraPreset(String name) {
    camera.name = name;
    for (JsonMember member : cameraPresets.getMemberList()) {
      if (member.getName().equals(name)) {
        member.setValue(camera.toJson());
        return;
      }
    }
    cameraPresets.add(name, camera.toJson());
  }

  public void loadCameraPreset(String name) {
    JsonValue value = cameraPresets.get(name);
    if (!value.isUnknown()) {
      camera.fromJson(value.object());
      refresh();
    }
  }

  public void deleteCameraPreset(String name) {
    for (int i = 0; i < cameraPresets.getNumMember(); ++i) {
      if (cameraPresets.getMember(i).getName().equals(name)) {
        cameraPresets.getMemberList().removeChild(i);
        return;
      }
    }
  }

  public JsonObject getCameraPresets() {
    return cameraPresets;
  }

  public RenderMode getMode() {
    return mode;
  }

  public void setFogDensity(double newValue) {
    if (newValue != fogDensity) {
      this.fogDensity = newValue;
      refresh();
    }
  }

  public double getFogDensity() {
    return fogDensity;
  }

  public void setFastFog(boolean value) {
    if (fastFog != value) {
      fastFog = value;
      refresh();
    }
  }

  public boolean fastFog() {
    return fastFog;
  }

  /**
   * @return {@code true} if volumetric fog is enabled
   */
  public boolean fogEnabled() {
    return fogDensity > 0.0;
  }

  public OutputMode getOutputMode() {
    return outputMode;
  }

  public void setOutputMode(OutputMode mode) {
    outputMode = mode;
  }

  public int numberOfChunks() {
    return chunks.size();
  }

  /**
   * Clears the reset reason and returns the previous reason.
   * @return the current reset reason
   */
  public synchronized ResetReason getResetReason() {
    return resetReason;
  }

  public void setResetReason(ResetReason resetReason) {
    if (this.resetReason != ResetReason.SCENE_LOADED) {
      this.resetReason = resetReason;
    }
  }
}
