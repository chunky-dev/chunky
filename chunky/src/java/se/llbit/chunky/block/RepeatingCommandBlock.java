package se.llbit.chunky.block;

import se.llbit.chunky.model.DirectionalBlockModel;
import se.llbit.chunky.resources.Texture;

public class RepeatingCommandBlock extends AbstractModelBlock {

  private final String description;

  public RepeatingCommandBlock(String facing, boolean conditional) {
    super("repeating_command_block", Texture.commandBlockFront);
    this.description = String.format("facing=%s, conditional=%s", facing, conditional);
    this.model = new DirectionalBlockModel(facing,
        conditional ? Texture.chainCommandBlockConditional : Texture.chainCommandBlockFront,
        Texture.chainCommandBlockBack, Texture.chainCommandBlockSide);
    opaque = true;
    solid = true;
  }

  @Override
  public String description() {
    return description;
  }
}
