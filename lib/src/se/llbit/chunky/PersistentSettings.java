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
package se.llbit.chunky;

import java.io.File;

import se.llbit.chunky.renderer.RenderConstants;
import se.llbit.chunky.resources.SettingsDirectory;

/**
 * Utility class for managing program properties.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public final class PersistentSettings {

	private static JsonSettings settings = new JsonSettings();

	public static final String SETTINGS_FILE = "chunky.json";
	public static final String DEFAULT_SCENE_DIRECTORY_NAME = "scenes";

	public static final int DEFAULT_RAY_DEPTH = 5;
	public static final int DEFAULT_SPP_TARGET = 1000;

	/**
	 * Default canvas width
	 */
	public static final int DEFAULT_3D_CANVAS_WIDTH = 400;

	/**
	 * Default canvas height
	 */
	public static final int DEFAULT_3D_CANVAS_HEIGHT = 400;


	private static File settingsDir = null;
	private static File settingsFile = null;

	private PersistentSettings() { }

	/**
	 * @return The directory where program settings are stored
	 */
	public static File getSettingsDirectory() {
		return settingsDir;
	}

	static {
		settingsDir = SettingsDirectory.defaultSettingsDirectory();
		settingsFile = new File(settingsDir, SETTINGS_FILE);
		settings.load(settingsFile);
	}

	private static void save() {
		settings.save(settingsFile);
	}

	/**
	 * @return The default scene directory
	 */
	public static File getSceneDirectory() {
		String defaultPath = new File(getSettingsDirectory(), DEFAULT_SCENE_DIRECTORY_NAME).getAbsolutePath();
		return new File(settings.getString("sceneDirectory", defaultPath));
	}

	/**
	 * @return Default number of render threads
	 */
	public static int getNumThreads() {
		return settings.getInt("numThreads", RenderConstants.NUM_RENDER_THREADS_DEFAULT);
	}

	/**
	 * Set default number of render threads
	 * @param numThreads
	 */
	public static void setNumThreads(int numThreads) {
		numThreads = Math.max(RenderConstants.NUM_RENDER_THREADS_MIN, numThreads);
		numThreads = Math.min(RenderConstants.NUM_RENDER_THREADS_MAX, numThreads);
		settings.setInt("numThreads", numThreads);
		save();
	}

	/**
	 * @return CPU load setting
	 */
	public static int getCPULoad() {
		return settings.getInt("cpuLoad", RenderConstants.CPU_LOAD_DEFAULT);
	}

	/**
	 * Change the default CPU load
	 * @param cpuLoad
	 */
	public static void setCPULoad(int cpuLoad) {
		cpuLoad = Math.max(1, cpuLoad);
		cpuLoad = Math.min(100, cpuLoad);
		settings.setInt("cpuLoad", cpuLoad);
		save();
	}

	public static void setLastWorld(File worldDirectory) {
		settings.setString("lastWorld", worldDirectory.getAbsolutePath());
		save();
	}

	public static File getLastWorld() {
		String lastWorld = settings.getString("lastWorld", "");
		return lastWorld.isEmpty() ? null : new File(lastWorld);
	}

	public static String getLastTexturePack() {
		return settings.getString("lastTexturePack", "");
	}

	public static void setRayDepth(int rayDepth) {
		settings.setInt("rayDepth", rayDepth);
		save();
	}

	public static int getSppTargetDefault() {
		return settings.getInt("sppTargetDefault", DEFAULT_SPP_TARGET);
	}

	public static int getRayDepth() {
		return settings.getInt("rayDepth", DEFAULT_RAY_DEPTH);
	}

	public static int get3DCanvasHeight() {
		return settings.getInt("3dcanvas.height", DEFAULT_3D_CANVAS_HEIGHT);
	}

	public static int get3DCanvasWidth() {
		return settings.getInt("3dcanvas.width", DEFAULT_3D_CANVAS_WIDTH);
	}

	public static void setSceneDirectory(File dir) {
		settings.setString("sceneDirectory", dir.getAbsolutePath());
		save();
	}

	public static boolean containsKey(String key) {
		return settings.containsKey(key);
	}

	public static void setSppTargetDefault(int targetSPP) {
		settings.setInt("sppTargetDefault", targetSPP);
		save();
	}

	public static boolean getAutoLock() {
		return settings.getBool("autoLock", true);
	}

	public static void setAutoLock(boolean selected) {
		settings.setBool("autoLock", selected);
		save();
	}

	public static void set3DCanvasSize(int width, int height) {
		settings.setInt("3dcanvas.width", width);
		settings.setInt("3dcanvas.height", height);
		save();
	}

	public static void removeSetting(String name) {
		settings.removeSetting(name);
		save();
	}

	public static void setLastTexturePack(String path) {
		settings.setString("lastTexturePack", path);
		save();
	}

	public static String getMinecraftDirectory() {
		return settings.getString("minecraftDir", "");
	}

	public static void setMinecraftDirectory(String path) {
		settings.setString("minecraftDir", path);
		save();
	}

	public static void setStringOption(String name, String value) {
		settings.setString(name, value);
		save();
	}

	public static void resetOption(String name) {
		settings.removeSetting(name);
		save();
	}
}

