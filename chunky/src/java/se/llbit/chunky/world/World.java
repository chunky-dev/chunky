/* Copyright (c) 2010-2015 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.world;

import se.llbit.chunky.world.worldformat.WorldFormat;
import se.llbit.util.annotation.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

/**
 * The World class contains {@link Info metadata} about itself, and methods for querying and loading its dimensions.
 * It also contains the {@link #currentDimension() currently loaded dimension} if there is one.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public abstract class World {
  /**
   * Metadata about a world.
   */
  public record Info(String name, Path path, long lastModified, long seed, String gameMode, WorldFormat worldFormat) {
    /**
     * Chunky sees each valid {@link Info#path()} and {@link Info#worldFormat()} combination as a distinct world which
     * can be selected by the user.
     */
    public boolean isSameWorld(Info other) {
      return this.worldFormat == other.worldFormat && this.path.equals(other.path);
    }
  }

  /** Default sea water level. */
  public static final int SEA_LEVEL = 63;

  private final Info info;

  @NotNull
  protected Dimension currentDimension;

  protected World(Info info) {
    this.info = info;
    this.currentDimension = EmptyDimension.INSTANCE;
  }

  public enum LoggedWarnings {
    NORMAL,
    SILENT
  }

  /**
   * The dimensions returned here are later provided to {@link #loadDimension(Dimension.Identifier)} when requesting a
   * dimension be loaded.
   *
   * @return List the viewable dimensions within the world.
   */
  public abstract Set<Dimension.Identifier> getAvailableDimensions();

  /**
   * <b>MUST</b> be one of {@link #getAvailableDimensions()}
   * @return The preferred default dimension of this world (typically the overworld)
   */
  public abstract Optional<Dimension.Identifier> getDefaultDimension();

  /**
   * @param dimensionId The dimension to load, guaranteed to be one of the dimensions previously returned by {@link #getAvailableDimensions()}
   * @return The loaded dimension
   */
  public abstract Dimension loadDimension(Dimension.Identifier dimensionId);

  /**
   * @return The current dimension or {@link EmptyDimension#INSTANCE} if there isn't one.
   */
  @NotNull
  public synchronized Dimension currentDimension() {
    return this.currentDimension;
  }

  public Info getInfo() {
    return this.info;
  }

  @Override public String toString() {
    return info.name + " (" + info.path.getFileName() + ")";
  }

  /**
   * Get the resource pack that is bundled with this world, i.e. the contained resourced directory or resources.zip.
   *
   * @return Resource pack file/directory or empty optional if this world has no bundled resource pack
   */
  public Optional<File> getResourcePack() {
    for (File resourcepacksDirectory : new File[]{ info.path.toFile(), new File(info.path.toFile(), "resourcepacks")}) {
      if (resourcepacksDirectory.isDirectory()) {
        File resourcePack = new File(resourcepacksDirectory, "resources.zip");
    if (resourcePack.isFile()) {
      return Optional.of(resourcePack);
    }
        resourcePack = new File(resourcepacksDirectory, "resources");
    if (resourcePack.isDirectory()) {
      return Optional.of(resourcePack);
        }
      }
    }
    return Optional.empty();
  }
}
