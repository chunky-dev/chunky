package se.llbit.chunky.block;

import se.llbit.chunky.model.CropsModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Potatoes extends MinecraftBlock {
  private static final Texture[] texture = {
      Texture.potatoes0, Texture.potatoes0, Texture.potatoes1, Texture.potatoes1,
      Texture.potatoes2, Texture.potatoes2, Texture.potatoes2, Texture.potatoes3
  };

  private final int age;

  public Potatoes(int age) {
    super("potatoes", texture[texture.length - 1]);
    localIntersect = true;
    opaque = false;
    this.age = age % texture.length;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return CropsModel.intersect(ray, texture[age]);
  }

  @Override public String description() {
    return "age=" + age;
  }
}
