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
import org.apache.commons.math3.util.FastMath;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class PngFileWriter {

	/**
	 * PNG magic value
	 */
	public static final long PNG_SIGNATURE = 0x89504E470D0A1A0AL;

	private DataOutputStream out;

	/**
	 * @param file
	 * @throws IOException
	 */
	public PngFileWriter(File file) throws IOException {
		out = new DataOutputStream(new FileOutputStream(file));
		out.writeLong(PNG_SIGNATURE);
	}

	/**
	 * @param chunk
	 * @throws IOException
	 */
	public void writeChunk(PngChunk chunk) throws IOException {
		chunk.writeChunk(out);
	}

	/**
	 * @throws IOException
	 */
	public void close() throws IOException {
		out.close();
	}
}
