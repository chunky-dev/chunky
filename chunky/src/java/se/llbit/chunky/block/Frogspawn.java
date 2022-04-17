package se.llbit.chunky.block;

import se.llbit.chunky.model.FrogspawnModel;
import se.llbit.chunky.resources.Texture;

public class Frogspawn extends AbstractModelBlock{
  public Frogspawn() {
    super("frogspawn", Texture.frogspawn);
    this.model = new FrogspawnModel();
  }
}
