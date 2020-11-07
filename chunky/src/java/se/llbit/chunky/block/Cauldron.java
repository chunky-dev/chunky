package se.llbit.chunky.block;

import se.llbit.chunky.model.CauldronModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Cauldron extends MinecraftBlockTranslucent {

  private final int level;

  public Cauldron(String name, int level) {
    super(name, Texture.cauldronSide);
    this.level = level;
    localIntersect = true;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return CauldronModel.intersect(ray, scene.stillWaterEnabled(), level);
  }

  @Override
  public String description() {
    return "level=" + level;
  }
}
