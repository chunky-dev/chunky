package se.llbit.chunky.block;

import se.llbit.chunky.model.Flowerbed;
import se.llbit.chunky.resources.Texture;

public class PinkPetals extends AbstractModelBlock {
  private final String description;

  public PinkPetals(String name, int flowerAmount, String facing) {
    super(name, Texture.pinkPetals);
    this.description = String.format("facing=%s, flower_amount=%d", facing, flowerAmount);
    this.model = new Flowerbed(Texture.pinkPetals, Texture.pinkPetalsStem, flowerAmount, facing);
  }

  @Override
  public String description() {
    return this.description;
  }
}
