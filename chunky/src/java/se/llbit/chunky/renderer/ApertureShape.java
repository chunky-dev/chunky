/* Copyright (c) 2022 Chunky contributors
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
package se.llbit.chunky.renderer;

import se.llbit.util.Registerable;

public enum ApertureShape implements Registerable {
  CIRCLE("Circle"),
  HEXAGON("Hexagon", "hexagon-aperture.png"),
  PENTAGON("Pentagon", "pentagon-aperture.png"),
  STAR("Star", "star-aperture.png"),
  GAUSSIAN("Gaussian", "gaussian-aperture.png"),
  CUSTOM("Custom");

  private final String resourceName;

  private final String name;

  ApertureShape(String name) {
    this(name, null);
  }

  ApertureShape(String name, String resourceName) {
    this.name = name;
    this.resourceName = resourceName;
  }

  public String getResourceName() {
    return resourceName;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return "";
  }

  @Override
  public String getId() {
    return this.name();
  }

  public static ApertureShape get(String name) {
    try {
      return valueOf(name);
    } catch (IllegalArgumentException e) {
      return CIRCLE;
    }
  }
}