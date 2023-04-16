package se.llbit.chunky.block;

import se.llbit.chunky.model.GrassTintedSpriteModel;
import se.llbit.chunky.resources.Texture;

public class SugarCane extends AbstractModelBlock {
  public SugarCane() {
    super("sugar_cane", Texture.sugarCane);
    solid = false;
    model = new GrassTintedSpriteModel(Texture.sugarCane);
  }
}
