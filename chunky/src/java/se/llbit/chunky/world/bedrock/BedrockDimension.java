package se.llbit.chunky.world.bedrock;

import io.github.notstirred.leveldb_ffi.*;
import se.llbit.chunky.map.MapView;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.world.*;
import se.llbit.chunky.world.region.EmptyRegion;
import se.llbit.chunky.world.region.Region;
import se.llbit.chunky.world.region.RegionChangeWatcher;
import se.llbit.math.Vector3;
import se.llbit.math.Vector3i;
import se.llbit.util.annotation.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BedrockDimension extends Dimension implements Closeable {
  private static final Cleaner cleaner = Cleaner.create(); // TODO: move this to Chunky class or something usable by all.

  protected final ConcurrentHashMap<RegionPosition, Region> regionMap = new ConcurrentHashMap<>();

  private final LevelDB db;

  private final Map<ChunkPosition, Chunk> chunks = new ConcurrentHashMap<>();

  protected BedrockDimension(BedrockWorld world, Identifier dimensionId, Path dimensionDirectory, Set<PlayerEntityData> playerEntities, @Nullable Vector3i spawnPos) {
    super(dimensionId, dimensionDirectory, playerEntities, null);
    Options options = Options.create();
    options.setCompression(Compressor.ZLIB_RAW);
    options.setCreateIfMissing(false);
    try {
      this.db = LevelDB.open(options, dimensionDirectory.resolve("db").toAbsolutePath());
      cleaner.register(this, this.db::close);
    } catch (LevelDBException e) {
      throw new RuntimeException(e);
    }
  }

  public Optional<byte[]> getDbValue(byte[] key) throws LevelDBException {
    return this.db.get(ReadOptions.create(), key);
  }

  @Override
  public boolean reloadPlayerData() {
    return false;
  }

  @Override
  public Optional<Vector3> getPlayerPos() {
    return Optional.empty();
  }

  @Override
  public void close() throws IOException {
    this.db.close();
  }

  public void setChunk(ChunkPosition position, Chunk chunk) {
    this.chunks.put(position, chunk);
    chunkUpdated(position);
  }

  static class ByteBufferBackedInputStream extends InputStream {
    private final ByteBuffer buf;

    public ByteBufferBackedInputStream(ByteBuffer buf) {
      this.buf = buf;
    }

    public int read() throws IOException {
      if (!buf.hasRemaining()) {
        return -1;
      }
      return buf.get() & 0xFF;
    }

    public int read(byte[] bytes, int off, int len)
      throws IOException {
      if (!buf.hasRemaining()) {
        return -1;
      }

      len = Math.min(len, buf.remaining());
      buf.get(bytes, off, len);
      return len;
    }
  }

  @Override
  public String getName() {
    return "";
  }

  @Override
  public Chunk getChunk(ChunkPosition pos) {
    return this.chunks.computeIfAbsent(pos, p -> new BedrockChunk(pos, this));
  }

  @Override
  public Region createRegion(RegionPosition pos) {
    return new VirtualBedrockRegion(pos, this);
  }

  @Override
  public HeightRange heightRange() {
    return new HeightRange(-64, 320);
  }

  @Override
  public RegionChangeWatcher createRegionChangeWatcher(WorldMapLoader worldMapLoader, MapView mapView) {
    return new RegionChangeWatcher(worldMapLoader, mapView, "the thread name") {
      @Override
      public void run() {

      }
    };
  }

  @Override
  public synchronized Region getRegion(RegionPosition pos) {
    // Unconditionally create virtual regions when requested, as bedrock has no concept of a region
    return regionMap.computeIfAbsent(pos, this::createRegion);
  }

  @Override
  public Region getRegionWithinRange(RegionPosition pos, HeightRange heightRange) {
    return getRegion(pos);
  }

  @Override
  public boolean hasRegion(RegionPosition pos) {
    return !(regionMap.get(pos) instanceof EmptyRegion);
  }

  @Override
  public String toString() {
    return "A Bedrock dimension"; // FIXME
  }

  @Override
  public boolean hasRegionWithinRange(RegionPosition regionPos, HeightRange heightRange) {
    return false;
  }
}
