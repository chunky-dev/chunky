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
package se.llbit.chunky.renderer.ui;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public interface ViewListener {

	/**
	 * Camera strafed left
	 */
	void onStrafeLeft();

	/**
	 * Camera strafed right
	 */
	void onStrafeRight();

	/**
	 * Camera moved forward
	 */
	void onMoveForward();

	/**
	 * Camera moved backward
	 */
	void onMoveBackward();

	/**
	 * Camera moved forward far
	 */
	void onMoveForwardFar();

	/**
	 * Camera moved backward far
	 */
	void onMoveBackwardFar();

	/**
	 * Camera moved up
	 */
	void onMoveUp();

	/**
	 * Camera moved down
	 */
	void onMoveDown();

	/**
	 * Mouse was dragged
	 * @param dx X distance dragged
	 * @param dy Y distance dragged
	 */
	void onMouseDragged(int dx, int dy);

	/**
	 * Update the Show/Hide 3D view button.
	 * @param visible
	 */
	void setViewVisible(boolean visible);

	/**
	 * Zoom the camera in/out
	 * @param diff
	 */
	void onZoom(int diff);

}
