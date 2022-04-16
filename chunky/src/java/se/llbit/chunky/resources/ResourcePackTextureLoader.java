package se.llbit.chunky.resources;

import se.llbit.chunky.resources.texturepack.TextureLoader;
import se.llbit.resources.ImageLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResourcePackTextureLoader implements ResourcePackLoader.PackLoader {
    private final HashMap<String, TextureLoader> texturesToLoad;

    /**
     * Create a texture loader.
     * @param textures Textures to load. This map will be duplicated and will not be modified.
     */
    public ResourcePackTextureLoader(Map<String, TextureLoader> textures) {
        texturesToLoad = new HashMap<>(textures);
    }

    /**
     * Create a texture loader for a single texture.
     */
    public static ResourcePackTextureLoader singletonLoader(String textureId, TextureLoader loader) {
        return new ResourcePackTextureLoader(Collections.singletonMap(textureId, loader));
    }

    @Override
    public boolean load(Path pack, String baseName) {
        if (texturesToLoad.isEmpty()) {
            return true;
        }

        Path root = null;
        if (Files.exists(pack.resolve("assets"))) {
            root = pack;
        } else if (baseName.toLowerCase().endsWith(".zip")) {
            // The assets directory can be inside a top-level directory with
            // the same name as the resource pack zip file.
            baseName = baseName.substring(0, baseName.length()-4);
            if (Files.exists(pack.resolve(baseName).resolve("assets"))) {
                root = pack.resolve(baseName);
            }
        }
        if (root == null) {
            return false;
        }

        // Keep track of which textures have been loaded and may be removed
        ArrayList<String> toRemove = new ArrayList<>();

        for (Map.Entry<String, TextureLoader> texture : texturesToLoad.entrySet()) {
            if (texture.getValue().load(pack)) {
                toRemove.add(texture.getKey());
            }
        }
        loadTerrainTextures(root, toRemove);

        // Remove all textures which have been loaded
        toRemove.forEach(texturesToLoad::remove);

        return texturesToLoad.isEmpty();
    }

    @Override
    public String[] toLoad() {
        return texturesToLoad.keySet().toArray(new String[0]);
    }

    private void loadTerrainTextures(Path root, ArrayList<String> toRemove) {
        try (InputStream in = Files.newInputStream(root.resolve("terrain.png"))) {
            BitmapImage spriteMap = ImageLoader.read(in);
            BitmapImage[] terrainTextures = getTerrainTextures(spriteMap);

            for (Map.Entry<String, TextureLoader> texture : texturesToLoad.entrySet()) {
                if (texture.getValue().loadFromTerrain(terrainTextures)) {
                    toRemove.add(texture.getKey());
                }
            }
        } catch (IOException e) {
            // Failed to load terrain textures - this is handled implicitly.
        }
    }

    /**
     * Load a 16x16 spritemap.
     *
     * @return A bufferedImage containing the spritemap
     * @throws IOException if the image dimensions are incorrect
     */
    private static BitmapImage[] getTerrainTextures(BitmapImage spritemap) throws IOException {
        if (spritemap.width != spritemap.height || spritemap.width % 16 != 0) {
            throw new IOException(
                    "Error: terrain.png file must have equal width and height, divisible by 16!");
        }

        int imgW = spritemap.width;
        int spriteW = imgW / 16;
        BitmapImage[] tex = new BitmapImage[256];

        for (int i = 0; i < 256; ++i) {
            tex[i] = new BitmapImage(spriteW, spriteW);
        }

        for (int y = 0; y < imgW; ++y) {
            int sy = y / spriteW;
            for (int x = 0; x < imgW; ++x) {
                int sx = x / spriteW;
                BitmapImage texture = tex[sx + sy * 16];
                texture.setPixel(x % spriteW, y % spriteW, spritemap.getPixel(x, y));
            }
        }
        return tex;
    }
}
