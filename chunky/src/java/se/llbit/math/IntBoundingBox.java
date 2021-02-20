/* Copyright (c) 2021 Chunky contributors
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
package se.llbit.math;

import org.apache.commons.math3.util.FastMath;

/**
 * Integer bounding box for objects in a 2d grid at integer coordinates.
 * <p>
 * Used in ChunkPosition#chunkBounds for Octree bounds calculations.
 */
public class IntBoundingBox {
  public int xmin = Integer.MAX_VALUE;
  public int xmax = Integer.MIN_VALUE;
  public int zmin = Integer.MAX_VALUE;
  public int zmax = Integer.MIN_VALUE;

  public IntBoundingBox include(int newX, int newZ) {
    xmin = FastMath.min(xmin, newX);
    xmax = FastMath.max(xmax, newX);
    zmin = FastMath.min(zmin, newZ);
    zmax = FastMath.max(zmax, newZ);
    return this;
  }

  public IntBoundingBox includeX(int newX) {
    xmin = FastMath.min(xmin, newX);
    xmax = FastMath.max(xmax, newX);
    return this;
  }

  public IntBoundingBox includeZ(int newZ) {
    zmin = FastMath.min(zmin, newZ);
    zmax = FastMath.max(zmax, newZ);
    return this;
  }

  public IntBoundingBox addMax(int delta) {
    if (xmax != Integer.MIN_VALUE) { xmax += delta; }
    if (zmax != Integer.MIN_VALUE) { zmax += delta; }
    return this;
  }

  public IntBoundingBox multiply(int scale) {
    if (xmin != Integer.MAX_VALUE) { xmin *= scale; }
    if (xmax != Integer.MIN_VALUE) { xmax *= scale; }
    if (zmin != Integer.MAX_VALUE) { zmin *= scale; }
    if (zmax != Integer.MIN_VALUE) { zmax *= scale; }
    return this;
  }

  public int maxDimension() {
    return FastMath.max(widthX(), widthZ());
  }

  public int widthX() {
    if (xmin == Integer.MAX_VALUE && xmax == Integer.MIN_VALUE) {
      return 0;
    }
    return xmax - xmin;
  }

  public int widthZ() {
    if (zmin == Integer.MAX_VALUE && zmax == Integer.MIN_VALUE) {
      return 0;
    }
    return zmax - zmin;
  }

  public int midpointX() {
    if (xmin == Integer.MAX_VALUE && xmax == Integer.MIN_VALUE) {
      return 0;
    }
    return (xmax + xmin) / 2;
  }

  public int midpointZ() {
    if (zmin == Integer.MAX_VALUE && zmax == Integer.MIN_VALUE) {
      return 0;
    }
    return (zmax + zmin) / 2;
  }
}
