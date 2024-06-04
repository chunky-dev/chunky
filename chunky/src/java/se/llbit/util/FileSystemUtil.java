package se.llbit.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.util.Collections;

public class FileSystemUtil {
  public static FileSystem getZipFileSystem(File file) throws IOException {
    URI uri = URI.create("jar:" + file.toURI());
    try {
      return java.nio.file.FileSystems.getFileSystem(uri);
    } catch (FileSystemNotFoundException e) {
      // in this case we need to initialize it first:
      return java.nio.file.FileSystems.newFileSystem(uri, Collections.emptyMap());
    }
  }
}
