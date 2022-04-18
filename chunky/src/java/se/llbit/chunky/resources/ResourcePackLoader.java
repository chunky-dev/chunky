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
import se.llbit.log.Log;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;

public class ResourcePackLoader {
    public static final ArrayList<PackLoaderFactory> PACK_LOADER_FACTORIES = new ArrayList<>();

    static {
        ResourcePackLoader.PACK_LOADER_FACTORIES.add(() -> new ResourcePackTextureLoader(TexturePackLoader.ALL_TEXTURES));
        ResourcePackLoader.PACK_LOADER_FACTORIES.add(ResourcePackBiomeLoader::new);
    }

    private static String[] resourcePacks;

    public interface PackLoader {
        /**
         * Load a resource pack.
         * @return True if this loader is done loading.
         */
        boolean load(Path pack, String baseName);

        /**
         * Get any resources that failed to load. Null if all resources were loaded.
         */
        default String[] toLoad() {
            return null;
        }
    }

    interface PackLoaderFactory {
        PackLoader create();
    }

    private static String resourcePackName(File pack) {
        boolean isDefault = pack.equals(MinecraftFinder.getMinecraftJar());
        return String.format("%s (%s)",
                isDefault ? "default resource pack" : "resource pack",
                pack.getAbsolutePath());
    }

    /**
     * Load specific resources from the last used set of resource packs.
     */
    public static void loadResources(PackLoader[] loaders) {
        loadResourcePacks(getResourcePacks().toArray(new String[0]), loaders);
    }

    /**
     * Load resources from a single resource pack.
     *
     * @return True if this is done loading
     */
    public static boolean loadResourcePack(File pack, PackLoader[] loaders) {
        FileSystem resourcePack = null;
        try {
            Path root;
            if (pack.isDirectory()) {
                // Raw directory
                resourcePack = FileSystems.getDefault();
                root = pack.toPath();
            } else {
                // Jar or Zip file
                resourcePack = FileSystems.newFileSystem(URI.create("jar:" + pack.toURI()), Collections.emptyMap());
                root = resourcePack.getPath("/");
            }

            boolean complete = true;
            for (PackLoader loader : loaders) {
                complete &= loader.load(root, pack.getName());
            }
            return complete;
        } catch (IOException e) {
            Log.warnf("Failed to open %s: %s", resourcePackName(pack), e.getMessage());
        } finally {
            if (resourcePack != null) {
                try {
                    resourcePack.close();
                } catch (UnsupportedOperationException | IOException ignore) {}
            }
        }
        return false;
    }

    /**
     * Load all resources in the default set of loaders.
     */
    public static void loadResourcePacks(String[] resourcePacks) {
        // Save the last used set of resource packs
        TextureCache.reset();
        ResourcePackLoader.resourcePacks = resourcePacks;

        // Create the loaders
        PackLoader[] loaders = PACK_LOADER_FACTORIES
                .stream()
                .map(PackLoaderFactory::create)
                .toArray(PackLoader[]::new);

        loadResourcePacks(resourcePacks, loaders);
    }

    private static void loadResourcePacks(String[] resourcePacks, PackLoader[] loaders) {
        boolean complete = false;
        for (String path : resourcePacks) {
            if (!path.isEmpty()) {
                File file = new File(path);
                if (!file.isFile() && !file.isDirectory()) {
                    Log.errorf("Could not open texture pack: %s", file.getAbsolutePath());
                } else {
                    Log.infof("Loading resources from %s", file.getAbsolutePath());
                    if (loadResourcePack(file, loaders)) {
                        complete = true;
                        break;
                    }
                }
            }
        }

        if (!complete && !PersistentSettings.getDisableDefaultTextures()) {
            File file = MinecraftFinder.getMinecraftJar();
            if (file != null) {
                Log.infof("Loading resources from %s", file.getAbsolutePath());
                complete = loadResourcePack(file, loaders);
            } else {
                Log.error("Minecraft Jar not found: falling back on placeholder textures.");
            }
        }

        if (!complete) {
            // TODO more detailed logging
            Log.infof("Failed to load %d resources.",
                    Arrays.stream(loaders)
                            .map(PackLoader::toLoad)
                            .filter(Objects::nonNull)
                            .mapToInt(r -> r.length)
                            .sum()
            );
        }
    }

    /**
     * Load resource packs from a system path separator delimited string.
     */
    public static void loadResourcePacks(String resourcePacks) {
        if (!resourcePacks.isEmpty()) {
            loadResourcePacks(resourcePacks.trim().split(File.pathSeparator));
        }
    }

    /**
     * Remember these resource packs into the persistent settings.
     */
    public static void rememberResourcePacks(String[] resourcePacks) {
        StringBuilder paths = new StringBuilder();
        for (String path : resourcePacks) {
            if (paths.length() > 0) {
                paths.append(File.pathSeparator);
            }
            paths.append(path);
        }
        PersistentSettings.setLastTexturePack(paths.toString());
    }

    /**
     * Get the set of last used resource packs.
     */
    public static List<String> getResourcePacks() {
        return Collections.unmodifiableList(Arrays.asList(resourcePacks));
    }

    @Deprecated  // Remove in 2.6
    public static void setResourcePacks(String[] resourcePacks) {
        ResourcePackLoader.resourcePacks = resourcePacks;
    }
}
