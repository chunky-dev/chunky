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
import se.llbit.chunky.model.model.EndRodModel;
import se.llbit.chunky.resources.Texture;

public class EndRod extends AbstractModelBlock {

  private final String description;

  public EndRod(String facingString) {
    super("end_rod", Texture.endRod);
    this.description = "facing=" + facingString;
    int facing;
    switch (facingString) {
      case "down":
        facing = 0;
        break;
      default:
      case "up":
        facing = 1;
        break;
      case "north":
        facing = 2;
        break;
      case "south":
        facing = 3;
        break;
      case "west":
        facing = 4;
        break;
      case "east":
        facing = 5;
        break;
    }
    model = new EndRodModel(facing);
  }

  @Override
  public String description() {
    return description;
  }
}
