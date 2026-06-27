package se.llbit.chunky.world.bedrock;

import io.github.notstirred.leveldb_ffi.*;

import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.Cleaner;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BedrockDB implements Closeable {
  /*
   * This class uses reference counting and cleaners to prevent the same DB from being opened by two different bedrock dimensions.
   *
   * Minecraft doesn't use lock files so we can't either.
   */

  private static final Cleaner cleaner = Cleaner.create(); // TODO: move this to Chunky class or something usable by all.

  private static final Lock lock = new ReentrantLock();
  private static final Map<Path, DBRef> openDBs = new HashMap<>();

  private final DBRef ref;
  private Cleaner.Cleanable cleanable;

  private BedrockDB(DBRef ref) {
    ref.acquire();
    this.ref = ref;
  }

  /**
   * leveldb is thread safe for N readers so no synchronization required
   */
  public Optional<byte[]> get(ReadOptions options, byte[] key) throws LevelDBException {
    // It's always safe to call this without locking. This BedrockDB exists so the db can't be closed.
    return this.ref.db.get(options, key);
  }

  public static BedrockDB getOrOpen(Path dbPath) {
    Lock lock = BedrockDB.lock;
    try {
      lock.lock();

      Options options = Options.create();
      options.setCompression(Compressor.ZLIB_RAW);
      options.setCreateIfMissing(false);
      try {
        DBRef dbRef = openDBs.get(dbPath);
        if (dbRef != null) {
          return new BedrockDB(dbRef);
        }

        DBRef ref = new DBRef(dbPath,
          LevelDB.open(options, dbPath.toAbsolutePath().toString())
        );
        openDBs.put(dbPath, ref);

        BedrockDB bedrockDB = new BedrockDB(ref);
        bedrockDB.cleanable = cleaner.register(bedrockDB, ref::release);

        return bedrockDB;
      } catch (LevelDBException e) {
        throw new RuntimeException(e);
      }
    } finally {
      lock.unlock();
    }
  }

  /**
   * Optional, triggers db close faster.
   */
  @Override
  public void close() throws IOException {
    this.cleanable.clean();
  }

  private static class DBRef {
    private final Path path;
    private final LevelDB db;
    private int references;

    private DBRef(Path path, LevelDB db) {
      this.path = path;
      this.db = db;
      this.references = 0;
    }

    private void acquire() {
      Lock lock = BedrockDB.lock;
      try {
        lock.lock();
        this.references++;
      } finally {
        lock.unlock();
      }
    }

    private void release() {
      Lock lock = BedrockDB.lock;
      try {
        lock.lock();
        references--;
        if (references == 0) {
          DBRef removed = openDBs.remove(this.path);
          removed.db.close();
        }
      } finally {
        lock.unlock();
      }
    }
  }
}
