package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class CakeWithCandleModel {

  private static final Texture bottom = Texture.cakeBottom;
  private static final Texture top = Texture.cakeTop;
  private static final Texture side = Texture.cakeSide;
  private static final Texture[] tex = new Texture[]{
      top, bottom, side, side, side, side,
  };

  private static final Quad[] quads = Model.join(
      new Quad[]{
          new Quad(
              new Vector3(1 / 16.0, 8 / 16.0, 15 / 16.0),
              new Vector3(15 / 16.0, 8 / 16.0, 15 / 16.0),
              new Vector3(1 / 16.0, 8 / 16.0, 1 / 16.0),
              new Vector4(1 / 16.0, 15 / 16.0, 1 - 15 / 16.0, 1 - 1 / 16.0)
          ),
          new Quad(
              new Vector3(1 / 16.0, 0 / 16.0, 1 / 16.0),
              new Vector3(15 / 16.0, 0 / 16.0, 1 / 16.0),
              new Vector3(1 / 16.0, 0 / 16.0, 15 / 16.0),
              new Vector4(1 / 16.0, 15 / 16.0, 15 / 16.0, 1 / 16.0)
          ),
          new Quad(
              new Vector3(1 / 16.0, 8 / 16.0, 15 / 16.0),
              new Vector3(1 / 16.0, 8 / 16.0, 1 / 16.0),
              new Vector3(1 / 16.0, 0 / 16.0, 15 / 16.0),
              new Vector4(15 / 16.0, 1 / 16.0, 8 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(15 / 16.0, 8 / 16.0, 1 / 16.0),
              new Vector3(15 / 16.0, 8 / 16.0, 15 / 16.0),
              new Vector3(15 / 16.0, 0 / 16.0, 1 / 16.0),
              new Vector4(1 / 16.0, 15 / 16.0, 8 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(1 / 16.0, 8 / 16.0, 1 / 16.0),
              new Vector3(15 / 16.0, 8 / 16.0, 1 / 16.0),
              new Vector3(1 / 16.0, 0 / 16.0, 1 / 16.0),
              new Vector4(1 - 15 / 16.0, 1 - 1 / 16.0, 8 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(15 / 16.0, 8 / 16.0, 15 / 16.0),
              new Vector3(1 / 16.0, 8 / 16.0, 15 / 16.0),
              new Vector3(15 / 16.0, 0 / 16.0, 15 / 16.0),
              new Vector4(15 / 16.0, 1 / 16.0, 8 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(7 / 16.0, 14 / 16.0, 9 / 16.0),
              new Vector3(9 / 16.0, 14 / 16.0, 9 / 16.0),
              new Vector3(7 / 16.0, 14 / 16.0, 7 / 16.0),
              new Vector4(0 / 16.0, 2 / 16.0, 8 / 16.0, 10 / 16.0)
          ),
          new Quad(
              new Vector3(7 / 16.0, 8 / 16.0, 7 / 16.0),
              new Vector3(9 / 16.0, 8 / 16.0, 7 / 16.0),
              new Vector3(7 / 16.0, 8 / 16.0, 9 / 16.0),
              new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 2 / 16.0)
          ),
          new Quad(
              new Vector3(7 / 16.0, 14 / 16.0, 9 / 16.0),
              new Vector3(7 / 16.0, 14 / 16.0, 7 / 16.0),
              new Vector3(7 / 16.0, 8 / 16.0, 9 / 16.0),
              new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
          ),
          new Quad(
              new Vector3(9 / 16.0, 14 / 16.0, 7 / 16.0),
              new Vector3(9 / 16.0, 14 / 16.0, 9 / 16.0),
              new Vector3(9 / 16.0, 8 / 16.0, 7 / 16.0),
              new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
          ),
          new Quad(
              new Vector3(7 / 16.0, 14 / 16.0, 7 / 16.0),
              new Vector3(9 / 16.0, 14 / 16.0, 7 / 16.0),
              new Vector3(7 / 16.0, 8 / 16.0, 7 / 16.0),
              new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
          ),
          new Quad(
              new Vector3(9 / 16.0, 14 / 16.0, 9 / 16.0),
              new Vector3(7 / 16.0, 14 / 16.0, 9 / 16.0),
              new Vector3(9 / 16.0, 8 / 16.0, 9 / 16.0),
              new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
          )
      },
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(7.5 / 16.0, 15 / 16.0, 8 / 16.0),
              new Vector3(8.5 / 16.0, 15 / 16.0, 8 / 16.0),
              new Vector3(7.5 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
          ),
          new Quad(
              new Vector3(8.5 / 16.0, 15 / 16.0, 8 / 16.0),
              new Vector3(7.5 / 16.0, 15 / 16.0, 8 / 16.0),
              new Vector3(8.5 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
          )
      }, Math.toRadians(-45)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(7.5 / 16.0, 15 / 16.0, 8 / 16.0),
              new Vector3(8.5 / 16.0, 15 / 16.0, 8 / 16.0),
              new Vector3(7.5 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
          ),
          new Quad(
              new Vector3(8.5 / 16.0, 15 / 16.0, 8 / 16.0),
              new Vector3(7.5 / 16.0, 15 / 16.0, 8 / 16.0),
              new Vector3(8.5 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
          )
      }, Math.toRadians(45)));


  public static boolean intersect(Ray ray, Texture candle, boolean lit) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;

    for (int i = 0; i < quads.length; i++) {
      Quad quad = quads[i];
      Texture texture = i < tex.length ? tex[i] : candle;
      if (quad.intersect(ray)) {
        float[] color = texture.getColor(ray.u, ray.v);
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
