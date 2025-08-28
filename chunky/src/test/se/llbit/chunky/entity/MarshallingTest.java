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

import org.junit.Test;
import se.llbit.json.Json;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonValue;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;

import static org.junit.Assert.assertEquals;

/**
 * Test entity marshalling/unmarshalling to/from JSON.
 */
public class MarshallingTest {
  @Test public void testPlayer() {
    PlayerEntity entity = new PlayerEntity("1234", new Vector3(100, 200, 300));
    JsonArray headPose = new JsonArray();
    headPose.add(Json.of(33));
    headPose.add(Json.of(66));
    headPose.add(Json.of(99));
    entity.getPose().set("head", headPose);
    JsonValue json = entity.toJson();
    PlayerEntity loaded = new PlayerEntity(json.object());
    assertEquals(100, loaded.getPosition().x, 0.01);
    assertEquals(200, loaded.getPosition().y, 0.01);
    assertEquals(300, loaded.getPosition().z, 0.01);
    assertEquals(33, loaded.getPose("head").x, 0.01);
    assertEquals(66, loaded.getPose("head").y, 0.01);
    assertEquals(99, loaded.getPose("head").z, 0.01);
  }

  @Test public void testArmorStand() {
    ArmorStand entity = new ArmorStand(new Vector3(100, 200, 300), new CompoundTag());
    JsonArray headPose = new JsonArray();
    headPose.add(Json.of(33));
    headPose.add(Json.of(66));
    headPose.add(Json.of(99));
    entity.getPose().set("head", headPose);
    JsonValue json = entity.toJson();
    ArmorStand loaded = new ArmorStand(json.object());
    assertEquals(100, loaded.getPosition().x, 0.01);
    assertEquals(200, loaded.getPosition().y, 0.01);
    assertEquals(300, loaded.getPosition().z, 0.01);
    assertEquals(33, loaded.getPose("head").x, 0.01);
    assertEquals(66, loaded.getPose("head").y, 0.01);
    assertEquals(99, loaded.getPose("head").z, 0.01);
  }
}
