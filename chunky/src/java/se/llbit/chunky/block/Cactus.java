package se.llbit.chunky.block;

import se.llbit.chunky.model.CactusModel;
import se.llbit.chunky.model.LadderModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Cactus extends MinecraftBlockTranslucent {
  public Cactus() {
    super("cactus", Texture.cactusSide);
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return CactusModel.intersect(ray);
  }
}
