package se.llbit.chunky.block;

import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Dropper extends MinecraftBlock {
  private static final Texture[][] texture = {
      // Facing down.
      {
        Texture.furnaceTop, Texture.furnaceTop, Texture.furnaceTop, Texture.furnaceTop,
        Texture.furnaceTop, Texture.dropperFrontVertical
      },
      // Facing up.
      {
        Texture.furnaceTop, Texture.furnaceTop, Texture.furnaceTop, Texture.furnaceTop,
        Texture.dropperFrontVertical, Texture.furnaceTop
      },
      // Facing north.
      {
        Texture.dropperFront, Texture.furnaceSide, Texture.furnaceSide, Texture.furnaceSide,
        Texture.furnaceTop, Texture.furnaceTop,
      },
      // Facing south.
      {
        Texture.furnaceSide, Texture.dropperFront, Texture.furnaceSide, Texture.furnaceSide,
        Texture.furnaceTop, Texture.furnaceTop,
      },
      // Facing east.
      {
        Texture.furnaceSide, Texture.furnaceSide, Texture.furnaceSide, Texture.dropperFront,
        Texture.furnaceTop, Texture.furnaceTop,
      },
      // Facing west.
      {
        Texture.furnaceSide, Texture.furnaceSide, Texture.dropperFront, Texture.furnaceSide,
        Texture.furnaceTop, Texture.furnaceTop,
      },
  };

  private final String description;
  private final Texture[] textures;

  public Dropper(String facing) {
    super("dropper", Texture.dropperFront);
    this.description = "facing=" + facing;
    int direction;
    switch (facing) {
      case "up":
        direction = 1;
        break;
      case "down":
        direction = 0;
        break;
      default:
      case "north":
        direction = 2;
        break;
      case "east":
        direction = 5;
        break;
      case "south":
        direction = 3;
        break;
      case "west":
        direction = 4;
        break;
    }
    textures = texture[direction];
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return TexturedBlockModel.intersect(ray, textures);
  }

  @Override public String description() {
    return description;
  }
}
