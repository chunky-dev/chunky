package se.llbit.chunky.block;

import se.llbit.chunky.resources.SolidColorTexture;

public class Void extends MinecraftBlock {
  public static final Void INSTANCE = new Void();

  public Void() {
    super("void", SolidColorTexture.EMPTY);
    solid = false;
    opaque = false;
    invisible = true;
  }
}
