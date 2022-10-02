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
package se.llbit.chunky.renderer;

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.Scene;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The scene provider is used by the renderer to get the current scene state.
 * The scene provider modifies it's own scene state, and the renderer should
 * listen for scene state changes using awaitSceneStateChange()
 * and pollSceneStateChange().
 */
public interface SceneProvider {
  /**
   * Blocks until the scene state has changed.
   * The state change flag is reset after the blocking period.
   */
  ResetReason awaitSceneStateChange() throws InterruptedException;

  /**
   * This does not reset the state change flag - awaitSceneStateChange() should
   * be called to reset the state change flag.
   * @return {@code true} if the scene state has changed.
   */
  boolean pollSceneStateChange();

  /**
   * Calls the argument function on the scene while holding the scene lock.
   */
  void withSceneProtected(Consumer<Scene> fun);

  /**
   * Calls the argument function on the scene while holding the scene lock.
   */
  void withEditSceneProtected(Consumer<Scene> fun);

  /**
   * Add a listener that is called when the scene state has changed.
   */
  @PluginApi
  void addChangeListener(BiConsumer<ResetReason, Scene> listener);

  /**
   * Remove a listener added by {@link SceneProvider#addChangeListener(BiConsumer)}
   */
  @PluginApi
  void removeChangeListener(BiConsumer<ResetReason, Scene> listener);
}
