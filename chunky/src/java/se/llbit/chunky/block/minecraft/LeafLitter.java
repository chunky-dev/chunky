package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.LeafLitterModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.biome.Biome;

public class LeafLitter extends AbstractModelBlock {
  private final String description;

  public LeafLitter(String facing, int segmentAmount) {
    super("leaf_litter", Texture.leafLitter);
    description = String.format("facing=%s, segment_amount=%d", facing, segmentAmount);
    model = new LeafLitterModel(facing, segmentAmount);
  }

  @Override
  public int getMapColor(Biome biome) {
    return 0xAA7B5334;
  }

  @Override
  public String description() {
    return description;
  }
}
