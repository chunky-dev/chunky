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

import org.apache.commons.math3.util.FastMath;

import se.llbit.json.JsonObject;

/**
 * A 3D vector of doubles.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Vector3 {

  public double x, y, z;

  /**
   * Creates a new vector (0, 0, 0).
   */
  public Vector3() {
    this(0, 0, 0);
  }

  /**
   * Creates a new vector (i, j, k).
   */
  public Vector3(double i, double j, double k) {
    x = i;
    y = j;
    z = k;
  }

  /**
   * Create a new vector equal to the given vector.
   */
  public Vector3(Vector3 o) {
    x = o.x;
    y = o.y;
    z = o.z;
  }

  /**
   * Set this vector equal to other vector.
   */
  public final void set(Vector3 o) {
    x = o.x;
    y = o.y;
    z = o.z;
  }

  /**
   * Set this vector equal to (d, e, f).
   */
  public final void set(double d, double e, double f) {
    x = d;
    y = e;
    z = f;
  }

  /**
   * @return The dot product of this vector and o vector
   */
  public final double dot(Vector3 o) {
    return x * o.x + y * o.y + z * o.z;
  }

  /**
   * Set this vector equal to a-b.
   */
  public final void sub(Vector3 a, Vector3 b) {
    x = a.x - b.x;
    y = a.y - b.y;
    z = a.z - b.z;
  }

  /**
   * @return The length of this vector, squared
   */
  public final double lengthSquared() {
    return x * x + y * y + z * z;
  }

  /**
   * @return Length of this vector
   */
  public final double length() {
    return FastMath.sqrt(lengthSquared());
  }

  /**
   * Set this vector equal to the cross product of a and b.
   */
  public final void cross(Vector3 a, Vector3 b) {
    x = a.y * b.z - a.z * b.y;
    y = a.z * b.x - a.x * b.z;
    z = a.x * b.y - a.y * b.x;
  }

  /**
   * Normalize this vector (scale the vector to unit length)
   */
  public final void normalize() {
    double s = 1 / FastMath.sqrt(lengthSquared());
    x *= s;
    y *= s;
    z *= s;
  }

  /**
   * Set this vector equal to s*d + o.
   */
  public final void scaleAdd(double s, Vector3 d, Vector3 o) {
    x = s * d.x + o.x;
    y = s * d.y + o.y;
    z = s * d.z + o.z;
  }

  /**
   * Add s*d to this vector.
   */
  public final void scaleAdd(double s, Vector3 d) {
    x += s * d.x;
    y += s * d.y;
    z += s * d.z;
  }

  /**
   * Scale this vector by s.
   */
  public final void scale(double s) {
    x *= s;
    y *= s;
    z *= s;
  }

  /**
   * Set this vector equal to a+b.
   */
  public final void add(Vector3 a, Vector3 b) {
    x = a.x + b.x;
    y = a.y + b.y;
    z = a.z + b.z;
  }

  /**
   * Add a to this vector.
   */
  public final void add(Vector3 a) {
    x += a.x;
    y += a.y;
    z += a.z;
  }

  /**
   * Add a to this vector.
   */
  public final void add(Vector3i a) {
    x += a.x;
    y += a.y;
    z += a.z;
  }

  /**
   * Add vector (a, b, c) to this vector.
   */
  public final void add(double a, double b, double c) {
    x += a;
    y += b;
    z += c;
  }

  /**
   * Subtract a from this vector.
   */
  public final void sub(Vector3 a) {
    x -= a.x;
    y -= a.y;
    z -= a.z;
  }

  /**
   * Subtract vector (a, b, c) from this vector.
   */
  public final void sub(double a, double b, double c) {
    x -= a;
    y -= b;
    z -= c;
  }

  /**
   * Subtract a from this vector.
   */
  public final void sub(Vector3i a) {
    x -= a.x;
    y -= a.y;
    z -= a.z;
  }

  /**
   * Set this vector equal to a.
   */
  public void set(Vector3i a) {
    x = a.x;
    y = a.y;
    z = a.z;
  }

  @Override public String toString() {
    return String.format("(%.2f, %.2f, %.2f)", x, y, z);
  }

  /**
   * Unmarshals a vector from JSON.
   */
  public void fromJson(JsonObject object) {
    x = object.get("x").doubleValue(0);
    y = object.get("y").doubleValue(0);
    z = object.get("z").doubleValue(0);
  }

  /**
   * Serialize to JSON
   *
   * @return JSON object
   */
  public JsonObject toJson() {
    JsonObject object = new JsonObject();
    object.add("x", x);
    object.add("y", y);
    object.add("z", z);
    return object;
  }
}
