package se.llbit.chunky.block;

import se.llbit.chunky.model.CakeModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

// TODO: render bites correctly.
public class Cake extends MinecraftBlockTranslucent {
  private final int bites;

  public Cake(int bites) {
    super("snow", Texture.cakeTop);
    localIntersect = true;
    this.bites = bites;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return CakeModel.intersect(ray);
  }

  @Override public String description() {
    return "bites=" + bites;
  }
}
