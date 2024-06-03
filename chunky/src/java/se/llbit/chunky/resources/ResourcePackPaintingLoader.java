package se.llbit.chunky.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import se.llbit.chunky.entity.PaintingEntity;
import se.llbit.chunky.resources.texturepack.SimpleTexture;
import se.llbit.log.Log;
import se.llbit.util.Pair;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

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
    for (LayeredResourcePacks.Entry data : resourcePacks.getAllEntries("data")) {
      try (Stream<Path> namespaces = Files.list(data.getPath())) {
        namespaces.forEach(ns -> {
          String namespace = String.valueOf(ns.getFileName());
          Path paintingVariants = ns.resolve("painting_variant");
          try (Stream<Path> paintingVariantStream = Files.walk(paintingVariants)) {
            paintingVariantStream
              .filter(p -> Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS))
              .forEach(paintingVariant -> {
                if (paintingVariant.toString().endsWith(".json")) {
                  String paintingVariantName = getPaintingVariantName(paintingVariants.relativize(paintingVariant));
                  String resourceLocation = namespace + ":" + paintingVariantName;

                  if (!PaintingEntity.containsPainting(resourceLocation)) {
                    try (Reader f = Files.newBufferedReader(paintingVariant)) {
                      PaintingVariantJson json = GSON.fromJson(f, PaintingVariantJson.class);
                      Texture paintingTexture = new Texture();
                      Pair<String, String> asset = json.getAsset();
                      if (!ResourcePackLoader.loadResources(
                        ResourcePackTextureLoader.singletonLoader(json.asset_id, new SimpleTexture("assets/" + asset.thing1 + "/textures/painting/" + asset.thing2, paintingTexture)))
                      ) {
                        Log.warnf("Failed to load painting texture: %s", json.asset_id);
                      }
                      PaintingEntity.registerPainting(resourceLocation, new PaintingEntity.Painting(paintingTexture, json.width, json.height));
                    } catch (IOException ignored) {
                      Log.warnf("Failed to load painting variant: %s", paintingVariantName);
                    }
                  }
                }
              });
          } catch (IOException ignored) {
          }
        });
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return false;
  }

  private static String getPaintingVariantName(Path paintingVariant) {
    ArrayList<String> path = new ArrayList<>();
    paintingVariant.iterator().forEachRemaining(p -> path.add(String.valueOf(p)));

    String out = String.join("/", path);
    if (out.toLowerCase().endsWith(".json")) {
      out = out.substring(0, out.length() - ".json".length());
    }
    return out;
  }
}
