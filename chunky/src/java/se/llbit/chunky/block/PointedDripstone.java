package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class PointedDripstone extends SpriteBlock {

  public PointedDripstone(String thickness, String verticalDirection, boolean waterlogged) {
    super("pointed_dripstone", getTexture(thickness, verticalDirection));
    this.waterlogged = waterlogged;
  }

  private static Texture getTexture(String thickness, String verticalDirection) {
    if (verticalDirection.equals("down")) {
      switch (thickness) {
        case "tip_merge":
          return Texture.pointedDripstoneDownTipMerge;
        case "frustum":
          return Texture.pointedDripstoneDownFrustum;
        case "middle":
          return Texture.pointedDripstoneDownMiddle;
        case "base":
          return Texture.pointedDripstoneDownBase;
        default:
        case "tip":
          return Texture.pointedDripstoneDownTip;
      }
    } else {
      switch (thickness) {
        case "tip_merge":
          return Texture.pointedDripstoneUpTipMerge;
        case "frustum":
          return Texture.pointedDripstoneUpFrustum;
        case "middle":
          return Texture.pointedDripstoneUpMiddle;
        case "base":
          return Texture.pointedDripstoneUpBase;
        default:
        case "tip":
          return Texture.pointedDripstoneUpTip;
      }
    }
  }
}
