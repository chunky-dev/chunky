package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.ShelfModel;
import se.llbit.chunky.resources.Texture;

public class Shelf extends AbstractModelBlock {
  private final String description;

  public Shelf(String name, Texture texture, String facing, boolean powered, String sideChain) {
    super(name, texture);
    description = String.format("facing=%s, powered=%b, side_chain=%s", facing, powered, sideChain);
    model = new ShelfModel(texture, facing, powered, sideChain);
  }

  @Override
  public String description() {
    return description;
  }
}
