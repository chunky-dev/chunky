package se.llbit.chunky.plugin.loader;

import se.llbit.chunky.Plugin;
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.plugin.manifest.PluginManifest;

import java.util.Set;
import java.util.function.BiConsumer;

@PluginApi
public interface PluginLoader {
  /**
   * Loads a set of plugins given their manifests.
   * On successfully loading each plugin class, the plugin instance and its manifest are sent to the onLoad consumer.
   *
   * @param pluginManifests the plugins to load.
   * @param onLoad a function object that the plugin instance and manifest
   * are sent to after it successfully loads.
   */
  @PluginApi
  void load(Set<PluginManifest> pluginManifests, BiConsumer<Plugin, PluginManifest> onLoad);
}
