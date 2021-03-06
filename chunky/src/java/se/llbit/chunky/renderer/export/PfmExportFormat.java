package se.llbit.chunky.renderer.export;

import java.io.IOException;
import java.io.OutputStream;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.pfm.PfmFileWriter;
import se.llbit.util.TaskTracker;

/**
 * Portable float map (PFM) with 32-bit color channels.
 */
public class PfmExportFormat implements PictureExportFormat {

  @Override
  public String getName() {
    return "PFM";
  }

  @Override
  public String getDescription() {
    return "PFM, Portable FloatMap (32-bit)";
  }

  @Override
  public String getExtension() {
    return ".pfm";
  }

  @Override
  public boolean isTransparencySupported() {
    return false;
  }

  @Override
  public void write(OutputStream out, Scene scene, TaskTracker taskTracker) throws IOException {
    try (TaskTracker.Task task = taskTracker.task("Writing PFM rows", scene.canvasHeight());
        PfmFileWriter writer = new PfmFileWriter(out)) {
      writer.write(scene, task);
    }
  }
}
