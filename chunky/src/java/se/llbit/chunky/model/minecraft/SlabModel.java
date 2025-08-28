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

import se.llbit.chunky.model.AABBModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;

public class SlabModel extends AABBModel {
  private final static AABB[] lower = { new AABB(0, 1, 0, .5, 0, 1) };
  private final static AABB[] upper = { new AABB(0, 1, .5, 1, 0, 1) };
  private final static AABB[] full = { new AABB(0, 1, 0, 1, 0, 1) };

  private final AABB[] boxes;
  private final Texture[][] textures;

  public SlabModel(Texture side, Texture top, String type) {
    switch (type) {
      case "top":
        boxes = upper;
        break;
      default:
      case "bottom":
        boxes = lower;
        break;
      case "double":
        boxes = full;
        break;
    }

    textures = new Texture[][] {{
      side, side, side, side, top, top
    }};
  }

  @Override
  public AABB[] getBoxes() {
    return boxes;
  }

  @Override
  public Texture[][] getTextures() {
    return textures;
  }
}
