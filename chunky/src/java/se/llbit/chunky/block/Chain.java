package se.llbit.chunky.block;

import se.llbit.chunky.model.ChainModel;
import se.llbit.chunky.resources.Texture;

public class Chain extends AbstractModelBlock {

  private final String description;

  public Chain(String name, Texture texture, String axis) {
    super(name, texture);
    model = new ChainModel(axis);
    description = "axis=" + axis;
  }

  @Override
  public String description() {
    return description;
  }
}
