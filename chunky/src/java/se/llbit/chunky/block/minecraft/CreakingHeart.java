package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.LogModel;
import se.llbit.chunky.resources.Texture;

public class CreakingHeart extends AbstractModelBlock {
  private final String description;

  public CreakingHeart(String name, String axis, String creakingHeartState) {
    super(name, Texture.creakingHeartTop);
    this.model = new LogModel(
      axis,
      creakingHeartState.equals("awake") ? Texture.creakingHeartAwake : (creakingHeartState.equals("dormant") ? Texture.creakingHeartDormant : Texture.creakingHeart),
      creakingHeartState.equals("awake") ? Texture.creakingHeartTopAwake : (creakingHeartState.equals("dormant") ? Texture.creakingHeartTopDormant : Texture.creakingHeartTop)
    );
    this.description = String.format("creaking_heart_state=%s, axis=%s", creakingHeartState, axis);
  }

  @Override
  public String description() {
    return description;
  }
}
