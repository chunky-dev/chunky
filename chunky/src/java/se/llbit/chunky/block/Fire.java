package se.llbit.chunky.block;

import se.llbit.chunky.model.FireModel;
import se.llbit.chunky.resources.Texture;

public class Fire extends AbstractModelBlock {

  public Fire() {
    super("fire", Texture.fire);
    model = new FireModel(Texture.fireLayer0, Texture.fireLayer1);
    solid = false;
  }
}
