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

import se.llbit.chunky.block.CoralFan;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;

import java.util.Collection;
import java.util.LinkedList;

/**
 * A mob head (skull) entity.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class CoralFanEntity extends Entity {

  private static final Quad[] quads = {
      // cube1
      new DoubleSidedQuad(new Quad(
          new Vector3(8 / 16.0, 0, 0),
          new Vector3(8 / 16.0, 0, 16 / 16.0),
          new Vector3(24 / 16.0, 0, 0),
          new Vector4(0, 16 / 16.0, 0, 16 / 16.0)),
          Transform.NONE.translate(0, 0.5 / 1.0, 0.5 / 1.0)
              .rotateZ(0.39269908169872414 / 1.0)
              .translate(0, -0.5 / 1.0, -0.5 / 1.0)),
      // cube2
      new DoubleSidedQuad(new Quad(
          new Vector3(8 / 16.0, 0, 16 / 16.0),
          new Vector3(8 / 16.0, 0, 0),
          new Vector3(-8 / 16.0, 0, 16 / 16.0),
          new Vector4(0, 16 / 16.0, 0, 16 / 16.0)),
          Transform.NONE.translate(0, 0.5 / 1.0, 0.5 / 1.0)
              .rotateZ(-0.39269908169872414 / 1.0)
              .translate(0, -0.5 / 1.0, -0.5 / 1.0)),
      // cube3
      new DoubleSidedQuad(new Quad(
          new Vector3(0, 0, 24 / 16.0),
          new Vector3(16 / 16.0, 0, 24 / 16.0),
          new Vector3(0, 0, 8 / 16.0),
          new Vector4(16 / 16.0, 0, 16 / 16.0, 0)),
          Transform.NONE.translate(0.5 / 1.0, 0.5 / 1.0, 0)
              .rotateX(-0.39269908169872414 / 1.0)
              .translate(-0.5 / 1.0, -0.5 / 1.0, 0)),
      // cube4
      new DoubleSidedQuad(new Quad(
          new Vector3(0, 0, 8 / 16.0),
          new Vector3(16 / 16.0, 0, 8 / 16.0),
          new Vector3(0, 0, -8 / 16.0),
          new Vector4(0, 16 / 16.0, 0, 16 / 16.0)),
          Transform.NONE.translate(0.5 / 1.0, 0.5 / 1.0, 0)
              .rotateX(0.39269908169872414 / 1.0)
              .translate(-0.5 / 1.0, -0.5 / 1.0, 0)),
  };
  private final String coralType;

  public CoralFanEntity(Vector3 position, String coralType) {
    super(position);
    this.coralType = coralType;
  }

  @Override public Collection<Primitive> primitives(Vector3 offset) {
    Collection<Primitive> faces = new LinkedList<>();
    Transform transform = Transform.NONE
        .translate(position.x + offset.x,
            position.y + offset.y,
            position.z + offset.z);
    Texture texture = CoralFan.coralTexture(coralType);
    Material mat = new TextureMaterial(texture);
    for (Quad quad : quads) {
      quad.addTriangles(faces, mat, transform);
    }
    return faces;
  }

  @Override public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "coral_fan");
    json.add("position", position.toJson());
    json.add("coral_type", coralType);
    return json;
  }

  public static Entity fromJson(JsonObject json) {
    Vector3 position = new Vector3();
    position.fromJson(json.get("position").object());
    return new CoralFanEntity(position, json.get("coral_type").stringValue("tube"));
  }


}
