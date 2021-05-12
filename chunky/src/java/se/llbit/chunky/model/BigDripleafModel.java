package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class BigDripleafModel {

  //#region big_dripleaf
  private static final Quad[] bigDripleafNorth = Model.join(
      new Quad[]{
          // top
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          // tip
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 15 / 16.0, 0.002 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 0.002 / 16.0),
              new Vector3(16 / 16.0, 11 / 16.0, 0.002 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          // side
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 11 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(0.002 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0.002 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(0.002 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(15.998 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(15.998 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(15.998 / 16.0, 11 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 12 / 16.0)
          )
      },
      // stem
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          )}, Math.toRadians(45), new Vector3(0.5, 0, 12.0 / 16.0)), // TODO rescale
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          )}, Math.toRadians(-45), new Vector3(0.5, 0, 12.0 / 16.0)) // TODO rescale
  );
  //#endregion

  //#region big_dripleaf_partial_tilt
  private static final Quad[] bigDripleafPartialTiltNorth = Model.join(
      Model.rotateX(new Quad[]{
          // top
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          // top
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          // side
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 11 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(0.002 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0.002 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(0.002 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(15.998 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(15.998 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(15.998 / 16.0, 11 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 12 / 16.0)
          )
      }, Math.toRadians(-22.5), new Vector3(0.5, 15.0 / 16.0, 1)),
      // stem
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          )}, Math.toRadians(45), new Vector3(0.5, 0.5, 12.0 / 16.0)), // TODO rescale
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          )}, Math.toRadians(-45), new Vector3(0.5, 0.5, 12.0 / 16.0)) // TODO rescale
  );
  //#endregion

  //#region big_dripleaf_full_tilt
  private static final Quad[] bigDripleafFullTiltNorth = Model.join(
      Model.rotateX(new Quad[]{
          // top
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          // tip
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          // side
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 11 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(0.002 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0.002 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(0.002 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(15.998 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(15.998 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(15.998 / 16.0, 11 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 12 / 16.0)
          )
      }, Math.toRadians(-45), new Vector3(8 / 16.0, 15 / 16.0, 16 / 16.0)),
      // stem
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          )}, Math.toRadians(45), new Vector3(0.5, 0.5, 12.0 / 16.0)), // TODO rescale
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          )}, Math.toRadians(-45), new Vector3(0.5, 0.5, 12.0 / 16.0)) // TODO rescale
  );
  //#endregion

  private static final Quad[][][] orientedVariantQuads = new Quad[3][4][];

  private static final Texture[] textures;

  static {
    orientedVariantQuads[0] = Model.rotateYNESW(bigDripleafNorth);
    orientedVariantQuads[1] = Model.rotateYNESW(bigDripleafPartialTiltNorth);
    orientedVariantQuads[2] = Model.rotateYNESW(bigDripleafFullTiltNorth);

    Texture top = Texture.bigDripleafTop;
    Texture tip = Texture.bigDripleafTip;
    Texture side = Texture.bigDripleafSide;
    Texture stem = Texture.bigDripleafStem;
    textures = new Texture[]{top, top, tip, tip, side, side, side, side, stem, stem, stem, stem};
  }

  public static boolean intersect(Ray ray, String facing, String tilt) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;

    Quad[] quads = orientedVariantQuads[getModelIndex(tilt)][getOrientationIndex(facing)];

    for (int i = 0; i < quads.length; i++) {
      Quad quad = quads[i];
      if (quad.intersect(ray)) {
        float[] color = textures[i].getColor(ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          ray.t = ray.tNext;
          ray.setN(quad.n);
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

  private static int getModelIndex(String tilt) {
    switch (tilt) {
      case "partial":
        return 1;
      case "full":
        return 2;
      case "none":
      case "unstable":
      default:
        return 0;
    }
  }
}
