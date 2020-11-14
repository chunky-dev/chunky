package se.llbit.chunky.block;

import se.llbit.chunky.model.CauldronModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class PowderSnowCauldron extends Cauldron {

  public PowderSnowCauldron(int level) {
    super("powder_snow_cauldron", level);
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return CauldronModel.intersect(ray, getLevel(), Texture.powderSnow);
  }
}
