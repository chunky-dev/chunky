package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

public class StrawBedHeadModel extends QuadModel {
  private static final Quad[] quadsNorth = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 4 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 4 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, 4 / 16.0, 0 / 16.0),
      new Vector4(6 / 16.0, 2 / 16.0, 12.75 / 16.0, 10.75 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 8 / 16.0),
      new Vector4(10 / 16.0, 6 / 16.0, 10.75 / 16.0, 12.75 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 4 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, 4 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 8 / 16.0),
      new Vector4(8 / 16.0, 6 / 16.0, 10.75 / 16.0, 9.75 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 4 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 4 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(2 / 16.0, 0 / 16.0, 10.75 / 16.0, 9.75 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 4 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 4 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(6 / 16.0, 2 / 16.0, 10.75 / 16.0, 9.75 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 4 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, 4 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 8 / 16.0),
      new Vector4(12 / 16.0, 8 / 16.0, 10.75 / 16.0, 9.75 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 5 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 5 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 5 / 16.0, 8 / 16.0),
      new Vector4(6 / 16.0, 2 / 16.0, 16 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(10 / 16.0, 6 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 5 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 5 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(8 / 16.0, 6 / 16.0, 14 / 16.0, 12.75 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 5 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 5 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 8 / 16.0),
      new Vector4(2 / 16.0, 0 / 16.0, 14 / 16.0, 12.75 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 5 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 5 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 8 / 16.0),
      new Vector4(6 / 16.0, 2 / 16.0, 14 / 16.0, 12.75 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 5 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 5 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(12 / 16.0, 8 / 16.0, 14 / 16.0, 12.75 / 16.0)
    )
  };

  private final Texture[] textures;
  private final Quad[] quads;

  public StrawBedHeadModel(int facing) {
    quads = Model.rotateY(quadsNorth, -Math.toRadians(90 * facing));
    textures = new Texture[quads.length];
    Arrays.fill(textures, Texture.strawBed);
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
