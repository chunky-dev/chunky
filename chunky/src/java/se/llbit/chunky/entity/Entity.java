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

import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.model.minecraft.DecoratedPotModel;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.math.Grid;
import se.llbit.math.Octree;
import se.llbit.math.Vector3;
import se.llbit.math.Vector3i;
import se.llbit.math.primitive.Primitive;

import java.util.Collection;
import java.util.LinkedList;

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

  public Grid.EmitterPosition[] getEmitterPosition() {
    return new Grid.EmitterPosition[0];
  }

  /**
   * Called on every entity in a scene to allow it to load it's data from other blocks in the Octree.
   *
   * @param octree  The scene's worldOctree
   * @param palette The scene's block palate
   * @param origin  The Octree's origin
   */
  public void loadDataFromOctree(Octree octree, BlockPalette palette, Vector3i origin) {
  }

  /**
   * Marshalls this entity to JSON.
   *
   * @return JSON object representing this entity.
   */
  abstract public JsonValue toJson();

  /**
   * Unmarshalls an entity object from JSON data.
   *
   * <p>This method only returns a {@link Collection} to support legacy scenes.</p>
   *
   * @param json json data.
   * @return The entities, or an empty collection if no entity was found.
   */
  public static Collection<Entity> entitiesFromJson(JsonObject json) {
    String kind = json.get("kind").stringValue("");
    Collection<Entity> entities = new LinkedList<>();
    switch (kind) {
      case "painting" -> entities.add(PaintingEntity.fromJson(json));
      case "sign" -> entities.add(SignEntity.fromJson(json));
      case "wallsign" -> entities.add(WallSignEntity.fromJson(json));
      case "skull" -> entities.add(SkullEntity.fromJson(json));
      case "head" -> entities.add(HeadEntity.fromJson(json));
      case "player" -> entities.add(PlayerEntity.fromJson(json));
      case "standing_banner" -> entities.add(StandingBanner.fromJson(json));
      case "wall_banner" -> entities.add(WallBanner.fromJson(json));
      case "armor_stand" -> entities.add(ArmorStand.fromJson(json));
      case "lily_pad" -> entities.add(LilyPadEntity.fromJson(json));
      case "coral_fan" -> entities.add(CoralFanEntity.fromJson(json));
      case "wall_coral_fan" -> entities.add(WallCoralFanEntity.fromJson(json));
      case "lectern" -> {
        if (json.get("book").isObject()) { // we still get the book from the lectern json to be compatible with the old format
          entities.add(Book.fromJson(json.get("book").object()));
        }
        entities.add(Lectern.fromJson(json));
      }
      case "campfire" -> entities.add(Campfire.fromJson(json));
      case "book" -> entities.add(Book.fromJson(json));
      case "flameParticles" -> entities.add(FlameParticles.fromJson(json));
      case "beaconBeam" -> entities.add(BeaconBeam.fromJson(json));
      case "sporeBlossom" -> entities.add(SporeBlossom.fromJson(json));
      case "decoratedPotSpout" -> entities.add(DecoratedPotModel.DecoratedPotSpoutEntity.fromJson(json));
      case "calibratedSculkSensorAmethyst" -> entities.add(CalibratedSculkSensorAmethyst.fromJson(json));
      case "hangingSign" -> entities.add(HangingSignEntity.fromJson(json));
      case "wallHangingSign" -> entities.add(WallHangingSignEntity.fromJson(json));
      case "sheep" -> entities.add(SheepEntity.fromJson(json));
      case "cow" -> entities.add(CowEntity.fromJson(json));
      case "chicken" -> entities.add(ChickenEntity.fromJson(json));
      case "pig" -> entities.add(PigEntity.fromJson(json));
      case "mooshroom" -> entities.add(MooshroomEntity.fromJson(json));
      case "squid" -> entities.add(SquidEntity.fromJson(json));
      case "copperGolemStatue" -> entities.add(CopperGolemStatueEntity.fromJson(json));
      default -> Log.errorf("Found unknown entity %s when loading from scene.", kind);
    }
    return entities;
  }

  public Vector3 getPosition() {
    return position;
  }

  public void setPosition(Vector3 position) {
    this.position.set(position);
  }
}
