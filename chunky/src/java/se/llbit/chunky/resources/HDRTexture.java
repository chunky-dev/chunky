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
package se.llbit.chunky.resources;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class HDRTexture extends AbstractHDRITexture {
	private static final Logger logger =
			Logger.getLogger(HDRTexture.class);

	public HDRTexture(File file) {
		// This RGBE loader was created to mimic the behavior of the RADIANCE
		// rendering system (http://radsite.lbl.gov/). I studied the sources
		// (src/common/color.c) to understand how RADIANCE worked, then wrote this
		// code from scratch in an attempt to implement the same interface.
		try {
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			String fmt = raf.readLine();
			if (!fmt.equals("#?RADIANCE")) {
				raf.close();
				throw new Error("not a recognized HDR format! Can only handle RGBE!");
			}
			boolean haveFormat = false;
			String format = "";
			while (true) {

				String cmd = raf.readLine();
				if (cmd.trim().isEmpty()) {
					break;
				}
				if (cmd.startsWith("FORMAT=")) {
					haveFormat = true;
					format = cmd;
				}
			}
			if (!haveFormat) {
				raf.close();
				throw new Error("could not find image format!");
			}
			if (!format.equals("FORMAT=32-bit_rle_rgbe")) {
				raf.close();
				throw new Error("only 32-bit RGBE HDR format supported!");
			}
			String resolution = raf.readLine();
			Pattern regex = Pattern.compile("-Y\\s(\\d+)\\s\\+X\\s(\\d+)");
			Matcher matcher = regex.matcher(resolution);
			if (!matcher.matches()) {
				raf.close();
				throw new Error("unrecognized pixel order");
			}
			width = Integer.parseInt(matcher.group(2));
			height = Integer.parseInt(matcher.group(1));

			long start = raf.getFilePointer();
			long byteBufLen = raf.length() - start;
			FileChannel channel = raf.getChannel();
			MappedByteBuffer byteBuf = channel.map(FileChannel.MapMode.READ_ONLY, start, byteBufLen);

			// precompute exponents
			double exp[] = new double[256];
			for (int e = 0; e < 256; ++e) {
				exp[e] = Math.pow(2, e-136);
			}

			buf = new float[width*height*3];
			byte[][] scanbuf = new byte[width][4];
			for (int i = 0; i < height; ++i) {
				readScanline(byteBuf, scanbuf, width);

				int offset = (height-i-1)*width*3;
				for (int x = 0; x < width; ++x) {
					int r = 0xFF&scanbuf[x][0];
					int g = 0xFF&scanbuf[x][1];
					int b = 0xFF&scanbuf[x][2];
					int e = 0xFF&scanbuf[x][3];
					if (e == 0) {
						buf[offset+0] = 0;
						buf[offset+1] = 0;
						buf[offset+2] = 0;
					} else {
						double f = exp[e];
						buf[offset+0] = (float) ((r+0.5)*f);
						buf[offset+1] = (float) ((g+0.5)*f);
						buf[offset+2] = (float) ((b+0.5)*f);
					}
					offset += 3;
				}
			}
			raf.close();
		} catch (IOException e) {
			logger.error("Error loading HRD image: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void readScanline(MappedByteBuffer byteBuf, byte[][] scanline, int len) {
		byte h0 = byteBuf.get();
		byte h1 = byteBuf.get();
		byte h2 = byteBuf.get();
		byte h3 = byteBuf.get();
		if (h0 != 2 || h1 != 2 || (h2&0x80) != 0) {
			scanline[0][0] = h0;
			scanline[0][1] = h1;
			scanline[0][2] = h2;
			scanline[0][3] = h2;
			readScanlineOldFmt(byteBuf, scanline, len);
		}

		int width = ((0xFF&h2)<<8) | (0xFF&h3);
		if (width != len) {
			throw new Error("length mismatch");
		}
		for (int i = 0; i < 4; ++i) {
			for (int j = 0; j < width; ) {
				int code = 0xFF & byteBuf.get();
				if (code > 128) {
					int num = 0x7F & code;
					if (j+num > width) {
						throw new Error("scanline overrun");
					}
					byte value = byteBuf.get();
					while (num-- > 0) {
						scanline[j++][i] = value;
					}
				} else {
					int num = code;
					if (j+num > width) {
						throw new Error("scanline overrun");
					}
					while (num-- > 0) {
						scanline[j++][i] = byteBuf.get();
					}
				}

			}
		}

	}

	private void readScanlineOldFmt(MappedByteBuffer byteBuf, byte[][] scanline, int len) {
		int shift = 0;
		for (int i = 1; i < len; ) {
			scanline[i][0] = byteBuf.get();
			scanline[i][1] = byteBuf.get();
			scanline[i][2] = byteBuf.get();
			scanline[i][3] = byteBuf.get();
			if (scanline[i][0] == 1 && scanline[i][1] == 1 && scanline[i][2] == 1) {
				int num = scanline[i][3]<<shift;
				for (int j = 0; j < num; ++j) {
					scanline[i][0] = scanline[i-1][0];
					scanline[i][1] = scanline[i-1][1];
					scanline[i][2] = scanline[i-1][2];
					scanline[i][3] = scanline[i-1][3];
					i += 1;
				}
				shift += 8;
			} else {
				shift = 0;
				i += 1;
			}
		}
	}

}
