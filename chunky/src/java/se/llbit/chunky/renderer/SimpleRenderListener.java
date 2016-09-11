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
import se.llbit.util.ProgressListener;
import se.llbit.util.TaskTracker;

/**
 * A simple render listener with a custom progress listener.
 */
public class SimpleRenderListener implements RenderStatusListener {
  private final ProgressListener progressListener;
  private final TaskTracker taskTracker;
  private final TaskTracker.Task renderTask;

  public SimpleRenderListener(ProgressListener progressListener) {
    this.progressListener = progressListener;
    taskTracker = new TaskTracker(progressListener);
    renderTask = taskTracker.backgroundTask();
  }

  @Override public void chunksLoaded() {
  }

  @Override public void setRenderTime(long time) {
  }

  @Override public void setSamplesPerSecond(int sps) {
  }

  @Override public void setSpp(int spp) {
  }

  @Override public void sceneSaved() {
  }

  @Override public void sceneLoaded() {
  }

  @Override public void renderStateChanged(RenderMode state) {
  }

  @Override public void renderJobFinished(long time, int sps) {
  }

  @Override public void frameCompleted(Scene scene, int spp) {
  }

  @Override public final TaskTracker taskTracker() {
    return taskTracker;
  }

  @Override public final TaskTracker.Task renderTask() {
    return renderTask;
  }

  public ProgressListener progressListener() {
    return progressListener;
  }
}
