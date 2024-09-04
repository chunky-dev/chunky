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

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.resources.Texture;
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
    roughness = 0f;
    transmissionRoughness = 0f;
    subSurfaceScattering = 0f;
    alpha = 1f;
    specularColor.set(1, 1, 1);
    transmissionSpecularColor.set(1, 1, 1);
    diffuseColor.set(1, 1, 1);
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
    roughness = json.get("roughness").floatValue(roughness);
    transmissionRoughness = json.get("transmissionRoughness").floatValue(transmissionRoughness);
    alpha = json.get("alpha").floatValue(alpha);
    specularColor.set(ColorUtil.jsonToRGB(json.get("specularColor").asObject(), specularColor));
    transmissionSpecularColor.set(ColorUtil.jsonToRGB(json.get("transmissionSpecularColor").asObject(), transmissionSpecularColor));
    subSurfaceScattering = json.get("subsurfaceScattering").floatValue(subSurfaceScattering);
    diffuseColor.set(ColorUtil.jsonToRGB(json.get("diffuseColor").asObject(), diffuseColor));
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
    properties.add("roughness", roughness);
    properties.add("transmissionRoughness", transmissionRoughness);
    properties.add("alpha", alpha);
    properties.add("specularColor", ColorUtil.rgbToJson(specularColor));
    properties.add("transmissionSpecularColor", ColorUtil.rgbToJson(transmissionSpecularColor));
    properties.add("subsurfaceScattering", subSurfaceScattering);
    properties.add("diffuseColor", ColorUtil.rgbToJson(diffuseColor));
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

  public boolean volumeIntersect(IntersectionRecord intersectionRecord, Random random) {
    if (volumeDensity < Constants.EPSILON) {
      return false;
    }

    double fogPenetrated = -FastMath.log(1 - random.nextDouble());
    intersectionRecord.distance = fogPenetrated / volumeDensity;
    intersectionRecord.material = this;
    intersectionRecord.color.set(volumeColor.x, volumeColor.y, volumeColor.z, 1);
    intersectionRecord.flags |= IntersectionRecord.VOLUME_INTERSECT;
    return true;
  }

  public void absorption(Vector3 color, double distance) {
    if (absorption < Constants.EPSILON) {
      return;
    }
    color.x *= FastMath.exp((1 - absorptionColor.x) * absorption * -distance);
    color.y *= FastMath.exp((1 - absorptionColor.y) * absorption * -distance);
    color.z *= FastMath.exp((1 - absorptionColor.z) * absorption * -distance);
  }

  public boolean scatter(Ray2 ray, IntersectionRecord intersectionRecord, final Vector3 emittance, Random random) {
    boolean mediumChanged = false;
    boolean throughSurface = false;

    Vector3 direction;

    double n2 = ior;
    double n1 = ray.getCurrentMedium().ior;

    double pDiffuse = intersectionRecord.color.w * alpha;
    if (random.nextDouble() < specular) {
      direction = specularReflection(ray.d, intersectionRecord.shadeN);
      if (roughness > Constants.EPSILON) {
        Vector3 roughnessDirection = lambertianReflection(intersectionRecord.n, random);
        roughnessDirection.scale(roughness);
        roughnessDirection.scaleAdd(1 - roughness, direction);
        roughnessDirection.normalize();
        direction = roughnessDirection;
      }
      colorSpecular(intersectionRecord.color, metalness, specularColor, random);
      ray.flags |= Ray2.SPECULAR;

    } else if (FastMath.abs(n2 - n1) > Constants.EPSILON) {
      boolean front_face = ray.d.dot(intersectionRecord.shadeN) < 0.0;
      double ri = (front_face) ? (n1 / n2) : (n2 / n1);

      Vector3 unitDirection = ray.d.normalized();
      double cosTheta = FastMath.min(unitDirection.rScale(-1).dot(intersectionRecord.shadeN), 1.0);
      double sinTheta = FastMath.sqrt(1.0 - cosTheta * cosTheta);

      boolean cannotRefract = ri * sinTheta > 1.0;

      if (cannotRefract || schlickReflectance(cosTheta, ri) > random.nextDouble()) {
        direction = specularReflection(unitDirection, intersectionRecord.shadeN);
        double interfaceRoughness = FastMath.max(roughness, ray.getCurrentMedium().roughness);
        if (interfaceRoughness > Constants.EPSILON) {
          Vector3 roughnessDirection = lambertianReflection(intersectionRecord.n, random);
          roughnessDirection.scale(interfaceRoughness);
          roughnessDirection.scaleAdd(1 - interfaceRoughness, direction);
          roughnessDirection.normalize();
          direction = roughnessDirection;
        }
        colorSpecular(intersectionRecord.color, metalness, specularColor, random);
        ray.flags |= Ray2.SPECULAR;
      } else {
        if (random.nextDouble() < pDiffuse) {
          if (random.nextDouble() < subSurfaceScattering) {
            intersectionRecord.shadeN.scale(-1);
            intersectionRecord.n.scale(-1);
          }
          direction = lambertianReflection(intersectionRecord.shadeN, random);
          colorSpecular(intersectionRecord.color, 1, diffuseColor, random);
          ray.flags |= Ray2.DIFFUSE | Ray2.INDIRECT;
          emittance.set(this.emittance);
        } else {
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
          colorSpecular(intersectionRecord.color, transmissionMetalness, transmissionSpecularColor, random);
          ray.flags |= Ray2.SPECULAR;
          throughSurface = true;
        }
      }
    } else if (random.nextDouble() < pDiffuse) {
      if (random.nextDouble() < subSurfaceScattering) {
        intersectionRecord.shadeN.scale(-1);
        intersectionRecord.n.scale(-1);
        if ((intersectionRecord.flags & IntersectionRecord.NO_MEDIUM_CHANGE) == 0) {
          mediumChanged = true;
        }
      }
      direction = lambertianReflection(intersectionRecord.shadeN, random);
      colorSpecular(intersectionRecord.color, 1, diffuseColor, random);
      ray.flags |= Ray2.DIFFUSE | Ray2.INDIRECT;
      emittance.set(this.emittance);
    } else {
      direction = new Vector3(ray.d);
      colorSpecular(intersectionRecord.color, transmissionMetalness, transmissionSpecularColor, random);
      ray.flags |= Ray2.SPECULAR;
      throughSurface = true;
      if ((intersectionRecord.flags & IntersectionRecord.NO_MEDIUM_CHANGE) == 0) {
        mediumChanged = true;
      }
    }

    byte sign;
    if (throughSurface) {
      sign = -1;
    } else {
      sign = 1;
    }
    if (QuickMath.signum(intersectionRecord.n.dot(direction)) == sign * QuickMath.signum(intersectionRecord.n.dot(ray.d))) {
      double factor = QuickMath.signum(intersectionRecord.n.dot(ray.d)) * -Constants.EPSILON - direction.dot(intersectionRecord.n);
      direction.scaleAdd(factor, intersectionRecord.n);
      direction.normalize();
    }
    ray.d.set(direction);

    ray.o.scaleAdd(sign * Constants.OFFSET, intersectionRecord.n);

    return mediumChanged;
  }

  public void volumeScatter(Ray2 ray, Random random) {
    Vector3 invDir = ray.d.rScale(-1);
    Vector3 outDir = new Vector3();
    double x1 = random.nextDouble();
    double x2 = random.nextDouble();
    henyeyGreensteinSampleP(volumeAnisotropy, invDir, outDir, x1, x2);
    outDir.normalize();
    ray.d.set(outDir);
    ray.flags |= Ray2.INDIRECT;
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
    Vector3 direction = n.rAdd(Vector3.randomUnitVector(random));
    if (direction.nearZero()) {
      direction.set(n);
    } else {
      direction.normalize();
    }
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

  private static void colorSpecular(Vector4 color, float metalness, Vector3 colorModifier, Random random) {
    color.x = 1 - metalness * (1 - color.x);
    color.y = 1 - metalness * (1 - color.y);
    color.z = 1 - metalness * (1 - color.z);
    color.x *= colorModifier.x;
    color.y *= colorModifier.y;
    color.z *= colorModifier.z;
  }
}
