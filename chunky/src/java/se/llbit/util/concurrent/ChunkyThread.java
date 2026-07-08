package se.llbit.util.concurrent;

import se.llbit.chunky.main.Chunky;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * {@link Thread}/{@link ExecutorService} resource management.
 * <h4>The goal of this glass is to primarily:</h4>
 * <ul>
 *   <li>Ensure all chunky threads (even daemons) are interrupted and given a chance clean up before getting killed by
 *       the runtime on termination.</li>
 *   <li>Within the non-termination {@link Runtime} shutdown sequence give guarantees to shut down hooks about the state
 *       of chunky.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <p>{@link Thread Threads} in chunky should extend this class, and {@link ExecutorService executor services} should
 * be created with {@link #addExecutorService(Function)} to allow chunky to interrupt and join them before chunky closes.</p>
 *
 */
public class ChunkyThread extends Thread {
  /*
   * All operations lock.
   * When interruptAndJoinAll is called, additional threads/executors can't be added preventing later joins from
   * waiting on threads that have not been interrupted.
   */
  private static final AtomicBoolean isShutdown = new AtomicBoolean(false);
  private static final Collection<Thread> threads = new ArrayList<>();
  private static final Collection<ExecutorService> executorServices = new ArrayList<>();

  /**
   * Add a {@link Thread} to be interrupted and joined by chunky on shutdown
   *
   * @throws IllegalStateException When calling after {@link #interruptAndJoinAll()} has been called.
   */
  public synchronized static <T extends Thread> T addThread(T thread) {
    if (isShutdown.get()) {
      throw new IllegalStateException("Creating a thread as chunky is stopping.");
    }
    threads.add(thread);
    return thread;
  }

  /**
   * Add an {@link ExecutorService} to be interrupted and joined by chunky on shutdown
   *
    @throws IllegalStateException When calling after {@link #interruptAndJoinAll()} has been called.
   */
  public synchronized static <E extends ExecutorService> E addExecutorService(Function<ThreadFactory, E> executorServiceSupplier) {
    if (isShutdown.get()) {
      throw new IllegalStateException("Creating an executor service as chunky is stopping.");
    }
    E e = executorServiceSupplier.apply(ChunkyThread::new);
    executorServices.add(e);
    return e;
  }

  /**
   * Await the joining of all threads managed by chunky
   *
   * <p>This method is always safe to call.</p>
   *
   * <p><b><i>WARNING: calling this from any thread registered with {@link #addThread(Thread)} may <u>deadlock</u>.</i></b></p>
   */
  public synchronized static void joinAll() {
    for (Thread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        // ignored
      }
    }
    for (ExecutorService executorService : executorServices) {
      try {
        executorService.awaitTermination(1, TimeUnit.MINUTES);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  /**
   * Interrupt and then await joining of all threads managed by chunky.
   *
   * <p>This method is always safe to call.</p>
   *
   * <p><b><i>WARNING: calling this from any thread registered with {@link #addThread(Thread)} may <u>deadlock</u>.</i></b></p>
   * <p>Only to be called by {@link Chunky}</p>
   */
  public synchronized static void interruptAndJoinAll() {
    assert Thread.currentThread().getName().equals("main");
    isShutdown.set(true);

    // shut down executorServices BEFORE threads because they recreate their threads when they are interrupted and stop
    // causing an infinite hang.
    for (ExecutorService executorService : executorServices) {
      executorService.shutdownNow();
    }

    for (Thread thread : ChunkyThread.threads) {
      thread.interrupt();
    }
    for (Thread thread : ChunkyThread.threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        // ignored
      }
    }
  }

  private void setDefaults() {
    this.setDaemon(true);
    addThread(this);
  }

  /* Constructors from super */
  public ChunkyThread() {
    super();
    setDefaults();
  }

  public ChunkyThread(Runnable task) {
    super(task);
    setDefaults();
  }

  public ChunkyThread(ThreadGroup group, Runnable task) {
    super(group, task);
    setDefaults();
  }

  public ChunkyThread(String name) {
    super(name);
    setDefaults();
  }

  public ChunkyThread(ThreadGroup group, String name) {
    super(group, name);
    setDefaults();
  }

  public ChunkyThread(Runnable task, String name) {
    super(task, name);
    setDefaults();
  }

  public ChunkyThread(ThreadGroup group, Runnable task, String name) {
    super(group, task, name);
    setDefaults();
  }

  public ChunkyThread(ThreadGroup group, Runnable task, String name, long stackSize) {
    super(group, task, name, stackSize);
    setDefaults();
  }

  public ChunkyThread(ThreadGroup group, Runnable task, String name, long stackSize, boolean inheritInheritableThreadLocals) {
    super(group, task, name, stackSize, inheritInheritableThreadLocals);
    setDefaults();
  }
}
