package se.llbit.chunky.block;

import se.llbit.chunky.model.JigsawModel;
import se.llbit.chunky.resources.Texture;

public class JigsawBlock extends AbstractModelBlock {

  private final String orientation;

  public JigsawBlock(String name, String orientation) {
    super(name, Texture.jigsawTop);
    this.orientation = orientation;
    this.model = new JigsawModel(orientation);
    opaque = true;
  }

  @Override
  public String description() {
    return "orientation=" + orientation;
  }
}
