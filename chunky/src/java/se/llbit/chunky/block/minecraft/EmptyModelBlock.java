package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.EmptyModel;
import se.llbit.chunky.resources.Texture;

public class EmptyModelBlock extends AbstractModelBlock {
  public EmptyModelBlock(String name, Texture texture) {
    super(name, texture);
    this.model = EmptyModel.INSTANCE;
  }
}
