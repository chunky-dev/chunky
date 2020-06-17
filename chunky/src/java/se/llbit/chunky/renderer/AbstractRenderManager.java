/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.scene.RayTracer;
import se.llbit.chunky.renderer.scene.Scene;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Base class for render managers.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public abstract class AbstractRenderManager extends Thread {

  @FunctionalInterface
  public interface WorkerFactory {
    RenderWorker buildWorker(AbstractRenderManager renderer, int index, long seed);
  }

  protected final WorkerFactory workerFactory;
  protected SceneProvider sceneProvider;
  private RayTracer previewRayTracer;
  private RayTracer rayTracer;
  protected ArrayBlockingQueue<RenderTask> jobQueue;
  private CyclicBarrier barrier;

  public AbstractRenderManager(RenderContext context) {
    super("Render Manager");

    this.numThreads = context.numRenderThreads();
    this.tileWidth = context.tileWidth();
    this.previewRayTracer = context.getChunky().getPreviewRayTracerFactory().newRayTracer();
    this.rayTracer = context.getChunky().getRayTracerFactory().newRayTracer();
    this.workerFactory = context.workerFactory;

    this.jobQueue = new ArrayBlockingQueue<>(numThreads * 2);
    this.barrier = new CyclicBarrier(numThreads + 1); // the +1 is the RenderManager itself
  }

  /**
   * Number of render threads
   */
  protected int numThreads;

  /**
   * Tile width
   */
  protected final int tileWidth;

  /**
   * CPU load percentage.
   */
  public int cpuLoad = PersistentSettings.getCPULoad();

  /**
   * Get a job from the job queue. The job describes the
   * next tile to be rendered.
   *
   * @return description of tile to be rendered.
   * @throws InterruptedException
   */
  public final RenderTask getNextJob() throws InterruptedException {
    return jobQueue.take();
  }

  /**
   * Synchronize with the RenderManager. Called by RenderWorkers to signal all jobs are done.
   * @throws BrokenBarrierException
   * @throws InterruptedException
   */
  public final void awaitEndOfFrame() throws BrokenBarrierException, InterruptedException {
    barrier.await();
  }

  /**
   * Queues up END_FRAME tasks and wait for all workers to reach the barrier
   * @throws InterruptedException
   * @throws BrokenBarrierException
   */
  protected final void waitOnWorkers() throws InterruptedException, BrokenBarrierException {
    for (int i = 0; i < numThreads; ++i) {
      jobQueue.put(RenderTask.END_FRAME);
    }
    barrier.await();
  }

  public void setSceneProvider(SceneProvider sceneProvider) {
    this.sceneProvider = sceneProvider;
  }

  public abstract Scene getBufferedScene();

  public RayTracer getPreviewRayTracer() {
    return previewRayTracer;
  }

  public RayTracer getRayTracer() {
    return rayTracer;
  }
}
