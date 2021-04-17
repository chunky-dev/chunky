package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class StemModel extends QuadModel {
  private static final Texture[] textures = {Texture.stemStraight, Texture.stemStraight};
  private static final Tint[][] stemColors = {
      {new Tint(0xFF00E210), new Tint(0xFF00E210)},
      {new Tint(0xFF00E210), new Tint(0xFF00E210)},
      {new Tint(0xFF00E210), new Tint(0xFF00E210)},
      {new Tint(0xFF00CC06), new Tint(0xFF00CC06)},
      {new Tint(0xFF5FC803), new Tint(0xFF5FC803)},
      {new Tint(0xFF65C206), new Tint(0xFF65C206)},
      {new Tint(0xFFA0B800), new Tint(0xFFA0B800)},
      {new Tint(0xFFBFB600), new Tint(0xFFBFB600)},
  };

  private final Quad[] quads;
  private final Tint[] tints;

  public StemModel(int height) {
    this.quads = new Quad[] {
        new DoubleSidedQuad(new Vector3(0, 0, 0), new Vector3(1, 0, 1),
            new Vector3(0, (height + 1) / 8., 0), new Vector4(0, 1, (7 - height) / 8., 1)),
        new DoubleSidedQuad(new Vector3(1, 0, 0), new Vector3(0, 0, 1),
            new Vector3(1, (height + 1) / 8., 0), new Vector4(0, 1, (7 - height) / 8., 1)),
    };
    this.tints = stemColors[height];
  }

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
