package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.ConduitModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Conduit extends MinecraftBlockTranslucent implements ModelBlock {
  private static final ConduitModel model = new ConduitModel();

  public Conduit() {
    super("conduit", Texture.conduit);
    localIntersect = true;
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
