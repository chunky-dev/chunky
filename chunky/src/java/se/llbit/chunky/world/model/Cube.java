/*
 * Copyright (c) 2017 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.world.model;

import se.llbit.math.Vector3;

public class Cube {
  public String name;
  public Vector3 start = new Vector3(0, 0, 0);
  public Vector3 end = new Vector3(16, 16, 16);
  public boolean visible = true;
  public Face[] faces = new Face[6];

  public Cube() {
    faces[0] = new Face("up");
    faces[1] = new Face("down");
    faces[2] = new Face("east");
    faces[3] = new Face("west");
    faces[4] = new Face("north");
    faces[5] = new Face("south");
  }
}
