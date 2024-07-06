package se.llbit.chunky.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import se.llbit.chunky.entity.BannerDesign;
import se.llbit.log.Log;
import se.llbit.util.Pair;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;

public class ResourcePackBannerPatternLoader implements ResourcePackLoader.PackLoader {

  protected static final Gson GSON = new GsonBuilder()
    .disableJdkUnsafe()
    .setLenient()
    .create();

  protected static class BannerPatternJson {
    public String asset_id;

    public Pair<String, String> getAsset() {
      String[] parts = asset_id.split(":");
      return new Pair<>(parts[0], parts[1]);
    }
  }

  @Override
  public boolean load(LayeredResourcePacks resourcePacks) {
    DataPackUtil.forEachDataRegistryEntry(resourcePacks, "banner_pattern", pattern -> {
      if (!BannerDesign.containsPattern(pattern.getNamespacedName())) {
        try (Reader f = Files.newBufferedReader(pattern.path())) {
          BannerPatternJson json = GSON.fromJson(f, BannerPatternJson.class);
          BannerDesign.registerPattern(pattern.getNamespacedName(), new BannerDesign.Pattern(json.asset_id));
        } catch (IOException ignored) {
          Log.warnf("Failed to load banner pattern: %s", pattern.getNamespacedName());
        }
      }
    });
    return false;
  }
}
