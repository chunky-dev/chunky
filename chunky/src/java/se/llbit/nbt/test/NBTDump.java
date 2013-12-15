/* Copyright (c) 2010-2013 Jesper Öqvist <jesper@llbit.se>
 *                    2013 TOGoS
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
package se.llbit.nbt.test;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import se.llbit.chunky.world.storage.RegionFile;
import se.llbit.nbt.AnyTag;
import se.llbit.nbt.ErrorTag;
import se.llbit.nbt.NamedTag;

@SuppressWarnings("javadoc")
public class NBTDump {

	static final Pattern REGION_PATTERN = Pattern.compile("^region-chunk:(\\d+),(\\d+):(.*)$");

	protected static boolean isGzipped(InputStream is) throws IOException {
		is.mark(2);
		boolean isGZ = is.read() == 0x1F && is.read() == 0x8B;
		is.reset();
		return isGZ;
	}

	protected static AnyTag read(String arg) throws IOException {
		Matcher m = REGION_PATTERN.matcher(arg);
		DataInputStream in = null;
		RegionFile rf = null;
		String filename;
		try {
			if (m.matches()) {
				filename = m.group(3);
				int x = Integer.parseInt(m.group(1));
				int z = Integer.parseInt(m.group(2));
				rf = new RegionFile(new File(filename));
				if (!rf.hasChunk(x, z)) {
					System.err.println("No such chunk in region: (" + x + "," + z + ")");
					// print available chunks
					System.err.println("available chunks in region:");
					for (int cx = 0; cx < 32; cx++) {
						for (int cz = 0; cz < 32; cz++) {
							if (rf.hasChunk(cx, cz)) {
								System.err.println("("+cx+","+cz+")");
							}
						}
					}
					return new ErrorTag();
				}
				in = rf.getChunkDataInputStream(x, z);
			} else {
				filename = arg;
				InputStream is = new BufferedInputStream(
						new FileInputStream(new File(filename)));
				if (isGzipped(is)) {
					is = new GZIPInputStream(is);
				}
				in = new DataInputStream(is);
			}
			if (in != null) {
				return NamedTag.read(in);
			} else {
				System.err.println("Failed to open file: " + filename);
				return new ErrorTag();
			}
		} finally {
			if (in != null) in.close();
			if (rf != null) rf.close();
		}
	}

	protected static final String USAGE =
		"Usage: NBTDump <file>\n"+
		"\n"+
		"<file> may be an NBT-formatted file (gzipped or uncompressed),\n"+
		"or of the form 'region-chunk:<x>,<y>:<region-file>',\n" +
		"which will dump the specified chunk in the given region file.";

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println(USAGE);
			System.exit(1);
		}
		if ("-?".equals(args[0]) || "-h".equals(args[0])) {
			System.out.println(USAGE);
			System.exit(0);
		}

		String fn = args[0];
		//String outFn = fn+".out";
		//System.out.println("parsing "+fn);
		//System.out.println("writing output to "+outFn);
		//PrintStream out = new PrintStream(new File(outFn));
		AnyTag tag = read(fn);
		PrintStream out = System.out;
		out.print(tag.dumpTree());
		out.close();
		System.out.println("done");
	}

}
