package se.llbit.pfm;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.util.TaskTracker;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * Portable FloatMap image file writer.
 */
public class PfmFileWriter implements AutoCloseable
{
  private final DataOutputStream out;

  public PfmFileWriter(OutputStream out)
  {
    this.out = new DataOutputStream(out);
  }

  public PfmFileWriter(File file) throws IOException {
    this(new FileOutputStream(file));
  }

  @Override public void close() throws IOException {
    out.close();
  }

  public void write(Scene scene, TaskTracker progress) throws IOException
  {
    TaskTracker.Task task = progress.task("Writing PFM Rows", scene.canvasHeight());
    ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;

    writeHeader(scene, byteOrder);

    // Image's actual data.
    writePixelData(scene, byteOrder, task);

    // No footer data.

    // Done
    task.close();
    close();
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
    double[] pixel = new double[3];

    for (int y = height-1; y >= 0; y--) {
      task.update(height, height-y-1);
      for (int x = 0; x < width; x++) {
        //TODO theres probably a better way to do this.
        scene.postProcessPixel(x, y, pixel);
        out.write(ByteBuffer.allocate(4)
            .order(byteOrder)
            .putFloat((float)pixel[0])
            .array());
        out.write(ByteBuffer.allocate(4)
            .order(byteOrder)
            .putFloat((float)pixel[1])
            .array());
        out.write(ByteBuffer.allocate(4)
            .order(byteOrder)
            .putFloat((float)pixel[2])
            .array());
      }
    }
  }
}
