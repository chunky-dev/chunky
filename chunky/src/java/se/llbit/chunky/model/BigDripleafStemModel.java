package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

public class BigDripleafStemModel extends QuadModel {

  //region Big Dripleaf Stem
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
  //endregion

  private static final Texture[] textures = new Texture[quadsNorth.length];
  static { Arrays.fill(textures, Texture.bigDripleafStem); }

  private final Quad[] quads;

  public BigDripleafStemModel(String facing) {
    switch (facing) {
      case "north":
      default:
        quads = quadsNorth;
        break;
      case "east":
        quads = Model.rotateY(quadsNorth, -Math.toRadians(90));
        break;
      case "south":
        quads = Model.rotateY(quadsNorth, -Math.toRadians(180));
        break;
      case "west":
        quads = Model.rotateY(quadsNorth, -Math.toRadians(270));
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
