package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class PolishedDiorite extends MinecraftBlock {
  public static final PolishedDiorite INSTANCE = new PolishedDiorite();

  private PolishedDiorite() {
    super("polished_diorite", Texture.smoothDiorite);
  }
}
