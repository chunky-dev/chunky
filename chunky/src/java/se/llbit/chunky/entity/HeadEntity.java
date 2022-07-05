/*
 * Copyright (c) 2017 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2021 Chunky contributors
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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;
import se.llbit.chunky.renderer.scene.PlayerModel;
import se.llbit.chunky.resources.PlayerTexture;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texturepack.PlayerTextureLoader;
import se.llbit.chunky.resources.texturepack.TextureFormatError;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.math.QuickMath;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.primitive.Box;
import se.llbit.math.primitive.Primitive;
import se.llbit.util.mojangapi.MojangApi;

/**
 * A mob head (skull) entity.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class HeadEntity extends Entity {

  private static final Map<String, PlayerTexture> textureCache = Collections
      .synchronizedMap(new WeakHashMap<>());

  /**
   * The rotation of the skull when attached to a wall.
   */
  private final int rotation;

  /**
   * Decides if the skull is attached to a wall or the floor.
   */
  private final int placement;

  /**
   * The URL of the skin that is used for this entity.
   */
  private final String skin;

  public HeadEntity(Vector3 position, String skin, int rotation, int placement) {
    super(position);
    this.skin = skin;
    this.rotation = rotation;
    this.placement = placement;
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    double wallHeight = 0;
    if (placement >= 2) {
      wallHeight = 4 / 16.;
    }
    Transform transform = Transform.NONE
      .translate(position.x + offset.x,
        position.y + offset.y + 4 / 16. + wallHeight,
        position.z + offset.z);
    switch (placement) {
      case 0:
        // Unused.
        break;
      case 1:
        // On floor.
        transform = Transform.NONE.rotateY(-rotation * Math.PI / 8)
          .chain(transform);
        break;
      case 2:
        // Facing north.
        transform = Transform.NONE.translate(0, 0, 4 / 16.)
          .chain(transform);
        break;
      case 3:
        // Facing south.
        transform = Transform.NONE.translate(0, 0, 4 / 16.)
          .rotateY(Math.PI)
          .chain(transform);
        break;
      case 4:
        // Facing west.
        transform = Transform.NONE.translate(0, 0, 4 / 16.)
          .rotateY(QuickMath.HALF_PI)
          .chain(transform);
        break;
      case 5:
        // Facing east.
        transform = Transform.NONE.translate(0, 0, 4 / 16.)
          .rotateY(-QuickMath.HALF_PI)
          .chain(transform);
        break;
    }

    return primitives(transform);
  }

  public Collection<Primitive> primitives(Transform transform) {
    PlayerTexture texture = Texture.steve;
    if (skin != null && !skin.isEmpty()) {
      texture = downloadSkin();
    }

    Collection<Primitive> faces = new LinkedList<>();

    Box head = new Box(-4 / 16., 4 / 16., -4 / 16., 4 / 16., -4 / 16., 4 / 16.);
    Box hat = new Box(-4.25 / 16., 4.25 / 16., -4.25 / 16., 4.25 / 16., -4.25 / 16., 4.25 / 16.);
    head.transform(transform);
    head.addFrontFaces(faces, texture, texture.getUV().headFront);
    head.addBackFaces(faces, texture, texture.getUV().headBack);
    head.addTopFaces(faces, texture, texture.getUV().headTop);
    head.addBottomFaces(faces, texture, texture.getUV().headBottom);
    head.addRightFaces(faces, texture, texture.getUV().headRight);
    head.addLeftFaces(faces, texture, texture.getUV().headLeft);
    hat.transform(transform);
    hat.addFrontFaces(faces, texture, texture.getUV().hatFront);
    hat.addBackFaces(faces, texture, texture.getUV().hatBack);
    hat.addLeftFaces(faces, texture, texture.getUV().hatLeft);
    hat.addRightFaces(faces, texture, texture.getUV().hatRight);
    hat.addTopFaces(faces, texture, texture.getUV().hatTop);
    hat.addBottomFaces(faces, texture, texture.getUV().hatBottom);
    return faces;
  }

  /**
   * Download the skin or take it from the cache.
   *
   * @param skin The URL of the skin
   * @return The downloaded/cached skin or the steve skin if the download failed
   */
  public static PlayerTexture downloadTexture(String skin) {
    return textureCache.computeIfAbsent(skin, (skinUrl) -> {
      PlayerTexture texture = new PlayerTexture();
      PlayerTextureLoader loader = new PlayerTextureLoader(skinUrl, texture, PlayerModel.STEVE);
      try {
        File cacheFile = MojangApi.downloadSkin(skin);
        try {
          loader.load(cacheFile);
          return texture;
        } catch (IOException | TextureFormatError  e) {
          Log.warn("Failed to load skin downloaded from " + skinUrl, e);
          cacheFile.delete();
        }
      } catch (IOException e) {
        Log.warn("Failed to download skin from " + skinUrl, e);
      }
      return Texture.steve;
    });
  }

  /**
   * Download the skin or take it from the cache.
   *
   * @return The downloaded/cached skin or the steve skin if the download failed
   */
  private PlayerTexture downloadSkin() {
    return downloadTexture(skin);
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "head");
    json.add("position", position.toJson());
    //json.add("type", type);
    json.add("rotation", rotation);
    json.add("placement", placement);
    json.add("skin", skin);
    return json;
  }

  public static Entity fromJson(JsonObject json) {
    Vector3 position = new Vector3();
    position.fromJson(json.get("position").object());
    //int type = json.get("type").intValue(0);
    int rotation = json.get("rotation").intValue(0);
    int placement = json.get("placement").intValue(0);
    String skin = json.get("skin").stringValue("");
    return new HeadEntity(position, skin, rotation, placement);
  }


}
