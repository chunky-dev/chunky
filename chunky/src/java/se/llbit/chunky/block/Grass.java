package se.llbit.chunky.block;

import se.llbit.chunky.model.TallGrassModel;
import se.llbit.chunky.resources.Texture;

public class Grass extends AbstractModelBlock {

  public Grass() {
    super("grass", Texture.tallGrass);
    solid = false;
    model = new TallGrassModel(texture);
  }
}
