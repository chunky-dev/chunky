package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.OpenEyeblossomModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;

public class OpenEyeblossom extends AbstractModelBlock {
  public static final Material emissiveMaterial = new TextureMaterial(Texture.openEyeblossomEmissive);

  public OpenEyeblossom() {
    super("open_eyeblossom", Texture.openEyeblossom);
    model = new OpenEyeblossomModel();
  }
}
