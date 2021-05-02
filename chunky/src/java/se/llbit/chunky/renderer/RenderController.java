/* Copyright (c) 2016-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2016-2021 Chunky contributors
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

/**
 * Contains all state for the renderManager.
 */
public class RenderController {
  private final SceneManager sceneManager;
  private final SceneProvider sceneProvider;
  private final RenderManager renderManager;
  private final RenderContext context;

  public RenderController(RenderContext context, RenderManager renderManager, SceneManager sceneManager,
                          SceneProvider sceneProvider) {
    this.context = context;
    this.renderManager = renderManager;
    this.sceneManager = sceneManager;
    this.sceneProvider = sceneProvider;
  }

  public SceneManager getSceneManager() {
    return sceneManager;
  }

  public RenderManager getRenderManager() {
    return renderManager;
  }

  public RenderContext getContext() {
    return context;
  }

  public SceneProvider getSceneProvider() {
    return sceneProvider;
  }
}
