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

import static se.llbit.chunky.model.Tint.BIOME_GRASS;
import static se.llbit.chunky.model.Tint.NONE;

import se.llbit.chunky.model.AABBModel;
import se.llbit.chunky.model.Tint;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.math.AABB;

public class GrassBlockModel extends AABBModel {

  private final static Tint[][] tints = new Tint[][] {
      {BIOME_GRASS, BIOME_GRASS, BIOME_GRASS, BIOME_GRASS, NONE, NONE},
      {NONE, NONE, NONE, NONE, BIOME_GRASS, NONE}
  };

  private final static AABB[] boxes = new AABB[]{
      new AABB(0, 1, 0, 1, 0, 1),
      new AABB(0, 1, 0, 1, 0, 1)
  };

  private static final AbstractTexture[][] textures = new AbstractTexture[][]{
      {
          Texture.grassSide, Texture.grassSide,
          Texture.grassSide, Texture.grassSide,
          null, null
      },
      {
          Texture.grassSideSaturated, Texture.grassSideSaturated,
          Texture.grassSideSaturated, Texture.grassSideSaturated,
          Texture.grassTop, Texture.dirt
      }
  };

  @Override
  public AABB[] getBoxes() {
    return boxes;
  }

  @Override
  public AbstractTexture[][] getTextures() {
    return textures;
  }

  @Override
  public Tint[][] getTints() {
    return tints;
  }
}
