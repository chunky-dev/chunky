package se.llbit.chunky.block;

import se.llbit.chunky.model.CarpetModel;
import se.llbit.chunky.resources.Texture;

public class Carpet extends AbstractModelBlock {

  public Carpet(String name, Texture texture) {
    super(name, texture);
    solid = false;
    this.model = new CarpetModel(texture);
  }
}
