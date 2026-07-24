package se.llbit.util.concurrent;

import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.util.annotation.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * {@link Thread}/{@link ExecutorService} resource management.
 * <h4>The goal of this class is to primarily:</h4>
 * <ul>
 *   <li>Ensure all chunky threads (even daemons) are interrupted and given a chance clean up before getting killed by
 *       the runtime on termination.</li>
 *   <li>Within the non-termination {@link Runtime} shutdown sequence give guarantees to shut down hooks about the state
 *       of chunky.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <ul>
 *   <li>{@link Thread Threads} in chunky should extend this class</li>
 *   <li>{@link ExecutorService Executor Services} should be created with {@link #addExecutorService(Function)}</li>
 *   <li>{@link ForkJoinPool Fork Join Pools} should be created with {@link #addForkJoinPool(ForkJoinPool)}</li>
 * </ul>
 *
 */
public class ChunkyThread extends Thread {
  private static final CountDownLatch shutdownLatch = new CountDownLatch(1);
  private static final Collection<Thread> threads = new ArrayList<>();
  private static final Collection<ExecutorService> executorServices = new ArrayList<>();

  /**
   * Add a {@link Thread} to be interrupted and joined by chunky on shutdown
   *
   * @throws IllegalStateException When calling after {@link #interruptAndJoinAll(long, TimeUnit)} has been called.
   */
  @PluginApi
  public synchronized static <T extends Thread> T addThread(T thread) {
    if (shutdownLatch.getCount() == 0) {
      throw new IllegalStateException("Creating a thread as chunky is stopping.");
    }
    threads.add(thread);
    return thread;
  }

  /**
   * Add an {@link ExecutorService} to be interrupted and joined by chunky on shutdown
   *
   * @throws IllegalStateException When calling after {@link #interruptAndJoinAll(long, TimeUnit)} has been called.
   */
  @PluginApi
  public synchronized static <E extends ExecutorService> E addExecutorService(Function<ThreadFactory, E> executorServiceSupplier) {
    if (shutdownLatch.getCount() == 0) {
      throw new IllegalStateException("Creating an executor service as chunky is stopping.");
    }
    E e = executorServiceSupplier.apply(r -> { // executor shutdown interrupts its own threads, so they don't need to be ChunkyThreads
      Thread t = new Thread(r);
      t.setDaemon(true);
      return t;
    });
    executorServices.add(e);
    return e;
  }

  /**
   * Add a {@link ForkJoinPool} to be interrupted and joined by chunky on shutdown
   *
   * @throws IllegalStateException When calling after {@link #interruptAndJoinAll(long, TimeUnit)} has been called.
   */
  @PluginApi
  public synchronized static ForkJoinPool addForkJoinPool(ForkJoinPool pool) {
    if (shutdownLatch.getCount() == 0) {
      throw new IllegalStateException("Creating a fork join pool as chunky is stopping.");
    }
    executorServices.add(pool);
    return pool;
  }

  /**
   * Await the joining of all threads managed by chunky. This method will <b><u>wait indefinitely</u></b> until a
   * shutdown happens to begin its timeout.
   *
   * <p>This method is always safe to call.</p>
   *
   * <p><b><i>WARNING: calling this from any thread registered with {@link #addThread(Thread)} may <u>deadlock</u>.</i></b></p>
   *
   * @param timeout The maximum time to wait <b><u>AFTER</u></b> a shutdown is initiated
   * @param unit the time unit of the timeout argument
   *
   * @return Whether all threads were joined before returning
   */
  public static boolean joinAll(long timeout, @NotNull TimeUnit unit) {
    /*
     * This method should not be synchronized because:
     *   1. Calls to this method that happen before interruptAndJoinAll will lock the latter interrupting thread, deadlocking.
     *   2. shutdownLatch.await is at least acquire memory ordering, and modification is disabled after the latch is zero.
     *      As such we are guaranteed that no threads can modify the state.
     */
    boolean interrupted = false;

    while (true) {
      try {
        // Must wait for the latch as hitting the for loop below first causes immediate evaluation of:
        // - The for loop iterator, potentially missing new threads.
        // - The end time, meaning waiting starts before shutdown begins.
        shutdownLatch.await();
        break;
      } catch (InterruptedException e) {
        interrupted = true;
      }
    }

    long endTime = System.nanoTime() + unit.toNanos(timeout);
    try {
      while (System.nanoTime() < endTime) {
        try {
          if (joinAllInterruptable(endTime)) {
            // All threads are joined, skip the rest of the wait time.
            return true;
          }
        } catch (InterruptedException e) {
          interrupted = true;
        }
      }
      // Got to the end of the wait time without joining everything, can give no guarantees
      return false;
    } finally {
      if (interrupted) {
        Thread.currentThread().interrupt();
      }
    }
  }

  /**
   * Interrupt and then await joining of all threads managed by chunky.
   *
   * <p><b><i>WARNING: calling this from any thread registered with {@link #addThread(Thread)} may <u>deadlock</u>.</i></b></p>
   * <p>Only to be called by {@link Chunky}</p>
   *
   * @param timeout The maximum time to wait
   * @param unit the time unit of the timeout argument
   * @return Whether all threads were joined before the time limit was reached
   */
  public static boolean interruptAndJoinAll(long timeout, @NotNull TimeUnit unit) {
    synchronized (ChunkyThread.class) {
      shutdownLatch.countDown();

      for (ExecutorService executorService : executorServices) {
        executorService.shutdownNow();
      }
      for (Thread thread : ChunkyThread.threads) {
        thread.interrupt();
      }
    }

    return joinAll(timeout, unit);
  }

  /**
   * Await the joining of all threads managed by chunky.
   *
   * <p>This method is only safe to call if the {@link ChunkyThread#shutdownLatch} has been set.</p>
   *
   * <p><b><i>WARNING: calling this from any thread registered with {@link #addThread(Thread)} may <u>deadlock</u>.</i></b></p>
   *
   * @param endTimeNanos The time at which to stop waiting.
   *
   * @return Whether all threads were joined before returning
   *
   * @throws InterruptedException Propagates up when interrupted. The caller has no guarantee that shutdown has begun,
   *                              or that any of the inner threads have been joined.
   */
  private static boolean joinAllInterruptable(long endTimeNanos) throws InterruptedException {
    // The intention here whether we return true or false, is to give the caller the most complete acquire load possible.
    // Even if we reach the timeout given by the caller, we still establish a happens-before with every dead thread.

    boolean anyAlive = false;
    for (ExecutorService executorService : executorServices) {
      long waitTime = endTimeNanos - System.nanoTime();
      anyAlive |= !executorService.awaitTermination(waitTime, TimeUnit.NANOSECONDS);
    }
    for (Thread thread : threads) {
      long waitTime = endTimeNanos - System.nanoTime();
      if (waitTime > 0) {
        thread.join(waitTime); // joining with 0 is infinite wait time, very intuitive.
      }
      // Thread.isAlive() establishes a happens-before with the thread. As such the following are non-issues:
      // - Not joining the thread, if waitTime <= 0
      // - The thread stopping between Thread.join() and Thread.isAlive().
      anyAlive |= thread.isAlive();
    }

    return !anyAlive;
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
