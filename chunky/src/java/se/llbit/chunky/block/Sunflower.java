package se.llbit.chunky.block;

import se.llbit.chunky.model.LargeFlowerModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

// TODO: refactor me!
// TODO: render the sunflower actually facing the sun.
public class Sunflower extends MinecraftBlockTranslucent {
  private final int top;

  public Sunflower(String half) {
    super("sunflower",
        half.equals("upper")
            ? Texture.sunflowerTop
            : Texture.sunflowerBottom);
    localIntersect = true;
    solid = false;
    top = half.equals("upper") ? 1 : 0;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return LargeFlowerModel.intersect(ray, scene, 0, top);
  }
}
