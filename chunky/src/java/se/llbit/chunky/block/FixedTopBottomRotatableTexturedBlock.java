package se.llbit.chunky.block;

import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.resources.Texture;

/**
 * A textured block that can have one of four orientations but have a fixed top and bottom that does NOT rotate.
 * E.g. chiseled bookshelves.
 * If the top and bottom should rotate with the rest of the block, use TopBottomOrientedTexturedBlock.
 */
public class FixedTopBottomRotatableTexturedBlock extends AbstractModelBlock {
  public FixedTopBottomRotatableTexturedBlock(String name, String facing, Texture front, Texture side, Texture top) {
    this(name, facing, front, side, side, side, top, top);
  }

  public FixedTopBottomRotatableTexturedBlock(String name, String facing, Texture front, Texture side, Texture top, Texture bottom) {
    this(name, facing, front, side, side, side, top, bottom);
  }

  public FixedTopBottomRotatableTexturedBlock(String name, String facing, Texture front, Texture south, Texture east, Texture west, Texture top, Texture bottom) {
    super(name, front);
    this.model = new TexturedBlockModel(TexturedBlockModel.Orientation.fromFacing(facing, true),
      front, east, south, west, top, bottom, null);
    solid = true;
    opaque = true;
  }
}
