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
	 * Offset for biome ID
	 */
	int BIOME_ID = 24;
}
