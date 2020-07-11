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
import java.util.concurrent.atomic.AtomicReference;

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

  public interface OctreeImplementation {
    void set(int type, int x, int y, int z);
    void set(Node data, int x, int y, int z);
    Node get(int x, int y, int z);
    Material getMaterial(int x, int y, int z, BlockPalette palette);
    void store(DataOutputStream output) throws IOException;
    boolean isInside(Vector3 pos);

    /**
     * Intersects the ray with the geometry inside the octree.
     *
     * @return {@code false} if the ray did not hit the geometry
     */
    boolean enterBlock(Scene scene, Ray ray, BlockPalette palette);

    /**
     * Advance the ray until it leaves the current water body.
     *
     * @return {@code false} if the ray doesn't hit anything
     */
    boolean exitWater(Scene scene, Ray ray, BlockPalette palette);
    int getDepth();
    long nodeCount();
  }

  public interface ImplementationFactory {
    OctreeImplementation create(int depth);
    OctreeImplementation load(DataInputStream in) throws IOException;
    OctreeImplementation loadWithNodeCount(long nodeCount, DataInputStream in) throws IOException;
    boolean isOfType(OctreeImplementation implementation);
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

  static private boolean usePacked = !System.getProperty("chunky.useLegacyOctree", "false").equals("true");

  /**
   * Create a new Octree. The dimensions of the Octree
   * are 2^levels.
   *
   * @param octreeDepth The number of levels in the Octree.
   */
  public Octree(String impl, int octreeDepth) {
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
        ioException.printStackTrace();
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
        ioException.printStackTrace();
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
    return new Octree(getImplementationFactory(impl).load(in));
  }

  /**
   * Test if a point is inside the octree.
   *
   * @param o vector
   * @return {@code true} if the vector is inside the octree
   */
  public boolean isInside(Vector3 o) {
    return implementation.isInside(o);
  }

  /**
   * Advance the ray until it intersects with the geometry inside the octree
   * @return {@code false} if the ray didn't intersect
   */
  public boolean enterBlock(Scene scene, Ray ray, BlockPalette palette) {
    return implementation.enterBlock(scene, ray, palette);
  }

  /**
   * Advance the ray until it leaves the current water body.
   */
  public boolean exitWater(Scene scene, Ray ray, BlockPalette palette) {
    return implementation.exitWater(scene, ray, palette);
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

    // Here we use a PipedStream to pipe what the old implementation stores
    // to what the new will load.
    // With this method both octree will exist side-by-side during the conversion
    // If this proves to be a problem, we could store to a temporary file instead
    long nodeCount = implementation.nodeCount();
    AtomicReference<OctreeImplementation> newOctree = new AtomicReference<>();
    PipedOutputStream out = new PipedOutputStream();
    PipedInputStream in = new PipedInputStream(out);
    Thread readerThread = new Thread(() -> {
      try {
        OctreeImplementation impl = factory.loadWithNodeCount(nodeCount, new DataInputStream(in));
        newOctree.set(impl);
      } catch(IOException e) {
        e.printStackTrace();
      }
    });
    readerThread.start();
    implementation.store(new DataOutputStream(out));
    try {
      readerThread.join();
    } catch(InterruptedException e) {
      e.printStackTrace();
    }
    in.close();
    out.close();

    implementation = newOctree.get();
  }

  public static void addImplementationFactory(String name, ImplementationFactory factory) {
    factories.put(name, factory);
  }

  static {
    NodeBasedOctree.initImplementation();
    PackedOctree.initImplementation();
    BigPackedOctree.initImplementation();
  }
}
