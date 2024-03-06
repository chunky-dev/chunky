package se.llbit.chunky.plugin.loader;

import se.llbit.chunky.plugin.manifest.PluginManifest;
import se.llbit.log.Log;

import java.util.*;

public class ResolvedPlugin {
  private final PluginManifest manifest;
  private final Set<ResolvedPlugin> dependencies = new HashSet<>();

  public ResolvedPlugin(PluginManifest manifest) {
    this.manifest = manifest;
  }

  /**
   * Resolve dependencies specified in the {@link #manifest} into known plugins.
   * @param pluginsByName All plugins to be loaded
   */
  public void resolveDependencies(Map<String, List<ResolvedPlugin>> pluginsByName) {
    // Adds any plugin as a dependency if it matches a dependency's name and version.
    this.manifest.getDependencies().forEach(unresolvedDep -> {
      List<ResolvedPlugin> resolvedDeps = pluginsByName.get(unresolvedDep.name);
      boolean resolved = false;
      for (ResolvedPlugin resolvedDep : resolvedDeps) {
        if (unresolvedDep.version.containsVersion(resolvedDep.manifest.version)) {
          this.dependencies.add(resolvedDep);
          resolved = true;
        }
      }
      if (!resolved) {
        Log.errorf("Could not find required dependency %s for plugin %s.", unresolvedDep, this.manifest.name);
      }
    });
  }

  /**
   * @param loadedPlugins The set of plugins that are currently loaded.
   * @return Whether the provided set contains all dependencies of this plugin.
   */
  public boolean allDependenciesLoaded(Set<ResolvedPlugin> loadedPlugins) {
    return loadedPlugins.containsAll(this.dependencies);
  }

  public PluginManifest getManifest() {
    return this.manifest;
  }

  public Set<ResolvedPlugin> getDependencies() {
    return dependencies;
  }

  @Override
  public String toString() {
    return this.manifest.name + ":" + this.manifest.version;
  }
}
