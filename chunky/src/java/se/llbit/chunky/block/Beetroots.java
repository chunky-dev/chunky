package se.llbit.chunky.block;

import se.llbit.chunky.model.CropsModel;
import se.llbit.chunky.resources.Texture;

public class Beetroots extends AbstractModelBlock {

  private static final Texture[] texture = {
      Texture.beets0, Texture.beets1, Texture.beets2, Texture.beets3
  };

  private final int age;

  public Beetroots(int age) {
    super("beetroots", Texture.beets3);
    this.age = age & 3;
    this.model = new CropsModel(texture[this.age]);
  }

  @Override
  public String description() {
    return "age=" + age;
  }
}
