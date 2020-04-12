package se.llbit.chunky.block;

import se.llbit.chunky.model.GrassPathModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.Ray;

public class GrassPath extends MinecraftBlockTranslucent {
  public GrassPath() {
    super("grass_path", Texture.grassPathTop);
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return GrassPathModel.intersect(ray);
  }
}
