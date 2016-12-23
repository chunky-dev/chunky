/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.RenderConstants;
import se.llbit.chunky.renderer.scene.Scene;

import java.io.File;

/**
 * Current configuration
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChunkyOptions {
  // The scene directory can be updated by the scene directory chooser dialog.
  public volatile File sceneDir = null;

  public String sceneName = null;

  /** The output filename when doing a headless snapshot. */
  public String imageOutputFile = "";

  public String texturePack = null;
  public int renderThreads = -1;
  public File worldDir = null;
  public int target = -1;

  public int tileWidth = RenderConstants.TILE_WIDTH_DEFAULT;

  private ChunkyOptions() {
  }

  public static ChunkyOptions getDefaults() {
    ChunkyOptions defaults = new ChunkyOptions();
    defaults.sceneDir = PersistentSettings.getSceneDirectory();
    defaults.renderThreads = PersistentSettings.getNumThreads();
    defaults.texturePack = PersistentSettings.getLastTexturePack();
    return defaults;
  }

  @Override public ChunkyOptions clone() {
    ChunkyOptions clone = new ChunkyOptions();
    clone.sceneDir = sceneDir;
    clone.sceneName = sceneName;
    clone.texturePack = texturePack;
    clone.renderThreads = renderThreads;
    clone.worldDir = worldDir;
    return clone;
  }

  /**
   * Retrieve the scene description file for the selected scene.
   *
   * @return the scene description file handle
   */
  public File getSceneDescriptionFile() {
    if (sceneName.endsWith(Scene.EXTENSION)) {
      return new File(sceneName);
    } else {
      if (sceneDir != null) {
        return new File(sceneDir, sceneName + Scene.EXTENSION);
      } else {
        return new File(PersistentSettings.getSceneDirectory(), sceneName + Scene.EXTENSION);
      }
    }
  }

}
