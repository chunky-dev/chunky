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
import se.llbit.chunky.model.model.PistonModel;
import se.llbit.chunky.resources.Texture;

public class Piston extends AbstractModelBlock {

  private final String description;

  public Piston(String name, boolean sticky, boolean extended, String facing) {
    super(name, Texture.pistonSide);
    this.description = String.format("sticky=%s, extended=%s, facing=%s", sticky, extended, facing);
    opaque = !extended;
    solid = false;
    int orientation;
    switch (facing) {
      case "down":
        orientation = 0;
        break;
      case "up":
        orientation = 1;
        break;
      default:
      case "north":
        orientation = 2;
        break;
      case "south":
        orientation = 3;
        break;
      case "west":
        orientation = 4;
        break;
      case "east":
        orientation = 5;
        break;
    }
    this.model = new PistonModel(sticky, extended, orientation);
  }

  @Override
  public String description() {
    return description;
  }
}
