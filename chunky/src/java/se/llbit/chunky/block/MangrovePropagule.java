package se.llbit.chunky.block;

import se.llbit.chunky.model.MangrovePropaguleModel;
import se.llbit.chunky.resources.Texture;

public class MangrovePropagule extends AbstractModelBlock {
  private final int age;
  private final boolean hanging;

  public MangrovePropagule(int age, boolean hanging) {
    super("mangrove_propagule", Texture.mangrovePropagule);
    this.age = age;
    this.hanging = hanging;
    this.model = new MangrovePropaguleModel(age, hanging);
  }

  @Override
  public String description() {
    return "age=" + age + ", hanging=" + hanging;
  }
}
