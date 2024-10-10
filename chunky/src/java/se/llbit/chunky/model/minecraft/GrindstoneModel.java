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

package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class GrindstoneModel extends QuadModel {
  private static final AbstractTexture pivot = Texture.grindstonePivot;
  private static final AbstractTexture round = Texture.grindstoneRound;
  private static final AbstractTexture side = Texture.grindstoneSide;
  private static final AbstractTexture leg = Texture.darkOakWood;
  private static final AbstractTexture[] textures = new AbstractTexture[] {
      leg, leg, leg, leg, leg,
      leg, leg, leg, leg, leg,
      pivot, pivot, pivot, pivot, pivot,
      pivot, pivot, pivot, pivot, pivot,
      round, round, side, side, round, round
  };

  //region Quads
  private static final Quad[] quadsFloorNorth = new Quad[] {
      new Quad(
          new Vector3(12 / 16.0, 0 / 16.0, 6 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 6 / 16.0),
          new Vector3(12 / 16.0, 0 / 16.0, 10 / 16.0),
          new Vector4(12 / 16.0, 14 / 16.0, 6 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(12 / 16.0, 7 / 16.0, 10 / 16.0),
          new Vector3(12 / 16.0, 7 / 16.0, 6 / 16.0),
          new Vector3(12 / 16.0, 0 / 16.0, 10 / 16.0),
          new Vector4(10 / 16.0, 6 / 16.0, 7 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 7 / 16.0, 6 / 16.0),
          new Vector3(14 / 16.0, 7 / 16.0, 10 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 6 / 16.0),
          new Vector4(6 / 16.0, 10 / 16.0, 0 / 16.0, 7 / 16.0)
      ),
      new Quad(
          new Vector3(12 / 16.0, 7 / 16.0, 6 / 16.0),
          new Vector3(14 / 16.0, 7 / 16.0, 6 / 16.0),
          new Vector3(12 / 16.0, 0 / 16.0, 6 / 16.0),
          new Vector4(4 / 16.0, 2 / 16.0, 7 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 7 / 16.0, 10 / 16.0),
          new Vector3(12 / 16.0, 7 / 16.0, 10 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 10 / 16.0),
          new Vector4(14 / 16.0, 12 / 16.0, 7 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 0 / 16.0, 6 / 16.0),
          new Vector3(4 / 16.0, 0 / 16.0, 6 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 10 / 16.0),
          new Vector4(2 / 16.0, 4 / 16.0, 6 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 7 / 16.0, 10 / 16.0),
          new Vector3(2 / 16.0, 7 / 16.0, 6 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 10 / 16.0),
          new Vector4(10 / 16.0, 6 / 16.0, 7 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(4 / 16.0, 7 / 16.0, 6 / 16.0),
          new Vector3(4 / 16.0, 7 / 16.0, 10 / 16.0),
          new Vector3(4 / 16.0, 0 / 16.0, 6 / 16.0),
          new Vector4(6 / 16.0, 10 / 16.0, 0 / 16.0, 7 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 7 / 16.0, 6 / 16.0),
          new Vector3(4 / 16.0, 7 / 16.0, 6 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 6 / 16.0),
          new Vector4(14 / 16.0, 12 / 16.0, 7 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(4 / 16.0, 7 / 16.0, 10 / 16.0),
          new Vector3(2 / 16.0, 7 / 16.0, 10 / 16.0),
          new Vector3(4 / 16.0, 0 / 16.0, 10 / 16.0),
          new Vector4(4 / 16.0, 2 / 16.0, 7 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(12 / 16.0, 13 / 16.0, 11 / 16.0),
          new Vector3(14 / 16.0, 13 / 16.0, 11 / 16.0),
          new Vector3(12 / 16.0, 13 / 16.0, 5 / 16.0),
          new Vector4(8 / 16.0, 10 / 16.0, 10 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(12 / 16.0, 7 / 16.0, 5 / 16.0),
          new Vector3(14 / 16.0, 7 / 16.0, 5 / 16.0),
          new Vector3(12 / 16.0, 7 / 16.0, 11 / 16.0),
          new Vector4(8 / 16.0, 10 / 16.0, 10 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 13 / 16.0, 5 / 16.0),
          new Vector3(14 / 16.0, 13 / 16.0, 11 / 16.0),
          new Vector3(14 / 16.0, 7 / 16.0, 5 / 16.0),
          new Vector4(6 / 16.0, 0 / 16.0, 16 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(12 / 16.0, 13 / 16.0, 5 / 16.0),
          new Vector3(14 / 16.0, 13 / 16.0, 5 / 16.0),
          new Vector3(12 / 16.0, 7 / 16.0, 5 / 16.0),
          new Vector4(8 / 16.0, 6 / 16.0, 16 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 13 / 16.0, 11 / 16.0),
          new Vector3(12 / 16.0, 13 / 16.0, 11 / 16.0),
          new Vector3(14 / 16.0, 7 / 16.0, 11 / 16.0),
          new Vector4(8 / 16.0, 6 / 16.0, 16 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 13 / 16.0, 11 / 16.0),
          new Vector3(4 / 16.0, 13 / 16.0, 11 / 16.0),
          new Vector3(2 / 16.0, 13 / 16.0, 5 / 16.0),
          new Vector4(8 / 16.0, 10 / 16.0, 10 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 7 / 16.0, 5 / 16.0),
          new Vector3(4 / 16.0, 7 / 16.0, 5 / 16.0),
          new Vector3(2 / 16.0, 7 / 16.0, 11 / 16.0),
          new Vector4(8 / 16.0, 10 / 16.0, 10 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 13 / 16.0, 11 / 16.0),
          new Vector3(2 / 16.0, 13 / 16.0, 5 / 16.0),
          new Vector3(2 / 16.0, 7 / 16.0, 11 / 16.0),
          new Vector4(6 / 16.0, 0 / 16.0, 16 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 13 / 16.0, 5 / 16.0),
          new Vector3(4 / 16.0, 13 / 16.0, 5 / 16.0),
          new Vector3(2 / 16.0, 7 / 16.0, 5 / 16.0),
          new Vector4(8 / 16.0, 6 / 16.0, 16 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(4 / 16.0, 13 / 16.0, 11 / 16.0),
          new Vector3(2 / 16.0, 13 / 16.0, 11 / 16.0),
          new Vector3(4 / 16.0, 7 / 16.0, 11 / 16.0),
          new Vector4(8 / 16.0, 6 / 16.0, 16 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(4 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(12 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(4 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector4(0 / 16.0, 8 / 16.0, 4 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(4 / 16.0, 4 / 16.0, 2 / 16.0),
          new Vector3(12 / 16.0, 4 / 16.0, 2 / 16.0),
          new Vector3(4 / 16.0, 4 / 16.0, 14 / 16.0),
          new Vector4(0 / 16.0, 8 / 16.0, 4 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(4 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(4 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(4 / 16.0, 4 / 16.0, 14 / 16.0),
          new Vector4(12 / 16.0, 0 / 16.0, 16 / 16.0, 4 / 16.0)
      ),
      new Quad(
          new Vector3(12 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(12 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(12 / 16.0, 4 / 16.0, 2 / 16.0),
          new Vector4(12 / 16.0, 0 / 16.0, 16 / 16.0, 4 / 16.0)
      ),
      new Quad(
          new Vector3(4 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(12 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(4 / 16.0, 4 / 16.0, 2 / 16.0),
          new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 4 / 16.0)
      ),
      new Quad(
          new Vector3(12 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(4 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(12 / 16.0, 4 / 16.0, 14 / 16.0),
          new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 4 / 16.0)
      )
  };
  //endregion

  static final Quad[][][] orientedQuads = new Quad[3][4][];

  static {
    // face=floor
    orientedQuads[0][0] = quadsFloorNorth;
    orientedQuads[0][1] = Model.rotateY(orientedQuads[0][0]);
    orientedQuads[0][2] = Model.rotateY(orientedQuads[0][1]);
    orientedQuads[0][3] = Model.rotateY(orientedQuads[0][2]);

    // face=wall
    orientedQuads[1][0] = Model.rotateX(quadsFloorNorth, Math.toRadians(-90));
    orientedQuads[1][1] = Model.rotateY(orientedQuads[1][0]);
    orientedQuads[1][2] = Model.rotateY(orientedQuads[1][1]);
    orientedQuads[1][3] = Model.rotateY(orientedQuads[1][2]);

    // face=ceiling
    orientedQuads[2][2] = Model.rotateX(quadsFloorNorth, Math.toRadians(180));
    orientedQuads[2][3] = Model.rotateY(orientedQuads[2][2]);
    orientedQuads[2][0] = Model.rotateY(orientedQuads[2][3]);
    orientedQuads[2][1] = Model.rotateY(orientedQuads[2][0]);
  }

  private final Quad[] quads;

  public GrindstoneModel(String faceString, String facingString) {
    int face;
    switch (faceString) {
      default:
      case "floor":
        face = 0;
        break;
      case "wall":
        face = 1;
        break;
      case "ceiling":
        face = 2;
        break;
    }

    int orientation;
    switch (facingString) {
      default:
      case "north":
        orientation = 0;
        break;
      case "east":
        orientation = 1;
        break;
      case "south":
        orientation = 2;
        break;
      case "west":
        orientation = 3;
    }

    this.quads = orientedQuads[face][orientation];
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
