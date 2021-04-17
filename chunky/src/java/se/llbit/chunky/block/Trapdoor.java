package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.TrapdoorModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

// TODO: fix rendering/texturing bugs.
public class Trapdoor extends MinecraftBlockTranslucent implements ModelBlock {
  private final TrapdoorModel model;
  private final String description;

  public Trapdoor(String name, Texture texture,
      String half, String facing, boolean open) {
    super(name, texture);
    localIntersect = true;
    this.description = String.format("half=%s, facing=%s, open=%s",
        half, facing, open);
    int state;
    switch (facing) {
      default:
      case "north":
        state = 0;
        break;
      case "south":
        state = 1;
        break;
      case "east":
        state = 3;
        break;
      case "west":
        state = 2;
        break;
    }
    if (open) {
      state |= 4;
    }
    if (half.equals("top")) {
      state |= 8;
    }
    this.model = new TrapdoorModel(texture, state);
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
