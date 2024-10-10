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
import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.math.AABB;

public class DispenserModel extends AABBModel {
  private final static AABB[] boxes = { new AABB(0, 1, 0, 1, 0, 1) };

  private final AbstractTexture[][] textures;

  public DispenserModel(String facing, AbstractTexture front, AbstractTexture frontVertical, AbstractTexture side, AbstractTexture back) {
    textures = new AbstractTexture[1][];

    switch (facing) {
      case "up":
        textures[0] = new AbstractTexture[] {back, back, back, back, frontVertical, back};
        break;
      case "down":
        textures[0] = new AbstractTexture[] {back, back, back, back, back, frontVertical};
        break;
      case "north":
        textures[0] = new AbstractTexture[] {front, side, side, side, back, back};
        break;
      case "east":
        textures[0] = new AbstractTexture[] {side, front, side, side, back, back};
        break;
      case "south":
        textures[0] = new AbstractTexture[] {side, side, front, side, back, back};
        break;
      case "west":
        textures[0] = new AbstractTexture[] {side, side, side, front, back, back};
        break;
    }
  }

  @Override
  public AABB[] getBoxes() {
    return boxes;
  }

  @Override
  public AbstractTexture[][] getTextures() {
    return textures;
  }
}
