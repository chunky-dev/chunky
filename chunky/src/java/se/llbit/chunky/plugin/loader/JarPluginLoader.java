package se.llbit.chunky.plugin.loader;

import se.llbit.chunky.Plugin;
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.plugin.manifest.PluginManifest;
import se.llbit.log.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.function.BiConsumer;

@PluginApi
public class JarPluginLoader implements PluginLoader {
  public void load(BiConsumer<Plugin, PluginManifest> onLoad, PluginManifest pluginManifest) {
    try {
      Class<?> pluginClass = loadPluginClass(pluginManifest.main, pluginManifest.pluginJar);
      Plugin plugin = (Plugin) pluginClass.getDeclaredConstructor().newInstance();
      onLoad.accept(plugin, pluginManifest);
    } catch (IOException | ClassNotFoundException e) {
      Log.error("Could not load the plugin", e);
    } catch (ClassCastException e) {
      Log.error("Plugin main class has wrong type (must implement se.llbit.chunky.Plugin)", e);
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      Log.error("Could not create plugin instance", e);
    }
  }

  /**
   * This method is {@link PluginApi} to allow plugins to override only classloading functionality of the default plugin loader.
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
}
