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
  private final PluginMeta meta;
  private final Plugin implementation;

  protected ChunkyPlugin(PluginMeta meta, Plugin implementation) {
    this.meta = meta;
    this.implementation = implementation;
  }

  /**
   * Gets meta information of this plugin.
   *
   * @return meta information of this plugin
   */
  public PluginMeta getMeta() {
    return meta;
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
    try {
      Map<String, String> env = new HashMap<>();
      env.put("create", "true");

      PluginMeta meta;
      try (FileSystem zipFs = FileSystems.newFileSystem(URI.create("jar:file:" + pluginJar.getAbsolutePath()), env);
           InputStream in = zipFs.getPath("/plugin.json").toUri().toURL().openStream()) {
        meta = PluginMeta.loadFromJson(in);
      }

      URLClassLoader classLoader = new URLClassLoader(new URL[]{pluginJar.toURI().toURL()});
      Class<?> pluginClass = classLoader.loadClass(meta.getPluginClassName());
      Plugin pluginInstance = (Plugin) pluginClass.newInstance();
      return new ChunkyPlugin(meta, pluginInstance);
    } catch (IOException e) {
      throw new LoadPluginException("Could not load the plugin", e);
    } catch (ClassCastException e) {
      throw new LoadPluginException("Main plugin class has wrong type", e);
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      throw new LoadPluginException("Could not create plugin instance", e);
    }
  }
}
