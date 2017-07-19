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
import se.llbit.chunky.world.Block;
import se.llbit.chunky.world.BlockData;

public class Stairs extends Block {
  public Stairs(int id, String name, Texture texture) {
    super(id, name, texture);
  }

  @Override public boolean isFenceConnector(int data, int direction) {
    switch (direction) {
      case BlockData.NORTH:
        return (data & 0x3) == BlockData.SOUTH;
      case BlockData.SOUTH:
        return (data & 0x3) == BlockData.NORTH;
      case BlockData.EAST:
        return (data & 0x3) == BlockData.WEST;
      case BlockData.WEST:
        return (data & 0x3) == BlockData.EAST;
    }
    return false;
  }

  @Override public boolean isNetherBrickFenceConnector(int data, int direction) {
    switch (direction) {
      case BlockData.NORTH:
        return (data & 0x3) == BlockData.SOUTH;
      case BlockData.SOUTH:
        return (data & 0x3) == BlockData.NORTH;
      case BlockData.EAST:
        return (data & 0x3) == BlockData.WEST;
      case BlockData.WEST:
        return (data & 0x3) == BlockData.EAST;
    }
    return false;
  }

  @Override public boolean isStoneWallConnector(int data, int direction) {
    switch (direction) {
      case BlockData.NORTH:
        return (data & 0x3) == BlockData.SOUTH;
      case BlockData.SOUTH:
        return (data & 0x3) == BlockData.NORTH;
      case BlockData.EAST:
        return (data & 0x3) == BlockData.WEST;
      case BlockData.WEST:
        return (data & 0x3) == BlockData.EAST;
    }
    return false;
  }

  @Override public boolean isGlassPaneConnector(int data, int direction) {
    switch (direction) {
      case BlockData.NORTH:
        return (data & 0x3) == BlockData.SOUTH;
      case BlockData.SOUTH:
        return (data & 0x3) == BlockData.NORTH;
      case BlockData.EAST:
        return (data & 0x3) == BlockData.WEST;
      case BlockData.WEST:
        return (data & 0x3) == BlockData.EAST;
    }
    return false;
  }

  @Override public boolean isIronBarsConnector(int data, int direction) {
    switch (direction) {
      case BlockData.NORTH:
        return (data & 0x3) == BlockData.SOUTH;
      case BlockData.SOUTH:
        return (data & 0x3) == BlockData.NORTH;
      case BlockData.EAST:
        return (data & 0x3) == BlockData.WEST;
      case BlockData.WEST:
        return (data & 0x3) == BlockData.EAST;
    }
    return false;
  }
}
