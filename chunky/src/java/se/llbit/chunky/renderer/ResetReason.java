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

/**
 * Indicates the reason a render needs to be reset.
 */
public enum ResetReason {
  NONE(false, false),
  MODE_CHANGE(false, true),
  MATERIALS_CHANGED(true, true),
  /**
   * Settings changed, and we do want to trigger a rerender.
   */
  SETTINGS_CHANGED(true, true),
  /**
   * Settings changed, but we do NOT want to trigger a rerender.
   */
  SETTINGS_CHANGED_SOFT(true, false),
  SCENE_LOADED(true, true);

  /** Determines if the non-transitive scene state needs to be modified. */
  private final boolean overwriteState;

  /** Determines if the scene needs to be rerendered. **/
  private final boolean shouldRerender;

  ResetReason(boolean overwrite, boolean rerender) {
    overwriteState = overwrite;
    shouldRerender = rerender;
  }
  public boolean overwriteState() {
    return overwriteState;
  }

  public boolean shouldRerender() {
    return shouldRerender;
  }
}
