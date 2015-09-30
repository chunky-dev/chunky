/* Copyright (c) 2012-2015 Jesper Öqvist <jesper@llbit.se>
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

import org.apache.commons.math3.util.FastMath;

import se.llbit.chunky.renderer.Postprocess;
import se.llbit.chunky.renderer.ProgressListener;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.QuickMath;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class PngFileWriter {

	/**
	 * PNG magic value
	 */
	public static final long PNG_SIGNATURE = 0x89504E470D0A1A0AL;

	public static final int MAX_CHUNK_BYTES = 0x100000;// max input/output buffer size = 1 MiB

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
	 * Write the image to a PNG file.
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
			idat.write(IDAT.FILTER_TYPE_NONE); // Scanline header.
			for (int x = 0; x < width; ++x) {
				int rgb = data[i++];
				idat.write((rgb >> 16) & 0xFF);
				idat.write((rgb >> 8) & 0xFF);
				idat.write(rgb & 0xFF);
			}
			progressListener.setProgress("Writing PNG", y+1, 0, height);
		}
		idat.close();
	}

	/**
	 * Write the image to a PNG file.
	 */
	public void write(BufferedImage image, byte[] alpha, ProgressListener progressListener)
			throws IOException {
		DataBufferInt dataBuf = (DataBufferInt) image.getData().getDataBuffer();
		int[] data = dataBuf.getData();
		int width = image.getWidth();
		int height = image.getHeight();
		writeChunk(new IHDR(width, height, IHDR.COLOR_TYPE_RGBA));
		IDATWriter idat = new IDATWriter();
		int i = 0;
		for (int y = 0; y < height; ++y) {
			progressListener.setProgress("Writing PNG", y, 0, height);
			idat.write(IDAT.FILTER_TYPE_NONE); // Scanline header.
			for (int x = 0; x < width; ++x) {
				int rgb = data[i];
				idat.write((rgb >> 16) & 0xFF);
				idat.write((rgb >> 8) & 0xFF);
				idat.write(rgb & 0xFF);
				idat.write(alpha[i]);
				i += 1;
			}
			progressListener.setProgress("Writing PNG", y + 1, 0, height);
		}
		idat.close();
	}

	/**
	 * Write the image to a PNG file with 16-bit color channels.
	 */
	public void write16(int width, int height, double[] samples,
			double exposure, Postprocess postprocess, ProgressListener progressListener)
			throws IOException {
		writeChunk(new IHDR(width, height, IHDR.COLOR_TYPE_RGB, 16));
		IDATWriter idat = new IDATWriter();
		for (int y = 0; y < height; ++y) {
			progressListener.setProgress("Writing PNG", y, 0, height);
			idat.write(IDAT.FILTER_TYPE_NONE); // Scanline header.
			for (int x = 0; x < width; ++x) {
				// TODO(jesper): refactor this to fix code duplication.
				double r = samples[(y*width+x)*3+0];
				double g = samples[(y*width+x)*3+1];
				double b = samples[(y*width+x)*3+2];

				r *= exposure;
				g *= exposure;
				b *= exposure;

				switch (postprocess) {
				case NONE:
					break;
				case TONEMAP1:
					r = QuickMath.max(0, r-0.004);
					r = (r*(6.2*r + .5)) / (r * (6.2*r + 1.7) + 0.06);
					g = QuickMath.max(0, g-0.004);
					g = (g*(6.2*g + .5)) / (g * (6.2*g + 1.7) + 0.06);
					b = QuickMath.max(0, b-0.004);
					b = (b*(6.2*b + .5)) / (b * (6.2*b + 1.7) + 0.06);
					break;
				case GAMMA:
					r = FastMath.pow(r, 1/Scene.DEFAULT_GAMMA);
					g = FastMath.pow(g, 1/Scene.DEFAULT_GAMMA);
					b = FastMath.pow(b, 1/Scene.DEFAULT_GAMMA);
					break;
				}

				r = QuickMath.min(1, r);
				g = QuickMath.min(1, g);
				b = QuickMath.min(1, b);

				idat.write16((int) (0xFFFF * r + .5));
				idat.write16((int) (0xFFFF * g + .5));
				idat.write16((int) (0xFFFF * b + .5));
			}
			progressListener.setProgress("Writing PNG", y + 1, 0, height);
		}
		idat.close();
	}

	/**
	 * Write the image to a PNG file with 16-bit color channels.
	 */
	public void write16(int width, int height, double[] samples, byte[] alpha,
			double exposure, Postprocess postprocess, ProgressListener progressListener)
			throws IOException {
		writeChunk(new IHDR(width, height, IHDR.COLOR_TYPE_RGBA, 16));
		IDATWriter idat = new IDATWriter();
		int i = 0;
		for (int y = 0; y < height; ++y) {
			progressListener.setProgress("Writing PNG", y, 0, height);
			idat.write(IDAT.FILTER_TYPE_NONE); // Scanline header.
			for (int x = 0; x < width; ++x) {
				// TODO(jesper): refactor this to fix code duplication.
				double r = samples[(y*width+x)*3+0];
				double g = samples[(y*width+x)*3+1];
				double b = samples[(y*width+x)*3+2];

				r *= exposure;
				g *= exposure;
				b *= exposure;

				switch (postprocess) {
				case NONE:
					break;
				case TONEMAP1:
					r = QuickMath.max(0, r-0.004);
					r = (r*(6.2*r + .5)) / (r * (6.2*r + 1.7) + 0.06);
					g = QuickMath.max(0, g-0.004);
					g = (g*(6.2*g + .5)) / (g * (6.2*g + 1.7) + 0.06);
					b = QuickMath.max(0, b-0.004);
					b = (b*(6.2*b + .5)) / (b * (6.2*b + 1.7) + 0.06);
					break;
				case GAMMA:
					r = FastMath.pow(r, 1/Scene.DEFAULT_GAMMA);
					g = FastMath.pow(g, 1/Scene.DEFAULT_GAMMA);
					b = FastMath.pow(b, 1/Scene.DEFAULT_GAMMA);
					break;
				}

				r = QuickMath.min(1, r);
				g = QuickMath.min(1, g);
				b = QuickMath.min(1, b);

				idat.write16((int) (0xFFFF * r + .5));
				idat.write16((int) (0xFFFF * g + .5));
				idat.write16((int) (0xFFFF * b + .5));
				// TODO(jesper): add real 16-bit alpha channel.
				idat.write16(alpha[i] << 8);
				i += 1;
			}
			progressListener.setProgress("Writing PNG", y + 1, 0, height);
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

		void write16(int bb) throws IOException {
			write(bb >> 8);
			write(bb & 0xFF);
		}

		private void deflate() throws IOException {
			int deflated;
			do {
				if (outputSize == MAX_CHUNK_BYTES) {
					writeChunk();
				}
				deflated = deflater.deflate(outputBuf, outputSize, MAX_CHUNK_BYTES - outputSize);
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
