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
package se.llbit.chunky.plugin.loader;

import se.llbit.chunky.Plugin;
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.plugin.manifest.PluginDependency;
import se.llbit.chunky.plugin.manifest.PluginManifest;
import se.llbit.json.JsonParser;
import se.llbit.log.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Chunky's default plugin loader.
 */
@PluginApi
public class ChunkyPluginLoader implements PluginLoader {
  private static final int MAX_CYCLES = 100;

  @Override
  public void load(Set<PluginManifest> pluginManifests, BiConsumer<Plugin, PluginManifest> onLoad) {
    // create plugin objects
    Map<String, List<ResolvedPlugin>> pluginsByName = new HashMap<>();
    pluginManifests.forEach(manifest -> {
      pluginsByName.computeIfAbsent(manifest.name, n -> new ArrayList<>()).add(
        new ResolvedPlugin(manifest)
      );
    });

    // check for duplicate plugins
    pluginsByName.forEach((name, plugins) -> {
      if (plugins.size() > 1) {
        // we report the error and hope it goes ok
        Log.errorf("There are %d plugins with the name %s, this is extremely likely to break, proceeding anyway.", plugins.size(), name);
      }
    });

    // resolve dependencies of every plugin.
    Set<ResolvedPlugin> pluginsToLoad = pluginsByName.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    pluginsToLoad.forEach(plugin -> plugin.resolveDependencies(pluginsByName));

    // load plugins in dependency-first order, cyclic dependencies will never be loaded and will hit MAX_CYCLES cap.
    Set<ResolvedPlugin> loadedPlugins = new HashSet<>();
    int loadCycles = 0;
    while (!pluginsToLoad.isEmpty() && loadCycles < MAX_CYCLES) {
      Log.infof("Cycle %d", loadCycles);
      loadCycles++;
      // new list to avoid CME due to removing inside iteration.
      new ArrayList<>(pluginsToLoad).forEach(plugin -> {
        if (plugin.allDependenciesLoaded(loadedPlugins)) {
          loadedPlugins.add(plugin);
          pluginsToLoad.remove(plugin);
          Log.infof("  Loading plugin %s with deps { %s }, resolved { %s }%n", plugin,
            plugin.getManifest().getDependencies().stream().map(PluginDependency::toString).collect(Collectors.joining(", ")),
            plugin.getDependencies().stream().map(ResolvedPlugin::toString).collect(Collectors.joining(", "))
          );
          loadPlugin(onLoad, plugin.getManifest());
        }
      });
    }

    // report if any unloaded plugins remain (their dependencies never got loaded)
    if (!pluginsToLoad.isEmpty()) {
      Log.errorf(
        "Reached maximum cycles (%d) when loading plugins. Failed to load plugins: (%s)",
        MAX_CYCLES,
        pluginsToLoad.stream().map(ResolvedPlugin::toString).collect(Collectors.joining(", "))
      );
    }
  }

  /**
   * Load the plugin specified in the manifest
   * @param onLoad The consumer to call with the loaded plugin
   * @param pluginManifest The plugin to load.
   */
  private void loadPlugin(BiConsumer<Plugin, PluginManifest> onLoad, PluginManifest pluginManifest) {
    try {
      Class<?> pluginClass = loadPluginClass(pluginManifest.main, pluginManifest.pluginJar);
      Plugin plugin = (Plugin) pluginClass.getDeclaredConstructor().newInstance();
      onLoad.accept(plugin, pluginManifest);
    } catch (IOException e) {
      Log.error("Could not load the plugin", e);
    } catch (ClassCastException e) {
      Log.error("Plugin main class has wrong type", e);
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
      Log.error("Could not create plugin instance", e);
    }
  }

  /**
   * This method is {@link PluginApi} to allow plugins to override only classloading functionality of the plugin loader.
   *
   * @param pluginMainClass The plugin's main class to load.
   * @param pluginJarFile The jar file to load classes from.
   * @return The loaded plugin's main class
   * @throws ClassNotFoundException If the main class doesn't exist
   * @throws MalformedURLException If the jar file cannot be converted to a URL
   */
  @PluginApi
  protected Class<?> loadPluginClass(String pluginMainClass, File pluginJarFile) throws ClassNotFoundException, MalformedURLException {
    return new URLClassLoader(new URL[] { pluginJarFile.toURI().toURL() }).loadClass(pluginMainClass);
  }

  /**
   * Parse and create a plugin manifest from the specified jar file.
   * @param pluginJar The jar to find the manifest file in.
   * @return The {@link PluginManifest} if it could be created.
   */
  public static Optional<PluginManifest> parsePluginManifest(File pluginJar) {
    try (FileSystem zipFs = FileSystems.newFileSystem(URI.create("jar:" + pluginJar.toURI()), Collections.emptyMap())) {
      Path manifestPath = zipFs.getPath("/plugin.json");
      if (!Files.exists(manifestPath)) {
        Log.errorf("Missing plugin manifest file (plugin.json) in plugin %s", pluginJar.getName());
        return Optional.empty();
      }
      try (InputStream in = Files.newInputStream(manifestPath); JsonParser parser = new JsonParser(in)) {
        return PluginManifest.parse(parser.parse().object(), pluginJar);
      } catch (JsonParser.SyntaxError e) {
        Log.error("Could not parse the plugin manifest file", e);
      }
    } catch (IOException e) {
      Log.error("Could not load the plugin", e);
    }
    return Optional.empty();
  }
}
