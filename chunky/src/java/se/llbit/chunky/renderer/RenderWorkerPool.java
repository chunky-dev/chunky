/* Copyright (c) 2012-2019 Jesper Öqvist <jesper@llbit.se>
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
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Performs rendering work.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class RenderWorkerPool {

  /**
   * Sleep interval (in ms).
   */
  private static final int SLEEP_INTERVAL = 10;
  private static final long MAX_SLEEP_TIME = 1000;

  public interface Factory {
    RenderWorkerPool create(int threads, long seed);
  }

  public static class RenderWorker extends Thread {
    private final RenderWorkerPool pool;

    public final Random random;
    public final int id;

    private long lastSleep;

    public RenderWorker(RenderWorkerPool pool, int id, long seed) {
      super("3D Render Worker " + id);

      this.pool = pool;
      this.id = id;
      this.random = new Random(seed);

      lastSleep = System.currentTimeMillis();
    }

    public void workSleep() throws InterruptedException {
      long workTime = System.currentTimeMillis() - lastSleep;
      if (workTime > SLEEP_INTERVAL) {
        if (pool.cpuLoad < 100) {
          double load = (100.0 - pool.cpuLoad) / pool.cpuLoad;
          sleep(Math.min((long) (workTime * load), MAX_SLEEP_TIME));
        }
        lastSleep = System.currentTimeMillis();
      }
    }

    protected void resetSleep() {
      lastSleep = System.currentTimeMillis();
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

  private volatile int cpuLoad = 100;

  private final ConcurrentLinkedQueue<Consumer<RenderWorker>> workQueue = new ConcurrentLinkedQueue<>();
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

  private void work(RenderWorker worker) throws InterruptedException {
    synchronized (workQueue) {
      while (workQueue.isEmpty()) {
        workQueue.wait();
      }
    }

    worker.resetSleep();

    Consumer<RenderWorker> work = workQueue.poll();
    if (work == null) return;
    work.accept(worker);

    worker.workSleep();

    progress.incrementAndGet();
    synchronized (progress) { progress.notifyAll(); }
  }

  public void submit(Consumer<RenderWorker> task) {
    workQueue.add(task);
    synchronized (workQueue) { workQueue.notifyAll(); }
    localProgress.incrementAndGet();
  }

  public void setCpuLoad(int cpuLoad) {
    // Clamp to 1-100
    this.cpuLoad = Math.max(Math.min(cpuLoad, 100), 1);
  }

  public void awaitEmpty() throws InterruptedException {
    synchronized (progress) {
      while (progress.get() != localProgress.get()) {
        progress.wait();
      }
    }
  }

  public void interrupt() {
    Arrays.stream(workers).forEach(Thread::interrupt);
  }
}
