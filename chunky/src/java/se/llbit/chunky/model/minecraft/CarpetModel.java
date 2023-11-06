/*
 * Copyright (c) 2013-2023 Chunky contributors
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

import se.llbit.chunky.model.AABBModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;

import java.util.Arrays;

public class CarpetModel extends AABBModel {
  private static final AABB[] aabbs = { new AABB(0, 1, 0, 1 / 16., 0, 1) };

  private final Texture[][] textures;

  public CarpetModel(Texture color) {
    textures = new Texture[1][6];
    Arrays.fill(textures[0], color);
  }

  @Override
  public AABB[] getBoxes() {
    return aabbs;
  }

  @Override
  public Texture[][] getTextures() {
    return textures;
  }
}
