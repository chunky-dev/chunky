package se.llbit.chunky.block;

import se.llbit.chunky.model.MangroveRootsModel;
import se.llbit.chunky.resources.Texture;

public class MangroveRoots extends AbstractModelBlock {
  public MangroveRoots() {
    super("mangrove_roots", Texture.mangroveRootsTop);
    this.model = new MangroveRootsModel();
  }
}
