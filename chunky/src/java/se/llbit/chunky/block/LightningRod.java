package se.llbit.chunky.block;

import se.llbit.chunky.model.LightningRodModel;
import se.llbit.chunky.resources.Texture;

public class LightningRod extends AbstractModelBlock {

  private final String facing;
  private final boolean powered;

  public LightningRod(String facing, boolean powered) {
    super("lightning_rod", Texture.lightningRod);
    this.model = new LightningRodModel(facing, powered);
    this.powered = powered;
    this.facing = facing;
  }

  @Override
  public String description() {
    return "facing=" + facing + ", powered=" + powered;
  }

  public boolean isPowered() {
    return powered;
  }
}
