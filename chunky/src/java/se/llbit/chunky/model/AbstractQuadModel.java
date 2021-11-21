package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;

public abstract class AbstractQuadModel extends QuadModel {

  protected Quad[] quads;
  protected Texture[] textures;
  protected Tint[] tints;

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }

  @Override
  public Tint[] getTints() {
    return tints;
  }
}
