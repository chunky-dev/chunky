package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class Stone extends MinecraftBlock {
  public static final Stone INSTANCE = new Stone();

  private Stone() {
    super("stone", Texture.stone);
  }
}
