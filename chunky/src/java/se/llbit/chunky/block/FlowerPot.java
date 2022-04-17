package se.llbit.chunky.block;

import se.llbit.chunky.model.FlowerPotModel;
import se.llbit.chunky.resources.Texture;

public class FlowerPot extends AbstractModelBlock {
  public FlowerPot(String name, FlowerPotModel.Kind kind) {
    super(name, Texture.flowerPot);
    this.model = new FlowerPotModel(kind);
  }
}