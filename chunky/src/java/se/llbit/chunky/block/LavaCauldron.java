package se.llbit.chunky.block;

import se.llbit.chunky.model.CauldronModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.Ray;

public class LavaCauldron extends Cauldron {

  public LavaCauldron() {
    super("lava_cauldron", 3); // lava cauldrons are always full
    localIntersect = true;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return CauldronModel.intersectWithLava(ray);
  }

  @Override
  public String description() {
    return ""; // do not show the level
  }
}
