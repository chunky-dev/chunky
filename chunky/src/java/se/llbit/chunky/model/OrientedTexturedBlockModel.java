package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class OrientedTexturedBlockModel extends QuadModel {
  private static final Quad[] side = {
      // north
      new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 0), new Vector3(1, 1, 0),
          new Vector4(0, 1, 0, 1)),

      // south
      new Quad(new Vector3(0, 0, 1), new Vector3(1, 0, 1), new Vector3(0, 1, 1),
          new Vector4(0, 1, 0, 1)),

      // west
      new Quad(new Vector3(0, 0, 0), new Vector3(0, 0, 1), new Vector3(0, 1, 0),
          new Vector4(0, 1, 0, 1)),

      // east
      new Quad(new Vector3(1, 0, 1), new Vector3(1, 0, 0), new Vector3(1, 1, 1),
          new Vector4(0, 1, 0, 1)),

      // top
      new Quad(new Vector3(1, 1, 0), new Vector3(0, 1, 0), new Vector3(1, 1, 1),
          new Vector4(0, 1, 0, 1)),

      // bottom
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1)),
  };

  private final Quad[] quads;
  private final Texture[] textures;

  public OrientedTexturedBlockModel(String facing, Texture north, Texture east, Texture south,
                                    Texture west, Texture top, Texture bottom) {
    switch (facing) {
      case "up":
        quads = Model.rotateX(side);
        break;
      case "down":
        quads = Model.rotateNegX(side);
        break;
      case "north":
        quads = side;
        break;
      case "south":
        quads = Model.rotateY(Model.rotateY(side));
        break;
      case "east":
        quads = Model.rotateY(side, -Math.toRadians(90));
        break;
      case "west":
        quads = Model.rotateNegY(side);
        break;
      default:
        throw new IllegalArgumentException(("Invalid facing: " + facing));
    }
    textures = new Texture[] {
        north, south, west, east, top, bottom
    };
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
