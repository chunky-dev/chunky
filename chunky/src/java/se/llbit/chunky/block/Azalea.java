package se.llbit.chunky.block;

import se.llbit.chunky.model.AzaleaModel;
import se.llbit.chunky.resources.Texture;

public class Azalea extends AbstractModelBlock {

  public Azalea(String name, Texture top, Texture side) {
    super(name, top);
    solid = false;
    model = new AzaleaModel(top, side);
  }
}
