package se.llbit.chunky.block;

import se.llbit.chunky.model.CropsModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Wheat extends MinecraftBlockTranslucent {
  private static final Texture[] texture = {
      Texture.crops0, Texture.crops1, Texture.crops2, Texture.crops3, Texture.crops4,
      Texture.crops5, Texture.crops6, Texture.crops7
  };

  private final int age;

  public Wheat(int age) {
    super("wheat", Texture.crops7);
    localIntersect = true;
    this.age = age & 7;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return CropsModel.intersect(ray, texture[age]);
  }

  @Override public String description() {
    return "age=" + age;
  }
}
