package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;

/**
 * A simple opaque block with a single texture.
 */
public class MinecraftBlock extends Block {
  public static final Material STONE = new MinecraftBlock("stone", Texture.stone);

  public MinecraftBlock(String name, Texture texture) {
    super("minecraft:" + name, texture);
    opaque = true;
    solid = true;
  }
}
