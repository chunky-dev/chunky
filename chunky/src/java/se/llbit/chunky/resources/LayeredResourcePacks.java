package se.llbit.chunky.resources;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LayeredResourcePacks implements Closeable {
  private final List<ResourcePack> resourcePacks = new ArrayList<>();

  public void addResourcePack(File resourcePackFile) throws IOException {
    this.resourcePacks.add(new ResourcePack(resourcePackFile));
  }

  public List<File> getResourcePackFiles() {
    return Collections.unmodifiableList(resourcePacks.stream().map(ResourcePack::getFile).collect(Collectors.toList()));
  }

  public Optional<InputStream> getInputStream(String path) throws IOException {
    Optional<Entry> entry = getFirstEntry(path);
    if (entry.isPresent()) {
      return Optional.ofNullable(entry.get().getInputStream());
    }
    return Optional.empty();
  }

  public Optional<Entry> getFirstEntry(String path) {
    for (ResourcePack pack : resourcePacks) {
      try {
        Path resolvedPath = pack.getRootPath().resolve(path);
        if (Files.exists(resolvedPath)) {
          return Optional.of(new Entry(pack, resolvedPath));
        }
      } catch (IOException e) {
        // ignore
      }
    }
    return Optional.empty();
  }

  public Iterable<Entry> getAllEntries(String path) {
    List<Entry> entries = new ArrayList<>();
    for (ResourcePack pack : resourcePacks) {
      Path resolvedPath;
      try {
        resolvedPath = pack.getRootPath().resolve(path);
      } catch (IOException e) {
        continue;
      }
      if (Files.exists(resolvedPath)) {
        entries.add(new Entry(pack, resolvedPath));
      }
    }
    return entries;
  }

  @Override
  public void close() throws IOException {
    for (ResourcePack resourcePack : resourcePacks) {
      resourcePack.close();
    }
  }

  public static class ResourcePack implements Closeable {
    public File file;
    private FileSystem fileSystem;
    private Path rootPath;

    private ResourcePack(File resourcePackFile) {
      this.file = resourcePackFile;
    }

    public File getFile() {
      return file;
    }

    private FileSystem getFileSystem() throws IOException {
      if (fileSystem != null && fileSystem.isOpen()) {
        return fileSystem;
      }
      return fileSystem = ResourcePackLoader.getPackFileSystem(file);
    }

    public Path getRootPath() throws IOException {
      if (rootPath == null) {
        Path rootPath = ResourcePackLoader.getPackRootPath(file, getFileSystem());
        String baseName = file.getName();
        if (baseName.toLowerCase().endsWith(".zip")) {
          // The assets directory can be inside a top-level directory with
          // the same name as the resource pack zip file.
          baseName = baseName.substring(0, baseName.length() - 4);
          if (Files.exists(rootPath.resolve(baseName).resolve("assets"))) {
            rootPath = rootPath.resolve(baseName);
          }
        }
        this.rootPath = rootPath;
      }

      return rootPath;
    }

    @Override
    public void close() throws IOException {
      if (fileSystem == null) {
        return;
      }
      try {
        fileSystem.close();
      } catch (UnsupportedOperationException e) {
        // default file systems do not support closing
      }
    }
  }

  public static class Entry {
    private final ResourcePack pack;
    private final Path path;

    public Entry(ResourcePack pack, Path path) {
      this.pack = pack;
      this.path = path;
    }

    public ResourcePack getPack() {
      return pack;
    }

    public Path getPath() {
      return path;
    }

    public InputStream getInputStream() throws IOException {
      return Files.newInputStream(path);
    }
  }
}
