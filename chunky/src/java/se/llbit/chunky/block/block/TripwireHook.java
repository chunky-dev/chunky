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

package se.llbit.chunky.block.block;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.block.BlockFace;
import se.llbit.chunky.model.model.TripwireHookModel;
import se.llbit.chunky.resources.Texture;

public class TripwireHook extends AbstractModelBlock {
  private final BlockFace facing;
  private final String description;

  public TripwireHook(String facingString, boolean attached, boolean powered) {
    super("tripwire_hook", Texture.tripwire);
    this.description = String
        .format("facing=%s,attached=%s,powered=%s", facingString, attached, powered);
    int facing;
    switch (facingString) {
      default:
      case "north":
        facing = 0;
        break;
      case "south":
        facing = 2;
        break;
      case "west":
        facing = 3;
        break;
      case "east":
        facing = 1;
        break;
    }
    this.model = new TripwireHookModel(facing, attached, powered);
    this.facing = new BlockFace[]{
        BlockFace.NORTH,
        BlockFace.EAST,
        BlockFace.SOUTH,
        BlockFace.WEST
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
