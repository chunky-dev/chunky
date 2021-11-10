package se.llbit.chunky.block;

import se.llbit.chunky.model.StemModel;
import se.llbit.chunky.resources.Texture;

/**
 * Melon or pumpkin stem.
 */
public class Stem extends AbstractModelBlock {

  private final int age;

  public Stem(String name, int age) {
    super(name, Texture.stemStraight);
    this.model = new StemModel(age & 7);
    this.age = age & 7;
  }

  @Override
  public String description() {
    return "age=" + age;
  }
}
