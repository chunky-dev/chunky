package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class AmethystCluster extends SpriteBlock {

  private final boolean lit;

  public AmethystCluster(String name, Texture texture, String facing, boolean lit) {
    super(name, texture, facing);
    this.lit = lit;
  }

  public boolean isLit() {
    return lit;
  }

  @Override
  public String description() {
    return "facing=" + facing + ", lit=" + lit;
  }
}
