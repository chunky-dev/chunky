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
package se.llbit.chunky.world.model;

import se.llbit.json.JsonArray;
import se.llbit.json.JsonMember;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonValue;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonModel {
  private static String mapTexture(Map<String, String> textures, String texture) {
    while (texture.startsWith("#")) {
      String res = textures.get(texture.substring(1));
      if (res == null) {
        break;
      } else {
        texture = res;
      }
    }
    return texture;
  }

  private static Map<String, String> getTextures(JsonObject json) {
    Map<String, String> map = new HashMap<>();
    while (true) {
      for (JsonMember texture : json.get("textures").object()) {
        map.putIfAbsent(texture.name, texture.value.asString("#unknownTexture"));
      }
      json = json.get("parent").object();
      if (json.isEmpty()) {
        break;
      }
    }
    return map;
  }

  /**
   * Loads a model, links parent recursively.
   */
  private static JsonObject getModel(FileSystem fileSystem, String model)
      throws IOException, JsonParser.SyntaxError {
    Path stairs = fileSystem.getPath("/assets/minecraft/models/", model + ".json");
    try (InputStream in = Files.newInputStream(stairs);
        JsonParser parser = new JsonParser(in)) {
      System.out.println("loading model: " + model);
      JsonObject json = parser.parse().object();
      // Check for parent.
      String parent = json.get("parent").asString("");
      if (!parent.isEmpty()) {
        // Attach parent JSON.
        json.set("parent", getModel(fileSystem, parent));
      }
      return json;
    } catch (NoSuchFileException e) {
      System.err.println(e.getMessage());
      return new JsonObject();
    }
  }

  private static JsonArray getElements(JsonObject json) {
    JsonValue elements = json.get("elements");
    if (elements.isArray()) {
      return elements.array();
    } else {
      // Check for parent.
      JsonValue parent = json.get("parent");
      if (parent.isObject()) {
        return getElements(parent.object());
      } else {
        return new JsonArray();
      }
    }
  }

  public static Collection<Cube> get(File resourcePack, String model) {
    try (FileSystem jarFs = FileSystems.newFileSystem(URI.create("jar:" + resourcePack.toURI()),
        Collections.emptyMap())) {
      JsonObject json = getModel(jarFs, model);
      return fromJson(json);
    } catch (IOException | JsonParser.SyntaxError e) {
      e.printStackTrace();
      return Collections.emptyList();
    }
  }

  public static Collection<Cube> fromJson(String json) {
    try (InputStream in = new ByteArrayInputStream(json.getBytes());
        JsonParser parser = new JsonParser(in)) {
      return fromJson(parser.parse().object());
    } catch (IOException | JsonParser.SyntaxError e) {
      e.printStackTrace();
      return Collections.emptyList();
    }
  }

  public static Collection<Cube> fromJson(JsonObject json) {
    List<Cube> cubes = new ArrayList<>();
    int cubeId = 0;
    Map<String, String> textures = getTextures(json);
    JsonArray elements = getElements(json);
    for (JsonValue elementV : elements) {
      JsonObject element = elementV.object();
      JsonArray fromO = element.get("from").array();
      JsonArray toO = element.get("to").array();
      Vector3 from = new Vector3(fromO.get(0).asDouble(0), fromO.get(1).asDouble(0),
          fromO.get(2).asDouble(0));
      Vector3 to = new Vector3(toO.get(0).asDouble(0), toO.get(1).asDouble(0), toO.get(2).asDouble(0));
      Cube cube = new Cube();
      cube.name = "cube" + (++cubeId);
      cube.start = from;
      cube.end = to;
      for (Face face : cube.faces) {
        face.visible = false;
      }
      int faceId = 0;
      for (JsonMember face : element.get("faces").object()) {
        JsonObject faceO = face.value.object();
        JsonArray uva = faceO.get("uv").array();
        Face cubeFace = cube.faces[faceId++];
        cubeFace.name = face.getName();
        cubeFace.visible = true;
        cubeFace.texture = faceO.get("texture").asString("#unknown");
        switch (faceO.get("rotation").asInt(0)) {
          case 0:
            break;
          case 90:
            cubeFace.rotation = 1;
            break;
          case 180:
            cubeFace.rotation = 2;
            break;
          case 270:
            cubeFace.rotation = 3;
            break;
          default:
            System.err.format("WARNING: Unhandled rotation value: %d%n",
                faceO.get("rotation").asInt(0));
        }
        Vector4 uv = new Vector4();
        if (uva.size() >= 4) {
          uv = new Vector4(
              uva.get(0).asDouble(0),
              uva.get(2).asDouble(16),
              uva.get(1).asDouble(0),
              uva.get(3).asDouble(16));
        }
        switch (face.name) {
          case "up":
            if (uva.size() < 4) {
              uv = new Vector4(from.x, to.x, to.z, from.z);
            }
            break;
          case "down":
            if (uva.size() < 4) {
              uv = new Vector4(from.x, to.x, from.z, to.z);
            }
            break;
          case "north":
            if (uva.size() < 4) {
              uv = new Vector4(to.x, from.x, from.y, to.y);
            }
            break;
          case "south":
            if (uva.size() < 4) {
              uv = new Vector4(from.x, to.x, from.y, to.y);
            }
            break;
          case "east":
            if (uva.size() < 4) {
              uv = new Vector4(from.z, to.z, from.y, to.y);
            }
            break;
          case "west":
            if (uva.size() < 4) {
              uv = new Vector4(from.z, to.z, from.y, to.y);
            }
            break;
        }
        cubeFace.uv0.set(uv.x, uv.z);
        cubeFace.uv1.set(uv.y, uv.w);
      }
      cubes.add(cube);
    }
    for (Cube cube : cubes) {
      for (Face face : cube.faces) {
        if (face.visible) {
          face.texture = mapTexture(textures, face.texture);
        }
      }
    }
    return cubes;
  }
}
