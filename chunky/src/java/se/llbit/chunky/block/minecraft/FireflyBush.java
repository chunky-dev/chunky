package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.EmissiveSpriteModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.material.TextureMaterial;

public class FireflyBush extends AbstractModelBlock {
  public static final TextureMaterial emissiveMaterial = new TextureMaterial(Texture.fireflyBushEmissive);

  public FireflyBush() {
    super("firefly_bush", Texture.fireflyBush);
    model = new EmissiveSpriteModel(Texture.fireflyBush, emissiveMaterial);
  }
}