package se.llbit.chunky.block;

import se.llbit.chunky.model.LightningRodModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class LightningRod extends MinecraftBlockTranslucent {

  private final String facing;

  public LightningRod(String facing) {
    super("lightning_rod", Texture.lightningRod);
    localIntersect = true;
    this.facing = facing;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return LightningRodModel.intersect(ray, facing);
  }

  @Override
  public String description() {
    return "facing=" + facing;
  }
}
