package se.llbit.chunky.plugin.manifest;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import se.llbit.chunky.main.Version;
import se.llbit.json.JsonMember;
import se.llbit.json.JsonObject;
import se.llbit.log.Log;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * An immutable class representing the manifest file (plugin.json) of a plugin.
 */
public class PluginManifest {
  public final File pluginJar;
  public final String name;
  public final String author;
  public final String description;
  public final ArtifactVersion version;
  public final VersionRange targetVersion;
  public final String main;
  private final Set<PluginDependency> dependencies;

  public PluginManifest(File pluginJar, String name, String author, String description, ArtifactVersion version,
                        VersionRange targetVersion, String main, Set<PluginDependency> dependencies) {
    this.pluginJar = pluginJar;
    this.name = name;
    this.author = author;
    this.description = description;
    this.version = version;
    this.targetVersion = targetVersion;
    this.main = main;
    this.dependencies = dependencies;
  }

  /**
   * Parse a json object into a PluginManifest if possible
   * @param manifest The manifest json data
   * @param pluginJar The plugin jar to associate with the manifest
   * @return The PluginManifest if it could be created.
   */
  public static Optional<PluginManifest> parse(JsonObject manifest, File pluginJar) {
    String name = manifest.get("name").stringValue("");
    String author = manifest.get("author").stringValue("");
    String description = manifest.get("description").stringValue("");
    String version = manifest.get("version").stringValue("");
    String targetVersion = manifest.get("targetVersion").stringValue("");
    String main = manifest.get("main").stringValue("");

    if (name.isEmpty()) {
      Log.errorf("Plugin %s has no name specified", pluginJar.getName());
      return Optional.empty();
    }
    if (version.isEmpty()) {
      Log.errorf("Plugin %s has no version specified", name);
      return Optional.empty();
    }
    if (main.isEmpty()) {
      Log.errorf("Plugin %s has no main class specified", name);
      return Optional.empty();
    }
    if (targetVersion.isEmpty()) {
      Log.errorf("Plugin %s has no targetVersion specified", name);
      return Optional.empty();
    }

    if (!targetVersion.equalsIgnoreCase(Version.getVersion())) {
      Log.warnf("The plugin %s was developed for Chunky %s but this is Chunky %s "
          + "- it may not work properly.",
        name, targetVersion, Version.getVersion());
    }

    Set<PluginDependency> dependencies = new HashSet<>();
    for (JsonMember dependency : manifest.get("dependencies").asObject()) {
      String dependencyVersion = dependency.getValue().stringValue("");
      if (dependency.name.isEmpty() || dependencyVersion.isEmpty()) {
        Log.errorf("Plugin %s has an invalid dependency specified %s: %s.", name, dependency.name, dependency.value);
        return Optional.empty();
      }
      try {
        dependencies.add(new PluginDependency(dependency.name, VersionRange.createFromVersionSpec(dependencyVersion)));
      } catch (InvalidVersionSpecificationException exception) {
        Log.error(
          String.format("Could not parse plugin %s dependency version range %s.", name, dependencyVersion),
          exception
        );
        return Optional.empty();
      }
    }

      VersionRange targetVersionRange;
      try {
          targetVersionRange = VersionRange.createFromVersionSpec(targetVersion);
      } catch (InvalidVersionSpecificationException e) {
          Log.error(String.format("Failed to parse plugin %s targetVersion %s into version range.", name, targetVersion), e);
          targetVersionRange = VersionRange.createFromVersion(targetVersion);
      }
      return Optional.of(new PluginManifest(pluginJar, name, author, description, new DefaultArtifactVersion(version), targetVersionRange, main, dependencies));
  }

  public Set<PluginDependency> getDependencies() {
    return dependencies;
  }
}
