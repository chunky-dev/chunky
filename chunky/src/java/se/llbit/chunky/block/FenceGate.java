/*
 * Copyright (c) 2017 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.BlockData;

public class FenceGate extends Block {
  public FenceGate(int id, String name, Texture texture) {
    super(id, name, texture);
  }

  @Override public boolean isFenceGate() {
    return true;
  }

  @Override public boolean isFenceConnector(int data, int direction) {
    if (direction == BlockData.NORTH || direction == BlockData.SOUTH) {
      return (data & 0x1) != 0;
    } else {
      return (data & 0x1) == 0;
    }
  }

  @Override public boolean isNetherBrickFenceConnector(int data, int direction) {
    if (direction == BlockData.NORTH || direction == BlockData.SOUTH) {
      return (data & 0x1) != 0;
    } else {
      return (data & 0x1) == 0;
    }
  }

  @Override public boolean isStoneWallConnector(int data, int direction) {
    if (direction == BlockData.NORTH || direction == BlockData.SOUTH) {
      return (data & 0x1) != 0;
    } else {
      return (data & 0x1) == 0;
    }
  }
}
