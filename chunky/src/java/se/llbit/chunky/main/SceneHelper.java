/* Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.main;

import se.llbit.chunky.renderer.scene.SceneDescription;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility functions for handling saved scenes.
 */
public class SceneHelper {
  /**
   * @return a list of available scene description files in the given scene
   * directory
   */
  public static List<File> getAvailableSceneFiles(File sceneDir) {
    File[] sceneFiles = sceneDir
        .listFiles((dir, name) -> name.endsWith(SceneDescription.SCENE_DESCRIPTION_EXTENSION));
    if (sceneFiles != null) {
      List<File> fileList = new ArrayList<>(sceneFiles.length);
      Collections.addAll(fileList, sceneFiles);
      return fileList;
    } else {
      return Collections.emptyList();
    }
  }
}
