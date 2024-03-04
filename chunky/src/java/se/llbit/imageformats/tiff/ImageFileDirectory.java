package se.llbit.imageformats.tiff;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

abstract class ImageFileDirectory {

  private final List<TagEntryData<?, ?>> tagEntries = new ArrayList<>(20);
  private final ByteArrayOutputStream tagDataBuffer = new ByteArrayOutputStream(72);
  private final DataOutput tagDataBufferOutput = new DataOutputStream(tagDataBuffer);

  <SingleType, ArrayType> void addTag(IFDTag<SingleType, ArrayType> tag, SingleType data) {
    tagEntries.add(new TagEntryData<>(tag, data));
  }
  <SingleType, ArrayType> void addMultiTag(IFDTag<SingleType, ArrayType> tag, ArrayType data) {
    tagEntries.add(new TagEntryData<>(tag, data, null));
  }
  private class TagEntryData<SingleType, ArrayType> implements Comparable<TagEntryData<?, ?>> {
    final IFDTag<SingleType, ArrayType> tag;
    final SingleType st;
    final ArrayType at;

    @Override
    public int compareTo(TagEntryData<?, ?> o) {
      return Short.compareUnsigned(tag.tagId, o.tag.tagId);
    }
    TagEntryData(IFDTag<SingleType, ArrayType> tag, SingleType st) {
      this.tag = tag;
      this.st = st;
      this.at = null;
    }
    TagEntryData(IFDTag<SingleType, ArrayType> tag, ArrayType at, Void unused) {
      this.tag = tag;
      this.st = null;
      this.at = at;
    }

    int writeHeader(
      FinalizableBFCOutputStream out
    ) throws IOException {
      out.writeShort(tag.tagId);
      out.writeShort(tag.type.id);
      int valueCount = tag.valueCount(st, at);
      out.writeInt(valueCount);
      return valueCount;
    }

    void writeData(
      FinalizableBFCOutputStream out,
      int valueCount,
      int bufferAddress
    ) throws IOException {
      if (valueCount * tag.type.byteSize <= 4) {
        // store in tag
        int byteCount = writeTagData(out);
        // pad tag
        out.skip(4 - byteCount);
      } else {
        // store in buffer
        int bufferDataAddress = bufferAddress + tagDataBuffer.size();
        out.writeInt(bufferDataAddress);
        int byteCount = writeTagData(tagDataBufferOutput);
        // pad address
        if (byteCount % 2 != 0)
          tagDataBufferOutput.writeByte(0);
      }
    }

    private int writeTagData(DataOutput out) throws IOException {
      if(at == null) {
        return tag.write(out, st);
      } else {
        return tag.writeMultiple(out, at);
      }
    }
  }

  /**
   * IFD structure:
   * - 2 bytes: tag count
   * - x * 12 bytes: tags
   * - 4 bytes: next IFD address
   */
  FinalizableBFCOutputStream.UnfinalizedData.Int write(
    FinalizableBFCOutputStream out,
    FinalizableBFCOutputStream.UnfinalizedData.Int ifdPointer,
    PixelDataWriter writer
  ) throws IOException {
    out.ensureAlignment();
    // update current pointer to location
    ifdPointer.data = (int) out.position();

    // Absolute strip address
    addTag(IFDTag.TAG_STRIP_OFFSETS, 0);
    FinalizableBFCOutputStream.UnfinalizedData.Int pixelDataPointer = null;
    // Strip length
    addTag(IFDTag.TAG_STRIP_BYTE_COUNTS, 0);
    FinalizableBFCOutputStream.UnfinalizedData.Int pixelDataByteCount = null;

    // write tag count
    int tagCount = tagEntries.size();
    out.writeShort(tagCount);

    // address for extended header data
    int bufferAddress = (int) out.position() + tagCount * 12 + 4;

    // write tags
    tagEntries.sort(Comparator.naturalOrder());
    IFDTag<?, ?> lastTag = null;
    for (TagEntryData<?, ?> tagEntry : tagEntries) {
      if(lastTag == tagEntry.tag)
        throw new IllegalStateException("duplicate IFD tag");
      lastTag = tagEntry.tag;

      int valueCount = tagEntry.writeHeader(out);
      if(tagEntry.tag == IFDTag.TAG_STRIP_OFFSETS) {
        pixelDataPointer = out.writeUnfinalizedInt();
      } else if(tagEntry.tag == IFDTag.TAG_STRIP_BYTE_COUNTS) {
        pixelDataByteCount = out.writeUnfinalizedInt();
      } else {
        tagEntry.writeData(out, valueCount, bufferAddress);
      }
    }
    // write pointer to next IFD
    FinalizableBFCOutputStream.UnfinalizedData.Int nextIFDPointer = out.writeUnfinalizedInt();

    // write extended header data
    tagDataBuffer.writeTo(out);
    out.flush();

    // write pixel data
    out.ensureAlignment();
    assert pixelDataPointer != null;
    pixelDataPointer.setData((int) out.position());
    writePixelData(out, writer);
    assert pixelDataByteCount != null;
    pixelDataByteCount.setData((int) out.position() - pixelDataPointer.getData());

    return nextIFDPointer;
  }

  abstract void writePixelData(
    FinalizableBFCOutputStream out,
    PixelDataWriter writer
  ) throws IOException;

  @FunctionalInterface
  interface PixelDataWriter {
    void writePixelData(DataOutput out) throws IOException;
  }
}
