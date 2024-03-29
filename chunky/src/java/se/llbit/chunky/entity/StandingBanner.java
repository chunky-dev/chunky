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
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.TextureCache;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.math.*;
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.SpecificTag;
import se.llbit.util.NbtUtil;

import java.util.Collection;
import java.util.LinkedList;

/**
 * A standing banner entity.
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

  public static JsonObject parseDesign(CompoundTag entityTag) {
    JsonObject design = new JsonObject();
    BannerDesign.Color base = BannerDesign.Color.get(entityTag.get("Base").intValue(BannerDesign.Color.WHITE.id));
    JsonArray patterns = new JsonArray();
    // tag names are Titlecase in 1.20 or earlier
    ListTag listTag = NbtUtil.getTagFromNames(entityTag, "Patterns", "patterns").asList();
    for (SpecificTag tag : listTag) {
      CompoundTag patternTag = tag.asCompound();
      int color = NbtUtil.getTagFromNames(patternTag, "Color").intValue();
      String colorName = patternTag.get("color").stringValue(null);
      if (colorName != null) {
        color = BannerDesign.Color.get(colorName).id;
      }
      String pattern = NbtUtil.getTagFromNames(patternTag, "Pattern", "pattern").stringValue();
      JsonObject patternJson = new JsonObject();
      patternJson.add("pattern", pattern);
      patternJson.add("color", color);
      patterns.add(patternJson);
    }
    design.add("base", base.id);
    design.add("patterns", patterns);
    return design;
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    Collection<Primitive> faces = new LinkedList<>();
    Transform transform = Transform.NONE
      .translate(position.x + offset.x, position.y + offset.y, position.z + offset.z);
    Material material = getBannerTexture(design);
    for (Quad quad : rot[rotation]) {
      quad.addTriangles(faces, material, transform);
    }
    return faces;
  }

  protected static BitmapImage getPatternBitmap(String patternName) {
    BannerDesign.Pattern pattern = BannerDesign.getPattern(patternName);
    if (pattern == null) {
      Log.warn("Unknown banner pattern: " + patternName);
      return Texture.bannerBase.getBitmap();
    }
    return pattern.getBitmap();
  }

  @Override
  public JsonValue toJson() {
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

  public static Material getBannerTexture(JsonObject design) {
    BannerDesign.Color base = BannerDesign.Color.get(design.get("base").asInt(BannerDesign.Color.WHITE.id));
    JsonArray patterns = design.get("patterns").array();
    if (base == BannerDesign.Color.WHITE && patterns.isEmpty()) {
      return new TextureMaterial(Texture.bannerBase);
    }
    Texture cachedTexture = TextureCache.get(design);
    if (cachedTexture != null) {
      return new TextureMaterial(cachedTexture);
    }
    BitmapImage plain = Texture.bannerBase.getBitmap();
    if (plain.width < 64 || plain.height < 64) {
      Log.info("Banner texture is too small to compose patterns.");
      return new TextureMaterial(Texture.unknown);
    }
    int scale = plain.width / 64;
    BitmapImage tinted = new BitmapImage(plain.width, plain.height);
    float[] color = base.rgbaColor;
    float[] col = new float[4];
    float[] com = new float[4];
    tinted.blit(plain, 0, 0);
    if (base != BannerDesign.Color.WHITE) {
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
        color = BannerDesign.Color.get(pattern.object().get("color").intValue(BannerDesign.Color.BLACK.id)).rgbaColor;
        for (int y = 0; y < 41 * scale; ++y) {
          for (int x = 0; x < 42 * scale; ++x) {
            int argb = bitmap.getPixel(x, y);
            ColorUtil.getRGBAComponents(argb, col);
            ColorUtil.getRGBAComponents(tinted.getPixel(x, y), com);
            float f = col[3];
            tinted.setPixel(x, y, ColorUtil.getArgb(
              color[0] * f + (1 - f) * com[0],
              color[1] * f + (1 - f) * com[1],
              color[2] * f + (1 - f) * com[2],
              1.f));
          }
        }
      }
    }
    Texture texture = new Texture(tinted);
    TextureCache.put(design, texture);
    return new TextureMaterial(texture);
  }
}
