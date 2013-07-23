/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
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
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * Locates the Minecraft installation.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class MinecraftFinder {

	/**
	 * Attempts to locate the local Minecraft installation directory.
	 * @return The directory of the local Minecraft installation
	 */
	public static File getMinecraftDirectory() {
		String home = System.getProperty("user.home", ".");   //$NON-NLS-1$ //$NON-NLS-2$
		String os = System.getProperty("os.name").toLowerCase();  //$NON-NLS-1$

		if (os.contains("win")) {  //$NON-NLS-1$
			String appdata = System.getenv("APPDATA");	//$NON-NLS-1$
			if (appdata != null)
				return new File(appdata, ".minecraft");	//$NON-NLS-1$
			else
				return new File(home, ".minecraft");  //$NON-NLS-1$
		} else if (os.contains("mac")) {  //$NON-NLS-1$
			return new File(home, "Library/Application Support/minecraft");	//$NON-NLS-1$
		} else {
			return new File(home, ".minecraft");  //$NON-NLS-1$
		}
	}

	/**
	 * @return The saves directory of the local Minecraft installation
	 */
	public static File getSavesDirectory() {
		return new File(getMinecraftDirectory(), "saves");
	}

	/**
	 * @return The texture pack directory of the local Minecraft installation
	 */
	public static File getTexturePacksDirectory() {
		return new File(getMinecraftDirectory(), "texturepacks");
	}

	/**
	 * @return The resource pack directory of the local Minecraft installation
	 */
	public static File getResourcePacksDirectory() {
		return new File(getMinecraftDirectory(), "resourcepacks");
	}

	/**
	 * @return File reference to the latest Minecraft jar of the local
	 * Minecraft installation, or <code>null</code> if the Minecraft jar
	 * could not be found.
	 */
	public static final File getMinecraftJar() {
		File minecraft = getMinecraftDirectory();
		// MC 1.6.1 and above jars located in subdirectories under versions/
		File versions = new File(minecraft, "versions");
		if (versions.isDirectory()) {
			File[] dirs = versions.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			});
			List<VersionDir> versionDirs = new ArrayList<VersionDir>();
			for (int i = dirs.length-1; i >= 0; --i) {
				File jarPath = new File(dirs[i], dirs[i].getName() + ".jar");
				if (!jarPath.isFile()) {
					continue;
				}
				JsonParser parser = new JsonParser();
				String releaseTime = "";
				try {
					File jsonFile = new File(dirs[i], dirs[i].getName() + ".json");
					JsonElement json = parser.parse(new FileReader(jsonFile));
					JsonObject obj = json.getAsJsonObject();
					JsonElement relTime = obj.get("releaseTime");
					releaseTime = relTime.getAsString();
				} catch (JsonIOException e) {
				} catch (JsonSyntaxException e) {
				} catch (FileNotFoundException e) {
				} catch (IllegalStateException e) {
					// Json parsing failed - assume
				} catch (NullPointerException e) {
					// Json parsing failed
				} finally {
					versionDirs.add(new VersionDir(dirs[i], releaseTime));
				}
			}

			// select latest available minecraft version
			if (!versionDirs.isEmpty()) {
				VersionDir latest = versionDirs.get(0);
				for (int i = 1; i < versionDirs.size()-1; ++i) {
					if (versionDirs.get(i).compareTo(latest) > 0) {
						latest = versionDirs.get(i);
					}
				}
				return latest.dir;
			}
		}
		// Backwards compatibility for pre-1.6.1:
		File bin = new File(minecraft, "bin");
		if (bin.isDirectory()) {
			for (File file : bin.listFiles()) {
				if (file.getName().equalsIgnoreCase("minecraft.jar")) {
					return new File(bin, file.getName());
				}
			}
		}
		return null;
	}

	private static class VersionDir implements Comparable<VersionDir> {
		private static final DateFormat dateFormat =
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

		private final File dir;
		private Date timestamp;

		/**
		 * If time is a string less than zero, or not ISO 8601, the
		 * timestamp is set to <code>new Date(0)</code>
		 * @param directory
		 * @param time
		 */
		public VersionDir(File directory, String time) {
			dir = directory;
			if (time.length() < 6) {
				timestamp = new Date(0);
				return;
			}
			try {
				// insert "GMT"
				if (time.endsWith("Z")) {
					time = time.substring(0, time.length()-1) + "GMT-00:00";
				} else {
					time = time.substring(0, time.length()-6) + "GMT" +
							time.substring(time.length()-6, time.length());
				}
				timestamp = dateFormat.parse(time);
			} catch (ParseException e) {
				timestamp = new Date(0);
			}
		}

		@Override
		public int compareTo(VersionDir other) {
			return timestamp.compareTo(other.timestamp);
		}

		@Override
		public String toString() {
			return dir.getName();
		}
	}
}
