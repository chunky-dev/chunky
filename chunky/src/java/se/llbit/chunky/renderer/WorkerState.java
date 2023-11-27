/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer;

import se.llbit.math.Ray;
import se.llbit.math.Vector4;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * State for a render worker.
 */
public class WorkerState {
  public Ray ray;
  public Vector4 attenuation = new Vector4();
  public Random random;
  private List<Ray> pool = new LinkedList<>();

  public WorkerState() {
    for (int i = 0; i < 10; i++) {
      pool.add(new Ray());
    }
  }

  public Ray newRay() {
    if (pool.isEmpty()) {
      return new Ray();
    }
    Ray ray = pool.remove(0);
    ray.setDefault();
    return ray;
  }

  public void returnRay(Ray ray) {
    pool.add(ray);
  }

  public Ray newRay(Ray original) {
    if (pool.isEmpty()) {
      return new Ray(original);
    }
    Ray ray = pool.remove(0);
    ray.set(original);
    return ray;
  }
}
