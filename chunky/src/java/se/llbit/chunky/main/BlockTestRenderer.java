/* Copyright (c) 2012-2013 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.main;

import java.io.File;
import java.io.PrintStream;

import se.llbit.chunky.ChunkySettings;
import se.llbit.chunky.renderer.test.TestRenderer;
import se.llbit.chunky.resources.MinecraftFinder;
import se.llbit.chunky.resources.TexturePackLoader;
import se.llbit.chunky.resources.TexturePackLoader.TextureLoadingError;
import se.llbit.chunky.world.BlockData;

/**
 * Test renderer application
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class BlockTestRenderer {

	/**
	 * Entry point
	 * @param args
	 */
	public static void main(String[] args) {
		String lastTexturePack = ChunkySettings.getLastTexturePack();
		try {
			if (!lastTexturePack.isEmpty()) {
				TexturePackLoader.loadTexturePack(new File(lastTexturePack), false);
			} else {
				TexturePackLoader.loadTexturePack(MinecraftFinder.getMinecraftJar(), false);
			}
		} catch (TextureLoadingError e) {
			System.err.println("Error: failed to load texture pack!");
		}

		String block = "";
		String targetFile = "";
		for (int i = 0; i < args.length; ++i) {
			String arg = args[i];
			if (arg.equals("-help") || arg.equals("-h")) {
				printHelp(System.out);
				return;
			} else if (arg.equals("-o")) {
				if (i+1 >= args.length) {
					System.err.println("Missing target file argument!");
					printHelp(System.out);
					System.exit(1);
				} else {
					targetFile = args[i+1];
					i += 1;
				}
			} else {
				if (block.isEmpty()) {
					block = arg;
				} else {
					System.err.println("Too many arguments!");
					printHelp(System.out);
					System.exit(1);
				}
			}
		}

		TestRenderer renderer;

		if (!block.isEmpty()) {
			int sep = block.indexOf(':');
			String blockPart;
			String metadataPart = "";
			if (sep == -1) {
				blockPart = block;
			} else {
				blockPart = block.substring(0, sep);
				if (sep+1 < block.length()) {
					metadataPart = block.substring(sep+1);
				}
			}
			int blockId = Integer.parseInt(blockPart);
			int metadata = 0;
			if (!metadataPart.isEmpty()) {
				metadata = Integer.parseInt(metadataPart);
			}
			renderer = new TestRenderer(null,
					blockId | (metadata << BlockData.OFFSET),
					targetFile);
		} else {
			renderer = new TestRenderer(null, -1, targetFile);
		}

		renderer.start();
	}

	private static void printHelp(PrintStream out) {
		out.println("Usage: BlockTestRenderer [ID[:METADATA]] [OPTIONS]");
		out.println("    ID         is the id of the block to render");
		out.println("    METADATA   specifies the metadata (TBD)");
		out.println("");
		out.println("Options:");
		out.println("    -o ARG     write rendered image to file ARG");
	}
}
