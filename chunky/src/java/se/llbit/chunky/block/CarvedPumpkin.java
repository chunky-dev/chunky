package se.llbit.chunky.block;

import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class CarvedPumpkin extends MinecraftBlock {
  private static final Texture[][] texture = {
      // Facing north.
      {
          Texture.pumpkinFront, Texture.pumpkinSide, Texture.pumpkinSide, Texture.pumpkinSide,
          Texture.pumpkinTop, Texture.pumpkinTop,
      },
      // Facing south.
      {
          Texture.pumpkinSide, Texture.pumpkinFront, Texture.pumpkinSide, Texture.pumpkinSide,
          Texture.pumpkinTop, Texture.pumpkinTop,
      },
      // Facing east.
      {
          Texture.pumpkinSide, Texture.pumpkinSide, Texture.pumpkinSide, Texture.pumpkinFront,
          Texture.pumpkinTop, Texture.pumpkinTop,
      },
      // Facing west.
      {
          Texture.pumpkinSide, Texture.pumpkinSide, Texture.pumpkinFront, Texture.pumpkinSide,
          Texture.pumpkinTop, Texture.pumpkinTop,
      },
  };

  private final int facing;
  private final String description;

  public CarvedPumpkin(String facing) {
    super("carved_pumpkin", Texture.pumpkinFront);
    this.description = "facing=" + facing;
    localIntersect = true;
    switch (facing) {
      default:
      case "north":
        this.facing = 0;
        break;
      case "south":
        this.facing = 1;
        break;
      case "west":
        this.facing = 2;
        break;
      case "east":
        this.facing = 3;
        break;
    }
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return TexturedBlockModel.intersect(ray, texture[facing]);
  }

  @Override public String description() {
    return description;
  }
}
