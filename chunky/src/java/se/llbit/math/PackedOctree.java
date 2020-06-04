package se.llbit.math;

import se.llbit.chunky.block.Air;
import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.UnknownBlock;
import se.llbit.chunky.block.Water;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.model.WaterModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.world.Material;
import sun.jvm.hotspot.oops.BranchData;

import java.io.DataOutputStream;
import java.io.IOException;

import static se.llbit.math.Octree.BRANCH_NODE;

/**
 * This is a packed representation of an octree
 * the whole octree is stored in a int array to reduce memory usage and
 * hopefully improve performance by being more cache-friendly
 *
 * (At least for now) this version only implements a subset of what is needed,
 * it is intented to be
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
   *  Note: I think only leaf nodes can have additional data. If that is indeed the case
   *  we could potentially optimize further by only storing the index for branch nodes
   */
  private final int[] treeData;

  private final int depth;

  /**
   * We build the tree by walking an existing tree and recreating it in this format
   * @param depth The depth of the tree
   * @param root The root of the tree to recreate
   */
  public PackedOctree(int depth, Octree.Node root) {
    this.depth = depth;
    int nodeCount = nodeCount(root);
    treeData = new int[nodeCount*2];
    addNode(root, 0, 2);
  }

  private static int nodeCount(Octree.Node node) {
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

  @Override
  public void set(int type, int x, int y, int z) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public void set(Octree.Node data, int x, int y, int z) {
    throw new RuntimeException("Not implemented");
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
    throw new RuntimeException("Not implemented");
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

  @Override
  public boolean enterBlock(Scene scene, Ray ray, BlockPalette palette) {
    int level;
    int nodeIndex;
    boolean first = true;

    int lx, ly, lz;
    int x, y, z;
    int nx = 0, ny = 0, nz = 0;
    double tNear = Double.POSITIVE_INFINITY;
    double t;
    Vector3 d = ray.d;

    while (true) {
      // Add small offset past the intersection to avoid
      // recursion to the same octree node!
      x = (int) QuickMath.floor(ray.o.x + d.x * Ray.OFFSET);
      y = (int) QuickMath.floor(ray.o.y + d.y * Ray.OFFSET);
      z = (int) QuickMath.floor(ray.o.z + d.z * Ray.OFFSET);

      nodeIndex = 0;
      level = depth;
      lx = x >>> level;
      ly = y >>> level;
      lz = z >>> level;

      if (lx != 0 || ly != 0 || lz != 0) {

        // ray origin is outside octree!

        // only check octree intersection if this is the first iteration
        if (first) {
          // test if it is entering the octree
          t = -ray.o.x / d.x;
          if (t > Ray.EPSILON) {
            tNear = t;
            nx = 1;
            ny = nz = 0;
          }
          t = ((1 << level) - ray.o.x) / d.x;
          if (t < tNear && t > Ray.EPSILON) {
            tNear = t;
            nx = -1;
            ny = nz = 0;
          }
          t = -ray.o.y / d.y;
          if (t < tNear && t > Ray.EPSILON) {
            tNear = t;
            ny = 1;
            nx = nz = 0;
          }
          t = ((1 << level) - ray.o.y) / d.y;
          if (t < tNear && t > Ray.EPSILON) {
            tNear = t;
            ny = -1;
            nx = nz = 0;
          }
          t = -ray.o.z / d.z;
          if (t < tNear && t > Ray.EPSILON) {
            tNear = t;
            nz = 1;
            nx = ny = 0;
          }
          t = ((1 << level) - ray.o.z) / d.z;
          if (t < tNear && t > Ray.EPSILON) {
            tNear = t;
            nz = -1;
            nx = ny = 0;
          }

          if (tNear < Double.MAX_VALUE) {
            ray.o.scaleAdd(tNear, d);
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

      while(treeData[nodeIndex] > 0) {
        level -= 1;
        lx = x >>> level;
        ly = y >>> level;
        lz = z >>> level;
        nodeIndex = treeData[nodeIndex] + (((lx & 1) << 2) | ((ly & 1) << 1) | (lz & 1)) * 2;
      }

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

      // Exit current octree leaf.
      t = ((lx << level) - ray.o.x) / d.x;
      if (t > Ray.EPSILON) {
        tNear = t;
        nx = 1;
        ny = nz = 0;
      } else {
        t = (((lx + 1) << level) - ray.o.x) / d.x;
        if (t < tNear && t > Ray.EPSILON) {
          tNear = t;
          nx = -1;
          ny = nz = 0;
        }
      }

      t = ((ly << level) - ray.o.y) / d.y;
      if (t < tNear && t > Ray.EPSILON) {
        tNear = t;
        ny = 1;
        nx = nz = 0;
      } else {
        t = (((ly + 1) << level) - ray.o.y) / d.y;
        if (t < tNear && t > Ray.EPSILON) {
          tNear = t;
          ny = -1;
          nx = nz = 0;
        }
      }

      t = ((lz << level) - ray.o.z) / d.z;
      if (t < tNear && t > Ray.EPSILON) {
        tNear = t;
        nz = 1;
        nx = ny = 0;
      } else {
        t = (((lz + 1) << level) - ray.o.z) / d.z;
        if (t < tNear && t > Ray.EPSILON) {
          tNear = t;
          nz = -1;
          nx = ny = 0;
        }
      }

      ray.o.scaleAdd(tNear, d);
      ray.n.set(nx, ny, nz);
      ray.distance += tNear;
      tNear = Double.POSITIVE_INFINITY;
    }
  }

  @Override
  public boolean exitWater(Scene scene, Ray ray, BlockPalette palette) {
    int level;
    int nodeIndex;
    boolean first = true;

    int lx, ly, lz;
    int x, y, z;
    int nx = 0, ny = 0, nz = 0;
    double tNear = Double.POSITIVE_INFINITY;
    double t;
    Vector3 d = ray.d;

    while (true) {

      x = (int) QuickMath.floor(ray.o.x + d.x * Ray.OFFSET);
      y = (int) QuickMath.floor(ray.o.y + d.y * Ray.OFFSET);
      z = (int) QuickMath.floor(ray.o.z + d.z * Ray.OFFSET);

      nodeIndex = 0;
      level = depth;
      lx = x >>> level;
      ly = y >>> level;
      lz = z >>> level;

      if (lx != 0 || ly != 0 || lz != 0) {

        // only check octree intersection if this is the first iteration
        if (first) {
          // test if it is entering the octree
          t = -ray.o.x / d.x;
          if (t > Ray.EPSILON) {
            tNear = t;
            nx = 1;
            ny = nz = 0;
          }
          t = ((1 << level) - ray.o.x) / d.x;
          if (t < tNear && t > Ray.EPSILON) {
            tNear = t;
            nx = -1;
            ny = nz = 0;
          }
          t = -ray.o.y / d.y;
          if (t < tNear && t > Ray.EPSILON) {
            tNear = t;
            ny = 1;
            nx = nz = 0;
          }
          t = ((1 << level) - ray.o.y) / d.y;
          if (t < tNear && t > Ray.EPSILON) {
            tNear = t;
            ny = -1;
            nx = nz = 0;
          }
          t = -ray.o.z / d.z;
          if (t < tNear && t > Ray.EPSILON) {
            tNear = t;
            nz = 1;
            nx = ny = 0;
          }
          t = ((1 << level) - ray.o.z) / d.z;
          if (t < tNear && t > Ray.EPSILON) {
            tNear = t;
            nz = -1;
            nx = ny = 0;
          }

          if (tNear < Double.MAX_VALUE) {
            ray.o.scaleAdd(tNear, d);
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

      while(treeData[nodeIndex] > 0) {
        level -= 1;
        lx = x >>> level;
        ly = y >>> level;
        lz = z >>> level;
        nodeIndex = treeData[nodeIndex] + (((lx & 1) << 2) | ((ly & 1) << 1) | (lz & 1)) * 2;
      }

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

      // Exit current octree leaf.
      if ((treeData[nodeIndex+1] & (1 << Water.FULL_BLOCK)) == 0) {
        if (WaterModel.intersectTop(ray)) {
          ray.setCurrentMaterial(Air.INSTANCE, 0);
          return true;
        } else {
          ray.exitBlock(x, y, z);
          continue;
        }
      }

      t = ((lx << level) - ray.o.x) / d.x;
      if (t > Ray.EPSILON) {
        tNear = t;
        nx = 1;
        ny = nz = 0;
      } else {
        t = (((lx + 1) << level) - ray.o.x) / d.x;
        if (t < tNear && t > Ray.EPSILON) {
          tNear = t;
          nx = -1;
          ny = nz = 0;
        }
      }

      t = ((ly << level) - ray.o.y) / d.y;
      if (t < tNear && t > Ray.EPSILON) {
        tNear = t;
        ny = 1;
        nx = nz = 0;
      } else {
        t = (((ly + 1) << level) - ray.o.y) / d.y;
        if (t < tNear && t > Ray.EPSILON) {
          tNear = t;
          ny = -1;
          nx = nz = 0;
        }
      }

      t = ((lz << level) - ray.o.z) / d.z;
      if (t < tNear && t > Ray.EPSILON) {
        tNear = t;
        nz = 1;
        nx = ny = 0;
      } else {
        t = (((lz + 1) << level) - ray.o.z) / d.z;
        if (t < tNear && t > Ray.EPSILON) {
          tNear = t;
          nz = -1;
          nx = ny = 0;
        }
      }

      ray.o.scaleAdd(tNear, d);
      ray.n.set(nx, ny, nz);
      ray.distance += tNear;
      tNear = Double.POSITIVE_INFINITY;
    }
  }

  @Override
  public int getDepth() {
    return depth;
  }
}
