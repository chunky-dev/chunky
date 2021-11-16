package se.llbit.chunky.block;

import se.llbit.chunky.model.BrewingStandModel;
import se.llbit.chunky.resources.Texture;

public class BrewingStand extends AbstractModelBlock {
  private final String description;

  public BrewingStand(boolean bottle0, boolean bottle1, boolean bottle2) {
    super("brewing_stand", Texture.brewingStandBase);
    description = String.format("has_bottle_0=%s, has_bottle_1=%s, has_bottle_2=%s",
        bottle0, bottle1, bottle2);
    this.model = new BrewingStandModel(bottle0, bottle1, bottle2);
  }

  @Override
  public String description() {
    return description;
  }
}
