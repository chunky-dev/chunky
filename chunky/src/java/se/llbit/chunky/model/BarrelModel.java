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
      default:
      case "up":
        quads = sides;
        break;
      case "down":
        quads = Model.rotateX(Model.rotateX(sides));
        break;
      case "north":
        quads = Model.rotateX(sides);
        break;
      case "south":
        quads = Model.rotateNegX(sides);
        break;
      case "east":
        quads = Model.rotateX(Model.rotateY(sides));
        break;
      case "west":
        quads = Model.rotateX(Model.rotateNegY(sides));
        break;
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
