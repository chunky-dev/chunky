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

import org.jastadd.util.PrettyPrinter;

import se.llbit.chunky.PersistentSettings;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonValue;

/**
 * Utility class to download Minecraft Jars and player data.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class MCDownloader {

  /**
   * Download a Minecraft Jar by version name.
   */
  public static void downloadMC(String version, File destDir) throws IOException {
    String theUrl = String
        .format("https://s3.amazonaws.com/Minecraft.Download/versions/%s/%s.jar", version, version);
    File destination = new File(destDir, "minecraft.jar");
    System.out.println("url: " + theUrl);
    System.out.println("destination: " + destination.getAbsolutePath());
    URL url = new URL(theUrl);
    ReadableByteChannel inChannel = Channels.newChannel(url.openStream());
    FileOutputStream out = new FileOutputStream(destination);
    out.getChannel().transferFrom(inChannel, 0, Long.MAX_VALUE);
    out.close();
  }

  /**
   * Download a player skin by player name.
   */
  public static void downloadSkin(String name, File destDir) throws IOException {
    String theUrl = String.format("http://s3.amazonaws.com/MinecraftSkins/%s.png", name);
    File destination = new File(destDir, name + ".skin.png");
    URL url = new URL(theUrl);
    ReadableByteChannel inChannel = Channels.newChannel(url.openStream());
    FileOutputStream out = new FileOutputStream(destination);
    out.getChannel().transferFrom(inChannel, 0, Long.MAX_VALUE);
    out.close();
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
      FileInputStream in = new FileInputStream(cacheFile);
      JsonParser cacheParse = new JsonParser(in);
      try {
        cache = cacheParse.parse().array();
        in.close();
        for (JsonValue entry : cache.getElementList()) {
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
      try {
        JsonParser parser = new JsonParser(conn.getInputStream());
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
    FileOutputStream out = new FileOutputStream(cacheFile);
    PrettyPrinter jsonOut = new PrettyPrinter("", new PrintStream(out));
    cache.prettyPrint(jsonOut);
    out.close();
    return profile;
  }
}
