/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
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

public interface RenderConstants {

  /**
   * Default samples per pixel per pass
   */
  int SPP_PER_PASS_DEFAULT = 1;

  /**
   * Default number of worker threads.
   * Is set to the number of available CPU cores.
   */
  int NUM_RENDER_THREADS_DEFAULT = Runtime.getRuntime().availableProcessors();

  /**
   * Default CPU load
   */
  int CPU_LOAD_DEFAULT = 100;

  /**
   * Minimum number of worker threads
   */
  int NUM_RENDER_THREADS_MIN = 1;

  /**
   * Maximum number of worker threads
   */
  int NUM_RENDER_THREADS_MAX = 10000;

  /**
   * Default tile width
   */
  int TILE_WIDTH_DEFAULT = 8;
}
