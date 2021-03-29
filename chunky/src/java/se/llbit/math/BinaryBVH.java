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
package se.llbit.math;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntStack;
import org.apache.commons.math3.util.FastMath;
import se.llbit.math.primitive.Primitive;

import java.util.ArrayList;
import java.util.Comparator;

import static se.llbit.math.BVH.SPLIT_LIMIT;
import static se.llbit.math.Ray.OFFSET;

public abstract class BinaryBVH implements BVH.BVHImplementation {
    /** Note: This is public for some plugins. Stability is not guaranteed. */
    public int[] packed;
    public int depth;
    public Primitive[][] packedPrimitives;

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

        abstract public int size();
    }

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


        @Override public int size() {
            return numPrimitives;
        }
    }

    public static class Leaf extends Node {
        public Leaf(Primitive[] primitives) {
            super(primitives);
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
     * Recursive algorithm to pack a node-based BVH into an int(ArrayList). Nodes are packed as follows:
     * int 0: Second child index. If this is a leaf, it is the negation of the index of the corresponding list of primitives.
     *        The first child immediately follows this (byte 8+). The second child starts at the index pointed to by this int.
     * int 1-6: AABB bounds stored as floats. Float bits are converted into int bits for more compact storage.
     * This compact array storage helps decrease memory usage.
     */
    public int packNode(Node node, IntArrayList data, ArrayList<Primitive[]> primitives) {
        int index = data.size();
        int depth;
        data.add(0);  // Next child (to be set)
        packAabb(node.bb, data);

        if (node instanceof Group) {
            depth = packNode(((Group) node).child1, data, primitives);
            data.set(index, data.size()); // Second child location
            depth = FastMath.max(packNode(((Group) node).child2, data, primitives), depth);
        } else if (node instanceof Leaf) {
            depth = 1;
            data.set(index, -primitives.size());  // Negative number = pointer to primitives array
            primitives.add(node.primitives);
        } else {
            depth = 0;
            data.set(index, index+7); // Skip this
        }

        return depth+1;
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
    public boolean closestIntersection(Ray ray) {
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
                    hit = primitive.intersect(ray) || hit;
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

                if (t1 == -1 || t1 > ray.t) {
                    if (t2 == -1 || t2 > ray.t) {
                        if (nodesToVisit.isEmpty()) break;
                        currentNode = nodesToVisit.popInt();
                    } else {
                        currentNode = packed[currentNode];
                    }
                } else if (t2 == -1 || t2 > ray.t) {
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
    public double quickAabbIntersect(Ray ray, float xmin, float xmax, float ymin, float ymax, float zmin, float zmax, double rx, double ry, double rz) {
        if (ray.o.x >= xmin && ray.o.x <= xmax && ray.o.y >= ymin && ray.o.y <= ymax && ray.o.z >= zmin && ray.o.z <= zmax) {
            return 0;
        }

        double tx1 = (xmin - ray.o.x) * rx;
        double tx2 = (xmax - ray.o.x) * rx;

        double ty1 = (ymin - ray.o.y) * ry;
        double ty2 = (ymax - ray.o.y) * ry;

        double tz1 = (zmin - ray.o.z) * rz;
        double tz2 = (zmax - ray.o.z) * rz;

        double tmin = FastMath.max(FastMath.max(FastMath.min(tx1, tx2), FastMath.min(ty1, ty2)), FastMath.min(tz1, tz2));
        double tmax = FastMath.min(FastMath.min(FastMath.max(tx1, tx2), FastMath.max(ty1, ty2)), FastMath.max(tz1, tz2));

        return tmin <= tmax+ OFFSET && tmin >= 0 ? tmin : -1;
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
