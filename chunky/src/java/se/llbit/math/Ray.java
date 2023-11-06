/* Copyright (c) 2012-2014 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.math;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.block.minecraft.Lava;
import se.llbit.chunky.block.minecraft.Water;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.Sun;
import se.llbit.chunky.world.Material;

import java.util.Random;

/**
 * The ray representation used for ray tracing.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Ray {

  public static final double EPSILON = 0.00000005;

  public static final double OFFSET = 0.000001;

  /**
   * Ray direction.
   */
  public Vector3 d = new Vector3();

  /**
   * Intersection point.
   */
  public Vector3 o = new Vector3();

  /**
   * Intersection normal.
   */
  private Vector3 n = new Vector3();

  /**
   * Geometry normal, almost always the same as normal except when a normal map is used
   * This stays the real normal of the geometry
   */
  private Vector3 geomN = new Vector3();

  /**
   * Distance traveled in current medium. This is updated after all intersection tests have run and
   * the final t value has been found.
   */
  public double distance;

  /**
   * Accumulated color value.
   */
  public Vector4 color = new Vector4();

  /**
   * Previous material.
   */
  private Material prevMaterial = Air.INSTANCE;

  /**
   * Current material.
   */
  private Material currentMaterial = Air.INSTANCE;

  /**
   * Previous block metadata.
   */
  private int prevData;

  /**
   * Current block metadata.
   */
  private int currentData;

  /**
   * Recursive ray depth
   */
  public int depth;

  /**
   * Distance to closest intersection.
   */
  public double t;

  /**
   * Distance to next potential intersection. The tNext value is stored by subroutines when
   * calculating a potential next hit point. This can then be stored in the t variable based on
   * further decision making.
   */
  public double tNext;

  /**
   * Texture coordinate.
   */
  public double u;

  /**
   * Texture coordinate.
   */
  public double v;

  /**
   * Is the ray specularly reflected
   */
  public boolean specular;

  /**
   * Builds an uninitialized ray.
   */
  public Ray() {
  }

  /**
   * Create a copy of the given ray
   *
   * @param other ray to copy
   */
  public Ray(Ray other) {
    set(other);
  }

  /**
   * Set default values for this ray.
   */
  public void setDefault() {
    distance = 0;
    prevMaterial = Air.INSTANCE;
    currentMaterial = Air.INSTANCE;
    depth = 0;
    color.set(0, 0, 0, 0);
    specular = true;
  }

  /**
   * Copy state from another ray.
   */
  public void set(Ray other) {
    prevMaterial = other.prevMaterial;
    currentMaterial = other.currentMaterial;
    depth = other.depth + 1;
    distance = 0;
    o.set(other.o);
    d.set(other.d);
    n.set(other.n);
    geomN.set(other.geomN);
    color.set(0, 0, 0, 0);
    specular = other.specular;
  }

  /**
   * Initialize a ray with origin and direction.
   *
   * @param o origin
   * @param d direction
   */
  public final void set(Vector3 o, Vector3 d) {
    setDefault();
    this.o.set(o);
    this.d.set(d);
  }

  /**
   * Find the exit point from the given block for this ray. This marches the ray forward - i.e.
   * updates ray origin directly.
   *
   * @param bx block x coordinate
   * @param by block y coordinate
   * @param bz block z coordinate
   */
  public final void exitBlock(int bx, int by, int bz) {
    int nx = 0;
    int ny = 0;
    int nz = 0;
    double tNext = Double.POSITIVE_INFINITY;
    double t = (bx - o.x) / d.x;
    if (t > Ray.EPSILON) {
      tNext = t;
      nx = 1;
      ny = nz = 0;
    } else {
      t = ((bx + 1) - o.x) / d.x;
      if (t < tNext && t > Ray.EPSILON) {
        tNext = t;
        nx = -1;
        ny = nz = 0;
      }
    }

    t = (by - o.y) / d.y;
    if (t < tNext && t > Ray.EPSILON) {
      tNext = t;
      ny = 1;
      nx = nz = 0;
    } else {
      t = ((by + 1) - o.y) / d.y;
      if (t < tNext && t > Ray.EPSILON) {
        tNext = t;
        ny = -1;
        nx = nz = 0;
      }
    }

    t = (bz - o.z) / d.z;
    if (t < tNext && t > Ray.EPSILON) {
      tNext = t;
      nz = 1;
      nx = ny = 0;
    } else {
      t = ((bz + 1) - o.z) / d.z;
      if (t < tNext && t > Ray.EPSILON) {
        tNext = t;
        nz = -1;
        nx = ny = 0;
      }
    }

    o.scaleAdd(tNext, d);
    n.set(nx, ny, nz);
    geomN.set(nx, ny, nz);
    distance += tNext;
  }

  /**
   * @return foliage color for the current block
   */
  public float[] getBiomeFoliageColor(Scene scene) {
    return scene.getFoliageColor((int) (o.x + d.x * OFFSET), (int) (o.y + d.y * OFFSET), (int) (o.z + d.z * OFFSET));
  }

  /**
   * @return grass color for the current block
   */
  public float[] getBiomeGrassColor(Scene scene) {
    return scene.getGrassColor((int) (o.x + d.x * OFFSET), (int) (o.y + d.y * OFFSET), (int) (o.z + d.z * OFFSET));
  }

  /**
   * @return water color for the current block
   */
  public float[] getBiomeWaterColor(Scene scene) {
    return scene.getWaterColor((int) (o.x + d.x * OFFSET), (int) (o.y + d.y * OFFSET), (int) (o.z + d.z * OFFSET));
  }

  /**
   * Set this ray to a random diffuse reflection of the input ray.
   */
  public final void diffuseReflection(Ray ray, Random random, Scene scene) {

    set(ray);

    // get random point on unit disk
    double x1 = random.nextDouble();
    double x2 = random.nextDouble();
    double r = FastMath.sqrt(x1);
    double theta = 2 * Math.PI * x2;

    // project to point on hemisphere in tangent space
    double tx = r * FastMath.cos(theta);
    double ty = r * FastMath.sin(theta);
    double tz; // to be initialized later, after potentially changing tx and ty

    // importance sampling, see PR #1604
    // https://github.com/chunky-dev/chunky/pull/1604/
    if(scene.getSunSamplingStrategy().isImportanceSampling()) {

      // constants
      final double sun_az = scene.sun().getAzimuth();
      final double sun_alt = scene.sun().getAltitude();
      final double sun_dx = FastMath.cos(sun_az)*FastMath.cos(sun_alt);
      final double sun_dz = FastMath.sin(sun_az)*FastMath.cos(sun_alt);
      final double sun_dy = FastMath.sin(sun_alt);

      // determine the sun direction in tangent space
      // since we know the sun's direction in world space easily, we must reverse the algebra done later in this method
      // (I calculated the inverse matrix by hand and it was not fun)
      double sun_tx, sun_ty, sqrt;
      double sun_tz = sun_dx*n.x + sun_dy*n.y + sun_dz*n.z;
      if(QuickMath.abs(n.x) > .1) {
        sun_tx = sun_dx * n.z - sun_dz * n.x;
        sun_ty = sun_dx * n.x * n.y - sun_dy * (n.x * n.x + n.z * n.z) + sun_dz * n.y * n.z;
        sqrt = FastMath.hypot(n.x, n.z);
      } else {
        sun_tx = sun_dz * n.y - sun_dy * n.z;
        sun_ty = sun_dy * n.x * n.y - sun_dx * (n.y * n.y + n.z * n.z) + sun_dz * n.x * n.z;
        sqrt = FastMath.hypot(n.z, n.y);
      }
      sun_tx /= sqrt;
      sun_ty /= sqrt;
      double circle_radius = scene.sun().getSunRadius() * scene.sun().getDiffuseSampleRadius();
      double sample_chance = scene.sun().getDiffuseSampleChance();
      double sun_alt_relative = FastMath.asin(sun_tz);
      // check if there is any chance of the sun being visible
      if(sun_alt_relative + circle_radius > Ray.EPSILON) {
        // if the sun is not at too shallow of an angle, then sample a circular region
        if(FastMath.hypot(sun_tx, sun_ty) + circle_radius + Ray.EPSILON < 1) {
          if (random.nextDouble() < sample_chance) {
            // sun sampling
            tx = sun_tx + tx * circle_radius;
            ty = sun_ty + ty * circle_radius;
            // diminish the contribution of the ray based on the circle area and the sample chance
            ray.color.scale(circle_radius * circle_radius / sample_chance);
          } else {
            // non-sun sampling
            // now, rather than guaranteeing that the ray is cast within a circle, instead guarantee that it does not
            while (FastMath.hypot(tx - sun_tx, ty - sun_ty) < circle_radius) {
              tx -= sun_tx;
              ty -= sun_ty;
              // avoid very unlikely infinite loop
              if (tx == 0 && ty == 0) {
                break;
              }
              tx /= circle_radius;
              ty /= circle_radius;
            }
            // correct for the fact that we are now undersampling everything but the sun
            ray.color.scale((1 - circle_radius * circle_radius) / (1 - sample_chance));
          }
        } else {
          // the sun is at a shallow angle, so instead we're using a "rectangular-ish segment"
          // it is important that we sample from a shape which we can easily calculate the area of
          double minr = FastMath.cos(sun_alt_relative + circle_radius);
          double maxr = FastMath.cos(FastMath.max(sun_alt_relative - circle_radius, 0));
          double sun_theta = FastMath.atan2(sun_ty, sun_tx);
          double segment_area_proportion = ((maxr * maxr - minr * minr) * circle_radius) / Math.PI;
          sample_chance *= segment_area_proportion / (circle_radius * circle_radius);
          sample_chance = FastMath.min(sample_chance, Sun.MAX_DIFFUSE_SAMPLE_CHANCE);
          if(random.nextDouble() < sample_chance) {
            // sun sampling
            r = FastMath.sqrt(minr * minr * x1 + maxr * maxr * (1 - x1));
            theta = sun_theta + (2 * x2 - 1) * circle_radius;
            tx = r * FastMath.cos(theta);
            ty = r * FastMath.sin(theta);
            // diminish the contribution of the ray based on the segment area and the sample chance
            ray.color.scale(segment_area_proportion / sample_chance);
          } else {
            // non-sun sampling
            // basically, if we are going to sample the sun segment, reset the rng until we don't
            while(r > minr || FastMath.abs(theta - sun_theta) < circle_radius) {
              x1 = random.nextDouble();
              x2 = random.nextDouble();
              r = FastMath.sqrt(x1);
              theta = 2 * Math.PI * x2;
            }
            tx = r * FastMath.cos(theta);
            ty = r * FastMath.sin(theta);
            // correct for the fact that we are now undersampling everything but the sun
            ray.color.scale((1 - segment_area_proportion) / (1 - sample_chance));
          }
        }
      }
    }

    tz = FastMath.sqrt(1 - tx*tx - ty*ty);

    // transform from tangent space to world space
    double xx, xy, xz;
    double ux, uy, uz;
    double vx, vy, vz;

    if (QuickMath.abs(n.x) > .1) {
      xx = 0;
      xy = 1;
      xz = 0;
    } else {
      xx = 1;
      xy = 0;
      xz = 0;
    }

    ux = xy * n.z - xz * n.y;
    uy = xz * n.x - xx * n.z;
    uz = xx * n.y - xy * n.x;

    r = 1 / FastMath.sqrt(ux * ux + uy * uy + uz * uz);

    ux *= r;
    uy *= r;
    uz *= r;

    vx = uy * n.z - uz * n.y;
    vy = uz * n.x - ux * n.z;
    vz = ux * n.y - uy * n.x;

    d.x = ux * tx + vx * ty + n.x * tz;
    d.y = uy * tx + vy * ty + n.y * tz;
    d.z = uz * tx + vz * ty + n.z * tz;

    o.scaleAdd(Ray.OFFSET, d);
    currentMaterial = prevMaterial;
    specular = false;

    // See specularReflection for explanation of why this is needed
    if(QuickMath.signum(geomN.dot(d)) == QuickMath.signum(geomN.dot(ray.d))) {
      double factor = QuickMath.signum(geomN.dot(ray.d)) * -Ray.EPSILON - d.dot(geomN);
      d.scaleAdd(factor, geomN);
      d.normalize();
    }
  }

  /**
   * Set this ray to the specular reflection of the input ray.
   */
  public final void specularReflection(Ray ray, Random random) {
    set(ray);
    currentMaterial = prevMaterial;

    double roughness = ray.getCurrentMaterial().roughness;
    if (roughness > Ray.EPSILON) {
      // For rough specular reflections, we interpolate linearly between the diffuse ray direction and the specular direction,
      // which is inspired by https://blog.demofox.org/2020/06/06/casual-shadertoy-path-tracing-2-image-improvement-and-glossy-reflections/
      // This gives good-looking results, although a microfacet-based model would be more physically correct.

      // 1. get specular reflection direction
      Vector3 specularDirection = new Vector3(d);
      specularDirection.scaleAdd(-2 * ray.d.dot(ray.n), ray.n, ray.d);

      // 2. get diffuse reflection direction (stored in this.d)
      // get random point on unit disk
      double x1 = random.nextDouble();
      double x2 = random.nextDouble();
      double r = FastMath.sqrt(x1);
      double theta = 2 * Math.PI * x2;

      // project to point on hemisphere in tangent space
      double tx = r * FastMath.cos(theta);
      double ty = r * FastMath.sin(theta);
      double tz = FastMath.sqrt(1 - x1);

      // transform from tangent space to world space
      double xx, xy, xz;
      double ux, uy, uz;
      double vx, vy, vz;

      if (QuickMath.abs(n.x) > .1) {
        xx = 0;
        xy = 1;
        xz = 0;
      } else {
        xx = 1;
        xy = 0;
        xz = 0;
      }

      ux = xy * n.z - xz * n.y;
      uy = xz * n.x - xx * n.z;
      uz = xx * n.y - xy * n.x;

      r = 1 / FastMath.sqrt(ux * ux + uy * uy + uz * uz);

      ux *= r;
      uy *= r;
      uz *= r;

      vx = uy * n.z - uz * n.y;
      vy = uz * n.x - ux * n.z;
      vz = ux * n.y - uy * n.x;

      d.x = ux * tx + vx * ty + n.x * tz;
      d.y = uy * tx + vy * ty + n.y * tz;
      d.z = uz * tx + vz * ty + n.z * tz;

      // 3. scale d to be roughness * dDiffuse + (1 - roughness) * dSpecular
      d.scale(roughness);
      d.scaleAdd(1 - roughness, specularDirection);
      d.normalize();
      o.scaleAdd(0.00001, d);
    } else {
      // roughness is zero, do a specular reflection
      d.scaleAdd(-2 * ray.d.dot(ray.n), ray.n, ray.d);
      o.scaleAdd(0.00001, ray.n);
    }

    // After reflection, the dot product between the direction and the real surface normal
    // should have the opposite sign as the dot product between the incoming direction
    // and the normal (because the incoming is going toward the volume enclosed
    // by the surface and the reflected ray is going away)
    // If this is not the case, we need to fix that
    if(QuickMath.signum(geomN.dot(d)) == QuickMath.signum(geomN.dot(ray.d))) {
      // The reflected ray goes is going through the geometry,
      // we need to alter its direction so it doesn't.
      // The way we do that is by adding the geometry normal multiplied by some factor
      // The factor can be determined by projecting the direction on the normal,
      // ie doing a dot product because, for every unit vector d and n,
      // we have the relation:
      // `(d - d.n * n) . n = 0`
      // This tells us that if we chose `-d.n` as the factor we would have a dot product
      // equals to 0, as we want something positive or negative,
      // we will use the factor `-d.n +/- epsilon`
      double factor = QuickMath.signum(geomN.dot(ray.d)) * -Ray.EPSILON - d.dot(geomN);
      d.scaleAdd(factor, geomN);
      d.normalize();
    }
  }

  /**
   * Scatter ray normal
   *
   * @param random random number source
   */
  public final void scatterNormal(Random random) {
    // get random point on unit disk
    double x1 = random.nextDouble();
    double x2 = random.nextDouble();
    double r = FastMath.sqrt(x1);
    double theta = 2 * Math.PI * x2;

    // project to point on hemisphere in tangent space
    double tx = r * FastMath.cos(theta);
    double ty = r * FastMath.sin(theta);
    double tz = FastMath.sqrt(1 - x1);

    // transform from tangent space to world space
    double xx, xy, xz;
    double ux, uy, uz;
    double vx, vy, vz;

    if (QuickMath.abs(n.x) > .1) {
      xx = 0;
      xy = 1;
      xz = 0;
    } else {
      xx = 1;
      xy = 0;
      xz = 0;
    }

    ux = xy * n.z - xz * n.y;
    uy = xz * n.x - xx * n.z;
    uz = xx * n.y - xy * n.x;

    r = 1 / FastMath.sqrt(ux * ux + uy * uy + uz * uz);

    ux *= r;
    uy *= r;
    uz *= r;

    vx = uy * n.z - uz * n.y;
    vy = uz * n.x - ux * n.z;
    vz = ux * n.y - uy * n.x;

    n.set(ux * tx + vx * ty + n.x * tz, uy * tx + vy * ty + n.y * tz, uz * tx + vz * ty + n.z * tz);
  }

  public void setPrevMaterial(Material mat, int data) {
    this.prevMaterial = mat;
    this.prevData = data;
  }

  public void setCurrentMaterial(Material mat) {
    this.currentMaterial = mat;
    if (mat instanceof Water) {
      this.currentData = ((Water) mat).data;
    } else if (mat instanceof Lava) {
      this.currentData = ((Lava) mat).data;
    } else {
      this.currentData = 0;
    }
  }

  public void setCurrentMaterial(Material mat, int data) {
    this.currentMaterial = mat;
    this.currentData = data;
  }

  public Material getPrevMaterial() {
    return prevMaterial;
  }

  public Material getCurrentMaterial() {
    return currentMaterial;
  }

  /**
   * Get the data of the previous block. This used to contain the block data but as of Chunky 2,
   * every block variant gets its own Block instance and this field is only used for water and lava
   * levels.
   *
   * @return Data of the previous block (if water or lava), 0 otherwise
   */
  public int getPrevData() {
    return prevData;
  }

  /**
   * Get the data of the current block. This used to contain the block data but as of Chunky 2,
   * every block variant gets its own Block instance and this field is only used for water and lava
   * levels.
   *
   * @return Data of the current block (if water or lava), 0 otherwise
   */
  public int getCurrentData() {
    return currentData;
  }

  /**
   * Get the normal of the previously hit surface.
   */
  public Vector3 getNormal() {
    return n;
  }
  /**
   * Get the geometric normal of the previously hit surface. When using normal maps,
   * this is the normal of the geometry that was hit, not taking the normal map into account.
   */
  public Vector3 getGeometryNormal() {
    return geomN;
  }

  /**
   * Set the geometry normal (not taking normal mapping into account)
   * and the shading normal (taking normal mapping into account)
   */
  public void setNormal(double x, double y, double z) {
    n.set(x, y, z);
    geomN.set(x, y, z);
  }

  /**
   * Set the geometry normal (not taking normal mapping into account)
   * and the shading normal (taking normal mapping into account)
   */
  public void setNormal(Vector3 newN) {
    n.set(newN);
    geomN.set(newN);
  }

  /**
   * Sets n to the given value and optionally flip it so the normal
   * is in the opposite direction as the ray direction (dot(d, n) < 0)
   */
  public void orientNormal(Vector3 normal) {
    if(d.dot(normal) > 0) {
      n.set(-normal.x, -normal.y, -normal.z);
    } else {
      n.set(normal);
    }
    geomN.set(n);
  }

  /**
   * Set the shading normal (taking normal mapping into account)
   */
  public void setShadingNormal(double x, double y, double z) {
    n.set(x, y, z);
  }

  public void invertNormal() {
    n.scale(-1);
    geomN.scale(-1);
  }
}
