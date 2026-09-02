package se.llbit.chunky.world.bedrock;

import io.github.notstirred.leveldb_ffi.*;
import se.llbit.log.Log;

import java.lang.foreign.Arena;
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
 * <h2>Automatic closure</h2>
 * <p>DBs are cleaned up automatically at an unknown time after all references are dropped.</p>
 * <p>All DBs are automatically closed on a non-termination stop of the JVM as according to the {@link Runtime Runtime's Shutdown Sequence}</p>
 */
public class BedrockDB {
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
          dbRef.arena = null; // null to prevent cleaner double free through leveldb_ffi_close
          dbRef.db = null;
          Log.info("Closed Bedrock DB on shutdown " + dbRef.path.toString());
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

      Arena arena = Arena.ofShared();

      Options options = Options.create(arena);
      options.setCompression(Compressor.ZLIB_RAW);
      options.setCreateIfMissing(false);

      DBRef dbRef = openDBs.get(dbPath);
      if (dbRef != null) {
        Log.info("Reused open Bedrock DB " + dbRef.path.toString());
        return new BedrockDB(dbRef);
      }

      LevelDB db;
      try {
        db = LevelDB.open(arena, options, dbPath.toAbsolutePath().toString());
      } catch (LevelDBException e) {
        arena.close(); // something threw, we are responsible for the arena
        throw new RuntimeException(e);
      }

      DBRef ref = new DBRef(dbPath, db, arena); // DBRef is responsible for the arena
      DBRef existing = openDBs.put(dbPath, ref);
      assert existing == null;

      BedrockDB bedrockDB = new BedrockDB(ref);
      cleaner.register(bedrockDB, ref::release);

      Log.info("Opened Bedrock DB " + ref.path.toString());
      return bedrockDB;
    } finally {
      lock.unlock();
    }
  }

  /**
   * @return An arena whose lifetime is at least as long as the underlying {@link LevelDB}
   */
  public Arena getArena() {
    return this.ref.arena;
  }

  /**
   * Holds leveldb references and frees them when appropriate.
   *
   * <p>It is only safe for {@link DBRef#references} to reach zero when no more references to this {@link DBRef} exist.</p>
   * <p>As such callers must only call {@link DBRef#release()} from a {@link Cleaner}'s cleanup action</p>
   */
  private static class DBRef {
    private final Path path;
    private LevelDB db;
    private Arena arena;
    private int references = 0;

    /**
     * @param path The path to the db
     * @param db   The db
     * @param arena <u>Must</u> be an arena that supports {@link Arena#close()}.
     *              The lifetime of the {@link LevelDB db} <u>must</u> be tied to the arena.
     */
    private DBRef(Path path, LevelDB db, Arena arena) {
      this.path = path;
      this.db = db;
      this.arena = arena;
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
        assert references > 0;
        references--;
        if (references == 0) {
          DBRef removed = openDBs.remove(this.path);
          assert this == removed;
          // We are not concerned with what the chunky threads are doing here (different to the shutdown hook)
          // Here we are guaranteed that no reference to this DBRef exist, so we instantly close.
          removed.arena.close();
          removed.db = null; // db lifetime is tied to arena.
          removed.arena = null;
          Log.info("Closed Bedrock DB " + this.path.toString());
        }
      } finally {
        lock.unlock();
      }
    }
  }
}
