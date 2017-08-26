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
import se.llbit.chunky.world.Material;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.CompoundTag;

import java.util.Collection;
import java.util.LinkedList;

/**
 * A mob head (skull) entity.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class WallBanner extends Entity {

  private static final Quad[] quads = {
      // Banner:
      new Quad(
          new Vector3(1.5 / 16.0, 13 / 16.0, 1.5 / 16.0),
          new Vector3(14.5 / 16.0, 13 / 16.0, 1.5 / 16.0),
          new Vector3(1.5 / 16.0, 13 / 16.0, 1 / 16.0),
          new Vector4(1 / 64.0, 21 / 64.0, 63 / 64.0, 64 / 64.0)),
      new Quad(
          new Vector3(1.5 / 16.0, -13 / 16.0, 1 / 16.0),
          new Vector3(14.5 / 16.0, -13 / 16.0, 1 / 16.0),
          new Vector3(1.5 / 16.0, -13 / 16.0, 1.5 / 16.0),
          new Vector4(21 / 64.0, 41 / 64.0, 63 / 64.0, 64 / 64.0)),
      new Quad(
          new Vector3(14.5 / 16.0, -13 / 16.0, 1.5 / 16.0),
          new Vector3(14.5 / 16.0, -13 / 16.0, 1 / 16.0),
          new Vector3(14.5 / 16.0, 13 / 16.0, 1.5 / 16.0),
          new Vector4(21 / 64.0, 22 / 64.0, 23 / 64.0, 63 / 64.0)),
      new Quad(
          new Vector3(1.5 / 16.0, -13 / 16.0, 1 / 16.0),
          new Vector3(1.5 / 16.0, -13 / 16.0, 1.5 / 16.0),
          new Vector3(1.5 / 16.0, 13 / 16.0, 1 / 16.0),
          new Vector4(0, 1 / 64.0, 23 / 64.0, 63 / 64.0)),
      new Quad(
          new Vector3(14.5 / 16.0, -13 / 16.0, 1 / 16.0),
          new Vector3(1.5 / 16.0, -13 / 16.0, 1 / 16.0),
          new Vector3(14.5 / 16.0, 13 / 16.0, 1 / 16.0),
          new Vector4(22 / 64.0, 42 / 64.0, 23 / 64.0, 63 / 64.0)),
      new Quad(
          new Vector3(1.5 / 16.0, -13 / 16.0, 1.5 / 16.0),
          new Vector3(14.5 / 16.0, -13 / 16.0, 1.5 / 16.0),
          new Vector3(1.5 / 16.0, 13 / 16.0, 1.5 / 16.0),
          new Vector4(1 / 64.0, 21 / 64.0, 23 / 64.0, 63 / 64.0)),
      // Crossbar:
      new Quad(
          new Vector3(1.5 / 16.0, 13 / 16.0, 1 / 16.0),
          new Vector3(14.5 / 16.0, 13 / 16.0, 1 / 16.0),
          new Vector3(1.5 / 16.0, 13 / 16.0, 0),
          new Vector4(2 / 64.0, 21 / 64.0, 20 / 64.0, 22 / 64.0)),
      new Quad(
          new Vector3(1.5 / 16.0, 12 / 16.0, 0),
          new Vector3(14.5 / 16.0, 12 / 16.0, 0),
          new Vector3(1.5 / 16.0, 12 / 16.0, 1 / 16.0),
          new Vector4(22 / 64.0, 42 / 64.0, 20 / 64.0, 22 / 64.0)),
      new Quad(
          new Vector3(14.5 / 16.0, 12 / 16.0, 1 / 16.0),
          new Vector3(14.5 / 16.0, 12 / 16.0, 0),
          new Vector3(14.5 / 16.0, 13 / 16.0, 1 / 16.0),
          new Vector4(22 / 64.0, 24 / 64.0, 18 / 64.0, 20 / 64.0)),
      new Quad(
          new Vector3(1.5 / 16.0, 12 / 16.0, 0),
          new Vector3(1.5 / 16.0, 12 / 16.0, 1 / 16.0),
          new Vector3(1.5 / 16.0, 13 / 16.0, 0),
          new Vector4(0, 2 / 64.0, 18 / 64.0, 20 / 64.0)),
      new Quad(
          new Vector3(14.5 / 16.0, 12 / 16.0, 0),
          new Vector3(1.5 / 16.0, 12 / 16.0, 0),
          new Vector3(14.5 / 16.0, 13 / 16.0, 0),
          new Vector4(2 / 64.0, 21 / 64.0, 18 / 64.0, 20 / 64.0)),
  };

  private static final Quad[][] rot = new Quad[16][];

  static {
    rot[3] = quads;
    rot[4] = Model.rotateY(rot[3]);
    rot[2] = Model.rotateY(rot[4]);
    rot[5] = Model.rotateY(rot[2]);
  }

  /**
   * The rotation of the skull when attached to a wall.
   */
  private final int rotation;
  private final JsonObject design;

  public WallBanner(Vector3 position, int rotation, JsonObject design) {
    super(position);
    this.rotation = rotation;
    this.design = design;
  }

  public WallBanner(Vector3 position, int rotation, CompoundTag entityTag) {
    this(position, rotation, StandingBanner.parseDesign(entityTag));
  }

  @Override public Collection<Primitive> primitives(Vector3 offset) {
    Collection<Primitive> faces = new LinkedList<>();
    Transform transform = Transform.NONE
        .translate(position.x + offset.x, position.y + offset.y, position.z + offset.z);
    Material material = StandingBanner.getBannerTexture(design);
    for (Quad quad : rot[rotation]) {
      quad.addTriangles(faces, material, transform);
    }
    return faces;
  }

  @Override public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "wall_banner");
    json.add("position", position.toJson());
    json.add("rotation", rotation);
    json.add("design", design);
    return json;
  }

  public static Entity fromJson(JsonObject json) {
    Vector3 position = new Vector3();
    position.fromJson(json.get("position").object());
    int rotation = json.get("rotation").intValue(0);
    return new WallBanner(position, rotation, json.get("design").object());
  }
}
