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

import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class ScaffoldingModel extends QuadModel {
  private static final Texture top = Texture.scaffoldingTop;
  private static final Texture side = Texture.scaffoldingSide;
  private static final Texture bottom = Texture.scaffoldingBottom;
  private static final Texture[] textures = new Texture[] {
      top, top, bottom, side, side, side, side, bottom, side, side, side, side, bottom, side, side, side, side, bottom,
      side, side, side, side, bottom, side, side, bottom, side, side, bottom, side, side, bottom, side, side, top, top,
      bottom, side, side, bottom, side, side, bottom, side, side, bottom, side, side
  };

  //region Model
  private static final Quad[] model = new Quad[] {
      new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 15.99 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 15.99 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 15.99 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 2 / 16.0),
          new Vector4(0 / 16.0, 2 / 16.0, 2 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 2 / 16.0),
          new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 2 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector4(1 - 2 / 16.0, 1 - 0 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 2 / 16.0),
          new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 0 / 16.0, 14 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 14 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 2 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(14 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 14 / 16.0),
          new Vector4(14 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 14 / 16.0),
          new Vector4(1 - 2 / 16.0, 1 - 0 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 0 / 16.0, 14 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 14 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(14 / 16.0, 16 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(14 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 14 / 16.0),
          new Vector4(14 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 14 / 16.0),
          new Vector4(1 - 16 / 16.0, 1 - 14 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(14 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 2 / 16.0),
          new Vector4(14 / 16.0, 16 / 16.0, 2 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 2 / 16.0),
          new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 2 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector4(1 - 16 / 16.0, 1 - 14 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 2 / 16.0),
          new Vector4(14 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 14 / 16.0, 0 / 16.0),
          new Vector3(14 / 16.0, 14 / 16.0, 0 / 16.0),
          new Vector3(2 / 16.0, 14 / 16.0, 2 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(2 / 16.0, 14 / 16.0, 0 / 16.0),
          new Vector4(1 - 14 / 16.0, 1 - 2 / 16.0, 1 - 16 / 16.0, 1 - 14 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 14 / 16.0, 2 / 16.0),
          new Vector4(14 / 16.0, 2 / 16.0, 14 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 14 / 16.0, 14 / 16.0),
          new Vector3(14 / 16.0, 14 / 16.0, 14 / 16.0),
          new Vector3(2 / 16.0, 14 / 16.0, 16 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(2 / 16.0, 14 / 16.0, 14 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(14 / 16.0, 14 / 16.0, 16 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 14 / 16.0, 2 / 16.0),
          new Vector3(16 / 16.0, 14 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 14 / 16.0, 14 / 16.0),
          new Vector4(14 / 16.0, 16 / 16.0, 14 / 16.0, 2 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 14 / 16.0, 14 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 14 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(16 / 16.0, 14 / 16.0, 2 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 14 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 14 / 16.0, 2 / 16.0),
          new Vector3(0 / 16.0, 14 / 16.0, 14 / 16.0),
          new Vector4(0 / 16.0, 2 / 16.0, 14 / 16.0, 2 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(0 / 16.0, 14 / 16.0, 14 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(2 / 16.0, 14 / 16.0, 2 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 1 - 16 / 16.0, 1 - 14 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 2 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 1.99 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 1.99 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 1.99 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 2 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 2 / 16.0, 0 / 16.0),
          new Vector3(14 / 16.0, 2 / 16.0, 0 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector4(14 / 16.0, 2 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 2 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 2 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 2 / 16.0),
          new Vector4(14 / 16.0, 2 / 16.0, 14 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 0 / 16.0, 14 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 14 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 2 / 16.0, 14 / 16.0),
          new Vector3(14 / 16.0, 2 / 16.0, 14 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 14 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 2 / 16.0, 16 / 16.0),
          new Vector3(2 / 16.0, 2 / 16.0, 16 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(14 / 16.0, 2 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 0 / 16.0, 2 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 14 / 16.0),
          new Vector4(14 / 16.0, 16 / 16.0, 14 / 16.0, 2 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 2 / 16.0, 14 / 16.0),
          new Vector3(14 / 16.0, 2 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 14 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 14 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 2 / 16.0, 2 / 16.0),
          new Vector3(16 / 16.0, 2 / 16.0, 14 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 2 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 0 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 2 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 14 / 16.0),
          new Vector4(0 / 16.0, 2 / 16.0, 14 / 16.0, 2 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 2 / 16.0, 14 / 16.0),
          new Vector3(0 / 16.0, 2 / 16.0, 2 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 14 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 2 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 2 / 16.0, 14 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 2 / 16.0),
          new Vector4(14 / 16.0, 2 / 16.0, 16 / 16.0, 14 / 16.0)
      )
  };
  //endregion

  private final Quad[] quads;

  public ScaffoldingModel(boolean bottom) {
    quads = new Quad[bottom ? model.length : model.length-14];
    System.arraycopy(model, 0, quads, 0, quads.length);
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
