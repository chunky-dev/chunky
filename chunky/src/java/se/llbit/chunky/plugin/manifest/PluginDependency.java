package se.llbit.chunky.plugin.manifest;

import org.apache.maven.artifact.versioning.VersionRange;

/**
 * An unresolved plugin dependency within a plugin manifest.
 */
public class PluginDependency {
  public final String name;
  public final VersionRange version;

  public PluginDependency(String name, VersionRange version) {
    this.name = name;
    this.version = version;
  }

  @Override
  public String toString() {
    return name + ":" + version;
  }
}
