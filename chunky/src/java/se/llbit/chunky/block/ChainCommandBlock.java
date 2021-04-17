package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.DirectionalBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class ChainCommandBlock extends MinecraftBlock implements ModelBlock {
  private final DirectionalBlockModel model;
  private final String description;

  public ChainCommandBlock(String facing, boolean conditional) {
    super("chain_command_block", Texture.commandBlockFront);
    this.description = String.format("facing=%s, conditional=%s", facing, conditional);
    this.model = new DirectionalBlockModel(facing,
        conditional ? Texture.chainCommandBlockConditional : Texture.chainCommandBlockFront,
        Texture.chainCommandBlockBack, Texture.chainCommandBlockSide);
    localIntersect = true;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
