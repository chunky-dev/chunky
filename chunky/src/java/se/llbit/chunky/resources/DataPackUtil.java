package se.llbit.chunky.resources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class DataPackUtil {
  private DataPackUtil() {
  }

  public static void forEachDataRegistryEntry(LayeredResourcePacks resourcePacks, String registryPath, Consumer<DataRegistryEntry> consumer) {
    for (LayeredResourcePacks.Entry data : resourcePacks.getAllEntries("data")) {
      try (Stream<Path> namespaces = Files.list(data.getPath())) {
        namespaces.forEach(ns -> {
          String namespace = String.valueOf(ns.getFileName());
          Path entriesPath = ns;
          for (String part : registryPath.split("/")) {
            entriesPath = entriesPath.resolve(part);
          }
          try (Stream<Path> entriesStream = Files.walk(entriesPath)) {
            entriesStream
              .filter(p -> Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS))
              .forEach(file -> {
                String name = file.getFileName().toString();
                if (name.toLowerCase().endsWith(".json")) {
                  name = name.substring(0, name.length() - ".json".length());
                }
                consumer.accept(new DataRegistryEntry(namespace, name, file));
              });
          } catch (IOException ignored) {
          }
        });
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public record DataRegistryEntry(String namespace, String name, Path path) {
    public String getNamespacedName() {
      return namespace + ":" + name;
    }
  }
}
