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
import se.llbit.chunky.model.model.ObserverModel;
import se.llbit.chunky.resources.Texture;

public class Observer extends AbstractModelBlock {

  private final String description;

  public Observer(String facing, boolean powered) {
    super("observer", Texture.observerFront);
    this.description = String.format("facing=%s, powered=%s", facing, powered);
    int direction;
    switch (facing) {
      case "up":
        direction = 1;
        break;
      case "down":
        direction = 0;
        break;
      default:
      case "north":
        direction = 2;
        break;
      case "east":
        direction = 5;
        break;
      case "south":
        direction = 3;
        break;
      case "west":
        direction = 4;
        break;
    }
    this.model = new ObserverModel(direction, powered);
    opaque = true;
  }

  @Override
  public String description() {
    return description;
  }
}
