package se.llbit.chunky.resources;

import se.llbit.chunky.resources.texture.AbstractNonlinearTexture;
import se.llbit.chunky.resources.texture.BitmapTexture;

public class ChiseledBookshelfTexture extends AbstractNonlinearTexture {
  // The texture when no books are present
  private AbstractNonlinearTexture empty;

  // The texture when all six books are present
  private AbstractNonlinearTexture full;

  // slot0 is top left, slot2 is top right, slot5 is bottom right
  private boolean slot0, slot1, slot2, slot3, slot4, slot5;

  public ChiseledBookshelfTexture(BitmapTexture empty, BitmapTexture full, boolean slot0, boolean slot1, boolean slot2, boolean slot3, boolean slot4, boolean slot5) {
    this.empty = empty;
    this.full = full;
    this.slot0 = slot0;
    this.slot1 = slot1;
    this.slot2 = slot2;
    this.slot3 = slot3;
    this.slot4 = slot4;
    this.slot5 = slot5;
  }
  private boolean bookPresentAt(int x, int y) {
    if(y * 2 < empty.getHeight()) {
      if(x * 3 < empty.getWidth()) {
        return slot0;
      } else if(x * 3 / 2 < empty.getWidth()) {
        return slot1;
      } else {
        return slot2;
      }
    } else {
      if(x * 3 < empty.getWidth()) {
        return slot3;
      } else if(x * 3 / 2 < empty.getWidth()) {
        return slot4;
      } else {
        return slot5;
      }
    }
  }

  @Override
  public int getWidth() {
    return empty.getWidth();
  }

  @Override
  public int getHeight() {
    return empty.getHeight();
  }

  @Override
  public float[] getAvgColorLinear() {
    return empty.getAvgColorLinear();
  }

  @Override
  public int getAvgColor() {
    return empty.getAvgColor();
  }

  @Override
  public int getTextureColorI(int x, int y) {
    if (bookPresentAt(x, y)) {
      return full.getTextureColorI(x, y);
    } else {
      return empty.getTextureColorI(x, y);
    }
  }
}
