package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.ObserverModel;
import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Observer extends MinecraftBlock implements ModelBlock {
  private final ObserverModel model;
  private final String description;

  public Observer(String facing, boolean powered) {
    super("observer", Texture.observerFront);
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
    this.model = new ObserverModel(direction, powered);
    localIntersect = true;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
