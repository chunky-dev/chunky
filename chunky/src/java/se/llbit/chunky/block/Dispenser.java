package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

/**
 * The dispenser behaves almost like a TopBottomOrientedTexturedBlock. If it's facing up or down, it
 * has different textures (and thus different texture orientation logic).
 */
public class Dispenser extends TopBottomOrientedTexturedBlock {

  private static final int[][] verticalUvRotationMap = {
      {0, 0, 0, 0, 2, 0}, // up
      {2, 2, 2, 2, 2, 0}, // down
  };

  private static final int[][] verticalTextureOrientationMap = {
      {4, 4, 4, 4, 0, 4}, // up
      {4, 4, 4, 4, 4, 0}, // down
  };

  private final String description;

  public Dispenser(String facing) {
    this("dispenser", facing, Texture.dispenserFront, Texture.dispenserFrontVertical,
        Texture.furnaceSide, Texture.furnaceTop);
  }

  public Dispenser(String name, String facing, Texture front, Texture frontVertical, Texture side,
      Texture back) {
    super(name, facing,
        facing.equals("up") || facing.equals("down") ? frontVertical : front,
        side, back);
    this.description = "facing=" + facing;
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  protected int[][] getUvRotationMap() {
    return facing <= 1 ? verticalUvRotationMap : super.getUvRotationMap();
  }

  @Override
  protected int[][] getTextureOrientationMap() {
    return facing <= 1 ? verticalTextureOrientationMap : super.getTextureOrientationMap();
  }
}
