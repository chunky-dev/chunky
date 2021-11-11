/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

public class DoorModel extends QuadModel {
  protected static final Quad[][] faces = {
    {
      // front
      new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 0), new Vector3(1, 1, 0),
          new Vector4(0, 1, 0, 1)),

      // back
      new Quad(new Vector3(0, 0, .1875), new Vector3(1, 0, .1875), new Vector3(0, 1, .1875),
          new Vector4(1, 0, 0, 1)),

      // right
      new Quad(new Vector3(0, 0, 0), new Vector3(0, 0, .1875), new Vector3(0, 1, 0),
          new Vector4(0, .1875, 0, 1)),

      // left
      new Quad(new Vector3(1, 0, .1875), new Vector3(1, 0, 0), new Vector3(1, 1, .1875),
          new Vector4(0, .1875, 0, 1)),

      // top
      new Quad(new Vector3(0, 1, 0), new Vector3(0, 1, .1875), new Vector3(1, 1, 0),
          new Vector4(0, .1875, 0, 1)),

      // bottom
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, .1875),
          new Vector4(0, 1, 0, .1875)),
    },
    {
      // front
      new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 0), new Vector3(1, 1, 0),
          new Vector4(1, 0, 0, 1)),

      // back
      new Quad(new Vector3(0, 0, .1875), new Vector3(1, 0, .1875), new Vector3(0, 1, .1875),
          new Vector4(0, 1, 0, 1)),

      // right
      new Quad(new Vector3(0, 0, 0), new Vector3(0, 0, .1875), new Vector3(0, 1, 0),
          new Vector4(0, .1875, 0, 1)),

      // left
      new Quad(new Vector3(1, 0, .1875), new Vector3(1, 0, 0), new Vector3(1, 1, .1875),
          new Vector4(0, .1875, 0, 1)),

      // top
      new Quad(new Vector3(0, 1, 0), new Vector3(0, 1, .1875), new Vector3(1, 1, 0),
          new Vector4(0, .1875, 0, 1)),

      // bottom
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, .1875),
          new Vector4(0, 1, 0, .1875)),
    },
  };

  private static final Quad[][][] rot = new Quad[2][4][];

  static {
    rot[0][1] = faces[0];
    rot[1][1] = faces[1];

    for (int mirror = 0; mirror < 2; ++mirror) {
      rot[mirror][2] = Model.rotateY(rot[mirror][1]);
    }

    for (int mirror = 0; mirror < 2; ++mirror) {
      rot[mirror][3] = Model.rotateY(rot[mirror][2]);
    }

    for (int mirror = 0; mirror < 2; ++mirror) {
      rot[mirror][0] = Model.rotateY(rot[mirror][3]);
    }
  }

  private final Quad[] quads;
  private final Texture[] textures;

  public DoorModel(Texture texture, int mirror, int facing) {
    quads = rot[mirror][facing];
    textures = new Texture[quads.length];
    Arrays.fill(textures, texture);
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
