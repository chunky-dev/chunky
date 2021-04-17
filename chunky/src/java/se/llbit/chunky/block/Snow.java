package se.llbit.chunky.block;

import se.llbit.chunky.model.SnowModel;
import se.llbit.chunky.resources.Texture;

public class Snow extends AbstractModelBlock {

  private final int layers;

  public Snow(int layers) {
    super("snow", Texture.snowBlock);
    this.layers = layers;
    localIntersect = layers < 8;
    opaque = layers == 8;
    this.model = new SnowModel(layers);
    localIntersect = layers < 8;
    opaque = layers == 8;
  }

  @Override
  public String description() {
    return "layers=" + layers;
  }
}
