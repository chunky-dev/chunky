/* Copyright (c) 2014-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2014-2021 Chunky contributors
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

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.math.primitive.MutableAABB;
import se.llbit.math.primitive.Primitive;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

/**
 * Bounding Volume Hierarchy based on AABBs.
 *
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class BVH {

  /**
   * Indicates whether to use SAH for construction.
   */
  private static final Method METHOD = Method.MIDPOINT;


  private enum Method {
    MIDPOINT,
    SAH,
    SAH_MA,
  }

  /** Note: this is public for some plugins. Stability is not guaranteed. */
  public static abstract class Node {
    public final AABB bb;
    public final Primitive[] primitives;

    /**
     * Create a new BVH node.
     */
    public Node(Primitive[] primitives) {
      this.bb = bb(primitives);
      this.primitives = primitives;
    }

    /**
     * Create new BVH node with specific bounds.
     */
    public Node(AABB bb, Primitive[] primitives) {
      this.bb = bb;
      this.primitives = primitives;
    }

    abstract public boolean closestIntersection(Ray ray);

    abstract public boolean anyIntersection(Ray ray);

    abstract public int size();
  }

  /** Note: this is public for some plugins. Stability is not guaranteed. */
  public static class Group extends Node {
    public final Node child1;
    public final Node child2;
    private final int numPrimitives;

    /**
     * Create a new BVH node.
     */
    public Group(Node child1, Node child2) {
      super(child1.bb.expand(child2.bb), new Primitive[0]);
      this.numPrimitives = child1.size() + child2.size();
      this.child1 = child1;
      this.child2 = child2;
    }

    @Override public boolean closestIntersection(Ray ray) {
      double t1 = Double.POSITIVE_INFINITY;
      double t2 = Double.POSITIVE_INFINITY;
      if (child1.bb.inside(ray.o)) {
        t1 = 0;
      } else if (child1.bb.quickIntersect(ray)) {
        t1 = ray.tNext;
      }
      if (child2.bb.inside(ray.o)) {
        t2 = 0;
      } else if (child2.bb.quickIntersect(ray)) {
        t2 = ray.tNext;
      }

      boolean hit;
      if (t1 < t2) {
        hit = child1.closestIntersection(ray);
        hit = (t2 < ray.t && child2.closestIntersection(ray)) || hit;
      } else if (t2 < t1) {
        hit = child2.closestIntersection(ray);
        hit = (t1 < ray.t && child1.closestIntersection(ray)) || hit;
      } else {
        hit = (t1 != Double.POSITIVE_INFINITY) && child1.closestIntersection(ray);
        hit = ((t2 < ray.t) && child2.closestIntersection(ray)) || hit;
      }
      return hit;
    }

    @Override public boolean anyIntersection(Ray ray) {
      return (child1.bb.hitTest(ray) && child1.anyIntersection(ray)) || (child2.bb.hitTest(ray)
          && child2.anyIntersection(ray));
    }

    @Override public int size() {
      return numPrimitives;
    }
  }

  /** Note: this is public for some plugins. Stability is not guaranteed. */
  public static class Leaf extends Node {

    public Leaf(Primitive[] primitives) {
      super(primitives);
    }

    @Override public boolean closestIntersection(Ray ray) {
      boolean hit = false;
      for (Primitive primitive : primitives) {
        hit = primitive.intersect(ray) || hit;
      }
      return hit;
    }

    @Override public boolean anyIntersection(Ray ray) {
      for (Primitive primitive : primitives) {
        if (primitive.intersect(ray)) {
          return true;
        }
      }
      return false;
    }

    @Override public int size() {
      return primitives.length;
    }
  }


  private static final int SPLIT_LIMIT = 4;

  Node root;


  interface Selector {
    boolean select(AABB bounds, double split);
  }


  private final Comparator<Primitive> cmpX = (g1, g2) -> {
    AABB b1 = g1.bounds();
    AABB b2 = g2.bounds();
    double c1 = b1.xmin + (b1.xmax - b1.xmin) / 2;
    double c2 = b2.xmin + (b2.xmax - b2.xmin) / 2;
    return Double.compare(c1, c2);
  };
  private final Selector selectX = (bounds, split) -> {
    double centroid = bounds.xmin + (bounds.xmax - bounds.xmin) / 2;
    return centroid < split;
  };
  private final Comparator<Primitive> cmpY = (g1, g2) -> {
    AABB b1 = g1.bounds();
    AABB b2 = g2.bounds();
    double c1 = b1.ymin + (b1.ymax - b1.ymin) / 2;
    double c2 = b2.ymin + (b2.ymax - b2.ymin) / 2;
    return Double.compare(c1, c2);
  };
  private final Selector selectY = (bounds, split) -> {
    double centroid = bounds.ymin + (bounds.ymax - bounds.ymin) / 2;
    return centroid < split;
  };
  private final Comparator<Primitive> cmpZ = (g1, g2) -> {
    AABB b1 = g1.bounds();
    AABB b2 = g2.bounds();
    double c1 = b1.zmin + (b1.zmax - b1.zmin) / 2;
    double c2 = b2.zmin + (b2.zmax - b2.zmin) / 2;
    return Double.compare(c1, c2);
  };
  private final Selector selectZ = (bounds, split) -> {
    double centroid = bounds.zmin + (bounds.zmax - bounds.zmin) / 2;
    return centroid < split;
  };

  /**
   * Construct a new BVH containing the given primitives.
   */
  public BVH(List<Primitive> primitives) {
    switch (METHOD) {
      case MIDPOINT:
        root = constructMidpointSplit(primitives.toArray(new Primitive[primitives.size()]));
        break;
      case SAH:
        root = constructSAH(primitives.toArray(new Primitive[primitives.size()]));
        break;
      case SAH_MA:
        root = constructSAH_MA(primitives.toArray(new Primitive[primitives.size()]));
        break;
    }
  }

  enum Action {
    PUSH,
    MERGE,
  }

  /**
   * Simple BVH construction using splitting by major axis.
   *
   * @return root node of constructed BVH
   */
  private Node constructMidpointSplit(Primitive[] primitives) {
    Stack<Node> nodes = new Stack<>();
    Stack<Action> actions = new Stack<>();
    Stack<Primitive[]> chunks = new Stack<>();
    chunks.push(primitives);
    actions.push(Action.PUSH);
    while (!actions.isEmpty()) {
      Action action = actions.pop();
      if (action == Action.MERGE) {
        nodes.push(new Group(nodes.pop(), nodes.pop()));
      } else {
        Primitive[] chunk = chunks.pop();
        if (chunk.length < SPLIT_LIMIT) {
          nodes.push(new Leaf(chunk));
        } else {
          splitMidpointMajorAxis(chunk, actions, chunks);
        }
      }
    }
    return nodes.pop();
  }

  /**
   * Split a chunk on the major axis.
   */
  private void splitMidpointMajorAxis(Primitive[] chunk, Stack<Action> actions,
      Stack<Primitive[]> chunks) {
    AABB bb = bb(chunk);
    double xl = bb.xmax - bb.xmin;
    double yl = bb.ymax - bb.ymin;
    double zl = bb.zmax - bb.zmin;
    double splitPos;
    Selector selector;
    if (xl >= yl && xl >= zl) {
      splitPos = bb.xmin + (bb.xmax - bb.xmin) / 2;
      selector = selectX;
      Arrays.sort(chunk, cmpX);
    } else if (yl >= xl && yl >= zl) {
      splitPos = bb.ymin + (bb.ymax - bb.ymin) / 2;
      selector = selectY;
      Arrays.sort(chunk, cmpY);
    } else {
      splitPos = bb.zmin + (bb.zmax - bb.zmin) / 2;
      selector = selectZ;
      Arrays.sort(chunk, cmpZ);
    }

    int split;
    int end = chunk.length;
    for (split = 1; split < end; ++split) {
      if (!selector.select(chunk[split].bounds(), splitPos)) {
        break;
      }
    }

    actions.push(Action.MERGE);
    Primitive[] cons = new Primitive[split];
    System.arraycopy(chunk, 0, cons, 0, split);
    chunks.push(cons);
    actions.push(Action.PUSH);

    cons = new Primitive[end - split];
    System.arraycopy(chunk, split, cons, 0, end - split);
    chunks.push(cons);
    actions.push(Action.PUSH);
  }

  /**
   * Construct a BVH using Surface Area Heuristic (SAH).
   *
   * @return root node of constructed BVH
   */
  private Node constructSAH(Primitive[] primitives) {
    Stack<Node> nodes = new Stack<>();
    Stack<Action> actions = new Stack<>();
    Stack<Primitive[]> chunks = new Stack<>();
    chunks.push(primitives);
    actions.push(Action.PUSH);
    while (!actions.isEmpty()) {
      Action action = actions.pop();
      if (action == Action.MERGE) {
        nodes.push(new Group(nodes.pop(), nodes.pop()));
      } else {
        Primitive[] chunk = chunks.pop();
        if (chunk.length < SPLIT_LIMIT) {
          nodes.push(new Leaf(chunk));
        } else {
          splitSAH(chunk, actions, chunks);
        }
      }
    }
    return nodes.pop();
  }

  /**
   * Construct a BVH using Surface Area Heuristic (SAH)
   *
   * @return root node of constructed BVH
   */
  private Node constructSAH_MA(Primitive[] primitives) {
    Stack<Node> nodes = new Stack<>();
    Stack<Action> actions = new Stack<>();
    Stack<Primitive[]> chunks = new Stack<>();
    chunks.push(primitives);
    actions.push(Action.PUSH);
    while (!actions.isEmpty()) {
      Action action = actions.pop();
      if (action == Action.MERGE) {
        nodes.push(new Group(nodes.pop(), nodes.pop()));
      } else {
        Primitive[] chunk = chunks.pop();
        if (chunk.length < SPLIT_LIMIT) {
          nodes.push(new Leaf(chunk));
        } else {
          splitSAH_MA(chunk, actions, chunks);
        }
      }
    }
    return nodes.pop();
  }

  /**
   * Split a chunk based on Surface Area Heuristic of all possible splits
   */
  private void splitSAH(Primitive[] chunk, Stack<Action> actions, Stack<Primitive[]> chunks) {
    MutableAABB bounds = new MutableAABB(0, 0, 0, 0, 0, 0);
    double cmin = Double.POSITIVE_INFINITY;
    int split = 0;
    int end = chunk.length;

    double[] sl = new double[end];
    double[] sr = new double[end];

    Comparator<Primitive> cmp = cmpX;
    Arrays.sort(chunk, cmpX);
    for (int i = 0; i < end - 1; ++i) {
      bounds.expand(chunk[i].bounds());
      sl[i] = bounds.surfaceArea();
    }
    bounds = new MutableAABB(0, 0, 0, 0, 0, 0);
    for (int i = end - 1; i > 0; --i) {
      bounds.expand(chunk[i].bounds());
      sr[i - 1] = bounds.surfaceArea();
    }
    for (int i = 0; i < end - 1; ++i) {
      double c = sl[i] * (i + 1) + sr[i] * (end - i - 1);
      if (c < cmin) {
        cmin = c;
        split = i;
      }
    }

    Arrays.sort(chunk, cmpY);
    for (int i = 0; i < end - 1; ++i) {
      bounds.expand(chunk[i].bounds());
      sl[i] = bounds.surfaceArea();
    }
    bounds = new MutableAABB(0, 0, 0, 0, 0, 0);
    for (int i = end - 1; i > 0; --i) {
      bounds.expand(chunk[i].bounds());
      sr[i - 1] = bounds.surfaceArea();
    }
    for (int i = 0; i < end - 1; ++i) {
      double c = sl[i] * (i + 1) + sr[i] * (end - i - 1);
      if (c < cmin) {
        cmin = c;
        split = i;
        cmp = cmpY;
      }
    }

    Arrays.sort(chunk, cmpZ);
    for (int i = 0; i < end - 1; ++i) {
      bounds.expand(chunk[i].bounds());
      sl[i] = bounds.surfaceArea();
    }
    bounds = new MutableAABB(0, 0, 0, 0, 0, 0);
    for (int i = end - 1; i > 0; --i) {
      bounds.expand(chunk[i].bounds());
      sr[i - 1] = bounds.surfaceArea();
    }
    for (int i = 0; i < end - 1; ++i) {
      double c = sl[i] * (i + 1) + sr[i] * (end - i - 1);
      if (c < cmin) {
        cmin = c;
        split = i;
        cmp = cmpZ;
      }
    }

    if (cmp != cmpZ) {
      Arrays.sort(chunk, cmp);
    }

    split += 1;

    actions.push(Action.MERGE);
    Primitive[] cons = new Primitive[split];
    System.arraycopy(chunk, 0, cons, 0, split);
    chunks.push(cons);
    actions.push(Action.PUSH);

    cons = new Primitive[end - split];
    System.arraycopy(chunk, split, cons, 0, end - split);
    chunks.push(cons);
    actions.push(Action.PUSH);
  }

  /**
   * Split a chunk based on Surface Area Heuristic of all possible splits.
   */
  private void splitSAH_MA(Primitive[] chunk, Stack<Action> actions, Stack<Primitive[]> chunks) {
    AABB bb = bb(chunk);
    double xl = bb.xmax - bb.xmin;
    double yl = bb.ymax - bb.ymin;
    double zl = bb.zmax - bb.zmin;
    Comparator<Primitive> cmp;
    if (xl >= yl && xl >= zl) {
      cmp = cmpX;
      Arrays.sort(chunk, cmpX);
    } else if (yl >= xl && yl >= zl) {
      cmp = cmpY;
    } else {
      cmp = cmpZ;
    }

    MutableAABB bounds = new MutableAABB(0, 0, 0, 0, 0, 0);
    double cmin = Double.POSITIVE_INFINITY;
    int split = 0;
    int end = chunk.length;

    double[] sl = new double[end];
    double[] sr = new double[end];

    Arrays.sort(chunk, cmp);
    for (int i = 0; i < end - 1; ++i) {
      bounds.expand(chunk[i].bounds());
      sl[i] = bounds.surfaceArea();
    }
    bounds = new MutableAABB(0, 0, 0, 0, 0, 0);
    for (int i = end - 1; i > 0; --i) {
      bounds.expand(chunk[i].bounds());
      sr[i - 1] = bounds.surfaceArea();
    }
    for (int i = 0; i < end - 1; ++i) {
      double c = sl[i] * (i + 1) + sr[i] * (end - i - 1);
      if (c < cmin) {
        cmin = c;
        split = i;
      }
    }

    split += 1;

    actions.push(Action.MERGE);
    Primitive[] cons = new Primitive[split];
    System.arraycopy(chunk, 0, cons, 0, split);
    chunks.push(cons);
    actions.push(Action.PUSH);

    cons = new Primitive[end - split];
    System.arraycopy(chunk, split, cons, 0, end - split);
    chunks.push(cons);
    actions.push(Action.PUSH);
  }

  private static AABB bb(Primitive[] primitives) {
    double xmin = Double.POSITIVE_INFINITY;
    double xmax = Double.NEGATIVE_INFINITY;
    double ymin = Double.POSITIVE_INFINITY;
    double ymax = Double.NEGATIVE_INFINITY;
    double zmin = Double.POSITIVE_INFINITY;
    double zmax = Double.NEGATIVE_INFINITY;

    for (Primitive primitive : primitives) {
      AABB bb = primitive.bounds();
      if (bb.xmin < xmin)
        xmin = bb.xmin;
      if (bb.xmax > xmax)
        xmax = bb.xmax;
      if (bb.ymin < ymin)
        ymin = bb.ymin;
      if (bb.ymax > ymax)
        ymax = bb.ymax;
      if (bb.zmin < zmin)
        zmin = bb.zmin;
      if (bb.zmax > zmax)
        zmax = bb.zmax;
    }
    return new AABB(xmin, xmax, ymin, ymax, zmin, zmax);
  }

  /**
   * Find closest intersection between the ray and any object in the BVH.
   *
   * @return {@code true} if there exists any intersection
   */
  public boolean closestIntersection(Ray ray) {
    return root.bb.hitTest(ray) && root.closestIntersection(ray);
  }

  /**
   * Find any intersection between the ray and any object in the BVH. For simple
   * intersection tests this method is quicker. The closest point search costs a
   * bit more.
   *
   * @return {@code true} if there exists any intersection
   */
  public boolean anyIntersection(Ray ray) {
    return root.bb.hitTest(ray) && root.anyIntersection(ray);
  }

  public Node getRoot() {
    return root;
  }
}
