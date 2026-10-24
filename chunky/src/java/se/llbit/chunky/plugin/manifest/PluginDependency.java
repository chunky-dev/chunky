package se.llbit.chunky.plugin.manifest;

import org.apache.maven.artifact.versioning.VersionRange;
import se.llbit.util.annotation.NotNull;

/**
 * An unresolved plugin dependency within a plugin manifest.
 */
public record PluginDependency(String name, VersionRange version) {
  @Override @NotNull
  public String toString() {
    return name + ":" + version;
  }
}
