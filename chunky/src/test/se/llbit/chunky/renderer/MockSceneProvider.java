/* Copyright (c) 2017-2019 Jesper Öqvist <jesper@llbit.se>
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

import java.util.function.Consumer;

class MockSceneProvider implements SceneProvider {
  private final Scene scene;
  private boolean change = true;

  public MockSceneProvider(Scene scene) {
    this.scene = scene;
  }

  @Override
  public synchronized ResetReason awaitSceneStateChange() throws InterruptedException {
    while (!change) {
      wait();
    }
    change = false;
    return ResetReason.SCENE_LOADED;
  }

  @Override public synchronized boolean pollSceneStateChange() {
    return change;
  }

  @Override public synchronized void withSceneProtected(Consumer<Scene> fun) {
    fun.accept(scene);
  }

  @Override public synchronized void withEditSceneProtected(Consumer<Scene> fun) {
    // Won't be edited by the scene manager.
  }
}
