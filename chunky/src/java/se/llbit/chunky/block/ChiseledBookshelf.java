package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class ChiseledBookshelf extends FixedTopBottomRotatableTexturedBlock {

  private final String description;

  public ChiseledBookshelf(String facing, boolean slot0, boolean slot1, boolean slot2, boolean slot3, boolean slot4, boolean slot5) {
      super("chiseled_bookshelf", facing, Texture.chiseledBookshelfCombinations[(slot0?1:0) + (slot1?2:0) + (slot2?4:0) + (slot3?8:0) + (slot4?16:0) + (slot5?32:0)],
        Texture.chiseledBookshelfSide, Texture.chiseledBookshelfTop);
    this.description = String.format("facing=%s, slot0=%s, slot1=%s, slot2=%s, slot3=%s, slot4=%s, slot5=%s", facing, slot0, slot1, slot2, slot3, slot4, slot5);
  }

  @Override
  public String description() {
    return description;
  }
}
