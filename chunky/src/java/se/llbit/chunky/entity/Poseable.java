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

import org.apache.commons.math3.util.FastMath;
import se.llbit.json.Json;
import se.llbit.json.JsonObject;
import se.llbit.math.Vector3;
import se.llbit.util.JsonUtil;

/**
 * Defines the API for poseable entities.
 *
 * <p>A poseable entity contains several named parts that can be individually posed.
 */
public interface Poseable {
  /**
   * @return an array of the names of the parts of this entity.
   */
  String[] partNames();

  double getScale();
  void setScale(double value);

  double getHeadScale();
  void setHeadScale(double value);

  Vector3 getPosition();

  default void lookAt(Vector3 target) {
    Vector3 dir = new Vector3(target);
    Vector3 face = new Vector3(getPosition());
    face.add(0, 28 / 16., 0);
    dir.sub(face);
    dir.normalize();
    double headYaw = getPose("head").y;
    getPose().set("rotation", Json.of(FastMath.atan2(dir.x, dir.z) + Math.PI - headYaw));
    double pitch = Math.asin(dir.y);
    getPose().add("head", JsonUtil.vec3ToJson(new Vector3(pitch, headYaw, 0)));
  }


  JsonObject getPose();

  /**
   * Get the pose for one body part.
   */
  default Vector3 getPose(String part) {
    return JsonUtil.vec3FromJsonArray(getPose().get(part));
  }
}
