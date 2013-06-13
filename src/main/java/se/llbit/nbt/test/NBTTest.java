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
import org.apache.commons.math3.util.FastMath;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.zip.GZIPInputStream;

import se.llbit.nbt.AnyTag;
import se.llbit.nbt.NamedTag;

@SuppressWarnings("javadoc")
public class NBTTest {

	public static void main(String[] args) {
		String fn = "test.chunk";
		try {
			System.out.println("parsing test.chunk");
			DataInputStream in = new DataInputStream(new GZIPInputStream(new FileInputStream(fn)));
			AnyTag tag = NamedTag.read(in);
			PrintStream out = new PrintStream(new File("output.test"));
			out.print(tag.dumpTree());
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
