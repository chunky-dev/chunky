package se.llbit.chunky.plugin;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.junit.jupiter.api.Test;
import se.llbit.chunky.main.Version;
import se.llbit.chunky.plugin.loader.PluginManager;
import se.llbit.chunky.plugin.manifest.PluginDependency;
import se.llbit.chunky.plugin.manifest.PluginManifest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PluginManagerTest {
  private static final VersionRange chunkyVersion = VersionRange.createFromVersion(Version.getVersion());

  @Test
  public void testSimpleChainOfDependencies() throws InvalidVersionSpecificationException {
    // Dependencies look like 10 > 9 > 8 > 7 > 6 > 5 > 4 > 3 > 2 > 1
    List<String> expectedLoadOrder = List.of("test10", "test9", "test8", "test7", "test6", "test5", "test4", "test3", "test2", "test1");

    // create 10 dependencies, each depending on the previous one
    HashSet<PluginManifest> manifests = new HashSet<>();
    for (int i = 1; i < 10; i++) {
      manifests.add(new PluginManifest(null, "test" + i, "author" + i, "desc" + i,
        new DefaultArtifactVersion(i + ".0"), chunkyVersion, "test1.Main",
        Set.of(
          new PluginDependency("test" + (i + 1), VersionRange.createFromVersionSpec("[" + (i + 1) + ".0]"))
        )
      ));
    }
    manifests.add(new PluginManifest(null, "test10", "author10", "desc10",
      new DefaultArtifactVersion("10.0"), chunkyVersion, "test10.Main", Collections.emptySet()
    ));

    Set<String> expectedLoadedPlugins = Set.of("test1", "test2", "test3", "test4", "test5", "test6", "test7", "test8", "test9", "test10");
    assertLoadOrder(manifests, expectedLoadedPlugins);
  }

  @Test
  public void testLoopOfDependencies() throws InvalidVersionSpecificationException {
    // Dependencies look like 10 > 9 > 8 > 7 > 6 > 5 > 4 > 3 > 2 > 1
    //                         ^                                   │
    //                         └───────────────────────────────────┘
    // We expect to load no plugins, as they are all in a loop.
    List<String> expectedLoadOrder = List.of();

    // create 10 dependencies, each depending on the previous one
    HashSet<PluginManifest> manifests = new HashSet<>();
    for (int i = 1; i < 10; i++) {
      manifests.add(new PluginManifest(null, "test" + i, "author" + i, "desc" + i,
        new DefaultArtifactVersion(i + ".0"), chunkyVersion, "test1.Main",
        Set.of(
          new PluginDependency("test" + (i + 1), VersionRange.createFromVersionSpec("[" + (i + 1) + ".0]"))
        )
      ));
    }
    // The last one depends on the first. A loop!
    manifests.add(new PluginManifest(null, "test10", "author10", "desc10",
      new DefaultArtifactVersion("10.0"), chunkyVersion, "test10.Main",
      Set.of(
        new PluginDependency("test1", VersionRange.createFromVersionSpec("[1.0]"))
      )
    ));

    // We expect to load no plugins, as they are all in a loop.
    Set<String> expectedLoadedPlugins = Set.of();
    assertLoadOrder(manifests, expectedLoadedPlugins);
  }

  @Test
  public void testNormalCase() throws InvalidVersionSpecificationException {
    /*
     Dependencies look like:
         1 > 9 > 8 > 5
         v   v       v
         6 > 7 ────> 4
         v   v       │
         3 > 2 > 10 <┘
    */
    Set<PluginManifest> manifests = new HashSet<>();
    manifests.add(new PluginManifest(null, "test10", "author10", "desc10",
      new DefaultArtifactVersion("10.0"), chunkyVersion, "test10.Main",
      Collections.emptySet()
    ));
    manifests.add(new PluginManifest(null, "test9", "author9", "desc9",
      new DefaultArtifactVersion("9.0"), chunkyVersion, "test9.Main",
      Set.of(
        new PluginDependency("test8", VersionRange.createFromVersionSpec("[8.0]")),
        new PluginDependency("test7", VersionRange.createFromVersionSpec("[7.0]"))
      )
    ));
    manifests.add(new PluginManifest(null, "test8", "author8", "desc8",
      new DefaultArtifactVersion("8.0"), chunkyVersion, "test8.Main",
      Set.of(
        new PluginDependency("test5", VersionRange.createFromVersionSpec("[5.0]"))
      )
    ));
    manifests.add(new PluginManifest(null, "test7", "author7", "desc7",
      new DefaultArtifactVersion("7.0"), chunkyVersion, "test7.Main",
      Set.of(
        new PluginDependency("test4", VersionRange.createFromVersionSpec("[4.0]")),
        new PluginDependency("test2", VersionRange.createFromVersionSpec("[2.0]"))
      )
    ));
    manifests.add(new PluginManifest(null, "test6", "author6", "desc6",
      new DefaultArtifactVersion("6.0"), chunkyVersion, "test6.Main",
      Set.of(
        new PluginDependency("test7", VersionRange.createFromVersionSpec("[7.0]")),
        new PluginDependency("test3", VersionRange.createFromVersionSpec("[3.0]"))
      )
    ));
    manifests.add(new PluginManifest(null, "test5", "author5", "desc5",
      new DefaultArtifactVersion("5.0"), chunkyVersion, "test5.Main",
      Set.of(
        new PluginDependency("test4", VersionRange.createFromVersionSpec("[4.0]"))
      )
    ));
    manifests.add(new PluginManifest(null, "test4", "author4", "desc4",
      new DefaultArtifactVersion("4.0"), chunkyVersion, "test4.Main",
      Set.of(
        new PluginDependency("test10", VersionRange.createFromVersionSpec("[10.0]"))
      )
    ));
    manifests.add(new PluginManifest(null, "test3", "author3", "desc3",
      new DefaultArtifactVersion("3.0"), chunkyVersion, "test3.Main",
      Set.of(
        new PluginDependency("test2", VersionRange.createFromVersionSpec("[2.0]"))
      )
    ));
    manifests.add(new PluginManifest(null, "test2", "author2", "desc2",
      new DefaultArtifactVersion("2.0"), chunkyVersion, "test2.Main",
      Set.of(
        new PluginDependency("test10", VersionRange.createFromVersionSpec("[10.0]"))
      )
    ));
    manifests.add(new PluginManifest(null, "test1", "author1", "desc1",
      new DefaultArtifactVersion("1.0"), chunkyVersion, "test1.Main",
      Set.of(
        new PluginDependency("test9", VersionRange.createFromVersionSpec("[9.0]")),
        new PluginDependency("test6", VersionRange.createFromVersionSpec("[6.0]"))
      )
    ));

    Set<String> expectedLoadedPlugins = Set.of("test1", "test2", "test3", "test4", "test5", "test6", "test7", "test8", "test9", "test10");
    assertLoadOrder(manifests, expectedLoadedPlugins);
  }

  /**
   * Asserts that every expectedPlugin is loaded only once all of its dependencies are also loaded
   * @param manifests The plugin manifests to parse and load
   * @param expectedPlugins Subset of manifests which are expected to load (Don't have dependency cycles, etc.).
   *                        This is used as an additional sanity check in addition to the load order testing.
   */
  private static void assertLoadOrder(Set<PluginManifest> manifests, Set<String> expectedPlugins) {
    Set<String> loadedPlugins = new HashSet<>();
    PluginManager pluginLoader = new PluginManager((onLoad, pluginManifest) -> {
      System.out.println("Loaded " + pluginManifest.name + ":\n\tWith dependencies: " + String.join(", ", pluginManifest.getDependencies().stream().map(p -> p.name).toList()));

      assertTrue(pluginManifest.getDependencies().stream().map(p -> p.name).allMatch(loadedPlugins::contains), () -> "Plugin " + pluginManifest.name + " does not have all dependencies loaded");
      loadedPlugins.add(pluginManifest.name);
    });

    pluginLoader.load(manifests, (plugin, manifest) -> {});
    assertEquals(expectedPlugins, loadedPlugins);
  }
}
