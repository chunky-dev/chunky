package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class LightningRodModel {

  private static final Quad[] quadsUp = new Quad[]{
      new Quad(
          new Vector3(6 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(10 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(6 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(6 / 16.0, 12 / 16.0, 6 / 16.0),
          new Vector3(10 / 16.0, 12 / 16.0, 6 / 16.0),
          new Vector3(6 / 16.0, 12 / 16.0, 10 / 16.0),
          new Vector4(0 / 16.0, 4 / 16.0, 12 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(6 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(6 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector3(6 / 16.0, 12 / 16.0, 10 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(10 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector3(10 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(10 / 16.0, 12 / 16.0, 6 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(6 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector3(10 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector3(6 / 16.0, 12 / 16.0, 6 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(10 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(6 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(10 / 16.0, 12 / 16.0, 10 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
          new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(7 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
          new Vector4(2 / 16.0, 0 / 16.0, 12 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(9 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector4(2 / 16.0, 0 / 16.0, 12 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(7 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector4(2 / 16.0, 0 / 16.0, 12 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(9 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 0 / 16.0, 9 / 16.0),
          new Vector4(2 / 16.0, 0 / 16.0, 12 / 16.0, 0 / 16.0)
      )
  };

  static final Quad[][] orientedQuads = new Quad[6][];

  static {
    orientedQuads[4] = quadsUp;
    orientedQuads[0] = Model.rotateX(Model.rotateX(quadsUp));
    orientedQuads[2] = Model.rotateNegX(quadsUp);
    orientedQuads[1] = Model.rotateY(orientedQuads[2]);
    orientedQuads[3] = Model.rotateY(orientedQuads[1]);
    orientedQuads[5] = Model.rotateY(orientedQuads[3]);
  }

  public static boolean intersect(Ray ray, String facing, boolean powered) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    Texture texture = powered ? Texture.lightningRodOn : Texture.lightningRod;

    for (Quad quad : orientedQuads[getOrientationIndex(facing)]) {
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

  private static int getOrientationIndex(String facing) {
    switch (facing) {
      case "down":
        return 0;
      case "east":
        return 1;
      case "north":
        return 2;
      case "south":
        return 3;
      case "up":
        return 4;
      case "west":
        return 5;
      default:
        return 4;
    }
  }
}
