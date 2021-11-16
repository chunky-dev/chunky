package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class NetherPortalModel extends QuadModel {
  private final static Quad[] quadNS = {
      new DoubleSidedQuad(
          new Vector3(16 / 16.0, 0, 6 / 16.0),
          new Vector3(0, 0, 6 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector4(0, 16 / 16.0, 0, 16 / 16.0)
      )
  };

  private final static Quad[] quadEW = {
      new DoubleSidedQuad(
          new Vector3(10 / 16.0, 0, 16 / 16.0),
          new Vector3(10 / 16.0, 0, 0),
          new Vector3(10 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector4(0, 16 / 16.0, 0, 16 / 16.0)
      )
  };

  private final static Texture[] textures = { Texture.portal };

  private final Quad[] quads;

  public NetherPortalModel(String axis) {
    quads = axis.equals("z") ? quadEW : quadNS;
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
