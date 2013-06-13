/* Copyright (c) 2010-2012 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.world;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Utility methods to scan world directories for region files.
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class WorldScanner {

	private static final Logger logger =
			Logger.getLogger(WorldScanner.class);

	// TODO: use region discovery listener instead
	/**
	 * Interface for world scan listeners
	 */
	public interface Operator {
	    /**
	     * Called when a new region has been discovered
	     * @param worldDirectory
	     * @param x
	     * @param z
	     */
	    void foundRegion(File worldDirectory, int x, int z);
	}

	private static final Pattern anvilPattern =
			Pattern.compile("r\\.([^\\.]+)\\.([^\\.]+)\\.mca");

	/**
	 * Search for existing chunks in the given region directory
	 * @param regionDirectory
	 * @param operator
	 */
	public static void findExistingChunks(File regionDirectory, Operator operator) {
	    if (!regionDirectory.exists())
	        return;

		if (!regionDirectory.isDirectory()) {
			logger.warn(String.format("Failed to read region directory for world %s!",
							regionDirectory.getPath()));
			return;
		}

		for (File anvilFile : regionDirectory.listFiles()) {

			Matcher matcher = anvilPattern.matcher(anvilFile.getName());

			if (!anvilFile.isDirectory() && matcher.matches()) {

				String x = matcher.group(1);
				String z = matcher.group(2);
				if (x != null && z != null) {
				    operator.foundRegion(regionDirectory,
				    		Integer.parseInt(x), Integer.parseInt(z));
				}
			}
		}
	}
}
