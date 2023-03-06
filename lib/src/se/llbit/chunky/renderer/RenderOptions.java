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

/**
 * The render options contain some values for tweaking the global render performance.
 * <p><i>Which of these options a specific Renderer/RayTracer supports is not defined.
 */
public interface RenderOptions {

  /**
   * Default samples per pixel per pass
   */
  int SPP_PER_PASS_DEFAULT = 1;

  int getSppPerPass();

  /**
   * Default CPU load
   */
  int TARGET_CPU_LOAD_PERCENTAGE = 100;

  /**
   * Default number of worker threads.
   * Is set to the number of available CPU cores.
   */
  int NUM_RENDER_THREADS_DEFAULT = Runtime.getRuntime().availableProcessors();

  /**
   * Minimum number of worker threads
   */
  int RENDER_THREADS_COUNT_MIN = 1;

  /**
   * Maximum number of worker threads
   */
  int RENDER_THREAD_COUNT_MAX = 10000;

  /**
   * -1 will use all available processors
   */
  int getRenderThreadCount();

  /**
   * Default tile width
   */
  int TILE_WIDTH_DEFAULT = 8;

  int getTileWidth();
}
