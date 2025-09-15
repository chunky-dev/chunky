package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.CopperGrateModel;
import se.llbit.chunky.resources.Texture;

public class CopperGrate extends AbstractModelBlock {
  public CopperGrate(String name, Texture texture) {
    super(name, texture);
    solid = true;
    this.model = new CopperGrateModel(texture);
  }
}
