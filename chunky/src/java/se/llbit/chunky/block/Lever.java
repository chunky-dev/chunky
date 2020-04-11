package se.llbit.chunky.block;

import se.llbit.chunky.model.LeverModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Lever extends MinecraftBlockTranslucent {
  private final int position, activated;
  private final String description;

  public Lever(String face, String facing, boolean powered) {
    super("lever", Texture.lever);
    this.description = String.format("face=%s, facing=%s, powered=%s",
        face, facing, powered);
    localIntersect = true;
    int active = powered ? 1 : 0;
    switch (face) {
      case "ceiling":
        switch (facing) {
          default:
          case "north":
            position = 7;
            break;
          case "south":
            active ^= 1;
            position = 7;
            break;
          case "west":
            position = 0;
            break;
          case "east":
            active ^= 1;
            position = 0;
            break;
        }
        break;
      case "wall":
        switch (facing) {
          default:
          case "north":
            position = 4;
            break;
          case "south":
            position = 3;
            break;
          case "west":
            position = 2;
            break;
          case "east":
            position = 1;
            break;
        }
        break;
      default:
      case "floor":
        switch (facing) {
          default:
          case "north":
            position = 5;
            break;
          case "south":
            active ^= 1;
            position = 5;
            break;
          case "west":
            position = 6;
            break;
          case "east":
            active ^= 1;
            position = 6;
            break;
        }
        break;
    }
    activated = active;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return LeverModel.intersect(ray, position, activated);
  }

  @Override public String description() {
    return description;
  }
}
