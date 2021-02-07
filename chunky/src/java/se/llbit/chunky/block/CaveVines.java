package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class CaveVines extends SpriteBlock {

  private final boolean berries;

  public CaveVines(String name, boolean berries) {
    super(name, getTexture(name.endsWith("_body"), berries));
    this.berries = berries;
  }

  private static Texture getTexture(boolean body, boolean lit) {
    if (body) {
      return lit ? Texture.caveVinesBodyLit : Texture.caveVinesBody;
    }
    return lit ? Texture.caveVinesHeadLit : Texture.caveVinesHead;
  }

  public boolean hasBerries() {
    return berries;
  }

  @Override
  public String description() {
    return "berries=" + berries;
  }
}
