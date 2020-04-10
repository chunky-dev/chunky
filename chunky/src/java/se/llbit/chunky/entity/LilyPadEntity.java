/* Copyright (c) 2019 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.LilyPadMaterial;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Vector2;
import se.llbit.math.Vector3;
import se.llbit.math.primitive.Primitive;
import se.llbit.math.primitive.TexturedTriangle;
import se.llbit.util.MinecraftPRNG;

import java.util.Collection;
import java.util.LinkedList;

public class LilyPadEntity extends Entity {

  private final int rotation;

  public LilyPadEntity(Vector3 position) {
    this(position, 3 & (int) (MinecraftPRNG.rand(
        (long) position.x, (long) position.y, (long) position.z) >> 16));
  }

  public LilyPadEntity(Vector3 position, int rotation) {
    super(position);
    this.rotation = rotation;
  }

  @Override public Collection<Primitive> primitives(Vector3 offset) {
    double x = position.x + offset.x,
        y = position.y + offset.y,
        z = position.z + offset.z;
    double height = y - 0.12;
    Vector3 c1 = new Vector3(x, height, z);
    Vector3 c2 = new Vector3(x, height, z + 1);
    Vector3 c3 = new Vector3(x + 1, height, z + 1);
    Vector3 c4 = new Vector3(x + 1, height, z);
    Vector2 t1 = new Vector2(0, 0);
    Vector2 t2 = new Vector2(0, 1);
    Vector2 t3 = new Vector2(1, 1);
    Vector2 t4 = new Vector2(1, 0);
    Material lilyMaterial = new LilyPadMaterial();
    Collection<Primitive> primitives = new LinkedList<>();
    switch (rotation) {
      case 0:
        primitives.add(new TexturedTriangle(c1, c3, c2, t1, t3, t2, lilyMaterial));
        primitives.add(new TexturedTriangle(c1, c4, c3, t1, t4, t3, lilyMaterial));
        break;
      case 1:
        primitives.add(new TexturedTriangle(c1, c3, c2, t4, t2, t1, lilyMaterial));
        primitives.add(new TexturedTriangle(c1, c4, c3, t4, t3, t2, lilyMaterial));
        break;
      case 2:
        primitives.add(new TexturedTriangle(c1, c3, c2, t3, t1, t4, lilyMaterial));
        primitives.add(new TexturedTriangle(c1, c4, c3, t3, t2, t1, lilyMaterial));
        break;
      case 3:
        primitives.add(new TexturedTriangle(c1, c3, c2, t2, t4, t3, lilyMaterial));
        primitives.add(new TexturedTriangle(c1, c4, c3, t2, t1, t4, lilyMaterial));
        break;
    }
    return primitives;
  }

  @Override public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "lily_pad");
    json.add("position", position.toJson());
    json.add("rotation", rotation);
    return json;
  }

  /**
   * Unmarshall a lily pad entity from JSON data.
   */
  public static Entity fromJson(JsonObject json) {
    Vector3 position = new Vector3();
    position.fromJson(json.get("position").object());
    int rotation = json.get("rotation").intValue(0);
    return new LilyPadEntity(position, rotation);
  }

}
