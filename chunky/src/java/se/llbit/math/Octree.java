/* Copyright (c) 2010-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2010-2021 Chunky contributors
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
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;
import org.apache.commons.math3.util.FastMath;

import se.llbit.chunky.block.Void;
import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.block.Block;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.WaterPlaneMaterial;
import se.llbit.log.Log;
import se.llbit.util.io.PositionalInputStream;
import se.llbit.util.io.PositionalOutputStream;
import se.llbit.util.TaskTracker;

/**
 * A simple voxel Octree.
 *
 * An octree is like a binary tree, except instead of being "bi"nary, it is an
 * "oct"tree, where each parent node has eight children instead of two. Octrees
 * are better suited for 3d scenes such as storing voxels (since subdividing a
 * cube gives 8 cubes of half the side length.)
 *
 * In this class, blocks are stored such that a ray that is traversing the scene
 * can determine what block it is in or will hit in O(log(n)) time.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class Octree implements Intersectable {

  public interface OctreeImplementation {
    void set(int type, int x, int y, int z);
    Material getMaterial(int x, int y, int z, BlockPalette palette);
    void store(DataOutputStream output) throws IOException;
    int getDepth();
    long nodeCount();
    NodeId getRoot();
    boolean isBranch(NodeId node);
    NodeId getChild(NodeId parent, int childNo);
    int getType(NodeId node);
    default void startFinalization() {}
    default void endFinalization() {}
    default void getWithLevel(IntIntMutablePair outTypeAndLevel, int x, int y, int z) {
      NodeId node = getRoot();
      int level = getDepth();
      while(isBranch(node)) {
        level -= 1;
        int lx = x >>> level;
        int ly = y >>> level;
        int lz = z >>> level;
        node = getChild(node, (((lx & 1) << 2) | ((ly & 1) << 1) | (lz & 1)));
      }
      outTypeAndLevel.right(level).left(getType(node));
    }

    /**
     * Set a whole 2^n * 2^n * 2^n cube of blocks
     * @param cubeDepth the n
     * @param types a flat array representation of a 3d array of the types to insert indexed by z then y then x
     * @param x the x of the position of the mi corner of the cube
     * @param y the y of the position of the mi corner of the cube
     * @param z the z of the position of the mi corner of the cube
     */
    default void setCube(int cubeDepth, int[] types, int x, int y, int z) {
      // Default implementation sets block one by one
      int size = 1 << cubeDepth;
      assert x % size == 0;
      assert y % size == 0;
      assert z % size == 0;
      for(int localZ = 0; localZ < size; ++localZ) {
        for(int localY = 0; localY < size; ++localY) {
          for(int localX = 0; localX < size; ++localX) {
            int globalX = x + localX;
            int globalY = y + localY;
            int globalZ = z + localZ;
            int index = (localZ * size + localY) * size + localX;
            set(types[index], globalX, globalY, globalZ);
          }
        }
      }
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
        node = new Node(type);
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

    @Deprecated
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
   * Timestamp of last serialization.
   */
  private long timestamp = 0;

  protected OctreeImplementation implementation;

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
   * Get the material at the given position (relative to the octree origin).
   * @param x x position
   * @param y y position
   * @param z z position
   * @param palette Block palette
   * @return Material at the given position or {@link Void#INSTANCE} if the position is outside of this octree
   */
  public Material getMaterial(int x, int y, int z, BlockPalette palette) {
    int size = (1 << implementation.getDepth());
    if(x < 0 || y < 0 || z < 0 || x >= size || y >= size || z >= size)
      return Void.INSTANCE;
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
  private double enterOctree(Ray ray, IntersectionRecord intersectionRecord) {
    double nx = 0, ny = 0, nz = 0;
    double octree_size = 1 << getDepth();

    double tMin = Double.NEGATIVE_INFINITY;
    double tMax = Double.POSITIVE_INFINITY;

    // Note: The following is made to be robust against edge cases (infinity/NaN)
    // without additional branches, be careful when editing.

    // Explanation:
    // the ray can have its x coordinate be 0, which mean that invDirX is Infinity.
    // This is not a problem as math works out well in this case:

    // If we have 0 < ray.o.x < octree_size, meaning the ray is correctly aligned in
    // x to intersect with the octree, [tXMin ; tXMax] will have the values
    // [-Infinity ; Infinity] meaning its intersection with the interval [tMin ; tMax]
    // will keep the value [tMin ; tMax].

    // On the other hand if ray.o.x < 0 or if ray.o.x > octree_size then both
    // tXMin and tXMax will have the value -Infinity (resp. +Infinity)
    // Meaning the interval [tXMin ; tXMax] (and [tMin ; tMax] once it is updated to be the intersection)
    // will be reduced to a single values: -Infinity (resp. +Infinity)
    // (this description is not mathematically rigorous as infinity is not really a value
    // but is enough here).
    // As ray.d is not a null vector, at least one of its component is not 0
    // and will have an associated interval with finite bounds, meaning the intersection of those interval
    // will give an empty set (in practice this is the condition
    // `if ((tMin > tXMax) || (tXMin > tMax)) return false;`) meaning no intersection is possible.

    // Lastly the other edge case (literally) is when we have ray.o.x == 0 or
    // ray.o.x == octree_size, meaning the ray is right on the edge and is
    // neither really outside nor inside.
    // In this case tXMin or tXMax will be NaN (and the other will be +/- Infinity but
    // that will not pose any problem as seen previously).
    // Every comparison involving a NaN returns false, this means that when doing the intersection
    // with [tMin ; tMax] (in practice the `if (tXMin > tMin) tMin = tXMin;` and
    // `if (tXMax < tMax) tMax = tXMax;`), the interval will not be changed, acting
    // as if the [tXMin ; tXMax] interval was [-Infinity ; +Infinity] and not disrupting
    // following computations.
    // Additionally, given how the condition to test whether the intersection will be empty before
    // updating it is written, (the `if ((tMin > tXMax) || (tXMin > tMax))`), it will
    // evaluate to false and not exit the function. This means that the ray is
    // considered to be intersecting with the octree (if the other coordinates fulfill the condition
    // to be intersecting as well).
    // If instead we would like rays on the edge of the octree not to be considered intersecting it,
    // the condition could be rewritten as `if(!(tMin <= tXMax) || !(tXMin <= tMax))`
    // which is equivalent to the current condition for every input not involving
    // NaN but will evaluates to true in the presence of NaN.

    // AABB intersection with the octree boundary
    double tXMin, tXMax;
    double invDirX = 1 / ray.d.x;
    if (invDirX >= 0) {
      tXMin = -ray.o.x * invDirX;
      tXMax = (octree_size - ray.o.x) * invDirX;
    } else {
      tXMin = (octree_size - ray.o.x) * invDirX;
      tXMax = -ray.o.x * invDirX;
    }

    if ((tMin > tXMax) || (tXMin > tMax))
      return Double.NaN;

    if (tXMin > tMin) {
      tMin = tXMin;

      nx = -FastMath.signum(ray.d.x);
      ny = nz = 0;
    }

    if (tXMax < tMax)
      tMax = tXMax;

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
      return Double.NaN;

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
      return Double.NaN;

    if (tZMin > tMin) {
      tMin = tZMin;

      nz = -FastMath.signum(ray.d.z);
      nx = ny = 0;
    }

    if (tMin < 0)
      return Double.NaN;

    intersectionRecord.setNormal(nx, ny, nz);
    return tMin;
  }

  /**
   * Intersects the ray with the geometry inside the octree.
   *
   * @return {@code false} if the ray did not hit the geometry
   */
  @Override
  public boolean closestIntersection(Ray ray, IntersectionRecord intersectionRecord, Scene scene, Random random) {
    BlockPalette palette = scene.getPalette();
    double distance = 0;
    IntIntMutablePair typeAndLevel = new IntIntMutablePair(0, 0);
    Vector3 invDir = new Vector3(
      1.0 / ray.d.x,
      1.0 / ray.d.y,
      1.0 / ray.d.z
    );
    Vector3 offset = new Vector3(
      -ray.o.x * invDir.x,
      -ray.o.y * invDir.y,
      -ray.o.z * invDir.z
    );
    Vector3 pos = new Vector3(ray.o);
    Ray testRay = new Ray(ray);

    // Check if we are in-bounds
    if (!isInside(ray.o)) {
      double dist = enterOctree(ray, intersectionRecord);
      if (Double.isNaN(dist)) {
        return false;
      }
      distance += dist;
    }

    // Marching is done in a top-down fashion: at each step, the octree is descended from the root to find the leaf
    // node the ray is in. Terminating the march is then decided based on the block type in that leaf node. Finally the
    // ray is advanced to the boundary of the current leaf node and the next, ready for the next iteration.
    while (true) {
      // Add small offset past the intersection to avoid
      // recursion to the same octree node!
      pos.set(ray.o);
      pos.scaleAdd(distance + Constants.OFFSET, ray.d);
      int bx = (int) Math.floor(pos.x);
      int by = (int) Math.floor(pos.y);
      int bz = (int) Math.floor(pos.z);

      if (!isInside(pos)) {
        Block currentBlock = scene.isUnderWaterPlane(pos) ? WaterPlaneMaterial.INSTANCE : Void.INSTANCE;
        Material prevBlock = ray.getCurrentMedium();
        if (currentBlock.isSameMaterial(prevBlock)
            || currentBlock == Void.INSTANCE && prevBlock == Air.INSTANCE) {
          return false;
        }
        testRay.o.set(ray.o);
        testRay.o.scaleAdd(distance, ray.d);
        intersectionRecord.material = currentBlock;
        currentBlock.getColor(intersectionRecord);
        intersectionRecord.distance = distance;
        return true;
      }

      implementation.getWithLevel(typeAndLevel, bx, by, bz);
      int type = typeAndLevel.leftInt();
      int level = typeAndLevel.rightInt();

      int lx = bx >>> level;
      int ly = by >>> level;
      int lz = bz >>> level;

      // Test intersection
      Block currentBlock = palette.get(type);
      if (scene.isUnderWaterPlane(pos) && (currentBlock == Air.INSTANCE || currentBlock == Void.INSTANCE)) {
        currentBlock = WaterPlaneMaterial.INSTANCE;
      }
      Material prevBlock = ray.getCurrentMedium();

      intersectionRecord.material = currentBlock;

      boolean hit = false;
      if (!currentBlock.hidden) {
        if (currentBlock.localIntersect) {
          testRay.o.set(ray.o);
          testRay.o.scaleAdd(distance, ray.d);
          if (currentBlock.intersect(testRay, intersectionRecord, scene)) {
            intersectionRecord.distance += distance;
            hit = true;
          } else {
            intersectionRecord.distance = Double.POSITIVE_INFINITY;
            distance += exitBlock(testRay, intersectionRecord, bx, by, bz);
            continue;
          }
        } else if (!currentBlock.isSameMaterial(prevBlock) && !(currentBlock == Void.INSTANCE && prevBlock == Air.INSTANCE || currentBlock == Air.INSTANCE && prevBlock == Void.INSTANCE)) {
          // && !((currentBlock == Void.INSTANCE && prevBlock == Air.INSTANCE || currentBlock == Air.INSTANCE && prevBlock == Void.INSTANCE) && (!scene.isWaterPlaneEnabled() || !scene.getWaterPlaneChunkClip()))
          testRay.o.set(ray.o);
          testRay.o.scaleAdd(distance, ray.d);
          TexturedBlockModel.getIntersectionColor(testRay, intersectionRecord);
          intersectionRecord.distance = distance;
          return true;
        }
      }

      if (hit) {
        return true;
      }

      // No intersection, exit current octree leaf.
      int nx = 0, ny = 0, nz = 0;
      double tNear = Double.POSITIVE_INFINITY;

      // Testing all six sides of the current leaf node and advancing to the closest intersection
      // Every side is unconditionally tested because the origin of the ray can be outside the block
      // The computation involves a multiplication and an addition so we could use a fma (need java 9+)
      // but according to measurement, performance are identical
      double t = (lx << level) * invDir.x + offset.x;
      if (t > distance + Constants.OFFSET) {
        tNear = t;
        nx = 1;
      }
      t = ((lx + 1) << level) * invDir.x + offset.x;
      if (t < tNear && t > distance + Constants.OFFSET) {
        tNear = t;
        nx = -1;
      }

      t = (ly << level) * invDir.y + offset.y;
      if (t < tNear && t > distance + Constants.OFFSET) {
        tNear = t;
        ny = 1;
        nx = 0;
      }
      t = ((ly + 1) << level) * invDir.y + offset.y;
      if (t < tNear && t > distance + Constants.OFFSET) {
        tNear = t;
        ny = -1;
        nx = 0;
      }

      t = (lz << level) * invDir.z + offset.z;
      if (t < tNear && t > distance + Constants.OFFSET) {
        tNear = t;
        nz = 1;
        nx = ny = 0;
      }
      t = ((lz + 1) << level) * invDir.z + offset.z;
      if (t < tNear && t > distance + Constants.OFFSET) {
        tNear = t;
        nz = -1;
        nx = ny = 0;
      }

      intersectionRecord.setNormal(nx, ny, nz);

      distance = tNear;
    }
  }

  /**
   * Find the exit point from the given block for this ray. This marches the ray forward - i.e.
   * updates ray origin directly.
   *
   * @param bx block x coordinate
   * @param by block y coordinate
   * @param bz block z coordinate
   */
  public double exitBlock(Ray ray, IntersectionRecord intersectionRecord, int bx, int by, int bz) {
    int nx = 0;
    int ny = 0;
    int nz = 0;
    double tNext = Double.POSITIVE_INFINITY;
    double t = (bx - ray.o.x) / ray.d.x;
    if (t > Constants.EPSILON) {
      tNext = t;
      nx = 1;
      ny = nz = 0;
    } else {
      t = ((bx + 1) - ray.o.x) / ray.d.x;
      if (t < tNext && t > Constants.EPSILON) {
        tNext = t;
        nx = -1;
        ny = nz = 0;
      }
    }

    t = (by - ray.o.y) / ray.d.y;
    if (t < tNext && t > Constants.EPSILON) {
      tNext = t;
      ny = 1;
      nx = nz = 0;
    } else {
      t = ((by + 1) - ray.o.y) / ray.d.y;
      if (t < tNext && t > Constants.EPSILON) {
        tNext = t;
        ny = -1;
        nx = nz = 0;
      }
    }

    t = (bz - ray.o.z) / ray.d.z;
    if (t < tNext && t > Constants.EPSILON) {
      tNext = t;
      nz = 1;
      nx = ny = 0;
    } else {
      t = ((bz + 1) - ray.o.z) / ray.d.z;
      if (t < tNext && t > Constants.EPSILON) {
        tNext = t;
        nz = -1;
        nx = ny = 0;
      }
    }

    intersectionRecord.setNormal(nx, ny, nz);
    return tNext;
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

  public void setCube(int cubeDepth, int[] types, int x, int y, int z) {
    implementation.setCube(cubeDepth, types, x, y, z);
  }

  public void switchImplementation(String newImplementation) throws IOException {
    switchImplementation(newImplementation, TaskTracker.Task.NONE);
  }

  /**
   * Switch between any two implementation by reusing the load and store methods of
   * the octree implementations
   * @param newImplementation The new Octree implementation
   */
  public void switchImplementation(String newImplementation, TaskTracker.Task task) throws IOException {
    ImplementationFactory factory = getImplementationFactory(newImplementation);
    if(factory.isOfType(implementation)) {
      // Already correct implementation
      return;
    }

    Log.infof("Changing octree implementation (%s)", newImplementation);

    // This function is called as to provide a fallback when
    // an implementation isn't suitable, we assume it means
    // that Chunky is already using a lot of memory, so we save the octree on disk
    // and reload it with another implementation
    long nodeCount = implementation.nodeCount();
    double writeProgressScaler = 62.5 / nodeCount;
    if (nodeCount != 0) {
      task.update(1000, 0);
    } else {
      task.update(2, 1);
    }

    File tempFile = File.createTempFile("octree-conversion", ".bin");
    try {
      try (DataOutputStream out = new DataOutputStream(new FastBufferedOutputStream(new PositionalOutputStream(Files.newOutputStream(tempFile.toPath()), position -> {
        if (nodeCount != 0) {
          int progress = (int) Math.min(position * writeProgressScaler, 500);
          task.updateInterval(progress, 1);
        }
      })))) {
        implementation.store(out);
      }
      // Allow the GC to free memory during construction of the new octree
      // Replace with an empty octree to prevent any NPE's
      implementation = new PackedOctree(1);

      double readProgressScaler = 500.0 / tempFile.length();
      try (DataInputStream in = new DataInputStream(new FastBufferedInputStream(new PositionalInputStream(Files.newInputStream(tempFile.toPath()), position -> {
        task.updateInterval(1000, (int) (position * readProgressScaler) + 500, 1);
      })))) {
        implementation = factory.loadWithNodeCount(nodeCount, in);
      }
    } finally {
      if (!tempFile.delete()) {
        Log.warnf("Failed to delete temporary file:\n%s", tempFile);
        tempFile.deleteOnExit();
      }
    }
  }

  @PluginApi
  public OctreeImplementation getImplementation() {
    return implementation;
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
