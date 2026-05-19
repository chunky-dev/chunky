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

package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.resources.Texture;

public class PointedDripstone extends SpriteBlock {
  private final String description;

  public PointedDripstone(String thickness, String verticalDirection) {
    super("pointed_dripstone", getTexture(thickness, verticalDirection));
    description = "thickness=" + thickness + ", vertical_direction=" + verticalDirection;
  }

  private static Texture getTexture(String thickness, String verticalDirection) {
    if (verticalDirection.equals("down")) {
      return switch (thickness) {
        case "tip_merge" -> Texture.pointedDripstoneDownTipMerge;
        case "frustum" -> Texture.pointedDripstoneDownFrustum;
        case "middle" -> Texture.pointedDripstoneDownMiddle;
        case "base" -> Texture.pointedDripstoneDownBase;
        default -> Texture.pointedDripstoneDownTip; // tip
      };
    } else {
      return switch (thickness) {
        case "tip_merge" -> Texture.pointedDripstoneUpTipMerge;
        case "frustum" -> Texture.pointedDripstoneUpFrustum;
        case "middle" -> Texture.pointedDripstoneUpMiddle;
        case "base" -> Texture.pointedDripstoneUpBase;
        default -> Texture.pointedDripstoneUpTip; // tip
      };
    }
  }

  @Override
  public String description() {
    return description;
  }
}
