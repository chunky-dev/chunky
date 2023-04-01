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
import se.llbit.chunky.model.minecraft.VineModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.BlockData;

public class Vine extends AbstractModelBlock {

  private final String description;

  public Vine(boolean north, boolean south, boolean east, boolean west,
      boolean up) {
    super("vine", Texture.vines);
    solid = false;
    this.description = String.format("north=%s, south=%s, east=%s, west=%s, up=%s",
        north, south, east, west, up);
    int connections = 0;
    if (north) {
      connections |= BlockData.CONNECTED_NORTH;
    }
    if (south) {
      connections |= BlockData.CONNECTED_SOUTH;
    }
    if (east) {
      connections |= BlockData.CONNECTED_EAST;
    }
    if (west) {
      connections |= BlockData.CONNECTED_WEST;
    }
    if (up) {
      connections |= BlockData.CONNECTED_ABOVE;
    }
    if (connections == 0) {
      // If no side is true, render on south side.
      connections = BlockData.CONNECTED_SOUTH;
    }
    this.model = new VineModel(connections);
  }

  @Override
  public String description() {
    return description;
  }
}
