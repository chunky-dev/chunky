package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.GrassTintedSpriteModel;
import se.llbit.chunky.resources.Texture;

public class Bush extends AbstractModelBlock {
  public Bush() {
    super("bush", Texture.bush);
    solid = false;
    model = new GrassTintedSpriteModel(Texture.bush);
  }
}
