package se.llbit.chunky.block;

import se.llbit.chunky.model.GrassTintedSpriteModel;
import se.llbit.chunky.resources.Texture;

public class TallGrass extends AbstractModelBlock {

  public TallGrass(String half) {
    super("tall_grass",
        half.equals("upper")
            ? Texture.doubleTallGrassTop
            : Texture.doubleTallGrassBottom);
    solid = false;
    model = new GrassTintedSpriteModel(texture);
  }
}
