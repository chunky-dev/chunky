package se.llbit.math;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.block.UnknownBlock;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.world.Material;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static se.llbit.math.Octree.*;

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

  private static final class NodeId implements Octree.NodeId {
    public Octree.Node node;

    public NodeId(Octree.Node node) {
      this.node = node;
    }
  }

  @Override
  public Octree.NodeId getRoot() {
    return new NodeId(root);
  }

  @Override
  public boolean isBranch(Octree.NodeId node) {
    return ((NodeId)node).node.type == BRANCH_NODE;
  }

  @Override
  public Octree.NodeId getChild(Octree.NodeId parent, int childNo) {
    return new NodeId(((NodeId)parent).node.children[childNo]);
  }

  @Override
  public int getType(Octree.NodeId node) {
    return ((NodeId)node).node.type;
  }

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
        parent.merge(data.type);
        cacheLevel = FastMath.max(i, cacheLevel);
      } else {
        break;
      }
    }
  }

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

  public int getDepth() {
    return depth;
  }

  public static NodeBasedOctree load(DataInputStream in) throws IOException {
    int treeDepth = in.readInt();
    return new NodeBasedOctree(treeDepth, Octree.Node.loadNode(in));
  }

  @Override
  public long nodeCount() {
    return countNodes(root);
  }

  private long countNodes(Octree.Node node) {
    if(node.type == BRANCH_NODE) {
      long total = 1;
      for(int i = 0; i < 8; ++i)
        total += countNodes(node.children[i]);
      return total;
    } else {
      return 1;
    }
  }

  @Override
  public void endFinalization() {
    if (root.children != null) {
      // There is a bunch of ANY_TYPE nodes we should try to merge
      finalizationNode(root, null, 0);
    }
  }

  private void finalizationNode(Octree.Node node, Octree.Node parent, int childNo) {
    boolean canMerge = true;
    int mergedType = ANY_TYPE;
    for(int i = 0; i < 8; ++i) {
      Octree.Node child = node.children[i];
      if(child.type == BRANCH_NODE) {
        finalizationNode(child, node, i);// The node may have been merged, retest if it still a branch node
        child = node.children[i];
        if(child.type == BRANCH_NODE) {
          canMerge = false;
        }
      }
      if(canMerge) {
        if(mergedType == ANY_TYPE) {
          mergedType = child.type;
        } else if(!(child.type == ANY_TYPE || child.type == mergedType)) {
          canMerge = false;
        }
      }
    }
    if(canMerge) {
      node.merge(mergedType);
    }
  }

  static public void initImplementation() {
    Octree.addImplementationFactory("NODE", new Octree.ImplementationFactory() {
      @Override
      public Octree.OctreeImplementation create(int depth) {
        return new NodeBasedOctree(depth, new Octree.Node(0));
      }

      @Override
      public Octree.OctreeImplementation load(DataInputStream in) throws IOException {
        return NodeBasedOctree.load(in);
      }

      @Override
      public Octree.OctreeImplementation loadWithNodeCount(long nodeCount, DataInputStream in) throws IOException {
        return NodeBasedOctree.load(in);
      }

      @Override
      public boolean isOfType(Octree.OctreeImplementation implementation) {
        return implementation instanceof NodeBasedOctree;
      }

      @Override
      public String getDescription() {
        return "The legacy octree implementation, memory inefficient but can work with scene of any size.";
      }
    });
  }
}
