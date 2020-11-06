package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class CandleModel {

  private static final Quad[] quads = Model.join(
      new Quad[]{
          new Quad(
              new Vector3(7 / 16.0, 6 / 16.0, 9 / 16.0),
              new Vector3(9 / 16.0, 6 / 16.0, 9 / 16.0),
              new Vector3(7 / 16.0, 6 / 16.0, 7 / 16.0),
              new Vector4(0 / 16.0, 2 / 16.0, 8 / 16.0, 10 / 16.0)
          ),
          new Quad(
              new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
              new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
              new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
              new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 2 / 16.0)
          ),
          new Quad(
              new Vector3(7 / 16.0, 6 / 16.0, 9 / 16.0),
              new Vector3(7 / 16.0, 6 / 16.0, 7 / 16.0),
              new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
              new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
          ),
          new Quad(
              new Vector3(9 / 16.0, 6 / 16.0, 7 / 16.0),
              new Vector3(9 / 16.0, 6 / 16.0, 9 / 16.0),
              new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
              new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
          ),
          new Quad(
              new Vector3(7 / 16.0, 6 / 16.0, 7 / 16.0),
              new Vector3(9 / 16.0, 6 / 16.0, 7 / 16.0),
              new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
              new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
          ),
          new Quad(
              new Vector3(9 / 16.0, 6 / 16.0, 9 / 16.0),
              new Vector3(7 / 16.0, 6 / 16.0, 9 / 16.0),
              new Vector3(9 / 16.0, 0 / 16.0, 9 / 16.0),
              new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
          ),
      },
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(7.5 / 16.0, 7 / 16.0, 8 / 16.0),
              new Vector3(8.5 / 16.0, 7 / 16.0, 8 / 16.0),
              new Vector3(7.5 / 16.0, 6 / 16.0, 8 / 16.0),
              new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
          ),
          new Quad(
              new Vector3(8.5 / 16.0, 7 / 16.0, 8 / 16.0),
              new Vector3(7.5 / 16.0, 7 / 16.0, 8 / 16.0),
              new Vector3(8.5 / 16.0, 6 / 16.0, 8 / 16.0),
              new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
          )
      }, Math.toRadians(45)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(7.5 / 16.0, 7 / 16.0, 8 / 16.0),
              new Vector3(8.5 / 16.0, 7 / 16.0, 8 / 16.0),
              new Vector3(7.5 / 16.0, 6 / 16.0, 8 / 16.0),
              new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
          ),
          new Quad(
              new Vector3(8.5 / 16.0, 7 / 16.0, 8 / 16.0),
              new Vector3(7.5 / 16.0, 7 / 16.0, 8 / 16.0),
              new Vector3(8.5 / 16.0, 6 / 16.0, 8 / 16.0),
              new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
          )

      }, Math.toRadians(-45)));


  public static boolean intersect(Ray ray, Texture candle) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;

    for (Quad quad : quads) {
      if (quad.intersect(ray)) {
        float[] color = candle.getColor(ray.u, ray.v);
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
