package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class AzaleaModel {

  private static final Quad[] quads = Model.join(
      new Quad[]{
          // top
          new Quad(
              new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          // side
          new Quad(
              new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 5 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 6 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 5 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 6 / 16.0)
          ),
          new Quad(
              new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 5 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 6 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 5 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 6 / 16.0)
          ),
          new Quad(
              new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 5 / 16.0, 16 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 6 / 16.0)
          ),
          new Quad(
              new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 5 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 6 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 5 / 16.0, 16 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 6 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 5 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 6 / 16.0)
          )
      },
      // plant
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(0.8 / 16.0, 15.8 / 16.0, 8 / 16.0),
              new Vector3(15.2 / 16.0, 15.8 / 16.0, 8 / 16.0),
              new Vector3(0.8 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(15.2 / 16.0, 15.8 / 16.0, 8 / 16.0),
              new Vector3(0.8 / 16.0, 15.8 / 16.0, 8 / 16.0),
              new Vector3(15.2 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
          )
      }, Math.toRadians(45)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(8 / 16.0, 15.8 / 16.0, 15.2 / 16.0),
              new Vector3(8 / 16.0, 15.8 / 16.0, 0.8 / 16.0),
              new Vector3(8 / 16.0, 0 / 16.0, 15.2 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 15.8 / 16.0, 0.8 / 16.0),
              new Vector3(8 / 16.0, 15.8 / 16.0, 15.2 / 16.0),
              new Vector3(8 / 16.0, 0 / 16.0, 0.8 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
          )
      }, Math.toRadians(45))
  );

  public static boolean intersect(Ray ray, Texture top, Texture side) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;

    for (int i = 0; i < 2; i++) {
      Quad quad = quads[i];
      if (quad.intersect(ray)) {
        float[] color = top.getColor(ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          ray.t = ray.tNext;
          ray.n.set(quad.n);
          hit = true;
        }
      }
    }

    for (int i = 2; i < 10; i++) {
      Quad quad = quads[i];
      if (quad.intersect(ray)) {
        float[] color = side.getColor(ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          ray.t = ray.tNext;
          ray.n.set(quad.n);
          hit = true;
        }
      }
    }

    for (int i = 10; i < 14; i++) {
      Quad quad = quads[i];
      if (quad.intersect(ray)) {
        float[] color = Texture.azaleaPlant.getColor(ray.u, ray.v);
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
