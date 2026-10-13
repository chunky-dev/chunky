package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.ShelfMushroomModel;
import se.llbit.chunky.resources.Texture;

public class ShelfMushroom extends AbstractModelBlock {
  private final String description;

  public ShelfMushroom(String name, String facing, int age) {
    super(name, Texture.shelfMushroomStage1);
    solid = false;
    this.description = String.format("facing=%s, age=%d", facing, age);
    this.model = new ShelfMushroomModel(facing, age);
  }

  @Override
  public String description() {
    return description;
  }
}
