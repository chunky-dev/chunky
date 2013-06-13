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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Rendering context.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class RenderContext {
	private final File sceneDirectory;
	private final int numThreads;
	private int tileWidth;

	/**
	 * Construct a new render context.
	 * @param sceneDir The scene directory
	 * @param renderThreads
	 * @param tileWidth
	 */
	public RenderContext(File sceneDir, int renderThreads, int tileWidth) {
		sceneDirectory = sceneDir;
		numThreads = renderThreads;
		this.tileWidth = tileWidth;
	}

	/**
	 * @return File handle to the scene directory
	 */
	public File getSceneDirectory() {
		return sceneDirectory;
	}

	/**
	 * @return The preferred number of rendering threads
	 */
	public int numRenderThreads() {
		return numThreads;
	}

	/**
	 * @param fileName
	 * @return Input stream for the given scene file
	 * @throws FileNotFoundException
	 */
	public InputStream getSceneFileInputStream(String fileName)
			throws FileNotFoundException {

		return new FileInputStream(new File(sceneDirectory, fileName));
	}

	/**
	 * @param fileName
	 * @return Output stream for the given scene file
	 * @throws FileNotFoundException
	 */
	public FileOutputStream getSceneFileOutputStream(String fileName)
			throws FileNotFoundException {

		return new FileOutputStream(new File(sceneDirectory, fileName));
	}

	/**
	 * @return The tile width
	 */
	public int tileWidth() {
		return tileWidth;
	}
}
