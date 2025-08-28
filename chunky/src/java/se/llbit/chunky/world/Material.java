/* Copyright (c) 2014-2015 Jesper Öqvist <jesper@llbit.se>
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

import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.apache.commons.math3.util.FastMath;
import org.controlsfx.control.ToggleSwitch;
import se.llbit.chunky.renderer.scene.EmitterMappingType;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.IntegerAdjuster;
import se.llbit.chunky.ui.data.MaterialReferenceColorData;
import se.llbit.fx.LuxColorPicker;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonString;
import se.llbit.json.JsonValue;
import se.llbit.math.*;

import java.util.Random;

public abstract class Material {

  /**
   * Index of refraction of air.
   */
  private static final float DEFAULT_IOR = 1.000293f;

  /**
   * The name of this material.
   */
  public final String name;

  /**
   * Index of refraction. Default value is equal to the IoR for air.
   */
  public float ior = DEFAULT_IOR;

  /**
   * A block is opaque if it occupies an entire voxel and no light can pass through it.
   */
  public boolean opaque = false;

  /**
   * The solid property controls various block behaviours like if the block connects to fences,
   * gates, walls, etc.
   */
  public boolean solid = true;

  /**
   * The specular coefficient controlling how shiny the block appears.
   */
  public float specular = 0f;

  /**
   * The amount of light the material emits.
   */
  public float emittance = 0f;

  /**
   * Offset to apply to the global emitter mapping exponent (the resulting value will be constrained to be >= 0).
   */
  public float emitterMappingOffset = 0;

  /**
   * Overrides the global emitter mapping type unless set to NONE.
   */
  public EmitterMappingType emitterMappingType = EmitterMappingType.NONE;

  /**
   * Whether to use reference colors.
   */
  public boolean useReferenceColors = false;

  /**
   * (x, y, z): The color to use for the REFERENCE_COLORS emitter mapping type.
   * w: The range surrounding the specified color to apply full brightness.
   */
  public ArrayList<Vector4> emitterMappingReferenceColors = null;

  /**
   * The (linear) roughness controlling how rough a shiny block appears. A value of 0 makes the
   * surface perfectly specular, a value of 1 makes it diffuse.
   */
  public float roughness = 0f;

  public float transmissionRoughness = 0f;

  /**
   * The metalness value controls how metal-y a block appears. In reality this is a boolean value
   * but in practice usually a float is used in PBR to allow adding dirt or scratches on metals
   * without increasing the texture resolution.
   * Metals only do specular reflection for certain wavelengths (effectively tinting the reflection)
   * and have no diffuse reflection. The albedo color is used for tinting.
   */
  public float metalness = 0f;

  public float transmissionMetalness = 0f;

  public final Vector3 specularColor = new Vector3(1, 1, 1);

  public final Vector3 transmissionSpecularColor = new Vector3(1, 1, 1);

  /**
   * Texture alpha multiplier.
   */
  public float alpha = 1f;

  /**
   * Subsurface scattering property.
   */
  public float subSurfaceScattering = 0f;

  public final Vector3 diffuseColor = new Vector3(1, 1, 1);

  public final Vector3 emittanceColor = new Vector3(1, 1, 1);

  public float volumeDensity = 0f;

  public float volumeAnisotropy = 0f;

  public float volumeEmittance = 0f;

  public Vector3 volumeColor = new Vector3(1, 1, 1);

  public float absorption = 0f;

  public Vector3 absorptionColor = new Vector3(1, 1, 1);

  public boolean hidden = false;

  /**
   * Base texture.
   */
  public final Texture texture;

  public boolean waterlogged = false;

  public Material(String name, Texture texture) {
    this.name = name;
    this.texture = texture;
  }

  /**
   * Restore the default material properties.
   */
  public void restoreDefaults() {
    ior = DEFAULT_IOR;
    opaque = false;
    solid = true;
    specular = 0f;
    metalness = 0f;
    transmissionMetalness = 0f;
    emittance = 0f;
    emitterMappingOffset = 0;
    emitterMappingType = EmitterMappingType.NONE;
    useReferenceColors = false;
    emitterMappingReferenceColors = null;
    roughness = 0f;
    transmissionRoughness = 0f;
    subSurfaceScattering = 0f;
    alpha = 1f;
    specularColor.set(1, 1, 1);
    transmissionSpecularColor.set(1, 1, 1);
    diffuseColor.set(1, 1, 1);
    emittanceColor.set(1);
    volumeDensity = 0f;
    volumeAnisotropy = 0f;
    volumeEmittance = 0f;
    volumeColor.set(1, 1, 1);
    absorption = 0f;
    absorptionColor.set(1, 1, 1);
    hidden = false;
  }

  public void getColor(IntersectionRecord intersectionRecord) {
    texture.getColor(intersectionRecord);
  }

  public float[] getColor(double u, double v) {
    return texture.getColor(u, v);
  }

  public JsonValue toJson() {
    return new JsonString("mat:" + name);
  }

  public void loadMaterialProperties(JsonObject json) {
    ior = json.get("ior").floatValue(ior);
    specular = json.get("specular").floatValue(specular);
    metalness = json.get("metalness").floatValue(metalness);
    transmissionMetalness = json.get("transmissionMetalness").floatValue(transmissionMetalness);
    emittance = json.get("emittance").floatValue(emittance);
    emitterMappingOffset = json.get("emitterMappingOffset").floatValue(emitterMappingOffset);
    emitterMappingType = EmitterMappingType.valueOf(json.get("emitterMappingType").asString(emitterMappingType.getId()));
    useReferenceColors = json.get("useReferenceColors").boolValue(useReferenceColors);
    JsonArray referenceColorsArray = json.get("emitterMappingReferenceColors").array();
    if (!referenceColorsArray.isEmpty()) {
      emitterMappingReferenceColors = new ArrayList<>();
      for (JsonValue referenceColorJson : referenceColorsArray.elements) {
        JsonObject referenceColorObject = referenceColorJson.object();
        emitterMappingReferenceColors.add(new Vector4(
            referenceColorObject.get("red").doubleValue(0),
            referenceColorObject.get("green").doubleValue(0),
            referenceColorObject.get("blue").doubleValue(0),
            referenceColorObject.get("range").doubleValue(0)));
      }
    } else {
      emitterMappingReferenceColors = null;
    }
    roughness = json.get("roughness").floatValue(roughness);
    transmissionRoughness = json.get("transmissionRoughness").floatValue(transmissionRoughness);
    alpha = json.get("alpha").floatValue(alpha);
    specularColor.set(ColorUtil.jsonToRGB(json.get("specularColor").asObject(), specularColor));
    transmissionSpecularColor.set(ColorUtil.jsonToRGB(json.get("transmissionSpecularColor").asObject(), transmissionSpecularColor));
    subSurfaceScattering = json.get("subsurfaceScattering").floatValue(subSurfaceScattering);
    diffuseColor.set(ColorUtil.jsonToRGB(json.get("diffuseColor").asObject(), diffuseColor));
    emittanceColor.set(ColorUtil.jsonToRGB(json.get("emittanceColor").asObject(), emittanceColor));
    volumeDensity = json.get("volumeDensity").floatValue(volumeDensity);
    volumeAnisotropy = json.get("volumeAnisotropy").floatValue(volumeAnisotropy);
    volumeEmittance = json.get("volumeEmittance").floatValue(volumeEmittance);
    volumeColor.set(ColorUtil.jsonToRGB(json.get("volumeColor").asObject(), volumeColor));
    absorption = json.get("absorption").floatValue(absorption);
    absorptionColor.set(ColorUtil.jsonToRGB(json.get("absorptionColor").asObject(), absorptionColor));
    opaque = json.get("opaque").boolValue(opaque);
    hidden = json.get("hidden").boolValue(hidden);
  }

  public JsonObject saveMaterialProperties() {
    JsonObject properties = new JsonObject();
    properties.add("ior", ior);
    properties.add("specular", specular);
    properties.add("metalness", metalness);
    properties.add("transmissionMetalness", transmissionMetalness);
    properties.add("emittance", emittance);
    properties.add("emitterMappingOffset", emitterMappingOffset);
    properties.add("emitterMappingType", emitterMappingType.getId());
    properties.add("useReferenceColors", useReferenceColors);
    JsonArray referenceColorsArray = new JsonArray(0);
    if (emitterMappingReferenceColors != null) {
      emitterMappingReferenceColors.forEach(referenceColor -> {
        JsonObject referenceColorJson = new JsonObject();
        referenceColorJson.add("red", referenceColor.x);
        referenceColorJson.add("green", referenceColor.y);
        referenceColorJson.add("blue", referenceColor.z);
        referenceColorJson.add("range", referenceColor.w);
        referenceColorsArray.add(referenceColorJson);
      });
    }
    properties.add("emitterMappingReferenceColors", referenceColorsArray);
    properties.add("roughness", roughness);
    properties.add("transmissionRoughness", transmissionRoughness);
    properties.add("alpha", alpha);
    properties.add("specularColor", ColorUtil.rgbToJson(specularColor));
    properties.add("transmissionSpecularColor", ColorUtil.rgbToJson(transmissionSpecularColor));
    properties.add("subsurfaceScattering", subSurfaceScattering);
    properties.add("diffuseColor", ColorUtil.rgbToJson(diffuseColor));
    properties.add("emittanceColor", ColorUtil.rgbToJson(emittanceColor));
    properties.add("volumeDensity", volumeDensity);
    properties.add("volumeAnisotropy", volumeAnisotropy);
    properties.add("volumeEmittance", volumeEmittance);
    properties.add("volumeColor", ColorUtil.rgbToJson(volumeColor));
    properties.add("absorption", absorption);
    properties.add("absorptionColor", ColorUtil.rgbToJson(absorptionColor));
    properties.add("opaque", opaque);
    properties.add("hidden", hidden);
    return properties;
  }

  public boolean isWater() {
    return false;
  }

  public boolean isWaterFilled() {
    return waterlogged || isWater();
  }

  public boolean isSameMaterial(Material other) {
    return other == this;
  }

  public double getPerceptualSmoothness() {
    return 1 - Math.sqrt(roughness);
  }

  public void setPerceptualSmoothness(double perceptualSmoothness) {
    roughness = (float) Math.pow(1 - perceptualSmoothness, 2);
  }

  public double getPerceptualTransmissionSmoothness() {
    return 1 - Math.sqrt(transmissionRoughness);
  }

  public void setPerceptualTransmissionSmoothness(double perceptualTransmissionSmoothness) {
    transmissionRoughness = (float) Math.pow(1 - perceptualTransmissionSmoothness, 2);
  }

  public void setLightLevel(float level) {
    emittance = level / 15;
  }

  public void addRefColorGammaCorrected(float r, float g, float b, float delta) {
    if (emitterMappingReferenceColors == null) {
      emitterMappingReferenceColors = new ArrayList<>();
    }
    emitterMappingReferenceColors.add(new Vector4(Math.pow(r/255, Scene.DEFAULT_GAMMA), Math.pow(g/255, Scene.DEFAULT_GAMMA), Math.pow(b/255, Scene.DEFAULT_GAMMA), delta));
  }

  public boolean volumeIntersect(IntersectionRecord intersectionRecord, Random random) {
    if (volumeDensity < Constants.EPSILON) {
      return false;
    }

    intersectionRecord.distance = fogDistance(volumeDensity, random);
    intersectionRecord.material = this;
    intersectionRecord.color.set(volumeColor.x, volumeColor.y, volumeColor.z, 1);
    intersectionRecord.flags |= IntersectionRecord.VOLUME_INTERSECT;
    return true;
  }

  public static double fogDistance(double density, Random random) {
    return -FastMath.log(1 - random.nextDouble()) / density;
  }

  public void absorption(Vector3 color, double distance) {
    if (absorption < Constants.EPSILON) {
      return;
    }
    color.x *= FastMath.exp((1 - absorptionColor.x) * absorption * -distance);
    color.y *= FastMath.exp((1 - absorptionColor.y) * absorption * -distance);
    color.z *= FastMath.exp((1 - absorptionColor.z) * absorption * -distance);
  }

  public void doEmitterMapping(Vector3 emittance, Vector4 color, Scene scene) {
    double exp = Math.max(scene.getEmitterMappingExponent() + emitterMappingOffset, 0);
    EmitterMappingType emitterMappingType = this.emitterMappingType == EmitterMappingType.NONE ? scene.getEmitterMappingType() : this.emitterMappingType;

    boolean emit = !useReferenceColors;
    if (useReferenceColors) {
      if (emitterMappingReferenceColors == null) {
        emittance.set(0, 0, 0);
        return;
      }

      for (Vector4 referenceColor : emitterMappingReferenceColors) {
        emit |= (Math.max(Math.abs(color.x - referenceColor.x), Math.max(Math.abs(color.y - referenceColor.y), Math.abs(color.z - referenceColor.z))) <= referenceColor.w);
      }
    }
    if (emit) {
      switch (emitterMappingType) {
        case BRIGHTEST_CHANNEL:
          double val = FastMath.pow(Math.max(color.x, Math.max(color.y, color.z)), exp);
          emittance.set(color.x * val, color.y * val, color.z * val);
          break;
        case INDEPENDENT_CHANNELS:
          emittance.set(FastMath.pow(color.x, exp), FastMath.pow(color.y, exp), FastMath.pow(color.z, exp));
          break;
      }
      emittance.scale(this.emittance);
    } else {
      emittance.set(0, 0, 0);
    }
  }

  public boolean scatter(Ray ray, IntersectionRecord intersectionRecord, Scene scene, final Vector3 emittance, Random random) {
    boolean mediumChanged = false;
    boolean throughSurface = false;

    Vector3 direction;

    double n2 = ior;
    double n1 = ray.getCurrentMedium().ior;

    double pDiffuse = intersectionRecord.color.w * alpha;

    if (random.nextDouble() < pDiffuse) {
      // Reflection
      if (random.nextDouble() < specular) {
        // Specular reflection with roughness

        // For rough specular reflections, we interpolate linearly between the diffuse ray direction and the specular direction,
        // which is inspired by https://blog.demofox.org/2020/06/06/casual-shadertoy-path-tracing-2-image-improvement-and-glossy-reflections/
        // This gives good-looking results, although a microfacet-based model would be more physically correct.
        direction = specularReflection(ray.d, intersectionRecord.shadeN);
        if (roughness > Constants.EPSILON) {
          Vector3 roughnessDirection = lambertianReflection(intersectionRecord.n, random);
          roughnessDirection.scale(roughness);
          roughnessDirection.scaleAdd(1 - roughness, direction);
          roughnessDirection.normalize();
          direction = roughnessDirection;
        }
        tintColor(intersectionRecord.color, metalness, specularColor, random);
        ray.flags |= Ray.SPECULAR;
        Vector4 emittanceColor1 = new Vector4(intersectionRecord.color);
        tintColor(emittanceColor1, 1, emittanceColor, random);
        doEmitterMapping(emittance, emittanceColor1, scene);
      } else {
        // Lambertian reflection
        if (random.nextDouble() < subSurfaceScattering) {
          intersectionRecord.shadeN.scale(-1);
          intersectionRecord.n.scale(-1);
          ray.d.scale(-1); // This is to prevent direction from being inverted later.
        }
        direction = lambertianReflection(intersectionRecord.shadeN, random);
        tintColor(intersectionRecord.color, 1, diffuseColor, random);
        ray.flags |= Ray.DIFFUSE | Ray.INDIRECT;
        Vector4 emittanceColor1 = new Vector4(intersectionRecord.color);
        tintColor(emittanceColor1, 1, emittanceColor, random);
        doEmitterMapping(emittance, emittanceColor1, scene);
      }
    } else {
      // Transmission / Refraction
      if (FastMath.abs(n2 - n1) > Constants.EPSILON) {
        // Refraction / Total internal reflection
        boolean front_face = ray.d.dot(intersectionRecord.shadeN) < 0.0;
        double ri = (front_face) ? (n1 / n2) : (n2 / n1);

        Vector3 unitDirection = ray.d.normalized();
        double cosTheta = FastMath.min(unitDirection.rScale(-1).dot(intersectionRecord.shadeN), 1.0);
        double sinTheta = FastMath.sqrt(1.0 - cosTheta * cosTheta);

        boolean cannotRefract = ri * sinTheta > 1.0;

        if (cannotRefract || schlickReflectance(cosTheta, ri) > random.nextDouble()) {
          // Total internal reflection
          direction = specularReflection(unitDirection, intersectionRecord.shadeN);
          double interfaceRoughness = FastMath.max(roughness, ray.getCurrentMedium().roughness);
          if (interfaceRoughness > Constants.EPSILON) {
            Vector3 roughnessDirection = lambertianReflection(intersectionRecord.n, random);
            roughnessDirection.scale(interfaceRoughness);
            roughnessDirection.scaleAdd(1 - interfaceRoughness, direction);
            roughnessDirection.normalize();
            direction = roughnessDirection;
          }
          tintColor(intersectionRecord.color, metalness, specularColor, random);
          ray.flags |= Ray.SPECULAR;
        } else {
          // Refraction
          if ((intersectionRecord.flags & IntersectionRecord.NO_MEDIUM_CHANGE) != 0) {
            direction = new Vector3(ray.d);
          } else {
            direction = specularRefraction(unitDirection, intersectionRecord.shadeN, ri);
            double interfaceTransmissionRoughness = FastMath.max(transmissionRoughness, ray.getCurrentMedium().transmissionRoughness);
            if (interfaceTransmissionRoughness > Constants.EPSILON) {
              Vector3 roughnessDirection = lambertianReflection(intersectionRecord.n.rScale(-1), random);
              roughnessDirection.scale(interfaceTransmissionRoughness);
              roughnessDirection.scaleAdd(1 - interfaceTransmissionRoughness, direction);
              roughnessDirection.normalize();
              direction = roughnessDirection;
            }
            mediumChanged = true;
          }
          tintColor(intersectionRecord.color, transmissionMetalness, transmissionSpecularColor, random);
          ray.flags |= Ray.SPECULAR;
          throughSurface = true;
        }
      } else {
        // Transmission
        direction = new Vector3(ray.d);
        tintColor(intersectionRecord.color, transmissionMetalness, transmissionSpecularColor, random);
        ray.flags |= Ray.SPECULAR;
        throughSurface = true;
        if ((intersectionRecord.flags & IntersectionRecord.NO_MEDIUM_CHANGE) == 0) {
          mediumChanged = true;
        }
      }
    }

    int sign = throughSurface ? -1 : 1;

    if (QuickMath.signum(intersectionRecord.n.dot(direction)) == sign * QuickMath.signum(intersectionRecord.n.dot(ray.d))) {
      double factor = QuickMath.signum(intersectionRecord.n.dot(ray.d)) * -Constants.EPSILON - direction.dot(intersectionRecord.n);
      direction.scaleAdd(factor, intersectionRecord.n);
      direction.normalize();
    }
    ray.d.set(direction);

    ray.o.scaleAdd(sign * Constants.OFFSET, intersectionRecord.n);

    return mediumChanged;
  }

  public void volumeScatter(Ray ray, Random random) {
    Vector3 invDir = ray.d.rScale(-1);
    Vector3 outDir = new Vector3();
    double x1 = random.nextDouble();
    double x2 = random.nextDouble();
    henyeyGreensteinSampleP(volumeAnisotropy, invDir, outDir, x1, x2);
    outDir.normalize();
    ray.d.set(outDir);
    ray.flags |= Ray.INDIRECT | Ray.DIFFUSE;
  }

  public static double phaseHG(double cosTheta, double g) {
    double denominator = 1 + (g * g) + (2 * g * cosTheta);
    return Constants.INV_4_PI * (1 - g * g) / (denominator * FastMath.sqrt(denominator));
  }

  /**
   * Code adapted from <a href="https://github.com/mmp/pbrt-v3/blob/b47ed0d334cde4c475def0044c974b7db173ff99/src/core/medium.cpp#L193">pbrt</a>
   */
  private static double henyeyGreensteinSampleP(double g, Vector3 wo, Vector3 wi, double x1, double x2) {
    double cosTheta;
    if (FastMath.abs(g) < 1e-3) {
      cosTheta = 1 - 2 * x1;
    } else {
      double sqrTerm = (1 - g * g) / (1 + g - 2 * g * x1);
      cosTheta = -(1 + g * g - sqrTerm * sqrTerm) / (2 * g);
    }

    double sinTheta = FastMath.sqrt(FastMath.max(0d, 1 - cosTheta * cosTheta));
    double phi = 2 * FastMath.PI * x2;
    Vector3 v1 = new Vector3();
    Vector3 v2 = new Vector3();
    coordinateSystem(wo, v1, v2);
    wi.set(sphericalDirection(sinTheta, cosTheta, phi, v1, v2, wo));
    return phaseHG(cosTheta, g);
  }

  /**
   * Code adapted from <a href="https://github.com/mmp/pbrt-v3/blob/b47ed0d334cde4c475def0044c974b7db173ff99/src/core/geometry.h#L1020">pbrt</a>
   */
  private static void coordinateSystem(Vector3 v1, Vector3 v2, Vector3 v3) {
    Vector3 x;
    if (FastMath.abs(v1.x) > FastMath.abs(v1.y)) {
      x = new Vector3(-v1.z, 0, v1.x);
      x.scale(1 / FastMath.sqrt(v1.x * v1.x + v1.z * v1.z));
    } else {
      x = new Vector3(0, v1.z, -v1.y);
      x.scale(1 / FastMath.sqrt(v1.y * v1.y + v1.z * v1.z));
    }
    v2.set(x);
    v3.cross(v1, v2);
  }

  /**
   * Code adapted from <a href="https://github.com/mmp/pbrt-v3/blob/b47ed0d334cde4c475def0044c974b7db173ff99/src/core/geometry.h#L1465C25-L1465C25">pbrt</a>
   */
  private static Vector3 sphericalDirection(double sinTheta, double cosTheta, double phi, Vector3 x, Vector3 y, Vector3 z) {
    Vector3 x1 = new Vector3(x);
    Vector3 y1 = new Vector3(y);
    Vector3 z1 = new Vector3(z);
    x1.scale(sinTheta * FastMath.cos(phi));
    y1.scale(sinTheta * FastMath.sin(phi));
    z1.scale(cosTheta);
    x1.add(y1);
    x1.add(z1);
    return x1;
  }

  private static Vector3 randomHemisphereDir(Vector3 normal, Random random) {
    double x1 = random.nextDouble();
    double x2 = random.nextDouble();
    double r = FastMath.sqrt(x1);
    double theta = 2 * FastMath.PI * x2;

    // project to point on hemisphere in tangent space
    double tx = r * FastMath.cos(theta);
    double ty = r * FastMath.sin(theta);
    double tz = FastMath.sqrt(1 - x1);

    // Transform from tangent space to world space
    double xx, xy, xz;
    double ux, uy, uz;
    double vx, vy, vz;

    if (QuickMath.abs(normal.x) > 0.1) {
      xx = 0;
      xy = 1;
    } else {
      xx = 1;
      xy = 0;
    }
    xz = 0;

    ux = xy * normal.z - xz * normal.y;
    uy = xz * normal.x - xx * normal.z;
    uz = xx * normal.y - xy * normal.x;

    r = 1 / FastMath.sqrt(ux*ux + uy*uy + uz*uz);

    ux *= r;
    uy *= r;
    uz *= r;

    vx = uy * normal.z - uz * normal.y;
    vy = uz * normal.x - ux * normal.z;
    vz = ux * normal.y - uy * normal.x;

    return new Vector3(
      ux * tx + vx * ty + normal.x * tz,
      uy * tx + vy * ty + normal.y * tz,
      uz * tx + vz * ty + normal.z * tz
    );
  }

  private static Vector3 lambertianReflection(Vector3 n, Random random) {
    Vector3 direction = randomHemisphereDir(n, random);
    direction.normalize();
    return direction;
  }

  private static Vector3 specularReflection(Vector3 v, Vector3 n) {
    return v.rSub(n.rScale(2 * v.dot(n)));
  }

  private static double schlickReflectance(double cosine, double refractionIndex) {
    double r0 = (1 - refractionIndex) / (1 + refractionIndex);
    r0 = r0 * r0;
    return r0 + (1 - r0) * FastMath.pow((1 - cosine), 5);
  }

  private static Vector3 specularRefraction(Vector3 uv, Vector3 n, double etaiOverEtat) {
    double cosTheta = FastMath.min(uv.rScale(-1).dot(n), 1.0);
    Vector3 rOutPerp = n.rScale(cosTheta).rAdd(uv).rScale(etaiOverEtat);
    Vector3 rOutParallel = n.rScale(-FastMath.sqrt(FastMath.abs(1.0 - rOutPerp.lengthSquared())));
    return rOutPerp.rAdd(rOutParallel);
  }

  public static void tintColor(Vector4 color, float metalness, Vector3 colorModifier, Random random) {
    color.x = 1 - metalness * (1 - color.x);
    color.y = 1 - metalness * (1 - color.y);
    color.z = 1 - metalness * (1 - color.z);
    color.x *= colorModifier.x;
    color.y *= colorModifier.y;
    color.z *= colorModifier.z;
  }

  public static VBox getControls(Material material, Scene scene) {
    DoubleAdjuster emittanceAdjuster = new DoubleAdjuster();
    LuxColorPicker emittanceColorPicker = new LuxColorPicker();
    DoubleAdjuster emitterMappingOffset = new DoubleAdjuster();
    ChoiceBox<EmitterMappingType> emitterMappingType = new ChoiceBox<>();
    ToggleSwitch useReferenceColors = new ToggleSwitch();
    DoubleAdjuster alphaAdjuster = new DoubleAdjuster();
    DoubleAdjuster subsurfaceScatteringAdjuster = new DoubleAdjuster();
    LuxColorPicker diffuseColorPicker = new LuxColorPicker();
    DoubleAdjuster specularAdjuster = new DoubleAdjuster();
    DoubleAdjuster iorAdjuster = new DoubleAdjuster();
    DoubleAdjuster smoothnessAdjuster = new DoubleAdjuster();
    DoubleAdjuster transmissionSmoothnessAdjuster = new DoubleAdjuster();
    DoubleAdjuster metalnessAdjuster = new DoubleAdjuster();
    DoubleAdjuster transmissionMetalnessAdjuster = new DoubleAdjuster();
    LuxColorPicker specularColorPicker = new LuxColorPicker();
    LuxColorPicker transmissionSpecularColorPicker = new LuxColorPicker();
    DoubleAdjuster volumeDensityAdjuster = new DoubleAdjuster();
    DoubleAdjuster volumeAnisotropyAdjuster = new DoubleAdjuster();
    DoubleAdjuster volumeEmittanceAdjuster = new DoubleAdjuster();
    LuxColorPicker volumeColorPicker = new LuxColorPicker();
    DoubleAdjuster absorptionAdjuster = new DoubleAdjuster();
    LuxColorPicker absorptionColorPicker = new LuxColorPicker();

    TableView<MaterialReferenceColorData> referenceColorTable = new TableView<>();
    Button addReferenceColor = new Button();
    Button removeReferenceColor = new Button();
    LuxColorPicker referenceColorPicker = new LuxColorPicker();
    IntegerAdjuster referenceColorRangeSlider = new IntegerAdjuster();

    emittanceAdjuster.setName("Emittance");
    emittanceAdjuster.setTooltip("Intensity of the light emitted from the selected material.");
    emittanceAdjuster.setRange(0, 100);
    emittanceAdjuster.clampMin();
    emittanceAdjuster.set(material.emittance);
    emittanceAdjuster.onValueChange(value -> {
      material.emittance = value.floatValue();
      scene.refresh();
    });

    emittanceColorPicker.setText("Emittance color");
    emittanceColorPicker.setColor(ColorUtil.toFx(material.emittanceColor));
    emittanceColorPicker.colorProperty().addListener(
        ((observable, oldValue, newValue) -> {
          material.emittanceColor.set(ColorUtil.fromFx(newValue));
          scene.refresh();
        })
    );

    emitterMappingOffset.setName("Emitter mapping offset");
    emitterMappingOffset.setRange(-5, 5);
    emitterMappingOffset.setTooltip("Offset applied to the global emitter mapping exponent.");
    emitterMappingOffset.set(material.emitterMappingOffset);
    emitterMappingOffset.onValueChange(value -> {
      material.emitterMappingOffset = value.floatValue();
      scene.refresh();
    });

    emitterMappingType.getItems().addAll(EmitterMappingType.values());
    emitterMappingType.setTooltip(new Tooltip("Overrides the global setting for emitter mapping type."));
    emitterMappingType.getSelectionModel().select(material.emitterMappingType);
    emitterMappingType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      material.emitterMappingType = newValue;
      scene.refresh();
    });

    VBox emitterMappingTypeBox = new VBox(6, new Label("Emitter mapping type"), emitterMappingType);

    useReferenceColors.setText("Use reference colors");
    useReferenceColors.setSelected(material.useReferenceColors);
    useReferenceColors.selectedProperty().addListener((observable, oldValue, newValue) -> {
      material.useReferenceColors = newValue;
      scene.refresh();
    });

    alphaAdjuster.setName("Alpha");
    alphaAdjuster.setTooltip("Alpha (opacity) of the selected material.");
    alphaAdjuster.setRange(0, 1);
    alphaAdjuster.clampBoth();
    alphaAdjuster.set(material.alpha);
    alphaAdjuster.onValueChange(value -> {
      material.alpha = value.floatValue();
      scene.refresh();
    });

    subsurfaceScatteringAdjuster.setName("Subsurface scattering");
    subsurfaceScatteringAdjuster.setTooltip("Probability of a ray to be scattered behind the surface.");
    subsurfaceScatteringAdjuster.setRange(0, 1);
    subsurfaceScatteringAdjuster.clampBoth();
    subsurfaceScatteringAdjuster.set(material.subSurfaceScattering);
    subsurfaceScatteringAdjuster.onValueChange(value -> {
      material.subSurfaceScattering = value.floatValue();
      scene.refresh();
    });

    diffuseColorPicker.setText("Diffuse color");
    diffuseColorPicker.setColor(ColorUtil.toFx(material.diffuseColor));
    diffuseColorPicker.colorProperty().addListener(
        ((observable, oldValue, newValue) -> {
          material.diffuseColor.set(ColorUtil.fromFx(newValue));
          scene.refresh();
        })
    );

    specularAdjuster.setName("Specular");
    specularAdjuster.setTooltip("Reflectivity of the selected material.");
    specularAdjuster.setRange(0, 1);
    specularAdjuster.clampBoth();
    specularAdjuster.set(material.specular);
    specularAdjuster.onValueChange(value -> {
      material.specular = value.floatValue();
      scene.refresh();
    });

    iorAdjuster.setName("IoR");
    iorAdjuster.setTooltip("Index of Refraction of the selected material.");
    iorAdjuster.setRange(0, 5);
    iorAdjuster.clampMin();
    iorAdjuster.setMaximumFractionDigits(6);
    iorAdjuster.set(material.ior);
    iorAdjuster.onValueChange(value -> {
      material.ior = value.floatValue();
      scene.refresh();
    });

    smoothnessAdjuster.setName("Smoothness");
    smoothnessAdjuster.setTooltip("Smoothness of the selected material.");
    smoothnessAdjuster.setRange(0, 1);
    smoothnessAdjuster.clampBoth();
    smoothnessAdjuster.set(material.getPerceptualSmoothness());
    smoothnessAdjuster.onValueChange(value -> {
      material.setPerceptualSmoothness(value);
      scene.refresh();
    });

    transmissionSmoothnessAdjuster.setName("Transmission smoothness");
    transmissionSmoothnessAdjuster.setTooltip("Transmission smoothness of the selected material.");
    transmissionSmoothnessAdjuster.setRange(0, 1);
    transmissionSmoothnessAdjuster.clampBoth();
    transmissionSmoothnessAdjuster.set(material.getPerceptualTransmissionSmoothness());
    transmissionSmoothnessAdjuster.onValueChange(value -> {
      material.setPerceptualTransmissionSmoothness(value);
      scene.refresh();
    });

    metalnessAdjuster.setName("Metalness");
    metalnessAdjuster.setTooltip("Texture tinting of reflected light.");
    metalnessAdjuster.setRange(0, 1);
    metalnessAdjuster.clampBoth();
    metalnessAdjuster.set(material.metalness);
    metalnessAdjuster.onValueChange(value -> {
      material.metalness = value.floatValue();
      scene.refresh();
    });

    transmissionMetalnessAdjuster.setName("Transmission metalness");
    transmissionMetalnessAdjuster.setTooltip("Texture tinting of refracted/transmitted light.");
    transmissionMetalnessAdjuster.setRange(0, 1);
    transmissionMetalnessAdjuster.clampBoth();
    transmissionMetalnessAdjuster.set(material.transmissionMetalness);
    transmissionMetalnessAdjuster.onValueChange(value -> {
      material.transmissionMetalness = value.floatValue();
      scene.refresh();
    });

    specularColorPicker.setText("Specular color");
    specularColorPicker.setColor(ColorUtil.toFx(material.specularColor));
    specularColorPicker.colorProperty().addListener(
        ((observable, oldValue, newValue) -> {
          material.specularColor.set(ColorUtil.fromFx(newValue));
          scene.refresh();
        })
    );

    transmissionSpecularColorPicker.setText("Transmission specular color");
    transmissionSpecularColorPicker.setColor(ColorUtil.toFx(material.transmissionSpecularColor));
    transmissionSpecularColorPicker.colorProperty().addListener(
        ((observable, oldValue, newValue) -> {
          material.transmissionSpecularColor.set(ColorUtil.fromFx(newValue));
          scene.refresh();
        })
    );

    volumeDensityAdjuster.setName("Volume density");
    volumeDensityAdjuster.setTooltip("Density of volume medium.");
    volumeDensityAdjuster.setRange(0, 1);
    volumeDensityAdjuster.clampMin();
    volumeDensityAdjuster.set(material.volumeDensity);
    volumeDensityAdjuster.onValueChange( value -> {
      material.volumeDensity = value.floatValue();
      scene.refresh();
    });

    volumeAnisotropyAdjuster.setName("Volume anisotropy");
    volumeAnisotropyAdjuster.setTooltip("Changes the direction light is more likely to be scattered.\n" +
        "Positive values increase the chance light scatters into its original direction of travel.\n" +
        "Negative values increase the chance light scatters away from its original direction of travel.");
    volumeAnisotropyAdjuster.setRange(-1, 1);
    volumeAnisotropyAdjuster.clampBoth();
    volumeAnisotropyAdjuster.set(material.volumeAnisotropy);
    volumeAnisotropyAdjuster.onValueChange( value -> {
      material.volumeAnisotropy = value.floatValue();
      scene.refresh();
    });

    volumeEmittanceAdjuster.setName("Volume emittance");
    volumeEmittanceAdjuster.setTooltip("Emittance of volume medium.");
    volumeEmittanceAdjuster.setRange(0, 100);
    volumeEmittanceAdjuster.clampMin();
    volumeEmittanceAdjuster.set(material.volumeEmittance);
    volumeEmittanceAdjuster.onValueChange( value -> {
      material.volumeEmittance = value.floatValue();
      scene.refresh();
    });

    volumeColorPicker.setText("Volume color");
    volumeColorPicker.setColor(ColorUtil.toFx(material.volumeColor));
    volumeColorPicker.colorProperty().addListener(
        ((observable, oldValue, newValue) -> {
          material.volumeColor.set(ColorUtil.fromFx(newValue));
          scene.refresh();
        })
    );

    absorptionAdjuster.setName("Absorption");
    absorptionAdjuster.setTooltip("Absorption of volume medium.");
    absorptionAdjuster.setRange(0, 10);
    absorptionAdjuster.clampMin();
    absorptionAdjuster.makeLogarithmic();
    absorptionAdjuster.set(material.absorption);
    absorptionAdjuster.onValueChange( value -> {
      material.absorption = value.floatValue();
      scene.refresh();
    });

    absorptionColorPicker.setText("Absorption color");
    absorptionColorPicker.setColor(ColorUtil.toFx(material.absorptionColor));
    absorptionColorPicker.colorProperty().addListener(
        ((observable, oldValue, newValue) -> {
          material.absorptionColor.set(ColorUtil.fromFx(newValue));
          scene.refresh();
        })
    );

    GridPane settings = new GridPane();

    ColumnConstraints columnConstraints = new ColumnConstraints();
    columnConstraints.setPercentWidth(50);

    settings.getColumnConstraints().addAll(columnConstraints, columnConstraints);
    settings.setHgap(10);
    settings.setVgap(10);

    VBox diffuseSettings = new VBox(6, emittanceAdjuster, emittanceColorPicker, emitterMappingOffset, emitterMappingTypeBox, useReferenceColors, alphaAdjuster, subsurfaceScatteringAdjuster, diffuseColorPicker);
    VBox volumeSettings = new VBox(6, volumeDensityAdjuster, volumeAnisotropyAdjuster, volumeEmittanceAdjuster, volumeColorPicker, absorptionAdjuster, absorptionColorPicker);
    VBox specularSettings = new VBox(6, specularAdjuster, iorAdjuster, smoothnessAdjuster, transmissionSmoothnessAdjuster);
    VBox specularColorSettings = new VBox(6, metalnessAdjuster, transmissionMetalnessAdjuster, specularColorPicker, transmissionSpecularColorPicker);

    settings.add(diffuseSettings, 0, 0);
    settings.add(volumeSettings, 1, 0);
    settings.add(specularSettings, 0, 1);
    settings.add(specularColorSettings, 1, 1);

    final ArrayList<ChangeListener<Color>> referenceColorPickerListener = new ArrayList<>(1);
    referenceColorPickerListener.add((observable, oldValue, newValue) -> {});

    referenceColorPicker.setText("Reference color");
    referenceColorPicker.colorProperty().addListener(referenceColorPickerListener.get(0));

    referenceColorRangeSlider.setName("Range");
    referenceColorRangeSlider.setRange(0, 255);
    referenceColorRangeSlider.clampBoth();

    TableColumn<MaterialReferenceColorData, String> redColumn = new TableColumn<>("Red");
    TableColumn<MaterialReferenceColorData, String> greenColumn = new TableColumn<>("Green");
    TableColumn<MaterialReferenceColorData, String> blueColumn = new TableColumn<>("Blue");
    TableColumn<MaterialReferenceColorData, String> rangeColumn = new TableColumn<>("Range");

    redColumn.setCellValueFactory(new PropertyValueFactory<>("red"));
    greenColumn.setCellValueFactory(new PropertyValueFactory<>("green"));
    blueColumn.setCellValueFactory(new PropertyValueFactory<>("blue"));
    rangeColumn.setCellValueFactory(new PropertyValueFactory<>("range"));

    redColumn.setSortable(true);
    greenColumn.setSortable(true);
    blueColumn.setSortable(true);
    rangeColumn.setSortable(true);

    referenceColorTable.getColumns().add(redColumn);
    referenceColorTable.getColumns().add(greenColumn);
    referenceColorTable.getColumns().add(blueColumn);
    referenceColorTable.getColumns().add(rangeColumn);
    referenceColorTable.setMaxHeight(200);

    if (material.emitterMappingReferenceColors != null && !material.emitterMappingReferenceColors.isEmpty()) {
      material.emitterMappingReferenceColors.forEach(referenceColor -> {
        referenceColorTable.getItems().add(new MaterialReferenceColorData(referenceColor));
      });
    }

    referenceColorTable.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          referenceColorPicker.colorProperty().removeListener(referenceColorPickerListener.get(0));
          if (newValue != null) {
            referenceColorPicker.setColor(ColorUtil.toFx(newValue.getReferenceColor().toVec3()));
            referenceColorPickerListener.set(0, (observable2, oldValue2, newValue2) -> {
              Vector3 color = ColorUtil.fromFx(newValue2);
              newValue.setReferenceColor(color);
              scene.refresh();
            });
            referenceColorPicker.colorProperty().addListener(referenceColorPickerListener.get(0));
            referenceColorRangeSlider.set(newValue.getReferenceColor().w * 255);
            referenceColorRangeSlider.onValueChange(value -> {
              newValue.setRange(value);
              scene.refresh();
            });
          } else {
            referenceColorRangeSlider.onValueChange(value -> {});
          }
        });

    addReferenceColor.setText("Add reference color");
    addReferenceColor.setOnAction(e -> {
      Vector4 referenceColor = new Vector4(1, 1, 1, 1);
      if (material.emitterMappingReferenceColors == null) {
        material.emitterMappingReferenceColors = new ArrayList<>(1);
      }
      material.emitterMappingReferenceColors.add(referenceColor);
      referenceColorPicker.colorProperty().removeListener(referenceColorPickerListener.get(0));
      referenceColorRangeSlider.onValueChange(value -> {});
      referenceColorTable.getItems().add(new MaterialReferenceColorData(referenceColor));
      referenceColorTable.getSelectionModel().selectLast();
    });
    removeReferenceColor.setText("Remove reference color");
    removeReferenceColor.setOnAction(e -> {
      referenceColorPicker.colorProperty().removeListener(referenceColorPickerListener.get(0));
      referenceColorRangeSlider.onValueChange(value -> {});
      Vector4 referenceColor = referenceColorTable.getSelectionModel().getSelectedItem().getReferenceColor();
      material.emitterMappingReferenceColors.remove(referenceColor);
      if (material.emitterMappingReferenceColors.isEmpty()) {
        material.emitterMappingReferenceColors = null;
      }
      referenceColorTable.getItems().remove(referenceColorTable.getSelectionModel().getSelectedItem());
    });

    HBox addRemoveControls = new HBox(10, addReferenceColor, removeReferenceColor);

    return new VBox(10, settings, addRemoveControls, referenceColorTable, referenceColorPicker, referenceColorRangeSlider);
  }
}
