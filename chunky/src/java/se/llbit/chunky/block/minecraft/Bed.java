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
import se.llbit.chunky.model.minecraft.BedModel;
import se.llbit.chunky.resources.texture.AbstractTexture;

public class Bed extends AbstractModelBlock {

  private final String description;

  public Bed(String name, AbstractTexture texture, String part, String facing) {
    super(name, texture);
    this.description = String.format("part=%s, facing=%s", part, facing);
    boolean head = part.equals("head");
    int direction;
    switch (facing) {
      default:
      case "north":
        direction = 2;
        break;
      case "east":
        direction = 3;
        break;
      case "south":
        direction = 0;
        break;
      case "west":
        direction = 1;
        break;
    }
    model = new BedModel(head, direction, texture);
    solid = false;
  }

  @Override
  public String description() {
    return description;
  }
}
