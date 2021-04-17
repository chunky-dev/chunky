package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.ArrayList;
import java.util.Arrays;

public class GlowLichenModel extends QuadModel {

  private static final Quad[] glowLichen = {
      // North
      new DoubleSidedQuad(new Vector3(0, 0, 0.1 / 16), new Vector3(1, 0, 0.1 / 16),
          new Vector3(0, 1, 0.1 / 16), new Vector4(0, 1, 0, 1)),

      // South
      new DoubleSidedQuad(new Vector3(1, 0, 15.9 / 16), new Vector3(0, 0, 15.9 / 16),
          new Vector3(1, 1, 15.9 / 16), new Vector4(1, 0, 0, 1)),

      // East
      new DoubleSidedQuad(new Vector3(15.9 / 16, 0, 0), new Vector3(15.9 / 16, 0, 1),
          new Vector3(15.9 / 16, 1, 0), new Vector4(0, 1, 0, 1)),

      // West
      new DoubleSidedQuad(new Vector3(0.1 / 16, 0, 1), new Vector3(0.1 / 16, 0, 0),
          new Vector3(0.1 / 16, 1, 1), new Vector4(1, 0, 0, 1)),

      // Top
      new DoubleSidedQuad(new Vector3(0, 15.9 / 16, 0), new Vector3(1, 15.9 / 16, 0),
          new Vector3(0, 15.9 / 16, 1), new Vector4(0, 1, 0, 1)),

      // Bottom
      new DoubleSidedQuad(new Vector3(0, 0.1 / 16, 0), new Vector3(1, 0.1 / 16, 0),
          new Vector3(0, 0.1 / 16, 1), new Vector4(0, 1, 0, 1)),
  };

  private final Quad[] quads;
  private final Texture[] textures;

  public GlowLichenModel(int connections) {
    ArrayList<Quad> quads = new ArrayList<>();
    for (int i = 0; i < glowLichen.length; i++) {
      if ((connections & (1 << i)) != 0)
        quads.add(glowLichen[i]);
    }
    this.quads = quads.toArray(new Quad[0]);
    this.textures = new Texture[this.quads.length];
    Arrays.fill(this.textures, Texture.glowLichen);
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
