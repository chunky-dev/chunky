package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class PolishedAndesite extends MinecraftBlock {
  public static final PolishedAndesite INSTANCE = new PolishedAndesite();

  private PolishedAndesite() {
    super("polished_andesite", Texture.smoothAndesite);
  }
}
