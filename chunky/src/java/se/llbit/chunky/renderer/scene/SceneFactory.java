/*
 * Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer.scene;

public abstract class SceneFactory {
  public static SceneFactory instance = new PaintableSceneFactory();

  public abstract Scene newScene();

  /**
   * Creates a scene which copies the state of another scene.
   * Some data like the sample buffer will be shared between the
   * two scenes.
   * @param scene the scene to copy
   */
  public abstract Scene copyScene(Scene scene);

  private static class PaintableSceneFactory extends SceneFactory {
    @Override public Scene newScene() {
      return new PaintableScene();
    }

    @Override public Scene copyScene(Scene scene) {
      return new PaintableScene(scene);
    }
  }
}
