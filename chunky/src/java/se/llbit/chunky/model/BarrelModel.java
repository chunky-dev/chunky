package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class BarrelModel extends QuadModel {
  private static final Quad[] sides = {
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

  public BarrelModel(String facing, String open) {
    textures = new Texture[] {Texture.barrelSide, Texture.barrelSide, Texture.barrelSide, Texture.barrelSide,
        open.equals("true") ? Texture.barrelOpen : Texture.barrelTop, Texture.barrelBottom};
    switch (facing) {
      case "up":
        quads = Model.rotateY(sides, Math.toRadians(180));
        break;
      case "down":
        quads = Model.rotateY(Model.rotateX(sides, Math.toRadians(180)), Math.toRadians(180));
        break;
      case "north":
        quads = Model.rotateY(Model.rotateX(sides), Math.toRadians(180));
        break;
      case "south":
        quads = Model.rotateX(sides);
        break;
      case "east":
        quads = Model.rotateNegY(Model.rotateX(sides));
        break;
      case "west":
        quads = Model.rotateY(Model.rotateX(sides));
        break;
      default:
        throw new IllegalArgumentException(("Invalid facing: " + facing));
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
