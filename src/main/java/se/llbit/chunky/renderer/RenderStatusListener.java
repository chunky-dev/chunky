/* Copyright (c) 2012-2013 Jesper Öqvist <jesper@llbit.se>
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
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public interface RenderStatusListener extends ProgressListener {

	/**
	 * Called when chunks have been loaded.
	 */
	public void chunksLoaded();

	/**
	 * Update render time status label
	 * @param time Total render time in milliseconds
	 */
	public void setRenderTime(long time);

	/**
	 * Update samples per second status label
	 * @param sps Samples per second
	 */
	public void setSamplesPerSecond(int sps);

	/**
	 * Update SPP status label
	 * @param spp Samples per pixel
	 */
	public void setSPP(int spp);

	/**
	 * Called when the current scene has been saved
	 */
	public void sceneSaved();

	/**
	 * Method to notify the render controls dialog that a scene has been loaded.
	 * Causes canvas size to be updated.
	 */
	public void sceneLoaded();

	/**
	 * Called when the rendering activity has changed state
	 * @param pathTrace
	 * @param paused
	 */
	void renderStateChanged(boolean pathTrace, boolean paused);

	/**
	 * Called when the current render job has completed.
	 * @param time Total rendering time
	 * @param sps Average SPS
	 */
	void renderJobFinished(long time, int sps);
}
