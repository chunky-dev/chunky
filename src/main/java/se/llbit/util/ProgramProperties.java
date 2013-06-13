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
package se.llbit.util;
import org.apache.commons.math3.util.FastMath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Utility class for managing program properties.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public final class ProgramProperties {

	private static final Logger logger =
			Logger.getLogger(ProgramProperties.class);

	private static final String SETTINGS_DIR = ".chunky";
	private static final String SETTINGS_FILE = "settings.cfg";
	private static Properties properties = new Properties();
	private static String path = SETTINGS_FILE;
	private static File settingsDir;

	private ProgramProperties() { }

	/**
	 * @return The directory where program settings are stored
	 */
	public static File getSettingsDirectory() {
		return settingsDir;
	}

	static {

		String userHome = System.getProperty("user.home");
		if (userHome != null && !userHome.isEmpty()) {
			settingsDir = new File(userHome, SETTINGS_DIR);
			if (!settingsDir.exists())
				settingsDir.mkdir();
			if (settingsDir.exists() && settingsDir.isDirectory() &&
					settingsDir.canWrite()) {

				path = new File(settingsDir, SETTINGS_FILE).getAbsolutePath();
			}
		}

		if (path == SETTINGS_FILE) {
			settingsDir = new File(System.getProperty("user.dir"));
			path = new File(settingsDir, SETTINGS_FILE).getAbsolutePath();
		}

		loadProperties();
	}

	/**
	 * Load saved properties.
	 */
	private static void loadProperties() {
		try {
			InputStream in = new FileInputStream(path);
			properties.load(in);
			logger.info("Properties loaded from " + path);
		} catch (IOException e) {
			logger.warn("Could not load the property file " + path +
					" - defaults will be used");
		}
	}

	/**
	 * Save settings to file.
	 */
	private static void saveProperties() {
		try {
			OutputStream out = new FileOutputStream(path);
			properties.store(out, "Chunky preferences");
			logger.info("Saved property file " + path);
		} catch (IOException e1) {
			logger.warn("Exception occurred when saving property file " + path, e1);
		}
	}

	/**
	 * Get a property value
	 * @param name Property name
	 * @param defValue Default value
	 * @return The value of the property, or <code>defValue</code> if
	 * the property was not set.
	 */
	public static String getProperty(String name, String defValue) {
		return properties.getProperty(name, defValue);
	}

	/**
	 * Get the integer value of a property
	 * @param name Property name
	 * @param defValue Default value
	 * @return The value of the property, or <code>defValue</code> if
	 * the property was not set, or was not set to a valid integer value.
	 */
	public static int getIntProperty(String name, int defValue) {
		try {
			return Integer.parseInt(properties.getProperty(name, ""+defValue));
		} catch (NumberFormatException e) {
			return defValue;
		}
	}

	/**
	 * Set the value of a property
	 * @param name Property name
	 * @param value Property value
	 */
	public static void setProperty(String name, String value) {
		// only set the property if the value has changed
		if ((value == null && properties.getProperty(name) != null)
				|| (!value.equals(properties.getProperty(name)))) {

			properties.setProperty(name, value);
			saveProperties();
		}
	}

	/**
	 * Get a propertie's value
	 * @param name Property name
	 * @return The propertie's value, or <code>null</code> if the property was not set.
	 */
	public static String getProperty(String name) {
		return properties.getProperty(name);
	}

	/**
	 * Clears the property
	 * @param name
	 */
	public static void removeProperty(String name) {
		if (properties.containsKey(name)) {
			properties.remove(name);
			saveProperties();
		}
	}

	/**
	 * @param name
	 * @return <code>true</code> if the property map contains a property of
	 * the given name
	 */
	public static boolean containsKey(String name) {
		return properties.containsKey(name);
	}

	/**
	 * @return The default scene directory
	 */
	public static File getSceneDirectory() {
		if (containsKey("sceneDirectory")) {
			return new File(getProperty("sceneDirectory"));
		} else {
			return new File(getSettingsDirectory(), "Scenes");
		}
	}
}

