package se.llbit.chunky.block;

import se.llbit.chunky.model.DirectionalBlockModel;
import se.llbit.chunky.resources.Texture;

public class ChainCommandBlock extends AbstractModelBlock {

  private final String description;

  public ChainCommandBlock(String facing, boolean conditional) {
    super("chain_command_block", Texture.commandBlockFront);
    this.description = String.format("facing=%s, conditional=%s", facing, conditional);
    this.model = new DirectionalBlockModel(facing,
        conditional ? Texture.chainCommandBlockConditional : Texture.chainCommandBlockFront,
        Texture.chainCommandBlockBack, Texture.chainCommandBlockSide);
    opaque = true;
  }

  @Override
  public String description() {
    return description;
  }
}
