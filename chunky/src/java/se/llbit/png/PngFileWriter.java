/* Copyright (c) 2012-2014 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

import se.llbit.chunky.renderer.ProgressListener;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class PngFileWriter {

	/**
	 * PNG magic value
	 */
	public static final long PNG_SIGNATURE = 0x89504E470D0A1A0AL;

	public static final int MAX_CHUNK_BYTES = 1000000;// max input/output buffer size

	private final DataOutputStream out;

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

	/**
	 * Write the image to the file
	 * @param image
	 * @param file
	 * @param progressListener
	 */
	public void write(BufferedImage image, ProgressListener progressListener)
			throws IOException {
		DataBufferInt dataBuf = (DataBufferInt) image.getData().getDataBuffer();
		int[] data = dataBuf.getData();
		int width = image.getWidth();
		int height = image.getHeight();
		writeChunk(new IHDR(width, height));
		IDATWriter idat = new IDATWriter();
		int i = 0;
		for (int y = 0; y < height; ++y) {
			progressListener.setProgress("Writing PNG", y, 0, height);
			idat.write(IDAT.FILTER_TYPE_NONE);// scanline header
			for (int x = 0; x < width; ++x) {
				int rgb = data[i++];
				idat.write((rgb>>16)&0xFF);
				idat.write((rgb>>8)&0xFF);
				idat.write(rgb&0xFF);
			}
			progressListener.setProgress("Writing PNG", y+1, 0, height);
		}
		idat.close();
	}

	class IDATWriter {
		Deflater deflater = new Deflater();
		int inputSize = 0;
		byte[] inputBuf = new byte[MAX_CHUNK_BYTES];
		int outputSize = 0;
		byte[] outputBuf = new byte[MAX_CHUNK_BYTES];

		void write(int b) throws IOException {
			if (inputSize == MAX_CHUNK_BYTES) {
				deflater.setInput(inputBuf, 0, inputSize);
				inputSize = 0;
				deflate();
			}
			inputBuf[inputSize++] = (byte) b;
		}

		private void deflate() throws IOException {
			int deflated;
			do {
				if (outputSize == MAX_CHUNK_BYTES) {
					writeChunk();
				}
				deflated = deflater.deflate(outputBuf, outputSize,
						MAX_CHUNK_BYTES-outputSize);
				outputSize += deflated;
			} while (deflated != 0);
		}

		private void writeChunk() throws IOException {
			out.writeInt(outputSize);

			CrcOutputStream crcOut = new CrcOutputStream();
			DataOutputStream crc = new DataOutputStream(crcOut);

			crc.writeInt(IDAT.CHUNK_TYPE);
			out.writeInt(IDAT.CHUNK_TYPE);

			crc.write(outputBuf, 0, outputSize);
			out.write(outputBuf, 0, outputSize);

			out.writeInt(crcOut.getCRC());
			crc.close();

			outputSize = 0;
		}

		void close() throws IOException {
			if (inputSize > 0) {
				deflater.setInput(inputBuf, 0, inputSize);
				deflater.finish();
				inputSize = 0;
				deflate();
			}
			if (outputSize > 0) {
				writeChunk();
			}
			deflater.end();
		}
	}
}
