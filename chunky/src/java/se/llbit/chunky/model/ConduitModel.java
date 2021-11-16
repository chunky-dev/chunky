package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class ConduitModel extends QuadModel {
  private static final Quad[] quads = {
      // cube1
      new Quad(
          new Vector3(5 / 16.0, 11 / 16.0, 11 / 16.0),
          new Vector3(11 / 16.0, 11 / 16.0, 11 / 16.0),
          new Vector3(5 / 16.0, 11 / 16.0, 5 / 16.0),
          new Vector4(8 / 16.0, 14 / 16.0, 14 / 16.0, 8 / 16.0)),

      new Quad(
          new Vector3(5 / 16.0, 5 / 16.0, 5 / 16.0),
          new Vector3(11 / 16.0, 5 / 16.0, 5 / 16.0),
          new Vector3(5 / 16.0, 5 / 16.0, 11 / 16.0),
          new Vector4(2 / 16.0, 8 / 16.0, 8 / 16.0, 14 / 16.0)),

      new Quad(
          new Vector3(11 / 16.0, 5 / 16.0, 11 / 16.0),
          new Vector3(11 / 16.0, 5 / 16.0, 5 / 16.0),
          new Vector3(11 / 16.0, 11 / 16.0, 11 / 16.0),
          new Vector4(14 / 16.0, 8 / 16.0, 8 / 16.0, 2 / 16.0)),

      new Quad(
          new Vector3(5 / 16.0, 5 / 16.0, 5 / 16.0),
          new Vector3(5 / 16.0, 5 / 16.0, 11 / 16.0),
          new Vector3(5 / 16.0, 11 / 16.0, 5 / 16.0),
          new Vector4(8 / 16.0, 14 / 16.0, 8 / 16.0, 14 / 16.0)),

      new Quad(
          new Vector3(11 / 16.0, 5 / 16.0, 5 / 16.0),
          new Vector3(5 / 16.0, 5 / 16.0, 5 / 16.0),
          new Vector3(11 / 16.0, 11 / 16.0, 5 / 16.0),
          new Vector4(8 / 16.0, 14 / 16.0, 8 / 16.0, 14 / 16.0)),

      new Quad(
          new Vector3(5 / 16.0, 5 / 16.0, 11 / 16.0),
          new Vector3(11 / 16.0, 5 / 16.0, 11 / 16.0),
          new Vector3(5 / 16.0, 11 / 16.0, 11 / 16.0),
          new Vector4(8 / 16.0, 14 / 16.0, 8 / 16.0, 14 / 16.0)),
  };

  private static final Texture[] textures = {
      Texture.conduit, Texture.conduit, Texture.conduit,
      Texture.conduit, Texture.conduit, Texture.conduit
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
