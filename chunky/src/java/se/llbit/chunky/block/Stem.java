package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.StemModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

/**
 * Melon or pumpkin stem.
 */
public class Stem extends MinecraftBlockTranslucent implements ModelBlock {
  private final StemModel model;
  private final int age;

  public Stem(String name, int age) {
    super(name, Texture.stemStraight);
    localIntersect = true;
    this.model = new StemModel(age & 7);
    this.age = age & 7;
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
