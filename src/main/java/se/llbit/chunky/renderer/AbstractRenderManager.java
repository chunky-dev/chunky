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
import org.apache.commons.math3.util.FastMath;

import se.llbit.chunky.renderer.scene.Scene;

/**
 * Interface for render managers
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public abstract class AbstractRenderManager extends Thread {

	/**
	 * Constructor
	 * @param context
	 */
	public AbstractRenderManager(RenderContext context) {
		super("Render Manager");

		this.numThreads = context.numRenderThreads();
		this.tileWidth = context.tileWidth();
	}

	/**
	 * Minimum number of worker threads
	 */
	public static int NUM_RENDER_THREADS_MIN = 1;

	/**
	 * Maximum number of worker threads
	 */
	public static int NUM_RENDER_THREADS_MAX = 10000;

	/**
	 * Default tile width
	 */
	public static int TILE_WIDTH_DEFAULT = 8;

	/**
	 * Number of render threads
	 */
	protected final int numThreads;

	/**
	 * Tile width
	 */
	protected final int tileWidth;

	/**
	 * Get a job from the job queue
	 * @return Next job Id
	 * @throws InterruptedException
	 */
	public abstract int getNextJob() throws InterruptedException;

	/**
	 * Report finished job
	 */
	public abstract void jobDone();

	/**
	 * @return The buffered scene object
	 */
	public abstract Scene bufferedScene();

}
