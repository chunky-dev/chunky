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

import se.llbit.chunky.block.minecraft.Air;
import se.llbit.math.Ray;
import se.llbit.math.Vector4;

import java.util.Random;

/**
 * State for a render worker.
 */
public class WorkerState {
  public Ray ray = new Ray();
  public Vector4 attenuation = new Vector4();
  public Random random;

  public void reset() {
    ray.distance = 0;
    ray.setPrevMaterial(Air.INSTANCE, 0);
    ray.setCurrentMaterial(Air.INSTANCE, 0);
    ray.depth = 0;
    ray.t = 0;
    ray.tNext = 0;
    ray.specular = false;

    attenuation.set(0, 0, 0, 0);
  }
}
