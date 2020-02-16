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

import se.llbit.chunky.model.Model;
import se.llbit.chunky.resources.SignTexture;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonParser.SyntaxError;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.Tag;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SignEntity extends Entity {

  public enum Color {
    BLACK(0, 0xFF000000),
    DARK_BLUE(1, 0xFF0000AA),
    DARK_GREEN(2, 0xFF00AA00),
    DARK_AQUA(3, 0xFF00AAAA),
    DARK_RED(4, 0xFFAA0000),
    DARK_PURPLE(5, 0xFFAA00AA),
    GOLD(6, 0xFFFFAA00),
    GRAY(7, 0xFFAAAAAA),
    DARK_GRAY(8, 0xFF555555),
    BLUE(9, 0xFF5555FF),
    GREEN(10, 0xFF55FF55),
    AQUA(11, 0xFF55FFFF),
    RED(12, 0xFFFF5555),
    LIGHT_PURPLE(13, 0xFFFF55FF),
    YELLOW(14, 0xFFFFFF55),
    WHITE(15, 0xFFFFFFFF);

    public final int id;
    public final int rgbColor;

    private static final Map<String, Color> map = new HashMap<>();

    static {
      map.put("dark_blue", DARK_BLUE);
      map.put("dark_green", DARK_GREEN);
      map.put("dark_aqua", DARK_AQUA);
      map.put("dark_red", DARK_RED);
      map.put("dark_purple", DARK_PURPLE);
      map.put("gold", GOLD);
      map.put("gray", GRAY);
      map.put("dark_gray", DARK_GRAY);
      map.put("blue", BLUE);
      map.put("green", GREEN);
      map.put("aqua", AQUA);
      map.put("red", RED);
      map.put("light_purple", LIGHT_PURPLE);
      map.put("yellow", YELLOW);
      map.put("white", WHITE);

    }

    Color(int id, int color) {
      this.id = id;
      this.rgbColor = color;
    }

    public static Color get(String color) {
      if (map.containsKey(color)) {
        return map.get(color);
      } else {
        return Color.BLACK;
      }
    }

    public static Color get(int id) {
      return values()[id & 0xF];
    }
  }


  // Facing south.
  protected static Quad[] sides = {
      // Front face.
      new Quad(new Vector3(0, 9 / 16., 9 / 16.), new Vector3(1, 9 / 16., 9 / 16.),
          new Vector3(0, 17 / 16., 9 / 16.), new Vector4(0, 1, 0, 1)),

      // Back face.
      new Quad(new Vector3(1, 9 / 16., 7 / 16.), new Vector3(0, 9 / 16., 7 / 16.),
          new Vector3(1, 17 / 16., 7 / 16.), new Vector4(28 / 64., 52 / 64., 18 / 32., 30 / 32.)),

      // Left face.
      new Quad(new Vector3(0, 9 / 16., 7 / 16.), new Vector3(0, 9 / 16., 9 / 16.),
          new Vector3(0, 17 / 16., 7 / 16.), new Vector4(0, 2 / 64., 18 / 32., 30 / 32.)),

      // Right face.
      new Quad(new Vector3(1, 9 / 16., 9 / 16.), new Vector3(1, 9 / 16., 7 / 16.),
          new Vector3(1, 17 / 16., 9 / 16.), new Vector4(26 / 64., 28 / 64., 18 / 32., 30 / 32.)),

      // Top face.
      new Quad(new Vector3(1, 17 / 16., 7 / 16.), new Vector3(0, 17 / 16., 7 / 16.),
          new Vector3(1, 17 / 16., 9 / 16.), new Vector4(2 / 64., 26 / 64., 1, 30 / 32.)),

      // Bottom face.
      new Quad(new Vector3(0, 9 / 16., 7 / 16.), new Vector3(1, 9 / 16., 7 / 16.),
          new Vector3(0, 9 / 16., 9 / 16.), new Vector4(26 / 64., 50 / 64., 1, 30 / 32.)),

      // Post front.
      new Quad(new Vector3(7 / 16., 0, 9 / 16.), new Vector3(9 / 16., 0, 9 / 16.),
          new Vector3(7 / 16., 9 / 16., 9 / 16.),
          new Vector4(2 / 64., 4 / 64., 2 / 32., 16 / 32.)),

      // Post back.
      new Quad(new Vector3(9 / 16., 0, 7 / 16.), new Vector3(7 / 16., 0, 7 / 16.),
          new Vector3(9 / 16., 9 / 16., 7 / 16.),
          new Vector4(4 / 64., 6 / 64., 2 / 32., 16 / 32.)),

      // Post left.
      new Quad(new Vector3(7 / 16., 0, 7 / 16.), new Vector3(7 / 16., 0, 9 / 16.),
          new Vector3(7 / 16., 9 / 16., 7 / 16.), new Vector4(0, 2 / 64., 2 / 32., 16 / 32.)),

      // Post right.
      new Quad(new Vector3(9 / 16., 0, 9 / 16.), new Vector3(9 / 16., 0, 7 / 16.),
          new Vector3(9 / 16., 9 / 16., 9 / 16.),
          new Vector4(6 / 64., 8 / 64., 2 / 32., 16 / 32.)),

      // Post bottom.
      new Quad(new Vector3(7 / 16., 0, 7 / 16.), new Vector3(9 / 16., 0, 7 / 16.),
          new Vector3(7 / 16., 0, 9 / 16.), new Vector4(4 / 64., 6 / 64., 16 / 32., 18 / 32.)),

  };

  private static final Quad[][] rot = new Quad[16][];

  static {
    // Rotate the sign post to face the correct direction.
    rot[0] = sides;
    for (int i = 1; i < 16; ++i) {
      rot[i] = Model.rotateY(sides, -i * Math.PI / 8);
    }
  }

  private final JsonArray[] text;
  private final int angle;
  private final SignTexture frontTexture;
  private final Texture texture;
  private final String material;

  public SignEntity(Vector3 position, CompoundTag entityTag, int blockData, String material) {
    this(position, getTextLines(entityTag), blockData & 0xF, material);
  }

  public SignEntity(Vector3 position, JsonArray[] text, int direction, String material) {
    super(position);
    Texture signTexture = SignEntity.textureFromMaterial(material);
    this.text = text;
    this.angle = direction;
    this.frontTexture = new SignTexture(text, signTexture);
    this.texture = signTexture;
    this.material = material;
  }

  /**
   * Extracts the text lines from a sign entity tag.
   *
   * @return array of text lines.
   */
  protected static JsonArray[] getTextLines(CompoundTag entityTag) {
    return new JsonArray[] {
        extractText(entityTag.get("Text1")), extractText(entityTag.get("Text2")),
        extractText(entityTag.get("Text3")), extractText(entityTag.get("Text4")),
    };
  }

  /**
   * Extract text from entity tag.
   */
  private static JsonArray extractText(Tag tag) {
    JsonArray array = new JsonArray();
    String data = tag.stringValue("");
    if (data.startsWith("{")) {
      JsonParser parser = new JsonParser(new ByteArrayInputStream(data.getBytes()));
      try {
        JsonValue value = parser.parse();
        if (value.isObject()) {
          JsonObject obj = value.object();
          addText(array, obj.get("text").stringValue(""));
          JsonArray extraArray = obj.get("extra").array();
          for (JsonValue extra : extraArray) {
            if (extra.isObject()) {
              JsonObject extraObject = extra.object();
              addText(array, extraObject.get("text").stringValue(""),
                  extraObject.get("color").stringValue(""));
            } else {
              addText(array, extra.stringValue(""));
            }
          }
        } else {
          for (JsonValue item : value.array()) {
            addText(array, item.stringValue(""));
          }
        }
      } catch (IOException | SyntaxError e) {
      }
    } else if (data.startsWith("\"")) {
      addText(array, data.substring(1, data.length() - 1));
    } else {
      addText(array, data);
    }
    return array;
  }

  /**
   * Add a text entry to a JSON text array.
   */
  private static void addText(JsonArray array, String text) {
    if (!text.isEmpty()) {
      JsonObject object = new JsonObject();
      object.add("text", text);
      array.add(object);
    }
  }

  /**
   * Add a text entry with color to a JSON text array.
   */
  private static void addText(JsonArray array, String text, String color) {
    if (!color.isEmpty() && !text.isEmpty()) {
      JsonObject object = new JsonObject();
      object.add("text", text);
      object.add("color", Color.get(color).id);
      array.add(object);
    } else {
      addText(array, text);
    }
  }

  @Override public Collection<Primitive> primitives(Vector3 offset) {
    Collection<Primitive> primitives = new LinkedList<>();
    Transform transform = Transform.NONE
        .translate(position.x + offset.x, position.y + offset.y, position.z + offset.z);
    for (int i = 0; i < sides.length; ++i) {
      Quad quad = rot[angle][i];
      Material material = new TextureMaterial(i == 0 ? frontTexture : texture);
      quad.addTriangles(primitives, material, transform);
    }
    return primitives;
  }

  @Override public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "sign");
    json.add("position", position.toJson());
    json.add("text", textToJson(text));
    json.add("direction", angle);
    json.add("material", material);
    return json;
  }

  /**
   * Unmarshalls a sign entity from JSON data.
   */
  public static Entity fromJson(JsonObject json) {
    Vector3 position = new Vector3();
    position.fromJson(json.get("position").object());
    JsonArray[] text = textFromJson(json.get("text"));
    int direction = json.get("direction").intValue(0);
    String material = json.get("material").stringValue("oak");
    return new SignEntity(position, text, direction, material);
  }

  /**
   * Marshalls sign text to JSON representation.
   */
  protected static JsonArray textToJson(JsonArray[] text) {
    JsonArray array = new JsonArray();
    array.add(text[0].copy());
    array.add(text[1].copy());
    array.add(text[2].copy());
    array.add(text[3].copy());
    return array;
  }

  /**
   * Unmarshalls sign text from JSON representation.
   */
  protected static JsonArray[] textFromJson(JsonValue json) {
    JsonArray array = json.array();
    JsonArray[] text = new JsonArray[4];
    text[0] = array.get(0).array();
    text[1] = array.get(1).array();
    text[2] = array.get(2).array();
    text[3] = array.get(3).array();
    return text;
  }

  public static Texture textureFromMaterial(String material) {
    switch (material) {
      case "oak":
        return Texture.oakSignPost;
      case "spruce":
        return Texture.spruceSignPost;
      case "birch":
        return Texture.birchSignPost;
      case "jungle":
        return Texture.jungleSignPost;
      case "acacia":
        return Texture.acaciaSignPost;
      case "dark_oak":
        return Texture.darkOakSignPost;
      // TODO [1.16] Add crimson and warped signs
      default:
        throw new IllegalArgumentException("Unknown sign material: " + material);
    }
  }
}
