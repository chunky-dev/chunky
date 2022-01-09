package se.llbit.chunky.block;

import se.llbit.chunky.model.GrassBlockModel;
import se.llbit.chunky.resources.Texture;

public class GrassBlock extends AbstractModelBlock {

  public GrassBlock() {
    super("grass_block", Texture.grassTop);
    model = new GrassBlockModel();
    opaque = true;
    solid = false;
  }
}
