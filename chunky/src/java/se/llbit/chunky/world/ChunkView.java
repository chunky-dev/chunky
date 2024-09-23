/* Copyright (c) 2010-2016 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.math.QuickMath;

/**
 * Abstract representation of a view over a map of chunks.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChunkView {
  /**
   * Minimum block scale for the map view.
   */
  public static final int BLOCK_SCALE_MIN = 1;

  /**
   * Maximum block scale for the map view.
   */
  public static final int BLOCK_SCALE_MAX = 8 * 16;

  /**
   * Default block scale for the map view.
   */
  public static final int DEFAULT_BLOCK_SCALE = 4 * 16;

  /**
   * A zero-size chunk view useful as a default chunk view for an uninitialized map.
   */
  public static final ChunkView EMPTY = new ChunkView(0, 0, 0, 0, DEFAULT_BLOCK_SCALE, 0, 0) {
    @Override public boolean isChunkVisible(int x, int z) {
      return false;
    }

    @Override public boolean isRegionVisible(int x, int z) {
      return false;
    }
  };

  // Center position.
  public final double x;
  public final double z;

  // Visible chunks.
  public final double x0;
  public final double z0;
  public final double x1;
  public final double z1;

  // Rendered chunks.
  public final int cx0;
  public final int cz0;
  public final int cx1;
  public final int cz1;

  // Preloaded chunks.
  public final int px0;
  public final int pz0;
  public final int px1;
  public final int pz1;

  // Rendered regions.
  public final int prx0;
  public final int prz0;
  public final int prx1;
  public final int prz1;

  public final int width;
  public final int height;

  /** The number of pixels per chunk in this view. */
  public final int scale;

  /** The pixel width for rendering a chunk in this view. */
  public final int chunkScale;

  /** The y level for rendering a chunk in this view. */
  public final int yMin;
  public final int yMax;

  /** Copies an existing chunk view. */
  public ChunkView(ChunkView other) {
    this(other.x, other.z, other.width, other.height, other.scale, other.yMin, other.yMax);
  }

  public ChunkView(double x, double z, int width, int height, int scale, int yMin, int yMax) {
    scale = clampScale(scale);
    this.scale = scale;
    if (this.scale <= 12) {
      chunkScale = 1;
    } else {
      chunkScale = 16;
    }
    this.x = x;
    this.z = z;
    double cw = width / (2.0 * this.scale);
    double ch = height / (2.0 * this.scale);
    this.x0 = x - cw;
    this.x1 = x + cw;
    this.z0 = z - ch;
    this.z1 = z + ch;
    this.width = width;
    this.height = height;
    this.yMin = yMin;
    this.yMax = yMax;
    // Visible chunks (integer coordinates):
    cx0 = (int) QuickMath.floor(x0);
    cx1 = (int) QuickMath.floor(x1);
    cz0 = (int) QuickMath.floor(z0);
    cz1 = (int) QuickMath.floor(z1);
    if (this.scale >= 16) {
      px0 = cx0 - 1;
      px1 = cx1 + 1;
      pz0 = cz0 - 1;
      pz1 = cz1 + 1;
      prx0 = px0 >> 5;
      prx1 = px1 >> 5;
      prz0 = pz0 >> 5;
      prz1 = pz1 >> 5;
    } else {
      // Visible regions.
      int irx0 = cx0 >> 5;
      int irx1 = cx1 >> 5;
      int irz0 = cz0 >> 5;
      int irz1 = cz1 >> 5;
      prx0 = irx0;
      prx1 = irx1;
      prz0 = irz0;
      prz1 = irz1;
      px0 = prx0 << 5;
      px1 = (prx1 << 5) + 31;
      pz0 = prz0 << 5;
      pz1 = (prz1 << 5) + 31;
    }
  }

  public boolean shouldPreload(Chunk chunk) {
    if (chunk.isEmpty()) {
      return false;
    }
    ChunkPosition pos = chunk.getPosition();
    return isChunkVisible(pos.x, pos.z);
  }

  @Override public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof ChunkView) {
      ChunkView other = (ChunkView) obj;
      return scale == other.scale
          && px0 == other.px0
          && px1 == other.px1
          && pz0 == other.pz0
          && pz1 == other.pz1;
    }
    return false;
  }

  public boolean isVisible(ChunkPosition pos) {
    return shouldPreload(pos);
  }


  /**
   * Determines if a chunk or region is visible based on the view scale.
   * If the scale is greater than or equal to 16 then the test is for chunk visibility,
   * otherwise region visibility is checked.
   */
  public boolean shouldPreload(ChunkPosition pos) {
    return chunkScale >= 16 ? isChunkVisible(pos.x, pos.z) : isRegionVisible(pos.x, pos.z);
  }

  public boolean isChunkVisible(ChunkPosition chunk) {
    return isChunkVisible(chunk.x, chunk.z);
  }

  public boolean isChunkVisible(int x, int z) {
    return px0 <= x && px1 >= x && pz0 <= z && pz1 >= z;
  }

  public boolean isRegionVisible(RegionPosition pos) {
    return isRegionVisible(pos.x, pos.z);
  }

  public boolean isRegionVisible(int x, int z) {
    return prx0 <= x && prx1 >= x && prz0 <= z && prz1 >= z;
  }

  @Override public String toString() {
    return String.format("[(%d, %d), (%d, %d)]", px0, pz0, px1, pz1);
  }

  /**
   * Clamp the block scale to the minimum / maximum values if it is outside
   * the valid value range.
   * @return clamped scale value
   */
  public static int clampScale(int scale) {
    return Math.max(BLOCK_SCALE_MIN, Math.min(BLOCK_SCALE_MAX, scale));
  }
}
