package se.llbit.chunky.block;

import java.util.Arrays;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;

public class SolidNonOpaqueBlock extends AbstractModelBlock {
  private static class Model extends QuadModel {
    private final Texture[] textures;

    private Model(Texture texture) {
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

  public SolidNonOpaqueBlock(String name, Texture texture) {
    super(name, texture);
    solid = true;
    opaque = false;
    this.model = new Model(texture);
  }
}
