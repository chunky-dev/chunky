package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class SmallDripleafModel {

  private static final Quad[] topQuadsNorth = Model.join(
      new Quad[]{
          // top
          new Quad(
              new Vector3(8 / 16.0, 2.99 / 16.0, 15 / 16.0),
              new Vector3(15 / 16.0, 2.99 / 16.0, 15 / 16.0),
              new Vector3(8 / 16.0, 2.99 / 16.0, 8 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 8 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 2.99 / 16.0, 8 / 16.0),
              new Vector3(15 / 16.0, 2.99 / 16.0, 8 / 16.0),
              new Vector3(8 / 16.0, 2.99 / 16.0, 15 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 8 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(1 / 16.0, 8 / 16.0, 8 / 16.0),
              new Vector3(8 / 16.0, 8 / 16.0, 8 / 16.0),
              new Vector3(1 / 16.0, 8 / 16.0, 1 / 16.0),
              new Vector4(0 / 16.0, 8 / 16.0, 8 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(1 / 16.0, 8 / 16.0, 1 / 16.0),
              new Vector3(8 / 16.0, 8 / 16.0, 1 / 16.0),
              new Vector3(1 / 16.0, 8 / 16.0, 8 / 16.0),
              new Vector4(0 / 16.0, 8 / 16.0, 16 / 16.0, 8 / 16.0)
          ),
          new Quad(
              new Vector3(1 / 16.0, 14 / 16.0, 15 / 16.0),
              new Vector3(8 / 16.0, 14 / 16.0, 15 / 16.0),
              new Vector3(1 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector4(0 / 16.0, 8 / 16.0, 8 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(1 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(8 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(1 / 16.0, 14 / 16.0, 15 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 8 / 16.0, 16 / 16.0)
          ),
          // side
          new Quad(
              new Vector3(8 / 16.0, 3 / 16.0, 15 / 16.0),
              new Vector3(8 / 16.0, 3 / 16.0, 8 / 16.0),
              new Vector3(8 / 16.0, 2 / 16.0, 15 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(15 / 16.0, 3 / 16.0, 8 / 16.0),
              new Vector3(15 / 16.0, 3 / 16.0, 15 / 16.0),
              new Vector3(15 / 16.0, 2 / 16.0, 8 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 3 / 16.0, 8 / 16.0),
              new Vector3(15 / 16.0, 3 / 16.0, 8 / 16.0),
              new Vector3(8 / 16.0, 2 / 16.0, 8 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(15 / 16.0, 3 / 16.0, 15 / 16.0),
              new Vector3(8 / 16.0, 3 / 16.0, 15 / 16.0),
              new Vector3(15 / 16.0, 2 / 16.0, 15 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(1 / 16.0, 8 / 16.0, 8 / 16.0),
              new Vector3(1 / 16.0, 8 / 16.0, 1.01 / 16.0),
              new Vector3(1 / 16.0, 7 / 16.0, 8 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 8 / 16.0, 1.01 / 16.0),
              new Vector3(8 / 16.0, 8 / 16.0, 8 / 16.0),
              new Vector3(8 / 16.0, 7 / 16.0, 1.01 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(1 / 16.0, 8 / 16.0, 1.01 / 16.0),
              new Vector3(8 / 16.0, 8 / 16.0, 1.01 / 16.0),
              new Vector3(1 / 16.0, 7 / 16.0, 1.01 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 8 / 16.0, 8 / 16.0),
              new Vector3(1 / 16.0, 8 / 16.0, 8 / 16.0),
              new Vector3(8 / 16.0, 7 / 16.0, 8 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(1 / 16.0, 14 / 16.0, 15 / 16.0),
              new Vector3(1 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(1 / 16.0, 13 / 16.0, 15 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(8 / 16.0, 14 / 16.0, 15 / 16.0),
              new Vector3(8 / 16.0, 13 / 16.0, 8 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(1 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(8 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(1 / 16.0, 13 / 16.0, 8 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 14 / 16.0, 15 / 16.0),
              new Vector3(1 / 16.0, 14 / 16.0, 15 / 16.0),
              new Vector3(8 / 16.0, 13 / 16.0, 15 / 16.0),
              new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 15 / 16.0)
          )
      },
      // stem
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(4.5 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(11.5 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(4.5 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(11.5 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(4.5 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(11.5 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          )
      }, Math.toRadians(45)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(4.5 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(11.5 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(4.5 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(11.5 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(4.5 / 16.0, 14 / 16.0, 8 / 16.0),
              new Vector3(11.5 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          )
      }, Math.toRadians(-45))
  );

  private static final Quad[] bottomQuadsNorth = Model.join(
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(4.5 / 16.0, 16 / 16.0, 8 / 16.0),
              new Vector3(11.5 / 16.0, 16 / 16.0, 8 / 16.0),
              new Vector3(4.5 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(11.5 / 16.0, 16 / 16.0, 8 / 16.0),
              new Vector3(4.5 / 16.0, 16 / 16.0, 8 / 16.0),
              new Vector3(11.5 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          )}, Math.toRadians(45)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(4.5 / 16.0, 16 / 16.0, 8 / 16.0),
              new Vector3(11.5 / 16.0, 16 / 16.0, 8 / 16.0),
              new Vector3(4.5 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(11.5 / 16.0, 16 / 16.0, 8 / 16.0),
              new Vector3(4.5 / 16.0, 16 / 16.0, 8 / 16.0),
              new Vector3(11.5 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          )
      }, Math.toRadians(-45))
  );

  private static final Quad[][] orientedTopQuads = new Quad[4][];
  private static final Quad[][] orientedBottomQuads = new Quad[4][];

  private static final Texture[] topTextures;
  private static final Texture[] bottomTextures;

  static {
    Texture top = Texture.smallDripleafTop;
    Texture side = Texture.smallDripleafSide;
    Texture stemTop = Texture.smallDripleafStemTop;
    Texture stemBottom = Texture.smallDripleafStemBottom;
    topTextures = new Texture[]{
        top, top, top, top, top, top, side, side, side, side, side, side, side, side, side, side,
        side, side, stemTop, stemTop, stemTop, stemTop
    };
    bottomTextures = new Texture[]{stemBottom, stemBottom, stemBottom, stemBottom};

    orientedTopQuads[0] = topQuadsNorth;
    orientedTopQuads[1] = Model.rotateY(orientedTopQuads[0]);
    orientedTopQuads[2] = Model.rotateY(orientedTopQuads[1]);
    orientedTopQuads[3] = Model.rotateY(orientedTopQuads[2]);
    orientedBottomQuads[0] = bottomQuadsNorth;
    orientedBottomQuads[1] = Model.rotateY(orientedBottomQuads[0]);
    orientedBottomQuads[2] = Model.rotateY(orientedBottomQuads[1]);
    orientedBottomQuads[3] = Model.rotateY(orientedBottomQuads[2]);
  }

  public static boolean intersect(Ray ray, String facing, String half) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;

    Quad[] quads = half.equals("upper")
        ? orientedTopQuads[getOrientationIndex(facing)]
        : orientedBottomQuads[getOrientationIndex(facing)];
    Texture[] tex = half.equals("upper") ? topTextures : bottomTextures;

    for (int i = 0; i < quads.length; i++) {
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
