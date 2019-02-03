package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class MinecraftBlock extends Block {
  public MinecraftBlock(String name, Texture texture) {
    super("minecraft:" + name, texture);
  }
}
