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

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.model.DecoratedPotModel;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Grid;
import se.llbit.math.Octree;
import se.llbit.math.Vector3;
import se.llbit.math.Vector3i;
import se.llbit.math.primitive.Primitive;

/**
 * Represents Minecraft entities that are not stored in the octree.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
abstract public class Entity {

  public final Vector3 position;

  protected Entity(Vector3 position) {
    this.position = new Vector3(position);
  }

  abstract public Collection<Primitive> primitives(Vector3 offset);

  public Grid.EmitterPosition[] getEmitterPosition() { return new Grid.EmitterPosition[0]; }

  /**
   * Called on every entity in a scene to allow it to load it's data from other blocks in the Octree.
   *
   * @param octree The scene's worldOctree
   * @param palette The scene's block palate
   * @param origin The Octree's origin
   */
  public void loadDataFromOctree(Octree octree, BlockPalette palette, Vector3i origin) {}

  /**
   * Marshalls this entity to JSON.
   *
   * @return JSON object representing this entity.
   */
  abstract public JsonValue toJson();

  /**
   * Unmarshalls an entity object from JSON data.
   *
   * @param json json data.
   * @return unmarshalled entity, or {@code null} if it was not a valid entity.
   */
  public static Entity fromJson(JsonObject json) {
    String kind = json.get("kind").stringValue("");
    switch (kind) {
      case "painting":
        return PaintingEntity.fromJson(json);
      case "sign":
        return SignEntity.fromJson(json);
      case "wallsign":
        return WallSignEntity.fromJson(json);
      case "skull":
        return SkullEntity.fromJson(json);
      case "head":
        return HeadEntity.fromJson(json);
      case "player":
        return PlayerEntity.fromJson(json);
      case "standing_banner":
        return StandingBanner.fromJson(json);
      case "wall_banner":
        return WallBanner.fromJson(json);
      case "armor_stand":
        return ArmorStand.fromJson(json);
      case "lily_pad":
        return LilyPadEntity.fromJson(json);
      case "coral_fan":
        return CoralFanEntity.fromJson(json);
      case "wall_coral_fan":
        return WallCoralFanEntity.fromJson(json);
      case "lectern":
        return Lectern.fromJson(json);
      case "campfire":
        return Campfire.fromJson(json);
      case "book":
        return Book.fromJson(json);
      case "flameParticles":
        return FlameParticles.fromJson(json);
      case "beaconBeam":
        return BeaconBeam.fromJson(json);
      case "sporeBlossom":
        return SporeBlossom.fromJson(json);
      case "decoratedPotSpout":
        return DecoratedPotModel.DecoratedPotSpoutEntity.fromJson(json);
    }
    return null;
  }

  public Vector3 getPosition() {
    return position;
  }

  public void setPosition(Vector3 position) {
    this.position.set(position);
  }
}
