package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class BigDripleafStemModel {

  private static final Quad[] quadsNorth =
      Model.join(
          Model.rotateY(new Quad[]{
                  new Quad(
                      new Vector3(5 / 16.0, 16 / 16.0, 12 / 16.0),
                      new Vector3(11 / 16.0, 16 / 16.0, 12 / 16.0),
                      new Vector3(5 / 16.0, 0 / 16.0, 12 / 16.0),
                      new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
                  ),
                  new Quad(
                      new Vector3(11 / 16.0, 16 / 16.0, 12 / 16.0),
                      new Vector3(5 / 16.0, 16 / 16.0, 12 / 16.0),
                      new Vector3(11 / 16.0, 0 / 16.0, 12 / 16.0),
                      new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
                  )
              },
              Math.toRadians(45), new Vector3(0.5, 0, 12 / 16.0)), // TODO rescale
          Model.rotateY(new Quad[]{
                  new Quad(
                      new Vector3(5 / 16.0, 16 / 16.0, 12 / 16.0),
                      new Vector3(11 / 16.0, 16 / 16.0, 12 / 16.0),
                      new Vector3(5 / 16.0, 0 / 16.0, 12 / 16.0),
                      new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
                  ),
                  new Quad(
                      new Vector3(11 / 16.0, 16 / 16.0, 12 / 16.0),
                      new Vector3(5 / 16.0, 16 / 16.0, 12 / 16.0),
                      new Vector3(11 / 16.0, 0 / 16.0, 12 / 16.0),
                      new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
                  )
              },
              Math.toRadians(-45), new Vector3(0.5, 0, 12 / 16.0)) // TODO rescale
      );

  private static final Quad[][] orientedQuads = new Quad[4][];

  static {
    orientedQuads[0] = quadsNorth;
    orientedQuads[1] = Model.rotateY(orientedQuads[0]);
    orientedQuads[2] = Model.rotateY(orientedQuads[1]);
    orientedQuads[3] = Model.rotateY(orientedQuads[2]);
  }

  public static boolean intersect(Ray ray, String facing) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;

    for (Quad quad : orientedQuads[getOrientationIndex(facing)]) {
      if (quad.intersect(ray)) {
        float[] color = Texture.bigDripleafStem.getColor(ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          ray.t = ray.tNext;
          ray.setNormal(quad.n);
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

  private static int getOrientationIndex(String facing) {
    switch (facing) {
      case "east":
        return 1;
      case "south":
        return 2;
      case "west":
        return 3;
      case "north":
      default:
        return 0;
    }
  }
}
