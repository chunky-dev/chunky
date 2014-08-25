/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.launcher;

import java.awt.Toolkit;
import java.net.URL;

import javax.swing.ImageIcon;

public class Icons {

	final public static ImageIcon expand = loadIcon("expand");
	final public static ImageIcon collapse = loadIcon("collapse");
	final public static ImageIcon expandHover = loadIcon("expand-hover");
	final public static ImageIcon collapseHover = loadIcon("collapse-hover");
	final public static ImageIcon cached = loadIcon("cached");
	final public static ImageIcon failed = loadIcon("failed");
	final public static ImageIcon refresh = loadIcon("refresh");

	private static ImageIcon loadIcon(String image) {
		URL url = Icons.class.getResource("/" + image + ".png");
		if (url != null) {
			return new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
		}
		return null;
	}

}
