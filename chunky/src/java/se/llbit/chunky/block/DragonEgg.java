package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.DragonEggModel;
import se.llbit.chunky.resources.Texture;

public class DragonEgg extends AbstractModelBlock {

  public DragonEgg() {
    super("dragon_egg", Texture.dragonEgg);
    model = new DragonEggModel();
  }
}
