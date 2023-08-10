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
import se.llbit.chunky.renderer.RenderOptions;
import se.llbit.chunky.renderer.ModifiableRenderOptions;
import se.llbit.chunky.renderer.scene.Scene;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Various options for Chunky, set via the configuration file and/or command-line flags.
 */
public class ChunkyOptions {
  // The scene directory can be updated by the scene directory chooser dialog.
  public volatile File sceneDir = null;

  public String sceneName = null;

  /** The output filename when doing a headless snapshot. */
  public String imageOutputFile = "";

  private List<File> resourcePacks = new ArrayList<>();
  public File worldDir = null;
  public int target = -1;

  protected ModifiableRenderOptions renderOptions = new ModifiableRenderOptions();

  /** Ignore scene loading errors when starting a headless render. */
  public boolean force = false;

  /** Reload chunks with headless render */
  public boolean reloadChunks;

  private ChunkyOptions() {
  }

  public static ChunkyOptions getDefaults() {
    ChunkyOptions defaults = new ChunkyOptions();
    defaults.sceneDir = PersistentSettings.getSceneDirectory();
    defaults.renderOptions.setRenderThreadCount(PersistentSettings.getRenderThreadCount());
    return defaults;
  }

  @Override public ChunkyOptions clone() {
    ChunkyOptions clone = new ChunkyOptions();
    clone.sceneDir = sceneDir;
    clone.sceneName = sceneName;
    clone.resourcePacks = new ArrayList<>(resourcePacks);
    clone.renderOptions.copyState(renderOptions);
    clone.worldDir = worldDir;
    return clone;
  }

  /**
   * Returns the current state of the render options to by the renderer.
   * It is not specified whether it is a copy or a view of the current options and
   * does not guarantee to update on change.
   * <p>Cast it to {@link ModifiableRenderOptions}, if you have to edit it.
   * Edits are only safe on startup and might not propagate while a render is in progress.
   *
   * @return Options to use to start the render system.
   */
  public RenderOptions getRenderOptions() {
    return renderOptions;
  }

  public List<File> getResourcePacks() {
    return resourcePacks;
  }

  /**
   * Adds all resource packs given in the paths string to the load list.
   *
   * @param paths The path(s) to resource pack zip files to be loaded.
   *             Resource packs are loaded in the order of the paths in this argument.
   *             Paths are separated by the system path separator.
   */
  public void addResourcePacks(String paths) {
    resourcePacks.addAll(
      PersistentSettings.parseResourcePackPaths(paths)
    );
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
        File thisSceneDir = new File(sceneDir, sceneName);
        if (thisSceneDir.isDirectory()) {
          return new File(thisSceneDir, sceneName + Scene.EXTENSION);
        }
        return new File(sceneDir, sceneName + Scene.EXTENSION);
      } else {
        File thisSceneDir = new File(PersistentSettings.getSceneDirectory(), sceneName);
        if (thisSceneDir.isDirectory()) {
          return new File(thisSceneDir, sceneName + Scene.EXTENSION);
        }
        return new File(PersistentSettings.getSceneDirectory(), sceneName + Scene.EXTENSION);
      }
    }
  }
}
