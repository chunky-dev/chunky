package se.llbit.chunky.resources;

import se.llbit.math.ColorUtil;

public class ChiseledBookshelfTexture extends Texture {
  // The texture when no books are present
  private Texture empty;

  // The texture when all six books are present
  private Texture full;

  // slot0 is top left, slot2 is top right, slot5 is bottom right
  private boolean slot0, slot1, slot2, slot3, slot4, slot5;

  public ChiseledBookshelfTexture(Texture empty, Texture full, boolean slot0, boolean slot1, boolean slot2, boolean slot3, boolean slot4, boolean slot5) {
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
    if(y * 2 < empty.image.height) {
      if(x * 3 < empty.image.width) {
        return slot0;
      } else if(x * 3 / 2 < empty.image.width) {
        return slot1;
      } else {
        return slot2;
      }
    } else {
      if(x * 3 < empty.image.width) {
        return slot3;
      } else if(x * 3 / 2 < empty.image.width) {
        return slot4;
      } else {
        return slot5;
      }
    }
  }
  @Override
  public int[] getData() {
    int[] overlaidData = new int[image.data.length];
    for(int i = 0; i < overlaidData.length; i++) {
      int x = i % empty.image.width;
      int y = i / empty.image.height;
      boolean shouldOverlay = bookPresentAt(x, y);
      overlaidData[i] = shouldOverlay ? full.image.data[i] : empty.image.data[i];
    }
    return overlaidData;
  }
  @Override
  public float[] getColor(int x, int y) {
    if(useAverageColor)
      return empty.getAvgColorFlat();
    float[] result = new float[4];
    boolean shouldOverlay = bookPresentAt(x, y);
    if(shouldOverlay) {
      ColorUtil.getRGBAComponentsGammaCorrected(full.image.data[width * y + x], result);
    } else {
      ColorUtil.getRGBAComponentsGammaCorrected(empty.image.data[width * y + x], result);
    }
    return result;
  }
}
