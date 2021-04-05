package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.TorchModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

/**
 * A standing torch (on ground).
 */
public class Torch extends MinecraftBlock implements ModelBlock {

  private final BlockModel model;

  public Torch(String name, Texture texture) {
    super(name, texture);
    localIntersect = true;
    opaque = false;
    solid = false;
    model = new TorchModel(texture, 5);
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
