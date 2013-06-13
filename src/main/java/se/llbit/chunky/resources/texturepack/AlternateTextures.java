/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.resources.texturepack;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

/**
 * An alternate texture will try loading several textures,
 * and only fail if none of them could be loaded.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class AlternateTextures extends TextureRef {

	private TextureRef[] alternatives;

	/**
	 * @param name Name of this texture
	 * @param alternatives
	 */
	public AlternateTextures(String name, TextureRef... alternatives) {
		super(name);

		this.alternatives = alternatives;
	}

	@Override
	public boolean load(ZipFile texturePack) {
		for (TextureRef alternative: alternatives) {
			if (alternative.load(texturePack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	boolean load(InputStream imageStream) throws IOException {
		throw new UnsupportedOperationException("Call load(ZipFile) instead!");
	}

}
