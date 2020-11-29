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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.resources.EntityTexture;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texturepack.EntityTextureLoader;
import se.llbit.chunky.resources.texturepack.TextureFormatError;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.math.QuickMath;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.primitive.Box;
import se.llbit.math.primitive.Primitive;
import se.llbit.util.Util;

/**
 * A mob head (skull) entity.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class HeadEntity extends Entity {

  private static final Map<String, EntityTexture> textureCache = Collections
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
    EntityTexture texture = Texture.steve;
    if (skin != null && !skin.isEmpty()) {
      texture = downloadSkin();
    }

    Collection<Primitive> faces = new LinkedList<>();
    double wallHeight = 0;
    if (placement >= 2) {
      wallHeight = 4 / 16.;
    }
    Transform transform = Transform.NONE
        .translate(position.x + offset.x + 0.5,
            position.y + offset.y + 4 / 16. + wallHeight,
            position.z + offset.z + 0.5);
    Box head = new Box(-4 / 16., 4 / 16., -4 / 16., 4 / 16., -4 / 16., 4 / 16.);
    Box hat = new Box(-4.25 / 16., 4.25 / 16., -4.25 / 16., 4.25 / 16., -4.25 / 16., 4.25 / 16.);
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
    head.transform(transform);
    head.addFrontFaces(faces, texture, texture.headFront);
    head.addBackFaces(faces, texture, texture.headBack);
    head.addTopFaces(faces, texture, texture.headTop);
    head.addBottomFaces(faces, texture, texture.headBottom);
    head.addRightFaces(faces, texture, texture.headRight);
    head.addLeftFaces(faces, texture, texture.headLeft);
    hat.transform(transform);
    hat.addFrontFaces(faces, texture, texture.hatFront);
    hat.addBackFaces(faces, texture, texture.hatBack);
    hat.addLeftFaces(faces, texture, texture.hatLeft);
    hat.addRightFaces(faces, texture, texture.hatRight);
    hat.addTopFaces(faces, texture, texture.hatTop);
    hat.addBottomFaces(faces, texture, texture.hatBottom);
    return faces;
  }

  /**
   * Download the skin or take it from the cache.
   *
   * @param skin The URL of the skin
   * @return The downloaded/cached skin or the steve skin if the download failed
   */
  public static EntityTexture downloadTexture(String skin) {
    return textureCache.computeIfAbsent(skin, (skinUrl) -> {
      EntityTexture texture = new EntityTexture();
      EntityTextureLoader loader = new EntityTextureLoader(skinUrl, texture);
      if (!PersistentSettings.cacheDirectory().isDirectory()) {
        PersistentSettings.cacheDirectory().mkdirs();
      }
      File cacheFile = new File(PersistentSettings.cacheDirectory(),
          Util.cacheEncode((skin + ":skin").hashCode()));
      if (cacheFile.exists()) {
        try {
          loader.load(cacheFile);
          return texture;
        } catch (IOException | TextureFormatError e) {
          // ignore, try download below
        }
      }
      try (ReadableByteChannel inChannel = Channels.newChannel(new URL(skinUrl).openStream());
          FileOutputStream out = new FileOutputStream(cacheFile)) {
        out.getChannel().transferFrom(inChannel, 0, Long.MAX_VALUE);
      } catch (IOException e) {
        Log.warn("Failed to download skin from " + skinUrl, e);
        return Texture.steve;
      }
      try {
        loader.load(cacheFile);
        return texture;
      } catch (IOException | TextureFormatError e) {
        cacheFile.delete();
        Log.warn("Failed to load skin downloaded from " + skinUrl, e);
      }

      return EntityTexture.steve;
    });
  }

  /**
   * Download the skin or take it from the cache.
   *
   * @return The downloaded/cached skin or the steve skin if the download failed
   */
  private EntityTexture downloadSkin() {
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
