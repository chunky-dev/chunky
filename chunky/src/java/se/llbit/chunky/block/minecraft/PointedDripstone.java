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

  public PointedDripstone(String thickness, String verticalDirection, boolean waterlogged) {
    super("pointed_dripstone", getTexture(thickness, verticalDirection));
    this.waterlogged = waterlogged;
  }

  private static Texture getTexture(String thickness, String verticalDirection) {
    if (verticalDirection.equals("down")) {
      switch (thickness) {
        case "tip_merge":
          return Texture.pointedDripstoneDownTipMerge;
        case "frustum":
          return Texture.pointedDripstoneDownFrustum;
        case "middle":
          return Texture.pointedDripstoneDownMiddle;
        case "base":
          return Texture.pointedDripstoneDownBase;
        default:
        case "tip":
          return Texture.pointedDripstoneDownTip;
      }
    } else {
      switch (thickness) {
        case "tip_merge":
          return Texture.pointedDripstoneUpTipMerge;
        case "frustum":
          return Texture.pointedDripstoneUpFrustum;
        case "middle":
          return Texture.pointedDripstoneUpMiddle;
        case "base":
          return Texture.pointedDripstoneUpBase;
        default:
        case "tip":
          return Texture.pointedDripstoneUpTip;
      }
    }
  }
}
