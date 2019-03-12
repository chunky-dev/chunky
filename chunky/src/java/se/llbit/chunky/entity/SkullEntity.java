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

import se.llbit.chunky.resources.EntityTexture;
import se.llbit.chunky.resources.Texture;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.QuickMath;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.primitive.Box;
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.CompoundTag;

/**
 * A mob head (skull) entity.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class SkullEntity extends Entity {

  /**
   * The skull type, i.e. Creeper head, Skeleton skull, etc.
   */
  private final int type;

  /**
   * The rotation of the skull when attached to a wall.
   */
  private final int rotation;

  /**
   * Decides if the skull is attached to a wall or the floor.
   */
  private final int placement;

  public SkullEntity(Vector3 position, CompoundTag entityTag, int metadata) {
    this(position, entityTag.get("SkullType").byteValue(0), entityTag.get("Rot").byteValue(0),
        metadata);
  }

  public SkullEntity(Vector3 position, int type, int rotation, int placement) {
    super(position);
    this.type = type;
    this.rotation = rotation;
    this.placement = placement;
    // TODO(jesper): add SkullOwner handling.
  }

  @Override public Collection<Primitive> primitives(Vector3 offset) {
    Collection<Primitive> faces = new LinkedList<>();
    double wallHeight = 0;
    if (placement >= 2) {
      wallHeight = 4 / 16.;
    }
    Transform transform = Transform.NONE
        .translate(position.x + offset.x + 0.5,
            position.y + offset.y + 4 / 16. + wallHeight,
            position.z + offset.z + 0.5);
    EntityTexture texture;
    switch (type) {
      case 0:
        texture = Texture.skeleton;
        break;
      case 1:
        texture = Texture.wither;
        break;
      case 2:
        texture = Texture.zombie;
        break;
      case 3:
        texture = Texture.steve;
        break;
      case 4:
        texture = Texture.creeper;
        break;
      default:
        texture = Texture.steve;
    }
    Box head = new Box(-4 / 16., 4 / 16., -4 / 16., 4 / 16., -4 / 16., 4 / 16.);
    switch (placement) {
      case 0:
        // Unused.
        break;
      case 1:
        // On floor.
        head.transform(Transform.NONE.rotateY(-rotation * Math.PI / 8));
        break;
      case 2:
        // Facing north.
        head.transform(Transform.NONE.translate(0, 0, 4 / 16.));
        break;
      case 3:
        // Facing south.
        head.transform(Transform.NONE.translate(0, 0, 4 / 16.));
        head.transform(Transform.NONE.rotateY(Math.PI));
        break;
      case 4:
        // Facing west.
        head.transform(Transform.NONE.translate(0, 0, 4 / 16.));
        head.transform(Transform.NONE.rotateY(QuickMath.HALF_PI));
        break;
      case 5:
        // Facing east.
        head.transform(Transform.NONE.translate(0, 0, 4 / 16.));
        head.transform(Transform.NONE.rotateY(-QuickMath.HALF_PI));
        break;
    }
    head.transform(transform);
    head.addFrontFaces(faces, texture, texture.headFront);
    head.addBackFaces(faces, texture, texture.headBack);
    head.addTopFaces(faces, texture, texture.headTop);
    head.addBottomFaces(faces, texture, texture.headBottom);
    head.addRightFaces(faces, texture, texture.headRight);
    head.addLeftFaces(faces, texture, texture.headLeft);
    return faces;
  }

  @Override public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "skull");
    json.add("position", position.toJson());
    json.add("type", type);
    json.add("rotation", rotation);
    json.add("placement", placement);
    return json;
  }

  public static Entity fromJson(JsonObject json) {
    Vector3 position = new Vector3();
    position.fromJson(json.get("position").object());
    int type = json.get("type").intValue(0);
    int rotation = json.get("rotation").intValue(0);
    int placement = json.get("placement").intValue(0);
    return new SkullEntity(position, type, rotation, placement);
  }


}
