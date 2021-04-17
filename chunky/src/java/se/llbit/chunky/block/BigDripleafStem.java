package se.llbit.chunky.block;

import se.llbit.chunky.model.BigDripleafStemModel;
import se.llbit.chunky.resources.Texture;

public class BigDripleafStem extends AbstractModelBlock {

  private final String facing;

  public BigDripleafStem(String facing) {
    super("big_dripleaf_stem", Texture.bigDripleafStem);
    this.facing = facing;
    model = new BigDripleafStemModel(facing);
    solid = false;
  }

  @Override
  public String description() {
    return "facing=" + facing;
  }
}
