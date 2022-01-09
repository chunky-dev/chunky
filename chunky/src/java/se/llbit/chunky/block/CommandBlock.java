package se.llbit.chunky.block;

import se.llbit.chunky.model.DirectionalBlockModel;
import se.llbit.chunky.resources.Texture;

public class CommandBlock extends AbstractModelBlock {

  private final String description;

  public CommandBlock(String facing, boolean conditional) {
    super("command_block", Texture.commandBlockFront);
    this.description = String.format("facing=%s, conditional=%s", facing, conditional);
    this.model = new DirectionalBlockModel(facing,
        conditional ? Texture.commandBlockConditional : Texture.commandBlockFront,
        Texture.commandBlockBack, Texture.commandBlockSide);
    opaque = true;
  }

  @Override
  public String description() {
    return description;
  }
}
