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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Rendering context that loads scene files from embedded resources.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class EmbeddedResourceContext extends RenderContext {

	/**
	 * @param parent
	 */
	public EmbeddedResourceContext(RenderContext parent) {
		super(parent.getSceneDirectory(), parent.numRenderThreads(),
				parent.tileWidth());
	}

	@Override
	public InputStream getSceneFileInputStream(String fileName)
			throws FileNotFoundException {
		InputStream in = EmbeddedResourceContext.class.getResourceAsStream(
				"/scenes/" + fileName);
		if (in == null)
			throw new FileNotFoundException();
		return in;
	}

	@Override
	public FileOutputStream getSceneFileOutputStream(String fileName) {
		throw new UnsupportedOperationException();
	}

}
