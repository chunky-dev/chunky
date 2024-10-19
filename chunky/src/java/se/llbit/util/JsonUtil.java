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

import se.llbit.json.*;
import se.llbit.math.ColorUtil;
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

  public static Vector3 vec3FromJsonArray(JsonValue json) {
    JsonArray array = json.array();
    double x = array.size() >= 1 ? array.get(0).asDouble(0) : 0;
    double y = array.size() >= 2 ? array.get(1).asDouble(0) : 0;
    double z = array.size() >= 3 ? array.get(2).asDouble(0) : 0;
    return new Vector3(x, y, z);
  }

  public static Vector3 vec3FromJsonObject(JsonValue json) {
    JsonObject obj = json.object();
    double x = obj.get("x").asDouble(0);
    double y = obj.get("y").asDouble(0);
    double z = obj.get("z").asDouble(0);
    return new Vector3(x, y, z);
  }

  public static JsonValue vec3ToJson(Vector3 vec) {
    JsonArray array = new JsonArray();
    array.add(Json.of(vec.x));
    array.add(Json.of(vec.y));
    array.add(Json.of(vec.z));
    return array;
  }

  public static Vector3 rgbFromJson(JsonValue json) {
    Vector3 color = new Vector3();
    rgbFromJson(json, color);
    return color;
  }

  /**
   * Parse RGB color from JSON.
   *
   * @param json  Either an object with red, green and blue keys (0…1), an array with 1…3 values (r,g,b) in range 0…1, or six digit hex color.
   * @param color Target color vector (r,g,b from 0…1)
   */
  public static void rgbFromJson(JsonValue json, Vector3 color) {
    if (json.isObject()) {
      color.set(
        json.object().get("red").doubleValue(0),
        json.object().get("green").doubleValue(0),
        json.object().get("blue").doubleValue(0)
      );
    } else if (json.isArray()) {
      // Maintain backwards-compatibility with scenes saved in older Chunky versions (eg. for sky color)
      color.set(vec3FromJsonArray(json));
    } else {
      ColorUtil.fromHexString(json.stringValue("#000000"), color);
    }
  }

  /**
   * Serialize an RGB color to JSON in a way that can be parsed by {@link #rgbFromJson(JsonValue)}.
   * <p/>
   * Depending on the <code>chunky.colorSerializationFormat</code> system property, this returns either an object with red, green and blue keys, or a hex string.
   *
   * @param color Color vector (r,g,b from 0…1)
   * @return Serialized color
   */
  public static JsonValue rgbToJson(Vector3 color) {
    switch (System.getProperty("chunky.colorSerializationFormat", "object")) {
      case "hex": {
        return new JsonString(String.format("#%02x%02x%02x", (int) (color.x * 255), (int) (color.y * 255), (int) (color.z * 255)));
      }
      case "object":
      default: {
        JsonObject colorObj = new JsonObject();
        colorObj.add("red", color.x);
        colorObj.add("green", color.y);
        colorObj.add("blue", color.z);
        return colorObj;
      }
    }
  }
}
