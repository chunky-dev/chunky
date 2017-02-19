/* Copyright (c) 2010-2016 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.main;

import se.llbit.chunky.plugin.ChunkyPlugin;
import se.llbit.chunky.plugin.LoadPluginException;
import se.llbit.chunky.renderer.*;
import se.llbit.chunky.renderer.scene.*;
import se.llbit.chunky.resources.SettingsDirectory;
import se.llbit.chunky.resources.TexturePackLoader;
import se.llbit.chunky.ui.ChunkyFx;
import se.llbit.chunky.ui.render.RenderControlsTabTransformer;
import se.llbit.log.Level;
import se.llbit.log.Log;
import se.llbit.log.Receiver;
import se.llbit.util.TaskTracker;
import se.llbit.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Chunky is a Minecraft mapping and rendering tool created by
 * Jesper Öqvist (jesper@llbit.se).
 * <p>
 * <p>Read more about Chunky at http://chunky.llbit.se .
 */
public class Chunky {

  /**
   * A log receiver suitable for headless rendering.
   */
  private static final Receiver HEADLESS_LOG_RECEIVER = new Receiver() {
    @Override
    public void logEvent(Level level, String message) {
      if (level == Level.ERROR) {
        System.err.println();  // Clear the current progress line.
        System.err.println(message);
      } else {
        System.out.println();  // Clear the current progress line.
        System.out.println(message);
      }
    }
  };

  public final ChunkyOptions options;
  private RenderController renderController;
  private SceneFactory sceneFactory = SceneFactory.DEFAULT;
  private RenderContextFactory renderContextFactory = RenderContext::new;
  private RendererFactory rendererFactory = RenderManager::new;
  private RayTracerFactory previewRayTracerFactory = PreviewRayTracer::new;
  private RayTracerFactory rayTracerFactory = PathTracer::new;
  private RenderControlsTabTransformer renderControlsTabTransformer = tabs -> tabs;

  /**
   * @return The name of this application, including version string.
   */
  public static String getAppName() {
    return String.format("Chunky %s", Version.getVersion());
  }

  public Chunky(ChunkyOptions options) {
    this.options = options;
  }

  /**
   * Start a headless (no GUI) render.
   *
   * @return error code
   */
  private int doHeadlessRender() {
    // TODO: This may not be needed after switching to JavaFX:
    System.setProperty("java.awt.headless", "true");

    Log.setReceiver(HEADLESS_LOG_RECEIVER, Level.INFO, Level.WARNING, Level.ERROR);

    RenderContext context = renderContextFactory.newRenderContext(this);
    Renderer renderer = rendererFactory.newRenderer(context, true);
    SynchronousSceneManager sceneManager = new SynchronousSceneManager(context, renderer);
    renderer.setSceneProvider(sceneManager);
    TaskTracker taskTracker = new TaskTracker(new ConsoleProgressListener(),
        (tracker, previous, name, size) -> new TaskTracker.Task(tracker, previous, name, size) {
          @Override
          public void close() {
            super.close();
            long endTime = System.currentTimeMillis();
            int seconds = (int) ((endTime - startTime) / 1000);
            System.out.format("\r%s took %dm %ds%n", name, seconds / 60, seconds % 60);
          }
        });
    sceneManager.setTaskTracker(taskTracker);
    renderer.setSnapshotControl(SnapshotControl.DEFAULT);
    renderer.setOnFrameCompleted((scene, spp) -> {
      if (SnapshotControl.DEFAULT.saveSnapshot(scene, spp)) {
        // Save the current frame.
        scene.saveSnapshot(context.getSceneDirectory(), taskTracker);
      }

      if (SnapshotControl.DEFAULT.saveRenderDump(scene, spp)) {
        // Save the scene description and current render dump.
        try {
          sceneManager.saveScene();
        } catch (InterruptedException e) {
          throw new Error(e);
        }
      }
    });
    renderer.setRenderTask(taskTracker.backgroundTask());
    renderer.setOnRenderCompleted((time, sps) -> {
      System.out.println("Render job finished.");
      int seconds = (int) ((time / 1000) % 60);
      int minutes = (int) ((time / 60000) % 60);
      int hours = (int) (time / 3600000);
      System.out.println(String
          .format("Total rendering time: %d hours, %d minutes, %d seconds", hours, minutes, seconds));
      System.out.println("Average samples per second (SPS): " + sps);
    });

    try {
      sceneManager.loadScene(options.sceneName);
      if (options.target != -1) {
        sceneManager.getScene().setTargetSpp(options.target);
      }
      sceneManager.getScene().startHeadlessRender();

      renderer.start();
      renderer.join();
      return 0;
    } catch (FileNotFoundException e) {
      System.err.format("Scene \"%s\" not found!%n", options.sceneName);
      return 1;
    } catch (IOException e) {
      System.err.format("IO error while loading scene (%s)%n", e.getMessage());
      return 1;
    } catch (SceneLoadingError e) {
      System.err.format("Scene loading error (%s)%n", e.getMessage());
      return 1;
    } catch (InterruptedException e) {
      System.err.println("Interrupted while loading scene");
      return 1;
    } finally {
      renderer.shutdown();
    }
  }

  /**
   * Main entry point for Chunky. Chunky should normally be started via
   * the launcher which sets up the classpath with all dependencies.
   */
  public static void main(final String[] args) {
    CommandLineOptions cmdline = new CommandLineOptions(args);

    if (cmdline.configurationError) {
      System.exit(1);
    }

    int exitCode = 0;
    if (cmdline.mode != CommandLineOptions.Mode.NOTHING) {
      Chunky chunky = new Chunky(cmdline.options);
      chunky.loadPlugins();

      try {
        switch (cmdline.mode) {
          case HEADLESS_RENDER:
            exitCode = chunky.doHeadlessRender();
            break;
          case SNAPSHOT:
            exitCode = chunky.doSnapshot();
            break;
          case DEFAULT:
            ChunkyFx.startChunkyUI(chunky);
            break;
        }
      } catch (Throwable t) {
        Log.error("Unchecked exception caused Chunky to close.", t);
        exitCode = 2;
      }
      if (exitCode != 0) {
        System.exit(exitCode);
      }
    }
  }

  /**
   * This can be used by plugins to load the default Minecraft textures.
   */
  public static void loadDefaultTextures() {
    TexturePackLoader.loadTexturePacks(new String[0], false);
  }

  private void loadPlugins() {
    Log.setLevel(Level.INFO);
    Log.info("Getting plugins from " + SettingsDirectory.getPluginsDirectory().getAbsolutePath());
    File[] pluginFiles = SettingsDirectory.getPluginsDirectory().listFiles((f) -> f.getName().endsWith(".jar"));
    if (pluginFiles == null) {
      return;
    }

    for (File file : pluginFiles) {
      Log.info("Loading plugin: " + file.getName());
      try {
        ChunkyPlugin plugin = ChunkyPlugin.load(file);
        Log.info("Plugin loaded: " + plugin.getMeta().getName() + " " + plugin.getMeta().getVersion());
        plugin.getImplementation().attach(this);
      } catch (LoadPluginException e) {
        Log.error("Failed to load plugin: " + file.getAbsolutePath(), e);
      }
    }
  }

  private static boolean verifyChecksumMd5(File pluginJar, String expected) {
    String actual = Util.md5sum(pluginJar);
    return actual.equalsIgnoreCase(expected);
  }

  /**
   * Save a snapshot for a scene.
   * <p>
   * <p>This currently disregards the various factories for the
   * render context and scene construction.
   */
  private int doSnapshot() {
    Log.setReceiver(HEADLESS_LOG_RECEIVER, Level.INFO, Level.WARNING, Level.ERROR);
    try {
      File file = options.getSceneDescriptionFile();
      try (FileInputStream in = new FileInputStream(file)) {
        Scene scene = new Scene();
        scene.loadDescription(in); // Load description to get current SPP & canvas size.
        RenderContext context = new RenderContext(this);
        TaskTracker taskTracker = new TaskTracker(new ConsoleProgressListener(),
            TaskTracker.Task::new,
            (tracker, previous, name, size) -> new TaskTracker.Task(tracker, previous, name, size) {
              @Override
              public void update() {
                // Don't report task state to progress listener.
              }
            });
        scene.loadDump(context, taskTracker); // Load the render dump.
        OutputMode outputMode = scene.getOutputMode();
        if (options.imageOutputFile.isEmpty()) {
          String extension = ".png";
          if (outputMode == OutputMode.TIFF_32) {
            extension = ".tiff";
          }
          options.imageOutputFile = String.format("%s-%d%s", scene.name(), scene.spp, extension);
        }
        switch (outputMode) {
          case PNG:
            System.out.println("Image output mode: PNG");
            break;
          case TIFF_32:
            System.out.println("Image output mode: TIFF32");
            break;
        }
        scene.saveFrame(new File(options.imageOutputFile), taskTracker);
        System.out.println("Saved snapshot to " + options.imageOutputFile);
        return 0;
      }
    } catch (IOException e) {
      System.err.println("Failed to dump snapshot: " + e.getMessage());
      return 1;
    }
  }

  public synchronized SceneManager getSceneManager() {
    return getRenderController().getSceneManager();
  }

  public boolean sceneInitialized() {
    return renderController != null;
  }

  public RenderController getRenderController() {
    if (renderController == null) {
      RenderContext context = renderContextFactory.newRenderContext(this);
      Renderer renderer = rendererFactory.newRenderer(context, false);
      AsynchronousSceneManager sceneManager = new AsynchronousSceneManager(context, renderer);
      SceneProvider sceneProvider = sceneManager.getSceneProvider();
      renderer.setSceneProvider(sceneProvider);
      renderer.start();
      sceneManager.start();
      renderController = new RenderController(context, renderer, sceneManager, sceneProvider);
    }
    return renderController;
  }

  public void setRenderContextFactory(RenderContextFactory renderContextFactory) {
    this.renderContextFactory = renderContextFactory;
  }

  public RenderContextFactory getRenderContextFactory() {
    return renderContextFactory;
  }

  public RenderContext getRenderContext() {
    return getRenderController().getContext();
  }

  public void setSceneFactory(SceneFactory sceneFactory) {
    this.sceneFactory = sceneFactory;
  }

  public SceneFactory getSceneFactory() {
    return sceneFactory;
  }

  public void setPreviewRayTracerFactory(RayTracerFactory previewRayTracerFactory) {
    this.previewRayTracerFactory = previewRayTracerFactory;
  }

  public RayTracerFactory getPreviewRayTracerFactory() {
    return previewRayTracerFactory;
  }

  public void setRayTracerFactory(RayTracerFactory rayTracerFactory) {
    this.rayTracerFactory = rayTracerFactory;
  }

  public RayTracerFactory getRayTracerFactory() {
    return rayTracerFactory;
  }

  public void setRenderControlsTabTransformer(
      RenderControlsTabTransformer renderControlsTabTransformer) {
    this.renderControlsTabTransformer = renderControlsTabTransformer;
  }

  public RenderControlsTabTransformer getRenderControlsTabTransformer() {
    return renderControlsTabTransformer;
  }
}
