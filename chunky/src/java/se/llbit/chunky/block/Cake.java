package se.llbit.chunky.block;

import se.llbit.chunky.model.CakeModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Cake extends MinecraftBlockTranslucent {
  private final int bites;

  public Cake(int bites) {
    super("cake", Texture.cakeTop);
    localIntersect = true;
    this.bites = bites;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return CakeModel.intersect(ray, bites);
  }

  @Override public String description() {
    return "bites=" + bites;
  }
}
