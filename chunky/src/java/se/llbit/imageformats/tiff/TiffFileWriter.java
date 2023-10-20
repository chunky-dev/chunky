/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2015-2022 Chunky contributors
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.imageformats.tiff;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import se.llbit.chunky.main.Version;
import se.llbit.chunky.renderer.postprocessing.PixelPostProcessingFilter;
import se.llbit.chunky.renderer.postprocessing.PostProcessingFilter;
import se.llbit.chunky.renderer.postprocessing.PostProcessingFilters;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.log.Log;
import se.llbit.util.TaskTracker;

/**
 * Basic writer for the TIFF image format.
 * <p>Supports (only) 32-bit floating point channel output.
 * See the <a href="https://download.osgeo.org/libtiff/doc/TIFF6.pdf">format specification</a> for details.
 */
public class TiffFileWriter implements AutoCloseable {

  private final DataOutputStream out;

  public TiffFileWriter(OutputStream outputStream) throws IOException {
    out = new DataOutputStream(outputStream);
    // "MM\0*"
    // - MM -> magic bytes
    // - \0* -> magic number 42 for big-endian byte order
    out.writeInt(0x4D4D002A);
  }

  @Override
  public void close() throws IOException {
    out.close();
  }

  static class IFDWriter {
    /**
     * Tag structure (12 bytes):
     * - 2 bytes: tag ID
     * - 2 bytes: field type
     * - 4 bytes: value count, not byte count!
     * - 4 bytes: value if it fits otherwise address in file
     */
    List<ByteBuffer> tagEntries = new ArrayList<>();
    List<Long> unfinalizedTags = new ArrayList<>();
    ByteArrayOutputStream tagDataBuffer = new ByteArrayOutputStream(128);

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

    /**
     * Writes a string as 7-bit ASCII code, 0-terminated
     */
    public void addAsciiTagEntry(short tagID, String data) throws IOException {
      byte[] bytes = data.getBytes(StandardCharsets.US_ASCII);
      // append 0-terminator
      bytes = Arrays.copyOf(bytes, bytes.length+1);
      addTagEntry(tagID, TagFieldType.ASCII, bytes.length, bytes);
    }

    /**
     * Writes single 16-bit unsigned(!) integer
     */
    public void addShortTagEntry(short tagID, short data) throws IOException {
      ByteBuffer buf = ByteBuffer.allocate(12);
      buf.putShort(tagID);
      buf.putShort(TagFieldType.SHORT.id);
      buf.putInt(1);
      buf.putShort(data);
      buf.putShort((short) 0);
      tagEntries.add(buf);
    }

    /**
     * Writes multiple 16-bit unsigned(!) integer
     */
    public void addShortTagEntry(short tagID, short[] data) throws IOException {
      ByteBuffer buf = ByteBuffer.allocate(data.length*2);
      buf.asShortBuffer().put(data);
      addTagEntry(tagID, TagFieldType.SHORT, data.length, buf.array());
    }

    /**
     * Writes single 32-bit unsigned(!) integer
     */
    public void addLongTagEntry(short tagID, int data) throws IOException {
      ByteBuffer buf = ByteBuffer.allocate(12);
      buf.putShort(tagID);
      buf.putShort(TagFieldType.LONG.id);
      buf.putInt(1);
      buf.putInt(data);
      tagEntries.add(buf);
    }

    /**
     * Writes multiple 32-bit unsigned(!) integer
     */
    private void addLongTagEntry(short tagID, int[] data) throws IOException {
      ByteBuffer buf = ByteBuffer.allocate(data.length*4);
      buf.asIntBuffer().put(data);
      addTagEntry(tagID, TagFieldType.LONG, data.length, buf.array());
    }

    /**
     * Writes a fraction using an
     * - 32-bit unsigned(!) integer numerator
     * - 32-bit unsigned(!) integer denominator
     * @param numeratorDenominatorPairs interleaved array of fraction numerator and denominator [n,d,n,d,...]
     */
    private void addRationalTagEntry(short tagID, int[] numeratorDenominatorPairs) throws IOException {
      ByteBuffer buf = ByteBuffer.allocate(numeratorDenominatorPairs.length*4);
      buf.asIntBuffer().put(numeratorDenominatorPairs);
      addTagEntry(tagID, TagFieldType.RATIONAL, numeratorDenominatorPairs.length, buf.array());
    }

    private void addTagEntry(short tagID, TagFieldType fieldType, int valueCount, byte[] data) throws IOException {
      ByteBuffer buf = ByteBuffer.allocate(12);
      buf.putShort(tagID);
      buf.putShort(fieldType.id);
      buf.putInt(valueCount);
      if(valueCount * fieldType.byteSize <= 4) {
        // store in tag
        buf.put(data);
        // pad tag
        while(buf.position() < 12) {
          buf.put((byte) 0);
        }
      } else {
        // finalize later
        buf.putInt(0);
        int tagIndex = tagEntries.size();
        int bufferOffset = tagDataBuffer.size();
        unfinalizedTags.add((long) tagIndex << 32 | bufferOffset);
        tagDataBuffer.write(data);
      }
      tagEntries.add(buf);
    }

    /**
     * IFD structure:
     * - 2 bytes: tag count
     * - x * 12 bytes: tags
     * - 4 bytes: next IFD address
     */
    void write(DataOutputStream out, int expectedAddress, boolean lastIFD) throws IOException {
      assert(expectedAddress >= out.size());
      int padding = expectedAddress - out.size();
      out.write(new byte[padding]);

      // write tag count
      out.writeShort(tagEntries.size());

      // address for buffered data
      int addressAfterIFD = out.size() + tagEntries.size() * 12 + 4;
      // finalize tag addresses
      for(long unfinalizedTag : unfinalizedTags) {
        int unfinalizedTagIndex = (int) (unfinalizedTag >> 32);
        int bufferOffset = (int) unfinalizedTag;
        ByteBuffer tagEntry = tagEntries.get(unfinalizedTagIndex);
        tagEntry.putInt(8, addressAfterIFD + bufferOffset);
      }
      // sort tag entries by tagID
      tagEntries.sort(
        Comparator.comparingInt((ByteBuffer tagEntry) -> (int) tagEntry.getShort(0))
      );

      // write tags
      for(ByteBuffer tagEntry : tagEntries) {
        out.write(tagEntry.array());
      }
      // write pointer to next IFD
      if(!lastIFD) {
        int nextIFDAddress = addressAfterIFD + tagDataBuffer.size();
        if ((nextIFDAddress & 0b1) != 0) {
          // align to 16-bit address
          nextIFDAddress++;
        }
        out.writeInt(nextIFDAddress);
      } else {
        // zero-pointer represents last IFD
        out.writeInt(0);
      }
      out.flush();

      // write buffered data
      out.write(tagDataBuffer.toByteArray());
      out.flush();
    }

    /** width / columns / pixels per scanline */
    static final short TAG_IMAGE_WIDTH = 0x0100;
    /** height / rows / length / scanline count */
    static final short TAG_IMAGE_HEIGHT = 0x0101;
    static final short TAG_BITS_PER_SAMPLE = 0x0102;
    static final short TAG_SAMPLE_FORMAT = 0x0153;

    /** defines details of subfile using 32 flag bits */
    static final short TAG_NEW_SUBFILE_TYPE = 0x00FE;

    static final short TAG_COMPRESSION_TYPE = 0x0103;
    static final short TAG_PHOTOMETRIC_INTERPRETATION = 0x0106;
    static final short TAG_PLANAR_CONFIGURATION = 0x011C;

    /** number of rows in each strip (except possibly the last strip) */
    static final short TAG_ROWS_PER_STRIP = 0x0116;
    /** for each strip, the byte offset of that strip */
    static final short TAG_STRIP_OFFSETS = 0x0111;
    /** for each strip, the number of bytes in that strip after any compression */
    static final short TAG_STRIP_BYTE_COUNTS = 0x0117;
    static final short TAG_ORIENTATION = 0x0112;
    static final short TAG_SAMPLES_PER_PIXEL = 0x0115;

    static final short TAG_X_RESOLUTION = 0x011A;
    static final short TAG_Y_RESOLUTION = 0x011B;
    static final short TAG_RESOLUTION_UNIT = 0x0128;

    static final short TAG_SOFTWARE = 0x0131;
    static final short TAG_DATETIME = 0x0132;
  }

  private static final int BYTES_PER_SAMPLE = 4;
  private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

  /**
   * Export sample buffer as Baseline TIFF RGB image / TIFF Class R image
   * with 32 bits per color component.
   */
  public void export(Scene scene, TaskTracker.Task task) throws IOException {
    int width = scene.canvasWidth();
    int height = scene.canvasHeight();

    int pixelDataOffset = out.size() + 4; // header + ifd address
    int pixelDataByteCount = width * height * 3 * BYTES_PER_SAMPLE;
    int ifdOffset = pixelDataOffset + pixelDataByteCount;
    out.writeInt(ifdOffset);

    writePixelData(width, height, scene, task);

    IFDWriter idf = new IFDWriter();
    // RGB full color
    idf.addShortTagEntry(IFDWriter.TAG_PHOTOMETRIC_INTERPRETATION, (short) 2);
    // Store pixel components contiguously [RGBRGBRGB...]
    idf.addShortTagEntry(IFDWriter.TAG_PLANAR_CONFIGURATION, (short) 1);
    // Number of components per pixel (R, G, B)
    idf.addShortTagEntry(IFDWriter.TAG_SAMPLES_PER_PIXEL, (short) 3);

    assert(width <= Short.MAX_VALUE);
    idf.addShortTagEntry(IFDWriter.TAG_IMAGE_WIDTH, (short) width);
    assert(height <= Short.MAX_VALUE);
    idf.addShortTagEntry(IFDWriter.TAG_IMAGE_HEIGHT, (short) height);
    short bitsPerSample = (short) (8 * BYTES_PER_SAMPLE);
    idf.addShortTagEntry(IFDWriter.TAG_BITS_PER_SAMPLE, new short[]{ bitsPerSample, bitsPerSample, bitsPerSample });
    // Interpret each component as IEEE754 float32
    idf.addShortTagEntry(IFDWriter.TAG_SAMPLE_FORMAT, new short[]{ 3, 3, 3 });

    // No compression, but pack data into bytes as tightly as possible, leaving no unused
    // bits (except at the end of a row). The component values are stored as an array of
    // type BYTE. Each scan line (row) is padded to the next BYTE boundary.
    idf.addShortTagEntry(IFDWriter.TAG_COMPRESSION_TYPE, (short) 1);

    // "Compressed or uncompressed image data can be stored almost anywhere in a
    // TIFF file. TIFF also supports breaking an image into separate strips for increased
    // editing flexibility and efficient I/O buffering."
    // We will use exactly 1 strip, therefore the relevant tags have only 1 entry.
    // All rows in 1 strip
    idf.addLongTagEntry(IFDWriter.TAG_ROWS_PER_STRIP, height);
    // Absolute strip address
    idf.addLongTagEntry(IFDWriter.TAG_STRIP_OFFSETS, pixelDataOffset);
    // Strip length
    idf.addLongTagEntry(IFDWriter.TAG_STRIP_BYTE_COUNTS, pixelDataByteCount);
    // The 0th row represents the visual top of the image, and the 0th column represents the visual left-hand side.
    idf.addShortTagEntry(IFDWriter.TAG_ORIENTATION, (short) 1);

    // Image does not have a physical size
    idf.addShortTagEntry(IFDWriter.TAG_RESOLUTION_UNIT, (short) 1); // not an absolute unit
    idf.addRationalTagEntry(IFDWriter.TAG_X_RESOLUTION, new int[]{ 1, 1 });
    idf.addRationalTagEntry(IFDWriter.TAG_Y_RESOLUTION, new int[]{ 1, 1 });

    idf.addAsciiTagEntry(IFDWriter.TAG_SOFTWARE, "Chunky " + Version.getVersion());
    idf.addAsciiTagEntry(IFDWriter.TAG_DATETIME, DATETIME_FORMAT.format(LocalDateTime.now()));

    idf.write(out, ifdOffset, true);
  }

  private void writePixelData(int width, int height, Scene scene, TaskTracker.Task task) throws IOException {
    PixelPostProcessingFilter filter = requirePixelPostProcessingFilter(scene);
    double[] sampleBuffer = scene.getSampleBuffer();
    double[] pixelBuffer = new double[3];
    for (int y = 0; y < height; ++y) {
      task.update(height, y);
      for (int x = 0; x < width; ++x) {
        // TODO: refactor pixel access to remove duplicate post processing code from here
        filter.processPixel(width, height, sampleBuffer, x, y, scene.getExposure(), pixelBuffer);
        out.writeFloat((float) pixelBuffer[0]);
        out.writeFloat((float) pixelBuffer[1]);
        out.writeFloat((float) pixelBuffer[2]);
      }
    }
    out.flush();
    task.update(height, height);
  }

  private PixelPostProcessingFilter requirePixelPostProcessingFilter(Scene scene) {
    PostProcessingFilter filter = scene.getPostProcessingFilter();
    if (filter instanceof PixelPostProcessingFilter) {
      // TODO: use https://openjdk.java.net/jeps/394
      return (PixelPostProcessingFilter) filter;
    } else {
      Log.warn("The selected post processing filter (" + filter.getName()
        + ") doesn't support pixel based processing and can't be used to export TIFF files. " +
        "The TIFF will be exported without post-processing instead.");
      return PostProcessingFilters.NONE;
    }
  }
}
