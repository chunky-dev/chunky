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
package se.llbit.resources;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

/**
 * Utility class for image loading.
 * 
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public final class ImageLoader {
	
	private static final Logger logger =
			Logger.getLogger(ImageLoader.class);
	
	private final static HashMap<String, BufferedImage> map = new HashMap<String, BufferedImage>();
	private final static BufferedImage missingImage;
	
	private ImageLoader() { }
	
	static {
		missingImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		Graphics g = missingImage.getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, 16, 16);
		g.setColor(Color.red);
		g.drawLine(0, 0, 16, 16);
		g.drawLine(0, 16, 16, 0);
		g.setColor(Color.white);
		g.drawRect(0, 0, 16, 16);
		g.dispose();
		map.put("missing-image", missingImage);
	}
	
	/**
	 * Attempt to load an image with the given resource name.
	 * If no image is found for that resource name the default
	 * missing image is returned.
	 * 
	 * @param resourceName
	 * @return Image for the given resource name
	 */
	public static synchronized BufferedImage get(String resourceName) {
		if (!map.containsKey(resourceName)) {
			loadImage(resourceName);
		}
		return map.get(resourceName);
	}
	
	/**
	 * Attempt to load an image with a given resource name.
	 * The Class loader is used to find the image resource.
	 * 
	 * @param resourceName
	 */
	private static synchronized void loadImage(String resourceName) {
		URL url = ImageLoader.class.getResource("/" + resourceName);
		if (url == null) {
			logger.info("Could not load image: " + resourceName);
			map.put(resourceName, missingImage);
			return;
		}
		try {
			BufferedImage imgIn = ImageIO.read(url);

			BufferedImage img;
			if (imgIn.getType() == BufferedImage.TYPE_INT_ARGB) {
				img = imgIn;
			} else {
				// convert to int ARGB
				img = new BufferedImage(imgIn.getWidth(),
						imgIn.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics g = img.createGraphics();
				g.drawImage(imgIn, 0, 0, null);
				g.dispose();
			}
			map.put(resourceName, img);
		} catch (IOException e) {
			map.put(resourceName, missingImage);
		}
	}

}
