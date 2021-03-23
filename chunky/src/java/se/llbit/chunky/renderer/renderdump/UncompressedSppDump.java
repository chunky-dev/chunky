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
 * This is a dump format for chunky that should allow for storing of SPP per pixel in addition to actual sample values.
 *
 * TODO: Allow for only storing partial regions and/or defining regions of SPPs.
 * TODO:  - Possibly deflate + huffman coding of spp values? That should have very good compression
 * TODO:    (~<50 bytes for all but the antithetical cases)
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

      // For future use; flag compression for this save format maybe?
      long saveflags = 0x0000000000000000L;
      outputStream.writeLong(saveflags);

      if (pixelRange.widthX()<=0 || pixelRange.widthZ()<=0)
        throw new NoDataException();
    }

    outputStream.writeChars(SECTION_HEADER_SAMPLES);
    long start;
    //****SAMPLES****//
    int taskCoef = pixelRange.widthX();
    try (TaskTracker.Task task = taskTracker.task("Saving render dump - Samples", taskCoef * pixelRange.widthZ())) {
      start = System.currentTimeMillis();
      // compile and write each row
      System.out.println(3*(pixelRange.xmax-pixelRange.xmin)*Double.BYTES);
      ByteBuffer bb = ByteBuffer.allocate(3*(pixelRange.xmax-pixelRange.xmin)*Double.BYTES+samples.rowSizeSpp*Integer.BYTES);
      System.out.println(bb.capacity());
      for (int y = pixelRange.zmin; y < pixelRange.zmax; y++) {
        for (int x = pixelRange.xmin; x < pixelRange.xmax; x++) {
          bb.putDouble(samples.get(x, y, 0));
          bb.putDouble(samples.get(x, y, 1));
          bb.putDouble(samples.get(x, y, 2));
          bb.putInt(samples.getSpp(x,y));
        }
        outputStream.write(bb.array());
        bb.rewind();
        task.update(taskCoef * (y - pixelRange.zmin));
      }
      System.out.println(System.currentTimeMillis()-start);
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

      scene.initBuffers();
      samples = scene.getSampleBuffer();

      int sppmin = inputStream.readInt();
      scene.spp = sppmin; // TODO: store spp min and max instead of only one value
      int sppmax = inputStream.readInt();
      scene.spp = sppmax; // TODO: store spp min and max instead of only one value

      scene.renderTime = inputStream.readLong();

      long flags = inputStream.readLong();
    }

    if (!("" + inputStream.readChar() + inputStream.readChar() + inputStream.readChar()).equals(SECTION_HEADER_SAMPLES))
      throw new StreamCorruptedException("Expected Sample Marker");

    ByteBuffer bb = ByteBuffer.allocate(width*Double.BYTES*3+width*Integer.BYTES);
    int taskCoef = scene.renderWidth();
    try (TaskTracker.Task task = taskTracker.task("Loading render dump - Samples", taskCoef * scene.renderHeight())) {
      for (int y = 0; y < height; y++) {
        inputStream.readFully(bb.array(),0,bb.capacity());
        bb.rewind();
        for (int x = 0; x < width; x++) {
          samples.setPixel(x, y, bb.getDouble(), bb.getDouble(), bb.getDouble());
          samples.setSpp(x,y,bb.getInt());
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

    Scene o = new Scene(scene);
    o.initBuffers(); // replaces o's SampleBuffer
    load(inputStream, o, taskTracker);

    SampleBuffer sb1 = scene.getSampleBuffer();
    SampleBuffer sb2 = o.getSampleBuffer();

    if (scene.width != o.width || scene.height != o.height)
      throw new Error("Failed to merge render dump - wrong canvas size.");

    int sxm = scene.crop_x;
    int sw = scene.subareaWidth;
    int sym = scene.crop_y;
    int sh = scene.subareaHeight;

    int oxm = o.crop_x;
    int ow = o.subareaWidth;
    int oym = o.crop_y;
    int oh = o.subareaHeight;

    int xmin = FastMath.min(scene.crop_x, o.crop_x);
    int ymin = FastMath.min(scene.crop_y, o.crop_y);
    int xmax = FastMath.max(scene.crop_x + scene.subareaWidth, o.crop_x + o.subareaWidth);
    int ymax = FastMath.max(scene.crop_y + scene.subareaHeight, o.crop_y + o.subareaHeight);
    scene.crop_x = xmin;
    scene.crop_y = ymin;
    scene.subareaWidth = xmax - xmin;
    scene.subareaHeight = ymax - ymin;
    scene.initBuffers();

    SampleBuffer out = scene.getSampleBuffer();

    int index, spp;
    double r, g, b;
    for (int y = 0; y < sh; y++)
      for (int x = 0; x < sw; x++) {
        spp = sb1.getSpp(x, y);
        if (spp != 0) {
          r = sb1.get(x, y, 0);
          g = sb1.get(x, y, 1);
          b = sb1.get(x, y, 2);
          index = (y + sym - ymin) * scene.subareaWidth + (x + sxm - xmin);
          out.mergeSamples(index, spp, r, g, b);
        }
      }

    for (int y = 0; y < oh; y++)
      for (int x = 0; x < ow; x++) {
        spp = sb2.getSpp(x, y);
        if (spp != 0) {
          r = sb2.get(x, y, 0);
          g = sb2.get(x, y, 1);
          b = sb2.get(x, y, 2);
          index = (y + oym - ymin) * scene.subareaWidth + (x + oxm - xmin);
          out.mergeSamples(index, spp, r, g, b);
        }
      }
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
