/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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

@SuppressWarnings("javadoc")
public interface BlockData {

	/**
	 * Offset to block metadata
	 */
	int BLOCK_DATA_OFFSET = 8;

	int RSW_EAST_CONNECTION = 12;
	int RSW_WEST_CONNECTION = 13;
	int RSW_NORTH_CONNECTION = 14;
	int RSW_SOUTH_CONNECTION = 15;
	int RSW_EAST_SIDE = 16;
	int RSW_WEST_SIDE = 17;
	int RSW_NORTH_SIDE = 18;
	int RSW_SOUTH_SIDE = 19;
	int VINE_TOP = 12;

	/**
	 * The water block has a lily pad floating on it
	 */
	int LILY_PAD = 13;

	/**
	 * Rotation of the lily pad
	 */
	int LILY_PAD_ROTATION = 14;

	/**
	 * Offset to bottom door metadata
	 */
	int DOOR_BOTTOM = 12;

	/**
	 * Offset to top door metadata
	 */
	int DOOR_TOP = 16;

	/**
	 * Offset for stone wall data
	 */
	int STONEWALL_CONN = 9;

	/**
	 * One bit telling if the stone wall is a corner section
	 */
	int STONEWALL_CORNER = 14;

	int CORNER_OFFSET = 24;

	int SOUTH_EAST = 4;
	int SOUTH_WEST = 5;
	int NORTH_EAST = 6;
	int NORTH_WEST = 7;

	/**
	 * Fence gate is three pixels lower
	 */
	int FENCEGATE_LOW = 20;
}
