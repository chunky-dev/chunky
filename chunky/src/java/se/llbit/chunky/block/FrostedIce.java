package se.llbit.chunky.block;

import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class FrostedIce extends MinecraftBlockTranslucent {
  private static final Texture[] texture = {
      Texture.frostedIce0, Texture.frostedIce1, Texture.frostedIce2, Texture.frostedIce3
  };

  private final int age;

  public FrostedIce(int age) {
    super("frosted_ice", Texture.frostedIce3);
    localIntersect = true;
    this.age = age & 3;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return TexturedBlockModel.intersect(ray, texture[age]);
  }

  @Override public String description() {
    return "age=" + age;
  }
}
