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
package se.llbit.chunky.ui.render;

import javafx.scene.control.Tab;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.RenderControlsFxController;

/**
 * Tabs in the Render Controls dialog implement this interface.
 *
 * <p>The update method is called to update the active tab with the
 * current scene state.
 */
public interface RenderControlsTab {
  /**
   * Called when the tab should update itself because something in the
   * scene state changed.
   *
   * <p>This is called on the JavaFX application thread.
   *
   * @param scene the current scene state
   */
  void update(Scene scene);

  /**
   * @return the JavaFX tab component for this render controls tab
   */
  Tab getTab();

  /**
   * Called after chunks have been loaded.
   */
  default void onChunksLoaded() {
  }

  default void setController(RenderControlsFxController controller) {
  }
}
