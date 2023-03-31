/*
 * Copyright (c) 2023 Chunky contributors
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

public class StonecutterModel extends QuadModel {

  private static final Texture bottom = Texture.stonecutterBottom;
  private static final Texture top = Texture.stonecutterTop;
  private static final Texture side = Texture.stonecutterSide;
  private static final Texture saw = Texture.stonecutterSaw;
  private static final Texture[] textures = new Texture[]{
      top, bottom, side, side, side, side, saw, saw
  };

  //region Model
  private static final Quad[] quadsNorth = new Quad[]{
      new Quad(
          new Vector3(0 / 16.0, 9 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 9 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 9 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      ),

      new Quad(
          new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 9 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 9 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 9 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 9 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 9 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 9 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 9 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 9 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 9 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 9 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 9 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 9 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(1 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(15 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(1 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector4(15 / 16.0, 1 / 16.0, 7 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(15 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(1 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(15 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector4(1 / 16.0, 15 / 16.0, 7 / 16.0, 0 / 16.0)
      )
  };
  //endregion

  static final Quad[][] orientedQuads = new Quad[4][];

  static {
    orientedQuads[0] = quadsNorth;
    orientedQuads[1] = Model.rotateY(orientedQuads[0]);
    orientedQuads[2] = Model.rotateY(orientedQuads[1]);
    orientedQuads[3] = Model.rotateY(orientedQuads[2]);
  }

  private final Quad[] quads;

  public StonecutterModel(String facing) {
    quads = orientedQuads[getOrientationIndex(facing)];
  }

  private static int getOrientationIndex(String facing) {
    switch (facing) {
      default:
      case "north":
        return 0;
      case "east":
        return 1;
      case "south":
        return 2;
      case "west":
        return 3;
    }
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
