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

/**
 * This is the classic node-based implementation of an octree
 */
public class NodeBasedOctree implements Octree.OctreeImplementation {
  /**
   * Recursive depth of the octree
   */
  public final int depth;

  /**
   * Root node
   */
  public Octree.Node root;

  private final Octree.Node[] parents;
  private final int[] positions;
  private final Octree.Node[] cache;
  private int cx = 0;
  private int cy = 0;
  private int cz = 0;
  private int cacheLevel;

  public NodeBasedOctree(int octreeDepth, Octree.Node node) {
    depth = octreeDepth;
    root = node;
    parents = new Octree.Node[depth];
    positions = new int[depth];
    cache = new Octree.Node[depth + 1];
    cache[depth] = root;
    cacheLevel = depth;
  }

  @Override
  public void set(int type, int x, int y, int z) {
    set(new Octree.Node(type), x, y, z);
  }

  @Override
  public void set(Octree.Node data, int x, int y, int z) {
    Octree.Node node = root;
    int parentLevel = depth - 1;
    int position = 0;
    for (int i = depth - 1; i >= 0; --i) {
      parents[i] = node;

      if (node.equals(data)) {
        return;
      } else if (node.children == null) {
        node.subdivide();
        parentLevel = i;
      }

      int xbit = 1 & (x >> i);
      int ybit = 1 & (y >> i);
      int zbit = 1 & (z >> i);
      position = (xbit << 2) | (ybit << 1) | zbit;
      positions[i] = position;
      node = node.children[position];

    }
    parents[0].children[position] = data;

    // Merge nodes where all children have been set to the same type.
    for (int i = 0; i <= parentLevel; ++i) {
      Octree.Node parent = parents[i];

      boolean allSame = true;
      for (Octree.Node child : parent.children) {
        if (!child.equals(data)) {
          allSame = false;
          break;
        }
      }

      if (allSame) {
        // The parent node needs to be replaced by a DataNode if children have data
        if(data.getData() != 0) {
          if(i < parentLevel) {
            // We need to find the grand parent and find which child of the grand parent
            // the parent is to replace it
            Octree.Node grandparent = parents[i+1];
            int parentPosition = positions[i+1];
            grandparent.children[parentPosition] = new Octree.DataNode(data.type, data.getData());
          } else {
            // The parent is the root
            root = new Octree.DataNode(data.type, data.getData());
          }
        } else {
          parent.merge(data.type);
        }
        cacheLevel = FastMath.max(i, cacheLevel);
      } else {
        break;
      }
    }
  }

  @Override
  public Octree.Node get(int x, int y, int z) {
    while (cacheLevel < depth && ((x >>> cacheLevel) != cx ||
            (y >>> cacheLevel) != cy || (z >>> cacheLevel) != cz))
      cacheLevel += 1;

    Octree.Node node;
    while (true) {
      node = cache[cacheLevel];
      if (node.type != BRANCH_NODE) {
        break;
      }
      cacheLevel -= 1;
      cx = x >>> cacheLevel;
      cy = y >>> cacheLevel;
      cz = z >>> cacheLevel;
      cache[cacheLevel] =
              cache[cacheLevel + 1].children[((cx & 1) << 2) | ((cy & 1) << 1) | (cz & 1)];
    }
    return node;
  }

  @Override
  public Material getMaterial(int x, int y, int z, BlockPalette palette) {
    Octree.Node node = get(x, y, z);
    if (node.type == BRANCH_NODE) {
      return UnknownBlock.UNKNOWN;
    }
    return palette.get(node.type);
  }

  @Override
  public void store(DataOutputStream out) throws IOException {
    out.writeInt(depth);
    root.store(out);
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
    Octree.Node node;
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

      node = root;
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

      while (node.type == BRANCH_NODE) {
        level -= 1;
        lx = x >>> level;
        ly = y >>> level;
        lz = z >>> level;
        node = node.children[((lx & 1) << 2) | ((ly & 1) << 1) | (lz & 1)];
      }

      Block currentBlock = palette.get(node.type);
      Material prevBlock = ray.getCurrentMaterial();

      ray.setPrevMaterial(prevBlock, ray.getCurrentData());
      ray.setCurrentMaterial(currentBlock, node.getData());

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
    Octree.Node node;
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

      node = root;
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

      while (node.type == BRANCH_NODE) {
        level -= 1;
        lx = x >>> level;
        ly = y >>> level;
        lz = z >>> level;
        node = node.children[((lx & 1) << 2) | ((ly & 1) << 1) | (lz & 1)];
      }

      Block currentBlock = palette.get(node.type);
      Material prevBlock = ray.getCurrentMaterial();

      ray.setPrevMaterial(prevBlock, ray.getCurrentData());
      ray.setCurrentMaterial(currentBlock, node.type);

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
      if ((node.getData() & (1 << Water.FULL_BLOCK)) == 0) {
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

  public int getDepth() {
    return depth;
  }

  public static NodeBasedOctree load(DataInputStream in) throws IOException {
    int treeDepth = in.readInt();
    return new NodeBasedOctree(treeDepth, Octree.Node.loadNode(in));
  }
}
