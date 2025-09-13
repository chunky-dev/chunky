package se.llbit.chunky.model.minecraft;

import java.util.Arrays;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;

public class CopperGrateModel extends QuadModel {
  private final Texture[] textures;

  public CopperGrateModel(Texture texture) {
    textures = new Texture[6];
    Arrays.fill(textures, texture);
  }

  @Override
  public Quad[] getQuads() {
    return FULL_BLOCK_QUADS;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }
}
