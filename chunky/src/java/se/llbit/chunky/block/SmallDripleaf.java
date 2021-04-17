package se.llbit.chunky.block;

import se.llbit.chunky.model.SmallDripleafModel;
import se.llbit.chunky.resources.Texture;

public class SmallDripleaf extends AbstractModelBlock {

  private final String description;

  public SmallDripleaf(String facing, String half) {
    super("small_dripleaf", Texture.smallDripleafTop);
    this.description = "facing=" + facing + ", half=" + half;
    this.model = new SmallDripleafModel(facing, half);
    solid = false;
  }

  @Override
  public String description() {
    return description;
  }
}
