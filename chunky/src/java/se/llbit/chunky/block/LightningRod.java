package se.llbit.chunky.block;

import se.llbit.chunky.model.LightningRodModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class LightningRod extends MinecraftBlockTranslucent {

  private final String facing;
  private final boolean powered;

  public LightningRod(String facing, boolean powered) {
    super("lightning_rod", Texture.lightningRod);
    this.powered = powered;
    localIntersect = true;
    this.facing = facing;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return LightningRodModel.intersect(ray, facing, powered);
  }

  @Override
  public String description() {
    return "facing=" + facing + ", powered=" + powered;
  }

  public boolean isPowered() {
    return powered;
  }
}
