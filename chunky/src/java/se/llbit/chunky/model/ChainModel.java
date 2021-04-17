package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class ChainModel extends QuadModel {
  private static final Quad[] quadsY =
      Model.rotateY(
          new Quad[] {
            new Quad(
                new Vector3(6.5 / 16.0, 16 / 16.0, 8 / 16.0),
                new Vector3(9.5 / 16.0, 16 / 16.0, 8 / 16.0),
                new Vector3(6.5 / 16.0, 0 / 16.0, 8 / 16.0),
                new Vector4(0 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)),
            new Quad(
                new Vector3(9.5 / 16.0, 16 / 16.0, 8 / 16.0),
                new Vector3(6.5 / 16.0, 16 / 16.0, 8 / 16.0),
                new Vector3(9.5 / 16.0, 0 / 16.0, 8 / 16.0),
                new Vector4(3 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)),
            new Quad(
                new Vector3(8 / 16.0, 16 / 16.0, 9.5 / 16.0),
                new Vector3(8 / 16.0, 16 / 16.0, 6.5 / 16.0),
                new Vector3(8 / 16.0, 0 / 16.0, 9.5 / 16.0),
                new Vector4(3 / 16.0, 6 / 16.0, 16 / 16.0, 0 / 16.0)),
            new Quad(
                new Vector3(8 / 16.0, 16 / 16.0, 6.5 / 16.0),
                new Vector3(8 / 16.0, 16 / 16.0, 9.5 / 16.0),
                new Vector3(8 / 16.0, 0 / 16.0, 6.5 / 16.0),
                new Vector4(6 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0))
          },
          Math.toRadians(45));

  private static final Texture[] textures = {
      Texture.chain, Texture.chain, Texture.chain, Texture.chain
  };

  private final Quad[] quads;

  public ChainModel(String axisName) {
    switch (axisName) {
      default:
      case "y":
        quads = quadsY;
        break;
      case "x":
        quads = Model.rotateZ(quadsY);
        break;
      case "z":
        quads = Model.rotateX(quadsY);
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
