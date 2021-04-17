package se.llbit.chunky.block;

import se.llbit.chunky.model.GrassPathModel;
import se.llbit.chunky.resources.Texture;

public class GrassPath extends AbstractModelBlock {

  public GrassPath() {
    super("grass_path", Texture.grassPathTop);
    model = new GrassPathModel();
  }
}
