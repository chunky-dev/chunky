package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.BarsModel;
import se.llbit.chunky.resources.Texture;

public class CopperBars extends AbstractModelBlock {
  private final String description;

  public CopperBars(String name, Texture texture, boolean north, boolean south, boolean east, boolean west) {
    super(name, texture);
    description = String.format("north=%s, south=%s, east=%s, west=%s", north, south, east, west);
    model = new BarsModel(north, east, south, west, texture, texture);
  }

  @Override
  public String description() {
    return description;
  }
}
