package se.llbit.chunky.block;

import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Furnace extends MinecraftBlock {
  private static final Texture[][][] texture = {
      // Unlit.
      {
          // Facing north.
          {
              Texture.furnaceUnlitFront, Texture.furnaceSide, Texture.furnaceSide, Texture.furnaceSide,
              Texture.furnaceTop, Texture.furnaceTop,
          },
          // Facing south.
          {
              Texture.furnaceSide, Texture.furnaceUnlitFront, Texture.furnaceSide, Texture.furnaceSide,
              Texture.furnaceTop, Texture.furnaceTop,
          },
          // Facing east.
          {
              Texture.furnaceSide, Texture.furnaceSide, Texture.furnaceSide, Texture.furnaceUnlitFront,
              Texture.furnaceTop, Texture.furnaceTop,
          },
          // Facing west.
          {
              Texture.furnaceSide, Texture.furnaceSide, Texture.furnaceUnlitFront, Texture.furnaceSide,
              Texture.furnaceTop, Texture.furnaceTop,
          },
      },
      // Unlit.
      {
          // Facing north.
          {
              Texture.furnaceLitFront, Texture.furnaceSide, Texture.furnaceSide, Texture.furnaceSide,
              Texture.furnaceTop, Texture.furnaceTop,
          },
          // Facing south.
          {
              Texture.furnaceSide, Texture.furnaceLitFront, Texture.furnaceSide, Texture.furnaceSide,
              Texture.furnaceTop, Texture.furnaceTop,
          },
          // Facing east.
          {
              Texture.furnaceSide, Texture.furnaceSide, Texture.furnaceSide, Texture.furnaceLitFront,
              Texture.furnaceTop, Texture.furnaceTop,
          },
          // Facing west.
          {
              Texture.furnaceSide, Texture.furnaceSide, Texture.furnaceLitFront, Texture.furnaceSide,
              Texture.furnaceTop, Texture.furnaceTop,
          },
      }
  };

  private final int facing, lit;
  private final String description;

  public Furnace(String facing, boolean lit) {
    super("furnace", Texture.furnaceLitFront);
    this.description = String.format("facing=%s, lit=%s", facing, lit);
    localIntersect = true;
    this.lit = lit ? 1 : 0;
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

  public boolean isLit() {
    return lit > 0;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return TexturedBlockModel.intersect(ray, texture[lit][facing]);
  }

  @Override public String description() {
    return description;
  }
}
