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

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.PathTracer;
import se.llbit.chunky.renderer.scene.PreviewRayTracer;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.log.Log;
import se.llbit.util.TaskTracker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
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
public class InternalRenderManager extends Thread implements RenderManager {
  @PluginApi
  public static final Map<String, Renderer> renderers = new HashMap<>();

  @PluginApi
  public static final Map<String, Renderer> previewRenderers = new HashMap<>();

  static {
    renderers.computeIfAbsent("Chunky Path Tracer", name -> new PathTracingRenderer(new PathTracer()));
    previewRenderers.computeIfAbsent("Chunky Preview", name -> new PreviewRenderer(new PreviewRayTracer()));
  }

  /**
   * This is a buffered scene which render workers should use while rendering.
   * The buffered scene is only updated when the workers are quiescent.
   *
   * Render workers should:
   *  * Increment {@code bufferedScene.spp} after rendering each frame
   *  * Merge the new frame with {@code bufferedScene.getSampleBuffer()}
   *
   * Render workers should not otherwise modify this.
   */
  public final Scene bufferedScene;

  /**
   * This is the render worker pool {@code Renderer}s should use.
   *
   * {@code Renderer}s should submit small work-units to this pool. CPU usage is limited automatically, but if
   * each work-unit can take a long time, they should call {@code RenderWorkerPool.RenderWorker.workSleep()}
   * periodically to manage CPU usage.
   */
  public final RenderWorkerPool pool;

  /**
   * The current renderer selections.
   */
  private String renderer = "Chunky Path Tracer";
  private String previewRenderer = "Chunky Preview";

  /**
   * The render canvas. This is redrawn on every frame (if applicable).
   */
  private Repaintable canvas = () -> {};

  /**
   * Decides if the canvas is in view and every frame needs to be finalized.
   */
  private boolean finalizeAllFrames = false;

  /**
   * Listeners that need to be called on every frame.
   */
  private final Collection<RenderStatusListener> renderStatusListeners = new ArrayList<>();
  private final Collection<SceneStatusListener> sceneStatusListeners = new ArrayList<>();

  private BiConsumer<Long, Integer> renderCompletionListener = (time, sps) -> {};
  private BiConsumer<Scene, Integer> frameCompletionListener = (scene, spp) -> {};


  private TaskTracker.Task renderTask = TaskTracker.Task.NONE;

  private long renderStart;

  /**
   * Decides if render threads shut down after reaching the target SPP.
   */
  private final boolean headless;

  /**
   * Current renderer mode.
   */
  private RenderMode mode = RenderMode.PREVIEW;

  /**
   * Current snapshot mode. Frame will always be post processed if a snapshot will happen after that frame.
   */
  private SnapshotControl snapshotControl = SnapshotControl.DEFAULT;

  protected SceneProvider sceneProvider;
  public final RenderContext context;

  /**
   * This renderer does nothing.
   */
  private static final Renderer EMPTY_RENDERER = new Renderer() {
    @Override public void setPostRender(BooleanSupplier callback) {}
    @Override public void render(InternalRenderManager manager) throws InterruptedException {}
  };

  private final BooleanSupplier previewCallback;
  private final BooleanSupplier renderCallback;

  private int cpuLoad = 100;

  /**
   * @param headless {@code true} if rendering threads should be shut
   * down after reaching the render target.
   */
  public InternalRenderManager(RenderContext context, boolean headless) {
    super("Internal Render Manager");

    this.context = context;
    this.headless = headless;
    bufferedScene = context.getChunky().getSceneFactory().newScene();

    pool = context.renderPoolFactory.create(context.numRenderThreads(), System.currentTimeMillis());

    // Initialize callbacks here since java will complain `bufferedScene` is not initialized yet.
    previewCallback = () -> {
      sendSceneStatus(bufferedScene.sceneStatus());

      renderStatusListeners.forEach(listener -> {
        listener.setRenderTime(System.currentTimeMillis() - renderStart);
        listener.setSamplesPerSecond(0);
        listener.setSpp(0);
      });

      this.finalizeFrame(true);

      return !finalizeAllFrames || sceneProvider.pollSceneStateChange();
    };

    renderCallback = () -> {
      sceneProvider.withSceneProtected(scene -> {
        synchronized (bufferedScene) {
          bufferedScene.copyTransients(scene);
          updateRenderState(scene);
        }
      });

      synchronized (bufferedScene) {
        bufferedScene.renderTime = System.currentTimeMillis() - renderStart;

        if (snapshotControl.saveSnapshot(bufferedScene, bufferedScene.spp))
          bufferedScene.postProcessFrame(renderTask);
        else
          this.finalizeFrame(false);

        frameCompletionListener.accept(bufferedScene, bufferedScene.spp);
        updateRenderProgress();

        if (bufferedScene.spp > bufferedScene.getTargetSpp()) {
          renderCompletionListener.accept(bufferedScene.renderTime, samplesPerSecond());
          return true;
        }
      }

      return mode == RenderMode.PAUSED || sceneProvider.pollSceneStateChange();
    };
  }

  @Override
  public void run() {
    try {
      while (!isInterrupted()) {
        ResetReason reason = sceneProvider.awaitSceneStateChange();

        final boolean[] sceneReset = {false};
        synchronized (bufferedScene) {
          sceneProvider.withSceneProtected(scene -> {
            if (reason.overwriteState()) {
              bufferedScene.copyState(scene);
              sceneReset[0] = true;
            }
            if (reason == ResetReason.MATERIALS_CHANGED || reason == ResetReason.SCENE_LOADED) {
              scene.importMaterials();
              sceneReset[0] = true;
            }

            bufferedScene.copyTransients(scene);
            finalizeAllFrames = scene.shouldFinalizeBuffer();
            updateRenderState(scene);

            if (reason == ResetReason.SCENE_LOADED) {
              // Make sure frame is finalized
              bufferedScene.postProcessFrame(renderTask);

              // Swap buffers so the render canvas will see the current frame.
              bufferedScene.swapBuffers();

              // Notify the scene listeners.
              sendSceneStatus(bufferedScene.sceneStatus());
              canvas.repaint();
            }
          });
        }

        Renderer render = mode == RenderMode.PREVIEW ? getPreviewRenderer() : getRenderer();
        renderStart = System.currentTimeMillis();

        if (sceneReset[0]) {
          render.sceneReset(this, reason);
        }

        if (mode == RenderMode.PREVIEW) {
          pool.setCpuLoad(100);
          render.setPostRender(previewCallback);
          render.render(this);
          pool.setCpuLoad(cpuLoad);
        } else {
          // Bail early if render is already done
          if (bufferedScene.spp > bufferedScene.getTargetSpp()) {
            sceneProvider.withSceneProtected(scene -> {
              scene.pauseRender();
              updateRenderState(scene);
            });
          } else if (mode != RenderMode.PAUSED) {
            render.setPostRender(renderCallback);
            render.render(this);
          }
        }

        if (headless) {
          break;
        }
      }
    } catch (InterruptedException e) {
      // Interrupted
    } catch (Throwable e) {
      Log.error("Unchecked exception in render manager.", e);
    }
  }

  private Renderer getRenderer() {
    return renderers.getOrDefault(renderer, EMPTY_RENDERER);
  }

  private Renderer getPreviewRenderer() {
    return previewRenderers.getOrDefault(previewRenderer, EMPTY_RENDERER);
  }

  public String getRendererName() {
    return renderer;
  }

  public String getPreviewRendererName() {
    return previewRenderer;
  }

  public void setRenderer(String value) {
    renderer = value;
  }

  public void setPreviewRenderer(String value) {
    previewRenderer = value;
  }

  @Override
  public synchronized void addRenderListener(RenderStatusListener listener) {
    renderStatusListeners.add(listener);
  }

  @Override
  public void removeRenderListener(RenderStatusListener listener) {
    renderStatusListeners.remove(listener);
  }

  @Override
  public void setSceneProvider(SceneProvider sceneProvider) {
    this.sceneProvider = sceneProvider;
  }

  @Override
  public void setCanvas(Repaintable canvas) {
    this.canvas = canvas;
  }

  private void updateRenderState(Scene scene) {
    finalizeAllFrames = scene.shouldFinalizeBuffer();
    if (mode != scene.getMode()) {
      mode = scene.getMode();
      // TODO: make render state update faster by moving this to Scene?
      renderStatusListeners.forEach(listener -> listener.renderStateChanged(mode));
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
      renderStatusListeners.forEach(listener -> {
        listener.setRenderTime(bufferedScene.renderTime);
        listener.setSamplesPerSecond(samplesPerSecond());
        listener.setSpp(bufferedScene.spp);
      });
    }
  }

  /**
   * Call the consumer with the current front frame buffer.
   */
  @Override
  public void withBufferedImage(Consumer<BitmapImage> consumer) {
    bufferedScene.withBufferedImage(consumer);
  }

  @Override
  public void setOnRenderCompleted(BiConsumer<Long, Integer> listener) {
    renderCompletionListener = listener;
  }

  @Override
  public void setOnFrameCompleted(BiConsumer<Scene, Integer> listener) {
    frameCompletionListener = listener;
  }

  @Override
  public void setSnapshotControl(SnapshotControl callback) {
    this.snapshotControl = callback;
  }

  @Override
  public void setRenderTask(TaskTracker.Task task) {
    renderTask = task;
  }

  public TaskTracker.Task getRenderTask() {
    return renderTask;
  }

  /**
   * Set CPU load percentage.
   *
   * @param value new load percentage.
   */
  @Override
  public void setCPULoad(int value) {
    this.cpuLoad = value;
    pool.setCpuLoad(value);
  }

  /**
   * Sends scene status text to the render preview tooltip.
   */
  private synchronized void sendSceneStatus(String status) {
    sceneStatusListeners.forEach(listener -> listener.sceneStatus(status));
  }

  @Override public synchronized void addSceneStatusListener(SceneStatusListener listener) {
    sceneStatusListeners.add(listener);
  }

  @Override public synchronized void removeSceneStatusListener(SceneStatusListener listener) {
    sceneStatusListeners.remove(listener);
  }

  @Override public RenderStatus getRenderStatus() {
    RenderStatus status;
    synchronized (bufferedScene) {
      status = new RenderStatus(bufferedScene.renderTime, bufferedScene.spp);
    }
    return status;
  }

  @Override
  public void withSampleBufferProtected(SampleBufferConsumer consumer) {
    synchronized (bufferedScene) {
      consumer.accept(bufferedScene.getSampleBuffer(), bufferedScene.width, bufferedScene.height);
    }
  }

  @Override
  public void shutdown() {
    pool.interrupt();
    interrupt();
  }

  public void finalizeFrame(boolean force) {
    if (force || finalizeAllFrames || snapshotControl.saveSnapshot(bufferedScene, bufferedScene.spp)) {
      // Split up to 10 tasks per thread
      int pixelsPerTask = (bufferedScene.width * bufferedScene.height) / (pool.threads * 10 - 1);

      for (int i = 0; i < bufferedScene.width * bufferedScene.height; i += pixelsPerTask) {
        int start = i;
        int end = Math.min(bufferedScene.width * bufferedScene.height, i + pixelsPerTask);
        pool.submit(worker -> {
          for (int j = start; j < end; j++) {
            bufferedScene.finalizePixel(j % bufferedScene.width, j / bufferedScene.width);
          }
        });
      }

      try {
        pool.awaitEmpty();
      } catch (InterruptedException e) {
        // Interrupted
      }

      bufferedScene.swapBuffers();
      canvas.repaint();
    }
  }
}
