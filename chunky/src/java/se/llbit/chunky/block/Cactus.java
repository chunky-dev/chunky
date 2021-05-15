package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.CactusModel;
import se.llbit.chunky.resources.Texture;

public class Cactus extends AbstractModelBlock {

  public Cactus() {
    super("cactus", Texture.cactusSide);
    localIntersect = true;
    opaque = false;
    this.model = new CactusModel();
  }
}
