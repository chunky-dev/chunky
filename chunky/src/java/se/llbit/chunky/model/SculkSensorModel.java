package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class SculkSensorModel {

  private static final Texture bottom = Texture.sculkSensorBottom;
  private static final Texture side = Texture.sculkSensorSide;
  private static final Texture top = Texture.sculkSensorTop;
  private static final Texture[] tex = new Texture[]{
      top, bottom, side, side, side, side
  };

  private static final Quad[] quads = Model.join(
      new Quad[]{
          new Quad(
              new Vector3(0 / 16.0, 8 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 8 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 8 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(0 / 16.0, 8 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 8 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 8 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 8 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 8 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 8 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(0 / 16.0, 8 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 8 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 8 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 8 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 8 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 8 / 16.0, 0 / 16.0)
          )
      },
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(-1 / 16.0, 16 / 16.0, 3 / 16.0),
              new Vector3(7 / 16.0, 16 / 16.0, 3 / 16.0),
              new Vector3(-1 / 16.0, 8 / 16.0, 3 / 16.0),
              new Vector4(12 / 16.0, 4 / 16.0, 8 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(7 / 16.0, 16 / 16.0, 3 / 16.0),
              new Vector3(-1 / 16.0, 16 / 16.0, 3 / 16.0),
              new Vector3(7 / 16.0, 8 / 16.0, 3 / 16.0),
              new Vector4(4 / 16.0, 12 / 16.0, 8 / 16.0, 0 / 16.0)
          ),
      }, Math.toRadians(45), new Vector3(3 / 16.0 - 0.5, 0, 3 / 16.0 - 0.5)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(9 / 16.0, 16 / 16.0, 3 / 16.0),
              new Vector3(17 / 16.0, 16 / 16.0, 3 / 16.0),
              new Vector3(9 / 16.0, 8 / 16.0, 3 / 16.0),
              new Vector4(4 / 16.0, 12 / 16.0, 8 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(17 / 16.0, 16 / 16.0, 3 / 16.0),
              new Vector3(9 / 16.0, 16 / 16.0, 3 / 16.0),
              new Vector3(17 / 16.0, 8 / 16.0, 3 / 16.0),
              new Vector4(12 / 16.0, 4 / 16.0, 8 / 16.0, 0 / 16.0)
          ),
      }, Math.toRadians(-45), new Vector3(13 / 16.0 - 0.5, 0, 3 / 16.0 - 0.5)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(9 / 16.0, 16 / 16.0, 13 / 16.0),
              new Vector3(17 / 16.0, 16 / 16.0, 13 / 16.0),
              new Vector3(9 / 16.0, 8 / 16.0, 13 / 16.0),
              new Vector4(4 / 16.0, 12 / 16.0, 8 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(17 / 16.0, 16 / 16.0, 13 / 16.0),
              new Vector3(9 / 16.0, 16 / 16.0, 13 / 16.0),
              new Vector3(17 / 16.0, 8 / 16.0, 13 / 16.0),
              new Vector4(12 / 16.0, 4 / 16.0, 8 / 16.0, 0 / 16.0)
          ),
      }, Math.toRadians(45), new Vector3(13 / 16.0 - 0.5, 0, 13 / 16.0 - 0.5)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(-1 / 16.0, 16 / 16.0, 13 / 16.0),
              new Vector3(7 / 16.0, 16 / 16.0, 13 / 16.0),
              new Vector3(-1 / 16.0, 8 / 16.0, 13 / 16.0),
              new Vector4(12 / 16.0, 4 / 16.0, 8 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(7 / 16.0, 16 / 16.0, 13 / 16.0),
              new Vector3(-1 / 16.0, 16 / 16.0, 13 / 16.0),
              new Vector3(7 / 16.0, 8 / 16.0, 13 / 16.0),
              new Vector4(4 / 16.0, 12 / 16.0, 8 / 16.0, 0 / 16.0)
          )
      }, Math.toRadians(-45), new Vector3(3 / 16.0 - 0.5, 0, 13 / 16.0 - 0.5))
  );

  public static boolean intersect(Ray ray, boolean active) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;

    for (int i = 0; i < 6; i++) {
      Quad quad = quads[i];
      if (quad.intersect(ray)) {
        float[] color = tex[i].getColor(ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          ray.t = ray.tNext;
          ray.n.set(quad.n);
          hit = true;
        }
      }
    }

    Texture tendril =
        active ? Texture.sculkSensorTendrilActive : Texture.sculkSensorTendrilInactive;
    for (int i = 6; i < quads.length; i++) {
      Quad quad = quads[i];
      if (quad.intersect(ray)) {
        float[] color = tendril.getColor(ray.u, ray.v);
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
