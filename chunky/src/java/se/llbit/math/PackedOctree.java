package se.llbit.math;

import se.llbit.chunky.block.UnknownBlock;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.world.Material;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static se.llbit.math.Octree.*;

/**
 * This is a packed representation of an octree
 * the whole octree is stored in a int array to reduce memory usage and
 * hopefully improve performance by being more cache-friendly
 */
public class PackedOctree implements Octree.OctreeImplementation {
  /**
   * The whole tree data is store in a int array
   * <p>
   * Each node is made of several values :
   * - the node type (could be a branch node or the type of block contained)
   * - optional additional data
   * - the index of its first child (if branch node)
   * <p>
   * As nodes are stored linearly, we place siblings nodes in a row and so
   * we only need the index of the first child as the following are just after
   * <p>
   * The node type is always positive, we can use this knowledge to compress the node to 2 ints:
   * one will contains the index of the first child if it is positive or the negation of the type
   * the other will contain the additional data
   * <p>
   * This implementation is inspired by this stackoverflow answer
   * https://stackoverflow.com/questions/41946007/efficient-and-well-explained-implementation-of-a-quadtree-for-2d-collision-det#answer-48330314
   * <p>
   * Note: Only leaf nodes can have additional data. In theory
   * we could potentially optimize further by only storing the index for branch nodes
   * by that would make other operations more complex. Most likely not worth it but could be an idea
   * <p>
   * When dealing with huge octree, the maximum size of an array may be a limitation
   * When this occurs this implementation wan no longer be used and we must fallback on another one.
   * Here we'll throw an exception that the caller can catch
   */
  private int[] treeData;

  /**
   * The max size of an array we allow is a bit less than the max value an integer can have
   */
  private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 16;

  /**
   * When adding nodes to the octree, the treeData array may have to grow
   * We implement a simple growing dynamic array, like an ArrayList
   * We don't we use ArrayList because it only works with objects
   * and having an array of Integer instead of int would increase the memory usage.
   * size gives us the size of the dynamic array, the capacity is given by treeData.length
   */
  private int size;
  /**
   * When removing nodes form the tree, it leaves holes in the array.
   * Those holes could be reused later when new nodes need to be added
   * We use a free list to keep of the location of the holes.
   * freeHead gives use the index of the head of the free list, if it is -1, there is no
   * holes that can be reused and the size of the array must be increased
   */
  private int freeHead;

  private int depth;

  private static final class NodeId implements Octree.NodeId {
    int nodeIndex;

    public NodeId(int nodeIndex) {
      this.nodeIndex = nodeIndex;
    }
  }

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

  @Override
  public int getData(Octree.NodeId node) {
    return 0;
  }

  /**
   * A custom exception that signals the octree is too big for this implementation
   */
  public static class OctreeTooBigException extends RuntimeException {
  }

  /**
   * Constructor building a tree with capacity for some nodes
   *
   * @param depth     The depth of the tree
   * @param nodeCount The number of nodes this tree will contain
   */
  public PackedOctree(int depth, long nodeCount) {
    this.depth = depth;
    long arraySize = Math.max(nodeCount, 64);
    if(arraySize > (long) MAX_ARRAY_SIZE)
      throw new OctreeTooBigException();
    treeData = new int[(int) arraySize];
    treeData[0] = 0;
    size = 1;
    freeHead = -1; // No holes
  }

  /**
   * Constructs an empty octree
   *
   * @param depth The depth of the tree
   */
  public PackedOctree(int depth) {
    this.depth = depth;
    treeData = new int[64];
    // Add a root node
    treeData[0] = 0;
    size = 1;
    freeHead = -1;
  }

  /**
   * Finds space in the array to put 8 nodes
   * We find space by searching in the free list
   * if this fails we append at the end of the array
   * if the size is greater than the capacity, we allocate a new array
   *
   * @return the index at the beginning of a free space in the array of size 16 ints (8 nodes)
   */
  private int findSpace() {
    // Look in free list
    if(freeHead != -1) {
      int index = freeHead;
      freeHead = treeData[freeHead];
      return index;
    }

    // append in array if we have the capacity
    if(size + 16 <= treeData.length) {
      int index = size;
      size += 16;
      return index;
    }

    // We need to grow the array
    long newSize = (long) Math.ceil(treeData.length * 1.5);
    // We need to check the array won't be too big
    if(newSize > (long) MAX_ARRAY_SIZE) {
      // We can allocate less memory than initially wanted if the next block will still be able to fit
      // If not, this implementation isn't suitable
      if(MAX_ARRAY_SIZE - treeData.length > 16) {
        // If by making the new array be of size MAX_ARRAY_SIZE we can still fit the block requested
        newSize = MAX_ARRAY_SIZE;
      } else {
        // array is too big
        throw new OctreeTooBigException();
      }
    }
    int[] newArray = new int[(int) newSize];
    System.arraycopy(treeData, 0, newArray, 0, size);
    treeData = newArray;
    // and then append
    int index = size;
    size += 16;
    return index;
    // FIXME If the array has a really small capacity (less than 32 ints) newArray may not have enough additional space
  }

  /**
   * free space at the given index, simply add the 16 ints block beginning at index to the free list
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
    int childrenIndex = findSpace();
    for(int i = 0; i < 8; ++i) {
      treeData[childrenIndex + i] = treeData[nodeIndex]; // copy type
    }
    treeData[nodeIndex] = childrenIndex; // Make the node a parent node pointing to its children
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
   * Compare two nodes
   *
   * @param firstNodeIndex  The index of the first node
   * @param secondNodeIndex The index of the second node
   * @return true id the nodes compare equals, false otherwise
   */
  private boolean nodeEquals(int firstNodeIndex, int secondNodeIndex) {
    boolean firstIsBranch = treeData[firstNodeIndex] > 0;
    boolean secondIsBranch = treeData[secondNodeIndex] > 0;
    return ((firstIsBranch && secondIsBranch) || treeData[firstNodeIndex] == treeData[secondNodeIndex]); // compare types
  }

  /**
   * Compare two nodes
   *
   * @param firstNodeIndex The index of the first node
   * @param secondNode     The second node (most likely outside of tree)
   * @return true id the nodes compare equals, false otherwise
   */
  private boolean nodeEquals(int firstNodeIndex, Octree.Node secondNode) {
    boolean firstIsBranch = treeData[firstNodeIndex] > 0;
    boolean secondIsBranch = (secondNode.type == BRANCH_NODE);
    return ((firstIsBranch && secondIsBranch) || -treeData[firstNodeIndex] == secondNode.type); // compare types (don't forget that in the tree the negation of the type is stored)
  }

  @Override
  public void set(int type, int x, int y, int z) {
    set(new Octree.Node(type), x, y, z);
  }

  @Override
  public void set(Octree.Node data, int x, int y, int z) {
    int[] parents = new int[depth]; // better to put as a field to preventallocation at each invocation?
    int nodeIndex = 0;
    int parentLevel = depth - 1;
    int position = 0;
    for(int i = depth - 1; i >= 0; --i) {
      parents[i] = nodeIndex;

      if(nodeEquals(nodeIndex, data)) {
        return;
      } else if(treeData[nodeIndex] <= 0) { // It's a leaf node
        subdivideNode(nodeIndex);
        parentLevel = i;
      }

      int xbit = 1 & (x >> i);
      int ybit = 1 & (y >> i);
      int zbit = 1 & (z >> i);
      position = (xbit << 2) | (ybit << 1) | zbit;
      nodeIndex = treeData[nodeIndex] + position;

    }
    int finalNodeIndex = treeData[parents[0]] + position;
    treeData[finalNodeIndex] = -data.type; // Store negation of the type

    // Merge nodes where all children have been set to the same type.
    for(int i = 0; i <= parentLevel; ++i) {
      int parentIndex = parents[i];

      boolean allSame = true;
      for(int j = 0; j < 8; ++j) {
        int childIndex = treeData[parentIndex] + j;
        if(!nodeEquals(childIndex, nodeIndex)) {
          allSame = false;
          break;
        }
      }

      if(allSame) {
        mergeNode(parentIndex, treeData[nodeIndex]);
      } else {
        break;
      }
    }
  }

  private int getNodeIndex(int x, int y, int z) {
    int nodeIndex = 0;
    int level = depth;
    while(treeData[nodeIndex] > 0) {
      level -= 1;
      int lx = x >>> level;
      int ly = y >>> level;
      int lz = z >>> level;
      nodeIndex = treeData[nodeIndex] + (((lx & 1) << 2) | ((ly & 1) << 1) | (lz & 1));
    }
    return nodeIndex;
  }

  @Override
  public Octree.Node get(int x, int y, int z) {
    int nodeIndex = getNodeIndex(x, y, z);

    Octree.Node node = new Octree.Node(treeData[nodeIndex] > 0 ? BRANCH_NODE : -treeData[nodeIndex]);

    // Return dummy Node, will work if only type and data are used, breaks if children are needed
    return node;
  }

  @Override
  public Material getMaterial(int x, int y, int z, BlockPalette palette) {
    // Building the dummy node is useless here
    int nodeIndex = getNodeIndex(x, y, z);
    if(treeData[nodeIndex] > 0) {
      return UnknownBlock.UNKNOWN;
    }
    return palette.get(-treeData[nodeIndex]);
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

  public static PackedOctree load(DataInputStream in) throws IOException {
    int depth = in.readInt();
    PackedOctree tree = new PackedOctree(depth);
    tree.loadNode(in, 0);
    return tree;
  }

  public static PackedOctree loadWithNodeCount(long nodeCount, DataInputStream in) throws IOException {
    int depth = in.readInt();
    PackedOctree tree = new PackedOctree(depth, nodeCount);
    tree.loadNode(in, 0);
    return tree;
  }

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

  private void storeNode(DataOutputStream out, int nodeIndex) throws IOException {
    int type = treeData[nodeIndex] > 0 ? BRANCH_NODE : -treeData[nodeIndex];
    out.writeInt(type);
    if(type == BRANCH_NODE) {
      for(int i = 0; i < 8; ++i) {
        int childIndex = treeData[nodeIndex] + i;
        storeNode(out, childIndex);
      }
    }
  }

  @Override
  public long nodeCount() {
    return countNodes(0);
  }

  private long countNodes(int nodeIndex) {
    if(treeData[nodeIndex] > 0) {
      long total = 1;
      for(int i = 0; i < 8; ++i)
        total += countNodes(treeData[nodeIndex] + i);
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

  private void finalizationNode(int nodeIndex) {
    boolean canMerge = true;
    int mergedType = -ANY_TYPE;
    for(int i = 0; i < 8; ++i) {
      int childIndex = treeData[nodeIndex] + i;
      if(treeData[childIndex] > 0) {
        finalizationNode(childIndex);
        // The node may have been merged, retest if it still a branch node
        if(treeData[childIndex] > 0) {
          canMerge = false;
        }
      }
      if(canMerge) {
        if(mergedType == -ANY_TYPE) {
          mergedType = treeData[childIndex];
        } else if(!(treeData[childIndex] == -ANY_TYPE || (treeData[childIndex] == mergedType))) {
          canMerge = false;
        }
      }
    }
    if(canMerge) {
      mergeNode(nodeIndex, mergedType);
    }
  }

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
