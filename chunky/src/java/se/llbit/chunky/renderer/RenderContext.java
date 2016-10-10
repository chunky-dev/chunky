/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer;

import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.main.ChunkyOptions;
import se.llbit.chunky.renderer.scene.SceneDescription;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Rendering context - keeps track of the Chunky configuration
 * used for rendering.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class RenderContext {

  protected final Chunky chunky;
  protected final ChunkyOptions config;

  /**
   * Construct a new render context.
   */
  public RenderContext(Chunky chunky) {
    this.chunky = chunky;
    this.config = chunky.options;
  }

  public Chunky getChunky() {
    return chunky;
  }

  /**
   * @return File handle to the scene directory.
   */
  public File getSceneDirectory() {
    return config.sceneDir;
  }

  /**
   * @return The preferred number of rendering threads.
   */
  public int numRenderThreads() {
    return config.renderThreads;
  }

  /**
   * @return Scene description file
   */
  public File getSceneDescriptionFile(String sceneName) {
    return getSceneFile(sceneName + SceneDescription.EXTENSION);
  }

  /**
   * @return Input stream for the scene description
   * @throws FileNotFoundException
   */
  public InputStream getSceneDescriptionInputStream(String sceneName) throws FileNotFoundException {
    return getSceneFileInputStream(sceneName + SceneDescription.EXTENSION);
  }

  /**
   * @return Output stream for the scene description
   * @throws FileNotFoundException
   */
  public OutputStream getSceneDescriptionOutputStream(String sceneName)
      throws FileNotFoundException {
    return getSceneFileOutputStream(sceneName + SceneDescription.EXTENSION);
  }

  /**
   * @return Input stream for the given scene file
   */
  public File getSceneFile(String fileName) {
    return new File(config.sceneDir, fileName);
  }

  /**
   * @return Input stream for the given scene file
   * @throws FileNotFoundException
   */
  public InputStream getSceneFileInputStream(String fileName) throws FileNotFoundException {
    return new FileInputStream(getSceneFile(fileName));
  }

  /**
   * @return Output stream for the given scene file
   * @throws FileNotFoundException
   */
  public OutputStream getSceneFileOutputStream(String fileName) throws FileNotFoundException {
    return new FileOutputStream(getSceneFile(fileName));
  }

  /**
   * @return The tile width.
   */
  public int tileWidth() {
    return config.tileWidth;
  }

  /**
   * @param timestamp the last file modification timestamp to compare against
   * @return {@code true} if the file has not changed since timestamp
   */
  public boolean fileUnchangedSince(String fileName, long timestamp) {
    File file = getSceneFile(fileName);
    return file.exists() && file.lastModified() == timestamp;
  }

  /**
   * @return last modification timestamp
   */
  public long fileTimestamp(String fileName) {
    return getSceneFile(fileName).lastModified();
  }
}
