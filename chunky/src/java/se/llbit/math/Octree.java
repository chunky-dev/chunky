/* Copyright (c) 2010-2019 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.block.Air;
import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.UnknownBlock;
import se.llbit.chunky.block.Water;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.model.WaterModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.world.Material;
import se.llbit.log.Log;

/**
 * A simple voxel Octree.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class Octree {


  /** An Octree node. */
  public static class Node {
    /**
     * The node type. Type is -1 if it's a non-leaf node.
     */
    public int type;

    /**
     * Child array
     */
    public Node[] children;

    /**
     * Create new octree leaf node with the given type.
     */
    public Node(int type) {
      this.type = type;
    }

    /**
     * Subdivide this leaf node.
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
     */
    public final void merge(int newType) {
      type = newType;
      children = null;
    }

    /**
     * Serialize this node.
     *
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
     * Deserialize node.
     *
     * @return the number of loaded octree nodes.
     */
    public int load(DataInputStream in) throws IOException {
      type = in.readInt();
      int children = 0;
      if (type == -1) {
        this.children = new Node[8];
        for (int i = 0; i < 8; ++i) {
          this.children[i] = new Node(0);
          children += this.children[i].load(in);
        }
      }
      return children + 1;
    }

    public void visit(OctreeVisitor visitor, int x, int y, int z, int depth) {
      if (type == -1) {
        int cx = x << 1;
        int cy = y << 1;
        int cz = z << 1;
        children[0].visit(visitor, cx, cy, cz, depth - 1);
        children[1].visit(visitor, cx, cy, cz | 1, depth - 1);
        children[2].visit(visitor, cx, cy | 1, cz, depth - 1);
        children[3].visit(visitor, cx, cy | 1, cz | 1, depth - 1);
        children[4].visit(visitor, cx | 1, cy, cz, depth - 1);
        children[5].visit(visitor, cx | 1, cy, cz | 1, depth - 1);
        children[6].visit(visitor, cx | 1, cy | 1, cz, depth - 1);
        children[7].visit(visitor, cx | 1, cy | 1, cz | 1, depth - 1);
      } else {
        visitor.visit(type, x << depth, y << depth, z << depth, depth);
      }
    }

    public int getData() {
      return 0;
    }

    @Override public boolean equals(Object obj) {
      if (obj != null && obj.getClass() == Node.class) {
        return ((Node) obj).type == type;
      }
      return false;
    }
  }


  /**
   * An octree node with extra data.
   */
  static public final class DataNode extends Node {
    final int data;

    public DataNode(int type, int data) {
      super(type);
      this.data = data;
    }

    @Override public int getData() {
      return data;
    }

    @Override public boolean equals(Object obj) {
      if (obj instanceof DataNode) {
        DataNode node = ((DataNode) obj);
        return node.type == type && node.data == data;
      }
      return false;
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
   *
   * @param octreeDepth The number of levels in the Octree.
   */
  public Octree(int octreeDepth) {
    depth = octreeDepth;
    root = new Node(0);
    parents = new Node[depth];
    cache = new Node[depth + 1];
    cache[depth] = root;
    cacheLevel = depth;
  }

  /**
   * Set the voxel type at the given coordinates.
   *
   * @param type The new voxel type to be set
   */
  public synchronized void set(int type, int x, int y, int z) {
    set(new Node(type), x, y, z);
  }

  /**
   * Set the voxel type at the given coordinates.
   *
   * @param data The new voxel to insert.
   */
  public synchronized void set(Node data, int x, int y, int z) {
    Node node = root;
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
      node = node.children[position];

    }
    parents[0].children[position] = data;

    // Merge nodes where all children have been set to the same type.
    for (int i = 0; i <= parentLevel; ++i) {
      Node parent = parents[i];

      boolean allSame = true;
      for (Node child : parent.children) {
        if (!child.equals(node)) {
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
   * @return The voxel type at the given coordinates
   */
  public synchronized Node get(int x, int y, int z) {
    while (cacheLevel < depth && ((x >>> cacheLevel) != cx ||
        (y >>> cacheLevel) != cy || (z >>> cacheLevel) != cz))
      cacheLevel += 1;

    Node node;
    while (true) {
      node = cache[cacheLevel];
      if (node.type != -1) break;
      cacheLevel -= 1;
      cx = x >>> cacheLevel;
      cy = y >>> cacheLevel;
      cz = z >>> cacheLevel;
      cache[cacheLevel] =
          cache[cacheLevel + 1].children[((cx & 1) << 2) | ((cy & 1) << 1) | (cz & 1)];
    }
    return node;
  }

  public Material getMaterial(int x, int y, int z, BlockPalette palette) {
    Node node = get(x, y, z);
    if (node.type == -1) return UnknownBlock.UNKNOWN;
    return palette.get(node.type);
  }

  /**
   * Serialize this octree to a data output stream.
   *
   * @throws IOException
   */
  public void store(DataOutputStream out) throws IOException {
    out.writeInt(depth);
    root.store(out);
  }

  /**
   * Deserialize the octree from a data input stream.
   *
   * @return The deserialized octree
   * @throws IOException
   */
  public static Octree load(DataInputStream in) throws IOException {
    int treeDepth = in.readInt();
    Octree tree = new Octree(treeDepth);
    int size = tree.root.load(in);
    Log.info("Loaded octree with " + size + " nodes.");
    return tree;
  }

  /**
   * Test if a point is inside the octree.
   *
   * @param o vector
   * @return {@code true} if the vector is inside the octree
   */
  public boolean isInside(Vector3 o) {
    int x = (int) QuickMath.floor(o.x);
    int y = (int) QuickMath.floor(o.y);
    int z = (int) QuickMath.floor(o.z);

    int lx = x >>> depth;
    int ly = y >>> depth;
    int lz = z >>> depth;

    return lx == 0 && ly == 0 && lz == 0;
  }

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

      while (node.type == -1) {
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

  /**
   * Advance the ray until it leaves the current water body.
   */
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

      while (node.type == -1) {
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

  /**
   * Update the serialization timestamp.
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
