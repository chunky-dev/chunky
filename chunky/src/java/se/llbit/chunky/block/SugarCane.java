package se.llbit.chunky.block;

import se.llbit.chunky.model.SpriteModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class SugarCane extends MinecraftBlock {
  public SugarCane() {
    super("sugar_cane", Texture.sugarCane);
    localIntersect = true;
    solid = false;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return SpriteModel.intersect(ray, texture);
  }
}
