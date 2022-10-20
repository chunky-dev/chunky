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
package se.llbit.chunky.renderer.scene.camera;

public enum ApertureShape {
  CIRCLE,
  HEXAGON("hexagon-aperture.png"),
  PENTAGON("pentagon-aperture.png"),
  STAR("star-aperture.png"),
  GAUSSIAN("gaussian-aperture.png"),
  CUSTOM;

  private final String resourceName;

  ApertureShape() {
    resourceName = null;
  }

  ApertureShape(String resourceName) {
    this.resourceName = resourceName;
  }

  public String getResourceName() {
    return resourceName;
  }
}
