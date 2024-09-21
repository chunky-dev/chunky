package se.llbit.chunky.plugin.loader;

import se.llbit.chunky.Plugin;
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.plugin.manifest.PluginManifest;

import java.util.function.BiConsumer;

@PluginApi
public interface PluginLoader {
  /**
   * Load the plugin specified in the manifest
   * @param onLoad The consumer to call with the loaded plugin
   * @param pluginManifest The plugin to load.
   */
  @PluginApi
  void load(BiConsumer<Plugin, PluginManifest> onLoad, PluginManifest pluginManifest);
}
