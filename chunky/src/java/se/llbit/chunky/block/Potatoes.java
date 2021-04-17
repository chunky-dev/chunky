package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.CropsModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Potatoes extends MinecraftBlockTranslucent implements ModelBlock {
  private static final Texture[] texture = {
      Texture.potatoes0, Texture.potatoes0, Texture.potatoes1, Texture.potatoes1,
      Texture.potatoes2, Texture.potatoes2, Texture.potatoes2, Texture.potatoes3
  };

  private final CropsModel model;
  private final int age;

  public Potatoes(int age) {
    super("potatoes", texture[texture.length - 1]);
    localIntersect = true;
    this.age = age % texture.length;
    this.model = new CropsModel(texture[this.age]);
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public String description() {
    return "age=" + age;
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
