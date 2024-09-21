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
import se.llbit.chunky.plugin.manifest.PluginDependency;
import se.llbit.chunky.plugin.manifest.PluginManifest;
import se.llbit.json.JsonParser;
import se.llbit.log.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class PluginManager {
  private static final int MAX_CYCLES = Integer.parseInt(System.getProperty("chunky.plugins.maxLoadCycles", "100"));

  private final PluginLoader pluginLoader;

  public PluginManager(PluginLoader pluginLoader) {
    this.pluginLoader = pluginLoader;
  }

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
    // this was so trivial to implement using cycles that I decided against any kind of dependency tree structure,
    // in the worst case this approach requires one cycle per plugin (if every plugin depended on the previous one in the list).
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
          pluginLoader.load(onLoad, plugin.getManifest());
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
