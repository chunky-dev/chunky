/* Copyright (c) 2021 Chunky contributors
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
package se.llbit.chunky.renderer.renderdump;

import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.renderer.scene.SampleBuffer;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.IntBoundingBox;
import se.llbit.util.TaskTracker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.util.function.LongConsumer;

/**
 * This is a dump format for chunky that should allow for storing of SPP per pixel in addition to actual sample values,
 * as well as supporting dumps that only contain data for an area that is not the whole image.
 *
 * TODO: - Possibly deflate + huffman coding of spp values? That should have very good compression
 *         (~<50 bytes for entirety of SPP for all cases but the antithetical cases)
 *         BUT: splitting spp from interleaved makes it harder to merge with O(1) memory.
 *         POSSIBLE SOLUTION: deflate SPP counts before samples, which will lead to tiny storage for SPP, which can be
 *                            pulled from as needed.
 */
class UncompressedSppDump extends DumpFormat {

  public static final DumpFormat INSTANCE = new UncompressedSppDump();

  // These must be 3 characters each. Just because.
  public static final String SECTION_HEADER_SAMPLES = "sam";
  // SECTION_HEADER_SPP has been joined into SECTION_HEADER_SAMPLES to make merging more memory efficient.
  //public static final String SECTION_HEADER_SPP = "spp";
  public static final String SECTION_HEADER_EOF = "dun";

  private UncompressedSppDump() {
  }

  @Override
  public void save(DataOutputStream outputStream, Scene scene, TaskTracker taskTracker) throws IOException {
    IntBoundingBox pixelRange;
    SampleBuffer samples = scene.getSampleBuffer();

    if(scene.spp<=0)
      throw new NoDataException();

    //****HEADER****//
    try (TaskTracker.Task task = taskTracker.task("Saving render dump - Header", 1)) {
      pixelRange = calculateRange(scene.getSampleBuffer());
      task.update(1);

      // Render size:
      outputStream.writeInt(scene.renderWidth());
      outputStream.writeInt(scene.renderHeight());
      // Saved region's dimensions:
      outputStream.writeInt(pixelRange.xmin + scene.crop_x);
      outputStream.writeInt(pixelRange.zmin + scene.crop_y);
      outputStream.writeInt(pixelRange.widthX());
      outputStream.writeInt(pixelRange.widthZ());
      // Scene spp range & render time
      outputStream.writeInt(scene.spp); // TODO: store spp min and max instead of only one value
      outputStream.writeInt(scene.spp); // TODO: store spp min and max instead of only one value
      outputStream.writeLong(scene.renderTime);

      // For future use; flags for gzip'd data / SPP interleaving or as an array?
      long saveflags = 0x0000000000000000L;
      outputStream.writeLong(saveflags);

      if (pixelRange.widthX()<=0 || pixelRange.widthZ()<=0)
        throw new NoDataException();
    }

    //****SAMPLES****//
    outputStream.writeChars(SECTION_HEADER_SAMPLES);
    int taskCoef = pixelRange.widthX();
    try (TaskTracker.Task task = taskTracker.task("Saving render dump - Samples", taskCoef * pixelRange.widthZ())) {

      // compile and write each row as tuples of <red, green, blue, spp> in a byte buffer.
      // Have enough space for an entire row of pixels which have 3 doubles and an integer per pixel.
      ByteBuffer bb = ByteBuffer.allocate(3*(pixelRange.xmax-pixelRange.xmin)*Double.BYTES+samples.rowSizeSpp*Integer.BYTES);
      for (int y = pixelRange.zmin; y < pixelRange.zmax; y++) {
        for (int x = pixelRange.xmin; x < pixelRange.xmax; x++) {
          bb.putDouble(samples.get(x, y, 0));
          bb.putDouble(samples.get(x, y, 1));
          bb.putDouble(samples.get(x, y, 2));
          bb.putInt(samples.getSpp(x, y));
        }
        // Write the compiled row
        outputStream.write(bb.array());
        // Reset buffer so it can be reused.
        bb.rewind();
        task.update(taskCoef * (y - pixelRange.zmin));
      }
    }

    outputStream.writeChars(SECTION_HEADER_EOF);
    outputStream.close();
  }

  @Override
  public void load(DataInputStream inputStream, Scene scene, TaskTracker taskTracker) throws IOException {
    SampleBuffer samples;
    int width, height;

    try (TaskTracker.Task task = taskTracker.task("Loading render dump - Header", 1)) {
      int renderWidth = inputStream.readInt();
      int renderHeight = inputStream.readInt();

      if (renderWidth != scene.renderWidth() || renderHeight != scene.renderHeight()) {
        throw new IllegalStateException("Scene size does not match dump size");
      }

      scene.crop_x = inputStream.readInt();
      scene.crop_y = inputStream.readInt();
      width = scene.subareaWidth = inputStream.readInt();
      height = scene.subareaHeight = inputStream.readInt();

      // This was already called by load scene, if crop sizes in dump were accurate.
      scene.initBuffers();
      samples = scene.getSampleBuffer();

      int sppmin = inputStream.readInt();
      scene.spp = sppmin; // TODO: store spp min and max instead of only one value
      int sppmax = inputStream.readInt();
      scene.spp = sppmax; // TODO: store spp min and max instead of only one value

      scene.renderTime = inputStream.readLong();

      // For future use; flags for gzip'd data / SPP interleaving or as an array?
      long flags = inputStream.readLong();
    }

    if (!("" + inputStream.readChar() + inputStream.readChar() + inputStream.readChar()).equals(SECTION_HEADER_SAMPLES))
      throw new StreamCorruptedException("Expected Sample Marker");

    ByteBuffer bb = ByteBuffer.allocate(width*Double.BYTES*3+width*Integer.BYTES);
    int taskCoef = scene.renderWidth();
    try (TaskTracker.Task task = taskTracker.task("Loading render dump - Samples", taskCoef * scene.renderHeight())) {
      for (int y = 0; y < height; y++) {
        inputStream.readFully(bb.array(), 0, bb.capacity());
        bb.rewind();
        for (int x = 0; x < width; x++) {
          samples.setPixel(x, y, bb.getDouble(), bb.getDouble(), bb.getDouble());
          samples.setSpp(x, y, bb.getInt());
        }
        bb.rewind();
        task.update(taskCoef * y);
      }
    }

    if (!("" + inputStream.readChar() + inputStream.readChar() + inputStream.readChar()).equals(SECTION_HEADER_EOF))
      throw new StreamCorruptedException("Expected Done Marker");

    inputStream.close();
  }

  @Override
  public void merge(DataInputStream inputStream, Scene scene, TaskTracker taskTracker)
      throws IOException, IllegalStateException {
    // Didnt want to duplicate all that code, so going to try just having 3 sample buffers at one time, instead of 2...
    // (thought it could be 0 if we merge from two files directly into a third file, but it isnt setup to do that atm.)

    // If safe, a copy of the scene is stored so that if the merge fails it can be reverted and nothing will be lost.
    // Unsafe may lose all progress, so it is recommended to save first.
    Boolean mergeSafely = false;

    int ox, oy, ow, oh, omx, omy; // "Other ..." crop_x, crop_y, width, height, max_x, max_y
    int sx, sy, sw, sh, smx, smy; // "Scene ..." crop_x, crop_y, width, height, max_x, max_y
    int fx, fy, fw, fh, fmx, fmy; // "Final ..." crop_x, crop_y, width, height, max_x, max_y
    SampleBuffer ss, fs;

    try (TaskTracker.Task task = taskTracker.task("Merging - Loading render dump - Header", 1)) {
      int renderWidth = inputStream.readInt();
      int renderHeight = inputStream.readInt();

      if (scene.width != renderWidth || scene.height != renderHeight)
        throw new Error("Failed to merge render dump - wrong canvas size.");

      ox = inputStream.readInt();
      oy = inputStream.readInt();
      ow = inputStream.readInt();
      oh = inputStream.readInt();
      omx = ox+ow;
      omy = oy+oh;

      sx = scene.crop_x;
      sy = scene.crop_y;
      sw = scene.subareaWidth;
      sh = scene.subareaHeight;
      smx = sx+sw;
      smy = sy+sh;

      fx = FastMath.min(sx, ox);
      fy = FastMath.min(sy, oy);
      fmx = FastMath.max(smx, omx);
      fmy = FastMath.max(smy, omy);
      fw = fmx-fx;
      fh = fmy-fy;

      ss = scene.getSampleBuffer();
      if (fx!=sx || fy!=sy || fmx!=smx || fmy!=smy) {
        scene.crop_x = fx;
        scene.crop_y = fy;
        scene.subareaWidth = fw;
        scene.subareaHeight = fh;
      }
      scene.initBuffers();
      fs = scene.getSampleBuffer();

      int sppmin = inputStream.readInt();
//      scene.spp_min += sppmin; // TODO: store spp min and max instead of only one value
      int sppmax = inputStream.readInt();
      scene.spp += sppmax; // TODO: store spp min and max instead of only one value

      scene.renderTime += inputStream.readLong();

      // For future use; flags for gzip'd data / SPP interleaving or as an array?
      long flags = inputStream.readLong();
    }

    try (TaskTracker.Task task = taskTracker.task("Merging - Copying old render dump", 1)) {
      fs.copyPixels(ss, 0, 0, sx-fx, sy-fy, sw, sh);
    }

    if (!("" + inputStream.readChar() + inputStream.readChar() + inputStream.readChar()).equals(SECTION_HEADER_SAMPLES))
      throw new StreamCorruptedException("Merging - Expected Sample Marker");

    ByteBuffer bb = ByteBuffer.allocate(ow*Double.BYTES*3+ow*Integer.BYTES);
    int taskCoef = ow;
    try (TaskTracker.Task task = taskTracker.task("Merging - Loading render dump - Samples", taskCoef * oh)) {
      for (int y = 0; y < oh; y++) {
        inputStream.readFully(bb.array(), 0, bb.capacity());
        bb.rewind();

        for (int x=0; x<ow; x++)
          fs.mergeSamples(x+ox-fx, y+oy-fy, bb.getDouble(), bb.getDouble(), bb.getDouble(), bb.getInt());

        bb.rewind();
        task.update(taskCoef * y);
      }
    }

    if (!("" + inputStream.readChar() + inputStream.readChar() + inputStream.readChar()).equals(SECTION_HEADER_EOF))
      throw new StreamCorruptedException("Merging - Expected Done Marker");
  }





  private IntBoundingBox calculateRange(SampleBuffer sampleBuffer) {

    // On a full render, each loop will exit on first index tested, and will complete in constant time.

    // Find highest pixel...
    IntBoundingBox ret = new IntBoundingBox();
    boolean done = false;
    for (int y = 0; y < sampleBuffer.rowCountSpp && !done; y++)
      for (int x = 0; x < sampleBuffer.rowSizeSpp && !done; x++)
        if (sampleBuffer.getSpp(x, y) > 0) {
          ret.include(x, y);
          done = true;
        }

    // If no pixel found, dump can be empty.
    // TODO: if maxSPP in scene is 0, can return this directly without traversal.
    if (ret.equals(new IntBoundingBox()))
      return ret.include(0, 0);

    // Find lowest pixel...
    done = false;
    for (int y = sampleBuffer.rowCountSpp - 1; y >= 0 && !done; y--)
      for (int x = 0; x < sampleBuffer.rowSizeSpp && !done; x++)
        if (sampleBuffer.getSpp(x, y) > 0) {
          ret.include(x, y);
          done = true;
        }

    // Find leftmost pixel...
    done = false;
    for (int x = 0; x < sampleBuffer.rowSizeSpp && !done; x++)
      for (int y = 0; y < sampleBuffer.rowCountSpp && !done; y++)
        if (sampleBuffer.getSpp(x, y) > 0) {
          ret.include(x, y);
          done = true;
        }

    // Find rightmost pixel...
    done = false;
    for (int x = sampleBuffer.rowSizeSpp - 1; x >= 0 && !done; x--)
      for (int y = 0; y < sampleBuffer.rowCountSpp && !done; y++)
        if (sampleBuffer.getSpp(x, y) > 0) {
          ret.include(x, y);
          done = true;
        }

    // Move x2 and y2 to be exclusive limits.
    ret.addMax(1);
    return ret;
  }


  @Override
  protected void readSamples(DataInputStream inputStream, Scene scene, PixelConsumer consumer, LongConsumer pixelProgress) throws IOException {
    try{throw new IllegalStateException("This shouldn't be getting called!");}
    catch(IllegalStateException e){e.printStackTrace();}
    throw new IllegalStateException("This shouldn't be getting called!");
  }

  @Override
  protected void writeSamples(DataOutputStream outputStream, Scene scene, LongConsumer pixelProgress) throws IOException {
    try{throw new IllegalStateException("This shouldn't be getting called!");}
    catch(IllegalStateException e){e.printStackTrace();}
    throw new IllegalStateException("This shouldn't be getting called!");
  }
}
