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
import se.llbit.chunky.model.model.RedstoneRepeaterModel;
import se.llbit.chunky.resources.Texture;

// TODO: render locked repeaters.
public class Repeater extends AbstractModelBlock {
  private final int facing;
  private final String description;

  public Repeater(int delay, String facingString, boolean powered, boolean locked) {
    super("repeater", Texture.redstoneRepeaterOn);
    this.description = String.format("delay=%d, facing=%s, powered=%s, locked=%s",
        delay, facingString, powered, locked);
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
    this.model = new RedstoneRepeaterModel(3 & (delay - 1), facing, powered ? 1 : 0,
        locked ? 1 : 0);
  }

  public int getFacing() {
    return facing;
  }

  @Override
  public String description() {
    return description;
  }
}
