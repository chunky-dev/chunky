package se.llbit.chunky.block;

import se.llbit.chunky.model.BambooModel;
import se.llbit.chunky.resources.Texture;

public class Bamboo extends AbstractModelBlock {

  private final String description;

  public Bamboo(int age, String leaves) {
    super("bamboo", Texture.bambooStalk);
    description = "age=" + age + ", leaves=" + leaves;
    model = new BambooModel(age, leaves);
  }

  @Override
  public String description() {
    return description;
  }
}
