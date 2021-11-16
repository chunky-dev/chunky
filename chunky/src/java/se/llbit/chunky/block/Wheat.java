package se.llbit.chunky.block;

import se.llbit.chunky.model.CropsModel;
import se.llbit.chunky.resources.Texture;

public class Wheat extends AbstractModelBlock {

  private static final Texture[] texture = {
      Texture.crops0, Texture.crops1, Texture.crops2, Texture.crops3, Texture.crops4,
      Texture.crops5, Texture.crops6, Texture.crops7
  };

  private final int age;

  public Wheat(int age) {
    super("wheat", Texture.crops7);
    this.age = age & 7;
    this.model = new CropsModel(texture[this.age]);
  }

  @Override
  public String description() {
    return "age=" + age;
  }
}
