package se.llbit.chunky.block;

import se.llbit.chunky.model.PitcherPlantBottomModel;
import se.llbit.chunky.model.PitcherPlantTopModel;
import se.llbit.chunky.resources.Texture;

public class PitcherPlant extends AbstractModelBlock {
  private final String description;

  public PitcherPlant(String name, String half) {
    super(name, Texture.pinkPetals);
    this.description = String.format("half=%s", half);
    this.model = half.equals("upper") ? new PitcherPlantTopModel() : new PitcherPlantBottomModel();
  }

  @Override
  public String description() {
    return this.description;
  }
}
