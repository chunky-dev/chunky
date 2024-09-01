package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class HeavyCoreModel extends QuadModel {
  private static final AbstractTexture[] textures = new AbstractTexture[]{
    Texture.heavyCore, Texture.heavyCore, Texture.heavyCore,
    Texture.heavyCore, Texture.heavyCore, Texture.heavyCore,
  };

  private static final Quad[] quads = new Quad[]{
    new Quad(
      new Vector3(4 / 16.0, 8 / 16.0, 12 / 16.0),
      new Vector3(12 / 16.0, 8 / 16.0, 12 / 16.0),
      new Vector3(4 / 16.0, 8 / 16.0, 4 / 16.0),
      new Vector4(0 / 16.0, 8 / 16.0, 8 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(4 / 16.0, 0 / 16.0, 4 / 16.0),
      new Vector3(12 / 16.0, 0 / 16.0, 4 / 16.0),
      new Vector3(4 / 16.0, 0 / 16.0, 12 / 16.0),
      new Vector4(8 / 16.0, 16 / 16.0, 8 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(4 / 16.0, 8 / 16.0, 12 / 16.0),
      new Vector3(4 / 16.0, 8 / 16.0, 4 / 16.0),
      new Vector3(4 / 16.0, 0 / 16.0, 12 / 16.0),
      new Vector4(8 / 16.0, 0 / 16.0, 8 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 8 / 16.0, 4 / 16.0),
      new Vector3(12 / 16.0, 8 / 16.0, 12 / 16.0),
      new Vector3(12 / 16.0, 0 / 16.0, 4 / 16.0),
      new Vector4(8 / 16.0, 0 / 16.0, 8 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(4 / 16.0, 8 / 16.0, 4 / 16.0),
      new Vector3(12 / 16.0, 8 / 16.0, 4 / 16.0),
      new Vector3(4 / 16.0, 0 / 16.0, 4 / 16.0),
      new Vector4(8 / 16.0, 0 / 16.0, 8 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 8 / 16.0, 12 / 16.0),
      new Vector3(4 / 16.0, 8 / 16.0, 12 / 16.0),
      new Vector3(12 / 16.0, 0 / 16.0, 12 / 16.0),
      new Vector4(8 / 16.0, 0 / 16.0, 8 / 16.0, 0 / 16.0)
    )
  };

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public AbstractTexture[] getTextures() {
    return textures;
  }
}
