package se.llbit.chunky.block;

import se.llbit.chunky.model.DragonEggModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class DragonEgg extends MinecraftBlockTranslucent {
  public DragonEgg() {
    super("dragon_egg", Texture.dragonEgg);
    localIntersect = true;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return DragonEggModel.intersect(ray);
  }
}
