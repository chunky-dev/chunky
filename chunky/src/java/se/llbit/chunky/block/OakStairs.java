package se.llbit.chunky.block;

import se.llbit.chunky.model.StairModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class OakStairs extends MinecraftBlock {
  private final int flipped;

  public OakStairs(String half, String shape, String facing) {
    super("oak_stairs", Texture.oakPlanks);
    localIntersect = true;
    solid = false;
    flipped = (half.equals("top")) ? 1 : 0;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return StairModel.intersect(ray, texture, flipped, 0, 0);
  }
}
