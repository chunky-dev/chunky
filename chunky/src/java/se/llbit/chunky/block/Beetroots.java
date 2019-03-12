package se.llbit.chunky.block;

import se.llbit.chunky.model.CropsModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Beetroots extends MinecraftBlock {
  private static final Texture[] texture = {
      Texture.beets0, Texture.beets1, Texture.beets2, Texture.beets3
  };

  private final int age;

  public Beetroots(int age) {
    super("beetroots", Texture.beets3);
    localIntersect = true;
    opaque = false;
    this.age = age & 3;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return CropsModel.intersect(ray, texture[age]);
  }

  @Override public String description() {
    return "age=" + age;
  }
}
