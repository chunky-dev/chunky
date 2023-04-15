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

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Performs rendering work.
 */
public class RenderWorkerPool {
  public interface Factory {
    RenderWorkerPool create(int threads, long seed);
  }

  public static class RenderWorker extends Thread {
    private final RenderWorkerPool pool;

    public final Random random;
    public final int id;

    public RenderWorker(RenderWorkerPool pool, int id, long seed) {
      super("3D Render Worker " + id);

      this.pool = pool;
      this.id = id;
      this.random = new Random(seed);
    }

    @Override
    public void run() {
      try {
        while (!isInterrupted()) pool.work(this);
      } catch (InterruptedException e) {
        // Interrupted
      } catch (Throwable e) {
        Log.error("Render worker " + id + " crashed with uncaught exception.", e);
      }
    }
  }

  public final int threads;

  private final ConcurrentLinkedQueue<RenderJobFuture> workQueue = new ConcurrentLinkedQueue<>();
  private final AtomicInteger progress = new AtomicInteger(0);
  private final AtomicInteger localProgress = new AtomicInteger(0);

  protected final RenderWorker[] workers;

  public RenderWorkerPool(int threads, long seed) {
    this.threads = threads;

    workers = new RenderWorker[threads];
    for (int i = 0; i < threads; i++) {
      workers[i] = new RenderWorker(this, i, seed + i);
      workers[i].start();
    }
  }

  private void work(RenderWorker worker) throws Throwable {
    synchronized (workQueue) {
      while (workQueue.isEmpty()) {
        workQueue.wait();
      }
    }

    RenderJobFuture task = workQueue.poll();
    if (task == null) return;
    task.task.accept(worker);
    task.finished();

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
    for (RenderWorker worker : workers) {
      worker.interrupt();
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
