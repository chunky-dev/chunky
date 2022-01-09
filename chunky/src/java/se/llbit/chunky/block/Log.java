package se.llbit.chunky.block;

import se.llbit.chunky.model.LogModel;
import se.llbit.chunky.resources.Texture;

public class Log extends AbstractModelBlock {

  private final String description;

  public Log(String name, Texture side, Texture top, String axis) {
    super(name, side);
    this.description = "axis=" + axis;
    this.model = new LogModel(axis, side, top);
    opaque = true;
    solid = true;
  }

  @Override
  public String description() {
    return description;
  }
}
