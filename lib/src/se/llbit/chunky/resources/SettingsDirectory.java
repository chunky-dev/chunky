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
import java.net.URISyntaxException;
import java.net.URL;

import se.llbit.chunky.ChunkySettings;

public final class SettingsDirectory {
	private static final String SETTINGS_DIR = ".chunky";

	private SettingsDirectory() {}

	/**
	 * @return {@code true} if the settings directory could be located
	 */
	public static boolean findSettingsDirectory() {
		return settingsDirectory() != null;
	}

	/**
	 * Falls back on home directory
	 * @return The current settings directory, or the default one.
	 */
	public static File defaultSettingsDirectory() {
		File dir = settingsDirectory();
		return (dir == null) ? getHomeDirectory() : dir;
	}

	/**
	 * @return {@code null} if the settings directory could not be located
	 */
	public static File settingsDirectory() {
		File directory = null;
		directory = getWorkingDirectory();
		if (isSettingsDirectory(directory)) {
			return directory;
		}
		directory = getProgramDirectory();
		if (isSettingsDirectory(directory)) {
			return directory;
		}
		directory = getHomeDirectory();
		if (isSettingsDirectory(directory)) {
			return directory;
		}
		return null;
	}

	private static boolean isSettingsDirectory(File settingsDir) {
		if (settingsDir != null && settingsDir.exists() &&
				settingsDir.isDirectory() && settingsDir.canWrite()) {
			File settingsFile = new File(settingsDir, ChunkySettings.SETTINGS_FILE);
			if (settingsFile.isFile() && settingsFile.canRead()) {
				return true;
			}
		}
		return false;
	}

	public static File getHomeDirectory() {
		String workingDir = System.getProperty("user.home");
		if (workingDir != null && !workingDir.isEmpty()) {
			return new File(workingDir, SETTINGS_DIR);
		}
		return null;
	}

	public static File getWorkingDirectory() {
		String workingDir = System.getProperty("user.dir");
		if (workingDir != null && !workingDir.isEmpty()) {
			return new File(workingDir);
		}
		return null;
	}

	public static File getProgramDirectory() {
		URL location = SettingsDirectory.class.getProtectionDomain()
				.getCodeSource().getLocation();
		try {
			File dir = new File(location.toURI());
			if (dir.isFile()) {
				dir = dir.getParentFile();
			}
			return dir;
		} catch (URISyntaxException e) {
			return null;
		}
	}
}
