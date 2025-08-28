/* Copyright (c) 2012-2015 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.world;

public interface BlockData {

  /**
   * Bit offset to block metadata.
   */
  int OFFSET = 8;

  int EAST = 0;
  int WEST = 1;
  int SOUTH = 2;
  int NORTH = 3;

  int RSW_EAST_CONNECTION = 12;
  int RSW_WEST_CONNECTION = 13;
  int RSW_NORTH_CONNECTION = 14;
  int RSW_SOUTH_CONNECTION = 15;
  int RSW_EAST_UP = 16;
  int RSW_WEST_UP = 17;
  int RSW_NORTH_UP = 18;
  int RSW_SOUTH_UP = 19;
  int VINE_TOP = 12;

  int GLASS_PANE_OFFSET = 12;

  /**
   * The water block has a lily pad floating on it.
   */
  int LILY_PAD = 13;

  /**
   * Rotation of the lily pad.
   */
  int LILY_PAD_ROTATION = 14;

  /**
   * Offset to bottom door metadata.
   */
  int DOOR_BOTTOM = 12;

  /**
   * Offset to top door metadata.
   */
  int DOOR_TOP = 16;

  /**
   * Offset for stone wall data.
   */
  int STONEWALL_CONN = 9;

  /**
   * One bit telling if the stone wall is a corner section
   */
  int STONEWALL_CORNER = 14;

  int CORNER_OFFSET = 24;

  int SOUTH_EAST = 8 + 0;
  int SOUTH_WEST = 8 + 1;
  int NORTH_EAST = 8 + 2;
  int NORTH_WEST = 8 + 3;
  int INNER_SOUTH_EAST = 8 + 4;
  int INNER_SOUTH_WEST = 8 + 5;
  int INNER_NORTH_EAST = 8 + 6;
  int INNER_NORTH_WEST = 8 + 7;

  /**
   * Fence gate is three pixels lower.
   */
  int FENCEGATE_LOW = 20;

  /**
   * The upside down stair bit.
   */
  int UPSIDE_DOWN_STAIR = 1 << 10;

  // Directional connection bits.
  int CONNECTED_NORTH = 1;
  int CONNECTED_SOUTH = 2;
  int CONNECTED_EAST = 4;
  int CONNECTED_WEST = 8;
  int CONNECTED_ABOVE = 16;
  int CONNECTED_BELOW = 32;

  int BED_COLOR = 16;

  int COLOR_WHITE = 0;
  int COLOR_ORANGE = 1;
  int COLOR_MAGENTA = 2;
  int COLOR_LIGHT_BLUE = 3;
  int COLOR_YELLOW = 4;
  int COLOR_LIME = 5;
  int COLOR_PINK = 6;
  int COLOR_GRAY = 7;
  int COLOR_SILVER = 8;
  int COLOR_CYAN = 9;
  int COLOR_PURPLE = 10;
  int COLOR_BLUE = 11;
  int COLOR_BROWN = 12;
  int COLOR_GREEN = 13;
  int COLOR_RED = 14;
  int COLOR_BLACK = 15;
}
