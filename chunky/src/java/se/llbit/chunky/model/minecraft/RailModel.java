/*
 * Copyright (c) 2012-2023 Chunky contributors
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
package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

/**
 * Minecart rails.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class RailModel extends QuadModel {
  private static final Quad[] rails = {
      // Flat north-south.
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1), true),

      // Flat east-west.
      new Quad(new Vector3(0, 0, 0), new Vector3(0, 0, 1), new Vector3(1, 0, 0),
          new Vector4(0, 1, 0, 1), true),

      // Ascending east.
      new Quad(new Vector3(0, 0, 0), new Vector3(0, 0, 1), new Vector3(1, 1, 0),
          new Vector4(0, 1, 0, 1), true),

      // Ascending west.
      new Quad(new Vector3(0, 1, 0), new Vector3(0, 1, 1), new Vector3(1, 0, 0),
          new Vector4(0, 1, 0, 1), true),

      // Ascending north.
      new Quad(new Vector3(0, 1, 0), new Vector3(1, 1, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1), true),

      // Ascending south
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 1, 1),
          new Vector4(0, 1, 0, 1), true),

      // Nw corner
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 1, 0), true),

      // ne corner
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(1, 0, 1, 0), true),

      // se corner
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(1, 0, 0, 1), true),

      // sw corner
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1), true),

  };

  private final Quad[] quads;
  private final Texture[] textures;

  public RailModel(Texture texture, int type) {
    this.quads = new Quad[] { rails[type] };
    this.textures = new Texture[] { texture };
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }
}
