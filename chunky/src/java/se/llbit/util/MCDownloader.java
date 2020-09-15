/* Copyright (c) 2014-2015 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import javax.net.ssl.HttpsURLConnection;
import se.llbit.chunky.PersistentSettings;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonParser.SyntaxError;
import se.llbit.json.JsonValue;
import se.llbit.json.PrettyPrinter;

/**
 * Utility class to download Minecraft Jars and player data.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class MCDownloader {

  /** Download a Minecraft Jar by version name. */
  public static void downloadMC(String version, File destination) throws IOException {
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

  /** Download a player skin by player name. */
  public static void downloadSkin(String name, File destDir) throws IOException {
    String theUrl = String.format("http://s3.amazonaws.com/MinecraftSkins/%s.png", name);
    File destination = new File(destDir, name + ".skin.png");
    URL url = new URL(theUrl);
    try (
      ReadableByteChannel inChannel = Channels.newChannel(url.openStream());
      FileOutputStream out = new FileOutputStream(destination)
    ) {
      out.getChannel().transferFrom(inChannel, 0, Long.MAX_VALUE);
    }
  }

  /**
   * Download a Minecraft player profile.
   *
   * @param uuid UUID of player
   * @throws IOException
   */
  public static JsonObject fetchProfile(String uuid) throws IOException {
    String key = uuid + ":profile";
    File cacheFile =
        new File(PersistentSettings.cacheDirectory(), Util.cacheEncode(key.hashCode()));
    JsonArray cache;
    if (cacheFile.exists()) {
      try (JsonParser cacheParse = new JsonParser(new FileInputStream(cacheFile))) {
        cache = cacheParse.parse().array();
        for (JsonValue entry : cache) {
          if (entry.array().get(0).stringValue("").equals(key)) {
            return entry.array().get(1).object();
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
    JsonObject profile;
    if (responseCode == 200) {
      try (JsonParser parser = new JsonParser(conn.getInputStream())) {
        profile = parser.parse().object();
        // TODO unparse base64 data.
      } catch (JsonParser.SyntaxError e) {
        e.printStackTrace(System.err);
        profile = new JsonObject();
      }
    } else {
      profile = new JsonObject();
    }

    JsonArray newEntry = new JsonArray();
    newEntry.add(key);
    newEntry.add(profile);
    cache.add(newEntry);
    if (!PersistentSettings.cacheDirectory().isDirectory()) {
      PersistentSettings.cacheDirectory().mkdirs();
    }
    try (FileOutputStream out = new FileOutputStream(cacheFile)) {
      PrettyPrinter jsonOut = new PrettyPrinter("", new PrintStream(out));
      cache.prettyPrint(jsonOut);
    }
    return profile;
  }
}
