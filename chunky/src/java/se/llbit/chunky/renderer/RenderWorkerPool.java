/* Copyright (c) 2012-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2012-2021 Chunky contributors
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
package se.llbit.chunky.renderer;

import se.llbit.log.Log;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Performs rendering work.
 */
public class RenderWorkerPool {

  /**
   * Sleep interval (in ms).
   */
  private static final int MIN_SLEEP_TIME = 100;
  private static final long MAX_SLEEP_TIME = 1000;

  public interface Factory {
    RenderWorkerPool create(int threads, long seed);
  }

  public static class RenderWorker extends Thread {
    private final RenderWorkerPool pool;

    public final Random random;
    public final int id;
    public final WorkerState state = new WorkerState();

    private long lastSleep;
    private long sleepTime = 0;

    private boolean running = true;

    public RenderWorker(RenderWorkerPool pool, int id, long seed) {
      super("3D Render Worker " + id);

      this.pool = pool;
      this.id = id;
      this.random = new Random(seed);
      state.random = this.random;

      lastSleep = System.currentTimeMillis();
    }

    public void shutdown() {
      this.running = false;
    }

    /**
     * Sleep to manage CPU usage.
     */
    public void workSleep() throws InterruptedException {
      if (pool.cpuLoad < 100) {
        long workTime = System.currentTimeMillis() - lastSleep;
        double load = (100.0 - pool.cpuLoad) / pool.cpuLoad;
        double sleepTime = workTime * load;

        if (sleepTime > MIN_SLEEP_TIME) {
            Thread.sleep(Math.min((long) sleepTime, MAX_SLEEP_TIME));
            lastSleep = System.currentTimeMillis();
        }
      }
    }

    /**
     * Reset the sleep interval.
     */
    public void resetSleep() {
      lastSleep = System.currentTimeMillis();
    }

    /**
     * Pause the sleep interval.
     */
    public void pauseSleep() {
      sleepTime += System.currentTimeMillis() - lastSleep;
    }

    /**
     * Resume the sleep interval.
     */
    public void resumeSleep() {
      lastSleep = System.currentTimeMillis() - sleepTime;
      sleepTime = 0;
    }

    @Override
    public void run() {
      try {
        while (!isInterrupted() && running) pool.work(this);
      } catch (InterruptedException e) {
        // Interrupted
      } catch (Throwable e) {
        Log.error("Render worker " + id + " crashed with uncaught exception.", e);
      }
    }
  }

  private volatile int cpuLoad = 100;

  private final ConcurrentLinkedQueue<RenderJobFuture> workQueue = new ConcurrentLinkedQueue<>();
  private final AtomicInteger progress = new AtomicInteger(0);
  private final AtomicInteger localProgress = new AtomicInteger(0);

  protected final ArrayList<RenderWorker> workers = new ArrayList<>();

  private long seed;
  private int workerId = 0;

  public RenderWorkerPool(int threads, long seed) {
    this.seed = seed;
    setThreadCount(threads);
  }

  /**
   * Get an approximation of the number workers in this pool.
   */
  public int getThreadCount() {
    return workers.size();
  }

  /**
   * Set the number of workers in this pool. Will add / kill workers in order to reach
   * the number desired.
   */
  public void setThreadCount(int threads) {
    // Must have at least 1 thread
    if (threads < 1) threads = 1;

    synchronized (workers) {
      // Kill off some workers
      while (workers.size() > threads) {
        RenderWorker worker = workers.remove(workers.size()-1);
        worker.shutdown();
      }

      // Not enough workers
      while (workers.size() < threads) {
        RenderWorker worker = new RenderWorker(this, workerId++, this.seed++);
        workers.add(worker);
        worker.start();
      }
    }
  }

  private void work(RenderWorker worker) throws Throwable {
    worker.pauseSleep();
    synchronized (workQueue) {
      if (workQueue.isEmpty()) {
        workQueue.wait();
      }
    }
    worker.resumeSleep();

    RenderJobFuture task = workQueue.poll();
    if (task == null) return;
    task.task.accept(worker);
    task.finished();

    worker.workSleep();

    progress.incrementAndGet();
    synchronized (progress) { progress.notifyAll(); }
  }

  public RenderJobFuture submit(RenderJob task) {
    RenderJobFuture future = new RenderJobFuture(task);
    workQueue.add(future);
    synchronized (workQueue) { workQueue.notifyAll(); }
    localProgress.incrementAndGet();
    return future;
  }

  /**
   * Set the cpu load. The pools will attempt (not guaranteed) to limit cpu usage to this value.
   * @param cpuLoad percentage of cpu usage, will be clamped to [1..100]
   */
  public void setCpuLoad(int cpuLoad) {
    // Clamp to 1-100
    this.cpuLoad = Math.max(Math.min(cpuLoad, 100), 1);
  }

  /**
   * Wait for the pool to become empty.
   */
  public void awaitEmpty() throws InterruptedException {
    synchronized (progress) {
      while (progress.get() != localProgress.get()) {
        progress.wait();
      }
    }
  }

  public void interrupt() {
    synchronized (workers) {
      // This will kill all alive workers
      workers.forEach(RenderWorker::interrupt);

      // This will wake up and kill any zombie workers
      synchronized (workQueue) {
        workQueue.notifyAll();
      }
    }
  }

  /**
   * The future to a job. It may be waited on to finish with {@code awaitFinish}.
   */
  public static class RenderJobFuture {
    private volatile boolean done = false;
    protected final RenderJob task;

    protected RenderJobFuture(RenderJob task) {
      this.task = task;
    }

    protected synchronized void finished() {
      this.done = true;
      this.notifyAll();
    }

    public boolean isDone() {
      return done;
    }

    public void awaitFinish() throws InterruptedException {
      if (done) return;
      synchronized (this) {
        while (!done) {
          this.wait();
        }
      }
    }
  }

  @FunctionalInterface
  public interface RenderJob {
    /**
     * @param worker      The render worker running this job.
     * @throws Throwable  Any unchecked exception.
     */
    void accept(RenderWorker worker) throws Throwable;
  }
}
