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

import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class SnifferEggModel extends QuadModel {
  //region Textures
  private static final Texture[][] textures = new Texture[][]{
    new Texture[]{
      Texture.snifferEggNotCrackedTop, Texture.snifferEggNotCrackedBottom,
      Texture.snifferEggNotCrackedWest, Texture.snifferEggNotCrackedEast,
      Texture.snifferEggNotCrackedNorth, Texture.snifferEggNotCrackedSouth
    },
    new Texture[]{
      Texture.snifferEggSlightlyCrackedTop, Texture.snifferEggSlightlyCrackedBottom,
      Texture.snifferEggSlightlyCrackedWest, Texture.snifferEggSlightlyCrackedEast,
      Texture.snifferEggSlightlyCrackedNorth, Texture.snifferEggSlightlyCrackedSouth
    },
    new Texture[]{
      Texture.snifferEggVeryCrackedTop, Texture.snifferEggVeryCrackedBottom,
      Texture.snifferEggVeryCrackedWest, Texture.snifferEggVeryCrackedEast,
      Texture.snifferEggVeryCrackedNorth, Texture.snifferEggVeryCrackedSouth
    },
  };
  //endregion

  //region Quads
  private static final Quad[] quads = new Quad[]{
    new Quad(
      new Vector3(1 / 16.0, 16 / 16.0, 14 / 16.0),
      new Vector3(15 / 16.0, 16 / 16.0, 14 / 16.0),
      new Vector3(1 / 16.0, 16 / 16.0, 2 / 16.0),
      new Vector4(0 / 16.0, 14 / 16.0, 4 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(1 / 16.0, 0 / 16.0, 2 / 16.0),
      new Vector3(15 / 16.0, 0 / 16.0, 2 / 16.0),
      new Vector3(1 / 16.0, 0 / 16.0, 14 / 16.0),
      new Vector4(0 / 16.0, 14 / 16.0, 4 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(1 / 16.0, 16 / 16.0, 14 / 16.0),
      new Vector3(1 / 16.0, 16 / 16.0, 2 / 16.0),
      new Vector3(1 / 16.0, 0 / 16.0, 14 / 16.0),
      new Vector4(12 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(15 / 16.0, 16 / 16.0, 2 / 16.0),
      new Vector3(15 / 16.0, 16 / 16.0, 14 / 16.0),
      new Vector3(15 / 16.0, 0 / 16.0, 2 / 16.0),
      new Vector4(12 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(1 / 16.0, 16 / 16.0, 2 / 16.0),
      new Vector3(15 / 16.0, 16 / 16.0, 2 / 16.0),
      new Vector3(1 / 16.0, 0 / 16.0, 2 / 16.0),
      new Vector4(14 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(15 / 16.0, 16 / 16.0, 14 / 16.0),
      new Vector3(1 / 16.0, 16 / 16.0, 14 / 16.0),
      new Vector3(15 / 16.0, 0 / 16.0, 14 / 16.0),
      new Vector4(14 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    )
  };
  //endregion

  private final int age;

  public SnifferEggModel(int age) {
    this.age = QuickMath.clamp(age, 0, 2);
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return textures[age];
  }
}
