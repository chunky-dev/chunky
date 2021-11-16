package se.llbit.chunky.block;

import se.llbit.chunky.model.TorchModel;
import se.llbit.chunky.resources.Texture;

/**
 * A standing torch (on ground).
 */
public class Torch extends AbstractModelBlock {

  public Torch(String name, Texture texture) {
    super(name, texture);
    solid = false;
    model = new TorchModel(texture, 5);
  }
}
