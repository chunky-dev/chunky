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

import se.llbit.chunky.ui.ProgressTracker;

import java.io.*;
import java.util.*;

/**
 * The World class contains information about the currently viewed world.
 * It has a map of all chunks in the world and is responsible for parsing
 * chunks when needed. All rendering is done through the WorldRenderer class.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public abstract class World implements Comparable<World> {
  /** Overworld dimension index. */
  public static final int OVERWORLD_DIMENSION = 0;

  /** Nether dimension index. */
  public static final int NETHER_DIMENSION = -1;

  /** End dimension index. */
  public static final int END_DIMENSION = 1;

  /** Default sea water level. */
  public static final int SEA_LEVEL = 63;


  protected final File worldDirectory;

  protected Dimension currentDimension;
  protected int currentDimensionId;

  protected final String levelName;
  protected int gameMode = 0;
  protected final long seed;

  /** Timestamp for level.dat when player data was last loaded. */
  protected long timestamp;

  /**
   * @param levelName name of the world (not the world directory).
   * @param worldDirectory Minecraft world directory.
   * @param seed
   * @param timestamp
   */
  protected World(String levelName, File worldDirectory, long seed, long timestamp) {
    this.levelName = levelName;
    this.worldDirectory = worldDirectory;
    this.seed = seed;
    this.timestamp = timestamp;
  }

  public enum LoggedWarnings {
    NORMAL,
    SILENT
  }

  public abstract void loadDimension(int dimensionId);

  /**
   * Parse player location and level name.
   *
   * @return {@code true} if the world data was loaded
   */
  public static World loadWorld(File worldDirectory, int dimensionId, LoggedWarnings warnings) {
    if (worldDirectory == null) {
      return EmptyWorld.INSTANCE;
    }
    return JavaWorld.loadWorld(worldDirectory, dimensionId, warnings);
  }

  /**
   * @return The current dimension
   */
  public synchronized Dimension currentDimension() {
    return this.currentDimension;
  }

  /**
   * @return The current dimension
   */
  public synchronized int currentDimensionId() {
    return this.currentDimensionId;
  }


  /**
   * @return The world directory
   */
  public File getWorldDirectory() {
    return worldDirectory;
  }


  /**
   * Export the given chunks to a Zip archive.
   * The Zip arhive is written without compression since the chunks are
   * already compressed with GZip.
   *
   * @throws IOException
   */
  public abstract void exportChunksToZip(File target, Collection<ChunkPosition> chunks, ProgressTracker progress)
    throws IOException;

  /**
   * Export the world to a zip file. The chunks which are included
   * depends on the selected chunks. If any chunks are selected, then
   * only those chunks are exported. If no chunks are selected then all
   * chunks are exported.
   *
   * @throws IOException
   */
  public abstract void exportWorldToZip(File target, ProgressTracker progress)
    throws IOException;

  @Override public String toString() {
    return levelName + " (" + worldDirectory.getName() + ")";
  }

  /** The name of this world (not the world directory name). */
  public String levelName() {
    return levelName;
  }

  /**
   * @return <code>true</code> if the given directory exists and
   * contains a level.dat file
   */
  public static boolean isWorldDir(File worldDir) {
    if (worldDir != null && worldDir.isDirectory()) {
      File levelDat = new File(worldDir, "level.dat");
      return levelDat.exists() && levelDat.isFile();
    }
    return false;
  }

  /**
   * @return String describing the game-mode of this world
   */
  public String gameMode() {
    switch (gameMode) {
      case 0:
        return "Survival";
      case 1:
        return "Creative";
      case 2:
        return "Adventure";
      default:
        return "Unknown";
    }
  }

  @Override public int compareTo(World o) {
    // Compares world names and directories.
    return toString().compareToIgnoreCase(o.toString());
  }

  public long getSeed() {
    return seed;
  }

  public Date getLastModified() {
    return new Date(this.worldDirectory.lastModified());
  }

  /**
   * Get the resource pack that is bundled with this world, i.e. the contained resourced directory or resources.zip.
   *
   * @return Resource pack file/directory or empty optional if this world has no bundled resource pack
   */
  public Optional<File> getResourcePack() {
    File resourcePack = new File(getWorldDirectory(), "resources.zip");
    if (resourcePack.isFile()) {
      return Optional.of(resourcePack);
    }
    resourcePack = new File(getWorldDirectory(), "resources");
    if (resourcePack.isDirectory()) {
      return Optional.of(resourcePack);
    }
    return Optional.empty();
  }
}
