package se.llbit.chunky.block;

import se.llbit.chunky.model.CauldronModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class LavaCauldron extends MinecraftBlockTranslucent {

  public LavaCauldron() {
    super("lava_cauldron", Texture.cauldronSide);
    localIntersect = true;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return CauldronModel.intersectWithLava(ray);
  }
}
