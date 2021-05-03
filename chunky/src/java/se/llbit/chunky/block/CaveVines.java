package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class CaveVines extends SpriteBlock {

  private final boolean berries;

  public CaveVines(String name, boolean berries, boolean body) {
    super(name, getTexture(body, berries));
    this.berries = berries;
  }

  private static Texture getTexture(boolean body, boolean lit) {
    if (body) {
      return lit ? Texture.caveVinesPlantLit : Texture.caveVinesPlant;
    }
    return lit ? Texture.caveVinesLit : Texture.caveVines;
  }

  public boolean hasBerries() {
    return berries;
  }

  @Override
  public String description() {
    return "berries=" + berries;
  }
}
