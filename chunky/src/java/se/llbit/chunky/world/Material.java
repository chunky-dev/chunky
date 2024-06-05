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
  public float specular = 0;

  /**
   * The amount of light the material emits.
   */
  public float emittance = 0;

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
  public float metalness = 0;

  public Vector3 specularColor = new Vector3(1, 1, 1);

  /**
   * Texture alpha multiplier.
   */
  public float alpha = 1f;

  /**
   * Subsurface scattering property.
   */
  public boolean subSurfaceScattering = false;

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
    specular = 0;
    metalness = 0;
    emittance = 0;
    roughness = 0;
    transmissionRoughness = 0;
    subSurfaceScattering = false;
    alpha = 1;
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
    emittance = json.get("emittance").floatValue(emittance);
    roughness = json.get("roughness").floatValue(roughness);
    transmissionRoughness = json.get("transmissionRoughness").floatValue(transmissionRoughness);
    alpha = json.get("alpha").floatValue(alpha);
  }

  public JsonObject saveMaterialProperties() {
    JsonObject properties = new JsonObject();
    properties.add("ior", ior);
    properties.add("specular", specular);
    properties.add("metalness", metalness);
    properties.add("emittance", emittance);
    properties.add("roughness", roughness);
    properties.add("transmissionRoughness", transmissionRoughness);
    properties.add("alpha", alpha);
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

  public boolean scatter(Ray2 ray, IntersectionRecord intersectionRecord, Random random) {

    boolean mediumChanged = false;
    boolean emittance = false;

    Vector3 direction;

    double n2 = intersectionRecord.material.ior;
    double n1 = ray.getCurrentMedium().ior;

    double pDiffuse = intersectionRecord.color.w * alpha;

    if (random.nextDouble() < specular) {

      direction = specularReflection(ray.d, intersectionRecord.shadeN);
      colorSpecular(intersectionRecord.color, random);
    } else if (FastMath.abs(n2 - n1) > Constants.EPSILON) {

      boolean front_face = ray.d.dot(intersectionRecord.shadeN) < 0.0;
      double ri = (front_face) ? (n1 / n2) : (n2 / n1);

      Vector3 unitDirection = ray.d.normalized();
      double cosTheta = FastMath.min(unitDirection.rScale(-1).dot(intersectionRecord.shadeN), 1.0);
      double sinTheta = FastMath.sqrt(1.0 - cosTheta * cosTheta);

      boolean cannotRefract = ri * sinTheta > 1.0;

      if (cannotRefract || schlickReflectance(cosTheta, ri) > random.nextDouble()) {
        direction = specularReflection(unitDirection, intersectionRecord.shadeN);
        colorSpecular(intersectionRecord.color, random);
      } else {
        if (random.nextDouble() < pDiffuse) {
          direction = lambertianReflection(intersectionRecord.shadeN, random);
          emittance = true;
        } else {
          direction = specularRefraction(unitDirection, intersectionRecord.shadeN, ri);
          colorSpecular(intersectionRecord.color, random);
          mediumChanged = true;
        }
      }
    } else if (random.nextDouble() < pDiffuse) {

      direction = lambertianReflection(intersectionRecord.shadeN, random);
      emittance = true;
    } else {
      //TODO fix
      direction = new Vector3(ray.d);
      mediumChanged = true;
    }

    int sign;
    if (mediumChanged) {
      sign = -1;
      ray.setCurrentMedium(this);
    } else {
      sign = 1;
    }
    if (QuickMath.signum(intersectionRecord.n.dot(direction)) == sign * QuickMath.signum(intersectionRecord.n.dot(ray.d))) {
      double factor = QuickMath.signum(intersectionRecord.n.dot(ray.d)) * -Constants.EPSILON - direction.dot(intersectionRecord.n);
      direction.scaleAdd(factor, intersectionRecord.n);
      direction.normalize();
    }
    ray.d.set(direction);

    //TODO quad model normals are inverted

    ray.o.scaleAdd(sign * Constants.OFFSET, intersectionRecord.n);

    return emittance;

    /*switch (materialType) {
      case LAMBERTIAN: {
        if (intersectionRecord.color.w < Constants.EPSILON) {
          ray.o.scaleAdd(-Constants.OFFSET, intersectionRecord.n);
          return false;
        }

        direction = lambertianReflection(intersectionRecord.shadeN, random);
        break;
      }

      case DIELECTRIC: {
        double n2 = intersectionRecord.material.ior;
        double n1 = ray.getCurrentMedium().ior;
        double ri = (intersectionRecord.shadeN.dot(ray.d) < 0) ? (n1 / n2) : (n2 / n1);

        Vector3 unitDirection = ray.d.normalized();
        double cosTheta = FastMath.min(unitDirection.rScale(-1).dot(intersectionRecord.shadeN), 1.0);
        double sinTheta = FastMath.sqrt(1.0 - cosTheta * cosTheta);

        boolean cannotRefract = ri * sinTheta > 1.0;

        if (cannotRefract || schlickReflectance(cosTheta, ri) > random.nextDouble()) {
          direction = specularReflection(unitDirection, intersectionRecord.shadeN);
          colorSpecular(intersectionRecord.color, random);
          emittance = false;
        } else {
          if (random.nextDouble() < intersectionRecord.color.w) {
            direction = lambertianReflection(intersectionRecord.shadeN, random);
          } else {
            direction = specularRefraction(unitDirection, intersectionRecord.shadeN, ri);
            colorSpecular(intersectionRecord.color, random);
            emittance = false;
            mediumChanged = true;
          }
        }

        break;
      }

      case METAL: {
        direction = specularReflection(ray.d, intersectionRecord.shadeN);
        if (roughness > Constants.EPSILON) {
          Vector3 randomDir = randomHemisphereDir(intersectionRecord.shadeN, random);
          randomDir.scale(roughness);
          randomDir.scaleAdd(1 - roughness, direction);
          randomDir.normalize();
          direction.set(randomDir);
        }

        if (QuickMath.signum(intersectionRecord.n.dot(direction)) == QuickMath.signum(intersectionRecord.n.dot(ray.d))) {
          double factor = QuickMath.signum(intersectionRecord.n.dot(ray.d)) * -Constants.EPSILON - direction.dot(intersectionRecord.n);
          direction.scaleAdd(factor, intersectionRecord.n);
          direction.normalize();
        }

        break;
      }
    }

    int sign;
    if (mediumChanged) {
      sign = -1;
      ray.setCurrentMedium(this);
    } else {
      sign = 1;
    }
    if (QuickMath.signum(intersectionRecord.n.dot(direction)) == sign * QuickMath.signum(intersectionRecord.n.dot(ray.d))) {
      double factor = QuickMath.signum(intersectionRecord.n.dot(ray.d)) * -Constants.EPSILON - direction.dot(intersectionRecord.n);
      direction.scaleAdd(factor, intersectionRecord.n);
      direction.normalize();
    }
    ray.d.set(direction);

    int sign2 = QuickMath.signum(ray.d.dot(intersectionRecord.n));
    ray.o.scaleAdd(sign * sign2 * Constants.OFFSET, intersectionRecord.n);

    return emittance;

    /*Material currentMat = this;
    Material prevMat = ray.getCurrentMedium();

    double pDiffuse = intersectionRecord.color.w;

    float pSpecular = currentMat.specular;
    float pMetal = currentMat.metalness;
    float roughness = Math.max(currentMat.roughness, prevMat.roughness);
    float transmissionRoughness = Math.max(currentMat.transmissionRoughness, prevMat.transmissionRoughness);
    float n1 = prevMat.ior;
    float n2 = currentMat.ior;

    Vector3 invDir = new Vector3(ray.d);
    invDir.scale(-1);

//    if (pDiffuse + pSpecular < Constants.EPSILON){// && (n1 - n2 <= DEFAULT_IOR)) {
//      ray.o.scaleAdd(Constants.OFFSET, ray.d);
//      if (currentMat.refractive) {
//        ray.setCurrentMedium(currentMat);
//        if (currentMat == Air.INSTANCE) {
//          intersectionRecord.color.set(1, 0.5, 0.5, 0);
//        }
//      }
//      return true;
//    }

    boolean doMetal = pMetal > Constants.EPSILON && random.nextFloat() < pMetal;

    if (doMetal || pSpecular > Constants.EPSILON && random.nextFloat() < pSpecular) {
      double sign = QuickMath.signum(ray.d.dot(intersectionRecord.n));
      ray.o.scaleAdd(-sign * Constants.OFFSET, intersectionRecord.n);

      if (!doMetal) {
        intersectionRecord.color.set(1, 1, 1, 1);
      }

      Vector3 direction = specularReflection(invDir, intersectionRecord.shadeN);

      if (QuickMath.signum(intersectionRecord.n.dot(direction)) == QuickMath.signum(intersectionRecord.n.dot(ray.d))) {
        double factor = QuickMath.signum(intersectionRecord.n.dot(ray.d)) * -Constants.EPSILON - ray.d.dot(direction);
        direction.scaleAdd(factor, intersectionRecord.n);
      }
      direction.normalize();

      ray.d.set(direction);

    } else if (pDiffuse > Constants.EPSILON && random.nextFloat() < pDiffuse) {
      ray.o.scaleAdd(Constants.OFFSET, intersectionRecord.n);
      ray.d.set(diffuseReflection(intersectionRecord, random));

    } else if (Math.abs(n1 - n2) > DEFAULT_IOR - 1) {
      double sign = QuickMath.signum(ray.d.dot(intersectionRecord.n));

      double eta = n2 / n1;
      if (!(pMetal > Constants.EPSILON && random.nextFloat() < pMetal)) {
        intersectionRecord.color.set(1, 1, 1, 0);
      }

      Vector3 direction = new Vector3();

      double cosTheta_i = ray.d.dot(intersectionRecord.shadeN);
      double factor = FrDielectric(cosTheta_i, eta);
      if (specularRefraction(invDir, intersectionRecord.shadeN, eta, direction)) {
        if (random.nextDouble() < factor) {
          ray.o.scaleAdd(sign * Constants.OFFSET, intersectionRecord.n);
          ray.d.set(direction);
          intersectionRecord.color.scale(1 - factor);
          ray.setCurrentMedium(this);
        } else {
          ray.o.scaleAdd(-sign * Constants.OFFSET, intersectionRecord.n);
          ray.d.set(specularReflection(invDir, intersectionRecord.shadeN));
          intersectionRecord.color.scale(factor);
        }

      } else {
        ray.o.scaleAdd(-sign * Constants.OFFSET, intersectionRecord.n);
        ray.d.set(specularReflection(invDir, intersectionRecord.shadeN));
        intersectionRecord.color.scale(factor);
      }

    } else if (pDiffuse > Constants.EPSILON) {
      double sign = QuickMath.signum(ray.d.dot(intersectionRecord.n));
      ray.o.scaleAdd(sign * Constants.OFFSET, intersectionRecord.n);

    } else {
      double sign = QuickMath.signum(ray.d.dot(intersectionRecord.n));
      ray.o.scaleAdd(sign * Constants.OFFSET, intersectionRecord.n);
      intersectionRecord.color.set(1, 1, 1, 0);
    }
    return false;*/
  }

  private static boolean shouldRefract(Vector3 wi, Vector3 normal, double eta, Vector3 wt) {
    double cosTheta_i = normal.dot(wi);
    if (cosTheta_i < 0) {
      eta = 1 / eta;
      cosTheta_i = -cosTheta_i;
      normal.scale(-1);
    }
    double sin2Theta_i = Math.max(0, 1 - cosTheta_i * cosTheta_i);
    double sin2Theta_t = sin2Theta_i / (eta * eta);
    if (sin2Theta_t >= 1) {
      return false;
    }
    double cosTheta_t = Math.sqrt(Math.max(0.0, 1 - sin2Theta_t));
    wt.x = -wi.x / eta + (cosTheta_i / eta - cosTheta_t) * normal.x;
    wt.y = -wi.y / eta + (cosTheta_i / eta - cosTheta_t) * normal.y;
    wt.z = -wi.z / eta + (cosTheta_i / eta - cosTheta_t) * normal.z;
    return true;
  }

  private static double FrDielectric(double cosTheta_i, double eta) {
    cosTheta_i = QuickMath.clamp(cosTheta_i, -1, 1);
    if (cosTheta_i < 0) {
      eta = 1 / eta;
      cosTheta_i = -cosTheta_i;
    }

    double sin2Theta_i = 1 - cosTheta_i * cosTheta_i;
    double sin2Theta_t = sin2Theta_i / (eta * eta);
    if (sin2Theta_t >= 1) {
      return 1.0;
    }
    double cosTheta_t = Math.sqrt(Math.max(0.0, 1 - sin2Theta_t));

    double r_parl = (eta * cosTheta_i - cosTheta_t) / (eta * cosTheta_i + cosTheta_t);
    double r_perp = (cosTheta_i - eta * cosTheta_t) / (cosTheta_i + eta * cosTheta_t);
    return (r_parl * r_parl + r_perp * r_perp) / 2;
  }

  private Vector3 randomHemisphereDir(Vector3 normal, Random random) {
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

  private Vector3 diffuseReflection(IntersectionRecord intersectionRecord, Random random) {
    return randomHemisphereDir(intersectionRecord.shadeN, random);
  }

  private Vector3 lambertianReflection(Vector3 n, Random random) {
    Vector3 direction = n.rAdd(Vector3.randomUnitVector(random));
    if (direction.nearZero()) {
      direction.set(n);
    } else {
      direction.normalize();
    }
    return direction;
  }

  private Vector3 specularReflection(Vector3 v, Vector3 n) {
    return v.rSub(n.rScale(2 * v.dot(n)));
  }

  private static double schlickReflectance(double cosine, double refractionIndex) {
    double r0 = (1 - refractionIndex) / (1 + refractionIndex);
    r0 = r0 * r0;
    return r0 + (1 - r0) * FastMath.pow((1 - cosine), 5);
  }

  private void colorSpecular(Vector4 color, Random random) {
    if (random.nextDouble() > metalness) {
      color.set(1, 1, 1, color.w);
    }
    color.x *= specularColor.x;
    color.y *= specularColor.y;
    color.z *= specularColor.z;
  }

  private Vector3 specularRefraction(Vector3 uv, Vector3 n, double etaiOverEtat) {
    double cosTheta = FastMath.min(uv.rScale(-1).dot(n), 1.0);
    Vector3 rOutPerp = n.rScale(cosTheta).rAdd(uv).rScale(etaiOverEtat);
    Vector3 rOutParallel = n.rScale(-FastMath.sqrt(FastMath.abs(1.0 - rOutPerp.lengthSquared())));
    return rOutPerp.rAdd(rOutParallel);
  }

  private boolean specularRefraction(Vector3 wi, Vector3 n, double eta, Vector3 wt) {
    double cosTheta_i =  wi.dot(n);

    if (cosTheta_i < 0) {
      eta = 1 / eta;
      cosTheta_i = -cosTheta_i;
      n.scale(-1);
    }

    double sin2Theta_i = FastMath.max(0, 1 - cosTheta_i * cosTheta_i);
    double sin2Theta_t = sin2Theta_i / (eta * eta);

    if (sin2Theta_t >= 1) {
      return false;
    }

    double cosTheta_t = FastMath.sqrt(FastMath.max(0, 1 - sin2Theta_t));

    Vector3 n2 = new Vector3(n);
    Vector3 wi2 = new Vector3(wi);

    n2.scale(eta + (cosTheta_i / eta - cosTheta_t));

    wi2.scale(-1);
    wi2.scale(1 / eta);

    wt.add(n2, wi2);

    return true;

    //    double sign = QuickMath.signum(ray.d.dot(intersectionRecord.n));
//    Vector3 invDir = new Vector3(-ray.d.x, -ray.d.y, -ray.d.z);
//    Vector3 direction = new Vector3();
//    double factor = FrDielectric(-ray.d.dot(intersectionRecord.shadeN), eta);
//    if (!shouldRefract(invDir, intersectionRecord.shadeN, eta, direction)) {
//      ray.o.scaleAdd(-sign * Constants.OFFSET, intersectionRecord.n);
//      direction.set(specularReflection(ray, intersectionRecord, random, roughness));
//    } else {
//      if (random.nextDouble() > factor) {
//        ray.o.scaleAdd(-sign * Constants.OFFSET, intersectionRecord.n);
//        direction.set(specularReflection(ray, intersectionRecord, random, roughness));
//        intersectionRecord.color.scale(factor);
//      } else {
//        ray.o.scaleAdd(sign * Constants.OFFSET, intersectionRecord.n);
//        intersectionRecord.color.scale(1 - factor);
//        ray.setCurrentMedium(this);
//      }
//    }
//    ray.d.set(direction);
  }
}
