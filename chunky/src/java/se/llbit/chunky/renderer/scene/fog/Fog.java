package se.llbit.chunky.renderer.scene.fog;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Constants;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray2;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.util.JsonSerializable;

public final class Fog implements JsonSerializable {
  private final Scene scene;
  private FogMode mode = FogMode.NONE;
  private boolean fastFog = true;
  private double uniformDensity = Scene.DEFAULT_FOG_DENSITY;
  private double skyFogDensity = 1;
  private ArrayList<FogLayer> layers = new ArrayList<>(0);
  private Vector3 fogColor = new Vector3(PersistentSettings.getFogColorRed(), PersistentSettings.getFogColorGreen(), PersistentSettings.getFogColorBlue());

  private static final double EXTINCTION_FACTOR = 0.04;
  public static final double FOG_LIMIT = 30000;

  public Fog(Scene scene) {
    this.scene = scene;
  }

  public boolean isFogEnabled() {
    return mode != FogMode.NONE;
  }

  public Vector3 getFogColor() {
    return fogColor;
  }

  public double getUniformDensity() {
    return uniformDensity;
  }

  public void setUniformDensity(double value) {
    this.uniformDensity = value;
  }

  public double getSkyFogDensity() {
    return skyFogDensity;
  }

  public void setSkyFogDensity(double value) {
    this.skyFogDensity = value;
  }

  public FogMode getFogMode() {
    return mode;
  }

  public void setFogMode(FogMode mode) {
    this.mode = mode;
  }

  public boolean isFastFog() {
    return fastFog;
  }

  public void setFastFog(boolean value) {
    this.fastFog = value;
  }

  public ArrayList<FogLayer> getFogLayers() {
    return layers;
  }

  public void addLayer() {
    layers.add(new FogLayer(scene));
    scene.refresh();
  }

  public void removeLayer(int index) {
    layers.remove(index);
    scene.refresh();
  }

  public void setY(int index, double value) {
    layers.get(index).setY(value);
    scene.refresh();
  }

  public void setBreadth(int index, double value) {
    layers.get(index).setBreadth(value);
    scene.refresh();
  }

  public void setDensity(int index, double value) {
    layers.get(index).setDensity(value);
    scene.refresh();
  }

  private static double clampDy(double dy) {
    // This method prevents numerical errors or division by 0 when dy is close to 0.
    final double epsilon = 0.00001;
    if (dy > 0) {
      if (dy < epsilon) {
        return epsilon;
      }
    } else if (dy > -epsilon) {
      return -epsilon;
    }
    return dy;
  }

  public void addSkyFog(Ray2 ray, IntersectionRecord intersectionRecord, Vector4 scatterLight) {
    if (mode == FogMode.UNIFORM) {
      if (uniformDensity > 0.0) {
        double fog;
        if (ray.d.y > 0) {
          fog = 1 - ray.d.y;
          fog *= fog;
        } else {
          fog = 1;
        }
        fog *= skyFogDensity;
        intersectionRecord.color.x = (1 - fog) * intersectionRecord.color.x + fog * fogColor.x;
        intersectionRecord.color.y = (1 - fog) * intersectionRecord.color.y + fog * fogColor.y;
        intersectionRecord.color.z = (1 - fog) * intersectionRecord.color.z + fog * fogColor.z;
      }
    } else if (mode == FogMode.LAYERED) {
      double dy = ray.d.y;
      double y1 = ray.o.y;
      double y2 = y1 + dy * FOG_LIMIT;
      addLayeredFog(intersectionRecord.color, intersectionRecord.color, null, dy, y1, y2, scatterLight);
    }
  }

  public void addGroundFog(Ray2 ray, Vector4 fogColor1, Vector4 color, Vector3 emittance, Vector3 ox, Vector3 od, double airDistance, Vector4 scatterLight, double scatterOffset) {
    if (mode == FogMode.UNIFORM) {
      double fogDensity = uniformDensity * EXTINCTION_FACTOR;
      double extinction = Math.exp(-airDistance * fogDensity);
      color.scale(extinction);
      if (emittance != null) {
        emittance.scale(extinction);
      }
      if (scatterLight.w > Constants.EPSILON) {
        double inscatter = scatterLight.w;
        if (fastFog) {
          inscatter *= (1 - extinction);
        } else {
          inscatter *= airDistance * fogDensity * Math.exp(-scatterOffset * fogDensity);
        }
        fogColor1.x += scatterLight.x * fogColor.x * inscatter;
        fogColor1.y += scatterLight.y * fogColor.y * inscatter;
        fogColor1.z += scatterLight.z * fogColor.z * inscatter;
      }
    } else if (mode == FogMode.LAYERED) {
      addLayeredFog(fogColor1, color, emittance, od.y, ox.y, ray.o.y, scatterLight);
    }
  }

  public void addLayeredFog(Vector4 fogColor1, Vector4 color, Vector3 emittance, double dy, double y1, double y2, Vector4 scatterLight) {
    double total = 0;
    for (FogLayer layer : layers) {
      // Logistic distribution CDF. It is the integral of the PDF, which is a nice bell shaped sigmoid function
      // used to express the fog density at a given y coordinate.
      total += layer.density * (1 / (1 + Math.exp((layer.yWithOrigin - y1) * layer.breadthInv)) - 1 / (1 + Math.exp((layer.yWithOrigin - y2) * layer.breadthInv)));
    }
    double extinction = Math.exp(total / clampDy(dy));
    color.scale(extinction);
    if (emittance != null) {
      emittance.scale(extinction);
    }
    if (scatterLight.w > Constants.EPSILON) {
      double inscatter = (1 - extinction) * scatterLight.w;
      fogColor1.x += inscatter * scatterLight.x * fogColor.x;
      fogColor1.y += inscatter * scatterLight.y * fogColor.y;
      fogColor1.z += inscatter * scatterLight.z * fogColor.z;
    }
  }

  public double sampleGroundScatterOffset(Ray2 ray, double airDistance, Vector3 ox, Vector3 od, Random random) {
    if (mode == FogMode.UNIFORM) {
      return QuickMath.clamp(airDistance * random.nextFloat(), Constants.EPSILON, airDistance - Constants.EPSILON);
    } else if (mode == FogMode.LAYERED) {
      double dy = od.y;
      double y1 = ox.y;
      double y2 = ray.o.y;
      return sampleLayeredScatterOffset(random, y1, y2, dy);
    } else {
      throw new IllegalStateException("Tried to sample ground fog scatter offset even though fog is off.");
    }
  }

  public double sampleSkyScatterOffset(Scene scene, Ray2 ray, Random random) {
    if (mode == FogMode.LAYERED) {
      double dy = ray.d.y;
      double y1 = ray.o.y;
      double y2 = dy > 0 ? scene.getYMax() : scene.getYMin();
      return sampleLayeredScatterOffset(random, y1, y2, dy);
    } else {
      throw new IllegalStateException("Tried to sample sky fog scatter offset even though fog is not layered.");
    }
  }

  private double sampleLayeredScatterOffset(Random random, double y1, double y2, double dy) {
    if (layers.isEmpty()) {
      return Constants.EPSILON;
    }
    // This works only for one fog layer yet.
    FogLayer layer = layers.get(0);
    // Logistic distribution CDF.
    double y1v = 1 / (1 + Math.exp((layer.yWithOrigin - y1) * layer.breadthInv));
    double y2v = 1 / (1 + Math.exp((layer.yWithOrigin - y2) * layer.breadthInv));
    double middle = y1v + random.nextDouble() * (y2v - y1v);
    // Logistic distribution Quantile function.
    double offsetY = Math.log(middle / (1 - middle)) * layer.breadth + layer.yWithOrigin;
    return (offsetY - y1) / clampDy(dy);
  }

  @Override public JsonObject toJson() {
    JsonObject fogObj = new JsonObject();
    fogObj.add("mode", mode.name());
    fogObj.add("uniformDensity", uniformDensity);
    fogObj.add("skyFogDensity", skyFogDensity);
    JsonArray jsonLayers = new JsonArray();
    for (FogLayer layer : layers) {
      JsonObject jsonLayer = new JsonObject();
      jsonLayer.add("y", layer.y);
      jsonLayer.add("breadth", layer.breadth);
      jsonLayer.add("density", layer.density);
      jsonLayers.add(jsonLayer);
    }
    fogObj.add("layers", jsonLayers);
    JsonObject colorObj = new JsonObject();
    colorObj.add("red", fogColor.x);
    colorObj.add("green", fogColor.y);
    colorObj.add("blue", fogColor.z);
    fogObj.add("color", colorObj);
    fogObj.add("fastFog", fastFog);
    return fogObj;
  }

  public void importFromJson(JsonObject json, Scene scene) {
    mode = FogMode.valueOf(json.get("mode").stringValue(mode.getId()));
    uniformDensity = json.get("uniformDensity").doubleValue(uniformDensity);
    skyFogDensity = json.get("skyFogDensity").doubleValue(skyFogDensity);
    layers = json.get("layers").array().elements.stream().map(JsonValue::object).map(o -> new FogLayer(
        o.get("y").doubleValue(0),
        o.get("breadth").doubleValue(0),
        o.get("density").doubleValue(0),
        scene)).collect(Collectors.toCollection(ArrayList<FogLayer>::new));
    JsonObject colorObj = json.get("color").object();
    fogColor.x = colorObj.get("red").doubleValue(fogColor.x);
    fogColor.y = colorObj.get("green").doubleValue(fogColor.y);
    fogColor.z = colorObj.get("blue").doubleValue(fogColor.z);
    fastFog = json.get("fastFog").boolValue(fastFog);
  }

  public void importFromLegacy(JsonObject json) {
    mode = json.get("fogEnabled").boolValue(mode != FogMode.NONE) ? FogMode.NONE : FogMode.UNIFORM;
    uniformDensity = json.get("fogDensity").doubleValue(uniformDensity);
    skyFogDensity = json.get("skyFogDensity").doubleValue(skyFogDensity);
    layers = new ArrayList<>(0);
    JsonObject colorObj = json.get("fogColor").object();
    fogColor.x = colorObj.get("red").doubleValue(fogColor.x);
    fogColor.y = colorObj.get("green").doubleValue(fogColor.y);
    fogColor.z = colorObj.get("blue").doubleValue(fogColor.z);
    fastFog = json.get("fastFog").boolValue(fastFog);
  }

  public void set(Fog other) {
    mode = other.mode;
    uniformDensity = other.uniformDensity;
    skyFogDensity = other.skyFogDensity;
    layers = new ArrayList<>(other.layers);
    fogColor.set(other.fogColor);
  }
}
