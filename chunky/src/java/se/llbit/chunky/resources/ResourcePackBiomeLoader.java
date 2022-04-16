package se.llbit.chunky.resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
                    try (Stream<Path> biomeStream = Files.list(biomes)) {
                        biomeStream.forEach(biome -> {
                            String biomeName = String.valueOf(biome.getFileName());
                            if (biomeName.endsWith(".json")) {
                                biomeName = biomeName.substring(0, biomeName.length() - ".json".length());
                                System.out.printf("%s:%s\n", namespace, biomeName);
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

    @Override
    public String[] toLoad() {
        return null;
    }
}
