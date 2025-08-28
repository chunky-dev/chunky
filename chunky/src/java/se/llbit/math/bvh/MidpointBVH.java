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

import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.renderer.HasPrimitives;
import se.llbit.log.Log;
import se.llbit.math.AABB;
import se.llbit.math.Vector3;
import se.llbit.math.primitive.Primitive;
import se.llbit.util.TaskTracker;

import java.util.*;
import java.util.function.IntConsumer;

public class MidpointBVH extends BinaryBVH {
    public static void registerImplementation() {
        Factory.addBVHBuilder(new Factory.BVHBuilder() {
            @Override
            public BVH create(Collection<HasPrimitives> entities, Vector3 worldOffset, TaskTracker.Task task) {
                task.update(1000, 0);
                double entityScaler = 500.0 / entities.size();
                int done = 0;

                List<Primitive> primitives = new ArrayList<>();
                for (HasPrimitives entity : entities) {
                    primitives.addAll(entity.primitives(worldOffset));

                    done++;
                    task.updateInterval((int) (done * entityScaler), 1);
                }
                Primitive[] allPrimitives = primitives.toArray(new Primitive[0]);
                primitives = null; // Allow the collection to be garbage collected during construction when only the array is used

                double primitiveScaler = 500.0 / allPrimitives.length;
                return new MidpointBVH(allPrimitives, i -> task.updateInterval((int) (i * primitiveScaler) + 500, 1));
            }

            @Override
            public String getName() {
                return "MIDPOINT";
            }

            @Override
            public String getDescription() {
                return "Fast and simple, but not optimal BVH building method.";
            }
        });
    }

    public MidpointBVH(Primitive[] primitives, IntConsumer task) {
        Node root = constructMidpointSplit(primitives, task);
        pack(root);
        Log.info("Built MIDPOINT BVH with depth " + this.depth);
    }

    private enum Action {
        PUSH,
        MERGE,
    }

    /**
     * Simple BVH construction using splitting by major axis.
     *
     * @return root node of constructed BVH
     */
    private Node constructMidpointSplit(Primitive[] primitives, IntConsumer task) {
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
            Chunky.getCommonThreads().submit(() -> Arrays.parallelSort(chunk, cmpX)).join();
        } else if (yl >= xl && yl >= zl) {
            splitPos = bb.ymin + (bb.ymax - bb.ymin) / 2;
            selector = selectY;
            Chunky.getCommonThreads().submit(() -> Arrays.parallelSort(chunk, cmpY)).join();
        } else {
            splitPos = bb.zmin + (bb.zmax - bb.zmin) / 2;
            selector = selectZ;
            Chunky.getCommonThreads().submit(() -> Arrays.parallelSort(chunk, cmpZ)).join();
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
}
