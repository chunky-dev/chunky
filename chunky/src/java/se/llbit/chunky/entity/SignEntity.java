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
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonParser.SyntaxError;
import se.llbit.json.JsonValue;
import se.llbit.math.*;
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
    // text colors
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
    WHITE(15, 0xFFFFFFFF),

    // dyed sign text colors
    DYE_WHITE(0xFF656565),
    DYE_ORANGE(0xFF65280C),
    DYE_MAGENTA(0xFF650065),
    DYE_LIGHT_BLUE(0xFF3C4B51),
    DYE_YELLOW(0xFF656500),
    DYE_LIME(0xFF4B6500),
    DYE_PINK(0xFF652947),
    DYE_GRAY(0xFF323232),
    DYE_LIGHT_GRAY(0xFF535353),
    DYE_CYAN(0xFF006565),
    DYE_PURPLE(0xFF3F0C5F),
    DYE_BLUE(0xFF000065),
    DYE_BROWN(0xFF361B07),
    DYE_GREEN(0xFF006500),
    DYE_RED(0xFF650000);

    public final int id;
    public final int rgbColor;
    public final float[] linearColor;

    private static final Map<String, Color> textColorMap = new HashMap<>();
    private static final Color[] textColorByIdMap = new Color[]{
      BLACK,
      DARK_BLUE,
      DARK_GREEN,
      DARK_AQUA,
      DARK_RED,
      DARK_PURPLE,
      GOLD,
      GRAY,
      DARK_GRAY,
      BLUE,
      GREEN,
      AQUA,
      RED,
      LIGHT_PURPLE,
      YELLOW,
      WHITE,
    };
    private static final Map<String, Color> dyedTextColorMap = new HashMap<>();

    static {
      textColorMap.put("dark_blue", DARK_BLUE);
      textColorMap.put("dark_green", DARK_GREEN);
      textColorMap.put("dark_aqua", DARK_AQUA);
      textColorMap.put("dark_red", DARK_RED);
      textColorMap.put("dark_purple", DARK_PURPLE);
      textColorMap.put("gold", GOLD);
      textColorMap.put("gray", GRAY);
      textColorMap.put("dark_gray", DARK_GRAY);
      textColorMap.put("blue", BLUE);
      textColorMap.put("green", GREEN);
      textColorMap.put("aqua", AQUA);
      textColorMap.put("red", RED);
      textColorMap.put("light_purple", LIGHT_PURPLE);
      textColorMap.put("yellow", YELLOW);
      textColorMap.put("white", WHITE);

      dyedTextColorMap.put("white", Color.DYE_WHITE);
      dyedTextColorMap.put("orange", Color.DYE_ORANGE);
      dyedTextColorMap.put("magenta", Color.DYE_MAGENTA);
      dyedTextColorMap.put("light_blue", Color.DYE_LIGHT_BLUE);
      dyedTextColorMap.put("yellow", Color.DYE_YELLOW);
      dyedTextColorMap.put("lime", Color.DYE_LIME);
      dyedTextColorMap.put("pink", Color.DYE_PINK);
      dyedTextColorMap.put("gray", Color.DYE_GRAY);
      dyedTextColorMap.put("light_gray", Color.DYE_LIGHT_GRAY);
      dyedTextColorMap.put("cyan", Color.DYE_CYAN);
      dyedTextColorMap.put("purple", Color.DYE_PURPLE);
      dyedTextColorMap.put("blue", Color.DYE_BLUE);
      dyedTextColorMap.put("brown", Color.DYE_BROWN);
      dyedTextColorMap.put("green", Color.DYE_GREEN);
      dyedTextColorMap.put("red", Color.DYE_RED);
    }

    Color(int color) {
      this(-1, color);
    }

    Color(int id, int color) {
      this.id = id;
      this.rgbColor = color;
      this.linearColor = new float[4];
      ColorUtil.getRGBAComponentsGammaCorrected(rgbColor, linearColor);
    }

    public static Color get(String color) {
      return textColorMap.getOrDefault(color, Color.BLACK);
    }

    public static Color get(int id) {
      return textColorByIdMap[id & 0xF];
    }

    public static Color getFromDyedSign(String color) {
      return dyedTextColorMap.getOrDefault(color, Color.BLACK);
    }
  }

  // Facing south.
  protected static Quad[] sides = {
    // Front face.
    new Quad(new Vector3(0, 9 / 16., 9 / 16.), new Vector3(1, 9 / 16., 9 / 16.),
      new Vector3(0, 17 / 16., 9 / 16.), new Vector4(2 / 64., 26 / 64., 18 / 32., 30 / 32.)),

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

  private static Quad[] frontFaceWithText = new Quad[16];
  private static Quad[] backFaceWithText = new Quad[16];

  private static final Quad[][] rot = new Quad[16][];

  static {
    // Rotate the sign post to face the correct direction.
    rot[0] = sides;
    for (int i = 1; i < 16; ++i) {
      rot[i] = Model.rotateY(sides, -i * Math.PI / 8);
    }

    frontFaceWithText[0] = new Quad(new Vector3(0, 9 / 16., 9 / 16.), new Vector3(1, 9 / 16., 9 / 16.),
      new Vector3(0, 17 / 16., 9 / 16.), new Vector4(0, 1, 0, 1));
    for (int i = 1; i < 16; ++i) {
      frontFaceWithText[i] = frontFaceWithText[0].transform(Transform.NONE.rotateY(-i * Math.PI / 8));
    }

    backFaceWithText[0] = new Quad(new Vector3(1, 9 / 16., 7 / 16.), new Vector3(0, 9 / 16., 7 / 16.),
      new Vector3(1, 17 / 16., 7 / 16.), new Vector4(0, 1, 0, 1));
    for (int i = 1; i < 16; ++i) {
      backFaceWithText[i] = backFaceWithText[0].transform(Transform.NONE.rotateY(-i * Math.PI / 8));
    }
  }

  private final JsonArray[] frontText;
  private final JsonArray[] backText;
  private final int angle;
  private final SignTexture frontTexture;
  private final SignTexture backTexture;
  private final Color frontDye;
  private final Color backDye;
  private final Texture texture;
  private final String material;

  public SignEntity(Vector3 position, CompoundTag entityTag, int blockData, String material) {
    this(position, getFrontTextLines(entityTag), getFrontDyeColor(entityTag), getBackTextLines(entityTag), getFrontDyeColor(entityTag), blockData & 0xF, material);
  }

  public SignEntity(Vector3 position, JsonArray[] frontText, Color frontDye, JsonArray[] backText, Color backDye, int direction, String material) {
    super(position);
    Texture signTexture = SignEntity.textureFromMaterial(material);
    this.frontText = frontText;
    this.backText = backText;
    this.frontDye = frontDye;
    this.backDye = backDye;
    this.angle = direction;
    this.frontTexture = frontText != null ? new SignTexture(frontText, frontDye, signTexture, 24, 12, 2 / 64., 18 / 32., 26 / 64., 30 / 32., 4, 1, 10) : null;
    this.backTexture = backText != null ? new SignTexture(backText, backDye, signTexture, 24, 12, 28 / 64., 18 / 32., 52 / 64., 30 / 32., 4, 1, 10) : null;
    this.texture = signTexture;
    this.material = material;
  }

  /**
   * Extracts the front text lines from a sign entity tag.
   *
   * @return array of text lines.
   */
  protected static JsonArray[] getFrontTextLines(CompoundTag entityTag) {
    if (!entityTag.get("front_text").isError()) {
      // 1.20+ sign
      Tag lines = entityTag.get("front_text").get("messages");
      JsonArray[] extractedText = new JsonArray[]{
        extractText(lines.get(0)), extractText(lines.get(1)),
        extractText(lines.get(2)), extractText(lines.get(3)),
      };
      if (isEmpty(extractedText)) {
        return null;
      }
      return extractedText;
    } else {
      // < 1.20 sign
      JsonArray[] extractedText = new JsonArray[]{
        extractText(entityTag.get("Text1")), extractText(entityTag.get("Text2")),
        extractText(entityTag.get("Text3")), extractText(entityTag.get("Text4")),
      };
      if (isEmpty(extractedText)) {
        return null;
      }
      return extractedText;
    }
  }

  /**
   * Extracts the front dye color from a sign entity tag.
   */
  protected static Color getFrontDyeColor(CompoundTag entityTag) {
    if (!entityTag.get("front_text").isError()) {
      return Color.getFromDyedSign(entityTag.get("front_text").get("color").stringValue("black"));
    } else {
      // < 1.20 sign
      return Color.getFromDyedSign(entityTag.get("Color").stringValue("black"));
    }
  }

  /**
   * Extracts the front text lines from a sign entity tag.
   *
   * @return array of text lines.
   */
  protected static JsonArray[] getBackTextLines(CompoundTag entityTag) {
    if (entityTag.get("back_text").isError()) {
      // < 1.20 sign
      return null;
    }

    // 1.20+ sign
    Tag lines = entityTag.get("back_text").get("messages");
    JsonArray[] extractedText = new JsonArray[]{
      extractText(lines.get(0)), extractText(lines.get(1)),
      extractText(lines.get(2)), extractText(lines.get(3)),
    };
    if (isEmpty(extractedText)) {
      return null;
    }
    return extractedText;
  }

  /**
   * Extracts the back dye color from a sign entity tag.
   */
  protected static Color getBackDyeColor(CompoundTag entityTag) {
    if (!entityTag.get("front_text").isError()) {
      return Color.getFromDyedSign(entityTag.get("front_text").get("color").stringValue("black"));
    } else {
      // < 1.20 sign
      return Color.BLACK;
    }
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
   * Check if the given lines are empty or only whitespace.
   *
   * @param lines Lines extracted by {{@link #extractText(Tag)}}
   * @return True if the lines are empty or only whitespace, false otherwise
   */
  private static boolean isEmpty(JsonArray[] lines) {
    for (JsonArray line : lines) {
      for (JsonValue text : line) {
        if (!text.object().get("text").stringValue("").trim().isEmpty()) {
          return false;
        }
      }
    }
    return true;
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

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    Collection<Primitive> primitives = new LinkedList<>();
    Transform transform = Transform.NONE
      .translate(position.x + offset.x, position.y + offset.y, position.z + offset.z);
    for (int i = 0; i < sides.length; ++i) {
      Quad quad = rot[angle][i];
      Texture tex = texture;
      if (i == 0 && frontTexture != null) {
        tex = frontTexture;
        quad = frontFaceWithText[angle];
      } else if (i == 1 && backTexture != null) {
        tex = backTexture;
        quad = backFaceWithText[angle];
      }
      quad.addTriangles(primitives, new TextureMaterial(tex), transform);
    }
    return primitives;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "sign");
    json.add("position", position.toJson());
    if (frontText != null) {
      json.add("text", textToJson(frontText));
      json.add("dye", frontDye.name().replace("DYE_", "").toLowerCase());
    }
    if (backText != null) {
      json.add("backText", textToJson(backText));
      json.add("backDye", backDye.name().replace("DYE_", "").toLowerCase());
    }
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
    JsonArray[] frontText = null;
    if (json.get("text").isArray()) {
      frontText = textFromJson(json.get("text"));
    }
    JsonArray[] backText = null;
    if (json.get("backText").isArray()) {
      backText = textFromJson(json.get("backText"));
    }
    int direction = json.get("direction").intValue(0);
    String material = json.get("material").stringValue("oak");
    Color dye = Color.getFromDyedSign(json.get("dye").stringValue("black"));
    Color backDye = Color.getFromDyedSign(json.get("backDye").stringValue("black"));
    return new SignEntity(position, frontText, dye, backText, backDye, direction, material);
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
      case "crimson":
        return Texture.crimsonSignPost;
      case "warped":
        return Texture.warpedSignPost;
      case "mangrove":
        return Texture.mangroveSignPost;
      case "bamboo":
        return Texture.bambooSignPost;
      case "cherry":
        return Texture.cherrySignPost;
      default:
        throw new IllegalArgumentException("Unknown sign material: " + material);
    }
  }
}
