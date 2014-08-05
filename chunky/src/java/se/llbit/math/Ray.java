/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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

import java.util.Random;

import org.apache.commons.math3.util.FastMath;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.world.Block;
import se.llbit.chunky.world.BlockData;

/**
 * The ray representation used for ray tracing.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Ray {

	/**
	 * EPSILON
	 */
	public static final double EPSILON = 0.000005;

	/**
	 * OFFSET
	 */
	public static final double OFFSET = 0.0001;

	/**
	 * Ray direction.
	 */
	public Vector3d d = new Vector3d();

	/**
	 * Intersection point.
	 */
	public Vector3d x = new Vector3d();

	/**
	 * Intersection normal.
	 */
	public Vector3d n = new Vector3d();

	/**
	 * Distance traveled in current medium.
	 */
	public double distance;

	/**
 	 * Accumulated color value.
 	 */
	public Vector4d color = new Vector4d();

	/**
 	 * Emittance of previously intersected surface.
 	 */
	public Vector3d emittance = new Vector3d();

	/**
	 * Previous material
	 */
	public int prevMaterial;

	/**
	 * Current material
	 */
	public int currentMaterial;

	/**
	 * Recursive ray depth
	 */
	public int depth;

	/**
	 * t variable
	 */
	public double t;

	/**
	 * tNear variable
	 */
	public double tNear;

	/**
	 * Texture coordinate
	 */
	public double u;

	/**
	 * Texture coordinate
	 */
	public double v;

	/**
	 * Is the ray specularly reflected
	 */
	public boolean specular;

	/**
 	 * <code>true</code> if the ray has intersected any surface.
 	 */
	public boolean hit;

	/**
 	 * Rays should be constructed using a ray pool.
 	 */
	private Ray() { }

	/**
	 * set default values
	 */
	public void setDefault() {
		distance = 0;
		prevMaterial = 0;
		currentMaterial = -1;
		depth = 0;
		hit = false;
		color.set(0, 0, 0, 0);
		emittance.set(0, 0, 0);
		specular = true;
	}

	/**
	 * Clone other ray
	 * @param other
	 */
	public void set(Ray other) {
		prevMaterial = other.prevMaterial;
		currentMaterial = other.currentMaterial;
		depth = other.depth+1;
		distance = 0;
		hit = false;
		x.set(other.x);
		d.set(other.d);
		n.set(other.n);
		color.set(0, 0, 0, 0);
		emittance.set(0, 0, 0);
		specular = other.specular;
	}

	/**
	 * A free list for ray objects.
	 */
	public static class RayPool {
		private int limit = 10;
		private int size = 0;
		private Ray[] pool = new Ray[limit];
		private static RayPool defaultInstance;

		/**
		 * @param other
		 * @return Next ray from free list
		 */
		public final Ray get(Ray other) {
			Ray ray;
			if (size == 0) {
				ray = new Ray();
			} else {
				ray = pool[--size];
			}
			ray.set(other);
			return ray;
		}

		/**
		 * @return Get default ray pool
		 */
		public static RayPool getDefaultRayPool() {
			if (defaultInstance == null) {
				defaultInstance = new RayPool();
			}
			return defaultInstance;
		}

		/**
		 * @return Next ray from free list
		 */
		public final Ray get() {
			Ray ray;
			if (size == 0) {
				ray = new Ray();
			} else {
				ray = pool[--size];
			}
			ray.setDefault();
			return ray;
		}

		/**
		 * Put ray back on the free list
		 * @param ray
		 */
		public final void dispose(Ray ray) {
			if (size == limit) {
				int newLimit = limit * 2;
				Ray[] newPool = new Ray[newLimit];
				System.arraycopy(pool, 0, newPool, 0, limit);
				limit = newLimit;
				pool = newPool;
			}
			pool[size++] = ray;
		}
	}


	/**
	 * @return Previous block
	 */
	public final Block getPrevBlock() {
		return Block.get(prevMaterial);
	}

	/**
	 * @return Current block
	 */
	public final Block getCurrentBlock() {
		return Block.get(currentMaterial);
	}

	/**
	 * @return Previous block type
	 */
	public final int getPrevBlockType() {
		return prevMaterial & 0xFF;
	}

	/**
	 * @return Current block type
	 */
	public final int getCurrentBlockType() {
		return currentMaterial & 0xFF;
	}

	/**
	 * The block data value is a 4-bit integer value describing
	 * properties of the current block.
	 * @return Current block data (sometimes called metadata)
	 */
	public final int getBlockData() {
		return 0xF & (currentMaterial >> BlockData.OFFSET);
	}

	/**
	 * Initialize a ray with origin and direction.
	 * @param o Origin
	 * @param d Direction
	 */
	public final void set(Vector3d o, Vector3d d) {
		setDefault();
		this.x.set(o);
		this.d.set(d);
	}

	/**
 	 * Find the exit point from the given block for this ray.
 	 * @param bx block x coordinate
 	 * @param by block y coordinate
 	 * @param bz block z coordinate
 	 */
	public final void exitBlock(int bx, int by, int bz) {
		int nx = 0;
		int ny = 0;
		int nz = 0;
		tNear = Double.POSITIVE_INFINITY;
		t = (bx - x.x) / d.x;
		if (t > Ray.EPSILON) {
			tNear = t;
			nx = 1;
			ny = nz = 0;
		} else {
			t = ((bx+1) - x.x) / d.x;
			if (t < tNear && t > Ray.EPSILON) {
				tNear = t;
				nx = -1;
				ny = nz = 0;
			}
		}

		t = (by - x.y) / d.y;
		if (t < tNear && t > Ray.EPSILON) {
			tNear = t;
			ny = 1;
			nx = nz = 0;
		} else {
			t = ((by+1) - x.y) / d.y;
			if (t < tNear && t > Ray.EPSILON) {
				tNear = t;
				ny = -1;
				nx = nz = 0;
			}
		}

		t = (bz - x.z) / d.z;
		if (t < tNear && t > Ray.EPSILON) {
			tNear = t;
			nz = 1;
			nx = ny = 0;
		} else {
			t = ((bz+1) - x.z) / d.z;
			if (t < tNear && t > Ray.EPSILON) {
				tNear = t;
				nz = -1;
				nx = ny = 0;
			}
		}

		x.scaleAdd(tNear, d, x);
		n.set(nx, ny, nz);
		distance += tNear;
	}

	/**
	 * @param scene
	 * @return Foliage color for the current block
	 */
	public float[] getBiomeFoliageColor(Scene scene) {
		return scene.getFoliageColor((int) (x.x + d.x * OFFSET), (int) (x.z + d.z * OFFSET));
	}

	/**
	 * @param scene
	 * @return Grass color for the current block
	 */
	public float[] getBiomeGrassColor(Scene scene) {
		return scene.getGrassColor((int) (x.x + d.x * OFFSET), (int) (x.z + d.z * OFFSET));
	}

	/**
	 * Set this ray to a random diffuse reflection of the input ray.
	 * @param ray
	 * @param random
	 */
	public final void diffuseReflection(Ray ray, Random random) {
		set(ray);

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

		r = 1/FastMath.sqrt(ux*ux + uy*uy + uz*uz);

		ux *= r;
		uy *= r;
		uz *= r;

		vx = uy * n.z - uz * n.y;
		vy = uz * n.x - ux * n.z;
		vz = ux * n.y - uy * n.x;

		d.x = ux * tx + vx * ty + n.x * tz;
		d.y = uy * tx + vy * ty + n.y * tz;
		d.z = uz * tx + vz * ty + n.z * tz;

		x.scaleAdd(Ray.OFFSET, d, x);
		currentMaterial = prevMaterial;
		specular = false;
	}

	/**
	 * Set this ray to the specular reflection of the input ray.
	 * @param ray
	 */
	public final void specularReflection(Ray ray) {
		set(ray);
		d.scaleAdd(
				- 2 * ray.d.dot(ray.n),
				ray.n, ray.d);
		x.scaleAdd(Ray.OFFSET, d, x);
		currentMaterial = prevMaterial;
	}

	/**
	 * Scatter ray normal
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

		r = 1/FastMath.sqrt(ux*ux + uy*uy + uz*uz);

		ux *= r;
		uy *= r;
		uz *= r;

		vx = uy * n.z - uz * n.y;
		vy = uz * n.x - ux * n.z;
		vz = ux * n.y - uy * n.x;

		n.set(ux * tx + vx * ty + n.x * tz,
				uy * tx + vy * ty + n.y * tz,
				uz * tx + vz * ty + n.z * tz);
	}

}
