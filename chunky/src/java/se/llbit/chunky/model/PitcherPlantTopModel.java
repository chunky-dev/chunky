package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class PitcherPlantTopModel extends QuadModel {
  private static final Texture top = Texture.pitcherCropTopStage4;
  private static final Texture[] textures = new Texture[]{
    top, top, top, top, top, top, top, top, top, top, top, top
  };

  private static final Quad[] quads = Model.rotateY(new Quad[]{
    new Quad(
      new Vector3(8 / 16.0, 11 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, 11 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, 11 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, -5 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, -5 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, -5 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 11 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, 11 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, -5 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 11 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, 11 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, -5 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 11 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, 11 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, -5 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 11 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, 11 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, -5 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, -5 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, -5 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, -5 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, -5 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, -5 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, -5 / 16.0, 8 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, -5 / 16.0, 8 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    )
  }, Math.toRadians(45));

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }
}
