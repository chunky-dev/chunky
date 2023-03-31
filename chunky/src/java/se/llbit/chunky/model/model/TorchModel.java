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
package se.llbit.chunky.model.model;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class TorchModel extends QuadModel {

  private static final Quad[] quadsGround = new Quad[]{
      new Quad(
          new Vector3(7 / 16.0, 10 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 10 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 10 / 16.0, 7 / 16.0),
          new Vector4(7 / 16.0, 9 / 16.0, 8 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
          new Vector4(7 / 16.0, 9 / 16.0, 1 / 16.0, 3 / 16.0)
      ),
      new Quad(
          new Vector3(7 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(7 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(7 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(9 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(9 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(9 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 7 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 7 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 16 / 16.0, 9 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 9 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 9 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      )
  };

  private static final Quad[] quadsWall = Model.rotateZ(new Quad[]{
      new Quad(
          new Vector3(-1 / 16.0, 13.5 / 16.0, 9 / 16.0),
          new Vector3(1 / 16.0, 13.5 / 16.0, 9 / 16.0),
          new Vector3(-1 / 16.0, 13.5 / 16.0, 7 / 16.0),
          new Vector4(7 / 16.0, 9 / 16.0, 8 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(-1 / 16.0, 3.5 / 16.0, 7 / 16.0),
          new Vector3(1 / 16.0, 3.5 / 16.0, 7 / 16.0),
          new Vector3(-1 / 16.0, 3.5 / 16.0, 9 / 16.0),
          new Vector4(7 / 16.0, 9 / 16.0, 1 / 16.0, 3 / 16.0)
      ),
      new Quad(
          new Vector3(-1 / 16.0, 19.5 / 16.0, 16 / 16.0),
          new Vector3(-1 / 16.0, 19.5 / 16.0, 0 / 16.0),
          new Vector3(-1 / 16.0, 3.5 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(1 / 16.0, 19.5 / 16.0, 0 / 16.0),
          new Vector3(1 / 16.0, 19.5 / 16.0, 16 / 16.0),
          new Vector3(1 / 16.0, 3.5 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(-8 / 16.0, 19.5 / 16.0, 7 / 16.0),
          new Vector3(8 / 16.0, 19.5 / 16.0, 7 / 16.0),
          new Vector3(-8 / 16.0, 3.5 / 16.0, 7 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 19.5 / 16.0, 9 / 16.0),
          new Vector3(-8 / 16.0, 19.5 / 16.0, 9 / 16.0),
          new Vector3(8 / 16.0, 3.5 / 16.0, 9 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      )
  }, Math.toRadians(-22.5), new Vector3(0, 3.5 / 16, 8 / 16.));

  private static final Quad[][] rotatedQuadsWall = new Quad[6][];

  static {
    rotatedQuadsWall[1] = quadsWall; // east
    rotatedQuadsWall[3] = Model.rotateY(rotatedQuadsWall[1]); // south
    rotatedQuadsWall[2] = Model.rotateY(rotatedQuadsWall[3]); // west
    rotatedQuadsWall[4] = Model.rotateY(rotatedQuadsWall[2]); // north
  }

  private final Texture[] textures;

  private final int rotation;

  public TorchModel(Texture texture, int rotation) {
    this.textures = new Texture[]{texture, texture, texture, texture, texture, texture};
    this.rotation = rotation;
  }

  @Override
  public Quad[] getQuads() {
    if (rotation < 5) {
      return rotatedQuadsWall[rotation];
    }
    return quadsGround;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }
}
