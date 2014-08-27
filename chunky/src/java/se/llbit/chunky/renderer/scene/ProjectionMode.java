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
package se.llbit.chunky.renderer.scene;

import se.llbit.json.JsonValue;

/**
 * Projection mode enumeration
 */
@SuppressWarnings("javadoc")
public enum ProjectionMode {
	PINHOLE("Standard"),
	PARALLEL("Parallel"),
	FISHEYE("Fisheye"),
	PANORAMIC("Panoramic (equirectangular)"),
	PANORAMIC_SLOT("Panoramic (slot)"),
	STEREOGRAPHIC("Stereographic");

	private final String niceName;

	private ProjectionMode(String niceName) {
		this.niceName = niceName;
	}

	@Override
	public String toString() {
		return niceName;
	}

	public static ProjectionMode fromJson(JsonValue jsonValue) {
		try {
			return ProjectionMode.valueOf(jsonValue.stringValue("PINHOLE"));
		} catch (IllegalArgumentException e) {
			return PINHOLE;
		}
	}
}