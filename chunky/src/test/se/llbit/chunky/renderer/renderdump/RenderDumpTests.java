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

import org.junit.Test;
import se.llbit.chunky.renderer.scene.SampleBuffer;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.util.ProgressListener;
import se.llbit.util.TaskTracker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class RenderDumpTests {
  protected static final int testWidth = Scene.MIN_CANVAS_WIDTH;
  protected static final int testHeight = Scene.MIN_CANVAS_HEIGHT;
  protected static final int testSPP = 100;
  protected static final long testRenderTime = 654321L;

  protected static final double[][][] humanReadable1QuarterRGBSamples = {
      {{1, 1, 1}, {0, 0, 0}, {1, 1, 1}, {1, 0, 0}, {0, 1, 0}},
      {{0, 0, 0}, {1, 1, 1}, {0, 0, 0}, {0, 1, 0}, {0, 0, 1}},
      {{1, 1, 1}, {0, 0, 0}, {1, 1, 1}, {0, 0, 1}, {1, 0, 0}},
      {{1, 1, 0}, {1, 0, 1}, {0, 1, 1}, {0, 0.25, 0.5}, {0.125, 0.1, 0.5}},
      {{2, 0.01, 0}, {2, 0, 0.01}, {0, 0.01, 2}, {0.5, 0.1, 0.25}, {0.5, 0.25, 0}}
  };
  protected static final double[] testSampleBuffer;

  static {
    int rgbSamplesHeight = humanReadable1QuarterRGBSamples.length;
    int rgbSamplesWidth = humanReadable1QuarterRGBSamples[0].length;
    testSampleBuffer = new double[testWidth * testHeight * 3];
    int index;
    for (int y = 0; y < testWidth; y++) {
      for (int x = 0; x < testHeight; x++) {
        double[] rgb = humanReadable1QuarterRGBSamples[y % rgbSamplesHeight][x % rgbSamplesWidth];
        index = (y * testWidth + x) * 3;
        System.arraycopy(rgb, 0, testSampleBuffer, index, 3);
      }
    }
  }

  protected static final TaskTracker taskTracker = new TaskTracker(ProgressListener.NONE);

  protected Scene createTestScene(int width, int height, int spp, long renderTime) {
    Scene scene = new Scene();
    scene.setCanvasSize(width, height);
    scene.spp = spp;
    scene.renderTime = renderTime;
    scene.getSampleBuffer().setGlobalSpp(spp);
    return scene;
  }

  private static byte[] getTestDump(String dumpName) {
    return Base64.getDecoder().decode(
      testDumps.get(dumpName)
    );
  }

  @Test
  public void loadClassicFormatDumpTest() throws IOException {
    loadDumpTest("classicFormatDump");
  }

  @Test
  public void loadCompressedFloatFormatDumpTest() throws IOException {
    loadDumpTest("compressedFloatFormatDump");
  }

  private void loadDumpTest(String dumpName) throws IOException {
    Scene scene = createTestScene(testWidth, testHeight, 0, 0);
    ByteArrayInputStream inputStream = new ByteArrayInputStream(getTestDump(dumpName));
    RenderDump.load(inputStream, scene, taskTracker);
    assertArrayEquals(testSampleBuffer, compileSampleBuffer(scene.getSampleBuffer()), 0.0);
    assertEquals(testSPP, scene.spp);
    assertEquals(testRenderTime, scene.renderTime);
  }

  @Test
  public void mergeClassicFormatDumpTest() throws IOException {
    fullMergeDumpTest("classicFormatDump");
  }

  @Test
  public void mergeCompressedFloatFormatDumpTest() throws IOException {
    fullMergeDumpTest("compressedFloatFormatDump");
  }

  public void fullMergeDumpTest(String dumpName) throws IOException {
    int spp = 100;
    long renderTime = 123456L;
    double[] preMergeSamples = {0.5, 1.0, 2.0, 0.5, 1.0, 2.0, 2.0, 1.5, 2.5};
    double[] postMergeSamples = {0.75, 1.0, 1.5, 0.25, 0.5, 1.0, 1.5, 1.25, 1.75};

    Scene scene = createTestScene(testWidth, testHeight, spp, renderTime);
    fillSampleBuffer(preMergeSamples, scene.getSampleBuffer());

    ByteArrayInputStream inputStream = new ByteArrayInputStream(getTestDump(dumpName));
    RenderDump.merge(inputStream, scene, taskTracker);
    assertEquals(spp + testSPP, scene.spp);
    assertEquals(renderTime + testRenderTime, scene.renderTime);
    assertArrayEquals(postMergeSamples, compileSampleBuffer(scene.getSampleBuffer(), postMergeSamples.length), 0.0);
  }

  @Test
  public void uncompressedSppDumpTest() throws IOException {
    Scene scene = createTestScene(7, 5, 0, 0);
    scene.width=7;scene.height=5;
    RenderDump.load(UNCOMPRESSED_DUMP_WITH_SPP_A.get(), scene, taskTracker);
    assert scene.width == 7 && scene.height == 5
        : "Read UncompressedSppDump dimensions failed.";
    assert scene.crop_x == 0 && scene.crop_y == 1 && scene.subareaWidth == 4 && scene.subareaHeight == 4
        : "Read UncompressedSppDump crop locations failed.";
    assertEquals("Read UncompressedSppDump black pixel", 0x01000000, scene.getSampleBuffer().getArgb(1,1));
    assertEquals("Read UncompressedSppDump colored pixel", 0x011A4D33, scene.getSampleBuffer().getArgb(0,2));
    assertEquals("Read UncompressedSppDump spp 0", 0, scene.getSampleBuffer().getSpp(1,1));
    assertEquals("Read UncompressedSppDump spp", 3, scene.getSampleBuffer().getSpp(0,2));
    assertEquals("Read UncompressedSppDump spp", 15, scene.getSampleBuffer().getSpp(1,2));
    assertArrayEquals("Read UncompressedSppDump spp", UNCOMPRESSED_SPP_A, compileSampleBufferSpp(scene.getSampleBuffer()));

    RenderDump.merge(UNCOMPRESSED_DUMP_WITH_SPP_B.get(), scene, taskTracker);


    assertArrayEquals("Merge UncompressedSppDump spp", UNCOMPRESSED_SPP_MERGED, compileSampleBufferSpp(scene.getSampleBuffer()));
//    assertArrayEquals(expected, compileSampleBuffer(scene.getSampleBuffer()));
  }

//  /**
//   * it is currently not expected to write the old format (but it would be possible)
//   */
//  @Test
//  public void saveCompressedFloatFormatDumpTest() throws IOException {
//    saveDumpTest("compressedFloatFormatDump");
//  }

  @Test
  public void saveDumpTest() throws IOException {
    Scene scene = createTestScene(testWidth, testHeight, testSPP, testRenderTime);
    fillSampleBuffer(testSampleBuffer, scene.getSampleBuffer());
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    RenderDump.save(outputStream, scene, taskTracker);
    byte[] save = outputStream.toByteArray();
    // CompressedFloatDumpFormat -> 5945, 0x9792E1E1
    // UncompressedSppDump -> 11268, 0xD8D57CA6
    assertEquals(11268, save.length);
    assertEquals(0xD8D57CA6, Arrays.hashCode(save));
//    assertArrayEquals(getTestDump(dumpName), save);
  }



  private double[] compileSampleBuffer(SampleBuffer sampleBuffer) {
    return compileSampleBuffer(sampleBuffer, sampleBuffer.rowCount*sampleBuffer.rowSize);
  }
  private int[] compileSampleBufferSpp(SampleBuffer sampleBuffer) {
    int len = (int) sampleBuffer.numberOfPixels();
    int[] ret = new int[len];
    for (int i = 0; i<len; i++)
      ret[i]=sampleBuffer.getSpp(i);
    return ret;
  }

  private double[] compileSampleBuffer(SampleBuffer sampleBuffer, int len) {
    double[] ret = new double[len];
    for (int i = 0; i<len; i++)
      ret[i]=sampleBuffer.get(i);
    return ret;
  }

  private void fillSampleBuffer(double[] src, SampleBuffer dest) {
    fillSampleBuffer(src, dest, Math.min(src.length, dest.rowCount*dest.rowSize));
  }

  private void fillSampleBuffer(double[] src, SampleBuffer dest, int len) {
    for (int i = 0; i<len; i++)
      dest.set(i,src[i]);
  }
  // This is just at the bottom because the strings are soooo lllooooonnnnngggggg
  private static final Map<String, String> testDumps = new HashMap<String, String>() {{

    final String CLASSIC_TEST_DUMP_STRING = "H4sIAAAAAAAAAO3Wuw0CMRAEUKfk14+d0QhVQEoTUAQ5GSVcCUcHEJOg4xeMsNYr+fzRrCXks3a9L0HyOOcG9/5t3LxWj7u/vb4c7rEV64/dD997++11fRp20hxz87ra+VI/ngP2Cb65ed1c/6tYHb0Q6TN3WVdyUut+/OwT7OfDvI6/urlFXO38WJ+/wHx0xz+XzF3cZcsbbK52vtSP54B9pDmnllv73Te3jCs5qfXW8wabq53fe95gc9nyBpurnS/14zlgH2nOqeXWfvfNLeNKTmq99bzB5mrn95432Fy2vMHmaudL/XgO2Eeac2q5td99c8u4kpNabz1vsLna+b3nDTb3Ce81b9eUJQAA";
    final String TEST_DUMP_STRING_COMPRESSED = "RFVNUAAAAAEAAAAUAAAAFAAAAGQAAAAAAAn78Q4/8AAAAAAAAA4/8AAAAAAAAA4/8AAAAAAAAA4/8AAAAAAAAAA/8AAAAAAAAD/wAAAAAAAAAD/wAAAAAAAAP/AAAAAAAADuDj/wAAAAAAAA7gA/8AAAAAAAAD/wAAAAAAAA7uA/8AAAAAAAAO4AP/AAAAAAAAA/8AAAAAAAAA4/8AAAAAAAAOA/8AAAAAAAAO7uDj/wAAAAAAAA4D/wAAAAAAAAAD/wAAAAAAAAP/AAAAAAAADuDj/wAAAAAAAA7gA/8AAAAAAAAD/wAAAAAAAA7uA/8AAAAAAAAO4AP/AAAAAAAAA/8AAAAAAAAA4/8AAAAAAAAA4/8AAAAAAAAAA/8AAAAAAAAD/wAAAAAAAAAD/wAAAAAAAAP/AAAAAAAADuAD/wAAAAAAAAP/AAAAAAAADuDj/wAAAAAAAADj/wAAAAAAAA7g4/8AAAAAAAAO4AP/AAAAAAAAA/8AAAAAAAAOA/8AAAAAAAAAA/8AAAAAAAAD/wAAAAAAAA7uA/8AAAAAAAAO7gP/AAAAAAAADu4D/wAAAAAAAADj/wAAAAAAAADj/wAAAAAAAADj/wAAAAAAAA7g4/8AAAAAAAAO4AP/AAAAAAAAA/8AAAAAAAAOA/8AAAAAAAAAA/8AAAAAAAAD/wAAAAAAAA7g4/8AAAAAAAAAA/8AAAAAAAAD/wAAAAAAAADj/wAAAAAAAAAD/wAAAAAAAAP/AAAAAAAAAAP/AAAAAAAAA/8AAAAAAAAO4OP/AAAAAAAADu7u7gP/AAAAAAAAAAP/AAAAAAAAA/8AAAAAAAAAA/8AAAAAAAAD/wAAAAAAAADj/wAAAAAAAA7u7u4D/wAAAAAAAA4D/wAAAAAAAAAD/wAAAAAAAAP/AAAAAAAAAOP/AAAAAAAAAOP/AAAAAAAADu7u7gP/AAAAAAAAAAP/AAAAAAAAA/8AAAAAAAAAA/8AAAAAAAAD/wAAAAAAAADj/wAAAAAAAA7uA/8AAAAAAAAO4OP/AAAAAAAAAAP/AAAAAAAAA/8AAAAAAAAAI/8AAAAAAAACAAAAAAAAAAP/AAAAAAAAA/4AAAAAAAACAwAAAAAAAAP/AAAAAAAAADP7mZmZmZmZpAAAAAAAAALhAAAAAAAAAOP/AAAAAAAADvLhAAAAAAAAAOP/AAAAAA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IAAAAAAAAD/wAAAAAAAAIBAAAAAAAAA/8AAAAAAAAO4OP/AAAAAAAADuAD/wAAAAAAAAP/AAAAAAAADu4D/wAAAAAAAA7gA/8AAAAAAAAD/wAAAAAAAADj/wAAAAAAAA4D/wAAAAAAAA7u4OP/AAAAAAAADgP/AAAAAAAAAAP/AAAAAAAAA/8AAAAAAAAO4OP/AAAAAAAADuAD/wAAAAAAAAP/AAAAAAAADu4D/wAAAAAAAA7gA/8AAAAAAAAD/wAAAAAAAADj/wAAAAAAAADj/wAAAAAAAAAD/wAAAAAAAAP/AAAAAAAAAAP/AAAAAAAAA/8AAAAAAAAO4AP/AAAAAAAAA/8AAAAAAAAO4OP/AAAAAAAAAOP/AAAAAAAADuDj/wAAAAAAAA7gA/8AAAAAAAAD/wAAAAAAAA4D/wAAAAAAAAAD/wAAAAAAAAP/AAAAAAAADu4D/wAAAAAAAA7uA/8AAAAAAAAO7gP/AAAAAAAAAOP/AAAAAAAAAOP/AAAAAAAAAOP/AAAAAAAADuDj/wAAAAAAAA7gA/8AAAAAAAAD/wAAAAAAAA4D/wAAAAAAAAAD/wAAAAAAAAP/AAAAAAAADuDj/wAAAAAAAAAD/wAAAAAAAAP/AAAAAAAAAOP/AAAAAAAAAAP/AAAAAAAAA/8AAAAAAAAAA/8AAAAAAAAD/wAAAAAAAA7g4/8AAAAAAAAO7u7uA/8AAAAAAAAAA/8AAAAAAAAD/wAAAAAAAAAD/wAAAAAAAAP/AAAAAAAAAOP/AAAAAAAADu7u7gP/AAAAAAAADgP/AAAAAAAAAAP/AAAAAAAAA/8AAAAAAAAA4/8AAAAAAAAA4/8AAAAAAAAO7u7uA/8AAAAAAAAAA/8AAAAAAAAD/wAAAAAAAAAD/wAAAAAAAAP/AAAAAAAAAOP/AAAAAAAADu4D/wAAAAAAAA7g4/8AAAAAAAAAA/8AAAAAAAAD/wAAAAAAAAAj/wAAAAAAAAIAAAAAAAAAA/8AAAAAAAAD/gAAAAAAAAIDAAAAAAAAA/8AAAAAAAAAI/uZmZmZmZmkmZmZmZmZouEAAAAAAAAA5/8AAAAAAAAAA/hHrhR64Uez/wAAAAAAAALhAAAAAAAAAOP/AAAAAAAAAiIAAAAAAAAGmZmZmZmZriEAAAAAAAAA4/8AAAAAAAACBJmZmZmZmaP/AAAAAAAADiEAAAAAAAAOA/8AAAAAAAAAI/8AAAAAAAACAAAAAAAADu4D/wAAAAAAAAImmZmZmZmZpJmZmZmZmaLhAAAAAAAADuAD/wAAAAAAAAP/AAAAAAAAAuEAAAAAAAAA4/8AAAAAAAACIgAAAAAAAAaZmZmZmZmuIQAAAAAAAAAEAAAAAAAAAAf/AAAAAAAAAgPeN43jeN4T/wAAAAAAAA4mR64UeuFHsAP8AAAAAAAAB/4AAAAAAAAAI/hHrhR64Uez3jeN43jeEAf/AAAAAAAAB/0AAAAAAAAAA/4AAAAAAAAH/wAAAAAAAAImmZmZmZmZpUeuFHrhR7Dj/QAAAAAAAAAH/gAAAAAAAAP+AAAAAAAAACP/AAAAAAAAA943jeN43h4EAAAAAAAAAAAH/gAAAAAAAAP+AAAAAAAAAjPeN43jeN4WmZmZmZmZoAf9AAAAAAAAA/0AAAAAAAAOB/4AAAAAAAACBUeuFHrhR7P4R64UeuFHvuAD/gAAAAAAAAf+AAAAAAAAAiPeN43jeN4T3jeN43jeEAQAAAAAAAAAB/0AAAAAAAAA4/4AAAAAAAADJpmZmZmZmaVHrhR64Uew4/0AAAAAAAAAB/4AAAAAAAAD/gAAAAAAAAAj+EeuFHrhR7PeN43jeN4eBAAAAAAAAAAAB/4AAAAAAAAD/gAAAAAAAAIz3jeN43jeFpmZmZmZmaAH/QAAAAAAAAP9AAAAAAAAA=";

    put("classicFormatDump", CLASSIC_TEST_DUMP_STRING);
    put("compressedFloatFormatDump", TEST_DUMP_STRING_COMPRESSED);
  }};

  // These are duplicated below where it is interleaved with sample values.
  public static final int[] UNCOMPRESSED_SPP_A = new int[]{
      0,  0,    2,   1,
      0,  0,    0,  50,
      3, 15, 1200, 730,
      0,  7,    0,   0
  };
  // These are duplicated below where it is interleaved with sample values.
  public static final int[] UNCOMPRESSED_SPP_B = new int[]{
      0,   0, 1,
      0,   1, 0,
      2,  50, 0,
      600, 730, 5
  };
  public static final int[] UNCOMPRESSED_SPP_MERGED = new int[]{
      0,  0,    0,   0, 1,
      0,  0,    2,   2, 0,
      0,  0,    2, 100, 0,
      3, 15, 1800,1460, 5,
      0,  7,    0,   0, 0
  };

  public static final TestStreamBuilder UNCOMPRESSED_DUMP_WITH_SPP_A =
      new TestStreamBuilder(
          RenderDump.DUMP_FORMAT_MAGIC_NUMBER,     // File Header
          2,          // Version
          7, 5,       // Render Size
          0, 1, 4, 4, // Dump's Range (bottom left 4x4)
          0, 1200,    // SPP range within range
          654321L,    // Render Time
          0L,         // Dump Flags (currently unused)

          "sam",      // Samples Header
          // Samples in 4-tuples: <double, double, double, int> = <red, green, blue, spp>
          // The spp values (the 4th value, the ints) are duplicated above.
          //0d,  0d,  0d,        0d,   0d,  0d,         0d, 0d, 0d,          0.0d, 0.0d, 0.0d,           0d, 0d, 0d,
          0.0d,  0d,  0d, 0,     0d,   0d,  0d,  0,     1d, 2d, 3d,    2,    0.5d, 1.0d, 2.0d,   1,   // 0d, 0d, 0d,
          0.0d,  0d,  0d, 0,     0d,   0d,  0d,  0,     3d, 2d, 1d,    0,    0.5d, 1.0d, 2.0d,  50,   // 0d, 0d, 0d,
          0.1d, .3d, .2d, 3,    .4d, 1.7d, 43d, 15,     2d, 3d, 4d, 1200,    2.0d, 1.5d, 2.5d, 730,   // 0d, 0d, 0d,
          0.0d,  0d,  0d, 0,     0d,   0d,  0d,  7,     0d, 0d, 0d,    0,    0.0d, 0.0d, 0.0d,   0,   // 0d, 0d, 0d,

          "dun"       // Completion Marker
      );
  public static final TestStreamBuilder UNCOMPRESSED_DUMP_WITH_SPP_B =
      new TestStreamBuilder(
          RenderDump.DUMP_FORMAT_MAGIC_NUMBER,     // File Header
          2,          // Version
          7, 5,       // Render Size
          2, 0, 3, 4, // Dump's Range (middle column 3x4)
          0, 1000,    // SPP range within range
          123456L,    // Render Time
          0L,         // Dump Flags (currently unused)

          "sam",      // Samples Header
          // Samples in 4-tuples: <double, double, double, int> = <red, green, blue, spp>
          // The spp values (the 4th value, the ints) are duplicated above.
          /*0d,0d,0d,   0d,0d,0d,*/  0d, 0d, 0d,   0,    0d, 0d, 0d,   0,    0d, 1d, 0d, 1,
          /*0d,0d,0d,   0d,0d,0d,*/  3d, 2d, 1d,   0,    1d, 1d, 1d,   1,    0d, 0d, 0d, 0,
          /*0d,0d,0d,   0d,0d,0d,*/  1d, 2d, 3d,   2,    0d, 0d, 0d,  50,    0d, 0d, 0d, 0,
          /*0d,0d,0d,   0d,0d,0d,*/  4d, 3d, 2d, 600,    1d, 1d, 1d, 730,    1d, 0d, 1d, 5,
          //0d,0d,0d,   0d,0d,0d,    0d, 0d, 0d,         0d, 0d, 0d,         0d, 0d, 0d,

          "dun"       // Completion Marker
      );

  public static class TestStreamBuilder {
    final Object[] stream;
    private TestStreamBuilder(Object... stream) {
      this.stream = stream;
    }
    public InputStream get() {
      ByteBuffer bb = ByteBuffer.allocate(size());
      for (Object o : stream) {
        if (o instanceof Double)
          bb.putDouble((Double) o);
        else if (o instanceof Integer)
          bb.putInt((Integer)o);
        else if (o instanceof String)
          for (char c : ((String) o).toCharArray())
            bb.putChar(c);
        else if (o instanceof Long)
          bb.putLong((Long)o);
        else if (o instanceof byte[])
          for (byte b : (byte[]) o)
            bb.put(b);
        else if (o instanceof int[])
          for (int i : (int[]) o)
            bb.putInt(i);
        else
          throw new IllegalArgumentException("Unexpected object type: " + o.getClass());
      }
      return new ByteArrayInputStream(bb.array());
    }

    private int size() {
      int count = 0;
      for (Object o : stream) {
        if (o instanceof Double || o instanceof Long)
          count += 8;
        else if (o instanceof Integer)
          count += 4;
        else if (o instanceof String)
          count += ((String) o).length();
        else if (o instanceof byte[])
          count += ((byte[]) o).length;
        else if (o instanceof int[])
          count += 4*((int[]) o).length;
        else
          throw new IllegalArgumentException("Unexpected object type: " + o.getClass());
      }
      return count+16;
    }
  }
}
