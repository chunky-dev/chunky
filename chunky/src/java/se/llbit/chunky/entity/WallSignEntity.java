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
import java.util.LinkedList;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.resources.SignTexture;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.CompoundTag;

public class WallSignEntity extends Entity {

  // Distance the sign is offset from the wall (as in Minecraft).
  private static final double offset = 0.02;

  private static Quad[][] faces = {{}, {},

      // Facing north
      {
          // North (front) face.
          new Quad(new Vector3(1, .2, .875 + offset), new Vector3(0, .2, .875 + offset),
              new Vector3(1, .8, .875 + offset), new Vector4(0, 1, 0, 1)),

          // South (back) face.
          new Quad(new Vector3(0, .2, 1 - offset), new Vector3(1, .2, 1 - offset),
              new Vector3(0, .8, 1 - offset),
              new Vector4(28 / 64., 52 / 64., 18 / 32., 30 / 32.)),

          // West (left) face.
          new Quad(new Vector3(0, .2, .875 + offset), new Vector3(0, .2, 1 - offset),
              new Vector3(0, .8, .875 + offset),
              new Vector4(26 / 64., 28 / 64., 18 / 32., 30 / 32.)),

          // East (right) face.
          new Quad(new Vector3(1, .2, 1 - offset), new Vector3(1, .2, .875 + offset),
              new Vector3(1, .8, 1 - offset), new Vector4(0, 2 / 64., 18 / 32., 30 / 32.)),

          // Top face.
          new Quad(new Vector3(1, .8, .875 + offset), new Vector3(0, .8, .875 + offset),
              new Vector3(1, .8, 1 - offset), new Vector4(2 / 64., 26 / 64., 30 / 32., 1)),

          // Bottom face
          new Quad(new Vector3(0, .2, .875 + offset), new Vector3(1, .2, .875 + offset),
              new Vector3(0, .2, 1 - offset), new Vector4(50 / 64., 26 / 64., 30 / 32., 1)),},

      // Facing south.
      {},

      // Facing west.
      {},

      // Facing east.
      {},};

  static {
    faces[5] = Model.rotateY(faces[2]);
    faces[3] = Model.rotateY(faces[5]);
    faces[4] = Model.rotateY(faces[3]);
  }

  private final JsonArray[] text;
  private final int orientation;
  private final SignTexture frontTexture;
  private final Texture texture;
  private final String material;

  public WallSignEntity(Vector3 position, CompoundTag entityTag, int blockData, String material) {
    this(position, SignEntity.getTextLines(entityTag), blockData % 6, material);
  }

  public WallSignEntity(Vector3 position, JsonArray[] text, int direction, String material) {
    super(position);
    Texture signTexture = SignEntity.textureFromMaterial(material);
    this.orientation = direction;
    this.text = text;
    this.frontTexture = new SignTexture(text, signTexture);
    this.texture = signTexture;
    this.material = material;
  }

  @Override public Collection<Primitive> primitives(Vector3 offset) {
    Collection<Primitive> primitives = new LinkedList<>();
    Transform transform = Transform.NONE
        .translate(position.x + offset.x, position.y + offset.y, position.z + offset.z);
    Quad[] quads = faces[orientation];
    for (int i = 0; i < quads.length; ++i) {
      Quad quad = quads[i];
      Material material = new TextureMaterial(i == 0 ? frontTexture : texture);
      quad.addTriangles(primitives, material, transform);
    }
    return primitives;
  }

  @Override public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "wallsign");
    json.add("position", position.toJson());
    json.add("text", SignEntity.textToJson(text));
    json.add("direction", orientation);
    json.add("material", material);
    return json;
  }

  /**
   * Unmarshalls a wall sign entity from JSON data.
   */
  public static Entity fromJson(JsonObject json) {
    Vector3 position = new Vector3();
    position.fromJson(json.get("position").object());
    JsonArray[] text = SignEntity.textFromJson(json.get("text"));
    int direction = json.get("direction").intValue(0);
    String material = json.get("material").stringValue("oak");
    return new WallSignEntity(position, text, direction, material);
  }
}
