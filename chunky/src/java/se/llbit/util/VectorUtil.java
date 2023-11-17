package se.llbit.util;

import se.llbit.math.Vector3;

public class VectorUtil {
  public static Vector3 orientNormal(Vector3 direction, Vector3 normal) {
    if(direction.dot(normal) > 0) {
      normal.set(-normal.x, -normal.y, -normal.z);
    }
    return normal;
  }
}
