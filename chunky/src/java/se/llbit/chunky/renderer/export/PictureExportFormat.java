package se.llbit.chunky.renderer.export;

import java.io.IOException;
import java.io.OutputStream;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.util.TaskTracker;

/**
 * An export format for pictures of a scene.
 */
public interface PictureExportFormat {

  /**
   * Unique name of this format, e.g. <code>PNG</code> or <code>TIFF_32</code>.
   *
   * @return Unique name of this format
   */
  String getName();

  /**
   * Get a human-readable description of this format.
   *
   * @return Description of this format
   */
  default String getDescription() {
    return getName();
  }

  /**
   * Get the file extension of this format.
   *
   * @return File extension with a leading dot (e.g. <code>.png</code>)
   */
  String getExtension();

  /**
   * Check if this format supports transparency (used for transparent sky).
   *
   * @return True if this format supports transparency, false otherwise
   */
  default boolean isTransparencySupported() {
    return false;
  }

  /**
   * Write the picture of the given scene into the given output stream, optionally reporting
   * progress to a task tracker.
   *
   * @param out         Output stream
   * @param scene       Scene to export
   * @param taskTracker Task tracker for progress reporting
   * @throws IOException If exporting the picture fails
   */
  void write(OutputStream out, Scene scene, TaskTracker taskTracker)
      throws IOException;
}
