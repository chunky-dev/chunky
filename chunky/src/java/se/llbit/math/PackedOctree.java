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

import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import it.unimi.dsi.fastutil.ints.IntObjectImmutablePair;
import org.apache.commons.math3.util.Pair;
import se.llbit.chunky.block.UnknownBlock;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.world.Material;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static se.llbit.math.Octree.*;

/**
 * This is a packed representation of an octree
 * the whole octree is stored in a int array to reduce memory usage and
 * hopefully improve performance by being more cache-friendly
 */
public class PackedOctree implements Octree.OctreeImplementation {
  /**
   * The entirety of the octree data is store in an int array.
   * <p>
   * Each node is made of a single integer value, which is either posative or negative.
   * - Positive index -> Branch node; int is the index of first child (the other 7 follow sequentially).
   * - Negative index -> Leaf node; int is the negation of the BlockPalette ID.
   * <p>
   * As nodes are stored linearly, we place siblings nodes in a row and so
   * we only need the index of the first child as the following are just after
   * <p>
   * This implementation is inspired by this stackoverflow answer
   * https://stackoverflow.com/questions/41946007/efficient-and-well-explained-implementation-of-a-quadtree-for-2d-collision-det#answer-48330314
   * <p>
   * When dealing with huge octree, the maximum size of an array may be a limitation.
   * When this occurs this implementation wan no longer be used and we must fallback on another one.
   * Here we'll throw an exception that the caller can catch.
   *
   * Note: This is public for some plugins. Stability is not guaranteed.
   */
  public int[] treeData;

  /**
   * The max size of an array we allow is a bit less than the max value an integer can have
   */
  private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 16;

  /**
   * The size of the dynamic array. Capacity is given by treeData.length.
   *
   * Java's built in ArrayList isn't used as generics don't work with primitives, and
   * Integer objects are object wrappers and will be references to somewhere else in
   * memory, not to mention other overhead. In the end, array of Integers would end up
   * using more than triple the memory and be slower.
   */
  private int size;

  /**
   * When removing nodes form the tree, it leaves holes in the array. Those holes
   * could be reused later when new nodes need to be added.
   *
   * freeHead is the "root node" of the singly linked list that keeps
   * track of free locations (by holding nodes in their places).
   *
   * If freeHead is -1, there is no holes that can be reused and the size of the array
   * must be increased.
   */
  private int freeHead;

  /**
   * The depth of the Octree records how many subdivisions are needed to represent a
   * single, individual block.
   */
  private int depth;

  /**
   * dense, temporary representation of a tree
   */
  List<int[]> tempTree = new ArrayList<>();

  /**
   * NodeId implementation for a int array PackedOctree.
   */
  private static final class NodeId implements Octree.NodeId {
    int nodeIndex;

    public NodeId(int nodeIndex) {
      this.nodeIndex = nodeIndex;
    }
  }

  /**
   * A custom exception that signals the octree is too big for this implementation
   */
  public static class OctreeTooBigException extends RuntimeException {
  }

  /**
   * The default size to start octree size at. Can start larger if specified, but
   * not smaller.
   */
  private static final int DEFAULT_INITIAL_SIZE = 64;

  /**
   * How much larger to make the array when dynamically resizing to make more space.
   */
  private static final double ARRAY_RESIZE_MULTIPLIER = 1.5;

  @Override
  public Octree.NodeId getRoot() {
    return new NodeId(0);
  }

  @Override
  public boolean isBranch(Octree.NodeId node) {
    return treeData[((NodeId) node).nodeIndex] > 0;
  }

  @Override
  public Octree.NodeId getChild(Octree.NodeId parent, int childNo) {
    return new NodeId(treeData[((NodeId) parent).nodeIndex] + childNo);
  }

  @Override
  public int getType(Octree.NodeId node) {
    return -treeData[((NodeId) node).nodeIndex];
  }

  private int getTypeFromIndex(int nodeIndex) {
    return -treeData[nodeIndex];
  }

  @Override
  public int getData(Octree.NodeId node) {
    return 0;
  }

  /**
   * Constructor building a tree with capacity for some nodes
   *
   * @param depth     The depth of the tree
   * @param nodeCount The number of nodes this tree will contain
   */
  public PackedOctree(int depth, long nodeCount) {
    this.depth = depth;
    long arraySize = Math.max(nodeCount, DEFAULT_INITIAL_SIZE);
    if(arraySize > (long) MAX_ARRAY_SIZE)
      throw new OctreeTooBigException();
    treeData = new int[(int) arraySize];
    // Add a root node
    treeData[0] = 0;
    size = 1;
    // No holes
    freeHead = -1;
  }

  /**
   * Constructs an empty octree
   *
   * @param depth The depth of the tree
   */
  public PackedOctree(int depth) {
    this.depth = depth;
    treeData = new int[DEFAULT_INITIAL_SIZE];
    // Add a root node
    treeData[0] = 0;
    size = 1;
    // No holes
    freeHead = -1;
  }

  /**
   * Finds an open space in the array to put 8 nodes.
   *
   * Checks free list first, then tries to append at the end of the array,
   * reallocating and extending the array if needed.
   *
   * Method also marks returned index as used (by removing from free list or
   * incrementing size)
   *
   * @return the index at the beginning of a free space in the array of size 8 ints
   */
  private int findSpace() {
    // Look in free list
    if(freeHead != -1) {
      // get and return first value
      int index = freeHead;
      // advance freeHead down the linked list.
      freeHead = treeData[freeHead];
      return index;
    }

    // append in array if we have the capacity
    if(size + 8 <= treeData.length) {
      int index = size;
      size += 8;
      return index;
    }

    // We need to grow the array
    long newSize = (long) Math.ceil(treeData.length * ARRAY_RESIZE_MULTIPLIER);
    // We need to check the array won't be too big
    if(newSize > (long) MAX_ARRAY_SIZE) {
      // We can allocate less memory than initially wanted if the next block will still be able to fit
      // If not, this implementation isn't suitable
      if(MAX_ARRAY_SIZE - size > 8) {
        // If by making the new array be of size MAX_ARRAY_SIZE we can still fit the block requested
        newSize = MAX_ARRAY_SIZE;
      } else {
        // array is too big for this datatype
        throw new OctreeTooBigException();
      }
    }
    // Allocate new array and copy existing contents over
    int[] newArray = new int[(int) newSize];
    System.arraycopy(treeData, 0, newArray, 0, size);
    treeData = newArray;
    // and increase size marker
    int index = size;
    size += 8;
    return index;
  }

  /**
   * free space at the given index, simply add the 8 ints block beginning at index to the free list
   *
   * @param index the index of the beginning of the block to free
   */
  private void freeSpace(int index) {
    treeData[index] = freeHead;
    freeHead = index;
  }

  /**
   * Subdivide a node, give to each child the same type and data that this node previously had
   *
   * @param nodeIndex The index of the node to subdivide
   */
  private void subdivideNode(int nodeIndex) {
    // Allocate space for children
    int firstChildIndex = findSpace();

    // Copy type to all children
    for(int i = 0; i < 8; ++i) {
      treeData[firstChildIndex + i] = treeData[nodeIndex];
    }
    // Make this parent node a branching node pointing to its children.
    treeData[nodeIndex] = firstChildIndex;
  }

  /**
   * Merge a parent node so it becomes a leaf node
   *
   * @param nodeIndex    The index of the node to merge
   * @param typeNegation The negation of the type (the value directly stored in the array)
   */
  private void mergeNode(int nodeIndex, int typeNegation) {
    int childrenIndex = treeData[nodeIndex];
    freeSpace(childrenIndex); // Delete children
    treeData[nodeIndex] = typeNegation; // Make the node a leaf one
  }

  /**
   * Compare two nodes, in array, by index.
   *
   * True if none branching, and same type.
   *
   * @param firstNodeIndex  The index of the first node
   * @param secondNodeIndex The index of the second node
   * @return true id the nodes compare equals, false otherwise
   */
  private boolean nodeEquals(int firstNodeIndex, int secondNodeIndex) {
    boolean firstIsBranch = treeData[firstNodeIndex] > 0;
    boolean secondIsBranch = treeData[secondNodeIndex] > 0;
    return (!firstIsBranch && !secondIsBranch && treeData[firstNodeIndex] == treeData[secondNodeIndex]); // compare types
  }

  /**
   * Compare two nodes.
   *
   * True if none branching, and same type.
   *
   * @param firstNodeIndex The index of the first node
   * @param secondNode     The second node (most likely outside of tree)
   * @return true id the nodes compare equals, false otherwise
   */
  private boolean nodeEquals(int firstNodeIndex, Octree.Node secondNode) {
    boolean firstIsBranch = treeData[firstNodeIndex] > 0;
    boolean secondIsBranch = (secondNode.type == BRANCH_NODE);
    return (!firstIsBranch && !secondIsBranch && -treeData[firstNodeIndex] == secondNode.type); // compare types (don't forget that in the tree the negation of the type is stored)
  }

  /**
   * Sets a specified block within the octree to a specific palette value, subdividing and merging as needed.
   *
   * x, y, z are in octree coordinates, NOT world coordinates.
   */
  @Override
  public void set(int type, int x, int y, int z) {
    set(new Octree.Node(type), x, y, z);
  }

  /**
   * Sets a specified block within the octree to a specific palette value, subdividing and merging as needed.
   *
   * x, y, z are in octree coordinates, NOT world coordinates.
   */
  @Override
  public void set(Octree.Node data, int x, int y, int z) {
    int[] parents = new int[depth]; // better to put as a field to prevent allocation at each invocation?
    int nodeIndex = 0; // start at root
    int position;

    // root is at the end of the array, its direct parent is at the front.
    for(int i = depth - 1; i >= 0; --i) {
      parents[i] = nodeIndex;

      if(nodeEquals(nodeIndex, data)) { // Everything in this region is already of this blocktype.
        return;
      }

      if(treeData[nodeIndex] <= 0) { // It's a leaf node
        subdivideNode(nodeIndex);
      }

      // Determine index of child (to go to next)
      int xbit = 1 & (x >> i);
      int ybit = 1 & (y >> i);
      int zbit = 1 & (z >> i);
      position = (xbit << 2) | (ybit << 1) | zbit;
      nodeIndex = treeData[nodeIndex] + position;

    }
    // store type into final node (this specific block coordinate's node)
    treeData[nodeIndex] = -data.type; // Negation of BlockPalette type stored

    // Merge nodes where all children have been set to the same type, starting from the bottom.
    for(int i = 0; i < depth; ++i) {
      int parentIndex = parents[i];

      // assert each child is of same type
      boolean allSame = true;
      for(int j = 0; j < 8; ++j) {
        int childIndex = treeData[parentIndex] + j;
        if(!nodeEquals(childIndex, nodeIndex)) {
          allSame = false;
          break;
        }
      }

      // If all same type, join them. Else, parents can't join, so break merge loop.
      if(allSame) {
        mergeNode(parentIndex, treeData[nodeIndex]);
      } else {
        break;
      }
    }
  }

  /**
   * Helper function that separate each bit of the input number
   * by 3 (eg 0b0110 -> 0b0001001000)
   *                      ^--^--^--^
   * This version only supports number with up to 8 bits
   */
  static private int splitBy3(int a)
  {
    int x = a & 0xff; // we only look at the first 8 bits
    // Here we have the bits          abcd efgh
    x = (x | x << 8) & 0x0f00f00f; // shift left 32 bits, OR with self, and 0001000000001111000000001111000000001111000000001111000000000000
    // Here we have         abcd 0000 0000 efgh
    x = (x | x << 4) & 0xc30c30c3; // shift left 32 bits, OR with self, and 0001000011000011000011000011000011000011000011000011000100000000
    // Here we have    ab00 00cd 0000 ef00 00gh
    x = (x | x << 2) & 0x49249249;
    // Here we have a0 0b00 c00d 00e0 0f00 g00h
    return x;
  }

  /**
   * Free a whole subtree recursively
   */
  private void freeSubTree(int nodeIndex) {
    int childrenIdx = treeData[nodeIndex];
    if(childrenIdx <= 0)
      return;

    for(int i = 0; i < 8; ++i)
      freeSubTree(childrenIdx+i);

    freeSpace(childrenIdx);
  }

  /**
   * Recursively insert the temporary tree representation into the tree
   * @param level the current level to insert
   * @param startIdx the index in the current level of the children to insert
   */
  private int insertTempTree(int level, int startIdx) {
    if(tempTree.get(level)[startIdx] <= 0)
      return tempTree.get(level)[startIdx];

    int childrenIdx = findSpace();
    for(int i = 0; i < 8; ++i) {
      int value = insertTempTree(level+1, startIdx*8 + i);
      treeData[childrenIdx+i] = value;
    }

    return childrenIdx;
  }

  @Override
  public void setCube(int cubeDepth, int[] types, int x, int y, int z) {
    int size = 1 << cubeDepth;

    for(int nextLevel = tempTree.size(); nextLevel <= cubeDepth; ++nextLevel)
      tempTree.add(new int[1 << (3*nextLevel)]);

    // Write all the types from in the last level of the temp tree in morton order
    // (so children are back to back in the array)
    for(int cz = 0; cz < size; ++cz) {
      for(int cy = 0; cy < size; ++cy) {
        for(int cx = 0; cx < size; ++cx) {
          int linearIdx = (cz << (2*cubeDepth)) + (cy << cubeDepth) + cx;
          int mortonIdx = (splitBy3(cx) << 2) | (splitBy3(cy) << 1) | splitBy3(cz);
          tempTree.get(cubeDepth)[mortonIdx] = -types[linearIdx];
        }
      }
    }

    // Construct levels from the level deeper until the root of the temp tree
    for(int curDepth = cubeDepth-1; curDepth >= 0; --curDepth) {
      int numElem = (1 << (3 * curDepth));
      for(int parentIdx = 0; parentIdx < numElem; ++parentIdx)
      {
        int childrenIdx = parentIdx * 8;
        boolean mergeable = true;
        int firstType = tempTree.get(curDepth+1)[childrenIdx];
        for(int childNo = 1; childNo < 8; ++childNo) {
          if(tempTree.get(curDepth+1)[childrenIdx+childNo] > 0) {
            mergeable = false;
            break;
          }
          if(firstType == -ANY_TYPE)
            firstType = tempTree.get(curDepth+1)[childrenIdx+childNo];
          else if(firstType != tempTree.get(curDepth+1)[childrenIdx+childNo] && tempTree.get(curDepth+1)[childrenIdx+childNo] != -ANY_TYPE) {
            mergeable = false;
            break;
          }
        }
        if(mergeable)
        {
          tempTree.get(curDepth)[parentIdx] = firstType;
        }
        else
        {
          tempTree.get(curDepth)[parentIdx] = 1;
        }
      }
    }

    int type = tempTree.get(0)[0];

    int[] parents = new int[depth]; // better to put as a field to prevent allocation at each invocation?
    int nodeIndex = 0; // start at root
    int position;

    // Walk down the tree until the place to insert similar to `set`
    for(int i = depth - 1; i >= cubeDepth; --i) {
      parents[i] = nodeIndex;

      if(type <= 0 && treeData[nodeIndex] == type) { // Everything in this region is already of this blocktype.
        return;
      }

      if(treeData[nodeIndex] <= 0) { // It's a leaf node
        subdivideNode(nodeIndex);
      }

      // Determine index of child (to go to next)
      int xbit = 1 & (x >> i);
      int ybit = 1 & (y >> i);
      int zbit = 1 & (z >> i);
      position = (xbit << 2) | (ybit << 1) | zbit;
      nodeIndex = treeData[nodeIndex] + position;
    }

    freeSubTree(nodeIndex);

    int value = insertTempTree(0, 0);
    treeData[nodeIndex] = value;

    // Merge nodes where all children have been set to the same type, starting from the bottom.
    for(int i = cubeDepth; i < depth; ++i) {
      int parentIndex = parents[i];

      // check each child is of same type
      boolean allSame = true;
      for(int j = 0; j < 8; ++j) {
        int childIndex = treeData[parentIndex] + j;
        if(!nodeEquals(childIndex, nodeIndex)) {
          allSame = false;
          break;
        }
      }

      // If all same type, join them. Else, parents can't join, so break merge loop.
      if(allSame) {
        mergeNode(parentIndex, treeData[nodeIndex]);
      } else {
        break;
      }
    }
  }

  /**
   * Gets a NodeID and depth of the node that is (or contains) the specified block.
   *
   * @param outTypeAndLevel is the reusable output type and level parameters, this is to save on allocation of {@code org.apache.commons.math3.util.Pair} and {@code PackedOctree.NodeId}
   *
   * x, y, z are in octree coordinates, NOT world coordinates.
   */
  @Override
  public void getWithLevel(IntIntMutablePair outTypeAndLevel, int x, int y, int z) {
    int nodeIndex = 0;
    int level = depth;
    while(treeData[nodeIndex] > 0) {
      level -= 1;
      int lx = x >>> level;
      int ly = y >>> level;
      int lz = z >>> level;
      nodeIndex = treeData[nodeIndex] + (((lx & 1) << 2) | ((ly & 1) << 1) | (lz & 1));
    }
    outTypeAndLevel.left(getTypeFromIndex(nodeIndex)).right(level);
  }

  /**
   * Gets the array index of the node which is (or contains) the block specified, via a binary (octnary?) search.
   *
   * x, y, z are in octree coordinates, NOT world coordinates.
   */
  private int getNodeIndex(int x, int y, int z) {
    int nodeIndex = 0;
    int level = depth;
    while(treeData[nodeIndex] > 0) {
      level -= 1;
      int lx = 1 & (x >>> level);
      int ly = 1 & (y >>> level);
      int lz = 1 & (z >>> level);
      nodeIndex = treeData[nodeIndex] + ((lx << 2) | (ly << 1) | lz);
    }
    return nodeIndex;
  }

  /**
   * Creates an octree node which represents the PackedOctree node which is (or contains) the
   * block specified.
   *
   * This node is not actually used within this PackedOctree, as it is stored inline in the
   * array here. This is just a Node object which wraps the values that the PackedOctree node
   * would have.
   *
   * x, y, z are in octree coordinates, NOT world coordinates.
   */
  @Override
  public Octree.Node get(int x, int y, int z) {
    int nodeIndex = getNodeIndex(x, y, z);

    Octree.Node node = new Octree.Node(treeData[nodeIndex] > 0 ? BRANCH_NODE : -treeData[nodeIndex]);

    // Return dummy Node, will work if only type and data are used, breaks if children are needed
    return node;
  }

  /**
   * Gets the block material type from the BlockPalette of the node which is (or contains) the block specified.
   *
   * x, y, z are in octree coordinates, NOT world coordinates.
   */
  @Override
  public Material getMaterial(int x, int y, int z, BlockPalette palette) {
    // Building the dummy node is useless here
    int nodeIndex = getNodeIndex(x, y, z);
    if(treeData[nodeIndex] > 0) {
      return UnknownBlock.UNKNOWN;
    }
    return palette.get(-treeData[nodeIndex]);
  }

  /**
   * Stores this PackedOctree into its serialized form.
   *
   * Note: Branching nodes will not store child array addresses, but instead
   * will be flagged as branches (can be reconstituted on load).
   */
  @Override
  public void store(DataOutputStream output) throws IOException {
    output.writeInt(depth);
    storeNode(output, 0);
  }

  @Override
  public int getDepth() {
    return depth;
  }

  /**
   * Create a new PackedOctree loaded from an InputStream without a node count.
   *
   * This defaults to a tiny octree that can only hold a few nodes to begin with (not
   * enough for any typical singular chunk), and needing to increase its size and copy
   * the array many many times to fit a normal scene. Use "loadWithNodeCount" if possible.
   */
  public static PackedOctree load(DataInputStream in) throws IOException {
    int depth = in.readInt();
    PackedOctree tree = new PackedOctree(depth);
    tree.loadNode(in, 0);
    return tree;
  }

  /**
   * Create a new PackedOctree loaded from an InputStream with a node count.
   *
   * Node count allows for creating an array for this packed octree of the correct size
   * first, without needing to resize and copy the array (which is slow).
   */
  public static PackedOctree loadWithNodeCount(long nodeCount, DataInputStream in) throws IOException {
    int depth = in.readInt();
    PackedOctree tree = new PackedOctree(depth, nodeCount);
    tree.loadNode(in, 0);
    return tree;
  }

  /**
   * Recursively read this node in from its serialized form from an InputStream (probably from a file).
   */
  private void loadNode(DataInputStream in, int nodeIndex) throws IOException {
    int type = in.readInt();
    if(type == BRANCH_NODE) {
      int childrenIndex = findSpace();
      treeData[nodeIndex] = childrenIndex;
      for(int i = 0; i < 8; ++i) {
        loadNode(in, childrenIndex + i);
      }
    } else {
      if((type & DATA_FLAG) == 0) {
        treeData[nodeIndex] = -type; // negation of type
      } else {
        int data = in.readInt();
        treeData[nodeIndex] = -(type ^ DATA_FLAG);
      }
    }
  }

  /**
   * Serialize this node and its children (recursively) to an OutputStream so it can be saved to a file.
   */
  private void storeNode(DataOutputStream out, int nodeIndex) throws IOException {
    // Branches are stored as branch markers, not the index (index is for array form only)
    // Otherwise store its palette type (positive of stored value)
    int type = treeData[nodeIndex] > 0 ? BRANCH_NODE : -treeData[nodeIndex];
    out.writeInt(type);

    // And if its a branch, recursively store its children.
    // Note: this stores Depth-First, NOT Breadth-First.
    if(type == BRANCH_NODE) {
      for(int i = 0; i < 8; ++i) {
        storeNode(out, treeData[nodeIndex] + i);
      }
    }
  }

  /**
   * Recursively count number of subnodes in this octree.
   */
  @Override
  public long nodeCount() {
    // Start counting from root node.
    return countNodes(0);
  }

  /**
   * Recursively count subnodes (including this one) from the specified node.
   * @param nodeIndex Index of starting node.
   */
  private long countNodes(int nodeIndex) {
    if(treeData[nodeIndex] > 0) {
      long total = 1; // this node
      for(int i = 0; i < 8; ++i)
        total += countNodes(treeData[nodeIndex] + i);
      return total;
    }
    // leaf node -> just this node
    return 1;
  }

  @Override
  public void startFinalization() {
    tempTree = null; // no longer needed
  }

  /**
   * Merge all nodes that can be merged together.
   */
  @Override
  public void endFinalization() {
    // There is a bunch of ANY_TYPE nodes we should try to merge
    finalizationNode(0);
  }

  /**
   * Merges all branching nodes of entirely one type and "ANY_TYPE" nodes.
   * @param nodeIndex Starting node index to begin recursive merge attempt
   */
  private void finalizationNode(int nodeIndex) {
    // Flag for still mergeable at this level. Set to false when different child node types, or when a child still has branches.
    boolean isStillMergeable = true;
    // The type to merge to. Will be ANY_TYPE, until the first node of another type is found (then taking on that value).
    int mergedType = -ANY_TYPE;

    // For each child node...
    for(int i = 0; i < 8; ++i) {
      int childIndex = treeData[nodeIndex] + i;

      // If branches, recursively attempt merge on it.
      if(treeData[childIndex] > 0) {
        finalizationNode(childIndex);

        // If child node did not merge, we cannot merge.
        if(treeData[childIndex] > 0) {
          isStillMergeable = false;
        }
      }

      // If we haven't yet disqualified a merge, check if the child's type is compatible for a merge.
      if(isStillMergeable) {
        // If no non-ANY_TYPE merge type selected, try to set it to this new block's.
        if(mergedType == -ANY_TYPE) {
          mergedType = treeData[childIndex];

          // Else we have already selected a type. Make sure this block is compatible with that merge.
        } else if(!(treeData[childIndex] == -ANY_TYPE || (treeData[childIndex] == mergedType))) {
          isStillMergeable = false;
        }
      }
    }
    // Now if it is still mergeable, all children of this node are leaves of type "mergedType" or ANY_TYPE, so merging to mergedType.
    if(isStillMergeable) {
      mergeNode(nodeIndex, mergedType);
    }
  }

  /**
   * Add PackedOctree to OctreeImplementationFactory so Packed can be created and loaded via by name.
   */
  static public void initImplementation() {
    Octree.addImplementationFactory("PACKED", new Octree.ImplementationFactory() {
      @Override
      public Octree.OctreeImplementation create(int depth) {
        return new PackedOctree(depth);
      }

      @Override
      public Octree.OctreeImplementation load(DataInputStream in) throws IOException {
        return PackedOctree.load(in);
      }

      @Override
      public Octree.OctreeImplementation loadWithNodeCount(long nodeCount, DataInputStream in) throws IOException {
        return PackedOctree.loadWithNodeCount(nodeCount, in);
      }

      @Override
      public boolean isOfType(Octree.OctreeImplementation implementation) {
        return implementation instanceof PackedOctree;
      }

      @Override
      public String getDescription() {
        return "Memory efficient octree implementation, doesn't work for octree with 2^31 nodes, i.e. scenes of 400k chunks. Should be enough for most use case.";
      }
    });
  }
}
