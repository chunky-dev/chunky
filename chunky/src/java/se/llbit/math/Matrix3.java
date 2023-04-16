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
package se.llbit.math;

import org.apache.commons.math3.util.FastMath;

/**
 * A three by three matrix of doubles.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Matrix3 {

  public double m11, m12, m13;
  public double m21, m22, m23;
  public double m31, m32, m33;

  /**
   * Set the matrix to be a rotation matrix for rotation
   * around the X axis.
   */
  public final void rotX(double theta) {
    double cost = FastMath.cos(theta);
    double sint = FastMath.sin(theta);
    m11 = 1;
    m12 = 0;
    m13 = 0;
    m21 = 0;
    m22 = cost;
    m23 = -sint;
    m31 = 0;
    m32 = sint;
    m33 = cost;
  }

  /**
   * Set the matrix to be a rotation matrix for rotation
   * around the Y axis.
   */
  public final void rotY(double theta) {
    double cost = FastMath.cos(theta);
    double sint = FastMath.sin(theta);
    m11 = cost;
    m12 = 0;
    m13 = sint;
    m21 = 0;
    m22 = 1;
    m23 = 0;
    m31 = -sint;
    m32 = 0;
    m33 = cost;
  }

  /**
   * Set the matrix to be a rotation matrix for rotation
   * around the X axis.
   */
  public final void rotZ(double theta) {
    double cost = FastMath.cos(theta);
    double sint = FastMath.sin(theta);
    m11 = cost;
    m12 = -sint;
    m13 = 0;
    m21 = sint;
    m22 = cost;
    m23 = 0;
    m31 = 0;
    m32 = 0;
    m33 = 1;
  }

  /**
   * Set the matrix to be a rotation matrix for rotation
   * around the X, Y and Z axis.
   */
  public final void rotate(double pitch, double yaw, double roll) {
    double cosa = FastMath.cos(roll);
    double sina = FastMath.sin(roll);
    double cosb = FastMath.cos(yaw);
    double sinb = FastMath.sin(yaw);
    double cosc = FastMath.cos(pitch);
    double sinc = FastMath.sin(pitch);
    m11 = cosa * cosb;
    m12 = cosa * sinb * sinc - sina * cosc;
    m13 = cosa * sinb * cosc + sina * sinc;
    m21 = sina * cosb;
    m22 = sina * sinb * sinc + cosa * cosc;
    m23 = sina * sinb * cosc - cosa * sinc;
    m31 = -sinb;
    m32 = cosb * sinc;
    m33 = cosb * cosc;
  }

  /**
   * Transform a vector using this matrix.
   */
  public void transform(Vector3 o) {
    o.set(m11 * o.x + m12 * o.y + m13 * o.z, m21 * o.x + m22 * o.y + m23 * o.z,
        m31 * o.x + m32 * o.y + m33 * o.z);
  }

  /**
   * Transform the vector using this matrix.
   * @return X component of the transformed vector
   */
  public double transformX(Vector3 o) {
    return m11 * o.x + m12 * o.y + m13 * o.z;
  }

  /**
   * Transform the vector using this matrix.
   * @return Y component of the transformed vector
   */
  public double transformY(Vector3 o) {
    return m21 * o.x + m22 * o.y + m23 * o.z;
  }

  /**
   * Transform the vector using this matrix.
   * @return Z component of the transformed vector
   */
  public double transformZ(Vector3 o) {
    return m31 * o.x + m32 * o.y + m33 * o.z;
  }

  /**
   * Set to the identity matrix.
   */
  public final void setIdentity() {
    m11 = m22 = m33 = 1;
    m12 = m13 = m21 = m23 = m31 = m32 = 0;
  }

  /**
   * Set equal to other matrix.
   */
  public final void set(Matrix3 o) {
    m11 = o.m11;
    m12 = o.m12;
    m13 = o.m13;
    m21 = o.m21;
    m22 = o.m22;
    m23 = o.m23;
    m31 = o.m31;
    m32 = o.m32;
    m33 = o.m33;
  }

  /**
   * Multiply with other matrix.
   */
  public final void mul(Matrix3 o) {
    double t11 = m11 * o.m11 + m12 * o.m21 + m13 * o.m31;
    double t12 = m11 * o.m12 + m12 * o.m22 + m13 * o.m32;
    double t13 = m11 * o.m13 + m12 * o.m23 + m13 * o.m33;
    double t21 = m21 * o.m11 + m22 * o.m21 + m23 * o.m31;
    double t22 = m21 * o.m12 + m22 * o.m22 + m23 * o.m32;
    double t23 = m21 * o.m13 + m22 * o.m23 + m23 * o.m33;
    double t31 = m31 * o.m11 + m32 * o.m21 + m33 * o.m31;
    double t32 = m31 * o.m12 + m32 * o.m22 + m33 * o.m32;
    double t33 = m31 * o.m13 + m32 * o.m23 + m33 * o.m33;
    m11 = t11;
    m12 = t12;
    m13 = t13;
    m21 = t21;
    m22 = t22;
    m23 = t23;
    m31 = t31;
    m32 = t32;
    m33 = t33;
  }

  /**
   * Set this matrix to a uniform scaling matrix with the given scale factor.
   */
  public void scale(double scale) {
    m11 = m22 = m33 = scale;
    m12 = m13 = 0;
    m21 = m23 = 0;
    m31 = m32 = 0;
  }
}
