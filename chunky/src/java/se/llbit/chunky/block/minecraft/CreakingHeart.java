package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.LogModel;
import se.llbit.chunky.resources.Texture;

public class CreakingHeart extends AbstractModelBlock {
  private final String description;

  public CreakingHeart(String name, String axis, boolean active) {
    super(name, Texture.creakingHeartTop);
    this.model = new LogModel(
      axis,
      active ? Texture.creakingHeartActive : Texture.creakingHeart,
      active ? Texture.creakingHeartTopActive : Texture.creakingHeartTop
    );
    this.description = String.format("active=%s, axis=%s", active, axis);
  }

  @Override
  public String description() {
    return description;
  }
}
