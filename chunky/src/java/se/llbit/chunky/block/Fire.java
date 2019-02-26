package se.llbit.chunky.block;

import se.llbit.chunky.model.FireModel;
import se.llbit.chunky.model.PistonModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Fire extends MinecraftBlock {

  public Fire() {
    super("fire", Texture.fire);
    localIntersect = true;
    opaque = false;
    solid = false;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return FireModel.intersect(ray);
  }
}
