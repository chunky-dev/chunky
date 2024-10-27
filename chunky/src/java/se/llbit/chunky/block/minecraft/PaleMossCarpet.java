package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.PaleMossCarpetModel;
import se.llbit.chunky.resources.Texture;

public class PaleMossCarpet extends AbstractModelBlock {
  private final String description;

  public PaleMossCarpet(String name, boolean bottom, String north, String east, String south, String west) {
    super(name, Texture.paleMossCarpet);
    this.model = new PaleMossCarpetModel(bottom, north, east, south, west);
    this.description = String.format("bottom=%s, north=%s, east=%s, south=%s, west=%s", bottom, north, east, south, west);
  }

  @Override
  public String description() {
    return description;
  }
}
