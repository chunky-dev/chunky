package se.llbit.chunky.block;

import se.llbit.chunky.model.SnowModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Snow extends MinecraftBlock {
  private final int layers;

  public Snow(int layers) {
    super("snow", Texture.snowBlock);
    this.layers = layers;
    localIntersect = layers < 8;
    opaque = layers == 8;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return SnowModel.intersect(ray, layers);
  }

  @Override public String description() {
    return "layers=" + layers;
  }
}
