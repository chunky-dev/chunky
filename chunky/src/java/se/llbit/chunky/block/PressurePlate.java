package se.llbit.chunky.block;

import se.llbit.chunky.model.PressurePlateModel;
import se.llbit.chunky.resources.Texture;

public class PressurePlate extends AbstractModelBlock {

  public PressurePlate(String name, Texture texture) {
    super(name, texture);
    model = new PressurePlateModel(texture);
  }
}
