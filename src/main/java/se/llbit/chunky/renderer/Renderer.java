/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.Graphics;

/**
 * A renderer renders to a buffered image.
 * The buffered image in turn is displayed by a render canvas.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public interface Renderer {
	/**
	 * Draws the buffered image
	 * @param g
	 * @param width
	 * @param height
	 */
	public void drawBufferedImage(Graphics g, int width, int height);

	/**
	 * Set the buffer update flag. The buffer update flag
	 * decides wheter the renderer should update the buffered image.
	 * @param flag
	 */
	public void setBufferFinalization(boolean flag);
}
