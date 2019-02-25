package se.llbit.chunky.block;

import se.llbit.chunky.model.StairModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Stairs extends MinecraftBlock {
  private final int flipped, facing, corner;
  private final boolean isCorner;
  private final Texture side, top, bottom;

  public Stairs(String name, Texture texture, String half, String shape, String facing) {
    this(name, texture, texture, texture, half, shape, facing);
  }

  public Stairs(String name, Texture side, Texture top, Texture bottom,
      String half, String shape, String facing) {
    super(String.format("%s (half=%s, shape=%s, facing=%s)", name, half, shape, facing),
        side);
    this.side = side;
    this.top = top;
    this.bottom = bottom;
    localIntersect = true;
    solid = false;
    flipped = (half.equals("top")) ? 1 : 0;
    switch (facing) {
      default:
      case "east":
        this.facing = 0;
        break;
      case "west":
        this.facing = 1;
        break;
      case "south":
        this.facing = 2;
        break;
      case "north":
        this.facing = 3;
        break;
    }
    isCorner = !shape.equals("straight");
    switch (shape) {
      default:
      case "straight":
        this.corner = 0;
        break;
      case "outer_right":
        switch (facing) {
          default:
          case "east":
            this.corner = 0;
            break;
          case "west":
            this.corner = 3;
            break;
          case "south":
            this.corner = 1;
            break;
          case "north":
            this.corner = 2;
            break;
        }
        break;
      case "outer_left":
        switch (facing) {
          default:
          case "east":
            this.corner = 2;
            break;
          case "west":
            this.corner = 1;
            break;
          case "south":
            this.corner = 0;
            break;
          case "north":
            this.corner = 3;
            break;
        }
        break;
      case "inner_right":
        switch (facing) {
          default:
          case "east":
            this.corner = 0+4;
            break;
          case "west":
            this.corner = 3+4;
            break;
          case "south":
            this.corner = 1+4;
            break;
          case "north":
            this.corner = 2+4;
            break;
        }
        break;
      case "inner_left":
        switch (facing) {
          default:
          case "east":
            this.corner = 2+4;
            break;
          case "west":
            this.corner = 1+4;
            break;
          case "south":
            this.corner = 0+4;
            break;
          case "north":
            this.corner = 3+4;
            break;
        }
        break;
    }
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return StairModel.intersect(ray, side, top, bottom, flipped, isCorner, corner, facing);
  }
}
