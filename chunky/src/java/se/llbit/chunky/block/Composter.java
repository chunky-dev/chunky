package se.llbit.chunky.block;

import se.llbit.chunky.model.ComposterModel;
import se.llbit.chunky.resources.Texture;

public class Composter extends AbstractModelBlock {

  private final int level;

  public Composter(int level) {
    super("composter", Texture.composterSide);
    this.level = level;
    this.model = new ComposterModel(level);
  }

  @Override
  public String description() {
    return "level=" + level;
  }
}
