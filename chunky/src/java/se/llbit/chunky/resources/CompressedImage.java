package se.llbit.chunky.resources;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import se.llbit.math.QuickMath;

/**
 * Image using a palette compression to reduce memory usage.
 * The size of the palette/number of bit each pixel takes
 * is computed based on the number of colors used in the image to compress
 */
public class CompressedImage implements Image {
  private int width;
  private int height;
  private int[] palette;
  private int bitDepth;
  private int pixelPerLong;
  private long[] compressedData;
  private long mask;

  CompressedImage(Image source) {
    compressFrom(source);
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public int getPixel(int x, int y) {
    return getPixel(y * width + x);
  }

  @Override
  public int getPixel(int index) {
    int longIndex = index / pixelPerLong;
    int indexInLong = index % pixelPerLong * bitDepth;
    int paletteIndex = (int) ((compressedData[longIndex] >>> indexInLong) & mask);
    return palette[paletteIndex];
  }

  @Override
  public BitmapImage asBitmap() {
    int pixelCount = width * height;
    int[] uncompressedData = new int[pixelCount];
    for(int i = 0; i < pixelCount; ++i) {
      uncompressedData[i] = getPixel(i);
    }
    return new BitmapImage(width, height, uncompressedData);
  }

  /**
   * Do the compression.
   * The input image is first analyzed to count the number of color and determine
   * the bit depth of the image.
   * Then the image is compressed by storing the palette indexes
   */
  private void compressFrom(Image source) {
    IntArrayList paletteBuilder = new IntArrayList();
    Int2IntOpenHashMap colorToIndex = new Int2IntOpenHashMap();

    width = source.getWidth();
    height = source.getHeight();
    int pixelCount = width * height;

    // Build the palette
    for(int i = 0; i < pixelCount; ++i) {
      int color = source.getPixel(i);
      if(!colorToIndex.containsKey(color)) {
        int colorIndex = paletteBuilder.size();
        paletteBuilder.add(color);
        colorToIndex.put(color, colorIndex);
      }
    }

    int colorCount = paletteBuilder.size();
    palette = paletteBuilder.toIntArray();
    paletteBuilder = null;
    bitDepth = QuickMath.log2(QuickMath.nextPow2(colorCount));
    if(bitDepth == 0)
      bitDepth = 1;
    pixelPerLong = 64 / bitDepth;
    mask = ((1L << bitDepth) - 1); // bitDepth 1s

    // build the image
    LongArrayList compressedDataBuilder = new LongArrayList();

    int pixelIndex = 0;
    while(pixelIndex < pixelCount) {
      long current = 0L;
      for(int i = 0; i < pixelPerLong && pixelIndex < pixelCount; ++i) {
        int color = source.getPixel(pixelIndex);
        long colorIndex = colorToIndex.get(color);
        current |= colorIndex  << (i * bitDepth);
        ++pixelIndex;
      }
      compressedDataBuilder.add(current);
    }
    compressedData = compressedDataBuilder.toLongArray();
  }
}
