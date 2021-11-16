package se.llbit.chunky.block;

import se.llbit.chunky.model.CropsModel;
import se.llbit.chunky.resources.Texture;

public class Potatoes extends AbstractModelBlock {

  private static final Texture[] texture = {
      Texture.potatoes0, Texture.potatoes0, Texture.potatoes1, Texture.potatoes1,
      Texture.potatoes2, Texture.potatoes2, Texture.potatoes2, Texture.potatoes3
  };

  private final int age;

  public Potatoes(int age) {
    super("potatoes", texture[texture.length - 1]);
    this.age = age % texture.length;
    this.model = new CropsModel(texture[this.age]);
  }

  @Override
  public String description() {
    return "age=" + age;
  }
}
