/* Copyright (c) 2021-2022 Chunky contributors
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
package se.llbit.chunky.renderer.export;

import java.io.IOException;
import java.io.OutputStream;

import se.llbit.chunky.renderer.scene.AlphaBuffer;
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
   * Note: It depends on the scene settings if the alpha buffer will be available on export.
   *
   * @return the required format for the alpha buffer or DISABLED if alpha is not supported.
   */
  default AlphaBuffer.Type transparencyType() {
    return AlphaBuffer.Type.DISABLED;
  }

  /**
   * @return true, if the export formats wants preprocessed buffer data<br>
   *         false, if the export format uses the unprocessed sampling data
   */
  default boolean wantsPostprocessing() {
    return true;
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
