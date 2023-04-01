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
import se.llbit.chunky.model.minecraft.StairModel;
import se.llbit.chunky.resources.Texture;

public class Stairs extends AbstractModelBlock {

  private final int flipped;
  private final BlockFace facing;
  private final String description;

  public Stairs(String name, Texture texture, String half, String shape, String facing) {
    this(name, texture, texture, texture, half, shape, facing);
  }

  public Stairs(String name, Texture side, Texture top, Texture bottom,
      String half, String shape, String facing) {
    super(name, side);
    this.description = String.format("half=%s, shape=%s, facing=%s", half, shape, facing);
    solid = false;
    flipped = (half.equals("top")) ? 1 : 0;
    this.facing = BlockFace.fromName(facing);
    boolean isCorner = !shape.equals("straight");

    int facingVal;
    switch (this.facing) {
      case EAST:
        facingVal = 0;
        break;
      case WEST:
        facingVal = 1;
        break;
      case SOUTH:
        facingVal = 2;
        break;
      default:
      case NORTH:
        facingVal = 3;
    }

    int corner;
    switch (shape) {
      default:
      case "straight":
        corner = 0;
        break;
      case "outer_right":
        switch (facing) {
          default:
          case "east":
            corner = 0;
            break;
          case "west":
            corner = 3;
            break;
          case "south":
            corner = 1;
            break;
          case "north":
            corner = 2;
            break;
        }
        break;
      case "outer_left":
        switch (facing) {
          default:
          case "east":
            corner = 2;
            break;
          case "west":
            corner = 1;
            break;
          case "south":
            corner = 0;
            break;
          case "north":
            corner = 3;
            break;
        }
        break;
      case "inner_right":
        switch (facing) {
          default:
          case "east":
            corner = 0 + 4;
            break;
          case "west":
            corner = 3 + 4;
            break;
          case "south":
            corner = 1 + 4;
            break;
          case "north":
            corner = 2 + 4;
            break;
        }
        break;
      case "inner_left":
        switch (facing) {
          default:
          case "east":
            corner = 2 + 4;
            break;
          case "west":
            corner = 1 + 4;
            break;
          case "south":
            corner = 0 + 4;
            break;
          case "north":
            corner = 3 + 4;
            break;
        }
        break;
    }

    this.model = new StairModel(side, top, bottom, flipped, isCorner, corner, facingVal);
  }

  @Override
  public String description() {
    return description;
  }

  public String getHalf() {
    return flipped == 1 ? "top" : "bottom";
  }

  public BlockFace getFacing() {
    return facing;
  }
}
