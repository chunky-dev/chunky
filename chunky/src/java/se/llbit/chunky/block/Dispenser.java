package se.llbit.chunky.block;

import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Dispenser extends MinecraftBlock {
  private static final Texture[][] texture = {
      // Facing down.
      {
        Texture.furnaceTop, Texture.furnaceTop, Texture.furnaceTop, Texture.furnaceTop,
        Texture.furnaceTop, Texture.dispenserFrontVertical
      },
      // Facing up.
      {
        Texture.furnaceTop, Texture.furnaceTop, Texture.furnaceTop, Texture.furnaceTop,
        Texture.dispenserFrontVertical, Texture.furnaceTop
      },
      // Facing north.
      {
        Texture.dispenserFront, Texture.furnaceSide, Texture.furnaceSide, Texture.furnaceSide,
        Texture.furnaceTop, Texture.furnaceTop,
      },
      // Facing south.
      {
        Texture.furnaceSide, Texture.dispenserFront, Texture.furnaceSide, Texture.furnaceSide,
        Texture.furnaceTop, Texture.furnaceTop,
      },
      // Facing east.
      {
        Texture.furnaceSide, Texture.furnaceSide, Texture.furnaceSide, Texture.dispenserFront,
        Texture.furnaceTop, Texture.furnaceTop,
      },
      // Facing west.
      {
        Texture.furnaceSide, Texture.furnaceSide, Texture.dispenserFront, Texture.furnaceSide,
        Texture.furnaceTop, Texture.furnaceTop,
      },
  };

  private final int facing;
  private final String description;

  public Dispenser(String facing) {
    super("dispenser", Texture.dispenserFront);
    this.description = "facing=" + facing;
    localIntersect = true;
    switch (facing) {
      case "down":
        this.facing = 0;
        break;
      case "up":
        this.facing = 1;
        break;
      default:
      case "north":
        this.facing = 2;
        break;
      case "south":
        this.facing = 3;
        break;
      case "west":
        this.facing = 4;
        break;
      case "east":
        this.facing = 5;
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
