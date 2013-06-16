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
package se.llbit.nbt.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import se.llbit.nbt.AnyTag;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.CompoundTag;

@SuppressWarnings("javadoc")
public class PlayerExtractor {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("arguments: <NBT file>");
			System.exit(1);
		}

		String fn = args[0];
		String outFn = "player.dat";
		try {
			System.out.println("parsing "+fn);
			DataInputStream in = new DataInputStream(new GZIPInputStream(new FileInputStream(fn)));

			Set<String> request = new HashSet<String>();
			request.add(".Data.Player");
			Map<String, AnyTag> result = NamedTag.quickParse(in, request);
			in.close();

			AnyTag playerTag = result.get(".Data.Player");

			System.out.println("writing output to "+outFn);
			DataOutputStream out = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(outFn)));
			NamedTag rootTag = new NamedTag(new StringTag(""), (CompoundTag) playerTag);
			rootTag.write(out);
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
