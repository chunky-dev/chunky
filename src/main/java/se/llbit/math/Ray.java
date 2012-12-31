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

import se.llbit.chunky.world.Biomes;
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
		return Block.values[prevMaterial & 0xFF];
	}

	/**
	 * @return Current block
	 */
	public final Block getCurrentBlock() {
		return Block.values[currentMaterial & 0xFF];
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
		return 0xF & (currentMaterial >> 8);
	}

	/**
	 * Set origin and direction
	 * @param o Origin
	 * @param d Direction
	 */
	public final void set(Vector3d o, Vector3d d) {
		setDefault();
		this.x.set(o);
		this.d.set(d);
	}

	/**
	 * @return Biome color of current block
	 */
	public final float[] getBiomeGrassColor() {
		return Biomes.getGrassColorCorrected(currentMaterial >> BlockData.BIOME_ID);
	}

	/**
	 * @return Biome id of current block
	 */
	public final int getBiomeId() {
		return 0xFF & (currentMaterial >> 24);
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
 	 * Calculate the UV coordinates for the ray on the given block.
 	 * @param bx block x coordinate
 	 * @param by block y coordinate
 	 * @param bz block z coordinate
 	 */
	public final void calcUVCoords(int bx, int by, int bz) {
		if (n.y != 0) {
			u = x.x - bx;
			v = x.z - bz;
		} else if (n.x != 0) {
			u = x.z - bz;
			v = x.y - by;
		} else {
			u = x.x - bx;
			v = x.y - by;
		}
	}
	
	private static final String[] torchDir = {
		"", "east", "west", "south", "north", "on floor"
	};
	
	private static final String[] cocoaSize = {
		"small", "medium", "large"
	};
	
	private static final String[] woodType = {
		"oak", "spruce", "birch", "jungle"
	};
	
	private static final String[] woolColor = {
		"white",
		"orange",
		"magenta",
		"light blue",
		"yellow",
		"lime",
		"pink",
		"gray",
		"light gray",
		"cyan",
		"purple",
		"blue",
		"brown",
		"green",
		"red",
		"black"
	};

	/**
	 * @return String description of the block's extra info
	 */
	public String getBlockExtraInfo() {
		int data = getBlockData();
		Block block = getCurrentBlock();
		switch (block.id) {
		case Block.WATER_ID:
		case Block.LAVA_ID:
			return "level: " + data;
		case Block.TORCH_ID:
		case Block.REDSTONETORCHOFF_ID:
		case Block.REDSTONETORCHON_ID:
			return torchDir[data % 6];
		case Block.COCOAPLANT_ID:
			return cocoaSize[data>>2];
		case Block.WOOD_ID:
			return woodType[data & 3];
		case Block.WOOL_ID:
			return woolColor[data];
		case Block.REDSTONEWIRE_ID:
			return "power: " + data;
		default:
			return "";
		}
	}

}
