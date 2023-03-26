package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

public class SnifferEggModel extends QuadModel {
  private final Texture[] textures;

  //region Quads
  private static final Quad[] quads = new Quad[]{
    new Quad(
      new Vector3(1 / 16.0, 16 / 16.0, 14 / 16.0),
      new Vector3(15 / 16.0, 16 / 16.0, 14 / 16.0),
      new Vector3(1 / 16.0, 16 / 16.0, 2 / 16.0),
      new Vector4(16 / 16.0, 10.4 / 16.0, 16 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(1 / 16.0, 0 / 16.0, 2 / 16.0),
      new Vector3(15 / 16.0, 0 / 16.0, 2 / 16.0),
      new Vector3(1 / 16.0, 0 / 16.0, 14 / 16.0),
      new Vector4(16 / 16.0, 10.4 / 16.0, 4 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(1 / 16.0, 16 / 16.0, 14 / 16.0),
      new Vector3(1 / 16.0, 16 / 16.0, 2 / 16.0),
      new Vector3(1 / 16.0, 0 / 16.0, 14 / 16.0),
      new Vector4(10.4 / 16.0, 5.6 / 16.0, 16 / 16.0, 8 / 16.0)
    ),
    new Quad(
      new Vector3(15 / 16.0, 16 / 16.0, 2 / 16.0),
      new Vector3(15 / 16.0, 16 / 16.0, 14 / 16.0),
      new Vector3(15 / 16.0, 0 / 16.0, 2 / 16.0),
      new Vector4(10.4 / 16.0, 5.6 / 16.0, 8 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(1 / 16.0, 16 / 16.0, 2 / 16.0),
      new Vector3(15 / 16.0, 16 / 16.0, 2 / 16.0),
      new Vector3(1 / 16.0, 0 / 16.0, 2 / 16.0),
      new Vector4(5.6 / 16.0, 0 / 16.0, 16 / 16.0, 8 / 16.0)
    ),
    new Quad(
      new Vector3(15 / 16.0, 16 / 16.0, 14 / 16.0),
      new Vector3(1 / 16.0, 16 / 16.0, 14 / 16.0),
      new Vector3(15 / 16.0, 0 / 16.0, 14 / 16.0),
      new Vector4(5.6 / 16.0, 0 / 16.0, 8 / 16.0, 0 / 16.0)
    )
  };
  //endregion

  public SnifferEggModel(Texture texture) {
    textures = new Texture[quads.length];
    Arrays.fill(textures, texture);
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
