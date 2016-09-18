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

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.Plugin;
import se.llbit.chunky.renderer.ConsoleProgressListener;
import se.llbit.chunky.renderer.ConsoleRenderListener;
import se.llbit.chunky.renderer.OutputMode;
import se.llbit.chunky.renderer.RayTracerFactory;
import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.renderer.RenderContextFactory;
import se.llbit.chunky.renderer.RenderController;
import se.llbit.chunky.renderer.RenderManager;
import se.llbit.chunky.renderer.RenderStatusListener;
import se.llbit.chunky.renderer.Renderer;
import se.llbit.chunky.renderer.RendererFactory;
import se.llbit.chunky.renderer.SceneProvider;
import se.llbit.chunky.renderer.SimpleRenderListener;
import se.llbit.chunky.renderer.scene.AsynchronousSceneManager;
import se.llbit.chunky.renderer.scene.PathTracer;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.SceneFactory;
import se.llbit.chunky.renderer.scene.SceneLoadingError;
import se.llbit.chunky.renderer.scene.SceneManager;
import se.llbit.chunky.renderer.scene.PreviewRayTracer;
import se.llbit.chunky.renderer.scene.SynchronousSceneManager;
import se.llbit.chunky.ui.ChunkyFx;
import se.llbit.json.JsonValue;
import se.llbit.log.Level;
import se.llbit.log.Log;
import se.llbit.log.Receiver;
import se.llbit.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Chunky is a Minecraft mapping and rendering tool created by
 * Jesper Öqvist (jesper@llbit.se).
 *
 * <p>Read more about Chunky at http://chunky.llbit.se .
 */
public class Chunky {

  public final ChunkyOptions options;
  private RenderController renderController;
  private SceneFactory sceneFactory = SceneFactory.PAINTABLE;
  private RenderContextFactory renderContextFactory = RenderContext::new;
  private RendererFactory rendererFactory = RenderManager::new;
  private RayTracerFactory previewRayTracerFactory = PreviewRayTracer::new;
  private RayTracerFactory rayTracerFactory = PathTracer::new;

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

    // Initialize the SceneFactory to create non-paintable scenes.
    sceneFactory = SceneFactory.HEADLESS;
    RenderContext context = renderContextFactory.newRenderContext(this);
    Renderer renderer = rendererFactory.newRenderer(context, true);
    SynchronousSceneManager sceneManager = new SynchronousSceneManager(context, renderer);
    renderer.setSceneProvider(sceneManager);
    RenderStatusListener renderListener = new ConsoleRenderListener(context, sceneManager);
    sceneManager.setRenderStatusListener(renderListener);
    renderer.setRenderListener(renderListener);

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

    if (cmdline.confError) {
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

  private void loadPlugins() {
    JsonValue plugins = PersistentSettings.getPlugins();
    for (JsonValue plugin : plugins.array().getElementList()) {
      String jar = plugin.object().get("jar").stringValue("");
      String main = plugin.object().get("main").stringValue("");
      // The MD5 checksum is only for Jar integrity checking, not security!
      // Plugin Jar trust is implicit. Only install plugins that you trust!
      String md5 = plugin.object().get("md5").stringValue("");
      boolean enabled = plugin.object().get("enabled").boolValue(true);
      if (!jar.endsWith(".jar")) {
        Log.error("Plugin Jar path does not seem to point to a Jar file: " + jar);
      }
      if (!enabled) {
        // Skip disabled plugin.
        continue;
      }
      if (main.isEmpty()) {
        Log.error("Plugin has no main class declared: " + jar);
        continue;
      }
      if (md5.isEmpty()) {
        Log.error("Plugin missing MD5 checksum: " + jar);
        continue;
      }
      File pluginJar = new File(jar);
      if (pluginJar.isFile()) {
        if (!verifyChecksumMd5(pluginJar, md5)) {
          Log.error("Plugin is corrupt (MD5 check failed): " + jar);
          continue;
        }
        try {
          URLClassLoader classLoader = new URLClassLoader(new URL[] {pluginJar.toURI().toURL()});
          Class<?> pluginClass = classLoader.loadClass(main);
          Plugin pluginInstance = (Plugin) pluginClass.newInstance();
          pluginInstance.attach(this);
          Log.info("Plugin loaded: " + jar);
        } catch (MalformedURLException| ClassNotFoundException
            | InstantiationException | IllegalAccessException e) {
          Log.error("Failed to load plugin " + pluginJar.getAbsolutePath(), e);
        } catch (ClassCastException e) {
          Log.error("Failed to load plugin " + pluginJar.getAbsolutePath()
              + ". Main plugin class has wrong type", e);
        }
      }
    }
  }

  private static boolean verifyChecksumMd5(File pluginJar, String expected) {
    String actual = Util.md5sum(pluginJar);
    return actual.equalsIgnoreCase(expected);
  }

  /**
   * Save a snapshot for a scene.
   *
   * <p>This currently disregards the various factories for the
   * render context and scene construction.
   */
  private int doSnapshot() {
    try {
      Log.setReceiver(new Receiver() {
        @Override public void logEvent(Level level, String message) {
          if (level == Level.ERROR) {
            System.err.println();  // Clear the current progress line.
            System.err.println(message);
          } else {
            System.out.println();  // Clear the current progress line.
            System.out.println(message);
          }
        }
      }, Level.INFO, Level.WARNING, Level.ERROR);
      File file = options.getSceneDescriptionFile();
      Scene scene = new Scene();
      try (FileInputStream in = new FileInputStream(file)) {
        scene.loadDescription(in); // Load description to get current SPP & canvas size.
        RenderContext context = new RenderContext(this);
        SimpleRenderListener listener = new SimpleRenderListener(new ConsoleProgressListener());
        scene.initBuffers();  // Initialize the sample buffer.
        scene.loadDump(context, listener); // Load the render dump.
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
        scene.saveFrame(new File(options.imageOutputFile), listener.taskTracker());
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
}
