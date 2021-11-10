package se.llbit.chunky.block;

import se.llbit.chunky.model.CropsModel;
import se.llbit.chunky.resources.Texture;

public class Carrots extends AbstractModelBlock {

  private static final Texture[] texture = {
      Texture.carrots0, Texture.carrots0, Texture.carrots1, Texture.carrots1,
      Texture.carrots2, Texture.carrots2, Texture.carrots2, Texture.carrots3
  };

  private final int age;

  public Carrots(int age) {
    super("carrots", texture[texture.length - 1]);
    this.age = age % texture.length;
    this.model = new CropsModel(texture[this.age]);
  }

  @Override
  public String description() {
    return "age=" + age;
  }
}
