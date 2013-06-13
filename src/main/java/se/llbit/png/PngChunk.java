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
package se.llbit.png;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A PNG chunk
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public abstract class PngChunk {

	/**
	 * Write the chunk to the given output stream
	 * @param out
	 * @throws IOException
	 */
	public void writeChunk(DataOutputStream out) throws IOException {
		out.writeInt(getChunkLength());
		out.writeInt(getChunkType());
		if (getChunkLength() > 0)
			writeChunkData(out);
		out.writeInt(getChunkCRC());
	}

	protected abstract void writeChunkData(DataOutputStream out) throws IOException;

	/**
	 * @return The chunk length, in bytes
	 */
	public abstract int getChunkLength();

	/**
	 * @return The chunk type identifier
	 */
	public abstract int getChunkType();

	/**
	 * @return The chunk CRC
	 */
	public abstract int getChunkCRC();

}
