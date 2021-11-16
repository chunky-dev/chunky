package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class StonecutterModel extends QuadModel {

  private static final Texture bottom = Texture.stonecutterBottom;
  private static final Texture top = Texture.stonecutterTop;
  private static final Texture side = Texture.stonecutterSide;
  private static final Texture saw = Texture.stonecutterSaw;
  private static final Texture[] textures = new Texture[]{
      top, bottom, side, side, side, side, saw, saw
  };

  //region Model
  private static final Quad[] quadsNorth = new Quad[]{
      new Quad(
          new Vector3(0 / 16.0, 9 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 9 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 9 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      ),

      new Quad(
          new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 9 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 9 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 9 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 9 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 9 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 9 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 9 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 9 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 9 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 9 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 9 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 9 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(1 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(15 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(1 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector4(15 / 16.0, 1 / 16.0, 7 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(15 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(1 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(15 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector4(1 / 16.0, 15 / 16.0, 7 / 16.0, 0 / 16.0)
      )
  };
  //endregion

  static final Quad[][] orientedQuads = new Quad[4][];

  static {
    orientedQuads[0] = quadsNorth;
    orientedQuads[1] = Model.rotateY(orientedQuads[0]);
    orientedQuads[2] = Model.rotateY(orientedQuads[1]);
    orientedQuads[3] = Model.rotateY(orientedQuads[2]);
  }

  private final Quad[] quads;

  public StonecutterModel(String facing) {
    quads = orientedQuads[getOrientationIndex(facing)];
  }

  private static int getOrientationIndex(String facing) {
    switch (facing) {
      default:
      case "north":
        return 0;
      case "east":
        return 1;
      case "south":
        return 2;
      case "west":
        return 3;
    }
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }
}
