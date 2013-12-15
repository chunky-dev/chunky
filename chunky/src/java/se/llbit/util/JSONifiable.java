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
package se.llbit.util;

import se.llbit.json.JsonObject;

/**
 * Interface for things that are serialized to JSON.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public interface JSONifiable {
	/**
	 * Serialize this object to a JSON object.
	 * @return JSON object
	 */
	JsonObject toJson();

	/**
	 * Deserialize this object from a JSON object.
	 * @param obj
	 */
	void fromJson(JsonObject obj);
}
