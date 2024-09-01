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
import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

public class TrapdoorModel extends QuadModel {
  //region Model
  private static final Quad[][] faces = {
      // low
      {
          // front
          new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 0), new Vector3(1, .1875, 0),
              new Vector4(1, 0, 0, .1875)),

          // back
          new Quad(new Vector3(0, 0, 1), new Vector3(1, 0, 1), new Vector3(0, .1875, 1),
              new Vector4(0, 1, 0, .1875)),

          // right
          new Quad(new Vector3(0, 0, 0), new Vector3(0, 0, 1), new Vector3(0, .1875, 0),
              new Vector4(0, 1, 0, .1875)),

          // left
          new Quad(new Vector3(1, 0, 1), new Vector3(1, 0, 0), new Vector3(1, .1875, 1),
              new Vector4(1, 0, 0, .1875)),

          // top
          new Quad(new Vector3(1, .1875, 0), new Vector3(0, .1875, 0), new Vector3(1, .1875, 1),
              new Vector4(1, 0, 0, 1)),

          // bottom
          new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
              new Vector4(0, 1, 0, 1))
      },

      // high
      {
          // front
          new Quad(new Vector3(1, .8125, 0), new Vector3(0, .8125, 0), new Vector3(1, 1, 0),
              new Vector4(1, 0, 0, .1875)),

          // back
          new Quad(new Vector3(0, .8125, 1), new Vector3(1, .8125, 1), new Vector3(0, 1, 1),
              new Vector4(0, 1, 0, .1875)),

          // right
          new Quad(new Vector3(0, .8125, 0), new Vector3(0, .8125, 1), new Vector3(0, 1, 0),
              new Vector4(0, 1, 0, .1875)),

          // left
          new Quad(new Vector3(1, .8125, 1), new Vector3(1, .8125, 0), new Vector3(1, 1, 1),
              new Vector4(1, 0, 0, .1875)),

          // top
          new Quad(new Vector3(1, 1, 0), new Vector3(0, 1, 0), new Vector3(1, 1, 1),
              new Vector4(1, 0, 0, 1)),

          // bottom
          new Quad(new Vector3(0, 13 / 16., 0), new Vector3(1, 13 / 16., 0),
              new Vector3(0, 13 / 16., 1), new Vector4(0, 1, 0, 1))
      },

      // facing north
      {
          // north
          new Quad(new Vector3(1, 0, .8125), new Vector3(0, 0, .8125), new Vector3(1, 1, .8125),
              new Vector4(1, 0, 0, 1)),

          // south
          new Quad(new Vector3(0, 0, 1), new Vector3(1, 0, 1), new Vector3(0, 1, 1),
              new Vector4(0, 1, 0, 1)),

          // west
          new Quad(new Vector3(0, 0, .8125), new Vector3(0, 0, 1), new Vector3(0, 1, .8125),
              new Vector4(.8125, 1, 0, 1)),

          // east
          new Quad(new Vector3(1, 0, 1), new Vector3(1, 0, .8125), new Vector3(1, 1, 1),
              new Vector4(1, .8125, 0, 1)),

          // top
          new Quad(new Vector3(1, 1, .8125), new Vector3(0, 1, .8125), new Vector3(1, 1, 1),
              new Vector4(1, 0, .8125, 1)),

          // bottom
          new Quad(new Vector3(0, 0, .8125), new Vector3(1, 0, .8125), new Vector3(0, 0, 1),
              new Vector4(0, 1, .8125, 1))
      },

      // facing south
      {
          // north
          new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 0), new Vector3(1, 1, 0),
              new Vector4(1, 0, 0, 1)),

          // south
          new Quad(new Vector3(0, 0, .1875), new Vector3(1, 0, .1875), new Vector3(0, 1, .1875),
              new Vector4(0, 1, 0, 1)),

          // west
          new Quad(new Vector3(0, 0, 0), new Vector3(0, 0, .1875), new Vector3(0, 1, 0),
              new Vector4(0, .1875, 0, 1)),

          // east
          new Quad(new Vector3(1, 0, .1875), new Vector3(1, 0, 0), new Vector3(1, 1, .1875),
              new Vector4(.1875, 0, 0, 1)),

          // top
          new Quad(new Vector3(1, 1, 0), new Vector3(0, 1, 0), new Vector3(1, 1, .1875),
              new Vector4(1, 0, 0, .1875)),

          // bottom
          new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, .1875),
              new Vector4(0, 1, 0, .1875))
      },

      // facing west
      {
          // north
          new Quad(new Vector3(1, 0, 0), new Vector3(.8125, 0, 0), new Vector3(1, 1, 0),
              new Vector4(1, .8125, 0, 1)),

          // south
          new Quad(new Vector3(.8125, 0, 1), new Vector3(1, 0, 1), new Vector3(.8125, 1, 1),
              new Vector4(.8125, 1, 0, 1)),

          // west
          new Quad(new Vector3(.8125, 0, 0), new Vector3(.8125, 0, 1), new Vector3(.8125, 1, 0),
              new Vector4(0, 1, 0, 1)),

          // east
          new Quad(new Vector3(1, 0, 1), new Vector3(1, 0, 0), new Vector3(1, 1, 1),
              new Vector4(1, 0, 0, 1)),

          // top
          new Quad(new Vector3(1, 1, 0), new Vector3(.8125, 1, 0), new Vector3(1, 1, 1),
              new Vector4(1, .8125, 0, 1)),

          // bottom
          new Quad(new Vector3(.8125, 0, 0), new Vector3(1, 0, 0), new Vector3(.8125, 0, 1),
              new Vector4(.8125, 1, 0, 1))
      },

      // facing east
      {
          // north
          new Quad(new Vector3(.1875, 0, 0), new Vector3(0, 0, 0), new Vector3(.1875, 1, 0),
              new Vector4(.1875, 0, 0, 1)),

          // south
          new Quad(new Vector3(0, 0, 1), new Vector3(.1875, 0, 1), new Vector3(0, 1, 1),
              new Vector4(0, .1875, 0, 1)),

          // west
          new Quad(new Vector3(0, 0, 0), new Vector3(0, 0, 1), new Vector3(0, 1, 0),
              new Vector4(0, 1, 0, 1)),

          // east
          new Quad(new Vector3(.1875, 0, 1), new Vector3(.1875, 0, 0), new Vector3(.1875, 1, 1),
              new Vector4(1, 0, 0, 1)),

          // top
          new Quad(new Vector3(.1875, 1, 0), new Vector3(0, 1, 0), new Vector3(.1875, 1, 1),
              new Vector4(.1875, 0, 0, 1)),

          // bottom
          new Quad(new Vector3(0, 0, 0), new Vector3(.1875, 0, 0), new Vector3(0, 0, 1),
              new Vector4(0, .1875, 0, 1))
      }
  };
  //endregion

  private final Quad[] quads;
  private final AbstractTexture[] textures;

  public TrapdoorModel(AbstractTexture texture, int state) {
    quads = (state & 4) == 0 ? faces[state >> 3] : faces[(state & 3) + 2];
    textures = new AbstractTexture[quads.length];
    Arrays.fill(textures, texture);
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public AbstractTexture[] getTextures() {
    return textures;
  }
}
