package se.llbit.chunky.block;

import se.llbit.chunky.model.ButtonModel;
import se.llbit.chunky.resources.Texture;

public class Button extends AbstractModelBlock {

  private final String description;
  // TODO(llbit): render powered buttons

  public Button(String name, Texture texture, String face, String facing, boolean powered) {
    super(name, texture);
    this.description = String.format("face=%s, facing=%s, powered=%s",
        face, facing, powered);
    this.model = new ButtonModel(face, facing, texture);
    // TODO handle rotation on top/bottom positions!
  }

  @Override
  public String description() {
    return description;
  }
}
