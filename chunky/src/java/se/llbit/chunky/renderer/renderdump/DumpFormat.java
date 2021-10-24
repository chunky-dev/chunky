package se.llbit.chunky.renderer.renderdump;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.util.Registerable;
import se.llbit.util.TaskTracker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A dump format reads a render dump from a DataInputStream into the scene/ writes a render dump from the scene into a
 * DataOutputStream.
 *
 * The input stream
 * The input stream is expected to <i>not</i> contain the magic number and version number - they should have been read
 * before calling load (logic for that in RenderDump class). The output stream is expected to contain the magic number
 * and version number - they should have been written before calling save (logic for that in RenderDump class).
 * <p>
 * The "header" of a dump typically contains width, height, spp and renderTime and is the same for all currently
 * implemented formats. The strategy for reading/writing the samples (from the buffer in the scene) has to be
 * implemented.
 */
public interface DumpFormat extends Registerable {
  /**
   * Get the dump version number. This should be a constant.
   */
  int getVersion();

  /**
   * Read a render dump from an input stream. The stream will only contain the payload of the Chunky render dump
   * container format.
   * <p>
   * Most render dumps will contain a "header" containing the width, height, spp, and renderTime followed by
   * the sample buffer in some compressed format.
   *
   * @throws IllegalStateException If the render dump is invalid.
   */
  void load(DataInputStream inputStream, Scene scene, TaskTracker taskTracker)
      throws IOException, IllegalStateException;

  /**
   * Save a render dump to the output stream. This should only write the payload of the Chunky render dump
   * container format.
   */
  void save(DataOutputStream outputStream, Scene scene, TaskTracker taskTracker)
      throws IOException;

  /**
   * Merge a render dump into the provided scene.
   *
   * @throws IllegalStateException If the render dump is invalid.
   */
  void merge(DataInputStream inputStream, Scene scene, TaskTracker taskTracker)
      throws IOException, IllegalStateException;
}
