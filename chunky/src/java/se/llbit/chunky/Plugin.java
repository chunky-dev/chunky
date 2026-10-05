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
import se.llbit.util.concurrent.ChunkyThread;

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

  /**
   * Called when chunky shuts down, allowing the plugin to close critical resources. Most plugins do not need to do
   * anything here.
   *
   * <p>This function will be called after all {@link ChunkyThread}s have shut down. Chunky's UI thread may still
   * be running.</p>
   * <p>This method will not be called if any {@link ChunkyThread} does not shut down within its given time limit</p>
   * <p>This method may not be called if chunky terminates in a non-normal way, such as through {@link Runtime#halt},
   * the user killing the process (<code>SIGKILL</code> & <code>TerminateProcess</code>), or an error within a native
   * function.</p>
   */
  default void shutdown(Chunky chunky) { }
}
