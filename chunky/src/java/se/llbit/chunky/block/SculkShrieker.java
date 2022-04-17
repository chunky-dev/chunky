package se.llbit.chunky.block;

import se.llbit.chunky.model.SculkShriekerModel;
import se.llbit.chunky.resources.Texture;

public class SculkShrieker extends AbstractModelBlock {
  private final boolean canSummon;

  public SculkShrieker(boolean canSummon) {
    super("sculk_shrieker", Texture.sculkShriekerTop);
    this.canSummon = canSummon;
    model = new SculkShriekerModel(canSummon);
  }

  @Override
  public String description() {
    return "can_summon=" + canSummon;
  }
}
