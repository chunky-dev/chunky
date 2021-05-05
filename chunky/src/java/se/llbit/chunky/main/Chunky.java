/* Copyright (c) 2010-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2010-2021 Chunky contributors
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

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.block.BlockProvider;
import se.llbit.chunky.block.BlockSpec;
import se.llbit.chunky.block.MinecraftBlockProvider;
import se.llbit.chunky.block.legacy.LegacyMinecraftBlockProvider;
import se.llbit.chunky.main.CommandLineOptions.Mode;
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.plugin.ChunkyPlugin;
import se.llbit.chunky.plugin.TabTransformer;
import se.llbit.chunky.renderer.ConsoleProgressListener;
import se.llbit.chunky.renderer.RayTracerFactory;
import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.renderer.RenderContextFactory;
import se.llbit.chunky.renderer.RenderController;
import se.llbit.chunky.renderer.RenderManager;
import se.llbit.chunky.renderer.Renderer;
import se.llbit.chunky.renderer.RendererFactory;
import se.llbit.chunky.renderer.SceneProvider;
import se.llbit.chunky.renderer.SnapshotControl;
import se.llbit.chunky.renderer.export.PictureExportFormat;
import se.llbit.chunky.renderer.scene.AsynchronousSceneManager;
import se.llbit.chunky.renderer.scene.PathTracer;
import se.llbit.chunky.renderer.scene.PreviewRayTracer;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.SceneFactory;
import se.llbit.chunky.renderer.scene.SceneManager;
import se.llbit.chunky.renderer.scene.SynchronousSceneManager;
import se.llbit.chunky.resources.SettingsDirectory;
import se.llbit.chunky.resources.TexturePackLoader;
import se.llbit.chunky.ui.ChunkyFx;
import se.llbit.chunky.ui.CreditsController;
import se.llbit.chunky.ui.render.RenderControlsTabTransformer;
import se.llbit.chunky.world.MaterialStore;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonValue;
import se.llbit.log.Level;
import se.llbit.log.Log;
import se.llbit.log.Receiver;
import se.llbit.util.TaskTracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

/**
 * Chunky is a Minecraft mapping and rendering tool created byJesper Öqvist (jesper@llbit.se).
 *
 * <p>Read more about Chunky at <a href="https://chunky.llbit.se">https://chunky.llbit.se</a>.
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
  private TabTransformer mainTabTransformer = tabs -> tabs;
  private boolean headless = false;

  private static ForkJoinPool commonThreads;

  /**
   * @return The title of the main window. Includes the current version string.
   */
  public static String getMainWindowTitle() {
    return String.format("Chunky %s", Version.getVersion());
  }

  public Chunky(ChunkyOptions options) {
    this.options = options;
    registerBlockProvider(new MinecraftBlockProvider());
    registerBlockProvider(new LegacyMinecraftBlockProvider());
  }

  /**
   * Start a headless (no GUI) render.
   *
   * @return error code
   */
  private int doHeadlessRender() {
    // TODO: This may not be needed after switching to JavaFX:
    System.setProperty("java.awt.headless", "true");

    HeadlessErrorTrackingLogger logger = new HeadlessErrorTrackingLogger();
    Log.setReceiver(logger, Level.INFO, Level.WARNING, Level.ERROR);

    SynchronousSceneManager sceneManager = (SynchronousSceneManager) getRenderController()
        .getSceneManager();
    Renderer renderer = getRenderController().getRenderer();
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
        scene.saveSnapshot(new File(getRenderContext().getSceneDirectory(), "snapshots"),
            taskTracker, getRenderContext().numRenderThreads());
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
          .format("Total rendering time: %d hours, %d minutes, %d seconds", hours, minutes,
              seconds));
      System.out.println("Average samples per second (SPS): " + sps);
    });

    try {
      sceneManager.loadScene(options.sceneName);
      if (options.target != -1) {
        sceneManager.getScene().setTargetSpp(options.target);
      }
      if (logger.getNumErrors() > 0) {
        if (!options.force) {
          System.err.println("\rAborting render due to errors while loading the scene.");
          System.err.println("Run again with -f to render anyway.");
          return 1;
        }
      }
      sceneManager.getScene().startHeadlessRender();

      renderer.start();
      renderer.join();
      return 0;
    } catch (FileNotFoundException e) {
      System.err.format("Scene \"%s\" not found!%n", options.sceneName);
      e.printStackTrace();
      return 1;
    } catch (IOException e) {
      System.err.format("IO error while loading scene (%s)%n", e.getMessage());
      e.printStackTrace();
      return 1;
    } catch (InterruptedException e) {
      System.err.println("Interrupted while loading scene");
      e.printStackTrace();
      return 1;
    } finally {
      renderer.shutdown();
    }
  }

  /**
   * Main entry point for Chunky. Chunky should normally be started via the launcher which sets up
   * the classpath with all dependencies.
   */
  public static void main(final String[] args) {
    CommandLineOptions cmdline = new CommandLineOptions(args);

    if (cmdline.configurationError) {
      System.exit(1);
    }

    int exitCode = 0;
    if (cmdline.mode == CommandLineOptions.Mode.NOTHING) {
      exitCode = cmdline.exitCode;
    } else {
      commonThreads = new ForkJoinPool(PersistentSettings.getNumThreads());

      Chunky chunky = new Chunky(cmdline.options);
      chunky.headless = cmdline.mode == Mode.HEADLESS_RENDER || cmdline.mode == Mode.SNAPSHOT;
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
    }
    if (exitCode != 0) {
      System.exit(exitCode);
    }
  }

  /**
   * This can be used by plugins to load the default Minecraft textures.
   */
  public static void loadDefaultTextures() {
    TexturePackLoader.loadTexturePacks(new String[0], false);
  }

  private void loadPlugins() {
    File pluginsDirectory = SettingsDirectory.getPluginsDirectory();
    if (!pluginsDirectory.isDirectory()) {
      Log.infof("Plugins directory does not exist: %s", pluginsDirectory.getAbsolutePath());
      return;
    }
    Path pluginsPath = pluginsDirectory.toPath();
    JsonArray plugins = PersistentSettings.getPlugins();
    Set<String> loadedPlugins = new HashSet<>();
    for (JsonValue value : plugins) {
      String jarName = value.asString("");
      if (!jarName.isEmpty()) {
        Log.info("Loading plugin: " + value);
        try {
          ChunkyPlugin
              .load(pluginsPath.resolve(jarName).toRealPath().toFile(), (plugin, manifest) -> {
                CreditsController.addPlugin(manifest);
                String pluginName = manifest.get("name").asString("");
                if (loadedPlugins.contains(pluginName)) {
                  Log.warnf(
                      "Multiple plugins with the same name (\"%s\") are enabled. Loading multiple versions of the same plugin can lead to strange behavior.",
                      pluginName);
                }
                loadedPlugins.add(pluginName);
                try {
                  plugin.attach(this);
                } catch (Throwable t) {
                  Log.error("Plugin " + jarName + " failed to load.", t);
                }
                Log.infof("Plugin loaded: %s %s", manifest.get("name").asString(""),
                    manifest.get("version").asString(""));
              });
        } catch (Throwable t) {
          Log.error("Plugin " + jarName + " failed to load.", t);
        }
      }
    }
  }

  /**
   * Save a snapshot for a scene.
   *
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
        context.setSceneDirectory(file.getParentFile());
        TaskTracker taskTracker = new TaskTracker(new ConsoleProgressListener(),
            TaskTracker.Task::new,
            (tracker, previous, name, size) -> new TaskTracker.Task(tracker, previous, name, size) {
              @Override
              public void update() {
                // Don't report task state to progress listener.
              }
            });
        if (!scene.loadDump(context, taskTracker)) {
          System.err.println("Failed to load the dump file found for this scene");
          return 1;
        }
        PictureExportFormat outputMode = scene.getOutputMode();
        if (options.imageOutputFile.isEmpty()) {
          options.imageOutputFile = String
              .format("%s-%d%s", scene.name(), scene.spp, outputMode.getExtension());
        }
        System.out.println("Image output mode: " + outputMode);
        scene.saveFrame(new File(options.imageOutputFile), taskTracker, context.numRenderThreads());
        System.out.println("Saved snapshot to " + options.imageOutputFile);
        return 0;
      }
    } catch (IOException e) {
      System.err.println("Failed to dump snapshot: " + e.getMessage());
      e.printStackTrace();
      return 1;
    }
  }

  /**
   * Get the common thread pool. This should only be used for parallelized processing, not for wait tasks.
   */
  public static ForkJoinPool getCommonThreads() {
    if (commonThreads == null) {
      commonThreads = new ForkJoinPool(PersistentSettings.getNumThreads());
    }
    return commonThreads;
  }

  public synchronized SceneManager getSceneManager() {
    return getRenderController().getSceneManager();
  }

  public boolean sceneInitialized() {
    return renderController != null;
  }

  @PluginApi
  public void setRendererFactory(RendererFactory rendererFactory) {
    this.rendererFactory = rendererFactory;
  }

  public RenderController getRenderController() {
    if (renderController == null) {
      // The renderController initialization is deferred to its first usage because plugins may want to overwrite
      // factories (which would require a new RenderController) but still add listeners e.g. to the Renderer which would
      // then be overwritten.
      RenderContext context = renderContextFactory.newRenderContext(this);
      Renderer renderer = rendererFactory.newRenderer(context, headless);
      if (headless) {
        SynchronousSceneManager sceneManager = new SynchronousSceneManager(context, renderer);
        renderer.setSceneProvider(sceneManager);
        renderController = new RenderController(context, renderer, sceneManager, sceneManager);
      } else {
        AsynchronousSceneManager sceneManager = new AsynchronousSceneManager(context, renderer);
        SceneProvider sceneProvider = sceneManager.getSceneProvider();
        renderer.setSceneProvider(sceneProvider);
        renderer.start();
        sceneManager.start();
        renderController = new RenderController(context, renderer, sceneManager, sceneProvider);
      }
    }
    return renderController;
  }

  @PluginApi
  public void setRenderContextFactory(RenderContextFactory renderContextFactory) {
    this.renderContextFactory = renderContextFactory;
  }

  @PluginApi
  public RenderContextFactory getRenderContextFactory() {
    return renderContextFactory;
  }

  public RenderContext getRenderContext() {
    return getRenderController().getContext();
  }

  @PluginApi
  public void setSceneFactory(SceneFactory sceneFactory) {
    this.sceneFactory = sceneFactory;
  }

  @PluginApi
  public SceneFactory getSceneFactory() {
    return sceneFactory;
  }

  @PluginApi
  public void setPreviewRayTracerFactory(RayTracerFactory previewRayTracerFactory) {
    this.previewRayTracerFactory = previewRayTracerFactory;
  }

  @PluginApi
  public RayTracerFactory getPreviewRayTracerFactory() {
    return previewRayTracerFactory;
  }

  @PluginApi
  public void setRayTracerFactory(RayTracerFactory rayTracerFactory) {
    this.rayTracerFactory = rayTracerFactory;
  }

  @PluginApi
  public RayTracerFactory getRayTracerFactory() {
    return rayTracerFactory;
  }

  /**
   * Registers a hook to modify the tabs in the Render Controls dialog.
   *
   * <p>Note: To behave nice with other plugins, please call to the previous
   * tab transformer.
   */
  @PluginApi
  public void setRenderControlsTabTransformer(
      RenderControlsTabTransformer renderControlsTabTransformer) {
    this.renderControlsTabTransformer = renderControlsTabTransformer;
  }

  @PluginApi
  public RenderControlsTabTransformer getRenderControlsTabTransformer() {
    return renderControlsTabTransformer;
  }

  /**
   * Registers a hook to modify the tabs in the Render Controls dialog.
   *
   * <p>Note: To behave nice with other plugins, please call to the previous
   * tab transformer.
   */
  @PluginApi
  public void setMainTabTransformer(TabTransformer mainTabTransformer) {
    this.mainTabTransformer = mainTabTransformer;
  }

  @PluginApi
  public TabTransformer getMainTabTransformer() {
    return mainTabTransformer;
  }

  /**
   * Registers a block provider to add support for blocks.
   */
  @PluginApi
  public void registerBlockProvider(BlockProvider blockProvider) {
    BlockSpec.blockProviders.add(0, blockProvider);
    MaterialStore.blockIds.addAll(blockProvider.getSupportedBlocks());
  }

  /**
   * Check if this Chunky instance is running in headless mode.
   * @return True if this Chunky instance is running in headless mode, false otherwise
   */
  @PluginApi
  public boolean isHeadless() {
    return headless;
  }
}
