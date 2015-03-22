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

import java.util.Random;

import org.apache.commons.math3.util.FastMath;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.world.Block;
import se.llbit.chunky.world.BlockData;
import se.llbit.chunky.world.Material;

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
	public Vector3d o = new Vector3d();

	/**
	 * Intersection normal.
	 */
	public Vector3d n = new Vector3d();

	/**
	 * Distance traveled in current medium. This is updated after all intersection
	 * tests have run and the final t value has been found.
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
	private Material prevMaterial = Block.AIR;

	/**
	 * Current material
	 */
	private Material currentMaterial = Block.AIR;

	/**
	 * Previous block metadata;
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
	 * Distance to next potential intersection.
	 * The tNext value is stored by subroutines when calculating a potential
	 * next hit point. This can then be stored in the t variable based on
	 * further decision making.
	 */
	public double tNext;

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
 	 * Creat an uninitialized ray.
 	 */
	public Ray() {
	}

	/**
 	 * Create a copy of the given ray
 	 * @param other ray to copy
 	 */
	public Ray(Ray other) {
		set(other);
	}

	/**
	 * set default values
	 */
	public void setDefault() {
		distance = 0;
		prevMaterial = Block.AIR;
		currentMaterial = Block.AIR;
		depth = 0;
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
		o.set(other.o);
		d.set(other.d);
		n.set(other.n);
		color.set(0, 0, 0, 0);
		emittance.set(0, 0, 0);
		specular = other.specular;
	}

	/**
	 * The block data value is a 4-bit integer value describing
	 * properties of the current block.
	 * @return Current block data (sometimes called metadata)
	 */
	public final int getBlockData() {
		return 0xF & (currentData >> BlockData.OFFSET);
	}

	/**
	 * Initialize a ray with origin and direction.
	 * @param o Origin
	 * @param d Direction
	 */
	public final void set(Vector3d o, Vector3d d) {
		setDefault();
		this.o.set(o);
		this.d.set(d);
	}

	/**
 	 * Find the exit point from the given block for this ray.
	 * This marches the ray forward - i.e. updates ray origin directly.
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
			t = ((bx+1) - o.x) / d.x;
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
			t = ((by+1) - o.y) / d.y;
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
			t = ((bz+1) - o.z) / d.z;
			if (t < tNext && t > Ray.EPSILON) {
				tNext = t;
				nz = -1;
				nx = ny = 0;
			}
		}

		o.scaleAdd(tNext, d);
		n.set(nx, ny, nz);
		distance += tNext;
	}

	/**
	 * @param scene
	 * @return Foliage color for the current block
	 */
	public float[] getBiomeFoliageColor(Scene scene) {
		return scene.getFoliageColor((int) (o.x + d.x * OFFSET), (int) (o.z + d.z * OFFSET));
	}

	/**
	 * @param scene
	 * @return Grass color for the current block
	 */
	public float[] getBiomeGrassColor(Scene scene) {
		return scene.getGrassColor((int) (o.x + d.x * OFFSET), (int) (o.z + d.z * OFFSET));
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
		double tx = r * QuickMath.cos(theta);
		double ty = r * QuickMath.sin(theta);
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

		o.scaleAdd(Ray.OFFSET, d);
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
		o.scaleAdd(0.00001, ray.n);
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
		double tx = r * QuickMath.cos(theta);
		double ty = r * QuickMath.sin(theta);
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

	public void setPrevMat(Material mat, int data) {
		this.prevMaterial = mat;
		this.prevData = data;
	}

	public void setPrevMat(int blockId) {
		this.prevMaterial = Block.get(blockId);
		this.prevData = blockId;
	}

	public void setCurrentMat(Material mat, int data) {
		this.currentMaterial = mat;
		this.currentData = data;
	}

	public void setMat(int blockId) {
		this.currentMaterial = Block.get(blockId);
		this.currentData = blockId;
	}

	public Material getPrevMaterial() {
		return prevMaterial;
	}

	public Material getCurrentMaterial() {
		return currentMaterial;
	}

	public int getPrevData() {
		return prevData;
	}

	public int getCurrentData() {
		return currentData;
	}

}
