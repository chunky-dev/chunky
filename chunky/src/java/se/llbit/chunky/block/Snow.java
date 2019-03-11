package se.llbit.chunky.block;

import se.llbit.chunky.model.SnowModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Snow extends MinecraftBlockTranslucent {
  private final int layers;

  public Snow(int layers) {
    super("snow", Texture.snowBlock);
    localIntersect = true;
    this.layers = layers;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return SnowModel.intersect(ray, layers);
  }

  @Override public String description() {
    return "layers=" + layers;
  }
}
