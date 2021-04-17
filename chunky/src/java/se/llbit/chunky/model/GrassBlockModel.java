package se.llbit.chunky.model;

import static se.llbit.chunky.model.Tint.BIOME_GRASS;
import static se.llbit.chunky.model.Tint.NONE;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;

public class GrassBlockModel extends AABBModel {

  private final static Tint[][] tints = new Tint[][] {
      {BIOME_GRASS, BIOME_GRASS, BIOME_GRASS, BIOME_GRASS, NONE, NONE},
      {NONE, NONE, NONE, NONE, BIOME_GRASS, NONE}
  };

  private final static AABB[] boxes = new AABB[]{
      new AABB(0, 1, 0, 1, 0, 1),
      new AABB(0, 1, 0, 1, 0, 1)
  };

  private static final Texture[][] textures = new Texture[][]{
      {
          Texture.grassSide, Texture.grassSide,
          Texture.grassSide, Texture.grassSide,
          null, null
      },
      {
          Texture.grassSideSaturated, Texture.grassSideSaturated,
          Texture.grassSideSaturated, Texture.grassSideSaturated,
          Texture.grassTop, Texture.dirt
      }
  };

  @Override
  public AABB[] getBoxes() {
    return boxes;
  }

  @Override
  public Texture[][] getTextures() {
    return textures;
  }

  @Override
  public Tint[][] getTints() {
    return tints;
  }
}
