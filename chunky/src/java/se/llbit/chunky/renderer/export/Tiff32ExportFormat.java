package se.llbit.chunky.renderer.export;

import java.io.IOException;
import java.io.OutputStream;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.tiff.TiffFileWriter;
import se.llbit.util.TaskTracker;

/**
 * TIFF with 32-bit color channels.
 */
public class Tiff32ExportFormat implements PictureExportFormat {

  @Override
  public String getName() {
    return "TIFF_32";
  }

  @Override
  public String getDescription() {
    return "TIFF, 32-bit floating point";
  }

  @Override
  public String getExtension() {
    return ".tiff";
  }

  @Override
  public boolean isTransparencySupported() {
    return false;
  }

  @Override
  public void write(OutputStream out, Scene scene, TaskTracker taskTracker) throws IOException {
    try (TaskTracker.Task task = taskTracker.task("Writing TIFF");
        TiffFileWriter writer = new TiffFileWriter(out)) {
      writer.write32(scene, task);
    }
  }
}
