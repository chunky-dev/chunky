/* Copyright (c) 2014-2016 Jesper Öqvist <jesper@llbit.se>
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

public class LauncherIcon {
  public static final ImageIcon expand = loadIcon("expand");
  public static final ImageIcon collapse = loadIcon("collapse");
  public static final ImageIcon expandHover = loadIcon("expand-hover");
  public static final ImageIcon collapseHover = loadIcon("collapse-hover");
  public static final ImageIcon cached = loadIcon("cached");
  public static final ImageIcon failed = loadIcon("failed");
  public static final ImageIcon refresh = loadIcon("refresh");

  private static ImageIcon loadIcon(String image) {
    URL url = LauncherIcon.class.getResource("/" + image + ".png");
    if (url != null) {
      return new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
    }
    return null;
  }
}
