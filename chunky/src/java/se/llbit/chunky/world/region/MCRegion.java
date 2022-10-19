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
package se.llbit.chunky.world.region;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import se.llbit.chunky.chunk.ChunkLoadingException;
import se.llbit.chunky.world.*;
import se.llbit.log.Log;
import se.llbit.nbt.ErrorTag;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.Tag;
import se.llbit.util.Mutable;

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
public class MCRegion implements Region {

  /**
   * Region X chunk width
   */
  public static final int CHUNKS_X = 32;

  /**
   * Region Z chunk width
   */
  public static final int CHUNKS_Z = 32;

  private static final int NUM_CHUNKS = CHUNKS_X * CHUNKS_Z;

  /**
   * Sector size in bytes.
   */
  private final static int SECTOR_SIZE = 4096;

  private final Chunk[] chunks = new Chunk[NUM_CHUNKS];
  private final ChunkPosition position;
  private final World world;
  private final String fileName;
  private long regionFileTime = 0;
  private final int[] chunkTimestamps = new int[NUM_CHUNKS];

  /**
   * Create new region
   *
   * @param pos the region position
   */
  public MCRegion(ChunkPosition pos, World world) {
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
  @Override
  public Chunk getChunk(int x, int z) {
    return chunks[(x & 31) + (z & 31) * 32];
  }

  /**
   * Set chunk at given position.
   */
  private void setChunk(ChunkPosition pos, Chunk chunk) {
    chunks[(pos.x & 31) + (pos.z & 31) * 32] = chunk;
  }

  /**
   * Delete a chunk.
   */
  @Override
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
   * @param minY the minimum requested block Y to be loaded. This does NOT need to be respected by the implementation
   * @param maxY the maximum requested block Y to be loaded. This does NOT need to be respected by the implementation
   */
  @Override
  public synchronized void parse(int minY, int maxY) {
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
        Log.warnf("Missing header in region file %s!", this.position);
        return;
      }

      for (int z = 0; z < 32; ++z) {
        for (int x = 0; x < 32; ++x) {
          ChunkPosition pos = new ChunkPosition((position.x << 5) + x, (position.z << 5) + z);
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
      Log.warn("Failed to read region: " + e.getMessage());
    }
  }

  /**
   * @return The region position
   */
  @Override
  public final ChunkPosition getPosition() {
    return position;
  }

  @Override
  public String toString() {
    return "Region " + position.toString();
  }

  /**
   * @param pos A region position
   * @return The region file name corresponding to the given region position
   */
  public static String getFileName(ChunkPosition pos) {
    return String.format("r.%d.%d.mca", pos.x, pos.z);
  }

  public Map<String, Tag> getChunkTags(ChunkPosition position, Set<String> request, Mutable<Integer> dataTimestamp) throws ChunkLoadingException {
    ChunkDataSource data = this.getChunkData(position);
    dataTimestamp.set(data.timestamp);
    if (data.hasData()) {
      try (DataInputStream in = new DataInputStream(data.getInputStream())) {
        Map<String, Tag> result = NamedTag.quickParse(in, request);
        for (String key : request) {
          if (!result.containsKey(key)) {
            result.put(key, new ErrorTag(""));
          }
        }
        return result;
      } catch (IOException e) {
        throw new ChunkLoadingException(String.format("Failed to read chunk %s from region file!", position), e);
      }
    }
    return null;
  }

  private static int getMCAChunkIndex(ChunkPosition chunkPos) {
    int x = chunkPos.x & 0b11111;
    int z = chunkPos.z & 0b11111;
    return x + (z << 5);
  }

  /**
   * Opens an input stream for the given chunk.
   *
   * @param chunkPos chunk position for the chunk to read
   * @return Chunk data source. The InputStream of the data source is
   * {@code null} if the chunk could not be read.
   */
  private ChunkDataSource getChunkData(ChunkPosition chunkPos) {
    File regionDirectory = world.getRegionDirectory();
    File regionFile = new File(regionDirectory, fileName);
    ChunkDataSource data = null;
    if (regionFile.exists()) {
      // TODO: reuse RandomAccessFile instances when loading world map
      try (RandomAccessFile raf = new RandomAccessFile(regionFile, "r")) {
        data = readChunkData(raf, chunkPos);
      } catch (IOException ex) {
        Log.warn(
          String.format(
            "Failed to read chunk %s in region %s",
            chunkPos,
            regionFile.getName()
          ), ex
        );
      }
    }
    if (data == null) {
      data = new ChunkDataSource((int) System.currentTimeMillis());
    }
    chunkTimestamps[getMCAChunkIndex(chunkPos)] = data.timestamp;
    return data;
  }

  /**
   * Read chunk data from region file.
   * <a href="https://wiki.vg/Region_Files#Structure">Format documentation</a>
   *
   * @return {@code null} if the chunk could not be loaded
   */
  private static ChunkDataSource readChunkData(RandomAccessFile file, ChunkPosition chunkPos) throws IOException {
    long index = getMCAChunkIndex(chunkPos);

    long length = file.length();
    if (length < SECTOR_SIZE << 1) {
      throw new ChunkReadException(chunkPos, "Missing header in region file");
    }

    // query location table for chunk location in file
    file.seek(index << 2);
    int locationEntry = file.readInt();
    int sectorCount = locationEntry & 0xFF;
    int sectorOffset = locationEntry >> 8;
    if (sectorOffset == 0 && sectorCount == 0) {
      // chunk not generated yet
      return null;
    }

    // query timestamp table (chunk last modified time)
    file.seek(SECTOR_SIZE + (index << 2));
    int lastModifiedTimestamp = file.readInt();

    long fileOffset = (long) sectorOffset * SECTOR_SIZE;
    if (fileOffset + 4 >= length) {
      throw new ChunkReadException(chunkPos, String.format(
        "Chunk is outside of region file. Expected chunk data at offset %d but file length is %d.",
        sectorOffset * SECTOR_SIZE, length
      ));
    }
    file.seek(fileOffset);

    int chunkSize = file.readInt();

    if (chunkSize > sectorCount * SECTOR_SIZE) {
      throw new ChunkReadException(chunkPos, "Chunk length does not fit in allocated sectors");
    }

    if (length < fileOffset + 4 + chunkSize) {
      throw new ChunkReadException(chunkPos, String.format(
        "Chunk is outside of region file. Expected %d bytes at offset %d but file length is %d.",
        chunkSize, sectorOffset * SECTOR_SIZE, length
      ));
    }

    if (chunkSize <= 0) {
      throw new ChunkReadException(chunkPos, String.format(
        "Invalid chunk size: %d",
        chunkSize
      ));
    }

    ChunkDataSource.CompressionScheme compressionScheme = readCompressionScheme(file, chunkPos);

    byte[] buf = new byte[chunkSize - 1];
    file.read(buf);
    return new ChunkDataSource(lastModifiedTimestamp, buf, compressionScheme);
  }

  private static ChunkDataSource.CompressionScheme readCompressionScheme(RandomAccessFile file, ChunkPosition chunkPos) throws IOException {
    byte compressionType = file.readByte();
    switch (compressionType) {
      case 1:
        return ChunkDataSource.CompressionScheme.GZIP;
      case 2:
        return ChunkDataSource.CompressionScheme.ZLIB;
      default:
        throw new ChunkReadException(chunkPos, String.format(
          "Unknown chunk data compression method: %d",
          compressionType
        ));
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
        if (offset != 0 && (chunks == null || chunks.contains(new ChunkPosition(i & 31, i >> 5)))) {
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

  @Override
  public boolean hasChanged() {
    File regionFile = new File(world.getRegionDirectory(), fileName);
    return regionFileTime != regionFile.lastModified();
  }

  /**
   * @return {@code true} if the chunk has changed since the timestamp
   */
  @Override
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
