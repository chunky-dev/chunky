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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;

/**
 * A PNG IDAT chunk
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class IDAT extends PngChunk {

	/**
	 * Output stream
	 */
	public class IDATOutputStream extends OutputStream {

		ByteArrayOutputStream bos;
		DeflaterOutputStream out;

		/**
		 * @throws IOException
		 */
		public IDATOutputStream() throws IOException {
			bos = new ByteArrayOutputStream();
			out = new DeflaterOutputStream(bos);
		}

		/**
		 * Flush the written data
		 */
		public void finishChunk() {
		    compressedData = bos.toByteArray();
		}

		@Override
		public void close() throws IOException {
		    out.finish();
		    compressedData = bos.toByteArray();
		    out.close();
		}

		@Override
		public void flush() throws IOException {
			out.flush();
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
		}

		@Override
		public void write(byte[] b) throws IOException {
			out.write(b);
		}

		@Override
		public void write(int b) throws IOException {
			out.write(b);
		}

		/**
		 * Reset the data stream
		 * @throws IOException
		 */
		public void reset() throws IOException {
			bos.reset();
		}

	}

	/**
	 * PNG chunk type ID
	 */
	public static final int CHUNK_TYPE = 0x49444154;

	/**
	 * Filter type
	 */
	public static final int FILTER_TYPE_NONE = 0;// the filter type for no filter

	private int crc;
	private byte[] compressedData;
	private IDATOutputStream idatOut;

	/**
	 * @return The output stream of this chunk
	 * @throws IOException
	 */
	public IDATOutputStream getIDATOutputStream() throws IOException {
		if (idatOut == null)
			idatOut = new IDATOutputStream();
		else
			idatOut.reset();
		return idatOut;
	}

	@Override
	public int getChunkType() {
		return CHUNK_TYPE;
	}

	@Override
	protected void writeChunkData(DataOutputStream out) throws IOException {
		CrcOutputStream crcOutputStream = new CrcOutputStream();
		DataOutputStream crcOut = new DataOutputStream(crcOutputStream);

		crcOut.writeInt(CHUNK_TYPE);

		crcOut.write(compressedData);
		out.write(compressedData);

		crc = crcOutputStream.getCRC();
		crcOut.close();
	}

	@Override
	public int getChunkLength() {
		return compressedData.length;
	}

	@Override
	public int getChunkCRC() {
		return crc;
	}


}
