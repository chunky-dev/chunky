/* Copyright (c) 2010-2012 Jesper Öqvist <jesper@llbit.se>
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
				cacheLevel = Math.max(i, cacheLevel);
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
}
