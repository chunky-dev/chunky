/* Copyright (c) 2014-2015 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.util.mojangapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import se.llbit.chunky.PersistentSettings;
import se.llbit.json.*;
import se.llbit.json.JsonParser.SyntaxError;
import se.llbit.util.Util;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

/**
 * Utility class to download Minecraft Jars and player data.
 */
public class MojangApi {

  // the base64-encoded string might not be valid json (sometimes keys are not quoted)
  // so we use lenient mode to extract the skin url
  static final Gson GSON = new GsonBuilder()
    .disableJdkUnsafe()
    .setLenient()
    .create();

  /**
   * Download a Minecraft Jar by version name.
   */
  public static void downloadMinecraft(String version, File destination) throws IOException {
    String theUrl = getClientUrl(getVersionManifestUrl(version));
    System.out.println("url: " + theUrl);
    System.out.println("destination: " + destination.getAbsolutePath());
    URL url = new URL(theUrl);
    try (
      ReadableByteChannel inChannel = Channels.newChannel(url.openStream());
      FileOutputStream out = new FileOutputStream(destination)
    ) {
      out.getChannel().transferFrom(inChannel, 0, Long.MAX_VALUE);
    }
  }

  /**
   * Get the version manifest url for the given Minecraft version.
   *
   * @param version Minecraft version, e.g. 20w16a or 1.12
   * @return URL of the version manifest, to be used with {@link #getClientUrl}
   * @throws IOException if the download failed or the version is unknown
   */
  private static String getVersionManifestUrl(final String version) throws IOException {
    HttpsURLConnection conn =
      (HttpsURLConnection)
        new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json")
          .openConnection();
    int responseCode = conn.getResponseCode();
    if (responseCode == 200) {
      try (JsonParser parser = new JsonParser(conn.getInputStream())) {
        JsonObject parsed = parser.parse().asObject();
        for (JsonValue versionData : parsed.get("versions").asArray()) {
          if (versionData.asObject().get("id").asString("").equals(version)) {
            return versionData.asObject().get("url").asString("");
          }
        }
        throw new IOException("Unknown version: " + version);
      } catch (SyntaxError e) {
        throw new IOException("Could not parse versions file", e);
      }
    } else {
      throw new IOException("Could not fetch versions file (status " + responseCode + ")");
    }
  }

  /**
   * Get the download URL of the minecraft.jar from the given version manifest url.
   *
   * @param versionManifestUrl URL of the version manifest
   * @return URL of the minecraft.jar
   * @throws IOException if the download failed
   */
  private static String getClientUrl(final String versionManifestUrl) throws IOException {
    HttpsURLConnection conn = (HttpsURLConnection) new URL(versionManifestUrl).openConnection();
    int responseCode = conn.getResponseCode();
    if (responseCode == 200) {
      try (JsonParser parser = new JsonParser(conn.getInputStream())) {
        return parser
          .parse()
          .asObject()
          .get("downloads")
          .asObject()
          .get("client")
          .asObject()
          .get("url")
          .asString("");
      } catch (SyntaxError e) {
        throw new IOException("Could not parse versions file", e);
      }
    } else {
      throw new IOException("Could not fetch versions file (status " + responseCode + ")");
    }
  }

  /**
   * Download a player skin by its URL or get it from the cache.
   *
   * @param url URL of the skin
   * @return Cached file of the skin
   */
  public static File downloadSkin(String url) throws IOException {
    if (!PersistentSettings.cacheDirectory().isDirectory()) {
      PersistentSettings.cacheDirectory().mkdirs();
    }
    File file = new File(PersistentSettings.cacheDirectory(),
      Util.cacheEncode((url + ":skin").hashCode()));
    if (!file.exists()) {
      try (ReadableByteChannel inChannel = Channels.newChannel(new URL(url).openStream());
           FileOutputStream out = new FileOutputStream(file)) {
        out.getChannel().transferFrom(inChannel, 0, Long.MAX_VALUE);
      }
    }
    return file;
  }

  /**
   * Get the given Base64 string with proper padding. Sometimes the base64-encoded texture string
   * isn't padded properly which would cause an IllegalArgumentException (wrong 4-byte ending unit)
   * because Java can't handle that.
   *
   * @param base64String Base64 string with or without proper padding
   * @return Base64 string with proper padding
   */
  static String fixBase64Padding(String base64String) {
    // the length of a base64 string must be a multiple of 4
    int missingPadding = (4 - (base64String.length() % 4)) % 4;
    if (missingPadding == 0) {
      return base64String;
    }

    StringBuilder fixedBase64 = new StringBuilder(base64String);
    for (int i = 0; i < missingPadding; i++) {
      fixedBase64.append("=");
    }
    return fixedBase64.toString();
  }

  /**
   * Download a Minecraft player profile.
   *
   * @param uuid UUID of player
   * @throws IOException if downloading the profile failed
   */
  public static MinecraftProfile fetchProfile(String uuid) throws IOException {
    uuid = uuid.toLowerCase();
    String key = uuid + ":profile";
    File cacheFile =
      new File(PersistentSettings.cacheDirectory(), Util.cacheEncode(key.hashCode()));
    JsonArray cache;
    if (cacheFile.exists()) {
      try (JsonParser cacheParse = new JsonParser(new FileInputStream(cacheFile))) {
        cache = cacheParse.parse().array();
        for (JsonValue entry : cache) {
          if (entry.array().get(0).stringValue("").equals(key)) {
            JsonObject profile = entry.array().get(1).object();
            return GSON.fromJson(profile.toString(), MinecraftProfile.class);
          }
        }
      } catch (JsonParser.SyntaxError e) {
        cache = new JsonArray();
      }
    } else {
      cache = new JsonArray();
    }

    String url = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid;
    HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
    int responseCode = conn.getResponseCode();
    MinecraftProfile profile;
    if (responseCode == 200) {
      try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
        profile = GSON.fromJson(reader, MinecraftProfile.class);
      } catch (JsonParseException e) {
        e.printStackTrace(System.err);
        profile = new MinecraftProfile();
      }
    } else {
      profile = new MinecraftProfile();
    }

    JsonArray newEntry = new JsonArray();
    newEntry.add(key);
    try {
      newEntry.add(new JsonParser(new ByteArrayInputStream(GSON.toJson(profile).getBytes(StandardCharsets.UTF_8))).parse().asObject());
      cache.add(newEntry);
      if (!PersistentSettings.cacheDirectory().isDirectory()) {
        PersistentSettings.cacheDirectory().mkdirs();
      }
      try (FileOutputStream out = new FileOutputStream(cacheFile)) {
        PrettyPrinter jsonOut = new PrettyPrinter("", new PrintStream(out));
        cache.prettyPrint(jsonOut);
      }
    } catch (SyntaxError e) {
      // ignore
    }
    return profile;
  }

  /**
   * Get UUID of a player from their username.
   *
   * @param username username of player
   * @return UUID of player if found, null otherwise
   * @throws IOException if retrieving UUID fails
   */
  public static String usernameToUUID(String username) throws IOException {
    //Lookup in cache first
    File cacheFile = new File(PersistentSettings.cacheDirectory(), "uuidCache.json");
    JsonObject cache;
    if (cacheFile.exists()) {
      try (JsonParser cacheParse = new JsonParser(new FileInputStream(cacheFile))) {
        cache = cacheParse.parse().object();
        if (!cache.get(username).isUnknown()) {
          return cache.get(username).asString(null);
        }
      } catch (JsonParser.SyntaxError e) {
        cache = new JsonObject();
      }
    } else {
      cache = new JsonObject();
    }

    //Request uuid from Mojang
    String url = "https://api.mojang.com/users/profiles/minecraft/" + username;
    HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
    int responseCode = conn.getResponseCode();
    JsonObject response;
    if (responseCode == 200) {
      try (JsonParser parser = new JsonParser(conn.getInputStream())) {
        response = parser.parse().object();
      } catch (JsonParser.SyntaxError e) {
        e.printStackTrace(System.err);
        response = new JsonObject();
      }
    } else {
      response = new JsonObject();
    }

    //Save in cache if there's a valid response
    String uuid = response.get("id").asString(null);
    if (uuid != null) {
      cache.set(username, response.get("id"));
      if (!PersistentSettings.cacheDirectory().isDirectory()) {
        PersistentSettings.cacheDirectory().mkdirs();
      }
      try (FileOutputStream out = new FileOutputStream(cacheFile)) {
        PrettyPrinter jsonOut = new PrettyPrinter("", new PrintStream(out));
        cache.prettyPrint(jsonOut);
      }
    }

    return response.get("id").asString(null);
  }
}
