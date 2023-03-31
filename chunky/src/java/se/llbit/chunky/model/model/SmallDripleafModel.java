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

public class SmallDripleafModel extends QuadModel {

  //region Top Model
  private static final Quad[] topQuadsNorth = Model.join(
      new Quad[] {
          // top
          new Quad(
              new Vector3(8 / 16.0, 2.99 / 16.0, 15 / 16.0),
              new Vector3(15 / 16.0, 2.99 / 16.0, 15 / 16.0),
              new Vector3(8 / 16.0, 2.99 / 16.0, 8 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 8 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 2.99 / 16.0, 8 / 16.0),
              new Vector3(15 / 16.0, 2.99 / 16.0, 8 / 16.0),
              new Vector3(8 / 16.0, 2.99 / 16.0, 15 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 8 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(1 / 16.0, 8 / 16.0, 8 / 16.0),
              new Vector3(8 / 16.0, 8 / 16.0, 8 / 16.0),
              new Vector3(1 / 16.0, 8 / 16.0, 1 / 16.0),
              new Vector4(0 / 16.0, 8 / 16.0, 8 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(1 / 16.0, 8 / 16.0, 1 / 16.0),
              new Vector3(8 / 16.0, 8 / 16.0, 1 / 16.0),
              new Vector3(1 / 16.0, 8 / 16.0, 8 / 16.0),
              new Vector4(0 / 16.0, 8 / 16.0, 16 / 16.0, 8 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 12 / 16.0, 15 / 16.0),
              new Vector3(8 / 16.0, 12 / 16.0, 8 / 16.0),
              new Vector3(1 / 16.0, 12 / 16.0, 15 / 16.0),
              new Vector4(0 / 16.0, 8 / 16.0, 8 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 12 / 16.0, 8 / 16.0),
              new Vector3(8 / 16.0, 12 / 16.0, 15 / 16.0),
              new Vector3(1 / 16.0, 12 / 16.0, 8 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 8 / 16.0, 16 / 16.0)
          ),
          // side
          new Quad(
              new Vector3(8 / 16.0, 3 / 16.0, 15 / 16.0),
              new Vector3(8 / 16.0, 3 / 16.0, 8 / 16.0),
              new Vector3(8 / 16.0, 2 / 16.0, 15 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(15 / 16.0, 3 / 16.0, 8 / 16.0),
              new Vector3(15 / 16.0, 3 / 16.0, 15 / 16.0),
              new Vector3(15 / 16.0, 2 / 16.0, 8 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 3 / 16.0, 8 / 16.0),
              new Vector3(15 / 16.0, 3 / 16.0, 8 / 16.0),
              new Vector3(8 / 16.0, 2 / 16.0, 8 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(15 / 16.0, 3 / 16.0, 15 / 16.0),
              new Vector3(8 / 16.0, 3 / 16.0, 15 / 16.0),
              new Vector3(15 / 16.0, 2 / 16.0, 15 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(1 / 16.0, 8 / 16.0, 8 / 16.0),
              new Vector3(1 / 16.0, 8 / 16.0, 1.01 / 16.0),
              new Vector3(1 / 16.0, 7 / 16.0, 8 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 8 / 16.0, 1.01 / 16.0),
              new Vector3(8 / 16.0, 8 / 16.0, 8 / 16.0),
              new Vector3(8 / 16.0, 7 / 16.0, 1.01 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(1 / 16.0, 8 / 16.0, 1.01 / 16.0),
              new Vector3(8 / 16.0, 8 / 16.0, 1.01 / 16.0),
              new Vector3(1 / 16.0, 7 / 16.0, 1.01 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 8 / 16.0, 8 / 16.0),
              new Vector3(1 / 16.0, 8 / 16.0, 8 / 16.0),
              new Vector3(8 / 16.0, 7 / 16.0, 8 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(1 / 16.0, 12 / 16.0, 15 / 16.0),
              new Vector3(1 / 16.0, 12 / 16.0, 8 / 16.0),
              new Vector3(1 / 16.0, 11 / 16.0, 15 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 12 / 16.0, 8 / 16.0),
              new Vector3(8 / 16.0, 12 / 16.0, 15 / 16.0),
              new Vector3(8 / 16.0, 11 / 16.0, 8 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(1 / 16.0, 12 / 16.0, 8 / 16.0),
              new Vector3(8 / 16.0, 12 / 16.0, 8 / 16.0),
              new Vector3(1 / 16.0, 11 / 16.0, 8 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 12 / 16.0, 15 / 16.0),
              new Vector3(1 / 16.0, 12 / 16.0, 15 / 16.0),
              new Vector3(8 / 16.0, 11 / 16.0, 15 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          )
      },
      // stem
      Model.rotateY(new Quad[] {
          new Quad(
              new Vector3(4.5 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(11.5 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(4.5 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(12 / 16.0, 4 / 16.0, 16 / 16.0, 2 / 16.0)
          ),
          new Quad(
              new Vector3(11.5 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(4.5 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(11.5 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(12 / 16.0, 4 / 16.0, 16 / 16.0, 2 / 16.0)
          )
      }, Math.toRadians(45)),
      Model.rotateY(new Quad[] {
          new Quad(
              new Vector3(4.5 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(11.5 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(4.5 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(12 / 16.0, 4 / 16.0, 16 / 16.0, 2 / 16.0)
          ),
          new Quad(
              new Vector3(11.5 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(4.5 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(11.5 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(12 / 16.0, 4 / 16.0, 16 / 16.0, 2 / 16.0)
          )
      }, Math.toRadians(-45))
  );
  //endregion

  //region Bottom Model
  private static final Quad[] bottomQuadsNorth = Model.join(
      Model.rotateY(new Quad[] {
          new Quad(
              new Vector3(4.5 / 16.0, 16 / 16.0, 8 / 16.0),
              new Vector3(11.5 / 16.0, 16 / 16.0, 8 / 16.0),
              new Vector3(4.5 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(11.5 / 16.0, 16 / 16.0, 8 / 16.0),
              new Vector3(4.5 / 16.0, 16 / 16.0, 8 / 16.0),
              new Vector3(11.5 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          )}, Math.toRadians(45)),
      Model.rotateY(new Quad[] {
          new Quad(
              new Vector3(4.5 / 16.0, 16 / 16.0, 8 / 16.0),
              new Vector3(11.5 / 16.0, 16 / 16.0, 8 / 16.0),
              new Vector3(4.5 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(11.5 / 16.0, 16 / 16.0, 8 / 16.0),
              new Vector3(4.5 / 16.0, 16 / 16.0, 8 / 16.0),
              new Vector3(11.5 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          )
      }, Math.toRadians(-45))
  );
  //endregion

  private static final Quad[][] orientedTopQuads = new Quad[4][];
  private static final Quad[][] orientedBottomQuads = new Quad[4][];

  private static final Texture[] topTextures;
  private static final Texture[] bottomTextures;

  static {
    Texture top = Texture.smallDripleafTop;
    Texture side = Texture.smallDripleafSide;
    Texture stemTop = Texture.smallDripleafStemTop;
    Texture stemBottom = Texture.smallDripleafStemBottom;
    topTextures = new Texture[] {
        top, top, top, top, top, top, side, side, side, side, side, side, side, side, side, side,
        side, side, stemTop, stemTop, stemTop, stemTop
    };
    bottomTextures = new Texture[] {stemBottom, stemBottom, stemBottom, stemBottom};

    orientedTopQuads[0] = topQuadsNorth;
    orientedTopQuads[1] = Model.rotateY(orientedTopQuads[0]);
    orientedTopQuads[2] = Model.rotateY(orientedTopQuads[1]);
    orientedTopQuads[3] = Model.rotateY(orientedTopQuads[2]);
    orientedBottomQuads[0] = bottomQuadsNorth;
    orientedBottomQuads[1] = Model.rotateY(orientedBottomQuads[0]);
    orientedBottomQuads[2] = Model.rotateY(orientedBottomQuads[1]);
    orientedBottomQuads[3] = Model.rotateY(orientedBottomQuads[2]);
  }

  private final Quad[] quads;
  private final Texture[] textures;

  public SmallDripleafModel(String facingString, String halfString) {
    quads = halfString.equals("upper")
        ? orientedTopQuads[getOrientationIndex(facingString)]
        : orientedBottomQuads[getOrientationIndex(facingString)];
    textures = halfString.equals("upper") ? topTextures : bottomTextures;
  }

  private static int getOrientationIndex(String facing) {
    switch (facing) {
      case "east":
        return 1;
      case "south":
        return 2;
      case "west":
        return 3;
      case "north":
      default:
        return 0;
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
