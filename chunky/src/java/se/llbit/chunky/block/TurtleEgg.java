package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.TurtleEggModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class TurtleEgg extends MinecraftBlockTranslucent implements ModelBlock {
  private final TurtleEggModel model;
  private final String description;

  public TurtleEgg(int eggs, int hatch) {
    super("turtle_egg", Texture.turtleEgg);
    this.description = String.format("eggs=%d, hatch=%d", eggs, hatch);
    this.model = new TurtleEggModel(eggs, hatch);
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
