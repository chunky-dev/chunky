package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texturepack.BedTexture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class BedHeadModel extends QuadModel {
  private static final Quad[] quadsNorth = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 9 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 9 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 9 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 3 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 3 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 3 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 9 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 9 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 3 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 9 / 16.0, 3 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 9 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 9 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 3 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 9 / 16.0, 3 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 9 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 9 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 3 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 9 / 16.0, 3 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(3 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 3 / 16.0),
      new Vector4(6 / 16.0, 9 / 16.0, 0 / 16.0, 3 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 3 / 16.0, 3 / 16.0),
      new Vector3(0 / 16.0, 3 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 3 / 16.0),
      new Vector4(3 / 16.0, 0 / 16.0, 3 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(3 / 16.0, 3 / 16.0, 0 / 16.0),
      new Vector3(3 / 16.0, 3 / 16.0, 3 / 16.0),
      new Vector3(3 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(13 / 16.0, 10 / 16.0, 3 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 3 / 16.0, 0 / 16.0),
      new Vector3(3 / 16.0, 3 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 13 / 16.0, 3 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(3 / 16.0, 3 / 16.0, 3 / 16.0),
      new Vector3(0 / 16.0, 3 / 16.0, 3 / 16.0),
      new Vector3(3 / 16.0, 0 / 16.0, 3 / 16.0),
      new Vector4(6 / 16.0, 3 / 16.0, 3 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(13 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(13 / 16.0, 0 / 16.0, 3 / 16.0),
      new Vector4(7 / 16.0, 10 / 16.0, 0 / 16.0, 3 / 16.0)
    ),
    new Quad(
      new Vector3(13 / 16.0, 3 / 16.0, 3 / 16.0),
      new Vector3(13 / 16.0, 3 / 16.0, 0 / 16.0),
      new Vector3(13 / 16.0, 0 / 16.0, 3 / 16.0),
      new Vector4(6 / 16.0, 3 / 16.0, 3 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 3 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 3 / 16.0, 3 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 13 / 16.0, 3 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(13 / 16.0, 3 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 3 / 16.0, 0 / 16.0),
      new Vector3(13 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(3 / 16.0, 0 / 16.0, 3 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 3 / 16.0, 3 / 16.0),
      new Vector3(13 / 16.0, 3 / 16.0, 3 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 3 / 16.0),
      new Vector4(13 / 16.0, 10 / 16.0, 3 / 16.0, 0 / 16.0)
    )
  };

  private final Texture[] textures;
  private final Quad[] quads;

  public BedHeadModel(int facing, BedTexture.Textures textures) {
    this.textures = new Texture[]{
      textures.headUp, textures.headDown, textures.headWest, textures.headEast,
      textures.headNorth, textures.headWest, textures.headWest, textures.headNorth,
      textures.headNorth, textures.headWest, textures.headEast, textures.headNorth,
      textures.headEast, textures.headNorth, textures.headEast};
    quads = Model.rotateY(quadsNorth, -Math.toRadians(90 * facing));
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
