/* Copyright (c) 2012-2016 Jesper Öqvist <jesper@llbit.se>
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

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import se.llbit.log.Log;

/**
 * Abstract region representation. Tracks loaded chunks and their timestamps.
 *
 * <p>If an error occurs it will usually be reported to STDERR instead of using
 * the logging framework, because the error dialogs can be so many for a
 * single corrupted region. Corrupted chunks are illustrated by a black square
 * with a red X and red outline in the map view.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Region implements Iterable<Chunk> {

  /**
   * Region X chunk width
   */
  public static final int CHUNKS_X = 32;

  /**
   * Region Z chunk width
   */
  public static final int CHUNKS_Z = 32;

  private static final int NUM_CHUNKS = CHUNKS_X * CHUNKS_Z;

  private final Chunk[] chunks = new Chunk[NUM_CHUNKS];
  private final ChunkPosition position;
  private final World world;
  private final String fileName;
  private long regionFileTime = 0;
  private final int[] chunkTimestamps = new int[NUM_CHUNKS];

  /**
   * Create new region
   *
   * @param pos   the region position
   */
  public Region(ChunkPosition pos, World world) {
    this.world = world;
    fileName = pos.getMcaName();
    position = pos;
    for (int z = 0; z < CHUNKS_Z; ++z) {
      for (int x = 0; x < CHUNKS_X; ++x) {
        chunks[x + z * 32] = EmptyChunk.INSTANCE;
      }
    }
  }

  /**
   * @return Chunk at (x, z)
   */
  public Chunk getChunk(int x, int z) {
    return chunks[(x & 31) + (z & 31) * 32];
  }

  /**
   * @param pos Chunk position
   * @return Chunk at given position
   */
  public Chunk getChunk(ChunkPosition pos) {
    return chunks[(pos.x & 31) + (pos.z & 31) * 32];
  }

  /**
   * Set chunk at given position.
   */
  public void setChunk(ChunkPosition pos, Chunk chunk) {
    chunks[(pos.x & 31) + (pos.z & 31) * 32] = chunk;
  }

  /**
   * Delete a chunk.
   */
  public synchronized void deleteChunk(ChunkPosition chunkPos) {
    deleteChunkFromRegion(chunkPos);
    Chunk chunk = getChunk(chunkPos);
    if (!chunk.isEmpty()) {
      chunk.reset();
      setChunk(chunkPos, EmptyChunk.INSTANCE);
      world.chunkDeleted(chunkPos);
    }
  }

  /**
   * Parse the region file to discover chunks.
   */
  public synchronized void parse() {
    File regionFile = new File(world.getRegionDirectory(), fileName);
    if (!regionFile.isFile()) {
      return;
    }
    long modtime = regionFile.lastModified();
    if (regionFileTime == modtime) {
      return;
    }
    regionFileTime = modtime;
    try (RandomAccessFile file = new RandomAccessFile(regionFile, "r")) {
      long length = file.length();
      if (length < 2 * SECTOR_SIZE) {
        System.err.println("Missing header in region file!");
        return;
      }

      for (int z = 0; z < 32; ++z) {
        for (int x = 0; x < 32; ++x) {
          ChunkPosition pos = ChunkPosition.get((position.x << 5) + x, (position.z << 5) + z);
          Chunk chunk = getChunk(x, z);
          int loc = file.readInt();
          if (loc != 0) {
            if (chunk.isEmpty()) {
              chunk = new Chunk(pos, world);
              setChunk(pos, chunk);
            }
          } else {
            if (!chunk.isEmpty()) {
              world.chunkDeleted(pos);
            }
          }
        }
      }

      for (int i = 0; i < NUM_CHUNKS; ++i) {
        chunkTimestamps[i] = file.readInt();
      }

      world.regionUpdated(position);
    } catch (IOException e) {
      System.err.println("Failed to read region: " + e.getMessage());
    }
  }

  /**
   * @return <code>true</code> if this is an empty or non-existent region
   */
  public boolean isEmpty() {
    return false;
  }

  /**
   * @return The region position
   */
  public final ChunkPosition getPosition() {
    return position;
  }

  @Override public String toString() {
    return "Region " + position.toString();
  }

  /**
   * @param pos A region position
   * @return The region file name corresponding to the given region position
   */
  public static String getFileName(ChunkPosition pos) {
    return String.format("r.%d.%d.mca", pos.x, pos.z);
  }

  /**
   * Sector size in bytes.
   */
  private final static int SECTOR_SIZE = 4096;

  /**
   * Opens an input stream for the given chunk.
   *
   * @param chunkPos chunk position for the chunk to read
   * @return Chunk data source. The InputStream of the data source is
   * {@code null} if the chunk could not be read.
   */
  public ChunkDataSource getChunkData(ChunkPosition chunkPos) {
    File regionDirectory = world.getRegionDirectory();
    File regionFile = new File(regionDirectory, fileName);
    ChunkDataSource data = null;
    if (regionFile.exists()) {
      data = getChunkData(regionFile, chunkPos);
    }
    if (data == null) {
      data = new ChunkDataSource((int) System.currentTimeMillis(), null);
    }
    chunkTimestamps[(chunkPos.x & 31) + (chunkPos.z & 31) * 32] = data.timestamp;
    return data;
  }

  /**
   * Read chunk data from region file.
   *
   * @return {@code null} if the chunk could not be loaded
   */
  public static ChunkDataSource getChunkData(File regionFile, ChunkPosition chunkPos) {
    int x = chunkPos.x & 31;
    int z = chunkPos.z & 31;
    int index = x + z * 32;
    try (RandomAccessFile file = new RandomAccessFile(regionFile, "r")) {
      long length = file.length();
      if (length < 2 * SECTOR_SIZE) {
        Log.warn("Missing header in region file!");
        return null;
      }
      file.seek(4 * index);
      int loc = file.readInt();
      int numSectors = loc & 0xFF;
      int sectorOffset = loc >> 8;
      file.seek(SECTOR_SIZE + 4 * index);
      int timestamp = file.readInt();
      if (length < sectorOffset * SECTOR_SIZE + 4) {
        System.err.printf("Chunk %s is outside of region file %s! Expected chunk data at offset %d but file length is %d.%n", chunkPos, regionFile.getName(), sectorOffset * SECTOR_SIZE, length);
        return null;
      }
      file.seek(sectorOffset * SECTOR_SIZE);

      int chunkSize = file.readInt();

      if (chunkSize > numSectors * SECTOR_SIZE) {
        System.err.println("Error: chunk length does not fit in allocated sectors!");
        return null;
      }

      if (length < sectorOffset * SECTOR_SIZE + 4 + chunkSize) {
        System.err.printf("Chunk %s is outside of region file %s! Expected %d bytes at offset %d but file length is %d.%n", chunkPos, regionFile.getName(), chunkSize, sectorOffset * SECTOR_SIZE, length);
        return null;
      }

      byte type = file.readByte();
      if (type != 1 && type != 2) {
        System.err.println("Error: unknown chunk data compression method: " + type + "!");
        return null;
      }

      byte[] buf = new byte[chunkSize - 1];
      file.read(buf);
      ByteArrayInputStream in = new ByteArrayInputStream(buf);
      if (type == 1) {
        return new ChunkDataSource(timestamp, new GZIPInputStream(in));
      } else {
        return new ChunkDataSource(timestamp, new InflaterInputStream(in));
      }
    } catch (IOException e) {
      System.err.println("Failed to read chunk: " + e.getMessage());
      return null;
    }
  }

  /**
   * Delete the chunk from the region file.
   */
  public void deleteChunkFromRegion(ChunkPosition chunkPos) {
    // Just write zero in the entry for the chunk in the location table.
    File regionDirectory = world.getRegionDirectory();
    int x = chunkPos.x & 31;
    int z = chunkPos.z & 31;
    File regionFile = new File(regionDirectory, fileName);
    int index = x + z * 32;
    try (RandomAccessFile file = new RandomAccessFile(regionFile, "rw")) {
      long length = file.length();
      if (length < 2 * SECTOR_SIZE) {
        Log.warn("Missing header in region file!");
        return;
      }
      file.seek(4 * index);
      file.writeInt(0);
    } catch (IOException e) {
      Log.warnf("Failed to delete chunk: %s", e.getMessage());
    }
  }

  /**
   * Write this region to the output stream.
   *
   * @throws IOException
   */
  public static synchronized void writeRegion(File regionDirectory, ChunkPosition regionPos,
      DataOutputStream out, Set<ChunkPosition> chunks) throws IOException {
    String fileName = regionPos.getMcaName();
    File regionFile = new File(regionDirectory, fileName);
    try (RandomAccessFile file = new RandomAccessFile(regionFile, "r")) {
      int[] location = new int[32 * 32];
      int[] loc_out = new int[32 * 32];
      int nextFree = 2;// 2 sectors reserved for offsets and timestamps
      for (int i = 0; i < 32 * 32; ++i) {
        location[i] = file.readInt();
        int offset = location[i];
        if (offset != 0 && (chunks == null || chunks.contains(ChunkPosition.get(i & 31, i >> 5)))) {
          loc_out[i] = nextFree << 8 | offset & 0xFF;
          nextFree += offset & 0xFF;
        }
      }

      // Write offset table.
      for (int i = 0; i < 32 * 32; ++i) {
        out.writeInt(loc_out[i]);
      }

      // Write timestamp table.
      for (int i = 0; i < 32 * 32; ++i) {
        out.writeInt(file.readInt());
      }

      // Write chunks.
      for (int i = 0; i < 32 * 32; ++i) {
        if (loc_out[i] == 0) {
          continue;
        }

        int loc = location[i];
        int numSectors = loc & 0xFF;
        int sectorOffset = loc >> 8;

        file.seek(sectorOffset * SECTOR_SIZE);
        byte[] buffer = new byte[SECTOR_SIZE];
        for (int j = 0; j < numSectors; ++j) {
          file.read(buffer);
          out.write(buffer);
        }
      }
    }
  }

  public boolean hasChanged() {
    File regionFile = new File(world.getRegionDirectory(), fileName);
    return regionFileTime != regionFile.lastModified();
  }

  /**
   * @return {@code true} if the chunk has changed since the timestamp
   */
  public boolean chunkChangedSince(ChunkPosition chunkPos, int timestamp) {
    return timestamp != chunkTimestamps[(chunkPos.x & 31) + (chunkPos.z & 31) * 32];
  }

  @Override public Iterator<Chunk> iterator() {
    return new Iterator<Chunk>() {
      private int index = 0;

      @Override public boolean hasNext() {
        return index < NUM_CHUNKS;
      }

      @Override public Chunk next() {
        return chunks[index++];
      }

      @Override public void remove() {
        chunks[index] = EmptyChunk.INSTANCE;
      }
    };
  }
}
