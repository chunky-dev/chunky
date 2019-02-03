package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class Air extends MinecraftBlock {
  public static final Air INSTANCE = new Air();

  private Air() {
    super("air", Texture.air);
    solid = false;
    invisible = true;
  }
}
