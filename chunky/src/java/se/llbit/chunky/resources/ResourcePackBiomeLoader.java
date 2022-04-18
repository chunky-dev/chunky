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

import se.llbit.chunky.world.biome.Biome;
import se.llbit.chunky.world.biome.BiomeBuilder;
import se.llbit.chunky.world.biome.Biomes;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonValue;
import se.llbit.util.JsonPreprocessor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

public class ResourcePackBiomeLoader implements ResourcePackLoader.PackLoader {
  public ResourcePackBiomeLoader() {}

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
                    try (InputStream f = new BufferedInputStream(Files.newInputStream(biome))) {
                      JsonValue v = JsonPreprocessor.parse(f);
                      JsonObject root = v.asObject();

                      double temperature = root.get("temperature").doubleValue(0.5);
                      double rain = root.get("downfall").doubleValue(0.5);
                      BiomeBuilder builder = Biome.create(resourceLocation, biomeName, temperature, rain);

                      JsonObject effects = root.get("effects").asObject();
                      JsonValue t;
                      if (!(t = effects.get("foliage_color")).isUnknown()) {
                        builder.foliageColor(t.intValue(0));
                      }
                      if (!(t = effects.get("grass_color")).isUnknown()) {
                        builder.grassColor(t.intValue(0));
                      }
                      if (!(t = effects.get("water_color")).isUnknown()) {
                        builder.waterColor(t.intValue(0));
                      }

                      Biomes.register(builder);
                    } catch (IOException | JsonParser.SyntaxError ignored) {
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
