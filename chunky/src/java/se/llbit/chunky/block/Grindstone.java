package se.llbit.chunky.block;

import se.llbit.chunky.model.GrindstoneModel;
import se.llbit.chunky.resources.Texture;

public class Grindstone extends AbstractModelBlock {

  private final String description;

  public Grindstone(String face, String facing) {
    super("grindstone", Texture.grindstoneSide);
    description = String.format("face=%s, facing=%s", face, facing);
    this.model = new GrindstoneModel(face, facing);
  }

  @Override
  public String description() {
    return description;
  }
}
