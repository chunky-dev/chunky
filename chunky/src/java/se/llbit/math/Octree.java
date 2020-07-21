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

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.util.FastMath;

import org.apache.commons.math3.util.Pair;
import se.llbit.chunky.block.Air;
import se.llbit.chunky.block.Block;
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

  public interface OctreeImplementation {
    void set(int type, int x, int y, int z);
    void set(Node data, int x, int y, int z);
    Node get(int x, int y, int z);
    Material getMaterial(int x, int y, int z, BlockPalette palette);
    void store(DataOutputStream output) throws IOException;
    int getDepth();
    long nodeCount();
    NodeId getRoot();
    boolean isBranch(NodeId node);
    NodeId getChild(NodeId parent, int childNo);
    int getType(NodeId node);
    int getData(NodeId node);
    default void startFinalization() {}
    default void endFinalization() {}
    default Pair<NodeId, Integer> getWithLevel(int x, int y, int z) {
      NodeId node = getRoot();
      int level = getDepth();
      while(isBranch(node)) {
        level -= 1;
        int lx = x >>> level;
        int ly = y >>> level;
        int lz = z >>> level;
        node = getChild(node, (((lx & 1) << 2) | ((ly & 1) << 1) | (lz & 1)));
      }
      return new Pair<>(node, level);
    }
  }

  public interface NodeId {}

  public interface ImplementationFactory {
    OctreeImplementation create(int depth);
    OctreeImplementation load(DataInputStream in) throws IOException;
    OctreeImplementation loadWithNodeCount(long nodeCount, DataInputStream in) throws IOException;
    boolean isOfType(OctreeImplementation implementation);
    String getDescription();
  }

  static private Map<String, ImplementationFactory> factories = new HashMap<>();
  static public final String DEFAULT_IMPLEMENTATION = "PACKED";
  static private ImplementationFactory getImplementationFactory(String implementation) {
    if(factories.containsKey(implementation))
      return factories.get(implementation);
    Log.warn(String.format("Unknown octree implementation specified, using %s", DEFAULT_IMPLEMENTATION));
    return factories.get(DEFAULT_IMPLEMENTATION);
  }

  public static final int BRANCH_NODE = -1;

  /**
   * A special type that indicate that we don't care about nodes with this type
   * (The value is chosen to behave like a normal type i.e first bit not set
   * and so that when serialized with data, it is not confused for a branch node)
   */
  public static final int ANY_TYPE = 0x7FFFFFFE;

  /**
   * The top bit of the type field in a serialized octree node is reserved for indicating
   * if the node is a data node.
   */
  public static final int DATA_FLAG = 0x80000000;

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
      type = BRANCH_NODE;
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
     * Serializes this node to a data stream.
     */
    public void store(DataOutputStream out) throws IOException {
      out.writeInt(type);
      if (type == BRANCH_NODE) {
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
    public static Node loadNode(DataInputStream in) throws IOException {
      int type = in.readInt();
      Node node;
      if (type == BRANCH_NODE) {
        node = new Node(BRANCH_NODE);
        node.children = new Node[8];
        for (int i = 0; i < 8; ++i) {
          node.children[i] = loadNode(in);
        }
      } else {
        if ((type & DATA_FLAG) == 0) {
          node = new Node(type);
        } else {
          int data = in.readInt();
          node = new DataNode(type ^ DATA_FLAG, data);
        }
      }
      return node;
    }

    public void visit(OctreeVisitor visitor, int x, int y, int z, int depth) {
      if (type == BRANCH_NODE) {
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

    @Override public void store(DataOutputStream out) throws IOException {
      out.writeInt(type | DATA_FLAG);
      if (type == BRANCH_NODE) {
        for (int i = 0; i < 8; ++i) {
          children[i].store(out);
        }
      } else {
        out.writeInt(data);
      }
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
   * Timestamp of last serialization.
   */
  private long timestamp = 0;

  private OctreeImplementation implementation;

  /**
   * Create a new Octree. The dimensions of the Octree
   * are 2^levels.
   *
   * @param octreeDepth The number of levels in the Octree.
   */
  public Octree(String impl, int octreeDepth) {
    Log.infof("Building new octree (%s)", impl);
    implementation = getImplementationFactory(impl).create(octreeDepth);
  }

  protected Octree(OctreeImplementation impl) {
    implementation = impl;
  }

  /**
   * Set the voxel type at the given coordinates.
   *
   * @param type The new voxel type to be set
   */
  public synchronized void set(int type, int x, int y, int z) {
    try {
      implementation.set(type, x, y, z);
    } catch(PackedOctree.OctreeTooBigException e) {
      // Octree is too big, switch implementation and retry
      Log.warn("Octree is too big, falling back to old (slower and bigger) implementation.");
      try {
        switchImplementation("NODE");
      } catch(IOException ioException) {
        throw new RuntimeException("Couldn't switch the octree implementation to NODE", ioException);
      }
      implementation.set(type, x, y, z);
    }
  }

  /**
   * Set the voxel type at the given coordinates.
   *
   * @param data The new voxel to insert.
   */
  public synchronized void set(Node data, int x, int y, int z) {
    try {
      implementation.set(data, x, y, z);
    } catch(PackedOctree.OctreeTooBigException e) {
      // Octree is too big, switch implementation and retry
      Log.warn("Octree is too big, falling back to old (slower and bigger) implementation.");
      try {
        switchImplementation("NODE");
      } catch(IOException ioException) {
        throw new RuntimeException("Couldn't switch the octree implementation to NODE", ioException);
      }
      implementation.set(data, x, y, z);
    }
  }

  /**
   * @return The voxel type at the given coordinates
   */
  public synchronized Node get(int x, int y, int z) {
    return implementation.get(x, y, z);
  }

  public Material getMaterial(int x, int y, int z, BlockPalette palette) {
    return implementation.getMaterial(x, y, z, palette);
  }

  /**
   * Serialize this octree to a data output stream.
   *
   * @throws IOException
   */
  public void store(DataOutputStream out) throws IOException {
    implementation.store(out);
  }

  /**
   * Deserialize the octree from a data input stream.
   *
   * @return The deserialized octree
   * @throws IOException
   */
  public static Octree load(String impl, DataInputStream in) throws IOException {
    Log.infof("Loading octree (%s)", impl);
    return new Octree(getImplementationFactory(impl).load(in));
  }

  /**
   * Test if a point is inside the octree.
   *
   * @param o vector
   * @return {@code true} if the vector is inside the octree
   */
  public boolean isInside(Vector3 o) {
    int depth = implementation.getDepth();

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
    double octree_size = 1 << getDepth();

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
   * Intersects the ray with the geometry inside the octree.
   *
   * @return {@code false} if the ray did not hit the geometry
   */
  public boolean enterBlock(Scene scene, Ray ray, BlockPalette palette) {
    if (!isInside(ray.o) && !enterOctree(ray))
      return false;

    int depth = implementation.getDepth();

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

      Pair<NodeId, Integer> nodeAndLevel = implementation.getWithLevel(x, y, z);
      NodeId node = nodeAndLevel.getFirst();
      int level = nodeAndLevel.getSecond();

      lx = x >>> level;
      ly = y >>> level;
      lz = z >>> level;

      // Test intersection
      Block currentBlock = palette.get(implementation.getType(node));
      Material prevBlock = ray.getCurrentMaterial();

      ray.setPrevMaterial(prevBlock, ray.getCurrentData());
      ray.setCurrentMaterial(currentBlock, implementation.getData(node));

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
   * Advance the ray until it leaves the current water body.
   *
   * @return {@code false} if the ray doesn't hit anything
   */
  public boolean exitWater(Scene scene, Ray ray, BlockPalette palette) {
    if (!isInside(ray.o) && !enterOctree(ray))
      return false;

    int depth = getDepth();
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
      Pair<NodeId, Integer> nodeAndLevel = implementation.getWithLevel(x, y, z);
      NodeId node = nodeAndLevel.getFirst();
      int level = nodeAndLevel.getSecond();

      // Test intersection
      Block currentBlock = palette.get(implementation.getType(node));
      Material prevBlock = ray.getCurrentMaterial();

      ray.setPrevMaterial(prevBlock, ray.getCurrentData());
      ray.setCurrentMaterial(currentBlock, implementation.getData(node));

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

      if ((implementation.getData(node) & (1 << Water.FULL_BLOCK)) == 0) {
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

  public int getDepth() {
    return implementation.getDepth();
  }

  public void startFinalization() {
    implementation.startFinalization();
  }

  public void endFinalization() {
    implementation.endFinalization();
  }

  /**
   * Switch between any two implementation by reusing the load and store methods of
   * the octree implementations
   * @param newImplementation The new Octree implementation
   */
  public void switchImplementation(String newImplementation) throws IOException {
    ImplementationFactory factory = getImplementationFactory(newImplementation);
    if(factory.isOfType(implementation)) {
      // Already correct implementation
      return;
    }

    Log.infof("Changing octree implementation (%s)", newImplementation);

    // This function is called as to provide a fallback when
    // an implementation isn't suitable, we assume it means
    // that chunky is already using a lot of memory so we save the octree on disk
    // and reload it with another implementation
    long nodeCount = implementation.nodeCount();
    File tempFile = File.createTempFile("octree-conversion", ".bin");
    DataOutputStream out = new DataOutputStream(new FileOutputStream(tempFile));
    implementation.store(out);
    out.flush();
    out.close();
    implementation = null; // Allow th gc to free memory during construction of the new octree

    DataInputStream in = new DataInputStream(new FileInputStream(tempFile));
    implementation = factory.loadWithNodeCount(nodeCount, in);
    in.close();

    tempFile.delete();
  }

  public static void addImplementationFactory(String name, ImplementationFactory factory) {
    factories.put(name, factory);
  }

  static {
    NodeBasedOctree.initImplementation();
    PackedOctree.initImplementation();
    BigPackedOctree.initImplementation();
  }

  public static Iterable<Map.Entry<String, ImplementationFactory>> getEntries() {
    return factories.entrySet();
  }
}
