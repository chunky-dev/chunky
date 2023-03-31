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

public class JigsawModel extends QuadModel {
  //region Jigsaw
  private static final Quad[] quadsNorthUp = {
      new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)),
      new Quad(
          new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)),
      new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)),
      new Quad(
          new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)),
      new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)),
      new Quad(
          new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0))
  };
  //endregion

  static final Quad[][] orientedQuads = new Quad[12][];

  private final static Texture[] texturesLock = {
      Texture.jigsawLock,
      Texture.jigsawSide,
      Texture.jigsawSide,
      Texture.jigsawSide,
      Texture.jigsawTop,
      Texture.jigsawBottom
  };

  private final static Texture[] textureNoLock = {
      Texture.jigsawSide,
      Texture.jigsawSide,
      Texture.jigsawSide,
      Texture.jigsawSide,
      Texture.jigsawTop,
      Texture.jigsawBottom
  };

  static {
    orientedQuads[1] = Model.rotateNegX(quadsNorthUp);
    orientedQuads[0] = Model.rotateY(orientedQuads[1]);
    orientedQuads[2] = Model.rotateY(orientedQuads[0]);
    orientedQuads[3] = Model.rotateY(orientedQuads[2]);
    orientedQuads[4] = Model.rotateY(quadsNorthUp);
    orientedQuads[5] = quadsNorthUp;
    orientedQuads[6] = Model.rotateY(orientedQuads[4]);
    orientedQuads[11] = Model.rotateY(orientedQuads[6]);
    orientedQuads[9] = Model.rotateNegX(Model.rotateNegX(orientedQuads[1]));
    orientedQuads[10] = Model.rotateY(orientedQuads[9]);
    orientedQuads[8] = Model.rotateY(orientedQuads[10]);
    orientedQuads[7] = Model.rotateY(orientedQuads[8]);
  }

  private final Quad[] quads;
  private final Texture[] textures;

  public JigsawModel(String orientation) {
    switch (orientation) {
      case "up":
        textures = textureNoLock;
        orientation = "up_north";
        break;
      case "down":
        textures = textureNoLock;
        orientation = "down_north";
        break;
      case "north":
        textures = texturesLock;
        orientation = "north_up";
        break;
      case "south":
        textures = texturesLock;
        orientation = "south_up";
        break;
      case "east":
        textures = texturesLock;
        orientation = "east_up";
        break;
      case "west":
        textures = texturesLock;
        orientation = "west_up";
        break;
      default:
        textures = texturesLock;
        break;
    }
    quads = orientedQuads[getOrientationIndex(orientation)];
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }

  private static int getOrientationIndex(String facing) {
    switch (facing) {
      case "down_east":
        return 0;
      case "down_north":
        return 1;
      case "down_south":
        return 2;
      case "down_west":
        return 3;
      case "east_up":
        return 4;
      case "north_up":
        return 5;
      case "south_up":
        return 6;
      case "up_east":
        return 7;
      case "up_north":
        return 8;
      case "up_south":
        return 9;
      case "up_west":
        return 10;
      case "west_up":
        return 11;
      default:
        return 5; // north_up
    }
  }
}
