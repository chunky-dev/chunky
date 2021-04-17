package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.SunFlowerModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

// TODO: refactor me!
// TODO: render the sunflower actually facing the sun.
public class Sunflower extends MinecraftBlockTranslucent implements ModelBlock {
  private final SunFlowerModel model;

  public Sunflower(String half) {
    super("sunflower",
        half.equals("upper")
            ? Texture.sunflowerTop
            : Texture.sunflowerBottom);
    localIntersect = true;
    solid = false;
    this.model = new SunFlowerModel(half.equals("upper"));
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
