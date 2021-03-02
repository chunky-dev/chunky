package se.llbit.chunky.block;

import se.llbit.chunky.model.GrassBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.Ray;

public class GrassBlock extends MinecraftBlock {

  private static final GrassBlockModel model = new GrassBlockModel();

  private final static AABB aabb = new AABB(0, 1, 0, 1, 0, 1);

  public GrassBlock() {
    super("grass_block", Texture.grassTop);
    localIntersect = true;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }
}
