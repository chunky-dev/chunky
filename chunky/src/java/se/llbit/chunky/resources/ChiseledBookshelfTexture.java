package se.llbit.chunky.resources;

import se.llbit.math.ColorUtil;

public class ChiseledBookshelfTexture extends Texture {
  // The texture when no books are present
  private Texture empty;

  // The texture when all six books are present
  private Texture full;

  // book1 is top left, book3 is top right, book6 is bottom right
  private boolean book1, book2, book3, book4, book5, book6;

  public ChiseledBookshelfTexture(Texture empty, Texture full, boolean book1, boolean book2, boolean book3, boolean book4, boolean book5, boolean book6) {
    this.empty = empty;
    this.full = full;
    this.book1 = book1;
    this.book2 = book2;
    this.book3 = book3;
    this.book4 = book4;
    this.book5 = book5;
    this.book6 = book6;
  }
  private boolean bookPresentAt(int x, int y) {
    if(y * 2 < empty.image.height) {
      if(x * 3 < empty.image.width) {
        return book1;
      } else if(x * 3 / 2 < empty.image.width) {
        return book2;
      } else {
        return book3;
      }
    } else {
      if(x * 3 < empty.image.width) {
        return book4;
      } else if(x * 3 / 2 < empty.image.width) {
        return book5;
      } else {
        return book6;
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
    if(empty.usesAverageColor())
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
