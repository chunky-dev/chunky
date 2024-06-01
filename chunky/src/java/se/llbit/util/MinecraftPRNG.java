/* Copyright (c) 2019 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.util;

/**
 * Pseudo-random number generator matching that used in Minecraft
 * for lilypad rotations.
 */
public class MinecraftPRNG {
  /**
   * Get a random number based on a 3D world position.
   */
  public static long rand(long x, long y, long z) {
    long pr = (x * 3129871L) ^ (z * 116129781L) ^ y;
    return pr * pr * 42317861L + pr * 11L;
  }
}
