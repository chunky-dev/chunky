package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;

public class DispenserModel extends AABBModel {
  private final static AABB[] boxes = { new AABB(0, 1, 0, 1, 0, 1) };

  private final Texture[][] textures;

  public DispenserModel(String facing, Texture front, Texture frontVertical, Texture side, Texture back) {
    textures = new Texture[1][];

    switch (facing) {
      case "up":
        textures[0] = new Texture[] {back, back, back, back, frontVertical, back};
        break;
      case "down":
        textures[0] = new Texture[] {back, back, back, back, back, frontVertical};
        break;
      case "north":
        textures[0] = new Texture[] {front, side, side, side, back, back};
        break;
      case "east":
        textures[0] = new Texture[] {side, front, side, side, back, back};
        break;
      case "south":
        textures[0] = new Texture[] {side, side, front, side, back, back};
        break;
      case "west":
        textures[0] = new Texture[] {side, side, side, front, back, back};
        break;
    }
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
