package se.llbit.chunky.block;

import se.llbit.chunky.model.ScaffoldingModel;
import se.llbit.chunky.resources.Texture;

public class Scaffolding extends AbstractModelBlock {

  private final boolean bottom;

  public Scaffolding(boolean bottom) {
    super("scaffolding", Texture.scaffoldingSide);
    this.model = new ScaffoldingModel(bottom);
    this.bottom = bottom;
  }

  @Override
  public String description() {
    return "bottom=" + bottom;
  }
}
