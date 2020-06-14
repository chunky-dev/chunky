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

  public interface OctreeImplementation {
    void set(int type, int x, int y, int z);
    void set(Node data, int x, int y, int z);
    Node get(int x, int y, int z);
    Material getMaterial(int x, int y, int z, BlockPalette palette);
    void store(DataOutputStream output) throws IOException;
    boolean isInside(Vector3 pos);
    boolean enterBlock(Scene scene, Ray ray, BlockPalette palette);
    boolean exitWater(Scene scene, Ray ray, BlockPalette palette);
    int getDepth();
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
  public Octree(int octreeDepth) {
    if(usePacked)
      implementation = new PackedOctree(octreeDepth);
    else
      implementation = new NodeBasedOctree(octreeDepth, new Node(0));
  }

  public Octree(int octreeDepth, Node node) {
    implementation = new NodeBasedOctree(octreeDepth, node);
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
      switchToNodeBased();
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
      switchToNodeBased();
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
  public static Octree load(DataInputStream in) throws IOException {
    if(usePacked) {
      return new Octree(PackedOctree.load(in));
    } else {
      return new Octree(NodeBasedOctree.load(in));
    }
  }

  /**
   * Deserialize the octree from a data input stream.
   * @param forceNodeBased Forces the tree to be deserialized as a NodeBasedOctree
   * @return The deserialized octree
   * @throws IOException
   */
  public static Octree load(DataInputStream in, boolean forceNodeBased) throws IOException {
    if(forceNodeBased) {
      return new Octree(NodeBasedOctree.load(in));
    } else {
      return load(in);
    }
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
   * Replace the implementation for the packed one
   */
  public void pack() {
    if(usePacked) {
      if(implementation instanceof NodeBasedOctree) {
        try {
          implementation = new PackedOctree(implementation.getDepth(), ((NodeBasedOctree) implementation).root);
        } catch(PackedOctree.OctreeTooBigException e) {
          // If octree is too big, do nothing, keep the node based implementation
        }
      }
    }
  }

  private void switchToNodeBased() {
    if(implementation instanceof PackedOctree) {
      implementation = ((PackedOctree) implementation).toNodeBasedOctree();
    }
  }

}
