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
 * Coordinate transforms.
 *
 * <p>A sequence of transforms can be chained together to form a more complex transform.
 *
 * <p>A sequence of transforms is created by using the NONE transform as base transform,
 * then using .scale(), .translate(), .rotate(), etc., to chain the next transform in
 * the sequence.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Transform {
  /**
   * Used to sequence two separate transformations.
   */
  private static class TransformPair extends Transform {
    private final Transform a;
    private final Transform b;

    protected TransformPair(Transform a, Transform b) {
      this.a = a;
      this.b = b;
    }

    @Override public void apply(Vector3 v) {
      a.apply(v);
      b.apply(v);
    }

    @Override public void applyRotScale(Vector3 v) {
      a.applyRotScale(v);
      b.applyRotScale(v);
    }
  }


  public static final Transform NONE = new Transform();

  private Transform() {
  }

  /**
   * Apply the transformation to a vertex.
   */
  public void apply(Vector3 v) {
  }

  /**
   * Apply only rotation and scaling to a vertex.
   */
  public void applyRotScale(Vector3 v) {
  }

  /**
   * Creates a new transform by chaining another transform after this one.
   */
  public Transform chain(Transform other) {
    if (this == NONE) {
      return other;
    } else {
      return new TransformPair(this, other);
    }
  }

  /**
   * Translate by a vector
   *
   * @param translation The translation vector
   */
  public final Transform translate(final Vector3 translation) {
    return chain(new Transform() {
      @Override public void apply(Vector3 v) {
        v.add(translation);
      }
    });
  }

  /**
   * Translate by a vector
   */
  public final Transform translate(final double x, final double y, final double z) {
    return chain(new Transform() {
      @Override public void apply(Vector3 v) {
        v.x += x;
        v.y += y;
        v.z += z;
      }
    });
  }

  /**
   * Scales all coordinates uniformly.
   */
  public final Transform scale(final double scale) {
    if (scale == 1.0) {
      // No transform.
      return this;
    }
    return chain(new Transform() {
      @Override public void apply(Vector3 v) {
        v.scale(scale);
      }

      @Override public void applyRotScale(Vector3 v) {
        v.scale(scale);
      }
    });
  }

  /**
   * Scales all coordinates.
   */
  public final Transform scale(final double scaleX, final double scaleY, final double scaleZ) {
    return chain(new Transform() {
      @Override
      public void apply(Vector3 v) {
        v.x *= scaleX;
        v.y *= scaleY;
        v.z *= scaleZ;
      }

      @Override
      public void applyRotScale(Vector3 v) {
        v.x *= scaleX;
        v.y *= scaleY;
        v.z *= scaleZ;
      }
    });
  }

  /**
   * Rotation by 90 degrees around the Y axis
   */
  public final Transform rotateY() {
    return chain(new Transform() {
      @Override public void apply(Vector3 o) {
        double tmp = o.x;
        o.x = -o.z;
        o.z = tmp;
      }

      @Override public void applyRotScale(Vector3 o) {
        double tmp = o.x;
        o.x = -o.z;
        o.z = tmp;
      }
    });
  }

  /**
   * Rotation by 90 degrees around the negative Y axis.
   */
  public final Transform rotateNegY() {
    return chain(new Transform() {
      @Override public void apply(Vector3 o) {
        double tmp = o.x;
        o.x = o.z;
        o.z = -tmp;
      }

      @Override public void applyRotScale(Vector3 o) {
        double tmp = o.x;
        o.x = o.z;
        o.z = -tmp;
      }
    });
  }

  /**
   * Rotation by 90 degrees around the X axis
   */
  public final Transform rotateX() {
    return chain(new Transform() {
      @Override public void apply(Vector3 o) {
        double tmp = o.y;
        o.y = -o.z;
        o.z = tmp;
      }

      @Override public void applyRotScale(Vector3 o) {
        double tmp = o.y;
        o.y = -o.z;
        o.z = tmp;
      }
    });
  }

  /**
   * Rotation by 90 degrees around the negative X axis
   */
  public final Transform rotateNegX() {
    return chain(new Transform() {
      @Override public void apply(Vector3 o) {
        double tmp = o.y;
        o.y = o.z;
        o.z = -tmp;
      }

      @Override public void applyRotScale(Vector3 o) {
        double tmp = o.y;
        o.y = o.z;
        o.z = -tmp;
      }
    });
  }

  /**
   * Rotation by 90 degrees around the Z axis
   */
  public final Transform rotateZ() {
    return chain(new Transform() {
      @Override public void apply(Vector3 o) {
        double tmp = o.x;
        o.x = -o.y;
        o.y = tmp;
      }

      @Override public void applyRotScale(Vector3 o) {
        double tmp = o.x;
        o.x = -o.y;
        o.y = tmp;
      }
    });
  }

  /**
   * Rotation by 90 degrees around the negative Z axis
   */
  public final Transform rotateNegZ() {
    return chain(new Transform() {
      @Override public void apply(Vector3 o) {
        double tmp = o.x;
        o.x = o.y;
        o.y = -tmp;
      }

      @Override public void applyRotScale(Vector3 o) {
        double tmp = o.x;
        o.x = o.y;
        o.y = -tmp;
      }
    });
  }

  public Transform rotateQuaternion(Vector4 quaternion) {
    return chain(new Transform() {
      @Override
      public void apply(Vector3 o) {
        Vector4 oAsVec4 = new Vector4(0, o);
        Vector4 quaternionConj = new Vector4(quaternion.x, quaternion.y * -1, quaternion.z * -1, quaternion.w * -1);
        Vector4 result = multiplyQuaternion(multiplyQuaternion(quaternion, oAsVec4), quaternionConj);
        o.x = result.y;
        o.y = result.z;
        o.z = result.w;
      }

      @Override
      public void applyRotScale(Vector3 o) {
        apply(o);
      }

      private Vector4 multiplyQuaternion(Vector4 q, Vector4 r) {
        return new Vector4(
          r.x * q.x - r.y * q.y - r.z * q.z - r.w * q.w,
          r.x * q.y + r.y * q.x - r.z * q.w + r.w * q.z,
          r.x * q.z + r.y * q.w + r.z * q.x - r.w * q.y,
          r.x * q.w - r.y * q.z + r.z * q.y + r.w * q.x
        );
      }
    });
  }

  /**
   * Mirror in Y axis
   */
  public final Transform mirrorY() {
    return chain(new Transform() {
      @Override public void apply(Vector3 o) {
        o.x = -o.x;
        o.y = -o.y;
      }
    });
  }

  /**
   * Mirror in X axis
   */
  public final Transform mirrorX() {
    return chain(new Transform() {
      @Override public void apply(Vector3 o) {
        o.x = -o.x;
        o.z = -o.z;
      }
    });
  }

  /**
   * Rotation around the Y axis
   */
  public final Transform rotateY(final double angle) {
    return chain(new Transform() {
      private final Matrix3 mat = new Matrix3();

      {
        mat.rotY(angle);
      }

      @Override public void apply(Vector3 v) {
        mat.transform(v);
      }

      @Override public void applyRotScale(Vector3 v) {
        mat.transform(v);
      }
    });
  }

  /**
   * Rotation around the X axis
   *
   * @param angle angle in radians
   */
  public final Transform rotateX(final double angle) {
    return chain(new Transform() {
      private final Matrix3 mat = new Matrix3();

      {
        mat.rotX(angle);
      }

      @Override public void apply(Vector3 v) {
        mat.transform(v);
      }

      @Override public void applyRotScale(Vector3 v) {
        mat.transform(v);
      }
    });
  }

  /**
   * Rotation around the Z axis
   *
   * @param angle angle in radians
   */
  public final Transform rotateZ(final double angle) {
    return chain(new Transform() {
      private final Matrix3 mat = new Matrix3();

      {
        mat.rotZ(angle);
      }

      @Override public void apply(Vector3 v) {
        mat.transform(v);
      }

      @Override public void applyRotScale(Vector3 v) {
        mat.transform(v);
      }
    });
  }
}
