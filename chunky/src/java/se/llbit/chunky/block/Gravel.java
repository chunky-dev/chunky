package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class Gravel extends MinecraftBlock {
  public static final Gravel INSTANCE = new Gravel();

  private Gravel() {
    super("gravel", Texture.gravel);
  }
}
