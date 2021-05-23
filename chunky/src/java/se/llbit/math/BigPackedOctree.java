/* Copyright (c) 2020-2021 Chunky contributors
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

import se.llbit.chunky.block.UnknownBlock;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.world.Material;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static se.llbit.math.Octree.*;

/**
 * This is a big packed representation of an octree
 * the design is almost identical to the packed octree
 * but uses several long arrays to have
 */
public class BigPackedOctree implements Octree.OctreeImplementation {
  /**
   * The design is almost the same that packed octree, the differences being:
   *  - The data is split into several long arrays instead of a single int array
   *  - The data for one node is store in one long instead of two ints, when the node
   *    is a branch node, the long represents the index of the first child. When the
   *    node is a leaf node, the upper 32 bits of the long contain the opposite of the node type
   *    and the lower 32 bits are the node data (that way, for a leaf node the sign bit will be set
   *    and the long can simply be compared with 0 to determine if it is a branch or a leaf)
   *
   * Note: This is public for some plugins. Stability is not guaranteed.
   */
  public ArrayList<long[]> treeData = new ArrayList<>();

  /**
   * The max size of an array we allow is a bit less than the max value an integer can have
   */
  private static final int MAX_ARRAY_SIZE = 1 << 30; // MAX_INT is 2^31 - 1 but it is useful to use a power of 2 so we use the biggest power of 2 that is smaller than MAX_INT
  private static final long SUB_ARRAY_MASK = MAX_ARRAY_SIZE - 1;
  private static final long FULL_ARRAY_MASK = ~SUB_ARRAY_MASK;
  private static final int FULL_ARRAY_SHIFT = 30;

  /**
   * The total capacity of every long array
   */
  private long capacity;

  /**
   * When adding nodes to the octree, the treeData array may have to grow
   * We implement a simple growing dynamic array, like an ArrayList
   * We don't we use ArrayList because it only works with objects
   * and having an array of Integer instead of int would increase the memory usage.
   * size gives us the size of the dynamic array, the capacity is given by treeData.length
   */
  private long size;
  /**
   * When removing nodes form the tree, it leaves holes in the array.
   * Those holes could be reused later when new nodes need to be added
   * We use a free list to keep of the location of the holes.
   * freeHead gives use the index of the head of the free list, if it is -1, there is no
   * holes that can be reused and the size of the array must be increased
   */
  private long freeHead;

  private int depth;

  private static final class NodeId implements Octree.NodeId {
    public long nodeIndex;

    public NodeId(long nodeIdex) {
      this.nodeIndex = nodeIdex;
    }
  }

  @Override
  public Octree.NodeId getRoot() {
    return new NodeId(0);
  }

  @Override
  public boolean isBranch(Octree.NodeId node) {
    return getAt(((NodeId)node).nodeIndex) > 0;
  }

  @Override
  public Octree.NodeId getChild(Octree.NodeId parent, int childNo) {
    return new NodeId(getAt(((NodeId)parent).nodeIndex) + childNo);
  }

  @Override
  public int getType(Octree.NodeId node) {
    return typeFromValue(getAt(((NodeId)node).nodeIndex));
  }

  /**
   * Constructor building a tree with capacity for some nodes
   * @param depth The depth of the tree
   * @param nodeCount The number of nodes this tree will contain
   */
  public BigPackedOctree(int depth, long nodeCount) {
    this.depth = depth;
    initTreeData(nodeCount);
    freeHead = -1; // No holes
    setAt(0, 0);
    size = 1;
  }

  /**
   * Constructs an empty octree
   * @param depth The depth of the tree
   */
  public BigPackedOctree(int depth) {
    this.depth = depth;
    initTreeData(64);
    // Add a root node
    setAt(0, 0);
    size = 1;
    freeHead = -1;
  }

  private void initTreeData(long requestedCapacity) {
    capacity = requestedCapacity;
    int numFullArray = (int) ((capacity & FULL_ARRAY_MASK) >> FULL_ARRAY_SHIFT);
    for(int i = 0; i < numFullArray; ++i) {
      treeData.add(new long[MAX_ARRAY_SIZE]);
    }

    int remainingSize = (int) (capacity & SUB_ARRAY_MASK);
    if(remainingSize > 0)
      treeData.add(new long[remainingSize]);
  }

  private long getAt(long index) {
    return treeData.get((int) ((index & FULL_ARRAY_MASK) >> FULL_ARRAY_SHIFT))[(int) (index & SUB_ARRAY_MASK)];
  }

  private void setAt(long index, long value) {
    treeData.get((int) ((index & FULL_ARRAY_MASK) >> FULL_ARRAY_SHIFT))[(int) (index & SUB_ARRAY_MASK)] = value;
  }

  private static int typeFromValue(long value) {
    return -(int) (value);
  }

  private static long valueFromType(int type) {
    return (long)(-type);
  }

  /**
   * Finds space in the array to put 8 nodes
   * We find space by searching in the free list
   * if this fails we append at the end of the array
   * if the size is greater than the capacity, we allocate a new array
   * @return the index at the beginning of a free space in the array of size 16 ints (8 nodes)
   */
  private long findSpace() {
    // Look in free list
    if(freeHead != -1) {
      long index = freeHead;
      freeHead = getAt(freeHead);
      return index;
    }

    if(size+8 <= capacity) {
      long index = size;
      size += 8;
      return index;
    }

    // Increase capacity
    if(treeData.size() > 2) {
      // Add a full array, this means a growth factor <= 1.5 depending of how many arrays were already there
      treeData.add(new long[MAX_ARRAY_SIZE]);
      capacity += MAX_ARRAY_SIZE;
    } else if(treeData.size() == 2) {
      // Grow the second array or create a third one
      if(treeData.get(1).length < MAX_ARRAY_SIZE) {
        // growth factor of 4/3
        long[] newArray = new long[MAX_ARRAY_SIZE];
        System.arraycopy(treeData.get(1), 0, newArray, 0, (int)(size & SUB_ARRAY_MASK));
        treeData.set(1, newArray);
        capacity += MAX_ARRAY_SIZE / 2;
      } else {
        // growth factor of 1.5
        treeData.add(new long[MAX_ARRAY_SIZE]);
        capacity += MAX_ARRAY_SIZE;
      }
    } else {
      // Grow the first array or create the second one or both
      long newCapacity = (long)Math.ceil(capacity*1.5);
      boolean resize = true;
      if(newCapacity > MAX_ARRAY_SIZE) {
        if(MAX_ARRAY_SIZE - capacity > 8) {
          // If by making the new array be of size MAX_ARRAY_SIZE we can still fit the block requested
          newCapacity = MAX_ARRAY_SIZE;
        } else {
          treeData.add(new long[MAX_ARRAY_SIZE/2]);
          capacity += MAX_ARRAY_SIZE/2;
          resize = false;
        }
      }

      if(resize) {
        long[] newArray = new long[(int)newCapacity];
        System.arraycopy(treeData.get(0), 0, newArray, 0, (int)size);
        treeData.set(0, newArray);
        capacity = newCapacity;
      }
    }

    // and then append
    long index = size;
    size += 8;
    return index;
  }

  /**
   * free space at the given index, simply add the 16 ints block beginning at index to the free list
   * @param index the index of the beginning of the block to free
   */
  private void freeSpace(long index) {
    setAt(index, freeHead);
    freeHead = index;
  }

  /**
   * Subdivide a node, give to each child the same type and data that this node previously had
   * @param nodeIndex The index of the node to subdivide
   */
  private void subdivideNode(long nodeIndex) {
    long childrenIndex = findSpace();
    for(int i = 0; i < 8; ++i) {
      setAt(childrenIndex + i, getAt(nodeIndex));
    }
    setAt(nodeIndex, childrenIndex); // Make the node a parent node pointing to its children
  }

  /**
   * Merge a parent node so it becomes a leaf node
   * @param nodeIndex The index of the node to merge
   * @param value The value of the node (type + data)
   */
  private void mergeNode(long nodeIndex, long value) {
    long childrenIndex = getAt(nodeIndex);
    freeSpace(childrenIndex); // Delete children
    setAt(nodeIndex, value);
  }

  /**
   * Compare two nodes
   * @param firstNodeIndex The index of the first node
   * @param secondNodeIndex The index of the second node
   * @return true id the nodes compare equals, false otherwise
   */
  private boolean nodeEquals(long firstNodeIndex, long secondNodeIndex) {
    long value1 = getAt(firstNodeIndex);
    long value2 = getAt(secondNodeIndex);
    return value1 == value2;
  }

  /**
   * Compare two nodes
   * @param firstNodeIndex The index of the first node
   * @param secondNode The second node (most likely outside of tree)
   * @return true id the nodes compare equals, false otherwise
   */
  private boolean nodeEquals(long firstNodeIndex, Octree.Node secondNode) {
    long value1 = getAt(firstNodeIndex);
    boolean firstIsBranch = value1 > 0;
    boolean secondIsBranch = (secondNode.type == BRANCH_NODE);
    if(firstIsBranch && secondIsBranch)
      return false;
    else if(!firstIsBranch && !secondIsBranch)
      return typeFromValue(value1) == secondNode.type; // compare types
    return false;
  }

  @Override
  public void set(int type, int x, int y, int z) {
    long[] parents = new long[depth]; // better to put as a field to preventallocation at each invocation?
    long nodeIndex = 0;
    int parentLevel = depth - 1;
    int position = 0;
    for (int i = depth - 1; i >= 0; --i) {
      parents[i] = nodeIndex;

      if (typeFromValue(getAt(nodeIndex)) == type) {
        return;
      } else if (getAt(nodeIndex) <= 0) { // It's a leaf node
        subdivideNode(nodeIndex);
        parentLevel = i;
      }

      int xbit = 1 & (x >> i);
      int ybit = 1 & (y >> i);
      int zbit = 1 & (z >> i);
      position = (xbit << 2) | (ybit << 1) | zbit;
      nodeIndex = getAt(nodeIndex) + position;

    }
    long finalNodeIndex = getAt(parents[0]) + position;
    setAt(finalNodeIndex, valueFromType(type));

    // Merge nodes where all children have been set to the same type.
    for (int i = 0; i <= parentLevel; ++i) {
      long parentIndex = parents[i];

      boolean allSame = true;
      for(int j = 0; j < 8; ++j) {
        long childIndex = getAt(parentIndex) + j;
        if(!nodeEquals(childIndex, nodeIndex)) {
          allSame = false;
          break;
        }
      }

      if (allSame) {
        mergeNode(parentIndex, getAt(nodeIndex));
      } else {
        break;
      }
    }
  }

  private long getNodeIndex(int x, int y, int z) {
    long nodeIndex = 0;
    int level = depth;
    while(getAt(nodeIndex) > 0) {
      level -= 1;
      int lx = x >>> level;
      int ly = y >>> level;
      int lz = z >>> level;
      nodeIndex = getAt(nodeIndex) + (((lx & 1) << 2) | ((ly & 1) << 1) | (lz & 1));
    }
    return nodeIndex;
  }

  @Override
  public Material getMaterial(int x, int y, int z, BlockPalette palette) {
    // Building the dummy node is useless here
    long nodeIndex = getNodeIndex(x, y, z);
    long value = getAt(nodeIndex);
    if(value > 0) {
      return UnknownBlock.UNKNOWN;
    }
    return palette.get(typeFromValue(value));
  }

  @Override
  public void store(DataOutputStream output) throws IOException {
    output.writeInt(depth);
    storeNode(output, 0);
  }

  @Override
  public int getDepth() {
    return depth;
  }

  public static BigPackedOctree load(DataInputStream in) throws IOException {
    int depth = in.readInt();
    BigPackedOctree tree = new BigPackedOctree(depth);
    tree.loadNode(in, 0);
    return tree;
  }

  public static BigPackedOctree loadWithNodeCount(long nodeCount, DataInputStream in) throws IOException {
    int depth = in.readInt();
    BigPackedOctree tree = new BigPackedOctree(depth, nodeCount);
    tree.loadNode(in, 0);
    return tree;
  }

  private void loadNode(DataInputStream in, long nodeIndex) throws IOException {
    int type = in.readInt();
    if(type == BRANCH_NODE) {
      long childrenIndex = findSpace();
      setAt(nodeIndex, childrenIndex);
      for (int i = 0; i < 8; ++i) {
        loadNode(in, childrenIndex + i);
      }
    } else {
      setAt(nodeIndex, valueFromType(type));
    }
  }

  private void storeNode(DataOutputStream out, long nodeIndex) throws IOException {
    long value = getAt(nodeIndex);
    int type = value > 0 ? BRANCH_NODE : typeFromValue(value);
    out.writeInt(type);
    if(type == BRANCH_NODE) {
      for(int i = 0; i < 8; ++i) {
        long childIndex = getAt(nodeIndex) + i;
        storeNode(out, childIndex);
      }
    }
  }

  @Override
  public long nodeCount() {
    return countNodes(0);
  }

  private long countNodes(long nodeIndex) {
    if(getAt(nodeIndex) > 0) {
      long total = 1;
      for(int i = 0; i < 8; ++i)
        total += countNodes(getAt(nodeIndex) + i);
      return total;
    } else {
      return 1;
    }
  }

  @Override
  public void endFinalization() {
    // There is a bunch of ANY_TYPE nodes we should try to merge
    finalizationNode(0);
  }

  private void finalizationNode(long nodeIndex) {
    boolean canMerge = true;
    int mergedType = ANY_TYPE;
    for(int i = 0; i < 8; ++i) {
      long childIndex = getAt(nodeIndex) + i;
      if(getAt(childIndex) > 0) {
        finalizationNode(childIndex);
        // The node may have been merged, retest if it still a branch node
        if(getAt(childIndex) > 0) {
          canMerge = false;
        }
      }
      if(canMerge) {
        if(mergedType == ANY_TYPE) {
          long value = getAt(childIndex);
          mergedType = typeFromValue(value);
        } else if(!(typeFromValue(getAt(childIndex)) == ANY_TYPE || getAt(childIndex) == valueFromType(mergedType))) {
          canMerge = false;
        }
      }
    }
    if(canMerge) {
      mergeNode(nodeIndex, valueFromType(mergedType));
    }
  }

  static public void initImplementation() {
    Octree.addImplementationFactory("BIGPACKED", new Octree.ImplementationFactory() {
      @Override
      public Octree.OctreeImplementation create(int depth) {
        return new BigPackedOctree(depth);
      }

      @Override
      public Octree.OctreeImplementation load(DataInputStream in) throws IOException {
        return BigPackedOctree.load(in);
      }

      @Override
      public Octree.OctreeImplementation loadWithNodeCount(long nodeCount, DataInputStream in) throws IOException {
        return BigPackedOctree.loadWithNodeCount(nodeCount, in);
      }

      @Override
      public boolean isOfType(Octree.OctreeImplementation implementation) {
        return implementation instanceof BigPackedOctree;
      }

      @Override
      public String getDescription() {
        return "Almost as memory efficient as PACKED but doesn't have a limitation on the size of the octree.";
      }
    });
  }
}
