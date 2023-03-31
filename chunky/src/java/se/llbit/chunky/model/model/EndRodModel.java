/*
 * Copyright (c) 2015-2023 Chunky contributors
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
package se.llbit.chunky.model.model;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

public class EndRodModel extends QuadModel {
  private static final Quad[] up = {
      // Side faces.
      new Quad(new Vector3(7 / 16., 1 / 16., 9 / 16.), new Vector3(9 / 16., 1 / 16., 9 / 16.),
          new Vector3(7 / 16., 1, 9 / 16.), new Vector4(0, 2 / 16., 1 / 16., 1)),
      new Quad(new Vector3(9 / 16., 1 / 16., 7 / 16.), new Vector3(7 / 16., 1 / 16., 7 / 16.),
          new Vector3(9 / 16., 1, 7 / 16.), new Vector4(2 / 16., 0, 1 / 16., 1)),
      new Quad(new Vector3(7 / 16., 1 / 16., 7 / 16.), new Vector3(7 / 16., 1 / 16., 9 / 16.),
          new Vector3(7 / 16., 1, 7 / 16.), new Vector4(0, 2 / 16., 1 / 16., 1)),
      new Quad(new Vector3(9 / 16., 1 / 16., 9 / 16.), new Vector3(9 / 16., 1 / 16., 7 / 16.),
          new Vector3(9 / 16., 1, 9 / 16.), new Vector4(2 / 16., 0, 1 / 16., 1)),
      // Top face.
      new Quad(new Vector3(7 / 16., 1, 7 / 16.), new Vector3(7 / 16., 1, 9 / 16.),
          new Vector3(9 / 16., 1, 7 / 16.), new Vector4(2 / 16., 4 / 16., 14 / 16., 1)),
      // Base top face.
      new Quad(new Vector3(6 / 16., 1 / 16., 6 / 16.), new Vector3(6 / 16., 1 / 16., 10 / 16.),
          new Vector3(10 / 16., 1 / 16., 6 / 16.),
          new Vector4(2 / 16., 6 / 16., 10 / 16., 14 / 16.)),
      // Base bottom face.
      new Quad(new Vector3(10 / 16., 0, 6 / 16.), new Vector3(10 / 16., 0, 10 / 16.),
          new Vector3(6 / 16., 0, 6 / 16.), new Vector4(6 / 16., 2 / 16., 10 / 16., 14 / 16.)),
      // Bottom side faces.
      new Quad(new Vector3(6 / 16., 0, 10 / 16.), new Vector3(10 / 16., 0, 10 / 16.),
          new Vector3(6 / 16., 1 / 16., 10 / 16.),
          new Vector4(2 / 16., 4 / 16., 9 / 16., 10 / 16.)),
      new Quad(new Vector3(10 / 16., 0, 6 / 16.), new Vector3(6 / 16., 0, 6 / 16.),
          new Vector3(10 / 16., 1 / 16., 6 / 16.),
          new Vector4(4 / 16., 2 / 16., 9 / 16., 10 / 16.)),
      new Quad(new Vector3(6 / 16., 0, 6 / 16.), new Vector3(6 / 16., 0, 10 / 16.),
          new Vector3(6 / 16., 1 / 16., 6 / 16.),
          new Vector4(2 / 16., 4 / 16., 9 / 16., 10 / 16.)),
      new Quad(new Vector3(10 / 16., 0, 10 / 16.), new Vector3(10 / 16., 0, 6 / 16.),
          new Vector3(10 / 16., 1 / 16., 10 / 16.),
          new Vector4(4 / 16., 2 / 16., 9 / 16., 10 / 16.)),};

  private static final Quad[][] orientations = new Quad[6][];

  static {
    Quad[] down = Model.rotateX(Model.rotateX(up));
    Quad[] east = Model.rotateZ(down);
    Quad[] south = Model.rotateY(east);
    Quad[] west = Model.rotateY(south);
    Quad[] north = Model.rotateY(west);

    orientations[0] = down;
    orientations[1] = up;
    orientations[2] = north;
    orientations[3] = south;
    orientations[4] = west;
    orientations[5] = east;
  }

  private final static Texture[] textures = new Texture[up.length];
  static { Arrays.fill(textures, Texture.endRod); }

  private final Quad[] quads;

  public EndRodModel(int facing) {
    quads = orientations[facing];
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
