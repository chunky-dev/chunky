package se.llbit.chunky.block;

import se.llbit.chunky.model.ConduitModel;
import se.llbit.chunky.resources.Texture;

public class Conduit extends AbstractModelBlock {

  public Conduit() {
    super("conduit", Texture.conduit);
    model = new ConduitModel();
  }
}
