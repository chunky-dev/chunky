package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.LightningRodModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class LightningRod extends MinecraftBlockTranslucent implements ModelBlock {
  private final LightningRodModel model;
  private final String facing;
  private final boolean powered;

  public LightningRod(String facing, boolean powered) {
    super("lightning_rod", Texture.lightningRod);
    localIntersect = true;
    this.model = new LightningRodModel(facing, powered);
    this.powered = powered;
    this.facing = facing;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public String description() {
    return "facing=" + facing + ", powered=" + powered;
  }

  public boolean isPowered() {
    return powered;
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
