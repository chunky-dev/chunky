package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class GoldBlock extends MinecraftBlock {
  public static final GoldBlock INSTANCE = new GoldBlock();

  private GoldBlock() {
    super("gold_block", Texture.goldBlock);
  }
}
