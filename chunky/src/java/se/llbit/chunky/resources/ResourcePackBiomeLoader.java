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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import se.llbit.chunky.world.biome.Biome;
import se.llbit.chunky.world.biome.BiomeBuilder;
import se.llbit.chunky.world.biome.Biomes;
import se.llbit.log.Log;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

public class ResourcePackBiomeLoader implements ResourcePackLoader.PackLoader {
  public ResourcePackBiomeLoader() {}

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
    public Integer foliage_color = null;
    public Integer grass_color = null;
    public Integer water_color = null;
    public String grass_color_modifier = null;
  }

  @Override
  public boolean load(Path pack, String baseName) {
    Path data = pack.resolve("data");
    if (Files.exists(data)) {
      try (Stream<Path> namespaces = Files.list(data)) {
        namespaces.forEach(ns -> {
          String namespace = String.valueOf(ns.getFileName());

          Path biomes = ns.resolve("worldgen").resolve("biome");
          try (Stream<Path> biomeStream = Files.walk(biomes)) {
            biomeStream
              .filter(p -> Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS))
              .forEach(biome -> {
                if (biome.toString().endsWith(".json")) {
                  String biomeName = getBiomeName(biomes.relativize(biome));
                  String resourceLocation = namespace + ":" + biomeName;

                  if (!Biomes.contains(resourceLocation)) {
                    try (Reader f = Files.newBufferedReader(biome)) {
                      BiomeJson json = GSON.fromJson(f, BiomeJson.class);

                      BiomeBuilder builder = Biome.create(resourceLocation, biomeName, json.temperature, json.downfall);
                      Optional.ofNullable(json.effects.foliage_color).ifPresent(builder::foliageColor);
                      Optional.ofNullable(json.effects.grass_color).ifPresent(builder::grassColor);
                      Optional.ofNullable(json.effects.water_color).ifPresent(builder::waterColor);
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
                    } catch (IOException ignored) {
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

  private static String getBiomeName(Path biome) {
    ArrayList<String> path = new ArrayList<>();
    biome.iterator().forEachRemaining(p -> path.add(String.valueOf(p)));

    String out = String.join("/", path);
    if (out.toLowerCase().endsWith(".json")) {
      out = out.substring(0, out.length() - ".json".length());
    }
    return out;
  }
}
