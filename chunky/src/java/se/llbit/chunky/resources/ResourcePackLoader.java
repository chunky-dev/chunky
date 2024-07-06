/*
 * Copyright (c) 2022 Chunky contributors
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
package se.llbit.chunky.resources;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.entity.BannerDesign;
import se.llbit.chunky.entity.PaintingEntity;
import se.llbit.chunky.world.biome.Biomes;
import se.llbit.log.Log;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipError;

public class ResourcePackLoader {
  public static final ArrayList<PackLoaderFactory> PACK_LOADER_FACTORIES = new ArrayList<>();

  static {
    ResourcePackLoader.PACK_LOADER_FACTORIES.add(() -> new ResourcePackTextureLoader(TexturePackLoader.ALL_TEXTURES));
    ResourcePackLoader.PACK_LOADER_FACTORIES.add(ResourcePackBiomeLoader::new);
    ResourcePackLoader.PACK_LOADER_FACTORIES.add(ResourcePackPaintingLoader::new);
    ResourcePackLoader.PACK_LOADER_FACTORIES.add(ResourcePackBannerPatternLoader::new);
  }

  public interface PackLoader {
    /**
     * Load a resource pack.
     *
     * @return True if this loader has found and loaded _all_ the things it is responsible for.
     * False if there is more to load (by a fallback resource pack).
     */
    boolean load(LayeredResourcePacks resourcePacks);

    /**
     * All resources that failed to load. Empty if all resources were loaded.
     */
    default Collection<String> notLoaded() {
      return Collections.emptyList();
    }

    default boolean hasUnloaded() {
      return !notLoaded().isEmpty();
    }
  }

  interface PackLoaderFactory {
    PackLoader create();
  }

  private static LayeredResourcePacks resourcePacks = new LayeredResourcePacks();

  /**
   * @return loaded resource packs
   */
  public static List<File> getLoadedResourcePacks() {
    return resourcePacks.getResourcePackFiles();
  }

  public static List<File> getAvailableResourcePacks() {
    List<File> resourcePacks = new ArrayList<>();
    File[] files = MinecraftFinder.getResourcePacksDirectory().listFiles();
    if (files != null)
      resourcePacks.addAll(Arrays.asList(files));
    files = MinecraftFinder.getTexturePacksDirectory().listFiles();
    if (files != null)
      resourcePacks.addAll(Arrays.asList(files));
    return resourcePacks;
  }

  /**
   * Load (only) the Minecraft default resource pack.
   */
  public static void loadDefaultResourcePack() {
    loadResourcePacks(Collections.emptyList());
  }

  /**
   * Load (only) the persisted resource packs.
   */
  public static void loadPersistedResourcePacks() {
    loadResourcePacks(PersistentSettings.getEnabledResourcePacks());
  }

  /**
   * Load the given resource pack files and save them in the
   * {@link PersistentSettings#getEnabledResourcePacks()}.
   *
   * @param resourcePacks resource pack files to load (can be non-existent, directories or zips)
   */
  public static void loadAndPersistResourcePacks(List<File> resourcePacks) {
    PersistentSettings.setEnabledResourcePacks(resourcePacks.toArray(new File[0]));
    loadResourcePacks(resourcePacks);
  }

  /**
   * Load all resources in the default set of loaders.
   * Resource pack files are loaded in list order - if a texture is not found in a pack,
   * the next packs is checked as a fallback.
   * Resources which could not be found or loaded get logged and fall back to missing texture.
   */
  public static void loadResourcePacks(List<File> resourcePacks) {
    TextureCache.reset();
    Biomes.reset();
    PaintingEntity.resetPaintings();
    BannerDesign.resetPatterns();

    if (ResourcePackLoader.resourcePacks != null) {
      try {
        ResourcePackLoader.resourcePacks.close();
      } catch (IOException e) {
        // ignore
      }
    }

    LayeredResourcePacks packs = new LayeredResourcePacks();
    for (File packFile : resourcePacks) {
      try {
        packs.addResourcePack(packFile);
      } catch (IOException e) {
        Log.warnf(
          "Failed to open %s (%s): %s",
          getResourcePackDescriptor(packFile),
          packFile.getAbsolutePath(),
          e.getMessage()
        );
      }
    }
    if (!PersistentSettings.getDisableDefaultTextures()) {
      File file = MinecraftFinder.getMinecraftJar();
      if (file != null) {
        try {
          packs.addResourcePack(file);
        } catch (IOException e) {
          Log.warn("Minecraft Jar could not be opened: falling back to placeholder textures.");
        }
      } else {
        Log.warn("Minecraft Jar not found: falling back to placeholder textures.");
      }
    }
    ResourcePackLoader.resourcePacks = packs;

    Log.infof(
      "Loading resource packs: \n%s",
      resourcePacks.stream()
        .map(File::toString)
        .map(s -> "- " + s)
        .collect(Collectors.joining("\n"))
    );

    List<PackLoader> loaders = PACK_LOADER_FACTORIES.stream()
      .map(PackLoaderFactory::create)
      .collect(Collectors.toList());

    if (!loadResources(loaders)) {
      Log.info(buildMissingResourcesErrorMessage(loaders));
    }
  }

  /**
   * Load specific resources from the currently used set of resource packs.
   *
   * @return True if all resources have been found and loaded.
   */
  public static boolean loadResources(PackLoader... loaders) {
    return loadResources(Arrays.asList(loaders));
  }

  /**
   * Load resources from all resource packs.
   *
   * @return True if all resources have been found in the packs and no fallback is required.
   */
  private static boolean loadResources(List<PackLoader> loaders) {
    boolean complete = true;
    for (PackLoader loader : loaders) {
      if (!loader.load(resourcePacks)) {
        complete = false;
      }
    }
    return complete;
  }

  public static String getResourcePackDescriptor(File pack) {
    return pack.equals(MinecraftFinder.getMinecraftJar()) ? "default resource pack" : "resource pack";
  }

  /**
   * FileSystem abstraction over pack files which can load files from either ZIP files or directories.
   */
  public static FileSystem getPackFileSystem(File pack) throws IOException {
    try {
      return pack.isDirectory()
        // for resource packs in directories
        ? FileSystems.getDefault()
        // for resource packs in jar or zip files
        : FileSystems.newFileSystem(URI.create("jar:" + pack.toURI()), Collections.emptyMap());
    } catch (ZipError e) {
      // This catch is required for Java 8. This error appears safe to catch.
      // https://stackoverflow.com/a/51715939
      throw new IOException(e);
    } catch (FileSystemAlreadyExistsException e) {
      // for resource packs in jar or zip files (re-use existing fs)
      return FileSystems.getFileSystem(URI.create("jar:" + pack.toURI()));
    }
  }

  /**
   * Get the relative pack root path for directory or jar/zip file root.
   */
  public static Path getPackRootPath(File pack, FileSystem fileSystem) {
    return pack.isDirectory()
      ? pack.toPath()
      : fileSystem.getRootDirectories().iterator().next();
  }

  private static int countMissingResources(List<PackLoader> loaders) {
    return loaders.stream()
      .map(PackLoader::notLoaded)
      .mapToInt(Collection::size)
      .sum();
  }

  private static String buildMissingResourcesErrorMessage(List<PackLoader> loaders) {
    int notLoadedCount = countMissingResources(loaders);

    String notLoadedOverview = loaders.stream()
      .filter(PackLoader::hasUnloaded)
      .flatMap(loader -> loader.notLoaded().stream())
      .limit(10)
      .collect(Collectors.joining(
        ", ",
        "",
        (notLoadedCount > 10) ? ", ..." : "")
      );

    return String.format("Failed to load %d resources: %s", notLoadedCount, notLoadedOverview);
  }
}
