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
package se.llbit.chunky.renderer.scene;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.resources.HDRTexture;
import se.llbit.chunky.resources.PFMTexture;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Clouds;
import se.llbit.chunky.world.SkymapTexture;
import se.llbit.chunky.world.material.CloudMaterial;
import se.llbit.chunky.world.material.VolumeCloudMaterial;
import se.llbit.json.Json;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.math.*;
import se.llbit.resources.ImageLoader;
import se.llbit.util.JsonSerializable;
import se.llbit.util.JsonUtil;
import se.llbit.util.Pair;
import se.llbit.util.annotation.NotNull;
import se.llbit.util.annotation.Nullable;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Sky model and sky state for ray tracing.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Sky implements JsonSerializable {

  //private static final double CLOUD_OPACITY = 0.4;

  /**
   * Default sky light intensity
   */
  public static final double DEFAULT_INTENSITY = 1;

  /**
   * Minimum sky light intensity
   */
  public static final double MIN_INTENSITY = 0.0;

  /**
   * Maximum sky light intensity
   */
  public static final double MAX_INTENSITY = 50;

  /**
   * Minimum apparent sky light intensity
   */
  public static final double MIN_APPARENT_INTENSITY = 0.0;

  /**
   * Maximum apparent sky light intensity
   */
  public static final double MAX_APPARENT_INTENSITY = 50;

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
    SKYBOX("Skybox"),

    /** A completely black sky, useful for rendering an emitter-only pass. */
    BLACK("Black");

    private String name;

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
  private final Texture skybox[] =
      {Texture.EMPTY_TEXTURE, Texture.EMPTY_TEXTURE, Texture.EMPTY_TEXTURE, Texture.EMPTY_TEXTURE,
          Texture.EMPTY_TEXTURE, Texture.EMPTY_TEXTURE};
  private String skymapFileName = "";
  private final String skyboxFileName[] = {"", "", "", "", "", ""};
  private final Scene scene;

  private Matrix3 rotation = new Matrix3();
  private double yaw = 0, pitch = 0, roll = 0;

  private boolean mirrored = true;

  private ArrayList<CloudLayer> cloudLayers = new ArrayList<>(0);

  private double skyExposure = DEFAULT_INTENSITY;
  private double skyLightModifier = DEFAULT_INTENSITY;
  private double apparentSkyLightModifier = DEFAULT_INTENSITY;

  /** Color gradient used for the GRADIENT sky mode. */
  private List<Vector4> gradient = new LinkedList<>();

  /** Color used for the SOLID_COLOR sky mode. */
  private Vector3 color = new Vector3(0, 0, 0);

  /** Current sky rendering mode. */
  private SkyMode mode = SkyMode.DEFAULT;

  /** Simulated skies. */
  public final static List<SimulatedSky> skies = new ArrayList<>();

  static {
    skies.add(new PreethamSky());
    skies.add(new NishitaSky());
  }

  /** Simulated sky mode. */
  private SimulatedSky simulatedSkyMode = skies.get(0);
  double horizonOffset = 0;

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
  public void reloadSkymap(@Nullable File sceneDirectory) {
    switch (mode) {
      case SKYMAP_EQUIRECTANGULAR:
      case SKYMAP_ANGULAR:
        if (!skymapFileName.isEmpty()) {
          loadSkymap(skymapFileName, sceneDirectory);
        }
        break;
      case SKYBOX:
        for (int i = 0; i < 6; ++i) {
          if (!skyboxFileName[i].isEmpty()) {
            loadSkyboxTexture(skyboxFileName[i], i, sceneDirectory);
          }
        }
      default:
        break;
    }
  }

  /**
   * Load a panoramic skymap texture.
   */
  public void loadSkymap(String fileName, @Nullable File sceneDirectory) {
    skymapFileName = fileName;
    skymap = loadSkyTexture(fileName, skymap, sceneDirectory);
    scene.refresh();
  }

  /**
   * Set the sky equal to other sky.
   */
  public void set(Sky other) {
    cloudLayers = new ArrayList<>(other.cloudLayers);
    skymapFileName = other.skymapFileName;
    skymap = other.skymap;
    yaw = other.yaw;
    pitch = other.pitch;
    roll = other.roll;
    rotation.set(other.rotation);
    mirrored = other.mirrored;
    skyExposure = other.skyExposure;
    skyLightModifier = other.skyLightModifier;
    apparentSkyLightModifier = other.apparentSkyLightModifier;
    gradient = new ArrayList<>(other.gradient);
    color.set(other.color);
    mode = other.mode;
    horizonOffset = other.horizonOffset;
    for (int i = 0; i < 6; ++i) {
      skybox[i] = other.skybox[i];
      skyboxFileName[i] = other.skyboxFileName[i];
    }

    simulatedSkyMode = other.simulatedSkyMode;
    skyCache.set(other.skyCache);
    skyCache.setSimulatedSkyMode(other.simulatedSkyMode);
    if (simulatedSkyMode.updateSun(scene.sun, horizonOffset)) {
      skyCache.precalculateSky();
    }
  }

  /**
   * Calculate sky color for the ray, based on sky mode.
   */
  public void getSkyDiffuseColorInner(Ray ray) {
    switch (mode) {
      case SOLID_COLOR: {
        ray.color.set(color.x, color.y, color.z, 1);
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
          ray.color.set(a * c0.x + b * c1.x, a * c0.y + b * c1.y, a * c0.z + b * c1.z, 1);
        }
        break;
      }
      case SIMULATED: {
        Vector3 color = skyCache.calcIncidentLight(ray);
        ray.color.set(color.x, color.y, color.z, 1);
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
          skymap.getColor(theta, phi, ray.color);
        } else {
          double theta = FastMath.atan2(z, x) / Constants.TAU;
          theta = (theta % 1 + 1) % 1;
          double phi = (Math.asin(y) + Constants.HALF_PI) / Math.PI;
          skymap.getColor(theta, phi, ray.color);
        }
        break;
      }
      case SKYMAP_ANGULAR: {
        double x = rotation.transformX(ray.d);
        double y = rotation.transformY(ray.d);
        double z = rotation.transformZ(ray.d);
        double len = Math.sqrt(x * x + y * y);
        double theta = (len < Ray.EPSILON) ? 0 : Math.acos(-z) / (Constants.TAU * len);
        double u = theta * x + .5;
        double v = .5 + theta * y;
        skymap.getColor(u, v, ray.color);
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
          skybox[SKYBOX_UP].getColor((1 + x * alpha) / 2.0, (1 + z * alpha) / 2.0, ray.color);
        } else if (-z > xabs && -z > yabs) {
          double alpha = 1 / zabs;
          skybox[SKYBOX_FRONT].getColor((1 + x * alpha) / 2.0, (1 + y * alpha) / 2.0, ray.color);
        } else if (z > xabs && z > yabs) {
          double alpha = 1 / zabs;
          skybox[SKYBOX_BACK].getColor((1 - x * alpha) / 2.0, (1 + y * alpha) / 2.0, ray.color);
        } else if (-x > zabs && -x > yabs) {
          double alpha = 1 / xabs;
          skybox[SKYBOX_LEFT].getColor((1 - z * alpha) / 2.0, (1 + y * alpha) / 2.0, ray.color);
        } else if (x > zabs && x > yabs) {
          double alpha = 1 / xabs;
          skybox[SKYBOX_RIGHT].getColor((1 + z * alpha) / 2.0, (1 + y * alpha) / 2.0, ray.color);
        } else if (-y > xabs && -y > zabs) {
          double alpha = 1 / yabs;
          skybox[SKYBOX_DOWN].getColor((1 + x * alpha) / 2.0, (1 - z * alpha) / 2.0, ray.color);
        }
        break;
      }
      case BLACK: {
        ray.color.set(0, 0, 0, 1);
        break;
      }
    }
  }

  /**
   * Panoramic skymap color.
   */
  public void getSkyColor(Ray ray, boolean drawSun) {
    getSkyDiffuseColorInner(ray);
    ray.color.scale(skyExposure);
    ray.color.scale(skyLightModifier);
    if (drawSun) addSunColor(ray);
    ray.color.w = 1;
  }

  public void getApparentSkyColor(Ray ray, boolean drawSun) {
    getSkyDiffuseColorInner(ray);
    ray.color.scale(skyExposure);
    ray.color.scale(apparentSkyLightModifier);
    if (drawSun) addSunColor(ray);
    ray.color.w = 1;
  }

  /**
   * Bilinear interpolated panoramic skymap color.
   */
  public void getSkyColorInterpolated(Ray ray) {
    switch (mode) {
      case SKYMAP_EQUIRECTANGULAR: {
        double x = rotation.transformX(ray.d);
        double y = rotation.transformY(ray.d);
        double z = rotation.transformZ(ray.d);
        if (mirrored) {
          double theta = FastMath.atan2(z, x) / Constants.TAU;
          theta = (theta % 1 + 1) % 1;
          double phi = Math.abs(Math.asin(y)) / Constants.HALF_PI;
          skymap.getColorInterpolated(theta, phi, ray.color);
        } else {
          double theta = FastMath.atan2(z, x) / Constants.TAU;
          if (theta > 1 || theta < 0) {
            theta = (theta % 1 + 1) % 1;
          }
          double phi = (Math.asin(y) + Constants.HALF_PI) / Math.PI;
          skymap.getColorInterpolated(theta, phi, ray.color);
        }
        break;
      }
      case SKYMAP_ANGULAR: {
        double x = rotation.transformX(ray.d);
        double y = rotation.transformY(ray.d);
        double z = rotation.transformZ(ray.d);
        double len = Math.sqrt(x * x + y * y);
        double theta = (len < Ray.EPSILON) ? 0 : Math.acos(-z) / (Constants.TAU * len);
        double u = theta * x + .5;
        double v = .5 + theta * y;
        skymap.getColorInterpolated(u, v, ray.color);
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
          skybox[SKYBOX_UP]
              .getColorInterpolated((1 + x * alpha) / 2.0, (1 + z * alpha) / 2.0, ray.color);
        } else if (-z > xabs && -z > yabs) {
          double alpha = 1 / zabs;
          skybox[SKYBOX_FRONT]
              .getColorInterpolated((1 + x * alpha) / 2.0, (1 + y * alpha) / 2.0, ray.color);
        } else if (z > xabs && z > yabs) {
          double alpha = 1 / zabs;
          skybox[SKYBOX_BACK]
              .getColorInterpolated((1 - x * alpha) / 2.0, (1 + y * alpha) / 2.0, ray.color);
        } else if (-x > zabs && -x > yabs) {
          double alpha = 1 / xabs;
          skybox[SKYBOX_LEFT]
              .getColorInterpolated((1 - z * alpha) / 2.0, (1 + y * alpha) / 2.0, ray.color);
        } else if (x > zabs && x > yabs) {
          double alpha = 1 / xabs;
          skybox[SKYBOX_RIGHT]
              .getColorInterpolated((1 + z * alpha) / 2.0, (1 + y * alpha) / 2.0, ray.color);
        } else if (-y > xabs && -y > zabs) {
          double alpha = 1 / yabs;
          skybox[SKYBOX_DOWN]
              .getColorInterpolated((1 + x * alpha) / 2.0, (1 - z * alpha) / 2.0, ray.color);
        }
        break;
      }
      default: {
        getSkyDiffuseColorInner(ray);
      }
    }
    ray.color.scale(skyExposure);
    ray.color.scale(apparentSkyLightModifier);
    addSunColor(ray);
    ray.color.w = 1;
  }

  /**
   * Add sun color contribution. This does not alpha blend the sun color
   * because the Minecraft sun texture has no alpha channel.
   */
  private void addSunColor(Ray ray) {
    double r = ray.color.x;
    double g = ray.color.y;
    double b = ray.color.z;
    if (scene.sun().intersect(ray)) {

      // Blend sun color with current color.
      ray.color.x = ray.color.x + r;
      ray.color.y = ray.color.y + g;
      ray.color.z = ray.color.z + b;
    }
  }

  public void getSkyColorDiffuseSun(Ray ray, boolean diffuseSun) {
    getSkyDiffuseColorInner(ray);
    ray.color.scale(skyExposure);
    ray.color.scale(skyLightModifier);
    if (diffuseSun) addSunColorDiffuseSun(ray);
    ray.color.w = 1;
  }

  public void addSunColorDiffuseSun(Ray ray) {
    double r = ray.color.x;
    double g = ray.color.y;
    double b = ray.color.z;

    if (scene.sun().intersectDiffuse(ray)) {
      double mult = scene.sun().getLuminosity();

      // Blend sun color with current color.
      ray.color.x = ray.color.x * mult + r;
      ray.color.y = ray.color.y * mult + g;
      ray.color.z = ray.color.z * mult + b;
    }
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
    this.simulatedSkyMode.updateSun(scene.sun, horizonOffset);
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
    if (simulatedSkyMode.updateSun(sun, horizonOffset)) {
      skyCache.precalculateSky();
    }
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
    sky.add("skyExposure", skyExposure);
    sky.add("skyLight", skyLightModifier);
    sky.add("apparentSkyLight", apparentSkyLightModifier);
    sky.add("mode", mode.name());
    sky.add("horizonOffset", horizonOffset);
    JsonArray cloudLayersJson = new JsonArray();
    for (CloudLayer layer : cloudLayers) {
      cloudLayersJson.add(layer.toJson());
    }
    sky.add("cloudLayers", cloudLayersJson);

    // Always save gradient.
    sky.add("gradient", gradientJson(gradient));

    sky.add("color", ColorUtil.rgbToJson(color));

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
    skyExposure = json.get("skyExposure").doubleValue(skyExposure);
    skyLightModifier = json.get("skyLight").doubleValue(skyLightModifier);
    apparentSkyLightModifier = json.get("apparentSkyLight").doubleValue(apparentSkyLightModifier);
    if (!(json.get("mode").stringValue(mode.name()).equals("SKYMAP_PANORAMIC") || json.get("mode").stringValue(mode.name()).equals("SKYMAP_SPHERICAL"))) {
      mode = SkyMode.get(json.get("mode").stringValue(mode.name()));
    } else if (json.get("mode").stringValue(mode.name()).equals("SKYMAP_PANORAMIC")) {
      mode = SkyMode.SKYMAP_EQUIRECTANGULAR;
    } else if (json.get("mode").stringValue(mode.name()).equals("SKYMAP_SPHERICAL")) {
      mode = SkyMode.SKYMAP_ANGULAR;
    }
    horizonOffset = json.get("horizonOffset").doubleValue(horizonOffset);

    if (json.get("cloudLayers").isUnknown()) {
      boolean cloudsEnabled = json.get("cloudsEnabled").boolValue(false);
      if (cloudsEnabled) {
        double cloudSize = json.get("cloudSize").doubleValue(12);
        Vector3 cloudOffset = JsonUtil.vec3FromJsonObject(json.get("cloudOffset").asObject());
        CloudLayer cloudLayer = new CloudLayer();
        cloudLayer.setCloudSizeX(cloudSize);
        cloudLayer.setCloudSizeY(5);
        cloudLayer.setCloudSizeZ(cloudSize);
        cloudLayer.setCloudXOffset(cloudOffset.x);
        cloudLayer.setCloudYOffset(cloudOffset.y);
        cloudLayer.setCloudZOffset(cloudOffset.z);
        cloudLayers.add(cloudLayer);
      }
    } else {
      JsonArray jsonCloudLayers = json.get("cloudLayers").asArray();
      cloudLayers.clear();
      for (JsonValue layer : jsonCloudLayers) {
        JsonObject layerObject = layer.asObject();
        CloudLayer cloudLayer = new CloudLayer();
        cloudLayer.importFromJson(layerObject);
        cloudLayers.add(cloudLayer);
      }
    }

    if (json.get("gradient").isArray()) {
      List<Vector4> theGradient = gradientFromJson(json.get("gradient").array());
      if (theGradient != null && theGradient.size() >= 2) {
        gradient = theGradient;
      }
    }

    if (json.get("color").isObject()) {
      color.set(ColorUtil.jsonToRGB(json.get("color").asObject()));
    } else {
      // Maintain backwards-compatibility with scenes saved in older Chunky versions
      color.set(JsonUtil.vec3FromJsonArray(json.get("color")));
    }

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
        skyCache.setSkyResolution(json.get("skyCacheResolution").asInt(skyCache.getSkyResolution()));

        String simSkyName = json.get("simulatedSky").asString(simulatedSkyMode.getName());
        Optional<SimulatedSky> match = skies.stream().filter(skyMode -> skyMode.getName().equals(simSkyName)).findAny();

        simulatedSkyMode = match.orElseGet(() -> simulatedSkyMode);
        simulatedSkyMode.updateSun(scene.sun(), horizonOffset);
        skyCache.setSimulatedSkyMode(simulatedSkyMode);
        skyCache.precalculateSky();
        scene.refresh();
        break;
      }
      default:
        break;
    }
  }

  private void updateTransform() {
    rotation.rotate(-pitch, -yaw, -roll);
  }

  public void setSkyExposure(double newValue) {
    skyExposure = newValue;
    scene.refresh();
  }

  /**
   * Set the sky light modifier.
   */
  public void setSkyLight(double newValue) {
    skyLightModifier = newValue;
    scene.refresh();
  }

  public void setApparentSkyLight(double newValue) {
    apparentSkyLightModifier = newValue;
    scene.refresh();
  }

  public double getSkyExposure() {
    return skyExposure;
  }

  /**
   * @return Current sky light modifier
   */
  public double getSkyLight() {
    return skyLightModifier;
  }

  public double getApparentSkyLight() {
    return apparentSkyLightModifier;
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
      obj.add("rgb", ColorUtil.toString(stop.x, stop.y, stop.z));
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
        ColorUtil.fromString(obj.get("rgb").stringValue(""), 16, color);
        Vector4 stop =
            new Vector4(color.x, color.y, color.z, obj.get("pos").doubleValue(Double.NaN));
        if (!Double.isNaN(stop.w)) {
          gradient.add(stop);
        }
      } catch (NumberFormatException e) {
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

  public void loadSkyboxTexture(String fileName, int index, @Nullable File sceneDirectory) {
    if (index < 0 || index >= 6) {
      throw new IllegalArgumentException();
    }
    skyboxFileName[index] = fileName;
    skybox[index] = loadSkyTexture(fileName, skybox[index], sceneDirectory);
    scene.refresh();
  }

  private Texture loadSkyTexture(String fileName, Texture prevTexture, @Nullable File sceneDirectory) {
    String resolvedFilename = sceneDirectory == null
      ? fileName
      : Paths.get(sceneDirectory.getAbsolutePath()).resolve(fileName).toAbsolutePath().toString();
    File textureFile = new File(resolvedFilename);
    if (textureFile.exists()) {
      try {
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
        }
        return prevTexture;
      }
    } else {
      Log.errorf("Failed to load skymap: %s (file does not exist)", fileName);
      return prevTexture;
    }
  }

  public void setHorizonOffset(double newValue) {
    newValue = Math.min(1, Math.max(0, newValue));
    if (newValue != horizonOffset) {
      horizonOffset = newValue;
      scene.refresh();
    }
  }

  public double getHorizonOffset() {
    return horizonOffset;
  }

  public void setColor(Vector3 color) {
    this.color.set(color);
    scene.refresh();
  }

  public Vector3 getColor() {
    return color;
  }

  public Vector3 getCloudLayerColor(int index) {
    return cloudLayers.get(index).getCloudColor();
  }

  public void setCloudLayerColor(int index, Vector3 color) {
    cloudLayers.get(index).setCloudColor(color);
    scene.refresh();
  }


  public boolean getCloudLayerVolumetricClouds(int index) {
    return cloudLayers.get(index).getVolumetricClouds();
  }

  public void setCloudLayerVolumetricClouds(int index, boolean value) {
    cloudLayers.get(index).setVolumetricClouds(value);
    scene.refresh();
  }


  public double getCloudLayerDensity(int index) {
    return cloudLayers.get(index).getCloudDensity();
  }

  public void setCloudLayerDensity(int index, double value) {
    cloudLayers.get(index).setCloudDensity(value);
    scene.refresh();
  }


  public double getCloudLayerSizeX(int index) {
    return cloudLayers.get(index).getCloudSizeX();
  }

  public void setCloudLayerSizeX(int index, double newValue) {
    cloudLayers.get(index).setCloudSizeX(newValue);
    scene.refresh();
  }


  public double getCloudLayerSizeY(int index) {
    return cloudLayers.get(index).getCloudSizeY();
  }

  public void setCloudLayerSizeY(int index, double newValue) {
    cloudLayers.get(index).setCloudSizeY(newValue);
    scene.refresh();
  }


  public double getCloudLayerSizeZ(int index) {
    return cloudLayers.get(index).getCloudSizeZ();
  }

  public void setCloudLayerSizeZ(int index, double newValue) {
    cloudLayers.get(index).setCloudSizeZ(newValue);
    scene.refresh();
  }


  public double getCloudLayerXOffset(int index) {
    return cloudLayers.get(index).getCloudXOffset();
  }

  public void setCloudLayerXOffset(int index, double newValue) {
    cloudLayers.get(index).setCloudXOffset(newValue);
    scene.refresh();
  }


  /**
   * @return The current cloud height
   */
  public double getCloudLayerYOffset(int index) {
    return cloudLayers.get(index).getCloudYOffset();
  }

  /**
   * Change the cloud height
   */
  public void setCloudLayerYOffset(int index, double newValue) {
    cloudLayers.get(index).setCloudYOffset(newValue);
    scene.refresh();
  }


  public double getCloudLayerZOffset(int index) {
    return cloudLayers.get(index).getCloudZOffset();
  }

  public void setCloudLayerZOffset(int index, double newValue) {
    cloudLayers.get(index).setCloudZOffset(newValue);
    scene.refresh();
  }

  public float getCloudLayerEmittance(int index) {
    return cloudLayers.get(index).getEmittance();
  }

  public void setCloudLayerEmittance(int index, float value) {
    cloudLayers.get(index).setEmittance(value);
    scene.refresh();
  }

  public float getCloudLayerSpecular(int index) {
    return cloudLayers.get(index).getSpecular();
  }

  public void setCloudLayerSpecular(int index, float value) {
    cloudLayers.get(index).setSpecular(value);
    scene.refresh();
  }

  public float getCloudLayerSmoothness(int index) {
    return cloudLayers.get(index).getSmoothness();
  }

  public void setCloudLayerSmoothness(int index, float value) {
    cloudLayers.get(index).setSmoothness(value);
    scene.refresh();
  }

  public float getCloudLayerIor(int index) {
    return cloudLayers.get(index).getIor();
  }

  public void setCloudLayerIor(int index, float value) {
    cloudLayers.get(index).setIor(value);
    scene.refresh();
  }

  public float getCloudLayerMetalness(int index) {
    return cloudLayers.get(index).getMetalness();
  }

  public void setCloudLayerMetalness(int index, float value) {
    cloudLayers.get(index).setMetalness(value);
    scene.refresh();
  }

  public float getCloudLayerAnisotropy(int index) {
    return cloudLayers.get(index).getAnisotropy();
  }

  public void setCloudLayerAnisotropy(int index, float value) {
    cloudLayers.get(index).setAnisotropy(value);
    scene.refresh();
  }

  public void addCloudLayer() {
    cloudLayers.add(new CloudLayer());
    scene.refresh();
  }

  public void removeCloudLayer(int index) {
    cloudLayers.remove(index);
    scene.refresh();
  }

  public int getNumCloudLayers() {
    return cloudLayers.size();
  }

  public boolean cloudIntersection(Scene scene, Ray ray, Random random) {
    boolean hit = false;
    for (CloudLayer layer : cloudLayers) {
      hit |= layer.intersect(scene, ray, random);
    }
    return hit;
  }
}
