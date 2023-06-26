package se.llbit.chunky.block;

import se.llbit.chunky.model.SnifferEggModel;
import se.llbit.chunky.resources.Texture;

public class SnifferEgg extends AbstractModelBlock {

  private final String description;

  public SnifferEgg(String name, int age) {
    super(name, getTopTexture(age));
    this.description = "age=" + age;
    this.model = new SnifferEggModel(age);
  }

  @Override
  public String description() {
    return description;
  }

  private static Texture getTopTexture(int age) {
    switch (age) {
      case 1:
        return Texture.snifferEggSlightlyCrackedTop;
      case 2:
        return Texture.snifferEggVeryCrackedTop;
      default:
      case 0:
        return Texture.snifferEggNotCrackedTop;
    }
  }
}
