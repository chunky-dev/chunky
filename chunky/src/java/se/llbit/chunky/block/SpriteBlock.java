package se.llbit.chunky.block;

import se.llbit.chunky.model.SpriteModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class SpriteBlock extends MinecraftBlockTranslucent {
  public SpriteBlock(String name, Texture texture) {
    super(name, texture);
    localIntersect = true;
    solid = false;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return SpriteModel.intersect(ray, texture);
  }
}
