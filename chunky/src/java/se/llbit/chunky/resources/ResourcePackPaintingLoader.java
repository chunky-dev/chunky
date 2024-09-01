package se.llbit.chunky.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import se.llbit.chunky.entity.PaintingEntity;
import se.llbit.chunky.resources.texture.BitmapTexture;
import se.llbit.chunky.resources.texturepack.SimpleTexture;
import se.llbit.log.Log;
import se.llbit.util.Pair;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;

public class ResourcePackPaintingLoader implements ResourcePackLoader.PackLoader {

  protected static final Gson GSON = new GsonBuilder()
    .disableJdkUnsafe()
    .setLenient()
    .create();

  protected static class PaintingVariantJson {
    public String asset_id;
    public int width;
    public int height;

    public Pair<String, String> getAsset() {
      String[] parts = asset_id.split(":");
      return new Pair<>(parts[0], parts[1]);
    }
  }

  @Override
  public boolean load(LayeredResourcePacks resourcePacks) {
    DataPackUtil.forEachDataRegistryEntry(resourcePacks, "painting_variant", paintingVariant -> {
      if (!PaintingEntity.containsPainting(paintingVariant.getNamespacedName())) {
        try (Reader f = Files.newBufferedReader(paintingVariant.path())) {
          PaintingVariantJson json = GSON.fromJson(f, PaintingVariantJson.class);
          BitmapTexture paintingTexture = new BitmapTexture();
          Pair<String, String> asset = json.getAsset();
          if (!ResourcePackLoader.loadResources(
            ResourcePackTextureLoader.singletonLoader(json.asset_id, new SimpleTexture("assets/" + asset.thing1 + "/textures/painting/" + asset.thing2, paintingTexture)))
          ) {
            Log.warnf("Failed to load painting texture: %s", json.asset_id);
          }
          PaintingEntity.registerPainting(paintingVariant.getNamespacedName(), new PaintingEntity.Painting(paintingTexture, json.width, json.height));
        } catch (IOException ignored) {
          Log.warnf("Failed to load painting variant: %s", paintingVariant.getNamespacedName());
        }
      }
    });
    return false;
  }

  @Override
  public void resetLoadedResources() {
    PaintingEntity.resetPaintings();
  }
}
