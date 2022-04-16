package se.llbit.chunky.resources;

import se.llbit.chunky.world.biome.Biome;
import se.llbit.chunky.world.biome.Biomes;

import java.io.File;
import java.io.IOException;
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
                                .map(biomes::relativize)
                                .forEach(biome -> {
                            if (biome.toString().endsWith(".json")) {
                                String biomeName = getBiomeName(biome);
                                Biomes.register(Biome.create(namespace + ":" + biomeName, biomeName, 0, 0));
                            }
                        });
                    } catch (IOException ignored) {}
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
