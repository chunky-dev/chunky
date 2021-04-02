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

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.util.TaskTracker;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Bounding Volume Hierarchy based on AABBs.
 *
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class BVH {
  public static final int SPLIT_LIMIT = 4;

  public interface BVHImplementation {
    boolean closestIntersection(Ray ray);
  }

  public interface ImplementationFactory {
    BVHImplementation create(Collection<Entity> entities, Vector3 worldOffset, TaskTracker.Task task);
    String getTooltip();
  }

  /**
   * Map containing all known implementation (factories) and their name. Elements are addressed by the String name.
   * Values must implement {@code BVH.ImplementationFactory}.
   */
  @PluginApi
  public static Map<String, ImplementationFactory> factories = new HashMap<>();

  public static final String DEFAULT_IMPLEMENTATION = "SAH_MA";
  public static ImplementationFactory getImplementationFactory(String name) {
    return factories.getOrDefault(name, factories.get(DEFAULT_IMPLEMENTATION));
  }

  static {
    MidpointBVH.initImplementation();
    SahBVH.initImplementation();
    SahMaBVH.initImplementation();
  }

  public final BVHImplementation implementation;

  /**
   * Construct a new BVH containing the given entities. This will generate the BVH using the
   * persistent BVH method (default is SAH_MA).
   */
  public BVH(Collection<Entity> entities, Vector3 worldOffset, TaskTracker.Task task) {
    implementation = getImplementationFactory(PersistentSettings.getBvhMethod()).create(entities, worldOffset, task);
  }

  /**
   * Calculate the closest intersection between a ray and a primitive in this BVH. Algorithm is implementation dependent.
   */
  public boolean closestIntersection(Ray ray) {
    return implementation.closestIntersection(ray);
  }
}
