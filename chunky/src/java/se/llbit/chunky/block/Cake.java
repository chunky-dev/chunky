package se.llbit.chunky.block;

import se.llbit.chunky.model.CakeModel;
import se.llbit.chunky.resources.Texture;

public class Cake extends AbstractModelBlock {

  private final int bites;

  public Cake(int bites) {
    super("cake", Texture.cakeTop);
    this.model = new CakeModel(bites);
    this.bites = bites;
  }

  @Override
  public String description() {
    return "bites=" + bites;
  }
}
