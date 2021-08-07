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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A chunk position consists of two integer coordinates x and z.
 * <p>
 * The filename of a chunk is uniquely defined by it's position.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class ChunkPosition {

  public int x, z;

  private ChunkPosition(int x, int z) {
    this.x = x;
    this.z = z;
  }

  @Override
  public String toString() {
    return "[" + x + ", " + z + "]";
  }

  public ChunkPosition regionPosition() {
    return get(x >> 5, z >> 5);
  }

  //not using synchronized Long2ReferenceOpenHashMap due to using one lock. ConcurrentHashMap locks on buckets
  private static final Map<Long, ChunkPosition> positions = new ConcurrentHashMap<>();

  public static ChunkPosition get(int x, int z) {
    return positions.computeIfAbsent(positionToLong(x, z), (position) -> new ChunkPosition(x, z));
  }

  public static long positionToLong(int x, int z) {
    return (z & 0xFFFFFFFFL) | (x & 0xFFFFFFFFL) << 32;
  }

  /**
   * @return The .mca name for the region with this position
   */
  public String getMcaName() {
    return String.format("r.%d.%d.mca", x, z);
  }

  /**
   * @return The long representation of the chunk position
   */
  public long getLong() {
    return positionToLong(x, z);
  }

  /**
   * @return Decoded chunk position
   */
  public static ChunkPosition get(long longValue) {
    return get((int) (longValue >>> 32), (int) longValue);
  }

  /**
   * @return Integer representation of chunk position
   */
  public int getInt() {
    return (x << 16) | (0xFFFF & z);
  }

  /**
   * @return The region position of a this chunk position
   */
  public ChunkPosition getRegionPosition() {
    return get(x >> 5, z >> 5);
  }
}
