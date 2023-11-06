/* Copyright (c) 2014-2016 Chunky committers
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
package se.llbit.util.io;

import se.llbit.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for exporting scenes to zip files.
 *
 * @author Tim De Keyser <aenterprise2@gmail.com>
 */
public class ZipExport {

  /**
   * Export a scene to a zip file.
   *
   * @param archive    zip file to write
   * @param sceneName  zip prefix
   * @param extensions file extensions to include
   */
  public static void zip(File archive, File sceneDir, String sceneName, String[] extensions) {
    try (
      FileOutputStream fos = new FileOutputStream(archive);
      ZipOutputStream zos = new ZipOutputStream(fos)
    ) {
      for (String extension : extensions) {
        File file = new File(sceneDir, sceneName + extension);
        if (file.exists()) {
          addToZipFile(zos, sceneName, file);
        }
      }
    } catch (IOException e) {
      Log.error(e);
    }
  }

  private static void addToZipFile(ZipOutputStream zos, String prefix, File file) throws IOException {
    try (FileInputStream fis = new FileInputStream(file)) {
      ZipEntry zipEntry = new ZipEntry(prefix + "/" + file.getName());
      zos.putNextEntry(zipEntry);

      byte[] bytes = new byte[4096];
      int length;
      while ((length = fis.read(bytes)) >= 0) {
        zos.write(bytes, 0, length);
      }
      zos.closeEntry();
    }
  }
}
