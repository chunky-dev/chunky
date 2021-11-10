package se.llbit.chunky.block;

import se.llbit.chunky.model.OrientedTexturedBlockModel;
import se.llbit.chunky.resources.Texture;

/**
 * A textured block that can have one of six orientations, e.g. barrels.
 */
public class OrientedTexturedBlock extends AbstractModelBlock {

  private final String facing;

  public OrientedTexturedBlock(String name, String facing, Texture side, Texture top,
      Texture bottom) {
    this(name, facing, side, side, side, side, top, bottom);
  }

  public OrientedTexturedBlock(String name, String facing, Texture north, Texture south,
      Texture east, Texture west, Texture top, Texture bottom) {
    super(name, north);
    opaque = true;
    model = new OrientedTexturedBlockModel(facing, north, east, south, west, top, bottom);
    this.facing = facing;
  }

  @Override
  public String description() {
    return "facing=" + facing;
  }
}
