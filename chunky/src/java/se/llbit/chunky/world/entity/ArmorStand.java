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
package se.llbit.chunky.world.entity;

import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.Tag;

import java.util.Collection;
import java.util.LinkedList;

public class ArmorStand extends Entity {

  private static final Quad[] base = {
      // cube1
      new Quad(
          new Vector3(2 / 16.0, 1 / 16.0, 14 / 16.0),
          new Vector3(14 / 16.0, 1 / 16.0, 14 / 16.0),
          new Vector3(2 / 16.0, 1 / 16.0, 2 / 16.0),
          new Vector4(12 / 64.0, 24 / 64.0, 20 / 64.0, 32 / 64.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 2 / 16.0),
          new Vector3(14 / 16.0, 0, 2 / 16.0),
          new Vector3(2 / 16.0, 0, 14 / 16.0),
          new Vector4(36 / 64.0, 24 / 64.0, 32 / 64.0, 20 / 64.0)),
      new Quad(
          new Vector3(14 / 16.0, 0, 14 / 16.0),
          new Vector3(14 / 16.0, 0, 2 / 16.0),
          new Vector3(14 / 16.0, 1 / 16.0, 14 / 16.0),
          new Vector4(24 / 64.0, 36 / 64.0, 19 / 64.0, 20 / 64.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 2 / 16.0),
          new Vector3(2 / 16.0, 0, 14 / 16.0),
          new Vector3(2 / 16.0, 1 / 16.0, 2 / 16.0),
          new Vector4(0, 12 / 64.0, 19 / 64.0, 20 / 64.0)),
      new Quad(
          new Vector3(14 / 16.0, 0, 2 / 16.0),
          new Vector3(2 / 16.0, 0, 2 / 16.0),
          new Vector3(14 / 16.0, 1 / 16.0, 2 / 16.0),
          new Vector4(36 / 64.0, 48 / 64.0, 19 / 64.0, 20 / 64.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 14 / 16.0),
          new Vector3(14 / 16.0, 0, 14 / 16.0),
          new Vector3(2 / 16.0, 1 / 16.0, 14 / 16.0),
          new Vector4(12 / 64.0, 24 / 64.0, 19 / 64.0, 20 / 64.0)),
  };

  private static final Quad[] quads = {
      // cube2
      new Quad(
          new Vector3(2 / 16.0, 24 / 16.0, 9.5 / 16.0),
          new Vector3(14 / 16.0, 24 / 16.0, 9.5 / 16.0),
          new Vector3(2 / 16.0, 24 / 16.0, 6.5 / 16.0),
          new Vector4(3 / 64.0, 15 / 64.0, 17.5 / 32.0, 19 / 32.0)),
      new Quad(
          new Vector3(2 / 16.0, 21 / 16.0, 6.5 / 16.0),
          new Vector3(14 / 16.0, 21 / 16.0, 6.5 / 16.0),
          new Vector3(2 / 16.0, 21 / 16.0, 9.5 / 16.0),
          new Vector4(15 / 64.0, 27 / 64.0, 19 / 32.0, 17.5 / 32.0)),
      new Quad(
          new Vector3(14 / 16.0, 21 / 16.0, 9.5 / 16.0),
          new Vector3(14 / 16.0, 21 / 16.0, 6.5 / 16.0),
          new Vector3(14 / 16.0, 24 / 16.0, 9.5 / 16.0),
          new Vector4(15 / 64.0, 18 / 64.0, 16 / 32.0, 17.5 / 32.0)),
      new Quad(
          new Vector3(2 / 16.0, 21 / 16.0, 6.5 / 16.0),
          new Vector3(2 / 16.0, 21 / 16.0, 9.5 / 16.0),
          new Vector3(2 / 16.0, 24 / 16.0, 6.5 / 16.0),
          new Vector4(0, 3 / 64.0, 16 / 32.0, 17.5 / 32.0)),
      new Quad(
          new Vector3(14 / 16.0, 21 / 16.0, 6.5 / 16.0),
          new Vector3(2 / 16.0, 21 / 16.0, 6.5 / 16.0),
          new Vector3(14 / 16.0, 24 / 16.0, 6.5 / 16.0),
          new Vector4(18 / 64.0, 30 / 64.0, 16 / 32.0, 17.5 / 32.0)),
      new Quad(
          new Vector3(2 / 16.0, 21 / 16.0, 9.5 / 16.0),
          new Vector3(14 / 16.0, 21 / 16.0, 9.5 / 16.0),
          new Vector3(2 / 16.0, 24 / 16.0, 9.5 / 16.0),
          new Vector4(3 / 64.0, 15 / 64.0, 16 / 32.0, 17.5 / 32.0)),
      // cube3
      new Quad(
          new Vector3(5 / 16.0, 21 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 21 / 16.0, 9 / 16.0),
          new Vector3(5 / 16.0, 21 / 16.0, 7 / 16.0),
          new Vector4(18 / 64.0, 20 / 64.0, 31 / 32.0, 32 / 32.0)),
      new Quad(
          new Vector3(5 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(5 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector4(20 / 64.0, 22 / 64.0, 31 / 32.0, 32 / 32.0)),
      new Quad(
          new Vector3(7 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 21 / 16.0, 9 / 16.0),
          new Vector4(20 / 64.0, 22 / 64.0, 27.5 / 32.0, 31 / 32.0)),
      new Quad(
          new Vector3(5 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(5 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(5 / 16.0, 21 / 16.0, 7 / 16.0),
          new Vector4(16 / 64.0, 18 / 64.0, 27.5 / 32.0, 31 / 32.0)),
      new Quad(
          new Vector3(7 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(5 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 21 / 16.0, 7 / 16.0),
          new Vector4(22 / 64.0, 24 / 64.0, 27.5 / 32.0, 31 / 32.0)),
      new Quad(
          new Vector3(5 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(5 / 16.0, 21 / 16.0, 9 / 16.0),
          new Vector4(18 / 64.0, 20 / 64.0, 27.5 / 32.0, 31 / 32.0)),
      // cube4
      new Quad(
          new Vector3(7 / 16.0, 30 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 30 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 30 / 16.0, 7 / 16.0),
          new Vector4(2 / 64.0, 4 / 64.0, 31 / 32.0, 32 / 32.0)),
      new Quad(
          new Vector3(7 / 16.0, 24 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 24 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 24 / 16.0, 9 / 16.0),
          new Vector4(4 / 64.0, 6 / 64.0, 31 / 32.0, 32 / 32.0)),
      new Quad(
          new Vector3(9 / 16.0, 24 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 24 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 30 / 16.0, 9 / 16.0),
          new Vector4(0, 2 / 64.0, 27.5 / 32.0, 31 / 32.0)),
      new Quad(
          new Vector3(7 / 16.0, 24 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 24 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 30 / 16.0, 7 / 16.0),
          new Vector4(4 / 64.0, 6 / 64.0, 27.5 / 32.0, 31 / 32.0)),
      new Quad(
          new Vector3(9 / 16.0, 24 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 24 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 30 / 16.0, 7 / 16.0),
          new Vector4(2 / 64.0, 4 / 64.0, 27.5 / 32.0, 31 / 32.0)),
      new Quad(
          new Vector3(7 / 16.0, 24 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 24 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 30 / 16.0, 9 / 16.0),
          new Vector4(6 / 64.0, 8 / 64.0, 27.5 / 32.0, 31 / 32.0)),
      // cube5
      new Quad(
          new Vector3(4 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(12 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(4 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector4(18 / 64.0, 30 / 64.0, 16 / 32.0, 17.5 / 32.0)),
      new Quad(
          new Vector3(4 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector3(12 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector3(4 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector4(18 / 64.0, 30 / 64.0, 16 / 32.0, 17.5 / 32.0)),
      new Quad(
          new Vector3(12 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(12 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector3(12 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector4(0, 3 / 64.0, 16 / 32.0, 17.5 / 32.0)),
      new Quad(
          new Vector3(4 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector3(4 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(4 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector4(15 / 64.0, 18 / 64.0, 16 / 32.0, 17.5 / 32.0)),
      new Quad(
          new Vector3(12 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector3(4 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector3(12 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector4(18 / 64.0, 30 / 64.0, 16 / 32.0, 17.5 / 32.0)),
      new Quad(
          new Vector3(4 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(12 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(4 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector4(18 / 64.0, 30 / 64.0, 16 / 32.0, 17.5 / 32.0)),
      // cube6
      new Quad(
          new Vector3(9 / 16.0, 21 / 16.0, 9 / 16.0),
          new Vector3(11 / 16.0, 21 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 21 / 16.0, 7 / 16.0),
          new Vector4(50 / 64.0, 52 / 64.0, 23 / 32.0, 24 / 32.0)),
      new Quad(
          new Vector3(9 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(11 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector4(52 / 64.0, 54 / 64.0, 23 / 32.0, 24 / 32.0)),
      new Quad(
          new Vector3(11 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(11 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(11 / 16.0, 21 / 16.0, 9 / 16.0),
          new Vector4(52 / 64.0, 54 / 64.0, 19.5 / 32.0, 23 / 32.0)),
      new Quad(
          new Vector3(9 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 21 / 16.0, 7 / 16.0),
          new Vector4(48 / 64.0, 50 / 64.0, 19.5 / 32.0, 23 / 32.0)),
      new Quad(
          new Vector3(11 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(11 / 16.0, 21 / 16.0, 7 / 16.0),
          new Vector4(54 / 64.0, 56 / 64.0, 19.5 / 32.0, 23 / 32.0)),
      new Quad(
          new Vector3(9 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(11 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 21 / 16.0, 9 / 16.0),
          new Vector4(50 / 64.0, 52 / 64.0, 19.5 / 32.0, 23 / 32.0)),
      // cube7
      new Quad(
          new Vector3(5 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(5 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector4(10 / 64.0, 12 / 64.0, 31 / 32.0, 32 / 32.0)),
      new Quad(
          new Vector3(5 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(5 / 16.0, 1 / 16.0, 9 / 16.0),
          new Vector4(12 / 64.0, 14 / 64.0, 31 / 32.0, 32 / 32.0)),
      new Quad(
          new Vector3(7 / 16.0, 1 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector4(12 / 64.0, 14 / 64.0, 25.5 / 32.0, 31 / 32.0)),
      new Quad(
          new Vector3(5 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(5 / 16.0, 1 / 16.0, 9 / 16.0),
          new Vector3(5 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector4(8 / 64.0, 10 / 64.0, 25.5 / 32.0, 31 / 32.0)),
      new Quad(
          new Vector3(7 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(5 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector4(14 / 64.0, 16 / 64.0, 25.5 / 32.0, 31 / 32.0)),
      new Quad(
          new Vector3(5 / 16.0, 1 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 1 / 16.0, 9 / 16.0),
          new Vector3(5 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector4(10 / 64.0, 12 / 64.0, 25.5 / 32.0, 31 / 32.0)),
      // cube8
      new Quad(
          new Vector3(9 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(11 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector4(42 / 64.0, 44 / 64.0, 23 / 32.0, 24 / 32.0)),
      new Quad(
          new Vector3(9 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(11 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 1 / 16.0, 9 / 16.0),
          new Vector4(44 / 64.0, 46 / 64.0, 23 / 32.0, 24 / 32.0)),
      new Quad(
          new Vector3(11 / 16.0, 1 / 16.0, 9 / 16.0),
          new Vector3(11 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(11 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector4(42 / 64.0, 40 / 64.0, 17.5 / 32.0, 23 / 32.0)),
      new Quad(
          new Vector3(9 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 1 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector4(46 / 64.0, 44 / 64.0, 17.5 / 32.0, 23 / 32.0)),
      new Quad(
          new Vector3(11 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(11 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector4(48 / 64.0, 46 / 64.0, 17.5 / 32.0, 23 / 32.0)),
      new Quad(
          new Vector3(9 / 16.0, 1 / 16.0, 9 / 16.0),
          new Vector3(11 / 16.0, 1 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector4(44 / 64.0, 42 / 64.0, 17.5 / 32.0, 23 / 32.0)),
  };

  private final double rotation;
  private final JsonObject gear;

  public ArmorStand(Vector3 position, double rotation, JsonObject gear) {
    super(position);
    this.rotation = rotation;
    this.gear = gear;
  }

  public ArmorStand(Vector3 position, double rotation, Tag tag) {
    this(position, rotation, parseGear(tag));
  }

  private static JsonObject parseGear(Tag tag) {
    JsonObject gear = new JsonObject();
    Tag armorItems = tag.get("ArmorItems");
    String boots = armorItems.get(0).get("id").stringValue("");
    if (!boots.isEmpty()) {
      gear.add("feet", boots);
    }
    String legs = armorItems.get(1).get("id").stringValue("");
    if (!legs.isEmpty()) {
      gear.add("legs", legs);
    }
    String chest = armorItems.get(2).get("id").stringValue("");
    if (!chest.isEmpty()) {
      gear.add("chest", chest);
    }
    String head = armorItems.get(3).get("id").stringValue("");
    if (!head.isEmpty()) {
      gear.add("head", head);
    }
    return gear;
  }

  @Override public Collection<Primitive> primitives(Vector3 offset) {
    Collection<Primitive> primitives = new LinkedList<>();
    Material material = new TextureMaterial(Texture.armorStand);

    // Add the base - not rotated.
    Transform transform = Transform.NONE.translate(-0.5, 0, -0.5)
        .translate(position.x + offset.x, position.y + offset.y, position.z + offset.z);
    for (Quad quad : base) {
      quad.addTriangles(primitives, material, transform);
    }


    JsonObject pose = new JsonObject();
    pose.add("pitch", 0);
    pose.add("headYaw", 0);
    pose.add("yaw", QuickMath.degToRad(180 - rotation));
    pose.add("leftArm", 0);
    pose.add("rightArm", 0);
    pose.add("leftLeg", 0);
    pose.add("rightLeg", 0);

    PlayerEntity.addArmor(primitives, gear, pose,
        new Vector3(position.x + offset.x, position.y + offset.y, position.z + offset.z),
        2);

    // Add the armor stand.
    double rot = QuickMath.degToRad(360 - rotation);
    transform = Transform.NONE.translate(-0.5, 0, -.5).rotateY(rot)
              .translate(position.x + offset.x, position.y + offset.y, position.z + offset.z);
    for (Quad quad : quads) {
      quad.addTriangles(primitives, material, transform);
    }
    return primitives;
  }

  @Override public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "armor_stand");
    json.add("position", position.toJson());
    json.add("rotation", rotation);
    json.add("gear", gear);
    return json;
  }

  /**
   * Deserialize entity from JSON.
   *
   * @return deserialized entity, or {@code null} if it was not a valid entity
   */
  public static Entity fromJson(JsonObject json) {
    Vector3 position = new Vector3();
    position.fromJson(json.get("position").object());
    double rotation = json.get("rotation").doubleValue(0.0);
    JsonObject gear = json.get("gear").object();
    return new ArmorStand(position, rotation, gear);
  }
}
