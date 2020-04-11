package se.llbit.chunky.block;

import se.llbit.chunky.model.FlowerPotModel;
import se.llbit.chunky.model.PistonModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class FlowerPot extends MinecraftBlockTranslucent {
  private final FlowerPotModel.Kind kind;

  public FlowerPot(String name, FlowerPotModel.Kind kind) {
    super(name, Texture.flowerPot);
    this.kind = kind;
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return FlowerPotModel.intersect(ray, scene, kind);
  }
}
