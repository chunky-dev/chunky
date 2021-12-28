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
package se.llbit.math.bvh;

import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.log.Log;
import se.llbit.math.Intersectable;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.util.Registerable;
import se.llbit.util.TaskTracker;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Bounding Volume Hierarchy based on AABBs.
 */
public interface BVH extends Intersectable {
  BVH EMPTY = ray -> false;

  /**
   * Find closest intersection between the ray and any object in the BVH
   *
   * @return {@code true} if there exists any intersection
   */
  @Override
  boolean closestIntersection(Ray ray);

  final class Factory {

    public interface BVHBuilder extends Registerable {
      BVH create(Collection<Entity> entities, Vector3 worldOffset, TaskTracker.Task task);

      default String getId() { return getName(); };
    }

    /**
     * Map containing all known BVH implementations and their name.
     * Elements are addressed by the String name.
     */
    private static final Map<String, BVHBuilder> implementations = new HashMap<>();

    public static final BVHBuilder DEFAULT_IMPLEMENTATION;

    public static BVHBuilder getImplementation(String id) {
      if (!implementations.containsKey(id))
        Log.warn("Unknown BVH implementation: " + id + ". Using " + DEFAULT_IMPLEMENTATION.getId() + ".");
      return implementations.getOrDefault(id, DEFAULT_IMPLEMENTATION);
    }

    /**
     * Get all the BVH implementations.
     */
    @PluginApi
    public static Collection<BVHBuilder> getImplementations() {
      return implementations.values();
    }

    /**
     * Add a new BVH implementation. It's name will be taken from {@code BVHBuilder.getName()}.
     */
    @PluginApi
    public static void addBVHBuilder(BVHBuilder builder) {
      if (implementations.containsKey(builder.getId()))
        Log.warn("Attempted to register 2+ BVH builders with the ID " + builder.getId() + " (do you have the same plugin installed twice?)");
      implementations.put(builder.getId(), builder);
    }

    static {
      MidpointBVH.registerImplementation();
      SahBVH.registerImplementation();
      SahMaBVH.registerImplementation();
      DEFAULT_IMPLEMENTATION = implementations.get("SAH_MA");
    }

    /**
     * Construct a new BVH containing the given entities. This will generate the BVH using the
     * persistent BVH method (default is SAH_MA).
     */
    public static BVH create(String implementation, Collection<Entity> entities, Vector3 worldOffset, TaskTracker.Task task) {
      if (entities.isEmpty()) {
        return BVH.EMPTY;
      } else {
        return getImplementation(implementation).create(entities, worldOffset, task);
      }
    }
  }
}
