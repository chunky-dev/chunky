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
 * Reads fixed-width bit fields from a long array.
 */
public class BitBuffer {
  private final long[] data;
  private final int stride;
  private final int mask;
  private final boolean aligned;

  // Current 8-byte position:
  private int offset;

  // Next bit position:
  private int shift = 0;

  /**
   * @param data the data (immutable).
   * @param stride the number of bits to read at a time (1-32).
   * @param aligned whether or not the bits are aligned at 8-byte boundaries or not
   */
  public BitBuffer(long[] data, int stride, boolean aligned) {
    this.data = data;
    this.stride = stride;
    this.aligned = aligned;
    mask = (1 << stride) - 1;
    shift = 0;
  }

  public int read() {
    int res;
    if (shift + stride < 64) {
      res = (int) (data[offset] >>> shift) & mask;
      shift += stride;
      if (aligned && shift + stride > 64) {
        offset += 1;
        shift = 0;
      }
    } else {
      if (shift + stride == 64) {
        res = (int) (data[offset] >>> shift) & mask;
        offset += 1;
        shift = 0;
      } else {
        if (aligned) {
          res = (int) (data[offset] >>> shift) & mask;
          shift += stride;
        } else {
          // High bits:
          int bits = 64 - shift;
          res = (int) (data[offset] >>> shift);
          offset += 1;
          int rem = stride - bits;
          // Low bits:
          res |= ((int) data[offset] & ((1 << rem) - 1)) << bits;
          shift = rem;
        }
      }
    }

    return res;
  }
}
