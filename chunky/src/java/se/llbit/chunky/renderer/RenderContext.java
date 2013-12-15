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
import java.io.OutputStream;

/**
 * Rendering context.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class RenderContext {
	public static final String SCENE_DESCRIPTION_EXTENSION = ".json";

	private final File sceneDirectory;
	private final int numThreads;
	private final int tileWidth;

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
	 * @param sceneName
	 * @return Scene description file
	 * @throws FileNotFoundException
	 */
	public File getSceneDescriptionFile(String sceneName) {
		return getSceneFile(sceneName + SCENE_DESCRIPTION_EXTENSION);
	}

	/**
	 * @param sceneName
	 * @return Input stream for the scene description
	 * @throws FileNotFoundException
	 */
	public InputStream getSceneDescriptionInputStream(String sceneName)
			throws FileNotFoundException {
		return getSceneFileInputStream(sceneName + SCENE_DESCRIPTION_EXTENSION);
	}

	/**
	 * @param sceneName
	 * @return Output stream for the scene description
	 * @throws FileNotFoundException
	 */
	public OutputStream getSceneDescriptionOutputStream(String sceneName)
			throws FileNotFoundException {
		return getSceneFileOutputStream(sceneName + SCENE_DESCRIPTION_EXTENSION);
	}

	/**
	 * @param fileName
	 * @return Input stream for the given scene file
	 * @throws FileNotFoundException
	 */
	public File getSceneFile(String fileName) {
		return new File(sceneDirectory, fileName);
	}

	/**
	 * @param fileName
	 * @return Input stream for the given scene file
	 * @throws FileNotFoundException
	 */
	public InputStream getSceneFileInputStream(String fileName)
			throws FileNotFoundException {
		return new FileInputStream(getSceneFile(fileName));
	}

	/**
	 * @param fileName
	 * @return Output stream for the given scene file
	 * @throws FileNotFoundException
	 */
	public OutputStream getSceneFileOutputStream(String fileName)
			throws FileNotFoundException {
		return new FileOutputStream(getSceneFile(fileName));
	}

	/**
	 * @return The tile width
	 */
	public int tileWidth() {
		return tileWidth;
	}
}
