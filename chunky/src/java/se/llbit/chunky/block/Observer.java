package se.llbit.chunky.block;

import se.llbit.chunky.model.ObserverModel;
import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Observer extends MinecraftBlock {
  private static final Texture[][] texture = {
      {
          Texture.observerBack, Texture.observerFront,
          Texture.observerSide, Texture.observerTop
      },
      {
          Texture.observerBackOn, Texture.observerFront,
          Texture.observerSide, Texture.observerTop
      }
  };

  private final String description;
  private final int facing;
  private final Texture[] textures;

  public Observer(String facing, boolean powered) {
    super("ovserver", Texture.observerFront);
    this.description = String.format("facing=%s, powered=%s", facing, powered);
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
    this.facing = direction;
    textures = powered ? texture[1] : texture[0];
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return ObserverModel.intersect(ray, textures, facing);
  }

  @Override public String description() {
    return description;
  }
}
