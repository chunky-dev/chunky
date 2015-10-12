/* Copyright (c) 2014-2015 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Utility class to download Minecraft
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class MCDownloader {

	/** Download a Minecraft Jar by version name. */
	public static final void downloadMC(String version, File destDir) throws IOException {
		String theUrl = String.format(
				"https://s3.amazonaws.com/Minecraft.Download/versions/%s/%s.jar",
				version, version);
		File destination = new File(destDir, "minecraft.jar");
		System.out.println("url: " + theUrl);
		System.out.println("destination: " + destination.getAbsolutePath());
		URL url = new URL(theUrl);
		ReadableByteChannel inChannel = Channels.newChannel(url.openStream());
		FileOutputStream out = new FileOutputStream(destination);
		out.getChannel().transferFrom(inChannel, 0, Long.MAX_VALUE);
		out.close();
	}

	/** Download a player skin by player name. */
	public static final void downloadSkin(String name, File destDir) throws IOException {
		String theUrl = String.format("http://s3.amazonaws.com/MinecraftSkins/%s.png", name);
		File destination = new File(destDir, name+".skin.png");
		URL url = new URL(theUrl);
		ReadableByteChannel inChannel = Channels.newChannel(url.openStream());
		FileOutputStream out = new FileOutputStream(destination);
		out.getChannel().transferFrom(inChannel, 0, Long.MAX_VALUE);
		out.close();
	}
}
