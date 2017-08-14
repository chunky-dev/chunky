/* Copyright (c) 2017 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.model.Model;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.resources.MinecraftFinder;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.TextureCache;
import se.llbit.chunky.resources.TexturePackLoader;
import se.llbit.chunky.resources.texturepack.SimpleTexture;
import se.llbit.chunky.resources.texturepack.TextureLoader;
import se.llbit.chunky.world.BlockData;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.math.ColorUtil;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.SpecificTag;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * A mob head (skull) entity.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class StandingBanner extends Entity {

  private static final Quad[] quads = {
      // Pole:
      new Quad(
          new Vector3(7.5 / 16.0, 0, 7.5 / 16.0),
          new Vector3(8.5 / 16.0, 0, 7.5 / 16.0),
          new Vector3(7.5 / 16.0, 0, 8.5 / 16.0),
          new Vector4(48 / 64.0, 50 / 64.0, 62 / 64.0, 64 / 64.0)),
      new Quad(
          new Vector3(8.5 / 16.0, 0, 8.5 / 16.0),
          new Vector3(8.5 / 16.0, 0, 7.5 / 16.0),
          new Vector3(8.5 / 16.0, 28 / 16.0, 8.5 / 16.0),
          new Vector4(46 / 64.0, 48 / 64.0, 20 / 64.0, 62 / 64.0)),
      new Quad(
          new Vector3(7.5 / 16.0, 0, 7.5 / 16.0),
          new Vector3(7.5 / 16.0, 0, 8.5 / 16.0),
          new Vector3(7.5 / 16.0, 28 / 16.0, 7.5 / 16.0),
          new Vector4(50 / 64.0, 52 / 64.0, 20 / 64.0, 62 / 64.0)),
      new Quad(
          new Vector3(8.5 / 16.0, 0, 7.5 / 16.0),
          new Vector3(7.5 / 16.0, 0, 7.5 / 16.0),
          new Vector3(8.5 / 16.0, 28 / 16.0, 7.5 / 16.0),
          new Vector4(44 / 64.0, 46 / 64.0, 20 / 64.0, 62 / 64.0)),
      new Quad(
          new Vector3(7.5 / 16.0, 0, 8.5 / 16.0),
          new Vector3(8.5 / 16.0, 0, 8.5 / 16.0),
          new Vector3(7.5 / 16.0, 28 / 16.0, 8.5 / 16.0),
          new Vector4(48 / 64.0, 50 / 64.0, 20 / 64.0, 62 / 64.0)),
      // Banner:
      new Quad(
          new Vector3(1.5 / 16.0, 29 / 16.0, 9 / 16.0),
          new Vector3(14.5 / 16.0, 29 / 16.0, 9 / 16.0),
          new Vector3(1.5 / 16.0, 29 / 16.0, 8.5 / 16.0),
          new Vector4(1 / 64.0, 21 / 64.0, 63 / 64.0, 64 / 64.0)),
      new Quad(
          new Vector3(1.5 / 16.0, 3 / 16.0, 8.5 / 16.0),
          new Vector3(14.5 / 16.0, 3 / 16.0, 8.5 / 16.0),
          new Vector3(1.5 / 16.0, 3 / 16.0, 9 / 16.0),
          new Vector4(21 / 64.0, 41 / 64.0, 63 / 64.0, 64 / 64.0)),
      new Quad(
          new Vector3(14.5 / 16.0, 3 / 16.0, 9 / 16.0),
          new Vector3(14.5 / 16.0, 3 / 16.0, 8.5 / 16.0),
          new Vector3(14.5 / 16.0, 29 / 16.0, 9 / 16.0),
          new Vector4(21 / 64.0, 22 / 64.0, 23 / 64.0, 63 / 64.0)),
      new Quad(
          new Vector3(1.5 / 16.0, 3 / 16.0, 8.5 / 16.0),
          new Vector3(1.5 / 16.0, 3 / 16.0, 9 / 16.0),
          new Vector3(1.5 / 16.0, 29 / 16.0, 8.5 / 16.0),
          new Vector4(0, 1 / 64.0, 23 / 64.0, 63 / 64.0)),
      new Quad(
          new Vector3(14.5 / 16.0, 3 / 16.0, 8.5 / 16.0),
          new Vector3(1.5 / 16.0, 3 / 16.0, 8.5 / 16.0),
          new Vector3(14.5 / 16.0, 29 / 16.0, 8.5 / 16.0),
          new Vector4(22 / 64.0, 42 / 64.0, 23 / 64.0, 63 / 64.0)),
      new Quad(
          new Vector3(1.5 / 16.0, 3 / 16.0, 9 / 16.0),
          new Vector3(14.5 / 16.0, 3 / 16.0, 9 / 16.0),
          new Vector3(1.5 / 16.0, 29 / 16.0, 9 / 16.0),
          new Vector4(1 / 64.0, 21 / 64.0, 23 / 64.0, 63 / 64.0)),
      // Crossbar:
      new Quad(
          new Vector3(1.5 / 16.0, 29 / 16.0, 8.5 / 16.0),
          new Vector3(14.5 / 16.0, 29 / 16.0, 8.5 / 16.0),
          new Vector3(1.5 / 16.0, 29 / 16.0, 7.5 / 16.0),
          new Vector4(2 / 64.0, 21 / 64.0, 20 / 64.0, 22 / 64.0)),
      new Quad(
          new Vector3(1.5 / 16.0, 28 / 16.0, 7.5 / 16.0),
          new Vector3(14.5 / 16.0, 28 / 16.0, 7.5 / 16.0),
          new Vector3(1.5 / 16.0, 28 / 16.0, 8.5 / 16.0),
          new Vector4(22 / 64.0, 42 / 64.0, 20 / 64.0, 22 / 64.0)),
      new Quad(
          new Vector3(14.5 / 16.0, 28 / 16.0, 8.5 / 16.0),
          new Vector3(14.5 / 16.0, 28 / 16.0, 7.5 / 16.0),
          new Vector3(14.5 / 16.0, 29 / 16.0, 8.5 / 16.0),
          new Vector4(22 / 64.0, 24 / 64.0, 18 / 64.0, 20 / 64.0)),
      new Quad(
          new Vector3(1.5 / 16.0, 28 / 16.0, 7.5 / 16.0),
          new Vector3(1.5 / 16.0, 28 / 16.0, 8.5 / 16.0),
          new Vector3(1.5 / 16.0, 29 / 16.0, 7.5 / 16.0),
          new Vector4(0, 2 / 64.0, 18 / 64.0, 20 / 64.0)),
      new Quad(
          new Vector3(14.5 / 16.0, 28 / 16.0, 7.5 / 16.0),
          new Vector3(1.5 / 16.0, 28 / 16.0, 7.5 / 16.0),
          new Vector3(14.5 / 16.0, 29 / 16.0, 7.5 / 16.0),
          new Vector4(2 / 64.0, 21 / 64.0, 18 / 64.0, 20 / 64.0)),
  };

  private static final Quad[][] rot = new Quad[16][];

  static {
    // Rotate to the correct direction.
    rot[0] = quads;
    for (int i = 1; i < 16; ++i) {
      rot[i] = Model.rotateY(quads, -i * Math.PI / 8);
    }
  }

  /**
   * The rotation of the skull when attached to a wall.
   */
  private final int rotation;
  private final JsonObject design;

  public StandingBanner(Vector3 position, int rotation, JsonObject design) {
    super(position);
    this.rotation = rotation;
    this.design = design;
  }

  public StandingBanner(Vector3 position, int rotation, CompoundTag entityTag) {
    this(position, rotation, parseDesign(entityTag));
  }

  protected static JsonObject parseDesign(CompoundTag entityTag) {
    JsonObject design = new JsonObject();
    int base = entityTag.get("Base").intValue(BlockData.BANNER_WHITE);
    JsonArray patterns = new JsonArray();
    ListTag listTag = entityTag.get("Patterns").asList();
    for (SpecificTag tag : listTag) {
      CompoundTag patternTag = tag.asCompound();
      int color = patternTag.get("Color").intValue();
      String pattern = patternTag.get("Pattern").stringValue();
      JsonObject patternJson = new JsonObject();
      patternJson.add("pattern", pattern);
      patternJson.add("color", color);
      patterns.add(patternJson);
    }
    design.add("base", base);
    design.add("patterns", patterns);
    return design;
  }

  @Override public Collection<Primitive> primitives(Vector3 offset) {
    Collection<Primitive> faces = new LinkedList<>();
    Transform transform = Transform.NONE
        .translate(position.x + offset.x, position.y + offset.y, position.z + offset.z);
    Material material = getBannerTexture(design);
    for (Quad quad : rot[rotation]) {
      quad.addTriangles(faces, material, transform);
    }
    return faces;
  }

  protected static BitmapImage getPatternBitmap(String pattern) {
    String filename = "";
    switch (pattern) {
      case "bs":
        filename = "stripe_bottom";
        break;
      case "ts":
        filename = "stripe_top";
        break;
      case "ls":
        filename = "stripe_left";
        break;
      case "rs":
        filename = "stripe_right";
        break;
      case "cs":
        filename = "stripe_center";
        break;
      case "ms":
        filename = "stripe_middle";
        break;
      case "drs":
        filename = "stripe_downright";
        break;
      case "dls":
        filename = "stripe_downleft";
        break;
      case "ss":
        filename = "small_stripes";
        break;
      case "cr":
        filename = "cross";
        break;
      case "sc":
        filename = "straight_cross";
        break;
      case "ld":
        filename = "diagonal_left";
        break;
      case "rud":
        filename = "diagonal_up_right";
        break;
      case "lud":
        filename = "diagonal_up_left";
        break;
      case "rd":
        filename = "diagonal_right";
        break;
      case "vh":
        filename = "half_vertical";
        break;
      case "vhr":
        filename = "half_vertical_right";
        break;
      case "hh":
        filename = "half_horizontal";
        break;
      case "hhb":
        filename = "half_horizontal_bottom";
        break;
      case "bl":
        filename = "square_bottom_left";
        break;
      case "br":
        filename = "square_bottom_right";
        break;
      case "tl":
        filename = "square_top_left";
        break;
      case "tr":
        filename = "square_top_right";
        break;
      case "bt":
        filename = "triangle_bottom";
        break;
      case "tt":
        filename = "triangle_top";
        break;
      case "bts":
        filename = "triangles_bottom";
        break;
      case "tts":
        filename = "triangles_top";
        break;
      case "mc":
        filename = "circle";
        break;
      case "mr":
        filename = "rhombus";
        break;
      case "bo":
        filename = "border";
        break;
      case "cbo":
        filename = "curly_border";
        break;
      case "bri":
        filename = "bricks";
        break;
      case "gra":
        filename = "gradient";
        break;
      case "gru":
        filename = "gradient_up";
        break;
      case "cre":
        filename = "creeper";
        break;
      case "sku":
        filename = "skull";
        break;
      case "flo":
        filename = "flower";
        break;
      case "moj":
        filename = "mojang";
        break;
    }
    if (filename.isEmpty()) {
      return Texture.bannerBase.getBitmap();
    } else {
      String texId = "assets/minecraft/textures/entity/banner/" + filename;
      Texture texture = TextureCache.get(texId);
      if (texture == null) {
        texture = new Texture();
        TextureCache.put(texId, texture);
        Map<String, TextureLoader> map =
            Collections.singletonMap(filename, new SimpleTexture(texId, texture));
        Collection<Map.Entry<String, TextureLoader>> missing =
            TexturePackLoader.loadTextures(map.entrySet());
        if (!missing.isEmpty()) {
          Log.info("Failed to load banner pattern: " + filename);
          texture = Texture.bannerBase;
        }
      }
      return texture.getBitmap();
    }
  }

  @Override public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "standing_banner");
    json.add("position", position.toJson());
    json.add("rotation", rotation);
    json.add("design", design);
    return json;
  }

  public static Entity fromJson(JsonObject json) {
    Vector3 position = new Vector3();
    position.fromJson(json.get("position").object());
    int rotation = json.get("rotation").intValue(0);
    return new StandingBanner(position, rotation, json.get("design").object());
  }


  public static float[] getColor(int colorCode) {
    int color = 0;
    switch (colorCode) {
      case BlockData.BANNER_BLACK:
        color = 0x000000;
        break;
      case BlockData.BANNER_BLUE:
        color = 0x454FC4;
        break;
      case BlockData.BANNER_BROWN:
        color = 0x96613A;
        break;
      case BlockData.BANNER_CYAN:
        color = 0x1CC6C6;
        break;
      case BlockData.BANNER_GRAY:
        color = 0x6E7B80;
        break;
      case BlockData.BANNER_GREEN:
        color = 0x81AA1E;
        break;
      case BlockData.BANNER_LIGHT_BLUE:
        color = 0x39AFD5;
        break;
      case BlockData.BANNER_LIME:
        color = 0x89D520;
        break;
      case BlockData.BANNER_MAGENTA:
        color = 0xCF51C5;
        break;
      case BlockData.BANNER_ORANGE:
        color = 0xD76F19;
        break;
      case BlockData.BANNER_PINK:
        color = 0xCF7691;
        break;
      case BlockData.BANNER_PURPLE:
        color = 0x9536C9;
        break;
      case BlockData.BANNER_RED:
        color = 0xCC352C;
        break;
      case BlockData.BANNER_SILVER:
        color = 0xCCCCCC;
        break;
      case BlockData.BANNER_WHITE:
        color = 0xFFFFFF;
        break;
      case BlockData.BANNER_YELLOW:
        color = 0xE6C438;
        break;
    }
    float[] components = new float[4];
    ColorUtil.getRGBAComponents(color, components);
    return components;
  }

  public static Material getBannerTexture(JsonObject design) {
    int base = design.get("base").asInt(BlockData.BANNER_WHITE);
    JsonArray patterns = design.get("patterns").array();
    if (base == BlockData.BANNER_WHITE && patterns.isEmpty()) {
      return new TextureMaterial(Texture.bannerBase);
    }
    Texture texture = TextureCache.get(design);
    if (texture != null) {
      return new TextureMaterial(texture);
    }
    BitmapImage plain = Texture.bannerBase.getBitmap();
    if (plain.width < 64 || plain.height < 64) {
      Log.info("Banner texture is too small to compose patterns.");
      return new TextureMaterial(texture);
    }
    int scale = plain.width / 64;
    BitmapImage tinted = new BitmapImage(plain.width, plain.height);
    float[] color = getColor(base);
    float[] col = new float[4];
    float[] com = new float[4];
    tinted.blit(plain, 0, 0);
    if (base != BlockData.BANNER_WHITE) {
      for (int y = 0; y < 41 * scale; ++y) {
        for (int x = 0; x < 42 * scale; ++x) {
          int argb = plain.getPixel(x, y);
          ColorUtil.getRGBAComponents(argb, col);
          tinted.setPixel(x, y, ColorUtil.getArgb(
              color[0] * col[0],
              color[1] * col[1],
              color[2] * col[2],
              1.f));
        }
      }
    }
    for (JsonValue pattern : patterns) {
      BitmapImage bitmap = getPatternBitmap(pattern.object().get("pattern").asString(""));
      if (bitmap.width != plain.width || bitmap.height != plain.height) {
        Log.info("Banner pattern does not match base texture size.");
      } else {
        color = getColor(pattern.object().get("color").intValue(BlockData.BANNER_BLACK));
        for (int y = 0; y < 41 * scale; ++y) {
          for (int x = 0; x < 42 * scale; ++x) {
            int argb = bitmap.getPixel(x, y);
            ColorUtil.getRGBAComponents(argb, col);
            ColorUtil.getRGBAComponents(tinted.getPixel(x, y), com);
            float f = col[0];
            tinted.setPixel(x, y, ColorUtil.getArgb(
                color[0] * f + (1 - f) * com[0],
                color[1] * f + (1 - f) * com[1],
                color[2] * f + (1 - f) * com[2],
                1.f));
          }
        }
      }
    }
    texture = new Texture(tinted);
    TextureCache.put(design, texture);
    return new TextureMaterial(texture);
  }
}
