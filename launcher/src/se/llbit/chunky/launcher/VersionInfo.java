/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.json.PrettyPrinter;
import se.llbit.util.Util;

/**
 * Describes Chunky version info.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class VersionInfo implements Comparable<VersionInfo> {

  public enum LibraryStatus {
    PASSED("Installed"),
    CHECKSUM_MISMATCH("To be downloaded (checksum mismatch)"),
    INCOMPLETE_INFO("Incomplete library information"),
    MISSING("To be downloaded (file missing)"),
    MALFORMED_URL("Download failed (malformed URL)"),
    FILE_NOT_FOUND("Download failed (file not found)"),
    DOWNLOAD_FAILED("Download failed"),
    DOWNLOADED_OK("Downloaded OK");

    public final String dlStatus;

    LibraryStatus(String status) {
      dlStatus = status;
    }

    public String downloadStatus() {
      return dlStatus;
    }
  }

  public static class Library {
    private static final boolean DISABLE_CHECKSUM_CHECK = Boolean.parseBoolean(System.getProperty("chunky.dangerouslyDisableLibraryVerification", "false"));
    public final String name;
    public final String md5;
    public final String sha256;
    public final String url;
    public final int size;

    public Library(String name, String md5, String sha256, int size) {
      this.name = name;
      this.md5 = md5;
      this.sha256 = sha256;
      this.url = "";
      this.size = size;
    }

    public Library(JsonObject obj) {
      name = obj.get("name").stringValue("");
      md5 = obj.get("md5").stringValue("");
      sha256 = obj.get("sha256").stringValue("");
      url = obj.get("url").stringValue("");
      size = obj.get("size").intValue(1);
    }

    public File getFile(File libDir) {
      return new File(libDir, name);
    }

    public LibraryStatus testIntegrity(File libDir) {
      if (name.isEmpty() || (!DISABLE_CHECKSUM_CHECK && md5.isEmpty() && sha256.isEmpty())) {
        return LibraryStatus.INCOMPLETE_INFO;
      }
      File library = new File(libDir, name);
      if (!library.isFile()) {
        return LibraryStatus.MISSING;
      }
      if (!DISABLE_CHECKSUM_CHECK) {
        if (!sha256.isEmpty()) {
          String libSha256 = Util.sha256sum(library);
          if (!libSha256.equalsIgnoreCase(sha256)) {
            return LibraryStatus.CHECKSUM_MISMATCH;
          }
        } else {
          String libMD5 = Util.md5sum(library);
          if (!libMD5.equalsIgnoreCase(md5)) {
            return LibraryStatus.CHECKSUM_MISMATCH;
          }
        }
      }
      return LibraryStatus.PASSED;
    }

    public JsonObject json() {
      JsonObject obj = new JsonObject();
      obj.add("name", name);
      obj.add("md5", md5);
      obj.add("sha256", sha256);
      obj.add("url", url);
      obj.add("size", size);
      return obj;
    }

    @Override public String toString() {
      return name;
    }
  }

  public static final VersionInfo LATEST = new VersionInfo("latest", new Date());
  public static final VersionInfo NONE = new VersionInfo("none", new Date());

  public final String name;
  public Collection<Library> libraries = new LinkedList<>();
  private final String timestamp;
  private final Date date;
  public String notes = "";

  public VersionInfo(JsonObject json) {
    name = json.get("name").stringValue("");
    timestamp = json.get("timestamp").stringValue("");
    date = Util.dateFromISO8601(timestamp);
    notes = json.get("notes").stringValue("");
    JsonArray libraryArray = json.get("libraries").array();
    for (JsonValue lib : libraryArray) {
      libraries.add(new Library(lib.object()));
    }
  }

  private VersionInfo(String name, Date date) {
    this.name = name;
    this.timestamp = Util.ISO8601FromDate(date);
    this.date = date;
  }

  @Override public boolean equals(Object obj) {
    if (!(obj instanceof VersionInfo)) {
      return false;
    }
    VersionInfo other = (VersionInfo) obj;
    return name.equals(other.name);
  }

  @Override public int hashCode() {
    return name.hashCode();
  }

  @Override public String toString() {
    return name;
  }

  /**
   * @return The value 0 if the argument version is as recent as this version; a
   * value less than 0 if this version is more recent than the argument version;
   * and a value greater than 0 if this version is older than the argument
   * version.
   */
  @Override public int compareTo(VersionInfo o) {
    return o.date.compareTo(date);
  }

  /**
   * @return {@code true} if the version data contains all required information.
   */
  public boolean isValid() {
    if (name.isEmpty()) {
      return false;
    }
    if (!Util.isValidISO8601(timestamp)) {
      return false;
    }
    for (Library lib : libraries) {
      if (lib.name.isEmpty() || lib.md5.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Write the version info to the given file.
   *
   * @throws FileNotFoundException
   */
  public void writeTo(File file) throws IOException {
    try (PrintStream out = new PrintStream(new FileOutputStream(file))) {
      PrettyPrinter pp = new PrettyPrinter("  ", out);
      pp.print(json());
    }
  }

  private JsonObject json() {
    JsonObject obj = new JsonObject();
    obj.add("name", name);
    obj.add("timestamp", timestamp);
    obj.add("notes", notes);
    JsonArray libraryArray = new JsonArray();
    for (Library lib : libraries) {
      libraryArray.add(lib.json());
    }
    obj.add("libraries", libraryArray);
    return obj;
  }

  public String date() {
    DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    return fmt.format(date);
  }
}
