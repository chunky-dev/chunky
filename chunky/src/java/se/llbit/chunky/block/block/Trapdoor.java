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
import se.llbit.chunky.model.model.TrapdoorModel;
import se.llbit.chunky.resources.Texture;

// TODO: fix rendering/texturing bugs.
public class Trapdoor extends AbstractModelBlock {

  private final String description;

  public Trapdoor(String name, Texture texture,
      String half, String facing, boolean open) {
    super(name, texture);
    solid = false;
    this.description = String.format("half=%s, facing=%s, open=%s",
        half, facing, open);
    int state;
    switch (facing) {
      default:
      case "north":
        state = 0;
        break;
      case "south":
        state = 1;
        break;
      case "east":
        state = 3;
        break;
      case "west":
        state = 2;
        break;
    }
    if (open) {
      state |= 4;
    }
    if (half.equals("top")) {
      state |= 8;
    }
    this.model = new TrapdoorModel(texture, state);
  }

  @Override
  public String description() {
    return description;
  }
}
