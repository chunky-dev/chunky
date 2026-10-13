package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.ShelfMushroomModel;
import se.llbit.chunky.resources.Texture;

public class ShelfMushroom extends AbstractModelBlock {
  private final String description;

  public ShelfMushroom(String name, Texture texture, String facing, int age) {
    super(name, texture);
    solid = false;
    this.description = String.format("facing=%s, age=%d", facing, age);
    this.model = new ShelfMushroomModel(texture, facing, age);
  }

  @Override
  public String description() {
    return description;
  }
}
