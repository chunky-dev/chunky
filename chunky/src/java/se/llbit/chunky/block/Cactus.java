package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.CactusModel;
import se.llbit.chunky.resources.Texture;

public class Cactus extends MinecraftBlockTranslucent implements ModelBlock {

  private static final CactusModel model = new CactusModel();

  public Cactus() {
    super("cactus", Texture.cactusSide);
    localIntersect = true;
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
