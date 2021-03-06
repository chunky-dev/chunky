/* Copyright (c) 2012-2014 Jesper Öqvist <jesper@llbit.se>
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

/**
 * 4D double vector
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Vector4 {

  public double x, y, z, w;

  /**
   * Create new vector.
   */
  public Vector4() {
    this(0, 0, 0, 0);
  }

  /**
   * Copy constructor.
   */
  public Vector4(Vector4 v) {
    this(v.x, v.y, v.z, v.w);
  }

  public Vector4(double i, double j, double k, double l) {
    x = i;
    y = j;
    z = k;
    w = l;
  }

  /**
   * Set the vector equal to other vector.
   */
  public final void set(Vector4 other) {
    x = other.x;
    y = other.y;
    z = other.z;
    w = other.w;
  }

  /**
   * Set the vector.
   */
  public final void set(double i, double j, double k, double l) {
    x = i;
    y = j;
    z = k;
    w = l;
  }

  /**
   * Scale the vector.
   */
  public void scale(double d) {
    x *= d;
    y *= d;
    z *= d;
    w *= d;
  }

  /**
   * Set the vector.
   */
  public void set(float[] v) {
    x = v[0];
    y = v[1];
    z = v[2];
    w = v[3];
  }

  /**
   * Scale and add argument the vector.
   */
  public void scaleAdd(double s, Vector4 v) {
    x += s * v.x;
    y += s * v.y;
    z += s * v.z;
    w += s * v.w;
  }

  @Override public String toString() {
    return String.format("(%.2f, %.2f, %.2f, %.2f)", x, y, z, w);
  }
}
