/* Copyright (c) 2012-2013 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

/**
 * Furnaces, chests, dispensers
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class FurnaceModel {
  private static final int texindices[][] = {
      // undirectional
      {2, 2, 2, 2, 4, 5},

      // undirectional
      {2, 2, 2, 2, 4, 5},

      // facing north
      {0, 1, 2, 2, 4, 5},

      // facing south
      {1, 0, 2, 2, 4, 5},

      // facing west
      {2, 2, 1, 0, 4, 5},

      // facing east
      {2, 2, 0, 1, 4, 5},};

  public static boolean intersect(Ray ray, Texture[] texture) {
    int rot = ray.getBlockData() % 6;
    return TexturedBlockModel.intersect(ray, texture, texindices[rot]);
  }
}
