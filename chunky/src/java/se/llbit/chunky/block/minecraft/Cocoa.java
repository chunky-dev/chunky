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
import se.llbit.chunky.model.minecraft.CocoaPlantModel;
import se.llbit.chunky.model.minecraft.CocoaPlantModel119;
import se.llbit.chunky.resources.Texture;

public class Cocoa extends AbstractModelBlock {

  private final String description;

  public Cocoa(String facingString, int age) {
    super("cocoa", Texture.cocoaPlantLarge);
    description = String.format("facing=%s, age=%d", facingString, age);
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
    model = System.getProperty("chunky.blockModels.cocoa", "1.19").equals("pre-1.19")
      ? new CocoaPlantModel(facing, age)
      : new CocoaPlantModel119(facing, age);
  }

  @Override
  public String description() {
    return description;
  }
}
