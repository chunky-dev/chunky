package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class GlowLichenModel {

  protected static final Quad[] quads = {
      // North
      new DoubleSidedQuad(new Vector3(0, 0, 0.1 / 16), new Vector3(1, 0, 0.1 / 16),
          new Vector3(0, 1, 0.1 / 16), new Vector4(0, 1, 0, 1)),

      // South
      new DoubleSidedQuad(new Vector3(1, 0, 15.9 / 16), new Vector3(0, 0, 15.9 / 16),
          new Vector3(1, 1, 15.9 / 16), new Vector4(1, 0, 0, 1)),

      // East
      new DoubleSidedQuad(new Vector3(15.9 / 16, 0, 0), new Vector3(15.9 / 16, 0, 1),
          new Vector3(15.9 / 16, 1, 0), new Vector4(0, 1, 0, 1)),

      // West
      new DoubleSidedQuad(new Vector3(0.1 / 16, 0, 1), new Vector3(0.1 / 16, 0, 0),
          new Vector3(0.1 / 16, 1, 1), new Vector4(1, 0, 0, 1)),

      // Top
      new DoubleSidedQuad(new Vector3(0, 15.9 / 16, 0), new Vector3(1, 15.9 / 16, 0),
          new Vector3(0, 15.9 / 16, 1), new Vector4(0, 1, 0, 1)),

      // Bottom
      new DoubleSidedQuad(new Vector3(0, 0.1 / 16, 0), new Vector3(1, 0.1 / 16, 0),
          new Vector3(0, 0.1 / 16, 1), new Vector4(0, 1, 0, 1)),
  };

  public static boolean intersect(Ray ray, int connections) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (int i = 0; i < quads.length; ++i) {
      if ((connections & (1 << i)) != 0) {
        Quad quad = quads[i];
        if (quad.intersect(ray)) {
          float[] color = Texture.glowLichen.getColor(ray.u, ray.v);
          if (color[3] > Ray.EPSILON) {
            ray.color.set(color);
            ray.t = ray.tNext;
            ray.n.set(quad.n);
            ray.n.scale(QuickMath.signum(-ray.d.dot(quad.n)));
            hit = true;
          }
        }
      }
    }
    if (hit) {
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }
}
