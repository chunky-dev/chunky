/* Copyright (c) 2012-2016 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.log.Log;
import se.llbit.util.TaskTracker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This class manages render workers. Each worker renders one tile at a time,
 * and the render manager ensures that each worker is assigned unique tiles.
 *
 * <p>The secondary purpose of the render manager is to manage the scene state
 * which the workers use.
 *
 * <p>Scene state is kept in Scene objects. The render controls dialog
 * stores the scene state in its own Scene object, which is read by
 * the render manager through the SceneProvider interface.
 * The render manager keeps an internal copy of the scene state which
 * is ensured to be unmodified while render workers are rendering.
 *
 * <p>A snapshot of the current render can be accessed by calling withSnapshot().
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class RenderManager extends AbstractRenderManager implements Renderer {
  public static final Repaintable EMPTY_CANVAS = () -> {};

  private boolean finalizeAllFrames = false;

  private Repaintable canvas = EMPTY_CANVAS;

  private Thread[] workers = {};

  /**
   * This scene state is used by render workers while rendering.
   * The buffered scene is only updated when the workers are
   * quiescent.
   */
  private final Scene bufferedScene;

  /** Gives the next tile index for a worker. */
  private volatile RenderTile[] tileQueue = new RenderTile[0];
  private final Object jobMonitor = new Object();
  private int numJobs = 0, lastJob = 0;
  private final AtomicInteger nextJob = new AtomicInteger(0);

  /** Latch for waiting on workers to finish the current frame. */
  private CountDownLatch frameFinished = new CountDownLatch(Integer.MAX_VALUE);

  private Collection<RenderStatusListener> listeners = new ArrayList<>();

  private BiConsumer<Long, Integer> renderCompletionListener = (time, sps) -> {};
  private BiConsumer<Scene, Integer> frameCompletionListener = (scene, spp) -> {};
  private TaskTracker.Task renderTask = TaskTracker.Task.NONE;

  /**
   * Decides if render threads shut down after reaching the target SPP.
   */
  private final boolean headless;

  /**
   * Current renderer mode.
   */
  private RenderMode mode = RenderMode.PREVIEW;

  private final Collection<SceneStatusListener> sceneListeners = new ArrayList<>();

  private SnapshotControl snapshotControl = SnapshotControl.DEFAULT;

  /**
   * @param headless {@code true} if rendering threads should be shut
   * down after reaching the render target.
   */
  public RenderManager(RenderContext context, boolean headless) {
    super(context);

    this.headless = headless;
    bufferedScene = context.getChunky().getSceneFactory().newScene();

    long seed = System.currentTimeMillis();
    workers = new Thread[numThreads];
    for (int i = 0; i < numThreads; ++i) {
      workers[i] = workerFactory.buildWorker(this, i, seed + i);
      workers[i].start();
    }
  }

  @Override public synchronized void addRenderListener(RenderStatusListener listener) {
    listeners.add(listener);
  }

  @Override public void removeRenderListener(RenderStatusListener listener) {
    listeners.remove(listener);
  }

  @Override public void setCanvas(Repaintable canvas) {
    this.canvas = canvas;
  }

  @Override public void run() {
    try {
      while (!isInterrupted()) {
        ResetReason reason = sceneProvider.awaitSceneStateChange();

        synchronized (bufferedScene) {
          sceneProvider.withSceneProtected(scene -> {
            if (reason.overwriteState()) {
              bufferedScene.copyState(scene);
            }
            if(reason == ResetReason.MATERIALS_CHANGED || reason == ResetReason.SCENE_LOADED) {
              scene.importMaterials();
            }
            bufferedScene.copyTransients(scene);
            finalizeAllFrames = scene.shouldFinalizeBuffer();
            updateRenderState(scene);
            if (reason == ResetReason.SCENE_LOADED) {
              // Swap buffers so the render canvas will see the current frame.
              bufferedScene.swapBuffers();

              // Notify the scene listeners (this triggers a canvas repaint).
              sendSceneStatus(bufferedScene.sceneStatus());
            }
          });
        }
        initializeJobQueue();

        if (mode == RenderMode.PREVIEW) {
          previewLoop();
        } else {
          int spp, targetSpp;
          synchronized (bufferedScene) {
            spp = bufferedScene.spp;
            targetSpp = bufferedScene.getTargetSpp();
            if (spp < targetSpp) {
              updateRenderProgress();
            }
          }
          if (spp < targetSpp) {
            pathTraceLoop();
          } else {
            sceneProvider.withEditSceneProtected(scene -> {
              scene.pauseRender();
              updateRenderState(scene);
            });
          }
        }

        if (headless) {
          break;
        }
      }
    } catch (InterruptedException e) {
      // 3D view was closed.
    } catch (Throwable e) {
      Log.error("Unchecked exception in render manager", e);
    }

    stopWorkers();
  }

  private void updateRenderState(Scene scene) {
    finalizeAllFrames = scene.shouldFinalizeBuffer();
    if (mode != scene.getMode()) {
      mode = scene.getMode();
      // TODO: make render state update faster by moving this to Scene?
      listeners.forEach(listener -> listener.renderStateChanged(mode));
    }
  }

  /**
   * Continually render frames until we reach the SPP target, or until
   * the render state is changed externally.
   * @throws InterruptedException
   */
  private void pathTraceLoop() throws InterruptedException {
    while (true) {
      sceneProvider.withSceneProtected(scene -> {
        synchronized (bufferedScene) {
          bufferedScene.copyTransients(scene);
          updateRenderState(scene);
        }
      });

      if (mode == RenderMode.PAUSED || sceneProvider.pollSceneStateChange()) {
        return;
      }

      synchronized (bufferedScene) {
        long frameStart = System.currentTimeMillis();
        startNextFrame();
        waitOnWorkers();
        bufferedScene.swapBuffers();
        bufferedScene.renderTime += System.currentTimeMillis() - frameStart;
      }

      // Notify the canvas to repaint.
      canvas.repaint();

      synchronized (bufferedScene) {
        bufferedScene.spp += RenderConstants.SPP_PER_PASS;
        int currentSpp = bufferedScene.spp;
        frameCompletionListener.accept(bufferedScene, currentSpp);
        updateRenderProgress();
        if (currentSpp >= bufferedScene.getTargetSpp()) {
          renderCompletionListener.accept(bufferedScene.renderTime, samplesPerSecond());
          return;
        }
      }
    }
  }

  /**
   * @return the current rendering speed in samples per second (SPS)
   */
  private int samplesPerSecond() {
    int canvasWidth = bufferedScene.canvasWidth();
    int canvasHeight = bufferedScene.canvasHeight();
    long pixelsPerFrame = canvasWidth * canvasHeight;
    double renderTime = bufferedScene.renderTime / 1000.0;
    return (int) ((bufferedScene.spp * pixelsPerFrame) / renderTime);
  }

  private void updateRenderProgress() {
    double renderTime = bufferedScene.renderTime / 1000.0;

    // Notify progress listener.
    int target = bufferedScene.getTargetSpp();
    long etaSeconds = (long) (((target - bufferedScene.spp) * renderTime) / bufferedScene.spp);
    if (etaSeconds > 0) {
      int seconds = (int) ((etaSeconds) % 60);
      int minutes = (int) ((etaSeconds / 60) % 60);
      int hours = (int) (etaSeconds / 3600);
      String eta = String.format("%d:%02d:%02d", hours, minutes, seconds);
      renderTask.update("Rendering", target, bufferedScene.spp, eta);
    } else {
      renderTask.update("Rendering", target, bufferedScene.spp, "");
    }

    synchronized (this) {
      // Update render status display.
      listeners.forEach(listener -> {
        listener.setRenderTime(bufferedScene.renderTime);
        listener.setSamplesPerSecond(samplesPerSecond());
        listener.setSpp(bufferedScene.spp);
      });
    }
  }

  private void previewLoop() throws InterruptedException {
    long frameStart;

    renderTask.update("Preview", 2, 0, "");
    synchronized (bufferedScene) {
      bufferedScene.previewCount = 2;
    }

    while (true) {
      int previewCount;
      synchronized (bufferedScene) {
        previewCount = bufferedScene.previewCount;
      }
      if (!finalizeAllFrames || previewCount <= 0 || sceneProvider.pollSceneStateChange()) {
        return;
      }

      int progress;
      long renderTime;
      synchronized (bufferedScene) {
        frameStart = System.currentTimeMillis();
        startNextFrame();
        waitOnWorkers();
        bufferedScene.swapBuffers();
        sendSceneStatus(bufferedScene.sceneStatus());
        bufferedScene.renderTime += System.currentTimeMillis() - frameStart;
        bufferedScene.previewCount -= 1;
        bufferedScene.spp = 0;
        progress = 2 - bufferedScene.previewCount;
        renderTime = bufferedScene.renderTime;
      }

      // Update render status display.
      listeners.forEach(listener -> {
        listener.setRenderTime(renderTime);
        listener.setSamplesPerSecond(0);
        listener.setSpp(0);
      });

      // Update render progress.
      renderTask.update("Preview", 2, progress, "");

      // Notify the canvas to repaint.
      canvas.repaint();
    }
  }

  /**
   * Assign render jobs to tiles of the canvas.
   */
  private void initializeJobQueue() {
    int canvasWidth = bufferedScene.canvasWidth();
    int canvasHeight = bufferedScene.canvasHeight();
    numJobs = ((canvasWidth + (tileWidth - 1)) / tileWidth)
        * ((canvasHeight + (tileWidth - 1)) / tileWidth);
    if (tileQueue.length != numJobs) {
      tileQueue = new RenderTile[numJobs];
    }
    int xjobs = (canvasWidth + (tileWidth - 1)) / tileWidth;
    for (int job = 0; job < numJobs; ++job) {
      // Calculate pixel bounds for this job.
      int x0 = tileWidth * (job % xjobs);
      int x1 = Math.min(x0 + tileWidth, canvasWidth);
      int y0 = tileWidth * (job / xjobs);
      int y1 = Math.min(y0 + tileWidth, canvasHeight);
      tileQueue[job] = new RenderTile(x0, x1, y0, y1);
    }
  }

  private void waitOnWorkers() throws InterruptedException {
    frameFinished.await();
  }

  /**
   * Adds new jobs to the job queue and releases the workers.
   */
  private void startNextFrame() {
    int nextSpp = bufferedScene.spp + RenderConstants.SPP_PER_PASS;
    bufferedScene.setBufferFinalization(finalizeAllFrames
        || snapshotControl.saveSnapshot(bufferedScene, nextSpp));
    frameFinished = new CountDownLatch(numJobs);
    synchronized (jobMonitor) {
      lastJob += numJobs;
      jobMonitor.notifyAll();
    }
  }

  @Override public RenderTile getNextJob() throws InterruptedException {
    int job = nextJob.getAndIncrement();
    if (job >= lastJob) {
      synchronized (jobMonitor) {
        while (job >= lastJob) {
          jobMonitor.wait();
        }
      }
    }
    return tileQueue[lastJob - job - 1];
  }

  @Override public void jobDone() {
    frameFinished.countDown();
  }

  @Override public Scene getBufferedScene() {
    return bufferedScene;
  }

  /**
   * Call the consumer with the current front frame buffer.
   */
  @Override public void withBufferedImage(Consumer<BitmapImage> consumer) {
    bufferedScene.withBufferedImage(consumer);
  }

  @Override public void setOnRenderCompleted(BiConsumer<Long, Integer> listener) {
    renderCompletionListener = listener;
  }

  @Override public void setOnFrameCompleted(BiConsumer<Scene, Integer> listener) {
    frameCompletionListener = listener;
  }

  @Override public void setSnapshotControl(SnapshotControl callback) {
    this.snapshotControl = callback;
  }

  @Override public void setRenderTask(TaskTracker.Task task) {
    renderTask = task;
  }

  /**
   * Set CPU load percentage.
   *
   * @param value new load percentage.
   */
  @Override public void setCPULoad(int value) {
    cpuLoad = value;
  }

  /**
   * Stop render workers.
   */
  private synchronized void stopWorkers() {
    // Halt all worker threads.
    for (int i = 0; i < numThreads; ++i) {
      workers[i].interrupt();
    }
  }

  @Override public synchronized void addSceneStatusListener(SceneStatusListener listener) {
    sceneListeners.add(listener);
  }

  @Override public synchronized void removeSceneStatusListener(SceneStatusListener listener) {
    sceneListeners.remove(listener);
  }

  @Override public RenderStatus getRenderStatus() {
    RenderStatus status;
    synchronized (bufferedScene) {
      status = new RenderStatus(bufferedScene.renderTime, bufferedScene.spp);
    }
    return status;
  }

  @Override public void withSampleBufferProtected(SampleBufferConsumer consumer) {
    // Synchronizing on bufferedScene ensures that we are outside the frame rendering loop.
    synchronized (bufferedScene) {
      consumer.accept(bufferedScene.getSampleBuffer(), bufferedScene.width, bufferedScene.height);
    }
  }

  /**
   * Sends scene status text to the render preview tooltip.
   */
  private synchronized void sendSceneStatus(String status) {
    for (SceneStatusListener listener : sceneListeners) {
      listener.sceneStatus(status);
    }
  }

  @Override public void shutdown() {
    interrupt();
  }
}
