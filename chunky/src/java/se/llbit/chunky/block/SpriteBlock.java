package se.llbit.chunky.block;

import se.llbit.chunky.model.SpriteModel;
import se.llbit.chunky.resources.Texture;

public class SpriteBlock extends AbstractModelBlock {

  protected String facing;

  public SpriteBlock(String name, Texture texture) {
    super(name, texture);
    solid = false;
    model = new SpriteModel(texture);
  }

  public SpriteBlock(String name, Texture texture, String facing) {
    super(name, texture);
    solid = false;
    model = new SpriteModel(texture, facing);
    this.facing = facing;
  }

  @Override
  public String description() {
    if (facing != null) {
      return "facing=" + facing;
    }
    return super.description();
  }
}
