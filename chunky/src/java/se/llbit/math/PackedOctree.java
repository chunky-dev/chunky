package se.llbit.math;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.block.Air;
import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.UnknownBlock;
import se.llbit.chunky.block.Water;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.model.WaterModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.world.Material;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static se.llbit.math.Octree.BRANCH_NODE;
import static se.llbit.math.Octree.DATA_FLAG;

/**
 * This is a packed representation of an octree
 * the whole octree is stored in a int array to reduce memory usage and
 * hopefully improve performance by being more cache-friendly
 */
public class PackedOctree implements Octree.OctreeImplementation {
  /**
   * The whole tree data is store in a int array
   *
   * Each node is made of several values :
   *  - the node type (could be a branch node or the type of block contained)
   *  - optional additional data
   *  - the index of its first child (if branch node)
   *
   *  As nodes are stored linearly, we place siblings nodes in a row and so
   *  we only need the index of the first child as the following are just after
   *
   *  The node type is always positive, we can use this knowledge to compress the node to 2 ints:
   *  one will contains the index of the first child if it is positive or the negation of the type
   *  the other will contain the additional data
   *
   *  This implementation is inspired by this stackoverflow answer
   *  https://stackoverflow.com/questions/41946007/efficient-and-well-explained-implementation-of-a-quadtree-for-2d-collision-det#answer-48330314
   *
   *  Note: Only leaf nodes can have additional data. In theory
   *  we could potentially optimize further by only storing the index for branch nodes
   *  by that would make other operations more complex. Most likely not worth it but could be an idea
   *
   *  When dealing with huge octree, the maximum size of an array may be a limitation
   *  When this occurs this implementation wan no longer be used and we must fallback on another one.
   *  Here we'll throw an exception that the caller can catch
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

  /**
   * A custom exception that signals the octree is too big for this implementation
   */
  public static class OctreeTooBigException extends RuntimeException {
  }

  /**
   * Constructor building a tree from an existing NodeBasedOctree
   * We build the tree by walking an existing tree and recreating it in this format
   * @param depth The depth of the tree
   * @param root The root of the tree to recreate
   */
  public PackedOctree(int depth, Octree.Node root) {
    this.depth = depth;
    long nodeCount = nodeCount(root);
    long arraySize = Math.max(nodeCount*2, 64);
    if(arraySize > (long)MAX_ARRAY_SIZE)
      throw new OctreeTooBigException();
    treeData = new int[(int)arraySize];
    addNode(root, 0, 2);
    freeHead = -1; // No holes
    size = (int)nodeCount*2;
  }

  /**
   * Constructs an empty octree
   * @param depth The depth of the tree
   */
  public PackedOctree(int depth) {
    this.depth = depth;
    treeData = new int[64];
    // Add a root node
    treeData[0] = 0;
    treeData[1] = 0;
    size = 2;
    freeHead = -1;
  }

  private static long nodeCount(Octree.Node node) {
    if(node.type == BRANCH_NODE) {
      return 1
        + nodeCount(node.children[0])
        + nodeCount(node.children[1])
        + nodeCount(node.children[2])
        + nodeCount(node.children[3])
        + nodeCount(node.children[4])
        + nodeCount(node.children[5])
        + nodeCount(node.children[6])
        + nodeCount(node.children[7]);
    } else {
      return 1;
    }
  }

  /**
   * Add a node at the next free index and call recursively on children
   * @param node The node to add
   * @param nodeIndex The index of the node currently being added
   * @param nextFreeIndex The next free index before adding the node
   * @return The next free index after adding the subtree
   */
  private int addNode(Octree.Node node, int nodeIndex, int nextFreeIndex) {
    // Unconditionally add data
    treeData[nodeIndex+1] = node.getData();
    if(node.type == BRANCH_NODE) {
      treeData[nodeIndex] = nextFreeIndex;
      int newNextFreeIndex = nextFreeIndex + 8*2;
      for(int i = 0; i < 8; ++i) {
        newNextFreeIndex = addNode(node.children[i], nextFreeIndex+2*i, newNextFreeIndex);
      }
      return newNextFreeIndex;
    } else {
      treeData[nodeIndex] = -node.type; // Store the negation of the type
      return nextFreeIndex;
    }
  }

  /**
   * Finds space in the array to put 8 nodes
   * We find space by searching in the free list
   * if this fails we append at the end of the array
   * if the size is greater than the capacity, we allocate a new array
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
    if(size+16 <= treeData.length) {
      int index = size;
      size += 16;
      return index;
    }

    // We need to grow the array
    long newSize = (long)Math.ceil(treeData.length*1.5);
    // We need to check the array won't be too big
    if(newSize > (long)MAX_ARRAY_SIZE) {
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
    int[] newArray = new int[(int)newSize];
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
   * @param index the index of the beginning of the block to free
   */
  private void freeSpace(int index) {
    treeData[index] = freeHead;
    freeHead = index;
  }

  /**
   * Subdivide a node, give to each child the same type and data that this node previously had
   * @param nodeIndex The index of the node to subdivide
   */
  private void subdivideNode(int nodeIndex) {
    int childrenIndex = findSpace();
    for(int i = 0; i < 8; ++i) {
      treeData[childrenIndex + 2*i] = treeData[nodeIndex]; // copy type
      treeData[childrenIndex + 2*i + 1] = treeData[nodeIndex+1]; // copy data
    }
    treeData[nodeIndex] = childrenIndex; // Make the node a parent node pointing to its children
    treeData[nodeIndex+1] = 0; // reset its data
  }

  /**
   * Merge a parent node so it becomes a leaf node
   * @param nodeIndex The index of the node to merge
   * @param typeNegation The negation of the type (the value directly stored in the array)
   */
  private void mergeNode(int nodeIndex, int typeNegation, int data) {
    int childrenIndex = treeData[nodeIndex];
    freeSpace(childrenIndex); // Delete children
    treeData[nodeIndex] = typeNegation; // Make the node a leaf one
    treeData[nodeIndex+1] = data;
  }

  /**
   * Compare two nodes
   * @param firstNodeIndex The index of the first node
   * @param secondNodeIndex The index of the second node
   * @return true id the nodes compare equals, false otherwise
   */
  private boolean nodeEquals(int firstNodeIndex, int secondNodeIndex) {
    boolean firstIsBranch = treeData[firstNodeIndex] > 0;
    boolean secondIsBranch = treeData[secondNodeIndex] > 0;
    return ((firstIsBranch && secondIsBranch) || treeData[firstNodeIndex] == treeData[secondNodeIndex]) // compare types
      && treeData[firstNodeIndex+1] == treeData[secondNodeIndex+1]; // compare data
    // FIXME possible bug here as we always compare the data even when dealing with nodes that don't really have data
    // The data int could potentially contain some junk leftover of a previous node
    // In theory it should be reset to 0 but we need to be careful
  }

  /**
   * Compare two nodes
   * @param firstNodeIndex The index of the first node
   * @param secondNode The second node (most likely outside of tree)
   * @return true id the nodes compare equals, false otherwise
   */
  private boolean nodeEquals(int firstNodeIndex, Octree.Node secondNode) {
    boolean firstIsBranch = treeData[firstNodeIndex] > 0;
    boolean secondIsBranch = (secondNode.type == BRANCH_NODE);
    return ((firstIsBranch && secondIsBranch) || -treeData[firstNodeIndex] == secondNode.type) // compare types (don't forget that in the tree the negation of the type is stored)
            && treeData[firstNodeIndex+1] == secondNode.getData(); // compare data
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
    for (int i = depth - 1; i >= 0; --i) {
      parents[i] = nodeIndex;

      if (nodeEquals(nodeIndex, data)) {
        return;
      } else if (treeData[nodeIndex] <= 0) { // It's a leaf node
        subdivideNode(nodeIndex);
        parentLevel = i;
      }

      int xbit = 1 & (x >> i);
      int ybit = 1 & (y >> i);
      int zbit = 1 & (z >> i);
      position = (xbit << 2) | (ybit << 1) | zbit;
      nodeIndex = treeData[nodeIndex] + position*2;

    }
    int finalNodeIndex = treeData[parents[0]] + position*2;
    treeData[finalNodeIndex] = -data.type; // Store negation of the type
    treeData[finalNodeIndex+1] = data.getData();

    // Merge nodes where all children have been set to the same type.
    for (int i = 0; i <= parentLevel; ++i) {
      int parentIndex = parents[i];

      boolean allSame = true;
      for(int j = 0; j < 8; ++j) {
        int childIndex = treeData[parentIndex] + 2*j;
        if(!nodeEquals(childIndex, nodeIndex)) {
          allSame = false;
          break;
        }
      }

      if (allSame) {
        mergeNode(parentIndex, treeData[nodeIndex], treeData[nodeIndex+1]);
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
      nodeIndex = treeData[nodeIndex] + (((lx & 1) << 2) | ((ly & 1) << 1) | (lz & 1)) * 2;
    }
    return nodeIndex;
  }

  @Override
  public Octree.Node get(int x, int y, int z) {
    int nodeIndex = getNodeIndex(x, y, z);

    Octree.Node node = new Octree.DataNode(treeData[nodeIndex] > 0 ? BRANCH_NODE : -treeData[nodeIndex], treeData[nodeIndex+1]);

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
  public boolean isInside(Vector3 o) {
    int x = (int) QuickMath.floor(o.x);
    int y = (int) QuickMath.floor(o.y);
    int z = (int) QuickMath.floor(o.z);

    int lx = x >>> depth;
    int ly = y >>> depth;
    int lz = z >>> depth;

    return lx == 0 && ly == 0 && lz == 0;
  }

  /**
   * Moves the ray to the of the octree.
   * @param ray Ray that will be moved to the boundary of the octree. The origin, distance and normals will be modified.
   * @return {@code false} if the ray doesn't intersect the octree.
   */
  private boolean enterOctree(Ray ray) {
    double nx, ny, nz;
    double octree_size = 1 << depth;

    // AABB intersection with the octree boundary
    double tMin, tMax;
    double invDirX = 1 / ray.d.x;
    if (invDirX >= 0) {
      tMin = -ray.o.x * invDirX;
      tMax = (octree_size - ray.o.x) * invDirX;

      nx = -1;
      ny = nz = 0;
    } else {
      tMin = (octree_size - ray.o.x) * invDirX;
      tMax = -ray.o.x * invDirX;

      nx = 1;
      ny = nz = 0;
    }

    double tYMin, tYMax;
    double invDirY = 1 / ray.d.y;
    if (invDirY >= 0) {
      tYMin = -ray.o.y * invDirY;
      tYMax = (octree_size - ray.o.y) * invDirY;
    } else {
      tYMin = (octree_size - ray.o.y) * invDirY;
      tYMax = -ray.o.y * invDirY;
    }

    if ((tMin > tYMax) || (tYMin > tMax))
      return false;

    if (tYMin > tMin) {
      tMin = tYMin;

      ny = -FastMath.signum(ray.d.y);
      nx = nz = 0;
    }

    if (tYMax < tMax)
      tMax = tYMax;

    double tZMin, tZMax;
    double invDirZ = 1 / ray.d.z;
    if (invDirZ >= 0) {
      tZMin = -ray.o.z * invDirZ;
      tZMax = (octree_size - ray.o.z) * invDirZ;
    } else {
      tZMin = (octree_size - ray.o.z) * invDirZ;
      tZMax = -ray.o.z * invDirZ;
    }

    if ((tMin > tZMax) || (tZMin > tMax))
      return false;

    if (tZMin > tMin) {
      tMin = tZMin;

      nz = -FastMath.signum(ray.d.z);
      nx = ny = 0;
    }

    if (tMin < 0)
      return false;

    ray.o.scaleAdd(tMin, ray.d);
    ray.n.set(nx, ny, nz);
    ray.distance += tMin;
    return true;
  }

  @Override
  public boolean enterBlock(Scene scene, Ray ray, BlockPalette palette) {
    if (!isInside(ray.o) && !enterOctree(ray))
      return false;

    while (true) {
      // Add small offset past the intersection to avoid
      // recursion to the same octree node!
      int x = (int) QuickMath.floor(ray.o.x + ray.d.x * Ray.OFFSET);
      int y = (int) QuickMath.floor(ray.o.y + ray.d.y * Ray.OFFSET);
      int z = (int) QuickMath.floor(ray.o.z + ray.d.z * Ray.OFFSET);

      int lx = x >>> depth;
      int ly = y >>> depth;
      int lz = z >>> depth;

      if (lx != 0 || ly != 0 || lz != 0)
          return false; // outside of octree!

      // Descend the tree to find the current leaf node
      int level = depth;
      int nodeIndex = 0;
      while(treeData[nodeIndex] > 0) {
        level -= 1;
        lx = x >>> level;
        ly = y >>> level;
        lz = z >>> level;
        nodeIndex = treeData[nodeIndex] + (((lx & 1) << 2) | ((ly & 1) << 1) | (lz & 1)) * 2;
      }

      // Test intersection
      Block currentBlock = palette.get(-treeData[nodeIndex]);
      Material prevBlock = ray.getCurrentMaterial();

      ray.setPrevMaterial(prevBlock, ray.getCurrentData());
      ray.setCurrentMaterial(currentBlock, treeData[nodeIndex+1]);

      if (currentBlock.localIntersect) {
        if (currentBlock.intersect(ray, scene)) {
          if (prevBlock != currentBlock)
            return true;

          ray.o.scaleAdd(Ray.OFFSET, ray.d);
          continue;
        } else {
          // Exit ray from this local block.
          ray.setCurrentMaterial(Air.INSTANCE, 0); // Current material is air.
          ray.exitBlock(x, y, z);
          continue;
        }
      } else if (!currentBlock.isSameMaterial(prevBlock) && currentBlock != Air.INSTANCE) {
        TexturedBlockModel.getIntersectionColor(ray);
        return true;
      }

      // No intersection, exit current octree leaf.
      int nx = 0, ny = 0, nz = 0;
      double tNear = Double.POSITIVE_INFINITY;

      // Testing all six sides of the current leaf node and advancing to the closest intersection
      double t = ((lx << level) - ray.o.x) / ray.d.x;
      if (t > Ray.EPSILON) {
        tNear = t;
        nx = 1;
        ny = nz = 0;
      } else {
        t = (((lx + 1) << level) - ray.o.x) / ray.d.x;
        if (t < tNear && t > Ray.EPSILON) {
          tNear = t;
          nx = -1;
          ny = nz = 0;
        }
      }

      t = ((ly << level) - ray.o.y) / ray.d.y;
      if (t < tNear && t > Ray.EPSILON) {
        tNear = t;
        ny = 1;
        nx = nz = 0;
      } else {
        t = (((ly + 1) << level) - ray.o.y) / ray.d.y;
        if (t < tNear && t > Ray.EPSILON) {
          tNear = t;
          ny = -1;
          nx = nz = 0;
        }
      }

      t = ((lz << level) - ray.o.z) / ray.d.z;
      if (t < tNear && t > Ray.EPSILON) {
        tNear = t;
        nz = 1;
        nx = ny = 0;
      } else {
        t = (((lz + 1) << level) - ray.o.z) / ray.d.z;
        if (t < tNear && t > Ray.EPSILON) {
          tNear = t;
          nz = -1;
          nx = ny = 0;
        }
      }

      ray.o.scaleAdd(tNear, ray.d);
      ray.n.set(nx, ny, nz);
      ray.distance += tNear;
    }
  }

  @Override
  public boolean exitWater(Scene scene, Ray ray, BlockPalette palette) {
    if (!isInside(ray.o) && !enterOctree(ray))
      return false;

    while (true) {
      // Add small offset past the intersection to avoid
      // recursion to the same octree node!
      int x = (int) QuickMath.floor(ray.o.x + ray.d.x * Ray.OFFSET);
      int y = (int) QuickMath.floor(ray.o.y + ray.d.y * Ray.OFFSET);
      int z = (int) QuickMath.floor(ray.o.z + ray.d.z * Ray.OFFSET);

      int lx = x >>> depth;
      int ly = y >>> depth;
      int lz = z >>> depth;

      if (lx != 0 || ly != 0 || lz != 0)
        return false; // outside of octree!

      // Descend the tree to find the current leaf node
      int nodeIndex = 0;
      int level = depth;
      while(treeData[nodeIndex] > 0) {
        level -= 1;
        lx = x >>> level;
        ly = y >>> level;
        lz = z >>> level;
        nodeIndex = treeData[nodeIndex] + (((lx & 1) << 2) | ((ly & 1) << 1) | (lz & 1)) * 2;
      }

      // Test intersection
      Block currentBlock = palette.get(-treeData[nodeIndex]);
      Material prevBlock = ray.getCurrentMaterial();

      ray.setPrevMaterial(prevBlock, ray.getCurrentData());
      ray.setCurrentMaterial(currentBlock, treeData[nodeIndex+1]);

      if (!currentBlock.isWater()) {
        if (currentBlock.localIntersect) {
          if (!currentBlock.intersect(ray, scene)) {
            ray.setCurrentMaterial(Air.INSTANCE, 0);
          }
          return true;
        } else if (currentBlock != Air.INSTANCE) {
          TexturedBlockModel.getIntersectionColor(ray);
          return true;
        } else {
          return true;
        }
      }

      if ((treeData[nodeIndex+1] & (1 << Water.FULL_BLOCK)) == 0) {
        if (WaterModel.intersectTop(ray)) {
          ray.setCurrentMaterial(Air.INSTANCE, 0);
          return true;
        } else {
          ray.exitBlock(x, y, z);
          continue;
        }
      }

      // No intersection, exit current octree leaf.
      int nx = 0, ny = 0, nz = 0;
      double tNear = Double.POSITIVE_INFINITY;

      // Testing all six sides of the current leaf node and advancing to the closest intersection
      double t = ((lx << level) - ray.o.x) / ray.d.x;
      if (t > Ray.EPSILON) {
        tNear = t;
        nx = 1;
        ny = nz = 0;
      } else {
        t = (((lx + 1) << level) - ray.o.x) / ray.d.x;
        if (t < tNear && t > Ray.EPSILON) {
          tNear = t;
          nx = -1;
          ny = nz = 0;
        }
      }

      t = ((ly << level) - ray.o.y) / ray.d.y;
      if (t < tNear && t > Ray.EPSILON) {
        tNear = t;
        ny = 1;
        nx = nz = 0;
      } else {
        t = (((ly + 1) << level) - ray.o.y) / ray.d.y;
        if (t < tNear && t > Ray.EPSILON) {
          tNear = t;
          ny = -1;
          nx = nz = 0;
        }
      }

      t = ((lz << level) - ray.o.z) / ray.d.z;
      if (t < tNear && t > Ray.EPSILON) {
        tNear = t;
        nz = 1;
        nx = ny = 0;
      } else {
        t = (((lz + 1) << level) - ray.o.z) / ray.d.z;
        if (t < tNear && t > Ray.EPSILON) {
          tNear = t;
          nz = -1;
          nx = ny = 0;
        }
      }

      ray.o.scaleAdd(tNear, ray.d);
      ray.n.set(nx, ny, nz);
      ray.distance += tNear;
    }
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

  private void loadNode(DataInputStream in, int nodeIndex) throws IOException {
    int type = in.readInt();
    if(type == BRANCH_NODE) {
      int childrenIndex = findSpace();
      treeData[nodeIndex] = childrenIndex;
      treeData[nodeIndex+1] = 0; // store 0 as data
      for (int i = 0; i < 8; ++i) {
        loadNode(in, childrenIndex + 2*i);
      }
    } else {
      if ((type & DATA_FLAG) == 0) {
        treeData[nodeIndex] = -type; // negation of type
        treeData[nodeIndex+1] = 0; // store 0 to be sure we don't have uninitialized garbage
      } else {
        int data = in.readInt();
        treeData[nodeIndex] = -(type ^ DATA_FLAG);
        treeData[nodeIndex+1] = data;
      }
    }
  }

  private void storeNode(DataOutputStream out, int nodeIndex) throws IOException {
    int type = treeData[nodeIndex] > 0 ? BRANCH_NODE : -treeData[nodeIndex];
    if(type == BRANCH_NODE) {
      out.writeInt(type);
      for(int i = 0; i < 8; ++i) {
        int childIndex = treeData[nodeIndex] + 2*i;
        storeNode(out, childIndex);
      }
    } else {
      boolean isDataNode = (treeData[nodeIndex+1] != 0);
      if(isDataNode) {
        out.writeInt(type | DATA_FLAG);
        out.writeInt(treeData[nodeIndex+1]);
      } else {
        out.writeInt(type);
      }
    }
  }

  /**
   * Convert this tree to the equivalent NodeBasedOctree
   * @return The NodeBasedOctree
   */
  public NodeBasedOctree toNodeBasedOctree() {
    return new NodeBasedOctree(depth, convertNode(0));
  }

  /**
   * Convert a node to the NodeBasedOctree node format
   * @param nodeIndex The index of the node to convert
   * @return The converted node
   */
  private Octree.Node convertNode(int nodeIndex) {
    if(treeData[nodeIndex] > 0) {
      // branch node
      Octree.Node node = new Octree.Node(BRANCH_NODE);
      node.children = new Octree.Node[8];
      for(int i = 0; i < 8; ++i) {
        int childIndex = treeData[nodeIndex] + 2*i;
        node.children[i] = convertNode(childIndex);
      }
      return node;
    } else {
      boolean isDataNode = (treeData[nodeIndex+1] != 0);
      return isDataNode ? new Octree.DataNode(treeData[nodeIndex], treeData[nodeIndex+1])
                        : new Octree.Node(treeData[nodeIndex]);
    }
  }
}
