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

import se.llbit.math.Vector2;

public class Face {
  public String name;
  public int rotation = 0;
  public boolean visible = true;
  public Vector2 uv0 = new Vector2(0, 0);
  public Vector2 uv1 = new Vector2(16.0, 16.0);
  public String texture = "blocks/stone";

  public Face(String name) {
    this.name = name;
  }

  public Face(Face face) {
    this.name = face.name;
    this.visible = face.visible;
    this.uv0.set(face.uv0);
    this.uv1.set(face.uv1);
    this.texture = face.texture;
    this.rotation = face.rotation;
  }
}
