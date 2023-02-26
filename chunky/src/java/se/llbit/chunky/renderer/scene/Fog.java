package se.llbit.chunky.renderer.scene;

import java.util.Arrays;
import java.util.Random;

import se.llbit.chunky.PersistentSettings;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public final class Fog {
  protected FogMode mode = FogMode.UNIFORM;
  protected boolean fastFog = true;
  protected double uniformDensity = Scene.DEFAULT_FOG_DENSITY;
  protected double skyFogDensity = 1;
  protected FogLayer[] layers = new FogLayer[0];
  protected Vector3 fogColor = new Vector3(PersistentSettings.getFogColorRed(), PersistentSettings.getFogColorGreen(), PersistentSettings.getFogColorBlue());

  private static final double EXTINCTION_FACTOR = 0.04;
  public static final double FOG_LIMIT = 30000;
  public static final Vector4 SKY_SCATTER = new Vector4(1, 1, 1, 1);

  public boolean fogEnabled() {
    return mode != FogMode.NONE;
  }

  public Vector3 getFogColor() {
    return fogColor;
  }

  public double getUniformDensity() {
    return uniformDensity;
  }

  public double getSkyFogDensity() {
    return skyFogDensity;
  }

  public boolean fastFog() {
    return fastFog;
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

  public void addSkyFog(Ray ray, Vector4 scatterLight) {
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
        ray.color.x = (1 - fog) * ray.color.x + fog * fogColor.x;
        ray.color.y = (1 - fog) * ray.color.y + fog * fogColor.y;
        ray.color.z = (1 - fog) * ray.color.z + fog * fogColor.z;
      }
    } else if (mode == FogMode.LAYERED) {
      double dy = ray.d.y;
      double y1 = ray.o.y;
      double y2 = y1 + dy * FOG_LIMIT;
      addLayeredFog(ray.color, dy, y1, y2, scatterLight);
    }
  }

  public void addGroundFog(Ray ray, Vector3 ox, double airDistance, Vector4 scatterLight, double scatterOffset) {
    Vector4 color = ray.color;
    if (mode == FogMode.UNIFORM) {
      double fogDensity = uniformDensity * EXTINCTION_FACTOR;
      double extinction = Math.exp(-airDistance * fogDensity);
      color.scale(extinction);
      if (scatterLight.w > Ray.EPSILON) {
        double inscatter = scatterLight.w;
        if (fastFog) {
          inscatter *= (1 - extinction);
        } else {
          inscatter *= airDistance * fogDensity * Math.exp(-scatterOffset * fogDensity);
        }
        color.x += scatterLight.x * fogColor.x * inscatter;
        color.y += scatterLight.y * fogColor.y * inscatter;
        color.z += scatterLight.z * fogColor.z * inscatter;
      }
    } else if (mode == FogMode.LAYERED) {
      addLayeredFog(color, ray.d.y, ox.y, ray.o.y, scatterLight);
    }
  }

  public void addLayeredFog(Vector4 color, double dy, double y1, double y2, Vector4 scatterLight) {
    double total = 0;
    for (FogLayer layer : layers) {
      // Logistic distribution CDF. It is the integral of the PDF, which is a nice bell shaped sigmoid function
      // used to express the fog density at a given y coordinate.
      total += layer.density * (1 / (1 + Math.exp((layer.yWithOrigin - y1) * layer.breadthInv)) - 1 / (1 + Math.exp((layer.yWithOrigin - y2) * layer.breadthInv)));
    }
    double extinction = Math.exp(total / clampDy(dy));
    color.scale(extinction);
    if (scatterLight.w > Ray.EPSILON) {
      double inscatter = (1 - extinction) * scatterLight.w;
      color.x += inscatter * scatterLight.x * fogColor.x;
      color.y += inscatter * scatterLight.y * fogColor.y;
      color.z += inscatter * scatterLight.z * fogColor.z;
    }
  }

  public double sampleGroundScatterOffset(Ray ray, Vector3 ox, Random random) {
    double airDistance = ray.distance;
    if (mode == FogMode.UNIFORM) {
      return QuickMath.clamp(airDistance * random.nextFloat(), Ray.EPSILON, airDistance - Ray.EPSILON);
    } else if (mode == FogMode.LAYERED) {
      double dy = ray.d.y;
      double y1 = ox.y;
      double y2 = ray.o.y;
      return sampleLayeredScatterOffset(random, y1, y2, dy);
    } else {
      throw new IllegalStateException("Tried to sample ground fog scatter offset even though fog is off.");
    }
  }

  public double sampleSkyScatterOffset(Scene scene, Ray ray, Random random) {
    if (mode == FogMode.LAYERED) {
      double dy = ray.d.y;
      double y1 = ray.o.y;
      double y2 = dy > 0 ? scene.yMax : scene.yMin;
      return sampleLayeredScatterOffset(random, y1, y2, dy);
    } else {
      throw new IllegalStateException("Tried to sample sky fog scatter offset even though fog is not layered.");
    }
  }

  private double sampleLayeredScatterOffset(Random random, double y1, double y2, double dy) {
    if (layers.length == 0) {
      return Ray.EPSILON;
    }
    // This works only for one fog layer yet.
    FogLayer layer = layers[0];
    // Logistic distribution CDF.
    double y1v = 1 / (1 + Math.exp((layer.yWithOrigin - y1) * layer.breadthInv));
    double y2v = 1 / (1 + Math.exp((layer.yWithOrigin - y2) * layer.breadthInv));
    double middle = y1v + random.nextDouble() * (y2v - y1v);
    // Logistic distribution Quantile function.
    double offsetY = Math.log(middle / (1 - middle)) * layer.breadth + layer.yWithOrigin;
    return (offsetY - y1) / clampDy(dy);
  }

  public JsonObject toJson() {
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
    mode = FogMode.get(json.get("mode").stringValue(mode.name()));
    uniformDensity = json.get("uniformDensity").doubleValue(uniformDensity);
    skyFogDensity = json.get("skyFogDensity").doubleValue(skyFogDensity);
    layers = json.get("layers").array().elements.stream().map(JsonValue::object).map(o -> new FogLayer(
        o.get("y").doubleValue(0),
        o.get("breadth").doubleValue(0),
        o.get("density").doubleValue(0),
        scene)).toArray(FogLayer[]::new);
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
    layers = new FogLayer[0];
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
    layers = Arrays.stream(other.layers).map(FogLayer::clone).toArray(FogLayer[]::new);
    fogColor.set(other.fogColor);
  }
}
