package se.llbit.chunky.world.bedrock;

import io.github.notstirred.leveldb_ffi.*;
import se.llbit.log.Log;

import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.Cleaner;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages {@link LevelDB} instances and prevents the same location from being opened in more than one place.
 *
 * <p>Callers are guaranteed that if they hold a {@link BedrockDB} it is not closed.</p> TODO: this isn't necessarily true during a shutdown hook
 *
 * <h2>Closure</h2>
 * <p>Callers can call {@link #close()} to release their hold on a DB. If this DB is being used elsewhere it will not be closed immediately.
 *
 * <h3>Automatic closure</h3>
 * <p>If a DB is not closed by the caller it will be cleaned up automatically at an unknown time later.</p>
 * <p>A DB is automatically closed on a non-termination stop of the JVM as according to the {@link Runtime Runtime's Shutdown Sequence}</p>
 */
public class BedrockDB implements Closeable {
  /*
   * This class keeps track of currently open LevelDB objects
   *
   * All operations (other than reading from a LevelDB) lock before starting.
   *
   */

  private static final Cleaner cleaner = Cleaner.create(); // TODO: move this to Chunky class or something usable by all.

  /*
   * A ReentrantLock is needed as getOrOpen locks, and acquire locks in the constructor which simplifies the impl a bit.
   */
  private static final ReentrantLock lock = new ReentrantLock();
  private static final Map<Path, DBRef> openDBs = new HashMap<>();

  private final DBRef ref;
  private Cleaner.Cleanable cleanable;

  /**
   * Only safe to call after ALL threads interacting with ALL dbs are stopped.
   */
  public static void closeAllDBs() {
    /*
     * In a situation where chunky is killed and this never runs the db state /should/ be fine.
     * - We never write to the DB (WAL will be empty).
     * - Crashes mid-compaction will be recovered by bedrock when opening the world.
     */
    Lock lock = BedrockDB.lock;
    try {
      lock.lock();
      openDBs.values().forEach(dbRef -> {
        try {
          dbRef.db.close(); // Don't need to free the arena as we're shutting down anyway.
          Log.info("Shutdown hook closed Bedrock DB " + dbRef.path.toString());
        } catch (Throwable t) {
          // Nothing we can do in the middle of closing.
        }
      });
    } finally {
      openDBs.clear();
      lock.unlock();
    }
  }

  private BedrockDB(DBRef ref) {
    ref.acquire();
    this.ref = ref;
  }

  /**
   * leveldb is thread safe for N readers so no synchronization required
   */
  public Optional<byte[]> get(ReadOptions options, byte[] key) throws LevelDBException {
    // It's always safe to call this without locking. This BedrockDB exists so the db shouldn't be closed.
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
   * Triggers db close immediately. If the db is already closed then invoking this method has no effect.
   */
  @Override
  public void close() throws IOException {
    this.cleanable.clean();
  }

  private static class DBRef {
    private final Path path;
    private final LevelDB db;
    private int references = 0;

    private DBRef(Path path, LevelDB db) {
      this.path = path;
      this.db = db;
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
