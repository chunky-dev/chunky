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

import se.llbit.chunky.renderer.scene.Scene;


/**
 * Interface for render managers
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public interface IRenderManager {
	
	/**
	 * Minimum number of worker threads
	 */
	int NUM_RENDER_THREADS_MIN = 1;
	
	/**
	 * Maximum number of worker threads
	 */
	int NUM_RENDER_THREADS_MAX = 10000;
	
	/**
	 * Maximum width of render tile
	 */
	int TILE_WIDTH = 8;
	
	/**
	 * Maximum height of render tile
	 */
	int TILE_HEIGHT = TILE_WIDTH;
	

	/**
	 * Get a job from the job queue
	 * @return Next job Id
	 * @throws InterruptedException
	 */
	public int getNextJob() throws InterruptedException;
	
	/**
	 * Report finished job
	 */
	public void jobDone();
	
	/**
	 * @return The buffered scene object
	 */
	public Scene bufferedScene();

}
