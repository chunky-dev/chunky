/* Copyright (c) 2010-2012 Jesper Öqvist <jesper@llbit.se>
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

/**
 * A chunk position consists of two integer coordinates x and z.
 * <p>
 * The filename of a chunk is uniquely defined by it's position.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class ChunkPosition {

  public final int x;
  public final int z;

  public ChunkPosition(int x, int z) {
    this.x = x;
    this.z = z;
  }

  public ChunkPosition(long position) {
    this((int) (position >>> 32), (int) position);
  }

  public static long positionToLong(int x, int z) {
    return (z & 0xFFFFFFFFL) | (x & 0xFFFFFFFFL) << 32;
  }

  /**
   * @return The long representation of the chunk position
   */
  public long getLong() {
    return positionToLong(x, z);
  }

  /**
   * @return The .mca name for the region with this position
   */
  public String getMcaName() {
    return String.format("r.%d.%d.mca", x, z);
  }

  /**
   * @return The region position of this chunk position
   */
  public ChunkPosition getRegionPosition() {
    return get(x >> 5, z >> 5);
  }

  @Override
  public String toString() {
    return "[" + x + ", " + z + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ChunkPosition) {
      ChunkPosition other = (ChunkPosition) o;
      return this.x == other.x && this.z == other.z;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return 31 * x + z;
  }

  @Deprecated
  public ChunkPosition regionPosition() {
    return get(x >> 5, z >> 5);
  }

  /**
   * @return Integer representation of chunk position
   */
  @Deprecated
  public int getInt() {
    return (x << 16) | (0xFFFF & z);
  }

  /**
   * @return Decoded chunk position
   */
  @Deprecated
  public static ChunkPosition get(long longValue) {
    return new ChunkPosition(longValue);
  }

  @Deprecated
  public static ChunkPosition get(int x, int z) {
    return new ChunkPosition(x, z);
  }
}
