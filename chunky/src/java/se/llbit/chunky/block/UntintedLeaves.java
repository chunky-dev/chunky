package se.llbit.chunky.block;

import se.llbit.chunky.model.minecraft.UntintedLeafModel;
import se.llbit.chunky.resources.Texture;

public class UntintedLeaves extends LeavesBase {

  public UntintedLeaves(String name, Texture texture) {
    super(name, texture);
    this.model = new UntintedLeafModel(texture);
  }
}
