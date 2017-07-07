/* Copyright (c) 2017 Chunky contributors
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
package se.llbit.chunky.plugin;

import se.llbit.chunky.Plugin;
import se.llbit.chunky.main.Version;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.log.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Helper class to load plugins.
 */
public final class ChunkyPlugin {

  private ChunkyPlugin() { }

  /**
   * Loads a plugin from the given file.
   * On successfully loading the plugin class, the plugin instance
   * and its manifest are sent to the onLoad consumer.
   *
   * @param pluginJar the plugin Jar file.
   * @param onLoad a function object that the plugin instance and manifest
   * are sent to after it successfully loads.
   */
  public static void load(File pluginJar, BiConsumer<Plugin, JsonObject> onLoad) {
    try (FileSystem zipFs = FileSystems.newFileSystem(URI.create("jar:" + pluginJar.toURI()),
        Collections.emptyMap())) {
      Path manifestPath = zipFs.getPath("/plugin.json");
      if (!Files.exists(manifestPath)) {
        Log.errorf("Missing plugin manifest file (plugin.json) in plugin %s", pluginJar.getName());
        return;
      }
      try (InputStream in = Files.newInputStream(manifestPath);
          JsonParser parser = new JsonParser(in)) {
        JsonObject manifest = parser.parse().object();

        String name = manifest.get("name").stringValue("");
        String main = manifest.get("main").stringValue("");
        if (name.isEmpty()) {
          Log.errorf("Plugin %s has no name specified", pluginJar.getName());
          return;
        }
        if (main.isEmpty()) {
          Log.errorf("Plugin %s has no main class specified", pluginJar.getName());
          return;
        }

        String targetVersion = manifest.get("targetVersion").stringValue("");
        if (!targetVersion.isEmpty() && !targetVersion.equalsIgnoreCase(Version.getVersion())) {
          Log.warnf("The plugin %s was developed for Chunky %s but this is Chunky %s "
              + "- it may not work properly.",
              name, targetVersion, Version.getVersion());
        }

        URLClassLoader classLoader = new URLClassLoader(new URL[] {pluginJar.toURI().toURL()});
        Class<?> pluginClass = classLoader.loadClass(main);
        Plugin plugin = (Plugin) pluginClass.newInstance();
        onLoad.accept(plugin, manifest);
      }
    } catch (IOException e) {
      Log.error("Could not load the plugin", e);
    } catch (ClassCastException e) {
      Log.error("Plugin main class has wrong type", e);
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      Log.error("Could not create plugin instance", e);
    } catch (JsonParser.SyntaxError e) {
      Log.error("Could not parse the plugin definition file", e);
    }
  }
}
