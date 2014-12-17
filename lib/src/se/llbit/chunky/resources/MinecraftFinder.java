/* Copyright (c) 2013-2014 Jesper Öqvist <jesper@llbit.se>
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import se.llbit.chunky.PersistentSettings;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonParser.SyntaxError;
import se.llbit.log.Log;
import se.llbit.util.OSDetector;
import se.llbit.util.Util;

/**
 * Locates the Minecraft installation.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class MinecraftFinder {

	private static File minecraftJar = null;
	private static boolean foundJar = false;

	/**
	 * @return The configured Minecraft directory.
	 */
	public static File getMinecraftDirectory() {
		File mcDir = new File(PersistentSettings.getMinecraftDirectory());
		if (getMinecraftJar(mcDir, false) == null) {
			mcDir = getDefaultMinecraftDirectory();
		}
		Log.infofmt("Found Minecraft directory %s", mcDir.getAbsolutePath());
		return mcDir;
	}

	/**
	 * @return The platform-dependent default Minecraft directory.
	 */
	public static File getDefaultMinecraftDirectory() {
		String home = System.getProperty("user.home", ".");   //$NON-NLS-1$ //$NON-NLS-2$

		switch (OSDetector.getOS()) {
		case WIN:
			String appdata = System.getenv("APPDATA");	//$NON-NLS-1$
			if (appdata != null) {
				return new File(appdata, ".minecraft");	//$NON-NLS-1$
			} else {
				return new File(home, ".minecraft");  //$NON-NLS-1$
			}
		case MAC:
			return new File(home, "Library/Application Support/minecraft");	//$NON-NLS-1$
		default:
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
	 * NB: This method caches its result
	 * @return File reference to the latest Minecraft jar of the local
	 * Minecraft installation, or <code>null</code> if the Minecraft jar
	 * could not be found.
	 */
	public static final File getMinecraftJar() {
		synchronized (MinecraftFinder.class) {
			if (!foundJar) {
				minecraftJar = getMinecraftJar(getMinecraftDirectory(), true);
				if (minecraftJar == null) {
					// Fall back on downloaded Jar if available
					File resourceDir = new File(
							PersistentSettings.getSettingsDirectory(),
							"resources");
					if (resourceDir.isDirectory()) {
						File jar = new File(resourceDir, "minecraft.jar");
						if (jar.isFile() && jar.canRead()) {
							minecraftJar = jar;
						}
					}
				}
				foundJar = true;
			}
			return minecraftJar;
		}
	}

	/**
	 * NB: This method caches its result
	 * @return File reference to the latest Minecraft jar of the local
	 * Minecraft installation, or throws a FileNotFoundException if it
	 * could not be found.
	 */
	public static final File getMinecraftJarNonNull() throws FileNotFoundException {
		File jarFile = getMinecraftJar();
		if (jarFile == null) {
			throw new FileNotFoundException("Could not locate Minecraft Jar!");
		}
		return jarFile;
	}

	/**
	 * @param mcDir directory to search for Minecraft jar
	 * @param debug print debug messages if {@code true}
	 * @return File reference to the latest Minecraft jar of the local
	 * Minecraft installation, or <code>null</code> if the Minecraft jar
	 * could not be found.
	 */
	public static final File getMinecraftJar(File mcDir, boolean debug) {
		// MC 1.6.1 and above jars located in subdirectories under versions/
		File versions = new File(mcDir, "versions");
		if (versions.isDirectory()) {
			File[] dirs = versions.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			});
			List<MCVersion> versionDirs = new ArrayList<MCVersion>();
			for (int i = dirs.length-1; i >= 0; --i) {
				File jarPath = new File(dirs[i], dirs[i].getName() + ".jar");
				if (!jarPath.isFile()) {
					continue;
				}
				String releaseTime = "";
				try {
					File jsonFile = new File(dirs[i], dirs[i].getName() + ".json");
					FileInputStream in = new FileInputStream(jsonFile);
					JsonParser parser = new JsonParser(in);
					JsonObject obj = parser.parse().object();
					releaseTime =  obj.get("releaseTime").stringValue("");
				} catch (IOException e) {
					// Json parsing failed
				} catch (SyntaxError e) {
					// Json parsing failed
				} finally {
					versionDirs.add(new MCVersion(jarPath, releaseTime));
				}
			}

			// select latest available minecraft version
			if (!versionDirs.isEmpty()) {
				MCVersion latest = versionDirs.get(0);
				for (int i = 1; i < versionDirs.size()-1; ++i) {
					if (versionDirs.get(i).compareTo(latest) > 0) {
						latest = versionDirs.get(i);
					}
				}
				if (debug) {
					Log.infofmt("Found latest Minecraft version: %s",
						latest.jar.getAbsolutePath());
				}
				return latest.jar;
			}
		}
		// Backwards compatibility for pre-1.6.1:
		File bin = new File(mcDir, "bin");
		if (bin.isDirectory()) {
			for (File file : bin.listFiles()) {
				if (file.getName().equalsIgnoreCase("minecraft.jar")) {
					return new File(bin, file.getName());
				}
			}
		}
		// Failed to locate Minecraft Jar
		return null;
	}

	private static class MCVersion implements Comparable<MCVersion> {

		private final File jar;
		private final Date timestamp;

		/**
		 * If time  not ISO 8601, the timestamp is set to {@code new Date(0)}
		 * @param jarFile
		 * @param time
		 */
		public MCVersion(File jarFile, String time) {
			jar = jarFile;
			timestamp = Util.dateFromISO8601(time);
		}

		@Override
		public int compareTo(MCVersion other) {
			return timestamp.compareTo(other.timestamp);
		}

		@Override
		public String toString() {
			return jar.getName();
		}
	}
}
