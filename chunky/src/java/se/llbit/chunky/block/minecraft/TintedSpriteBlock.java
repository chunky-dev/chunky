package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.model.Tint;
import se.llbit.chunky.model.TintedSpriteModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.biome.Biome;

public class TintedSpriteBlock extends SpriteBlock {
  private Tint tint;

  public TintedSpriteBlock(String name, Texture texture, Tint tint) {
    super(name, texture);
    this.tint = tint;
    model = new TintedSpriteModel(texture, tint);
  }

  public TintedSpriteBlock(String name, Texture texture, Tint tint, String facing) {
    super(name, texture, facing);
    model = new TintedSpriteModel(texture, tint);
  }

  @Override
  public int getMapColor(Biome biome) {
    return switch (tint.type) {
      case BIOME_DRY_FOLIAGE -> biome.dryFoliageColor | 0xFF000000;
      case BIOME_FOLIAGE -> biome.foliageColor | 0xFF000000;
      case BIOME_GRASS -> biome.grassColor | 0xFF000000;
      case BIOME_WATER -> biome.waterColor | 0xFF000000;
      default -> super.getMapColor(biome);
    };
  }
}
