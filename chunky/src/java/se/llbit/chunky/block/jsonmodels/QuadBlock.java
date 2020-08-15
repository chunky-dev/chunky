package se.llbit.chunky.block.jsonmodels;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;

public class QuadBlock extends AbstractModelBlock {
  private final boolean isEntity;

  public QuadBlock(String name, Texture up, Quad[] quads, Texture[] textures, boolean entity) {
    super(name, up);
    this.model = new Model(quads, textures);
    this.isEntity = entity;
  }

  @Override
  public boolean isBlockEntity() {
    return isEntity;
  }

  static class Model extends QuadModel {
    private final Quad[] quads;
    private final Texture[] textures;

    Model(Quad[] quads, Texture[] textures) {
      this.quads = quads;
      this.textures = textures;
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
}
