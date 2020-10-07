package se.llbit.pfm;

import se.llbit.chunky.renderer.Postprocess;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.util.TaskTracker;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Portable FloatMap image file writer.
 */
public class PfmFileWriter implements AutoCloseable {
  private final DataOutputStream out;

  public PfmFileWriter(OutputStream out) {
    this.out = new DataOutputStream(out);
  }

  public PfmFileWriter(File file) throws IOException
  {
    this(new FileOutputStream(file));
  }

  @Override
  public void close() throws IOException {
    out.close();
  }

  public void write(Scene scene, TaskTracker.Task task) throws IOException {
    ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;

    writeHeader(scene, byteOrder);

    // Image's actual data.
    writePixelData(scene, byteOrder, task);

    // No footer data to write.
  }

  private void writeHeader(Scene scene, ByteOrder byteOrder) throws IOException {
    //Declare File Type
    out.write("PF".getBytes(StandardCharsets.US_ASCII));
    out.write(0x0a);

    // Declare Image Size
    out.write((scene.canvasWidth()+" "+scene.canvasHeight()).getBytes(StandardCharsets.US_ASCII));
    out.write(0x0a);

    // Declare Byte Order
    out.write((byteOrder == ByteOrder.LITTLE_ENDIAN ? "-1.0" : "1.0").getBytes(StandardCharsets.US_ASCII));
    out.write(0x0a);
  }

  private void writePixelData(Scene scene, ByteOrder byteOrder, TaskTracker.Task task) throws IOException {
    int width = scene.canvasWidth();
    int height = scene.canvasHeight();

    // holds the row's data as floats before pushing to stream.
    float[] rowBuffer = new float[3*width];
    // one or the other will be used, depending on if postprocessing is enabled.
    double[] pixel = new double[3];
    double[] sampleBuffer = scene.getSampleBuffer();

    // for each row...
    for (int y = height-1; y >= 0; y--) {
      task.update(height, height-y-1);

      // get the row's data as floats
      if (scene.postprocess == Postprocess.NONE)
        // from raw pixel data
        for (int x = 0; x < 3*width; x++)
          rowBuffer[x] = (float)sampleBuffer[y*width*3+x];
      else
        // or from post processor
        for (int x = 0; x < width; x++) {
          scene.postProcessPixel(x, y, pixel);
          rowBuffer[x*3+0] = (float)pixel[0];
          rowBuffer[x*3+1] = (float)pixel[1];
          rowBuffer[x*3+2] = (float)pixel[2];
        }

      // Write to buffer as floats using byteOrder
      ByteBuffer buffer = ByteBuffer.allocate(width*3*4).order(byteOrder);
      FloatBuffer floatBuffer = buffer.asFloatBuffer();
      floatBuffer.put(rowBuffer);
      out.write(buffer.array());
    }
  }
}
