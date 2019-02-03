package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class CoarseDirt extends MinecraftBlock {
  public static final CoarseDirt INSTANCE = new CoarseDirt();

  private CoarseDirt() {
    super("coarse_dirt", Texture.coarseDirt);
  }
}
