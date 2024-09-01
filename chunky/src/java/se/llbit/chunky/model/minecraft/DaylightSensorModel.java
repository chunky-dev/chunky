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
import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.math.AABB;

public class DaylightSensorModel extends AABBModel {
  private static final AABB[] aabbs = { new AABB(0, 1, 0, 6 / 16., 0, 1) };

  private final AbstractTexture[][] textures;

  public DaylightSensorModel(AbstractTexture top) {
    AbstractTexture side = Texture.daylightDetectorSide;
    textures = new AbstractTexture[][] { {side, side, side, side, top, side} };
  }

  @Override
  public AABB[] getBoxes() {
    return aabbs;
  }

  @Override
  public AbstractTexture[][] getTextures() {
    return textures;
  }
}
