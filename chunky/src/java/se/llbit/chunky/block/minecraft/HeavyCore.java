package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.HeavyCoreModel;
import se.llbit.chunky.resources.Texture;

public class HeavyCore extends AbstractModelBlock {
  public HeavyCore() {
    super("heavy_core", Texture.heavyCore);
    this.model = new HeavyCoreModel();
  }
}
