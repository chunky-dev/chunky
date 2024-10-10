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
import se.llbit.chunky.model.minecraft.RailModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texture.AbstractTexture;

public class Rail extends AbstractModelBlock {

  private final String description;

  public Rail(String name, AbstractTexture straightTrack, String shape) {
    super(name, Texture.dispenserFront);
    AbstractTexture[] texture = new AbstractTexture[]{
        straightTrack, straightTrack, straightTrack, straightTrack, straightTrack, straightTrack,
        Texture.railsCurved, Texture.railsCurved, Texture.railsCurved, Texture.railsCurved
    };
    this.description = "shape=" + shape;
    solid = false;

    int variation;
    switch (shape) {
      default:
      case "north_south":
        variation = 0;
        break;
      case "east_west":
        variation = 1;
        break;
      case "ascending_east":
        variation = 2;
        break;
      case "ascending_west":
        variation = 3;
        break;
      case "ascending_north":
        variation = 4;
        break;
      case "ascending_south":
        variation = 5;
        break;
      case "north_west":
        variation = 8;
        break;
      case "north_east":
        variation = 9;
        break;
      case "south_east":
        variation = 6;
        break;
      case "south_west":
        variation = 7;
        break;
    }
    this.model = new RailModel(texture[variation], variation);
  }

  @Override
  public String description() {
    return description;
  }
}
