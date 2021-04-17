package se.llbit.chunky.block;

import se.llbit.chunky.model.LanternModel;
import se.llbit.chunky.resources.Texture;

public class Lantern extends AbstractModelBlock {

  private final boolean hanging;

  public Lantern(String name, Texture texture, boolean hanging) {
    super(name, texture);
    this.hanging = hanging;
    this.model = new LanternModel(texture, hanging);
  }

  @Override
  public String description() {
    return "hanging=" + hanging;
  }
}
