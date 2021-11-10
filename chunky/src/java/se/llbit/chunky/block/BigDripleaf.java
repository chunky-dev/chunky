package se.llbit.chunky.block;

import se.llbit.chunky.model.BigDripleafModel;
import se.llbit.chunky.resources.Texture;

public class BigDripleaf extends AbstractModelBlock {

  private final String description;

  public BigDripleaf(String facing, String tilt) {
    super("big_dripleaf", Texture.bigDripleafTop);
    description = "facing=" + facing + ", tilt=" + tilt;
    model = new BigDripleafModel(facing, tilt);
    solid = false;
  }

  @Override
  public String description() {
    return description;
  }
}
