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
package se.llbit.chunky;

import se.llbit.chunky.main.Chunky;

/**
 * The plugin interface for Chunky plugins.
 *
 * <p>The Chunky launcher calls the attach() method for each plugin
 * in the plugin load order. A reference to the Chunky instance is passed
 * so that plugins can register their hooks.
 */
public interface Plugin {
  /**
   * This is called so that the plugin can initialize itself and
   * register its hooks with the Chunky instance.
   * @param chunky Chunky instance which the plugin should attach to
   */
  void attach(Chunky chunky);
}
