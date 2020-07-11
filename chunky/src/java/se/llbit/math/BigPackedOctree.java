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
import java.util.ArrayList;

import static se.llbit.math.Octree.BRANCH_NODE;
import static se.llbit.math.Octree.DATA_FLAG;

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
   */
  private ArrayList<long[]> treeData = new ArrayList<>();

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

  /**
   * Constructor building a tree from an existing NodeBasedOctree
   * We build the tree by walking an existing tree and recreating it in this format
   * @param depth The depth of the tree
   * @param root The root of the tree to recreate
   */
  public BigPackedOctree(int depth, Octree.Node root) {
    this.depth = depth;
    long nodeCount = nodeCount(root);
    initTreeData(nodeCount);
    addNode(root, 0, 2);
    freeHead = -1; // No holes
    size = nodeCount;
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
    return -(int) ((value & 0xFFFFFFFF00000000L) >> 32);
  }

  private static int dataFromValue(long value) {
    return (int) (value & 0xFFFFFFFFL);
  }

  private static long valueFromTypeData(int type, int data) {
    return (long)(-type) << 32 | data;
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
  private long addNode(Octree.Node node, long nodeIndex, long nextFreeIndex) {
    if(node.type == BRANCH_NODE) {
      setAt(nodeIndex, nextFreeIndex);
      long newNextFreeIndex = nextFreeIndex + 8;
      for(int i = 0; i < 8; ++i) {
        newNextFreeIndex = addNode(node.children[i], nextFreeIndex+i, newNextFreeIndex);
      }
      return newNextFreeIndex;
    } else {
      setAt(nodeIndex, valueFromTypeData(node.type, node.getData()));
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
      return typeFromValue(value1) == secondNode.type // compare types
              && dataFromValue(value1) == secondNode.getData(); // compare data
    return false;
  }

  @Override
  public void set(int type, int x, int y, int z) {
    set(new Octree.Node(type), x, y, z);
  }

  @Override
  public void set(Octree.Node data, int x, int y, int z) {
    long[] parents = new long[depth]; // better to put as a field to preventallocation at each invocation?
    long nodeIndex = 0;
    int parentLevel = depth - 1;
    int position = 0;
    for (int i = depth - 1; i >= 0; --i) {
      parents[i] = nodeIndex;

      if (nodeEquals(nodeIndex, data)) {
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
    setAt(finalNodeIndex, valueFromTypeData(data.type, data.getData()));

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
  public Octree.Node get(int x, int y, int z) {
    long nodeIndex = getNodeIndex(x, y, z);
    long value = getAt(nodeIndex);
    Octree.Node node = new Octree.DataNode(value > 0 ? BRANCH_NODE : typeFromValue(value), dataFromValue(value));

    // Return dummy Node, will work if only type and data are used, breaks if children are needed
    return node;
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
   * Moves the ray to the boundary of the octree.
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

  /**
   *  {@inheritDoc}
   */
  @Override
  public boolean enterBlock(Scene scene, Ray ray, BlockPalette palette) {
    if (!isInside(ray.o) && !enterOctree(ray))
      return false;

    // Marching is done in a top-down fashion: at each step, the octree is descended from the root to find the leaf
    // node the ray is in. Terminating the march is then decided based on the block type in that leaf node. Finally the
    // ray is advanced to the boundary of the current leaf node and the next, ready for the next iteration.
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
      long nodeIndex = 0;
      while(getAt(nodeIndex) > 0) {
        level -= 1;
        lx = x >>> level;
        ly = y >>> level;
        lz = z >>> level;
        nodeIndex = getAt(nodeIndex) + (((lx & 1) << 2) | ((ly & 1) << 1) | (lz & 1));
      }

      long value = getAt(nodeIndex);

      // Test intersection
      Block currentBlock = palette.get(typeFromValue(value));
      Material prevBlock = ray.getCurrentMaterial();

      ray.setPrevMaterial(prevBlock, ray.getCurrentData());
      ray.setCurrentMaterial(currentBlock, dataFromValue(value));

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

  /**
   *  {@inheritDoc}
   */
  @Override
  public boolean exitWater(Scene scene, Ray ray, BlockPalette palette) {
    if (!isInside(ray.o) && !enterOctree(ray))
      return false;

    // Marching is done in a top-down fashion: at each step, the octree is descended from the root to find the leaf
    // node the ray is in. Terminating the march is then decided based on the block type in that leaf node. Finally the
    // ray is advanced to the boundary of the current leaf node and the next, ready for the next iteration.
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
      long nodeIndex = 0;
      int level = depth;
      while(getAt(nodeIndex) > 0) {
        level -= 1;
        lx = x >>> level;
        ly = y >>> level;
        lz = z >>> level;
        nodeIndex = getAt(nodeIndex) + (((lx & 1) << 2) | ((ly & 1) << 1) | (lz & 1));
      }

      long value = getAt(nodeIndex);

      // Test intersection
      Block currentBlock = palette.get(typeFromValue(value));
      Material prevBlock = ray.getCurrentMaterial();

      ray.setPrevMaterial(prevBlock, ray.getCurrentData());
      ray.setCurrentMaterial(currentBlock, dataFromValue(value));

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

      if ((dataFromValue(value) & (1 << Water.FULL_BLOCK)) == 0) {
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

  public static BigPackedOctree load(DataInputStream in) throws IOException {
    int depth = in.readInt();
    BigPackedOctree tree = new BigPackedOctree(depth);
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
      if ((type & DATA_FLAG) == 0) {
        setAt(nodeIndex, valueFromTypeData(type, 0));
      } else {
        int data = in.readInt();
        setAt(nodeIndex, valueFromTypeData(type ^ DATA_FLAG, 0));
      }
    }
  }

  private void storeNode(DataOutputStream out, long nodeIndex) throws IOException {
    long value = getAt(nodeIndex);
    int type = value > 0 ? BRANCH_NODE : typeFromValue(value);
    if(type == BRANCH_NODE) {
      out.writeInt(type);
      for(int i = 0; i < 8; ++i) {
        long childIndex = getAt(nodeIndex) + i;
        storeNode(out, childIndex);
      }
    } else {
      boolean isDataNode = (dataFromValue(value) != 0);
      if(isDataNode) {
        out.writeInt(type | DATA_FLAG);
        out.writeInt(dataFromValue(value));
      } else {
        out.writeInt(type);
      }
    }
  }
}
