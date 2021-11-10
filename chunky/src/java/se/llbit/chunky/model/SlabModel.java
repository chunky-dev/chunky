package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;

public class SlabModel extends AABBModel {
  private final static AABB[] lower = { new AABB(0, 1, 0, .5, 0, 1) };
  private final static AABB[] upper = { new AABB(0, 1, .5, 1, 0, 1) };
  private final static AABB[] full = { new AABB(0, 1, 0, 1, 0, 1) };

  private final AABB[] boxes;
  private final Texture[][] textures;

  public SlabModel(Texture side, Texture top, String type) {
    switch (type) {
      case "top":
        boxes = upper;
        break;
      default:
      case "bottom":
        boxes = lower;
        break;
      case "double":
        boxes = full;
        break;
    }

    textures = new Texture[][] {{
      side, side, side, side, top, top
    }};
  }

  @Override
  public AABB[] getBoxes() {
    return boxes;
  }

  @Override
  public Texture[][] getTextures() {
    return textures;
  }
}
