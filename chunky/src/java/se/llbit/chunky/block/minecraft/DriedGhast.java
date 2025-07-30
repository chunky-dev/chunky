package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.DriedGhastModel;
import se.llbit.chunky.resources.Texture;

public class DriedGhast extends AbstractModelBlock {
  private final String description;

  public DriedGhast(String facing, int hydration) {
    super("dried_ghast", Texture.driedGhastHydration0Top);
    model = new DriedGhastModel(facing, hydration);
    description = String.format("facing=%s, hydration=%d", facing, hydration);
  }

  @Override
  public String description() {
    return description;
  }
}
