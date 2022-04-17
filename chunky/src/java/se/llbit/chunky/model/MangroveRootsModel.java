package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class MangroveRootsModel extends QuadModel {
  private static final Texture side = Texture.mangroveRootsSide;
  private static final Texture top = Texture.mangroveRootsTop;

  private static final Texture[] textures = new Texture[]{
    side, side, side, side, top, top, top, top, side, side, side, side, side, side, side, side
  };

  private static final Quad[] quads = new Quad[]{
    new Quad(
      new Vector3(0.8 / 16.0, 16 / 16.0, 8 / 16.0),
      new Vector3(15.2 / 16.0, 16 / 16.0, 8 / 16.0),
      new Vector3(0.8 / 16.0, 0 / 16.0, 8 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(15.2 / 16.0, 16 / 16.0, 8 / 16.0),
      new Vector3(0.8 / 16.0, 16 / 16.0, 8 / 16.0),
      new Vector3(15.2 / 16.0, 0 / 16.0, 8 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 16 / 16.0, 15.2 / 16.0),
      new Vector3(8 / 16.0, 16 / 16.0, 0.8 / 16.0),
      new Vector3(8 / 16.0, 0 / 16.0, 15.2 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 16 / 16.0, 0.8 / 16.0),
      new Vector3(8 / 16.0, 16 / 16.0, 15.2 / 16.0),
      new Vector3(8 / 16.0, 0 / 16.0, 0.8 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 15.998 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 15.998 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 15.998 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0.002 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0.002 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 0.002 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 16 / 16.0, 0.002 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 0.002 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0.002 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 15.998 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 15.998 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 15.998 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0.002 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(0.002 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0.002 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(15.998 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(15.998 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(15.998 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    )
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
