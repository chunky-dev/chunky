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
import se.llbit.math.Constants;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Ray2;
import se.llbit.math.Vector3;

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

  /**
   * The metalness value controls how metal-y a block appears. In reality this is a boolean value
   * but in practice usually a float is used in PBR to allow adding dirt or scratches on metals
   * without increasing the texture resolution.
   * Metals only do specular reflection for certain wavelengths (effectively tinting the reflection)
   * and have no diffuse reflection. The albedo color is used for tinting.
   */
  public float metalness = 0;

  /**
   * Subsurface scattering property.
   */
  public boolean subSurfaceScattering = false;

  /**
   * Base texture.
   */
  public final Texture texture;

  public boolean refractive = false;

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
    emittance = 0;
    roughness = 0;
    subSurfaceScattering = false;
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
    emittance = json.get("emittance").floatValue(emittance);
    roughness = json.get("roughness").floatValue(roughness);
    metalness = json.get("metalness").floatValue(metalness);
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

  public boolean setOutboundDir(Ray2 ray, IntersectionRecord intersectionRecord, Random random) {
    Material currentMat = intersectionRecord.material;
    Material prevMat = ray.getCurrentMedium();

    double pDiffuse = intersectionRecord.color.w;

    float pSpecular = currentMat.specular;
    float pMetal = currentMat.metalness;
    float roughness = currentMat.roughness;
    float n1 = prevMat.ior;
    float n2 = currentMat.ior;

    if (pDiffuse + pSpecular < Constants.EPSILON && n1 != n2) {
      ray.o.add(ray.d.x * Constants.OFFSET, ray.d.y * Constants.OFFSET, ray.d.z * Constants.OFFSET);
      return true;
    }

    if (pMetal > Constants.EPSILON && random.nextFloat() < pMetal) {
      ray.d.set(specularReflection(ray, intersectionRecord, random));
    } else if (pSpecular > Constants.EPSILON && random.nextFloat() < pSpecular) {
      ray.d.set(specularReflection(ray, intersectionRecord, random));
      intersectionRecord.color.set(1, 1, 1, 1);
    } else {
      ray.d.set(diffuseReflection(intersectionRecord, random));
    }
    return false;
  }

  private Vector3 randomHemisphereDir(Vector3 normal, Random random) {
    double x1 = random.nextDouble();
    double x2 = random.nextDouble();
    double r = FastMath.sqrt(x1);
    double theta = 2 * FastMath.PI * x2;

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

  private Vector3 specularReflection(Ray2 ray, IntersectionRecord intersectionRecord, Random random) {
    Vector3 direction = new Vector3(ray.d);
    Vector3 normal = new Vector3(intersectionRecord.shadeN);
    normal.scale(-2 * direction.dot(normal));
    direction.add(normal);

    if (intersectionRecord.material.roughness > Constants.EPSILON) {
      Vector3 randomHemisphereDir = randomHemisphereDir(intersectionRecord.shadeN, random);
      randomHemisphereDir.scale(intersectionRecord.material.roughness);
      direction.scale(1 - intersectionRecord.material.roughness);
      direction.add(randomHemisphereDir);
    }

    if (QuickMath.signum(intersectionRecord.n.dot(direction)) == QuickMath.signum(intersectionRecord.n.dot(ray.d))) {
      double factor = QuickMath.signum(intersectionRecord.n.dot(ray.d)) * -Constants.EPSILON - ray.d.dot(direction);
      direction.scaleAdd(factor, intersectionRecord.n);
    }
    direction.normalize();
    return direction;
  }
}
