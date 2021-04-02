package se.llbit.chunky.block;

import se.llbit.chunky.model.BambooModel;
import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Bamboo extends MinecraftBlockTranslucent {

  private final int age;
  private final String leaves;
  private final BlockModel model;

  public Bamboo(int age, String leaves) {
    super("bamboo", Texture.bambooStalk);
    this.age = age;
    this.leaves = leaves;
    localIntersect = true;
    model = new BambooModel(age, leaves);
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }
}
