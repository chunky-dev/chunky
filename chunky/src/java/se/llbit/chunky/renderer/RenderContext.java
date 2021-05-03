/* Copyright (c) 2012-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2012-2021 Chunky contributors
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
import se.llbit.chunky.renderer.scene.Scene;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Rendering context - keeps track of the Chunky configuration used for rendering.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class RenderContext {

  protected final Chunky chunky;
  protected final ChunkyOptions config;
  public AbstractRenderManager.WorkerFactory workerFactory = RenderWorker::new;
  private File sceneDirectory;

  /**
   * Construct a new render context.
   */
  public RenderContext(Chunky chunky) {
    this.chunky = chunky;
    this.config = chunky.options;
    this.sceneDirectory = config.sceneDir;
  }

  public Chunky getChunky() {
    return chunky;
  }

  /**
   * Set the saving/loading directory of the current scene.
   *
   * @param sceneDirectory The directory the scene should be saved/loaded to/from
   */
  public void setSceneDirectory(File sceneDirectory) {
    this.sceneDirectory = sceneDirectory;
  }

  /**
   * @return File handle to the scene directory.
   */
  public File getSceneDirectory() {
    return sceneDirectory;
  }

  /**
   * @return The preferred number of rendering threads.
   */
  public int numRenderThreads() {
    return config.renderThreads;
  }

  /**
   * @param sceneName The name of the scene description file without the file extension
   * @return Scene description file
   */
  public File getSceneDescriptionFile(String sceneName) {
    return getSceneFile(sceneName + Scene.EXTENSION);
  }

  /**
   * @param sceneName The name of the scene description file without the file extension
   * @return Input stream for the scene description
   * @throws FileNotFoundException If the file does not exist.
   */
  public InputStream getSceneDescriptionInputStream(String sceneName) throws FileNotFoundException {
    return getSceneFileInputStream(sceneName + Scene.EXTENSION);
  }

  /**
   * @param sceneName The name of the scene description file without the file extension
   * @return Output stream for the scene description
   * @throws FileNotFoundException If the file does not exist.
   */
  public OutputStream getSceneDescriptionOutputStream(String sceneName) throws FileNotFoundException {
    return getSceneFileOutputStream(sceneName + Scene.EXTENSION);
  }

  /**
   * Gets the directory of the given scene file.
   *
   * @param fileName the filename with the extension
   * @return A File object. Note, the file object may not exist yet and the directory leading to the file will be
   * created.
   */
  public File getSceneFile(String fileName) {
    ensureSceneDirectory();
    return new File(sceneDirectory, fileName);
  }

  /**
   * @param fileName the filename with the extension
   * @return Input stream for the given scene file
   * @throws FileNotFoundException If the file does not exist.
   */
  public InputStream getSceneFileInputStream(String fileName) throws FileNotFoundException {
    return new FileInputStream(getSceneFile(fileName));
  }

  /**
   * @param fileName the filename with the extension
   * @return Output stream for the given scene file
   * @throws FileNotFoundException If the file does not exist.
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
   * @return The samples per pixel per pass
   */
  public int sppPerPass() {
    return config.sppPerPass;
  }

  /**
   * @param fileName  the filename with the extension
   * @param timestamp the last file modification timestamp to compare against
   * @return {@code true} if the file has not changed since timestamp
   */
  public boolean fileUnchangedSince(String fileName, long timestamp) {
    File file = getSceneFile(fileName);
    return file.exists() && file.lastModified() == timestamp;
  }

  /**
   * @param fileName the filename with the extension
   * @return last modification timestamp
   */
  public long fileTimestamp(String fileName) {
    return getSceneFile(fileName).lastModified();
  }

  /**
   * Ensures the scene directory specified exists
   */
  private void ensureSceneDirectory() {
    if (sceneDirectory.exists())
      return;
    sceneDirectory.mkdirs();
  }
}
