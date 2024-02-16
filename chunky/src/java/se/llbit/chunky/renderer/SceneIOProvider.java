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

import se.llbit.chunky.renderer.scene.Scene;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface SceneIOProvider {

  /**
   * Gets the scene directory.
   *
   * @return The scene directory, which may not exist yet
   */
  File getSceneDirectory();

  /**
   * Gets the directory of the given scene file.
   *
   * @param fileName the filename with the extension
   * @return A file object that may not exist yet
   */
  default File getSceneFile(String fileName) {
    return new File(getSceneDirectory(), fileName);
  }

  /**
   * Resolves a file needed for the scene (typically a user-selected texture)
   * which could either be directly in the scene directory (or subfolder)
   * or at an absolute location in the file system.
   *
   * @param fileName scene relative or absolute file name
   * @return the file if found
   * @throws FileNotFoundException if the file was not found
   */
  default File resolveLinkedFile(String fileName) throws FileNotFoundException {
    File file = new File(fileName);
    if (!file.isAbsolute()) {
      file = getSceneFile(fileName);
    }
    if (!file.exists()) {
      throw new FileNotFoundException("file does not exist: " + fileName);
    }
    return file;
  }

  /**
   * @param sceneName The name of the scene description file without the file extension
   * @return Input stream for the scene description
   * @throws FileNotFoundException If the file does not exist.
   */
  default InputStream getSceneDescriptionInputStream(String sceneName) throws FileNotFoundException {
    return getSceneFileInputStream(sceneName + Scene.EXTENSION);
  }

  /**
   * @param sceneName The name of the scene description file without the file extension
   * @return Output stream for the scene description
   * @throws FileNotFoundException If the file does not exist.
   */
  default OutputStream getSceneDescriptionOutputStream(String sceneName) throws FileNotFoundException {
    return getSceneFileOutputStream(sceneName + Scene.EXTENSION);
  }

  /**
   * @param fileName the filename with the extension
   * @return Input stream for the given scene file
   * @throws FileNotFoundException If the file does not exist.
   */
  default InputStream getSceneFileInputStream(String fileName) throws FileNotFoundException {
    return new FileInputStream(getSceneFile(fileName));
  }

  /**
   * @param fileName the filename with the extension
   * @return Output stream for the given scene file
   * @throws FileNotFoundException If the file does not exist.
   */
  default OutputStream getSceneFileOutputStream(String fileName) throws FileNotFoundException {
    return new FileOutputStream(getSceneFile(fileName));
  }

  /**
   * @param fileName  the filename with the extension
   * @param timestamp the last file modification timestamp to compare against
   * @return {@code true} if the file has not changed since timestamp
   */
  default boolean fileUnchangedSince(String fileName, long timestamp) {
    File file = getSceneFile(fileName);
    return file.exists() && file.lastModified() == timestamp;
  }

  /**
   * @param fileName the filename with the extension
   * @return last modification timestamp
   */
  default long fileTimestamp(String fileName) {
    return getSceneFile(fileName).lastModified();
  }

  /**
   * @return a list of available scene description files in the given scene directory
   */
  public static List<File> getAvailableSceneFiles(File sceneDir) {
    //Get all the files with either a .json extension or get all directories in the given folder since scenes can be held in directories now.
    File[] sceneList = sceneDir.listFiles((dir, name) -> name.endsWith(Scene.EXTENSION)
      || (name.endsWith(Scene.EXTENSION + ".backup") && !new File(dir, name.substring(0, name.length() - 7)).exists())
      || new File(dir, name).isDirectory());
    if (sceneList == null) {
      return Collections.emptyList();
    }

    List<File> sceneFiles = new ArrayList<>();
    for (File file : sceneList) {
      //If the file was a directory, we just run this method on that folder, otherwise, we know it's a json file, so we add it to the "sceneFiles" list
      if (file.isDirectory()) {
        sceneFiles.addAll(getAvailableSceneFiles(file));
      } else {
        sceneFiles.add(file);
      }
    }
    return sceneFiles;
  }
}
