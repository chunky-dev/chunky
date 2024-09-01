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

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.TorchModel;
import se.llbit.chunky.resources.texture.AbstractTexture;

/**
 * A torch attached to a wall.
 */
public class WallTorch extends AbstractModelBlock {
  protected final String facing;

  public WallTorch(String name, AbstractTexture texture, String facing) {
    super(name, texture);
    this.facing = facing;
    solid = false;
    int facingInt;
    switch (facing) {
      default:
      case "north":
        facingInt = 4;
        break;
      case "south":
        facingInt = 3;
        break;
      case "west":
        facingInt = 2;
        break;
      case "east":
        facingInt = 1;
        break;
    }
    model = new TorchModel(texture, facingInt);
  }

  @Override
  public String description() {
    return "facing=" + facing;
  }
}
