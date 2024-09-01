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
import se.llbit.chunky.block.BlockFace;
import se.llbit.chunky.model.minecraft.FenceGateModel;
import se.llbit.chunky.resources.texture.AbstractTexture;

public class FenceGate extends AbstractModelBlock {
  private final BlockFace facing;
  private final String description;

  public FenceGate(String name, AbstractTexture texture, String facingString, boolean inWall,
                   boolean open) {
    super(name, texture);
    this.description = String.format("facing=%s, in_wall=%s, open=%s",
        facingString, inWall, open);
    solid = false;
    int facing;
    switch (facingString) {
      default:
      case "north":
        facing = 2;
        break;
      case "south":
        facing = 0;
        break;
      case "west":
        facing = 1;
        break;
      case "east":
        facing = 3;
        break;
    }
    this.model = new FenceGateModel(texture, facing, inWall ? 1 : 0, open ? 1 : 0);
    this.facing = new BlockFace[]{
        BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST
    }[facing];
  }

  @Override
  public String description() {
    return description;
  }

  public BlockFace getFacing() {
    return this.facing;
  }
}
