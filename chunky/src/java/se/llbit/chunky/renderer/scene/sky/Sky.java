/* Copyright (c) 2012 - 2022 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2012 - 2022 Chunky contributors
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
package se.llbit.chunky.renderer.scene.sky;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.renderer.SceneIOProvider;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.HDRTexture;
import se.llbit.chunky.resources.PFMTexture;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.SkymapTexture;
import se.llbit.json.Json;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.math.*;
import se.llbit.resources.ImageLoader;
import se.llbit.util.JsonSerializable;
import se.llbit.util.JsonUtil;
import se.llbit.util.annotation.NotNull;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Sky model and sky state for ray tracing.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Sky implements JsonSerializable {

  /**
   * Default sky light intensity
   */
  public static final double DEFAULT_EMITTANCE = 1;

  /**
   * Minimum sky light intensity
   */
  public static final double MIN_INTENSITY = 0.0;

  /**
   * Maximum sky light intensity
   */
  public static final double MAX_INTENSITY = 50;

  public static final int SKYBOX_UP = 0;
  public static final int SKYBOX_DOWN = 1;
  public static final int SKYBOX_FRONT = 2;
  public static final int SKYBOX_BACK = 3;
  public static final int SKYBOX_RIGHT = 4;
  public static final int SKYBOX_LEFT = 5;

  // TODO(jesper): add simulated night-time mode.

  /**
   * Sky rendering mode.
   *
   * @author Jesper Öqvist <jesper@llbit.se>
   */
  public enum SkyMode {
    /** Simulated realistic sky. */
    SIMULATED("Simulated"),

    /** Single color. */
    SOLID_COLOR("Solid Color"),

    /** Color gradient. */
    GRADIENT("Color Gradient"),

    /** Equirectangular skymap. */
    SKYMAP_EQUIRECTANGULAR("Skymap (equirectangular)"),

    /** Light probe (angular fisheye) skymap. */
    SKYMAP_ANGULAR("Skymap (angular)"),

    /** Skybox. */
    SKYBOX("Skybox");

    private final String name;

    SkyMode(String name) {
      this.name = name;
    }

    @Override public String toString() {
      return name;
    }

    public static final SkyMode DEFAULT = SIMULATED;
    public static final SkyMode[] values = values();

    public static SkyMode get(String name) {
      try {
        return SkyMode.valueOf(name);
      } catch (IllegalArgumentException e) {
        return DEFAULT;
      }
    }

  }

  @NotNull private Texture skymap = Texture.EMPTY_TEXTURE;
  private final Texture[] skybox =
      {Texture.EMPTY_TEXTURE, Texture.EMPTY_TEXTURE, Texture.EMPTY_TEXTURE, Texture.EMPTY_TEXTURE,
          Texture.EMPTY_TEXTURE, Texture.EMPTY_TEXTURE};
  private String skymapFileName = "";
  private final String[] skyboxFileName = {"", "", "", "", "", ""};
  private final Scene scene;

  private Matrix3 rotation = new Matrix3();
  private double yaw = 0, pitch = 0, roll = 0;

  private boolean mirrored = true;

  private double skyEmittance = DEFAULT_EMITTANCE;

  /** Color gradient used for the GRADIENT sky mode. */
  private List<Vector4> gradient = new LinkedList<>();

  /** Color used for the SOLID_COLOR sky mode. */
  private final Vector3 color = new Vector3(0, 0, 0);

  /** Current sky rendering mode. */
  private SkyMode mode = SkyMode.DEFAULT;

  private boolean textureInterpolation = true;

  /** Simulated skies. */
  public final static List<SimulatedSky> skies = new ArrayList<>();

  static {
    skies.add(new PreethamSky());
    skies.add(new NishitaSky());
  }

  /** Simulated sky mode. */
  private SimulatedSky simulatedSkyMode = skies.get(0);

  private final SkyCache skyCache;

  public Sky(Scene sceneDescription) {
    this.scene = sceneDescription;
    makeDefaultGradient(gradient);
    skyCache = new SkyCache(this);
    rotation.setIdentity();
  }

  /**
   * Load the configured skymap file
   */
  public void reloadSkymap(SceneIOProvider ioContext) {
    switch (mode) {
      case SKYMAP_EQUIRECTANGULAR:
      case SKYMAP_ANGULAR:
        if (!skymapFileName.isEmpty()) {
          loadSkymap(ioContext, skymapFileName);
        }
        break;
      case SKYBOX:
        for (int i = 0; i < 6; ++i) {
          if (!skyboxFileName[i].isEmpty()) {
            loadSkyboxTexture(ioContext, skyboxFileName[i], i);
          }
        }
      default:
        break;
    }
  }

  /**
   * Load a panoramic skymap texture.
   */
  public void loadSkymap(SceneIOProvider ioContext, String fileName) {
    skymapFileName = fileName;
    skymap = loadSkyTexture(ioContext, fileName, skymap);
    scene.refresh();
  }

  /**
   * Set the sky equal to other sky.
   */
  public void set(Sky other) {
    skymapFileName = other.skymapFileName;
    skymap = other.skymap;
    yaw = other.yaw;
    pitch = other.pitch;
    roll = other.roll;
    rotation.set(other.rotation);
    mirrored = other.mirrored;
    skyEmittance = other.skyEmittance;
    gradient = new ArrayList<>(other.gradient);
    color.set(other.color);
    mode = other.mode;
    for (int i = 0; i < 6; ++i) {
      skybox[i] = other.skybox[i];
      skyboxFileName[i] = other.skyboxFileName[i];
    }

    simulatedSkyMode = other.simulatedSkyMode;
    skyCache.set(other.skyCache);
    skyCache.setSimulatedSkyMode(other.simulatedSkyMode);
    if (simulatedSkyMode.updateSun(scene.sun())) {
      skyCache.precalculateSky();
    }
    textureInterpolation = other.textureInterpolation;
  }

  /**
   * Calculate sky color for the ray, based on sky mode.
   */
  private void getSkyColorInner(Ray ray, IntersectionRecord intersectionRecord) {
    switch (mode) {
      case SOLID_COLOR: {
        intersectionRecord.color.set(color.x, color.y, color.z, 1);
        break;
      }
      case GRADIENT: {
        double angle = Math.asin(ray.d.y);
        int x = 0;
        if (gradient.size() > 1) {
          double pos = (angle + Constants.HALF_PI) / Math.PI;
          Vector4 c0 = gradient.get(x);
          Vector4 c1 = gradient.get(x + 1);
          double xx = (pos - c0.w) / (c1.w - c0.w);
          while (x + 2 < gradient.size() && xx > 1) {
            x += 1;
            c0 = gradient.get(x);
            c1 = gradient.get(x + 1);
            xx = (pos - c0.w) / (c1.w - c0.w);
          }
          xx = 0.5 * (Math.sin(Math.PI * xx - Constants.HALF_PI) + 1);
          double a = 1 - xx;
          double b = xx;
          intersectionRecord.color.set(a * c0.x + b * c1.x, a * c0.y + b * c1.y, a * c0.z + b * c1.z, 1);
        }
        break;
      }
      case SIMULATED: {
        Vector3 color = skyCache.calcIncidentLight(ray);
        intersectionRecord.color.set(color.x, color.y, color.z, 1);
        break;
      }
      case SKYMAP_EQUIRECTANGULAR: {
        double x = rotation.transformX(ray.d);
        double y = rotation.transformY(ray.d);
        double z = rotation.transformZ(ray.d);
        if (mirrored) {
          double theta = FastMath.atan2(z, x);
          theta /= Constants.TAU;
          if (theta > 1 || theta < 0) {
            theta = (theta % 1 + 1) % 1;
          }
          double phi = Math.abs(Math.asin(y)) / Constants.HALF_PI;
          skymap.getColor(theta, phi, intersectionRecord.color);
        } else {
          double theta = FastMath.atan2(z, x) / Constants.TAU;
          theta = (theta % 1 + 1) % 1;
          double phi = (Math.asin(y) + Constants.HALF_PI) / Math.PI;
          getSkymapColor(skymap, theta, phi, intersectionRecord.color);
        }
        break;
      }
      case SKYMAP_ANGULAR: {
        double x = rotation.transformX(ray.d);
        double y = rotation.transformY(ray.d);
        double z = rotation.transformZ(ray.d);
        double len = Math.sqrt(x * x + y * y);
        double theta = (len < Constants.EPSILON) ? 0 : Math.acos(-z) / (Constants.TAU * len);
        double u = theta * x + .5;
        double v = .5 + theta * y;
        getSkymapColor(skymap, u, v, intersectionRecord.color);
        break;
      }
      case SKYBOX: {
        double x = rotation.transformX(ray.d);
        double y = rotation.transformY(ray.d);
        double z = rotation.transformZ(ray.d);
        double xabs = QuickMath.abs(x);
        double yabs = QuickMath.abs(y);
        double zabs = QuickMath.abs(z);
        if (y > xabs && y > zabs) {
          double alpha = 1 / yabs;
          getSkymapColor(skybox[SKYBOX_UP], (1 + x * alpha) / 2.0, (1 + z * alpha) / 2.0, intersectionRecord.color);
        } else if (-z > xabs && -z > yabs) {
          double alpha = 1 / zabs;
          getSkymapColor(skybox[SKYBOX_FRONT], (1 + x * alpha) / 2.0, (1 + y * alpha) / 2.0, intersectionRecord.color);
        } else if (z > xabs && z > yabs) {
          double alpha = 1 / zabs;
          getSkymapColor(skybox[SKYBOX_BACK], (1 - x * alpha) / 2.0, (1 + y * alpha) / 2.0, intersectionRecord.color);
        } else if (-x > zabs && -x > yabs) {
          double alpha = 1 / xabs;
          getSkymapColor(skybox[SKYBOX_LEFT], (1 - z * alpha) / 2.0, (1 + y * alpha) / 2.0, intersectionRecord.color);
        } else if (x > zabs && x > yabs) {
          double alpha = 1 / xabs;
          getSkymapColor(skybox[SKYBOX_RIGHT], (1 + z * alpha) / 2.0, (1 + y * alpha) / 2.0, intersectionRecord.color);
        } else if (-y > xabs && -y > zabs) {
          double alpha = 1 / yabs;
          getSkymapColor(skybox[SKYBOX_DOWN], (1 + x * alpha) / 2.0, (1 - z * alpha) / 2.0, intersectionRecord.color);
        }
        break;
      }
    }
  }

  private void getSkymapColor(Texture texture, double u, double v, Vector4 color) {
    if (textureInterpolation) {
      texture.getColorInterpolated(u, v, color);
    } else {
      texture.getColor(u, v, color);
    }
  }

  /**
   * Panoramic skymap color.
   */
  public void getSkyColor(Ray ray, IntersectionRecord intersectionRecord, boolean addSun) {
    getSkyColorInner(ray, intersectionRecord);
    intersectionRecord.color.scale(skyEmittance);
    if (addSun) {
      intersectionRecord.color.add(scene.sun().getSunIntersectionColor(ray));
    }
    intersectionRecord.color.w = 1;
  }

  public void intersect(Ray ray, IntersectionRecord intersectionRecord) {
    if ((ray.isIndirect()) && scene.sun().intersect(ray, intersectionRecord)) {
      if ((ray.isDiffuse()) && scene.getSunSamplingStrategy().doSunSampling()) {
        return;
      }
      if ((ray.isSpecular()) && !scene.getSunSamplingStrategy().isDiffuseSun()) {
        return;
      }
    }
    getSkyColor(ray, intersectionRecord, true);
  }

  /**
   * Set the yaw rotation of the skymap.
   * @deprecated Use {@link #setYaw(double)} instead.
   */
  @Deprecated
  public void setRotation(double yaw) {
    this.setYaw(yaw);
  }

  /**
   * @return The yaw rotation of the skymap
   * @deprecated Use {@link #getYaw()} instead.
   */
  @Deprecated
  public double getRotation() {
    return getYaw();
  }

  public double getYaw() {
    return yaw;
  }

  public void setYaw(double yaw) {
    this.yaw = yaw;
    this.updateTransform();
    scene.refresh();
  }

  public double getPitch() {
    return pitch;
  }

  public void setPitch(double pitch) {
    this.pitch = pitch;
    this.updateTransform();
    scene.refresh();
  }

  public double getRoll() {
    return roll;
  }

  public void setRoll(double roll) {
    this.roll = roll;
    this.updateTransform();
    scene.refresh();
  }

  /**
   * Set sky mirroring at the horizon
   */
  public void setMirrored(boolean b) {
    if (b != mirrored) {
      mirrored = b;
      scene.refresh();
    }
  }

  /**
   * @return <code>true</code> if the sky is mirrored at the horizon
   */
  public boolean isMirrored() {
    return mirrored;
  }

  /**
   * Set the sky rendering mode.
   */
  public void setSkyMode(SkyMode newMode) {
    if (this.mode != newMode) {
      this.mode = newMode;
      if (newMode != SkyMode.SKYMAP_EQUIRECTANGULAR && newMode != SkyMode.SKYMAP_ANGULAR) {
        skymapFileName = "";
        skymap = Texture.EMPTY_TEXTURE;
      }
      if (newMode != SkyMode.SKYBOX) {
        for (int i = 0; i < 6; ++i) {
          skybox[i] = Texture.EMPTY_TEXTURE;
          skyboxFileName[i] = "";
        }
      }
      scene.refresh();
    }
  }

  /**
   * @return Current sky rendering mode
   */
  public SkyMode getSkyMode() {
    return mode;
  }

  /**
   * Set the simulated sky rendering mode.
   */
  public void setSimulatedSkyMode(int mode) {
    this.simulatedSkyMode = skies.get(mode);
    this.simulatedSkyMode.updateSun(scene.sun());
    skyCache.setSimulatedSkyMode(this.simulatedSkyMode);
    scene.refresh();
  }

  /**
   * @return Current simulated sky.
   */
  public SimulatedSky getSimulatedSky() {
    return simulatedSkyMode;
  }

  /**
   * Update the current simulated sky
   */
  public void updateSimulatedSky(Sun sun) {
    simulatedSkyMode.updateSun(sun);
    skyCache.precalculateSky();
  }

  /**
   * Set the simulated sky cache resolution
   */
  public void setSkyCacheResolution(int resolution) {
    skyCache.setSkyResolution(resolution);
  }

  @Override public JsonObject toJson() {
    JsonObject sky = new JsonObject();
    sky.add("skyYaw", yaw);
    sky.add("skyPitch", pitch);
    sky.add("skyRoll", roll);
    sky.add("skyMirrored", mirrored);
    sky.add("skyEmittance", skyEmittance);
    sky.add("mode", mode.name());

    // Always save gradient.
    sky.add("gradient", gradientJson(gradient));

    sky.add("color", JsonUtil.rgbToJson(color));
    sky.add("textureInterpolation", textureInterpolation);

    switch (mode) {
      case SKYMAP_EQUIRECTANGULAR:
      case SKYMAP_ANGULAR: {
        if (!skymap.isEmptyTexture()) {
          sky.add("skymap", skymapFileName);
        }
        break;
      }
      case SKYBOX: {
        JsonArray array = new JsonArray();
        for (int i = 0; i < 6; ++i) {
          if (!skybox[i].isEmptyTexture()) {
            array.add(skyboxFileName[i]);
          } else {
            array.add(Json.NULL);
          }
        }
        sky.add("skybox", array);
        break;
      }
      case SIMULATED: {
        sky.add("simulatedSky", simulatedSkyMode.getName());
        sky.add("simulatedSkySettings", simulatedSkyMode.toJson());
        sky.add("skyCacheResolution", skyCache.getSkyResolution());
        break;
      }
      default: {
        break;
      }
    }
    return sky;
  }

  public void importFromJson(JsonObject json) {
    yaw = json.get("skyYaw").doubleValue(yaw);
    pitch = json.get("skyPitch").doubleValue(pitch);
    roll = json.get("skyRoll").doubleValue(roll);
    updateTransform();
    mirrored = json.get("skyMirrored").boolValue(mirrored);
    skyEmittance = json.get("skyEmittance").doubleValue(skyEmittance);
    if (json.get("mode").stringValue(mode.name()).equals("BLACK")) {
      mode = SkyMode.SOLID_COLOR;
      color.x = 0;
      color.y = 0;
      color.z = 0;
    } else if (json.get("mode").stringValue(mode.name()).equals("SKYMAP_PANORAMIC")) {
      mode = SkyMode.SKYMAP_EQUIRECTANGULAR;
    } else if (json.get("mode").stringValue(mode.name()).equals("SKYMAP_SPHERICAL")) {
      mode = SkyMode.SKYMAP_ANGULAR;
    } else {
      mode = SkyMode.get(json.get("mode").stringValue(mode.name()));
    }

    if (json.get("gradient").isArray()) {
      List<Vector4> theGradient = gradientFromJson(json.get("gradient").array());
      if (theGradient != null && theGradient.size() >= 2) {
        gradient = theGradient;
      }
    }

    JsonUtil.rgbFromJson(json.get("color"), color);

    switch (mode) {
      case SKYMAP_EQUIRECTANGULAR:
      case SKYMAP_ANGULAR: {
        skymapFileName = json.get("skymap").stringValue(skymapFileName);
        if (skymapFileName.isEmpty()) {
          skymapFileName = json.get("skymapFileName").stringValue(skymapFileName);
        }
        break;
      }
      case SKYBOX: {
        JsonArray array = json.get("skybox").array();
        for (int i = 0; i < 6; ++i) {
          JsonValue value = array.get(i);
          skyboxFileName[i] = value.stringValue(skyboxFileName[i]);
        }
        break;
      }
      case SIMULATED: {
        String simSkyName = json.get("simulatedSky").asString(simulatedSkyMode.getName());
        Optional<SimulatedSky> match = skies.stream().filter(skyMode -> skyMode.getName().equals(simSkyName)).findAny();

        simulatedSkyMode = match.orElseGet(() -> simulatedSkyMode);
        simulatedSkyMode.fromJson(json.get("simulatedSkySettings").asObject());
        simulatedSkyMode.updateSun(scene.sun());
        skyCache.setSimulatedSkyMode(simulatedSkyMode);
        skyCache.setSkyResolution(json.get("skyCacheResolution").asInt(skyCache.getSkyResolution()));
        scene.refresh();
        break;
      }
      default:
        break;
    }
    textureInterpolation = json.get("textureInterpolation").boolValue(true);
  }

  private void updateTransform() {
    rotation.rotate(-pitch, -yaw, -roll);
  }

  public void setSkyEmittance(double newValue) {
    skyEmittance = newValue;
    scene.refresh();
  }

  public double getSkyEmittance() {
    return skyEmittance;
  }

  public void setGradient(List<Vector4> newGradient) {
    gradient = newGradient.stream().map(Vector4::new).collect(Collectors.toList());
    scene.refresh();
  }

  public List<Vector4> getGradient() {
    return gradient.stream().map(Vector4::new).collect(Collectors.toList());
  }

  public static JsonArray gradientJson(Collection<Vector4> gradient) {
    JsonArray array = new JsonArray();
    for (Vector4 stop : gradient) {
      JsonObject obj = new JsonObject();
      JsonObject colorObj = new JsonObject();
      colorObj.add("red", stop.x);
      colorObj.add("green", stop.y);
      colorObj.add("blue", stop.z);
      obj.add("color", JsonUtil.rgbToJson(stop.toVec3()));
      obj.add("pos", stop.w);
      array.add(obj);
    }
    return array;
  }

  /**
   * @return {@code null} if the gradient was not valid
   */
  public static List<Vector4> gradientFromJson(JsonArray array) {
    List<Vector4> gradient = new ArrayList<>(array.size());
    for (int i = 0; i < array.size(); ++i) {
      JsonObject obj = array.get(i).object();
      Vector3 color = new Vector3();
      try {
        if (obj.get("color").isUnknown()) {
          // support for old scene files (2.5.0 snapshot phase)
          ColorUtil.fromString(obj.get("rgb").stringValue(""), 16, color);
        } else  {
          JsonUtil.rgbFromJson(obj.get("color"), color);
        }
        Vector4 stop =
          new Vector4(color.x, color.y, color.z, obj.get("pos").doubleValue(Double.NaN));
        if (!Double.isNaN(stop.w)) {
          gradient.add(stop);
        }
      } catch (IllegalArgumentException e) {
        // Ignored.
      }
    }
    boolean errors = false;
    for (int i = 0; i < gradient.size(); ++i) {
      Vector4 stop = gradient.get(i);
      if (i == 0) {
        if (stop.w != 0) {
          errors = true;
          break;
        }
      } else if (i < gradient.size() - 1) {
        if (stop.w < gradient.get(i - 1).w) {
          errors = true;
          break;
        }
      } else {
        if (stop.w != 1) {
          errors = true;
          break;
        }
      }
    }
    if (errors) {
      // Error in gradient data.
      return null;
    } else {
      return gradient;
    }
  }

  public static void makeDefaultGradient(Collection<Vector4> gradient) {
    gradient.add(new Vector4(0x0B / 255., 0xAB / 255., 0xC7 / 255., 0));
    gradient.add(new Vector4(0x75 / 255., 0xAA / 255., 0xFF / 255., 1));
  }

  public void loadSkyboxTexture(SceneIOProvider ioContext, String fileName, int index) {
    if (index < 0 || index >= 6) {
      throw new IllegalArgumentException();
    }
    skyboxFileName[index] = fileName;
    skybox[index] = loadSkyTexture(ioContext, fileName, skybox[index]);
    scene.refresh();
  }

  private Texture loadSkyTexture(SceneIOProvider ioContext, String fileName, Texture prevTexture) {
    try {
      File textureFile = ioContext.resolveLinkedFile(fileName);
      Log.info("Loading skymap: " + fileName);
      if (fileName.toLowerCase().endsWith(".pfm")) {
        return new PFMTexture(textureFile);
      } else if (fileName.toLowerCase().endsWith(".hdr")) {
        return new HDRTexture(textureFile);
      } else {
        return new SkymapTexture(ImageLoader.read(textureFile));
      }
    } catch (Throwable e) {
      if (e instanceof IllegalArgumentException && e.getMessage().contains("Invalid scanline stride")) {
        Log.errorf("Failed to load skymap: %s\nImage too big. Image must contain less than 715,827,882 pixels.", fileName);
      } else {
        Log.error("Failed to load skymap: " + fileName, e);
        return prevTexture;
      }
      return prevTexture;
    }
  }

  public void setColor(Vector3 color) {
    this.color.set(color);
    scene.refresh();
  }

  public Vector3 getColor() {
    return color;
  }

  public boolean getTextureInterpolation() {
    return this.textureInterpolation;
  }

  public void setTextureInterpolation(boolean textureInterpolation) {
    this.textureInterpolation = textureInterpolation;
  }
}
