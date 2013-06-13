/* Copyright (c) 2010 Jesper Öqvist <jesper@llbit.se>
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import se.llbit.nbt.AnyTag;
import se.llbit.nbt.LongTag;
import se.llbit.nbt.NamedTag;

@SuppressWarnings("javadoc")
public class SeedTool {

	public static void main(String[] args) {

		if (args.length < 3) {
			System.out.println("arguments: <level.dat> <new seed> <output file>");
			System.exit(0);
		}

		String fn = args[0];
		Long seed = Long.parseLong(args[1]);
		try {
			System.out.println("loading save: "+fn);
			DataInputStream in = new DataInputStream(new GZIPInputStream(new FileInputStream(fn)));
			AnyTag tag = NamedTag.read(in);
			LongTag seedTag = (LongTag) tag.unpack().get("Data").get("RandomSeed");
			seedTag.setData(seed);
			DataOutputStream out = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(args[2])));
			tag.write(out);
			out.close();
			System.out.println("done");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
