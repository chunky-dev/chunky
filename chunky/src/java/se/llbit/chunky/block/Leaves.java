package se.llbit.chunky.block;

import se.llbit.chunky.model.LeafModel;
import se.llbit.chunky.resources.Texture;

public class Leaves extends AbstractModelBlock {

  public Leaves(String name, Texture texture) {
    super(name, texture);
    solid = false;
    this.model = new LeafModel(texture);
  }

  public Leaves(String name, Texture texture, int tint) {
    super(name, texture);
    solid = false;
    this.model = new LeafModel(texture, tint);
  }
}
