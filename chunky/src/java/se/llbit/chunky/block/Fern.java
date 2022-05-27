package se.llbit.chunky.block;

import se.llbit.chunky.model.GrassTintedSpriteModel;
import se.llbit.chunky.resources.Texture;

public class Fern extends AbstractModelBlock {

  public Fern() {
    super("fern", Texture.fern);
    solid = false;
    model = new GrassTintedSpriteModel(Texture.fern);
  }
}
