/*
 * Copyright (c) 2017 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.entity;

import java.util.Collection;

import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Vector3;
import se.llbit.math.primitive.Primitive;

/**
 * Represents Minecraft entities that are not stored in the octree.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
abstract public class Entity {
  public final Vector3 position;

  protected Entity(Vector3 position) {
    this.position = new Vector3(position);
  }

  abstract public Collection<Primitive> primitives(Vector3 offset);

  /**
   * Marshalls this entity to JSON.
   *
   * @return JSON object representing this entity.
   */
  abstract public JsonValue toJson();

  /**
   * Unmarshalls an entity object from JSON data.
   *
   * @param json json data.
   * @return unmarshalled entity, or {@code null} if it was not a valid entity.
   */
  public static Entity fromJson(JsonObject json) {
    String kind = json.get("kind").stringValue("");
    switch (kind) {
      case "painting":
        return PaintingEntity.fromJson(json);
      case "sign":
        return SignEntity.fromJson(json);
      case "wallsign":
        return WallSignEntity.fromJson(json);
      case "skull":
        return SkullEntity.fromJson(json);
      case "player":
        return PlayerEntity.fromJson(json);
      case "standing_banner":
        return StandingBanner.fromJson(json);
      case "wall_banner":
        return WallBanner.fromJson(json);
      case "armor_stand":
        return ArmorStand.fromJson(json);
      case "lily_pad":
        return LilyPadEntity.fromJson(json);
    }
    return null;
  }

  public Vector3 getPosition() {
    return position;
  }

  public void setPosition(Vector3 position) {
    this.position.set(position);
  }
}
