/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2015-2022 Chunky contributors
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
package se.llbit.chunky.renderer.export;

import se.llbit.chunky.plugin.PluginApi;

import java.util.Collection;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

public abstract class PictureExportFormats {

  public static final PictureExportFormat PNG;

  /**
   * All available export formats sorted by name (key).
   */
  private static final SortedMap<String, PictureExportFormat> formats = new TreeMap<>();

  static {
    PictureExportFormat png = new PngExportFormat();
    registerFormat(png);
    PNG = png;

    registerFormat(new Tiff32ExportFormat());
    registerFormat(new PfmExportFormat());
  }

  /**
   * Register a picture export format for scenes.
   *
   * @param pictureExportFormat Picture format implementation
   */
  @PluginApi
  public static void registerFormat(PictureExportFormat pictureExportFormat) {
    formats.put(pictureExportFormat.getName(), pictureExportFormat);
  }

  /**
   * Get an output format by its name.
   *
   * @param name Unique name of the format
   * @return Format implementation
   */
  public static Optional<PictureExportFormat> getFormat(String name) {
    return Optional.ofNullable(formats.get(name));
  }

  /**
   * Get all output formats.
   *
   * @return All output formats
   */
  public static Collection<PictureExportFormat> getFormats() {
    return formats.values();
  }
}
