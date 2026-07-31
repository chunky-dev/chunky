package se.llbit.chunky.plugin;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import se.llbit.chunky.main.Version;
import se.llbit.chunky.plugin.manifest.PluginManifest;

import java.util.Collection;
import java.util.List;

public class BuiltInPlugins {
  /**
   * The plugins built into chunky
   */
  public static final Collection<PluginManifest> PLUGINS;

  /**
   * Built-in plugins are the same version as chunky
   */
  private static final ArtifactVersion BUILT_IN_PLUGINS_VERSION = new DefaultArtifactVersion(Version.getVersion());
  /**
   * Built-in plugins target the current chunky version
   */
  private static final VersionRange BUILT_IN_PLUGINS_VERSION_RANGE = VersionRange.createFromVersion(Version.getVersion());

  static {
    PLUGINS = List.of(
    );
  }
}
