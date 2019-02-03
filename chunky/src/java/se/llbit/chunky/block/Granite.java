package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class Granite extends MinecraftBlock {
  public static final Granite INSTANCE = new Granite();

  private Granite() {
    super("granite", Texture.granite);
  }
}
