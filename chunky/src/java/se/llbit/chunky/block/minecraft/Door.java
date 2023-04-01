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
import se.llbit.chunky.model.minecraft.DoorModel;
import se.llbit.chunky.resources.Texture;

// TODO: hinge placement is wrong for some variants.
public class Door extends AbstractModelBlock {
  private final BlockFace facing;
  public final String hinge;
  public final boolean open;
  private final String description;

  public Door(String name, Texture texture, String facingString, String half,
      String hinge, boolean open) {
    super(name, texture);
    this.description = String.format("facing=%s, half=%s, hinge=%s, open=%s",
        facingString, half, hinge, open);
    int mirrored = hinge.equals("left") ? 0 : 1;
    int direction;
    switch (facingString) {
      default:
      case "north":
        direction = 3;
        break;
      case "south":
        direction = 1;
        break;
      case "west":
        direction = 2;
        break;
      case "east":
        direction = 0;
        break;
    }
    int facing;
    if (open && mirrored != 0) {
      facing = (direction + 3) % 4;
    } else if (open) {
      facing = (direction + 1) % 4;
    } else {
      facing = direction;
    }

    model = new DoorModel(texture, mirrored, facing);
    this.facing = BlockFace.fromName(facingString);
    this.open = open;
    this.hinge = hinge;
  }

  @Override
  public String description() {
    return description;
  }

  public BlockFace getFacing() {
    return facing;
  }

  public String getHinge() {
    return hinge;
  }

  public boolean isOpen() {
    return open;
  }
}
