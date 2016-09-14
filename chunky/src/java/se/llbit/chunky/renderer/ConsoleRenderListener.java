/* Copyright (c) 2012-2013 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.renderer.scene.SceneManager;
import se.llbit.util.TaskTracker;

/**
 * Prints the render progress to the console.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ConsoleRenderListener extends StandardRenderListener {

  public ConsoleRenderListener(RenderContext context, SceneManager sceneManager) {
    super(context, sceneManager, new TaskTracker(new ConsoleProgressListener(),
        (tracker, previous, name, size) -> new TaskTracker.Task(tracker, previous, name, size) {
          @Override public void close() {
            super.close();
            long endTime = System.currentTimeMillis();
            int seconds = (int) ((endTime - startTime) / 1000);
            System.out.format("\r%s took %dm %ds%n", name, seconds / 60, seconds % 60);
          }
        }));
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
    System.out.println("Render job finished.");
    int seconds = (int) ((time / 1000) % 60);
    int minutes = (int) ((time / 60000) % 60);
    int hours = (int) (time / 3600000);
    System.out.println(String
        .format("Total rendering time: %d hours, %d minutes, %d seconds", hours, minutes, seconds));
    System.out.println("Average samples per second (SPS): " + sps);
  }
}
