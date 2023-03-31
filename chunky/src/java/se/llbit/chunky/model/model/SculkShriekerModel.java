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

public class SculkShriekerModel extends QuadModel {
  private static final Quad[] quads = new Quad[]{
    // bottom_slab
    new Quad(
      new Vector3(0 / 16.0, 8 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 8 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 8 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 8 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 8 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 8 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 8 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 8 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 8 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 8 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 8 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 8 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 8 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 8 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 8 / 16.0, 0 / 16.0)
    ),
    // top_slab
    new Quad(
      new Vector3(1 / 16.0, 15 / 16.0, 15 / 16.0),
      new Vector3(15 / 16.0, 15 / 16.0, 15 / 16.0),
      new Vector3(1 / 16.0, 15 / 16.0, 1 / 16.0),
      new Vector4(1 / 16.0, 15 / 16.0, 1 / 16.0, 15 / 16.0)
    ),
    new Quad(
      new Vector3(1 / 16.0, 15 / 16.0, 15 / 16.0),
      new Vector3(1 / 16.0, 15 / 16.0, 1 / 16.0),
      new Vector3(1 / 16.0, 8 / 16.0, 15 / 16.0),
      new Vector4(15 / 16.0, 1 / 16.0, 15 / 16.0, 8 / 16.0)
    ),
    new Quad(
      new Vector3(15 / 16.0, 15 / 16.0, 1 / 16.0),
      new Vector3(15 / 16.0, 15 / 16.0, 15 / 16.0),
      new Vector3(15 / 16.0, 8 / 16.0, 1 / 16.0),
      new Vector4(15 / 16.0, 1 / 16.0, 15 / 16.0, 8 / 16.0)
    ),
    new Quad(
      new Vector3(1 / 16.0, 15 / 16.0, 1 / 16.0),
      new Vector3(15 / 16.0, 15 / 16.0, 1 / 16.0),
      new Vector3(1 / 16.0, 8 / 16.0, 1 / 16.0),
      new Vector4(15 / 16.0, 1 / 16.0, 15 / 16.0, 8 / 16.0)
    ),
    new Quad(
      new Vector3(15 / 16.0, 15 / 16.0, 15 / 16.0),
      new Vector3(1 / 16.0, 15 / 16.0, 15 / 16.0),
      new Vector3(15 / 16.0, 8 / 16.0, 15 / 16.0),
      new Vector4(15 / 16.0, 1 / 16.0, 15 / 16.0, 8 / 16.0)
    ),
    // up
    new Quad(
      new Vector3(1 / 16.0, 14.98 / 16.0, 1 / 16.0),
      new Vector3(15 / 16.0, 14.98 / 16.0, 1 / 16.0),
      new Vector3(1 / 16.0, 14.98 / 16.0, 15 / 16.0),
      new Vector4(1 / 16.0, 15 / 16.0, 1 / 16.0, 15 / 16.0)
    ),
    // south
    new Quad(
      new Vector3(1 / 16.0, 15 / 16.0, 14.98 / 16.0),
      new Vector3(15 / 16.0, 15 / 16.0, 14.98 / 16.0),
      new Vector3(1 / 16.0, 8 / 16.0, 14.98 / 16.0),
      new Vector4(15 / 16.0, 1 / 16.0, 15 / 16.0, 8 / 16.0)
    ),
    // north
    new Quad(
      new Vector3(15 / 16.0, 15 / 16.0, 1.02 / 16.0),
      new Vector3(1 / 16.0, 15 / 16.0, 1.02 / 16.0),
      new Vector3(15 / 16.0, 8 / 16.0, 1.02 / 16.0),
      new Vector4(15 / 16.0, 1 / 16.0, 15 / 16.0, 8 / 16.0)
    ),
    // east
    new Quad(
      new Vector3(14.98 / 16.0, 15 / 16.0, 15 / 16.0),
      new Vector3(14.98 / 16.0, 15 / 16.0, 1 / 16.0),
      new Vector3(14.98 / 16.0, 8 / 16.0, 15 / 16.0),
      new Vector4(15 / 16.0, 1 / 16.0, 15 / 16.0, 8 / 16.0)
    ),
    // west
    new Quad(
      new Vector3(1.02 / 16.0, 15 / 16.0, 1 / 16.0),
      new Vector3(1.02 / 16.0, 15 / 16.0, 15 / 16.0),
      new Vector3(1.02 / 16.0, 8 / 16.0, 1 / 16.0),
      new Vector4(15 / 16.0, 1 / 16.0, 15 / 16.0, 8 / 16.0)
    )
  };

  private static final Texture top = Texture.sculkShriekerTop;
  private static final Texture side = Texture.sculkShriekerSide;
  private static final Texture bottom = Texture.sculkShriekerBottom;

  private static final Texture[] defaultTextures = new Texture[]{
    Texture.sculkShriekerInnerTop, bottom, side, side, side, side, top, side, side, side, side, top, side, side, side, side
  };

  private static final Texture[] canSummonTextures = new Texture[]{
    Texture.sculkShriekerCanSummonInnerTop, bottom, side, side, side, side, top, side, side, side, side, top, side, side, side, side
  };

  private final Texture[] textures;

  public SculkShriekerModel(boolean canSummon) {
    this.textures = canSummon ? canSummonTextures : defaultTextures;
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
