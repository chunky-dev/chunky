package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class SculkCatalyst extends TexturedBlock {
  private final boolean bloom;

  public SculkCatalyst(boolean bloom) {
    super("sculk_catalyst",
      bloom ? Texture.sculkCatalystSideBloom : Texture.sculkCatalystSide,
      bloom ? Texture.sculkCatalystTopBloom : Texture.sculkCatalystTop,
      Texture.sculkCatalystBottom);
    this.bloom = bloom;
  }

  @Override
  public String description() {
    return "bloom=" + bloom;
  }
}
