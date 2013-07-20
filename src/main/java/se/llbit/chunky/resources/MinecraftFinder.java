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
	 * @return File reference to the minecraft jar of the local Minecraft
	 * installation, or <code>null</code> if the minecraft jar could not be
	 * found.
	 */
	public static final File getMinecraftJar() {
		File bin = new File(getMinecraftDirectory(), "bin");
		for (File file : bin.listFiles()) {
			if (file.getName().equalsIgnoreCase("minecraft.jar")) {
				return new File(bin, file.getName());
			}
		}
		return null;
	}

}
