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
package se.llbit.chunky.resources;
import org.apache.commons.math3.util.FastMath;

import java.awt.image.BufferedImage;

import se.llbit.chunky.world.Messages;
import se.llbit.resources.ImageLoader;

/**
 * Miscellaneous images used by Chunky.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
@SuppressWarnings("javadoc")
public interface MiscImages {
	BufferedImage face = ImageLoader.get(Messages.getString("MiscImages.0")); //$NON-NLS-1$
	BufferedImage face_t = ImageLoader.get(Messages.getString("MiscImages.1")); //$NON-NLS-1$
	BufferedImage home = ImageLoader.get(Messages.getString("MiscImages.2")); //$NON-NLS-1$
	BufferedImage home_t = ImageLoader.get(Messages.getString("MiscImages.3")); //$NON-NLS-1$
	BufferedImage clock = ImageLoader.get(Messages.getString("MiscImages.4")); //$NON-NLS-1$
	BufferedImage load = ImageLoader.get(Messages.getString("MiscImages.5")); //$NON-NLS-1$
	BufferedImage corruptLayer = ImageLoader.get(Messages.getString("MiscImages.Corrupt_Layer")); //$NON-NLS-1$
}
