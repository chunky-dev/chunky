/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.main;

import java.io.File;

import se.llbit.chunky.renderer.RenderConstants;

/**
 * Current configuration
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChunkyOptions {
	public File sceneDir = null;
	public String sceneName = null;
	public String texturePack = null;
	public int renderThreads = -1;
	public File worldDir = null;
	public int target = -1;

	/**
	 * Whether or not OpenCL rendering is enabled
	 */
	public boolean openCLEnabled;

	public int tileWidth = RenderConstants.TILE_WIDTH_DEFAULT;

	@Override
	public ChunkyOptions clone() {
		ChunkyOptions clone = new ChunkyOptions();
		clone.sceneDir = sceneDir;
		clone.sceneName = sceneName;
		clone.texturePack = texturePack;
		clone.renderThreads = renderThreads;
		clone.worldDir = worldDir;
		return clone;
	}
}
