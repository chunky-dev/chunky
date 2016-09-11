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
package se.llbit.chunky.renderer;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.SceneManager;
import se.llbit.util.ProgressListener;
import se.llbit.util.TaskTracker;

/**
 * The standard render listener saves the scene when a render
 * job finishes and according to the render dump frequency setting.
 */
public abstract class StandardRenderListener implements RenderStatusListener {
  private final TaskTracker taskTracker;
  private final TaskTracker.Task renderTask;
  private final RenderContext context;
  private final SceneManager sceneManager;

  public StandardRenderListener(RenderContext context, SceneManager sceneManager,
      ProgressListener progressListener) {
    this(context, sceneManager, new TaskTracker(progressListener));
  }

  public StandardRenderListener(RenderContext context, SceneManager sceneManager,
      TaskTracker taskTracker) {
    this.context = context;
    this.sceneManager = sceneManager;
    this.taskTracker = taskTracker;
    renderTask = taskTracker.backgroundTask();
  }

  @Override public void frameCompleted(Scene scene, int spp) {
    if (saveSnapshot(scene, spp)) {
      // Save the current frame.
      scene.saveSnapshot(context.getSceneDirectory(), taskTracker);
    }

    if (saveRenderDump(scene, spp)) {
      // Save the scene description and current render dump.
      try {
        sceneManager.saveScene();
      } catch (InterruptedException e) {
        throw new Error(e);
      }
    }
  }

  @Override public final TaskTracker taskTracker() {
    return taskTracker;
  }

  @Override public final TaskTracker.Task renderTask() {
    return renderTask;
  }
}
