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
import se.llbit.chunky.model.model.ComparatorModel;
import se.llbit.chunky.resources.Texture;

// TODO: render locked repeaters.
public class Comparator extends AbstractModelBlock {

  private final String description;

  public Comparator(String facingString, String modeString, boolean powered) {
    super("comparator", Texture.redstoneRepeaterOn);
    this.description = String.format("facing=%s, mode=%s, powered=%s",
        facingString, modeString, powered);
    int mode = modeString.equals("compare") ? 0 : 1;
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

    this.model = new ComparatorModel(facing, mode, powered ? 1 : 0);
  }

  @Override
  public String description() {
    return description;
  }
}
