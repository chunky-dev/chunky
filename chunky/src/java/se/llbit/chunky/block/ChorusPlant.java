package se.llbit.chunky.block;

import se.llbit.chunky.model.ChorusPlantModel;
import se.llbit.chunky.resources.Texture;

public class ChorusPlant extends AbstractModelBlock {

  private final String description;

  public ChorusPlant(
      boolean north, boolean south, boolean east, boolean west,
      boolean up, boolean down) {
    super("chorus_plant", Texture.chorusPlant);
    this.description = String.format("north=%s, south=%s, east=%s, west=%s",
        north, south, east, west);
    model = new ChorusPlantModel(north, south, east, west, up, down);
  }

  @Override
  public String description() {
    return description;
  }
}
