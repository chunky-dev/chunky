package se.llbit.chunky.block;

import se.llbit.chunky.model.minecraft.UntintedLeafModel;
import se.llbit.chunky.resources.Texture;

public class UntintedLeaves extends AbstractModelBlock {

  public UntintedLeaves(String name, Texture texture) {
    super(name, texture);
    solid = false;
    this.model = new UntintedLeafModel(texture);
  }
}
