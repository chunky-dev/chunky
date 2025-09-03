package se.llbit.chunky.block;

import se.llbit.chunky.block.minecraft.Leaves;
import se.llbit.chunky.model.minecraft.UntintedLeafModel;
import se.llbit.chunky.resources.Texture;

public class UntintedLeaves extends Leaves {

  public UntintedLeaves(String name, Texture texture) {
    super(texture, name);
    this.model = new UntintedLeafModel(texture);
  }
}
