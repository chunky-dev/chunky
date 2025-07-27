package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.model.Tint;
import se.llbit.chunky.model.TintedSpriteModel;
import se.llbit.chunky.resources.Texture;

public class TintedSpriteBlock extends SpriteBlock {
  private Tint tint;

  public TintedSpriteBlock(String name, Texture texture, Tint tint) {
    super(name, texture);
    this.tint = tint;
    model = new TintedSpriteModel(texture, tint);
  }

  public TintedSpriteBlock(String name, Texture texture, Tint tint, String facing) {
    super(name, texture, facing);
    model = new TintedSpriteModel(texture, tint);
  }
}
