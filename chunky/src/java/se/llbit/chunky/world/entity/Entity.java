/* Copyright (c) 2014-2015 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.world.entity;

import java.util.Collection;

import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Vector3d;
import se.llbit.math.primitive.Primitive;

/**
 * Represents Minecraft entities that are not stored in the octree.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
abstract public class Entity {
	protected final Vector3d position;

	protected Entity(Vector3d position) {
		this.position = new Vector3d(position);
	}

	abstract public Collection<Primitive> primitives(Vector3d offset);

	/**
	 * Marshalls this entity to JSON.
	 * @return JSON object representing this entity.
	 */
	abstract public JsonValue toJson();

	/**
	 * Unmarshalls an entity object from JSON data.
	 * @param json json data.
	 * @return unmarshalled entity, or {@code null} if it was not a valid entity.
	 */
	public static Entity fromJson(JsonObject json) {
		String kind = json.get("kind").stringValue("");
		if (kind.equals("painting")) {
			return PaintingEntity.fromJson(json);
		} else if (kind.equals("sign")) {
			return SignEntity.fromJson(json);
		} else if (kind.equals("wallsign")) {
			return WallSignEntity.fromJson(json);
		} else if (kind.equals("skull")) {
			return SkullEntity.fromJson(json);
		} else if (kind.equals("player")) {
			return PlayerEntity.fromJson(json);
		}
		return null;
	}
}
