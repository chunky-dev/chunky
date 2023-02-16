package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class ChiseledBookshelf extends TopBottomOrientedTexturedBlock {

  private final String description;

  public ChiseledBookshelf(String facing) {
    super("chiseled_bookshelf", facing, Texture.chiseledBookshelfEmpty,
        Texture.chiseledBookshelfSide, Texture.chiseledBookshelfTop);
    this.description = String.format("facing=%s", facing);
  }

  @Override
  public String description() {
    return description;
  }
}
