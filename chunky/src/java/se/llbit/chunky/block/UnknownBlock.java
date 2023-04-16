package se.llbit.chunky.block;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class UnknownBlock extends SpriteBlock {
  public static final UnknownBlock UNKNOWN = new UnknownBlock("?");

  public UnknownBlock(String name) {
    super(name, Texture.unknown);
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    if (scene.getHideUnknownBlocks()) {
      return false;
    }
    return super.intersect(ray, scene);
  }
}
