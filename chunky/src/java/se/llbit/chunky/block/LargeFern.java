package se.llbit.chunky.block;

import se.llbit.chunky.model.GrassTintedSpriteModel;
import se.llbit.chunky.resources.Texture;

public class LargeFern extends AbstractModelBlock {

  public LargeFern(String half) {
    super("large_fern",
        half.equals("upper")
            ? Texture.largeFernTop
            : Texture.largeFernBottom);
    solid = false;
    model = new GrassTintedSpriteModel(texture);
  }
}
