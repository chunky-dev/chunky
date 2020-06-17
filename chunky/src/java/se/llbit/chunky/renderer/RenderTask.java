/* Copyright (c) 2019 Jesper Öqvist <jesper@llbit.se>
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
 * Describes part of the canvas to be rendered by a render worker.
 */
public class RenderTask {
  /**
   * {@see RenderTask.Kind}
   */
  public final Kind kind;

  /**
   * The rectangle of pixels to render. Unused in the case of END_FRAME tasks.
   */
  public final int x0, x1, y0, y1;

  public static final RenderTask END_FRAME = new RenderTask(Kind.END_FRAME);

  public enum Kind {
    /**
     * Render the given rectangle
     */
    RENDER,

    /**
     * Synchronize with the RenderManager
     */
    END_FRAME
  }

  public RenderTask(int x0, int x1, int y0, int y1) {
    this.kind = Kind.RENDER;
    this.x0 = x0;
    this.y0 = y0;
    this.x1 = x1;
    this.y1 = y1;
  }

  private RenderTask(Kind kind) {
    this.kind = kind;
    this.x0 = 0;
    this.y0 = 0;
    this.x1 = 0;
    this.y1 = 0;
  }

  @Override public String toString() {
    return String.format("[Tile: [%d %d]->[%d %d]]", x0, y0, x1, y1);
  }
}
