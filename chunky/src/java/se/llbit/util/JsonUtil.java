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
package se.llbit.util;

import se.llbit.json.Json;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonValue;
import se.llbit.math.QuickMath;
import se.llbit.math.Vector3;
import se.llbit.nbt.Tag;

/**
 * Utility methods for working with JSON vectors and lists.
 */
public final class JsonUtil {

  private JsonUtil() {
  }

  public static JsonArray listTagToJson(Tag tag) {
    JsonArray json = new JsonArray();
    for (Tag val : tag.asList()) {
      json.add(QuickMath.degToRad(val.floatValue()));
    }
    return json;
  }

  public static Vector3 vec3FromJson(JsonValue json) {
    JsonArray array = json.array();
    double x = array.size() >= 1 ? array.get(0).asDouble(0) : 0;
    double y = array.size() >= 2 ? array.get(1).asDouble(0) : 0;
    double z = array.size() >= 3 ? array.get(2).asDouble(0) : 0;
    return new Vector3(x, y, z);
  }

  public static JsonValue vec3ToJson(Vector3 vec) {
    JsonArray array = new JsonArray();
    array.add(Json.of(vec.x));
    array.add(Json.of(vec.y));
    array.add(Json.of(vec.z));
    return array;
  }
}
