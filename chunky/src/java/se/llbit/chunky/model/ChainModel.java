package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class ChainModel {
  private static final Quad[] quads =
      new Quad[] {
        new Quad(
            new Vector3(6.5 / 16.0, 16 / 16.0, 8 / 16.0),
            new Vector3(9.5 / 16.0, 16 / 16.0, 8 / 16.0),
            new Vector3(6.5 / 16.0, 0 / 16.0, 8 / 16.0),
            new Vector4(3 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)),
        new Quad(
            new Vector3(9.5 / 16.0, 16 / 16.0, 8 / 16.0),
            new Vector3(6.5 / 16.0, 16 / 16.0, 8 / 16.0),
            new Vector3(9.5 / 16.0, 0 / 16.0, 8 / 16.0),
            new Vector4(3 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)),
        new Quad(
            new Vector3(8 / 16.0, 16 / 16.0, 9.5 / 16.0),
            new Vector3(8 / 16.0, 16 / 16.0, 6.5 / 16.0),
            new Vector3(8 / 16.0, 0 / 16.0, 9.5 / 16.0),
            new Vector4(6 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)),
        new Quad(
            new Vector3(8 / 16.0, 16 / 16.0, 6.5 / 16.0),
            new Vector3(8 / 16.0, 16 / 16.0, 9.5 / 16.0),
            new Vector3(8 / 16.0, 0 / 16.0, 6.5 / 16.0),
            new Vector4(6 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0))
      };

  public static boolean intersect(Ray ray) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;

    for (Quad quad : quads) {
      if (quad.intersect(ray)) {
        float[] color = Texture.chain.getColor(ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          ray.t = ray.tNext;
          ray.n.set(quad.n);
          hit = true;
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
