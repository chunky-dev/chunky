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

import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.*;

public class SnowModel extends QuadModel {
  protected static final Quad[][] model = new Quad[8][];

  static {
    for (int i = 0; i < 8; ++i) {
      double height = (i + 1) * .125;

      model[i] = new Quad[6];

      // front
      model[i][0] =
          new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 0), new Vector3(1, height, 0),
              new Vector4(1, 0, 0, height));
      // back
      model[i][1] =
          new Quad(new Vector3(0, 0, 1), new Vector3(1, 0, 1), new Vector3(0, height, 1),
              new Vector4(0, 1, 0, height));

      // right
      model[i][2] =
          new Quad(new Vector3(0, 0, 0), new Vector3(0, 0, 1), new Vector3(0, height, 0),
              new Vector4(0, 1, 0, height));

      // left
      model[i][3] =
          new Quad(new Vector3(1, 0, 1), new Vector3(1, 0, 0), new Vector3(1, height, 1),
              new Vector4(1, 0, 0, height));

      // top
      model[i][4] = new Quad(new Vector3(1, height, 0), new Vector3(0, height, 0),
          new Vector3(1, height, 1), new Vector4(1, 0, 0, 1));

      // bottom
      model[i][5] = new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1));
    }
  }

  private final static Texture[] textures = {
      Texture.snowBlock, Texture.snowBlock,Texture.snowBlock,
      Texture.snowBlock, Texture.snowBlock,Texture.snowBlock
  };

  private final Quad[] quads;

  public SnowModel(int layers) {
    quads = model[(int) QuickMath.clamp(layers-1, 0, model.length-1)];
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
