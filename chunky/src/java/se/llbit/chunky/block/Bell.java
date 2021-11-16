package se.llbit.chunky.block;

import se.llbit.chunky.model.BellModel;
import se.llbit.chunky.resources.Texture;

public class Bell extends AbstractModelBlock {

  private final String description;

  public Bell(String facing, String attachment) {
    super("bell", Texture.bellBody);
    this.description = "attachment=" + attachment + ",facing=" + facing;
    model = new BellModel(facing, attachment);
  }

  @Override
  public String description() {
    return description;
  }
}
