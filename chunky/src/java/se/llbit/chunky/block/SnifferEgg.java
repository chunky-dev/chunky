package se.llbit.chunky.block;

import se.llbit.chunky.model.SnifferEggModel;
import se.llbit.chunky.resources.Texture;

public class SnifferEgg extends AbstractModelBlock {

  private final String description;

  public SnifferEgg(String name, int age) {
    super(name, getTexture(age));
    this.description = "age=" + age;
    this.model = new SnifferEggModel(this.texture);
  }

  @Override
  public String description() {
    return description;
  }

  private static Texture getTexture(int age) {
    switch (age) {
      case 1:
        return Texture.snifferEggSlightlyCracked;
      case 2:
        return Texture.snifferEggVeryCracked;
      default:
      case 0:
        return Texture.snifferEggNotCracked;
    }
  }
}
