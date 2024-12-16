package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.ResinClumpModel;
import se.llbit.chunky.resources.Texture;

public class ResinClump extends AbstractModelBlock {
  private final String description;

  public ResinClump(boolean north, boolean south, boolean east, boolean west, boolean up,
                    boolean down) {
    super("resin_clump", Texture.resinClump);
    this.description = String.format("north=%s, south=%s, east=%s, west=%s, up=%s, down=%s",
      north, south, east, west, up, down);
    this.model = new ResinClumpModel(north, south, east, west, up, down);
  }

  @Override
  public String description() {
    return description;
  }
}
