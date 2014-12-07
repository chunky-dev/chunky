/* Copyright (c) 2010-2014 Jesper Öqvist <jesper@llbit.se>
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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.commons.math3.util.FastMath;

import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.world.Block;

/**
 * A simple voxel Octree.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class Octree {

	/**
	 * An Octree node
	 */
	public static final class Node {
		/**
		 * The node type. Type is -1 if it's a non-leaf node.
		 */
		public int type;

		/**
		 * Child array
		 */
		public Node[] children;

		/**
		 * Create new octree leaf node with the given type
		 * @param type
		 */
		public Node(int type) {
			this.type = type;
		}

		/**
		 * Subdivide this leaf node
		 */
		public final void subdivide() {
			children = new Node[8];
			children[0] = new Node(type);
			children[1] = new Node(type);
			children[2] = new Node(type);
			children[3] = new Node(type);
			children[4] = new Node(type);
			children[5] = new Node(type);
			children[6] = new Node(type);
			children[7] = new Node(type);
			type = -1;
		}

		/**
		 * Merge the leafs of this node and make this node a
		 * leaf node.
		 * @param newType
		 */
		public final void merge(int newType) {
			type = newType;
			children = null;
		}

		/**
		 * @return Calculated data size, in bytes, to store this node
		 */
		public int dataSize() {
			if (type != -1) {
				return 1;
			} else {
				int total = 9;// type plus child indices
				for (Node child: children)
					total += child.dataSize();
				return total;
			}
		}

		/**
		 * @param index
		 * @param data
		 * @return A dump of the octree data in this node
		 */
		public int dumpData(int index, int[] data) {
			data[index++] = type;
			if (type == -1) {
				int childIndex = index;
				index += 8;
				for (int i = 0; i < 8; ++i) {
					data[childIndex+i] = index;
					index = children[i].dumpData(index, data);
				}
			}
			return index;
		}

		/**
		 * Serialize this node
		 * @param out
		 * @throws IOException
		 */
		public void store(DataOutputStream out) throws IOException {
			out.writeInt(type);
			if (type == -1) {
				for (int i = 0; i < 8; ++i) {
					children[i].store(out);
				}
			}
		}

		/**
		 * Deserialize node
		 * @param in
		 * @throws IOException
		 */
		public void load(DataInputStream in) throws IOException {
			type = in.readInt();
			if (type == -1) {
				children = new Node[8];
				for (int i = 0; i < 8; ++i) {
					children[i] = new Node(0);
					children[i].load(in);
				}
			}
		}
	}

	/**
	 * Recursive depth of the octree
	 */
	public final int depth;

	/**
	 * Root node
	 */
	public final Node root;

	/**
	 * Timestamp of last serialization.
	 */
	private long timestamp = 0;

	private final Node[] parents;
	private final Node[] cache;
	private int cx = 0;
	private int cy = 0;
	private int cz = 0;
	private int cacheLevel;

	/**
	 * Create a new Octree. The dimensions of the Octree
	 * are 2^levels.
	 * @param octreeDepth The number of levels in the Octree.
	 */
	public Octree(int octreeDepth) {
		depth = octreeDepth;
		root = new Node(0);
		parents = new Node[depth];
		cache = new Node[depth+1];
		cache[depth] = root;
		cacheLevel = depth;
	}

	/**
	 * Set the voxel type at the given coordinates.
	 *
	 * @param type The new voxel type to be set
	 * @param x
	 * @param y
	 * @param z
	 */
	public synchronized void set(int type, int x, int y, int z) {
		Node node = root;
		int parentLvl = depth-1;
		int level = parentLvl;
		for (int i = depth-1; i >= 0; --i) {
			level = i;
			parents[i] = node;

			if (node.type == type) {
				return;
			} else if (node.children == null) {
				node.subdivide();
				parentLvl = i;
			}

			int xbit = 1 & (x >> i);
			int ybit = 1 & (y >> i);
			int zbit = 1 & (z >> i);
			node = node.children[(xbit<<2) | (ybit<<1) | zbit];

		}
		node.type = type;

		// merge nodes where all children have been set to the same type
		for (int i = level; i <= parentLvl; ++i) {
			Node parent = parents[i];

			boolean allSame = true;
			for (Node child: parent.children) {
				if (child.type != node.type) {
					allSame = false;
					break;
				}
			}

			if (allSame) {
				parent.merge(node.type);
				cacheLevel = FastMath.max(i, cacheLevel);
			} else {
				break;
			}
		}

	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 * @return The voxel type at the given coordinates
	 */
	public synchronized int get(int x, int y, int z) {
		while (cacheLevel < depth && ((x >>> cacheLevel) != cx ||
					(y >>> cacheLevel) != cy || (z >>> cacheLevel) != cz))
			cacheLevel += 1;

		int type;
		while ((type = cache[cacheLevel].type) == -1) {
			cacheLevel -= 1;
			cx = x >>> cacheLevel;
			cy = y >>> cacheLevel;
			cz = z >>> cacheLevel;
			cache[cacheLevel] = cache[cacheLevel+1]
					.children[((cx&1)<<2) | ((cy&1)<<1) | (cz&1)];
		}
		return type;
	}

	/**
	 * Create a data buffer containing the octree data
	 * @return The data buffer representing the full octree data
	 */
	public int[] toDataBuffer() {
		int size = 0;
		size = root.dataSize();
		int[] data = new int[size];
		root.dumpData(0, data);
		return data;
	}

	/**
	 * Serialize this octree to a data output stream
	 * @param out
	 * @throws IOException
	 */
	public void store(DataOutputStream out) throws IOException {
		out.writeInt(depth);
		root.store(out);
	}

	/**
	 * Deserialize the octree from a data input stream
	 * @param in
	 * @return The deserialized octree
	 * @throws IOException
	 */
	public static Octree load(DataInputStream in) throws IOException {
		int treeDepth = in.readInt();
		Octree tree = new Octree(treeDepth);
		tree.root.load(in);
		return tree;
	}

	/**
	 * Test whether the ray intersects any voxel before exiting the Octree.
	 * @param scene
	 * @param ray the ray
	 * @return <code>true</code> if the ray intersects a voxel
	 */
	public boolean intersect(Scene scene, Ray ray) {

		int level;
		Octree.Node node;
		boolean first = true;

		int lx, ly, lz;
		int x, y, z;
		int nx = 0, ny = 0, nz = 0;
		double tNear = Double.POSITIVE_INFINITY;
		double t;
		Vector3d d = ray.d;

		while (true) {

			// add small offset past the intersection to avoid
			// recursion to the same octree node!
			x = (int) QuickMath.floor(ray.x.x + ray.d.x * Ray.OFFSET);
			y = (int) QuickMath.floor(ray.x.y + ray.d.y * Ray.OFFSET);
			z = (int) QuickMath.floor(ray.x.z + ray.d.z * Ray.OFFSET);

			node = root;
			level = depth;
			lx = x >>> level;
			ly = y >>> level;
			lz = z >>> level;

			if (lx != 0 || ly != 0 || lz != 0) {

				// ray origin is outside octree!
				ray.currentMaterial = Block.AIR_ID;

				// only check octree intersection if this is the first iteration
				if (first) {
					// test if it is entering the octree
					t = -ray.x.x / d.x;
					if (t > Ray.EPSILON) {
						tNear = t;
						nx = 1;
						ny = nz = 0;
					}
					t = ((1<<level) - ray.x.x) / d.x;
					if (t < tNear && t > Ray.EPSILON) {
						tNear = t;
						nx = -1;
						ny = nz = 0;
					}
					t = -ray.x.y / d.y;
					if (t < tNear && t > Ray.EPSILON) {
						tNear = t;
						ny = 1;
						nx = nz = 0;
					}
					t = ((1<<level) - ray.x.y) / d.y;
					if (t < tNear && t > Ray.EPSILON) {
						tNear = t;
						ny = -1;
						nx = nz = 0;
					}
					t = -ray.x.z / d.z;
					if (t < tNear && t > Ray.EPSILON) {
						tNear = t;
						nz = 1;
						nx = ny = 0;
					}
					t = ((1<<level) - ray.x.z) / d.z;
					if (t < tNear && t > Ray.EPSILON) {
						tNear = t;
						nz = -1;
						nx = ny = 0;
					}

					if (tNear < Double.MAX_VALUE) {
						ray.x.scaleAdd(tNear, d, ray.x);
						ray.n.set(nx, ny, nz);
						ray.distance += tNear;
						tNear = Double.POSITIVE_INFINITY;
						continue;
					} else {
						return false;// outside of octree!
					}
				} else {
					return false;// outside of octree!
				}
			}

			first = false;

			while (node.type == -1) {
				level -= 1;
				lx = x >>> level;
				ly = y >>> level;
				lz = z >>> level;
				node = node.children[((lx&1)<<2) | ((ly&1)<<1) | (lz&1)];
			}

			// old octree visualization code
			/*double w = .1 * (1 + level);
			w*=w;
			if (ray.x.x < (lx<<level) + w && (ray.x.y < (ly<<level) + w || ray.x.y > ((ly+1)<<level) - w) ||
					ray.x.x < (lx<<level) + w && (ray.x.z < (lz<<level) + w || ray.x.z > ((lz+1)<<level) - w) ||
					ray.x.y < (ly<<level) + w && (ray.x.z < (lz<<level) + w || ray.x.z > ((lz+1)<<level) - w) ||
					ray.x.x > ((lx+1)<<level) - w && (ray.x.y < (ly<<level) + w || ray.x.y > ((ly+1)<<level) - w) ||
					ray.x.x > ((lx+1)<<level) - w && (ray.x.z < (lz<<level) + w || ray.x.z > ((lz+1)<<level) - w) ||
					ray.x.y > ((ly+1)<<level) - w && (ray.x.z < (lz<<level) + w || ray.x.z > ((lz+1)<<level) - w)) {
				ray.color.x = .5;
				ray.color.y = .5;
				ray.color.z = .5;
				ray.color.w = 1;
				ray.prevMaterial = Block.AIR.id;
				ray.currentMaterial = 0xFF;
				return true;
			}*/

			if (ray.currentMaterial == -1) {
				ray.prevMaterial = 0;
				ray.currentMaterial = node.type;
			}

			Block currentBlock = Block.get(node.type);
			Block prevBlock = Block.get(ray.currentMaterial);

			ray.prevMaterial = ray.currentMaterial;
			ray.currentMaterial = node.type;


			if (currentBlock.localIntersect) {

				if (currentBlock == Block.WATER &&
						prevBlock == Block.WATER) {
					return exitWater(scene, ray);
				}

				if (currentBlock.intersect(ray, scene)) {
					if (prevBlock != currentBlock)
						return true;

					ray.x.scaleAdd(Ray.OFFSET, ray.d, ray.x);
					continue;
				} else {
					// exit ray from this local block
					ray.currentMaterial = 0;// current material is air

					ray.exitBlock(x, y, z);
					continue;
				}
			} else if (!currentBlock.isSameMaterial(prevBlock) && currentBlock != Block.AIR) {
				TexturedBlockModel.getIntersectionColor(ray);
				return true;
			}

			t = ((lx<<level) - ray.x.x) / d.x;
			if (t > Ray.EPSILON) {
				tNear = t;
				nx = 1;
				ny = nz = 0;
			} else {
				t = (((lx+1)<<level) - ray.x.x) / d.x;
				if (t < tNear && t > Ray.EPSILON) {
					tNear = t;
					nx = -1;
					ny = nz = 0;
				}
			}

			t = ((ly<<level) - ray.x.y) / d.y;
			if (t < tNear && t > Ray.EPSILON) {
				tNear = t;
				ny = 1;
				nx = nz = 0;
			} else {
				t = (((ly+1)<<level) - ray.x.y) / d.y;
				if (t < tNear && t > Ray.EPSILON) {
					tNear = t;
					ny = -1;
					nx = nz = 0;
				}
			}

			t = ((lz<<level) - ray.x.z) / d.z;
			if (t < tNear && t > Ray.EPSILON) {
				tNear = t;
				nz = 1;
				nx = ny = 0;
			} else {
				t = (((lz+1)<<level) - ray.x.z) / d.z;
				if (t < tNear && t > Ray.EPSILON) {
					tNear = t;
					nz = -1;
					nx = ny = 0;
				}
			}

			ray.x.scaleAdd(tNear, d, ray.x);
			ray.n.set(nx, ny, nz);
			ray.distance += tNear;
			tNear = Double.POSITIVE_INFINITY;
		}
	}

	/**
	 * @param scene
	 * @param ray
	 * @return <code>true</code> if exited water
	 */
	private boolean exitWater(Scene scene, Ray ray) {
		int level;
		Octree.Node node;

		int lx, ly, lz;
		int x, y, z;

		double nx, ny, nz;
		double xx, xy, xz;
		double cx, cy, cz, cw;
		double distance;

		while (true) {
			Block.WATER.intersect(ray, scene);
			ray.n.x = -ray.n.x;
			ray.n.y = -ray.n.y;
			ray.n.z = -ray.n.z;

			xx = ray.x.x;
			xy = ray.x.y;
			xz = ray.x.z;
			nx = ray.n.x;
			ny = ray.n.y;
			nz = ray.n.z;
			cx = ray.color.x;
			cy = ray.color.y;
			cz = ray.color.z;
			cw = ray.color.w;
			distance = ray.distance;

			// add small offset past the intersection to avoid
			// recursion to the same octree node!
			x = (int) QuickMath.floor(ray.x.x + ray.d.x * Ray.OFFSET);
			y = (int) QuickMath.floor(ray.x.y + ray.d.y * Ray.OFFSET);
			z = (int) QuickMath.floor(ray.x.z + ray.d.z * Ray.OFFSET);

			node = root;
			level = depth;
			lx = x >>> level;
			ly = y >>> level;
			lz = z >>> level;

			if (lx != 0 || ly != 0 || lz != 0) {

				// ray origin is outside octree!
				ray.currentMaterial = Block.AIR.id;
				return true;
			}

			while (node.type == -1) {
				level -= 1;
				lx = x >>> level;
				ly = y >>> level;
				lz = z >>> level;
				node = node.children[((lx&1)<<2) | ((ly&1)<<1) | (lz&1)];
			}

			Block currentBlock = Block.get(node.type);
			Block prevBlock = Block.get(ray.currentMaterial);

			ray.prevMaterial = ray.currentMaterial;
			ray.currentMaterial = node.type;

			if (currentBlock.localIntersect) {

				if (!currentBlock.intersect(ray, scene)) {
					ray.currentMaterial = Block.AIR.id;
					return true;
				}

				if (ray.distance > distance) {
					ray.x.set(xx, xy, xz);
					ray.n.set(nx, ny, nz);
					ray.color.set(cx, cy, cz, cw);
					ray.distance = distance;
					ray.currentMaterial = Block.AIR.id;
					return true;
				} else if (currentBlock == Block.WATER) {
					ray.x.scaleAdd(Ray.OFFSET, ray.d, ray.x);
					continue;
				} else {
					return true;
				}
			}

			if (currentBlock != prevBlock) {
				TexturedBlockModel.getIntersectionColor(ray);
				ray.n.scale(-1);
				return true;
			}
		}
	}

	/**
	 * Update the serialization timestamp.
	 * @param timestamp
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the serialization timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}
}
