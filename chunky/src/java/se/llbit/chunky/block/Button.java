package se.llbit.chunky.block;

import se.llbit.chunky.model.ButtonModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Button extends MinecraftBlockTranslucent {
  private final int position;
  private final String description;

  public Button(String name, Texture texture,
      String face, String facing, boolean powered) {
    super(name, texture);
    this.description = String.format("face=%s, facing=%s, powered=%s",
        face, facing, powered);
    localIntersect = true;
    // TODO handle rotation on top/bottom positions!
    switch (face) {
      case "ceiling":
        position = 0;
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
            position = 1;
            break;
          case "east":
            position = 2;
            break;
        }
        break;
      default:
      case "floor":
        position = 5;
        break;
    }
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return ButtonModel.intersect(ray, texture, position);
  }

  @Override public String description() {
    return description;
  }
}
