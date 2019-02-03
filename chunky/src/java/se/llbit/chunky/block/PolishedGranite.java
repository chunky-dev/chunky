package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class PolishedGranite extends MinecraftBlock {
  public static final PolishedGranite INSTANCE = new PolishedGranite();

  private PolishedGranite() {
    super("polished_granite", Texture.smoothGranite);
  }
}
