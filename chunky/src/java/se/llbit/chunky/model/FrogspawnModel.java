package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class FrogspawnModel extends QuadModel {
  private static final Quad[] quads = {
    new Quad(new Vector3(0, 0.25 / 16, 0), new Vector3(1, 0.25 / 16, 0),
      new Vector3(0, 0.25 / 16, 1), new Vector4(0, 1, 1, 0), true),
  };

  private static final Texture[] textures = {
    Texture.frogspawn
  };

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }
}
