package se.llbit.chunky.block;

import se.llbit.chunky.model.SunFlowerModel;
import se.llbit.chunky.resources.Texture;

// TODO: refactor me!
// TODO: render the sunflower actually facing the sun.
public class Sunflower extends AbstractModelBlock {

  public Sunflower(String half) {
    super("sunflower",
        half.equals("upper")
            ? Texture.sunflowerTop
            : Texture.sunflowerBottom);
    solid = false;
    model = new SunFlowerModel(half.equals("upper"));
  }
}
