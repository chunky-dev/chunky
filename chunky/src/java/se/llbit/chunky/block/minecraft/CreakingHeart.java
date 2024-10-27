package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.LogModel;
import se.llbit.chunky.resources.Texture;

public class CreakingHeart extends AbstractModelBlock {
  private final String description;

  public CreakingHeart(String name, String axis, String creaking) {
    super(name, Texture.creakingHeartTop);
    this.model = new LogModel(
      axis,
      creaking.equals("disabled") ? Texture.creakingHeart : Texture.creakingHeartActive,
      creaking.equals("disabled") ? Texture.creakingHeartTop : Texture.creakingHeartTopActive
    );
    this.description = String.format("axis=%s, creaking=%s", axis, creaking);
  }

  @Override
  public String description() {
    return description;
  }
}
