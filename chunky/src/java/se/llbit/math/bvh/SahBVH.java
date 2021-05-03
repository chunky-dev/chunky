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

import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.main.Chunky;
import se.llbit.log.Log;
import se.llbit.math.Vector3;
import se.llbit.math.primitive.MutableAABB;
import se.llbit.math.primitive.Primitive;
import se.llbit.util.TaskTracker;

import java.util.*;
import java.util.function.IntConsumer;

public class SahBVH extends BinaryBVH {
    public static void initImplementation() {
        Factory.addBVHBuilder(new Factory.BVHBuilder() {
            @Override
            public BVH create(Collection<Entity> entities, Vector3 worldOffset, TaskTracker.Task task) {
                task.update(1000, 0);
                double entityScaler = 500.0 / entities.size();
                int done = 0;

                List<Primitive> primitives = new ArrayList<>();
                for (Entity entity : entities) {
                    primitives.addAll(entity.primitives(worldOffset));

                    done++;
                    task.updateInterval((int) (done * entityScaler), 1);
                }
                Primitive[] allPrimitives = primitives.toArray(new Primitive[0]);
                primitives = null; // Allow the collection to be garbage collected during construction when only the array is used

                double primitiveScaler = 500.0 / allPrimitives.length;
                return new SahBVH(allPrimitives, i -> task.updateInterval((int) (i * primitiveScaler) + 500, 1));
            }

            @Override
            public String getName() {
                return "SAH";
            }

            @Override
            public String getDescription() {
                return "Slow but nearly optimal BVH building method.";
            }
        });
    }

    public SahBVH(Primitive[] primitives, IntConsumer task) {
        Node root = constructSAH(primitives, task);
        pack(root);
        Log.info("Built SAH BVH with depth " + this.depth);
    }

    private enum Action {
        PUSH,
        MERGE,
    }

    /**
     * Construct a BVH using Surface Area Heuristic (SAH).
     */
    private Node constructSAH(Primitive[] primitives, IntConsumer task) {
        int progress = 0;

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

                    progress += chunk.length;
                    task.accept(progress);
                } else {
                    splitSAH(chunk, actions, chunks);
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
        Chunky.getCommonThreads().submit(() -> Arrays.parallelSort(chunk, cmpX)).join();
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

        Chunky.getCommonThreads().submit(() -> Arrays.parallelSort(chunk, cmpY)).join();
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

        Chunky.getCommonThreads().submit(() -> Arrays.parallelSort(chunk, cmpZ)).join();
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
            Comparator<Primitive> finalCmp = cmp;
            Chunky.getCommonThreads().submit(() -> Arrays.parallelSort(chunk, finalCmp)).join();
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
}
