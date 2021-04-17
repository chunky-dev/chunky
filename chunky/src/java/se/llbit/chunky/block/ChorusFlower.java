package se.llbit.chunky.block;

import se.llbit.chunky.model.ChorusFlowerModel;
import se.llbit.chunky.resources.Texture;

public class ChorusFlower extends AbstractModelBlock {

  private final int age;

  public ChorusFlower(int age) {
    super("chorus_flower", Texture.chorusFlower);
    this.model = new ChorusFlowerModel(age % 6);
    this.age = age % 6;
  }

  @Override
  public String description() {
    return "age=" + age;
  }
}
