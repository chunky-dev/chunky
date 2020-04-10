package se.llbit.chunky.block;

import se.llbit.chunky.model.TorchModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

/**
 * A standing torch (on ground).
 */
public class Torch extends MinecraftBlock {
  public Torch(String name, Texture texture) {
    super(name, texture);
    localIntersect = true;
    opaque = false;
    solid = false;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return TorchModel.intersect(ray, texture, 5);
  }
}
