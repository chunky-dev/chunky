package se.llbit.imageformats.tiff;

import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class IFDTag<SingleType, ArrayType> {
  final TagFieldType type;
  final short tagId;

  enum TagFieldType {
    ASCII(2, 1),
    SHORT(3, 2),
    LONG(4, 4),
    RATIONAL(5, 8);
    final short id;
    final int byteSize;

    TagFieldType(int id, int byteSize) {
      this.id = (short) id;
      this.byteSize = byteSize;
    }
  }

  IFDTag(TagFieldType type, short tagId) {
    this.type = type;
    this.tagId = tagId;
  }

  abstract int write(DataOutput out, SingleType data) throws IOException;

  int writeMultiple(DataOutput out, ArrayType data) throws IOException {
    throw new UnsupportedOperationException("cannot store multiple data entries");
  }

  abstract int valueCount(SingleType st, ArrayType at);

  /**
   * width / columns / pixels per scanline
   */
  static final ShortTag TAG_IMAGE_WIDTH = new ShortTag(0x0100);
  /**
   * height / rows / length / scanline count
   */
  static final ShortTag TAG_IMAGE_HEIGHT = new ShortTag(0x0101);
  static final ShortTag TAG_BITS_PER_SAMPLE = new ShortTag(0x0102);
  static final ShortTag TAG_SAMPLE_FORMAT = new ShortTag(0x0153);
  static final ShortTag TAG_EXTRA_SAMPLES = new ShortTag(0x0152);

  /**
   * defines details of subfile using 32 flag bits
   */
  static final LongTag TAG_NEW_SUBFILE_TYPE = new LongTag(0x00FE);

  static final ShortTag TAG_COMPRESSION_TYPE = new ShortTag(0x0103);
  static final ShortTag TAG_PHOTOMETRIC_INTERPRETATION = new ShortTag(0x0106);
  static final ShortTag TAG_PLANAR_CONFIGURATION = new ShortTag(0x011C);

  /**
   * number of rows in each strip (except possibly the last strip)
   */
  static final LongTag TAG_ROWS_PER_STRIP = new LongTag(0x0116);
  /**
   * for each strip, the byte offset of that strip
   */
  static final LongTag TAG_STRIP_OFFSETS = new LongTag(0x0111);
  /**
   * for each strip, the number of bytes in that strip after any compression
   */
  static final LongTag TAG_STRIP_BYTE_COUNTS = new LongTag(0x0117);
  static final ShortTag TAG_ORIENTATION = new ShortTag(0x0112);
  static final ShortTag TAG_SAMPLES_PER_PIXEL = new ShortTag(0x0115);

  static final RationalTag TAG_X_RESOLUTION = new RationalTag(0x011A);
  static final RationalTag TAG_Y_RESOLUTION = new RationalTag(0x011B);
  static final ShortTag TAG_RESOLUTION_UNIT = new ShortTag(0x0128);

  static final ASCIITag TAG_SOFTWARE = new ASCIITag(0x0131);
  static final ASCIITag TAG_DATETIME = new ASCIITag(0x0132);

  /**
   * 7-bit ASCII code, 0-terminated
   */
  static class ASCIITag extends IFDTag<String, Void> {
    ASCIITag(int tagID) {
      super(TagFieldType.ASCII, (short) tagID);
    }

    @Override
    int write(DataOutput out, String data) throws IOException {
      byte[] strBuf = data.getBytes(StandardCharsets.US_ASCII);
      out.write(strBuf);
      out.writeByte(0);
      return strBuf.length + 1;
    }

    @Override
    int valueCount(String st, Void at) {
      return st.getBytes(StandardCharsets.US_ASCII).length + 1;
    }
  }

  /**
   * 16-bit unsigned(!) integer
   */
  static class ShortTag extends IFDTag<Short, short[]> {
    ShortTag(int tagID) {
      super(TagFieldType.SHORT, (short) tagID);
    }

    @Override
    int write(DataOutput out, Short data) throws IOException {
      out.writeShort(data);
      return 2;
    }

    @Override
    int writeMultiple(DataOutput out, short[] data) throws IOException {
      for (short s : data) {
        out.writeShort(s);
      }
      return data.length * 2;
    }

    @Override
    int valueCount(Short st, short[] at) {
      return at != null ? at.length : 1;
    }
  }

  /**
   * 32-bit unsigned(!) integer
   */
  static class LongTag extends IFDTag<Integer, int[]> {
    LongTag(int tagID) {
      super(TagFieldType.LONG, (short) tagID);
    }

    @Override
    int write(DataOutput out, Integer data) throws IOException {
      out.writeInt(data);
      return 4;
    }

    @Override
    int writeMultiple(DataOutput out, int[] data) throws IOException {
      for (int i : data) {
        out.writeInt(i);
      }
      return data.length * 4;
    }

    @Override
    int valueCount(Integer st, int[] at) {
      return at != null ? at.length : 1;
    }
  }

  /**
   * fraction using:
   * - 32-bit unsigned(!) integer numerator
   * - 32-bit unsigned(!) integer denominator
   */
  static class RationalTag extends IFDTag<Void, int[]> {
    RationalTag(int tagID) {
      super(TagFieldType.RATIONAL, (short) tagID);
    }

    @Override
    int write(DataOutput out, Void data) {
      throw new UnsupportedOperationException("fraction requires numerator denominator pairs");
    }

    @Override
    int writeMultiple(DataOutput out, int[] numeratorDenominatorPairs) throws IOException {
      for (int nd : numeratorDenominatorPairs) {
        out.writeInt(nd);
      }
      return numeratorDenominatorPairs.length * 4;
    }

    @Override
    int valueCount(Void st, int[] numeratorDenominatorPairs) {
      if (numeratorDenominatorPairs.length % 2 != 0)
        throw new IllegalArgumentException("fraction requires pairs of numerators and denominators");
      return numeratorDenominatorPairs.length;
    }
  }
}
