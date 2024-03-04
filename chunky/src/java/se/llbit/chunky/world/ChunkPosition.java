/* Copyright (c) 2010-2022 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2010-2022 Chunky Contributors
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
 */
public class ChunkPosition {

  public final int x;
  public final int z;

  public ChunkPosition(int x, int z) {
    this.x = x;
    this.z = z;
  }

  public ChunkPosition(long position) {
    this(longPositionX(position), longPositionZ(position));
  }

  /**
   * @return This chunk position packed into a long.
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
    return new ChunkPosition(x >> 5, z >> 5);
  }

  public ChunkPosition chunkPositionFromRegion(int localX, int localZ) {
    return new ChunkPosition((this.x << 5) | (localX & 0x1f), (this.z << 5) | (localZ & 0x1f));
  }

  /**
   * @return The packed {@code long} chunk position for the x and z
   */
  public static long positionToLong(int x, int z) {
    return (z & 0xFFFFFFFFL) | (x & 0xFFFFFFFFL) << 32;
  }

  /**
   * @return The {@code X} component of the packed long position
   */
  public static int longPositionX(long position) {
    return (int) (position >>> 32);
  }

  /**
   * @return The {@code Z} component of the packed long position
   */
  public static int longPositionZ(long position) {
    return (int) position;
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

  /**
   * @deprecated Use {@link ChunkPosition#getRegionPosition()}. Remove in 2.6.
   */
  @Deprecated
  public ChunkPosition regionPosition() {
    return getRegionPosition();
  }

  /**
   * @deprecated Remove in 2.6.
   */
  @Deprecated
  public int getInt() {
    return (x << 16) | (0xFFFF & z);
  }

  /**
   * @deprecated Use {@link ChunkPosition#ChunkPosition(int, int)}. Remove in 2.6.
   */
  @Deprecated
  public static ChunkPosition get(long longValue) {
    return new ChunkPosition(longValue);
  }

  /**
   * @deprecated Use {@link ChunkPosition#ChunkPosition(long)}. Remove in 2.6.
   */
  @Deprecated
  public static ChunkPosition get(int x, int z) {
    return new ChunkPosition(x, z);
  }
}
