package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.ChorusFlowerModel;
import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class ChorusFlower extends MinecraftBlockTranslucent implements ModelBlock {
  private final ChorusFlowerModel model;
  private final int age;

  public ChorusFlower(int age) {
    super("chorus_flower", Texture.chorusFlower);
    localIntersect = true;
    this.model = new ChorusFlowerModel(age % 6);
    this.age = age % 6;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override public String description() {
    return "age=" + age;
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
