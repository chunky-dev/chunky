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
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map;

/**
 * A chunky plugin.
 */
public class ChunkyPlugin {
  private final JsonObject meta;
  private final Plugin implementation;

  protected ChunkyPlugin(JsonObject meta, Plugin implementation) {
    this.meta = meta;
    this.implementation = implementation;
  }

  /**
   * Gets the name of this plugin.
   *
   * @return the name of this plugin
   */
  public String getName() {
    return meta.get("name").stringValue("");
  }

  /**
   * Gets the version of this plugin.
   *
   * @return the version of this plugin
   */
  public String getVersion() {
    return meta.get("version").stringValue("<Unknown version>");
  }

  /**
   * Gets the implementation of this plugin.
   *
   * @return the implementation of this plugin
   */
  public Plugin getImplementation() {
    return implementation;
  }

  public static ChunkyPlugin load(File pluginJar) throws LoadPluginException {
    Map<String, String> env = new HashMap<>();
    env.put("create", "true");

    try (FileSystem zipFs = FileSystems.newFileSystem(URI.create("jar:file:" + pluginJar.getAbsolutePath()), env);
         InputStream in = zipFs.getPath("/plugin.json").toUri().toURL().openStream();
         JsonParser parser = new JsonParser(in)) {
      JsonObject pluginDefiniton = parser.parse().object();

      String name = pluginDefiniton.get("name").stringValue("");
      String main = pluginDefiniton.get("main").stringValue("");
      if (name.isEmpty()) {
        throw new LoadPluginException("Plugin has no name specified");
      }
      if (main.isEmpty()) {
        throw new LoadPluginException("Plugin has no main class specified");
      }

      URLClassLoader classLoader = new URLClassLoader(new URL[]{pluginJar.toURI().toURL()});
      Class<?> pluginClass = classLoader.loadClass(main);
      Plugin pluginInstance = (Plugin) pluginClass.newInstance();
      return new ChunkyPlugin(pluginDefiniton, pluginInstance);
    } catch (IOException e) {
      throw new LoadPluginException("Could not load the plugin", e);
    } catch (ClassCastException e) {
      throw new LoadPluginException("Main plugin class has wrong type", e);
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      throw new LoadPluginException("Could not create plugin instance", e);
    } catch (JsonParser.SyntaxError e) {
      throw new LoadPluginException("Could not parse the plugin definition file", e);
    }
  }
}
