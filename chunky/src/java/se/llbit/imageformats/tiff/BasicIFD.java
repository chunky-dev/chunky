package se.llbit.imageformats.tiff;

import java.io.IOException;

public class BasicIFD extends ImageFileDirectory {
  final CompressionType compressionType;

  public BasicIFD(
    int width, int height,
    CompressionType compressionType
  ) throws IOException {
    this.compressionType = compressionType;

    // RGB full color
    addTag(IFDTag.TAG_PHOTOMETRIC_INTERPRETATION, (short) 2);
    // Store pixel components contiguously [RGBRGBRGB...]
    addTag(IFDTag.TAG_PLANAR_CONFIGURATION, (short) 1);

    assert (width <= Short.MAX_VALUE);
    addTag(IFDTag.TAG_IMAGE_WIDTH, (short) width);
    assert (height <= Short.MAX_VALUE);
    addTag(IFDTag.TAG_IMAGE_HEIGHT, (short) height);
    // The 0th row represents the visual top of the image, and the 0th column represents the visual left-hand side.
    addTag(IFDTag.TAG_ORIENTATION, (short) 1);

    // No compression, but pack data into bytes as tightly as possible, leaving no unused
    // bits (except at the end of a row). The component values are stored as an array of
    // type BYTE. Each scan line (row) is padded to the next BYTE boundary.
    addTag(IFDTag.TAG_COMPRESSION_TYPE, compressionType.id);

    // Image does not have a physical size
    addTag(IFDTag.TAG_RESOLUTION_UNIT, (short) 1); // not an absolute unit
    addMultiTag(IFDTag.TAG_X_RESOLUTION, new int[]{1, 1});
    addMultiTag(IFDTag.TAG_Y_RESOLUTION, new int[]{1, 1});

    // "Compressed or uncompressed image data can be stored almost anywhere in a
    // TIFF file. TIFF also supports breaking an image into separate strips for increased
    // editing flexibility and efficient I/O buffering."
    // We will use exactly 1 strip, therefore the relevant tags have only 1 entry with all rows in 1 strip.
    addTag(IFDTag.TAG_ROWS_PER_STRIP, height);
  }

  @Override
  void writePixelData(
    FinalizableBFCOutputStream out,
    PixelDataWriter writer
  ) throws IOException {
    compressionType.writePixelData(out, writer);
  }
}
