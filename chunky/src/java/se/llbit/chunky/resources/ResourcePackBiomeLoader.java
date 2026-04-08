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

import com.google.gson.*;
import se.llbit.chunky.world.biome.Biome;
import se.llbit.chunky.world.biome.BiomeBuilder;
import se.llbit.chunky.world.biome.Biomes;
import se.llbit.log.Log;
import se.llbit.math.ColorUtil;
import se.llbit.math.Vector3;

import java.io.Reader;
import java.nio.file.Files;
import java.util.Optional;

public class ResourcePackBiomeLoader implements ResourcePackLoader.PackLoader {
  public ResourcePackBiomeLoader() {
  }

  protected static final Gson GSON = new GsonBuilder()
    .disableJdkUnsafe()
    .setLenient()
    .create();

  protected static class BiomeJson {
    public double temperature = 0.5;
    public double downfall = 0.5;
    public BiomeEffects effects = new BiomeEffects();
  }

  protected static class BiomeEffects {
    public JsonElement foliage_color = null;
    public JsonElement grass_color = null;
    public JsonElement water_color = null;
    public String grass_color_modifier = null;
  }

  private static Integer parseColor(JsonElement element) {
    if (element == null || element.isJsonNull()) {
      return null;
    }
    try {
      if (element.isJsonPrimitive()) {
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (primitive.isNumber()) {
          return primitive.getAsInt();
        }
        if (primitive.isString()) {
          Vector3 color = new Vector3();
          ColorUtil.fromHexString(primitive.getAsString(), color);
          return ColorUtil.getRGB(color);
        }
        if (primitive.isJsonArray()) {
          JsonArray array = primitive.getAsJsonArray();
          return ColorUtil.getRGB(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
        }
      }
    } catch (Exception e) {
      Log.warnf("Unsupported biome color value: %s", element.toString());
    }
    return null;
  }

  @Override
  public boolean load(LayeredResourcePacks resourcePacks) {
    DataPackUtil.forEachDataRegistryEntry(resourcePacks, "worldgen/biome", biome -> {
      if (!Biomes.contains(biome.getNamespacedName())) {
        try (Reader f = Files.newBufferedReader(biome.path())) {
          BiomeJson json = GSON.fromJson(f, BiomeJson.class);

          BiomeBuilder builder = Biome.create(biome.getNamespacedName(), biome.name(), json.temperature, json.downfall);
          Optional.ofNullable(parseColor(json.effects.foliage_color)).ifPresent(builder::foliageColor);
          Optional.ofNullable(parseColor(json.effects.grass_color)).ifPresent(builder::grassColor);
          Optional.ofNullable(parseColor(json.effects.water_color)).ifPresent(builder::waterColor);
          Optional.ofNullable(json.effects.grass_color_modifier).ifPresent(modifier -> {
            switch (modifier.toLowerCase()) {
              case "none":
                break;
              case "dark_forest":
                builder.darkForest();
                break;
              case "swamp":
                builder.swamp();
                break;
              default:
                Log.warnf("Unsupported biome `grass_modifier_color`: %s", modifier);
            }
          });
          // TODO Custom fog colors
          Biomes.register(builder);
        } catch (Exception e) {
          Log.warn("Failed to load biome " + biome.getNamespacedName() + " from " + biome.resourcePack().getFile().getAbsolutePath(), e);
        }
      }
    });
    return false;
  }

  @Override
  public void resetLoadedResources() {
    Biomes.reset();
  }
}
