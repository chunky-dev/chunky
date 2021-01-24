/* Copyright (c) 2012-2016 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer.renderdump;

@FunctionalInterface
public interface PixelConsumer {
  /**
   * @param pixelIndex Index of pixel between 0 and canvas.width*canvas.height (* 3 for index in sampleBuffer)
   * @param r          Red pixel value
   * @param g          Green pixel value
   * @param b          Blue pixel value
   */
  void consume(int pixelIndex, double r, double g, double b);
}
