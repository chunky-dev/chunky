/* Copyright (c) 2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2021 Chunky contributors
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
package se.llbit.math.bvh;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntStack;
import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.AABB;
import se.llbit.math.Ray;
import se.llbit.math.Ray2;
import se.llbit.math.primitive.Primitive;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Stack;

import static se.llbit.math.Constants.OFFSET;

/**
 * An abstract class for BinaryBVHs. This provides helper methods for packing a node based BVH into a more compact
 * and faster array based BVH. It also provides an implementation for {@code closestIntersection}.
 */
public abstract class BinaryBVH implements BVH {

    public static final int SPLIT_LIMIT = 4;

    /** Note: This is public for some plugins. Stability is not guaranteed. */
    public int[] packed;
    public int depth;
    public Primitive[][] packedPrimitives;

    public static abstract class Node {
        public final AABB bb;

        /**
         * Create new BVH node with specific bounds.
         */
        public Node(AABB bb) {
            this.bb = bb;
        }

        abstract public int size();
    }

    public static class Group extends Node {
        public Node child1;
        public Node child2;
        private final int numPrimitives;

        /**
         * Create a new BVH node.
         */
        public Group(Node child1, Node child2) {
            super(child1.bb.expand(child2.bb));
            this.numPrimitives = child1.size() + child2.size();
            this.child1 = child1;
            this.child2 = child2;
        }


        @Override public int size() {
            return numPrimitives;
        }
    }

    public static class Leaf extends Node {
        public final Primitive[] primitives;

        public Leaf(Primitive[] primitives) {
            super(bb(primitives));
            this.primitives = primitives;
        }

        @Override public int size() {
            return primitives.length;
        }
    }

    public interface Selector {
        boolean select(AABB bounds, double split);
    }

    public final Comparator<Primitive> cmpX = (g1, g2) -> {
        AABB b1 = g1.bounds();
        AABB b2 = g2.bounds();
        double c1 = b1.xmin + (b1.xmax - b1.xmin) / 2;
        double c2 = b2.xmin + (b2.xmax - b2.xmin) / 2;
        return Double.compare(c1, c2);
    };
    public final Selector selectX = (bounds, split) -> {
        double centroid = bounds.xmin + (bounds.xmax - bounds.xmin) / 2;
        return centroid < split;
    };
    public final Comparator<Primitive> cmpY = (g1, g2) -> {
        AABB b1 = g1.bounds();
        AABB b2 = g2.bounds();
        double c1 = b1.ymin + (b1.ymax - b1.ymin) / 2;
        double c2 = b2.ymin + (b2.ymax - b2.ymin) / 2;
        return Double.compare(c1, c2);
    };
    public final Selector selectY = (bounds, split) -> {
        double centroid = bounds.ymin + (bounds.ymax - bounds.ymin) / 2;
        return centroid < split;
    };
    public final Comparator<Primitive> cmpZ = (g1, g2) -> {
        AABB b1 = g1.bounds();
        AABB b2 = g2.bounds();
        double c1 = b1.zmin + (b1.zmax - b1.zmin) / 2;
        double c2 = b2.zmin + (b2.zmax - b2.zmin) / 2;
        return Double.compare(c1, c2);
    };
    public final Selector selectZ = (bounds, split) -> {
        double centroid = bounds.zmin + (bounds.zmax - bounds.zmin) / 2;
        return centroid < split;
    };

    private static class PackData {
        public int index;
        public Node node;

        public PackData(int index, Node node) {
            this.index = index;
            this.node = node;
        }
    }

    /**
     * Helper method to pack a node-based BVH. Uses {@code packNode}.
     */
    public void pack(Node root) {
        IntArrayList data = new IntArrayList(root.size());
        ArrayList<Primitive[]> packedPrimitives = new ArrayList<>(data.size() / SPLIT_LIMIT);
        this.depth = packNode(root, data, packedPrimitives);
        this.packed = data.toIntArray();
        this.packedPrimitives = packedPrimitives.toArray(new Primitive[0][]);
    }

    /**
     * Recursive algorithm (utilizing a call stack) to pack a node-based BVH into an int(ArrayList).
     * Nodes are packed as follows:
     * int 0: Second child index. If this is a leaf, it is the negation of the index of the corresponding list of primitives.
     *        The first child immediately follows this (byte 8+). The second child starts at the index pointed to by this int.
     * int 1-6: AABB bounds stored as floats. Float bits are converted into int bits for more compact storage.
     * This compact array storage helps decrease memory usage and increases intersection speed.
     */
    public int packNode(Node startNode, IntArrayList dataArray, ArrayList<Primitive[]> primitives) {
        Stack<PackData> callStack = new Stack<>();
        int depth = 0;

        // First call
        callStack.add(new PackData(-1, startNode));

        while (!callStack.isEmpty()) {
            PackData call = callStack.pop();

            if (call.index != -1) dataArray.set(call.index, dataArray.size());

            int index = dataArray.size();
            dataArray.add(0);
            packAabb(call.node.bb, dataArray);

            if (call.node instanceof Group) {
                Group group = (Group) call.node;
                callStack.add(new PackData(index, group.child2));
                callStack.add(new PackData(-1, group.child1));

                // Set these references to null so the GC can clean them up early.
                group.child1 = null;
                group.child2 = null;
            } else if (call.node instanceof Leaf) {
                dataArray.set(index, -primitives.size());
                primitives.add(((Leaf) call.node).primitives);
            } else {
                dataArray.set(index, index+7); // Skip, this should never happen.
            }

            depth = FastMath.max(depth, callStack.size());
        }

        return depth;
    }

    /** Pack an AABB into 6 floats (and store the bits in 6 consecutive ints). */
    private void packAabb(AABB box, IntArrayList data) {
        data.add(Float.floatToIntBits((float) box.xmin));
        data.add(Float.floatToIntBits((float) box.xmax));
        data.add(Float.floatToIntBits((float) box.ymin));
        data.add(Float.floatToIntBits((float) box.ymax));
        data.add(Float.floatToIntBits((float) box.zmin));
        data.add(Float.floatToIntBits((float) box.zmax));
    }

    /**
     * Find closest intersection between the ray and any object in the BVH. This uses a recursion-less algorithm
     * based on the compact BVH traversal algorithm presented in:
     * http://www.pbr-book.org/3ed-2018/Primitives_and_Intersection_Acceleration/Bounding_Volume_Hierarchies.html#Traversal
     *
     * @return {@code true} if there exists any intersection
     */
    @Override
    public boolean closestIntersection(Ray2 ray, IntersectionRecord intersectionRecord, Scene scene) {
        boolean hit = false;
        int currentNode = 0;
        IntStack nodesToVisit = new IntArrayList(depth/2);

        double rx = 1 / ray.d.x;
        double ry = 1 / ray.d.y;
        double rz = 1 / ray.d.z;

        while (true) {
            if (packed[currentNode] <= 0) {
                // Is leaf
                int primIndex = -packed[currentNode];
                for (Primitive primitive : packedPrimitives[primIndex]) {
                    hit = primitive.closestIntersection(ray, intersectionRecord) | hit;
                }

                if (nodesToVisit.isEmpty()) break;
                currentNode = nodesToVisit.popInt();
            } else {
                // Is branch, find closest node
                int offset = currentNode+7;
                double t1 = quickAabbIntersect(ray, Float.intBitsToFloat(packed[offset+1]), Float.intBitsToFloat(packed[offset+2]),
                        Float.intBitsToFloat(packed[offset+3]), Float.intBitsToFloat(packed[offset+4]),
                        Float.intBitsToFloat(packed[offset+5]), Float.intBitsToFloat(packed[offset+6]),
                        rx, ry, rz);
                offset = packed[currentNode];
                double t2 = quickAabbIntersect(ray, Float.intBitsToFloat(packed[offset+1]), Float.intBitsToFloat(packed[offset+2]),
                        Float.intBitsToFloat(packed[offset+3]), Float.intBitsToFloat(packed[offset+4]),
                        Float.intBitsToFloat(packed[offset+5]), Float.intBitsToFloat(packed[offset+6]),
                        rx, ry, rz);

                if (t1 > intersectionRecord.distance | t1 == -1) {
                    if (t2 > intersectionRecord.distance | t2 == -1) {
                        if (nodesToVisit.isEmpty()) break;
                        currentNode = nodesToVisit.popInt();
                    } else {
                        currentNode = packed[currentNode];
                    }
                } else if (t2 > intersectionRecord.distance | t2 == -1) {
                    currentNode += 7;
                } else if (t1 < t2) {
                    nodesToVisit.push(packed[currentNode]);
                    currentNode += 7;
                } else {
                    nodesToVisit.push(currentNode + 7);
                    currentNode = packed[currentNode];
                }
            }
        }

        return hit;
    }

    /**
     * Perform a fast AABB intersection with cached reciprocal direction. This is a branchless approach based on:
     * https://gamedev.stackexchange.com/a/146362
     */
    public double quickAabbIntersect(Ray2 ray, float xmin, float xmax, float ymin, float ymax, float zmin, float zmax, double rx, double ry, double rz) {
        double tx1 = (xmin - ray.o.x) * rx;
        double tx2 = (xmax - ray.o.x) * rx;

        double ty1 = (ymin - ray.o.y) * ry;
        double ty2 = (ymax - ray.o.y) * ry;

        double tz1 = (zmin - ray.o.z) * rz;
        double tz2 = (zmax - ray.o.z) * rz;

        double tmin = FastMath.max(FastMath.max(FastMath.min(tx1, tx2), FastMath.min(ty1, ty2)), FastMath.min(tz1, tz2));
        double tmax = FastMath.min(FastMath.min(FastMath.max(tx1, tx2), FastMath.max(ty1, ty2)), FastMath.max(tz1, tz2));

        return (tmin <= tmax + OFFSET) & (tmax >= 0) ? tmin : -1;
    }

    /**
     * Calculate the bounding box of an array of primitives.
     */
    public static AABB bb(Primitive[] primitives) {
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
}
