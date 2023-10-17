/* Copyright (c) 2012-2022 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2012-2022 Chunky contributors
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
package se.llbit.chunky.renderer.scene;

import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.minecraft.Lava;
import se.llbit.chunky.block.minecraft.Water;
import se.llbit.chunky.block.legacy.LegacyBlocksFinalizer;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.chunk.ChunkData;
import se.llbit.chunky.chunk.ChunkLoadingException;
import se.llbit.chunky.chunk.EmptyChunkData;
import se.llbit.chunky.chunk.biome.BiomeData;
import se.llbit.chunky.entity.*;
import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.*;
import se.llbit.chunky.renderer.export.PictureExportFormat;
import se.llbit.chunky.renderer.export.PictureExportFormats;
import se.llbit.chunky.renderer.postprocessing.PostProcessingFilter;
import se.llbit.chunky.renderer.postprocessing.PostProcessingFilters;
import se.llbit.chunky.renderer.postprocessing.PreviewFilter;
import se.llbit.chunky.renderer.projection.ParallelProjector;
import se.llbit.chunky.renderer.projection.ProjectionMode;
import se.llbit.chunky.renderer.renderdump.RenderDump;
import se.llbit.chunky.renderer.scene.biome.BiomeStructure;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.resources.OctreeFileFormat;
import se.llbit.chunky.world.*;
import se.llbit.chunky.world.biome.ArrayBiomePalette;
import se.llbit.chunky.world.biome.Biome;
import se.llbit.chunky.world.biome.BiomePalette;
import se.llbit.chunky.world.biome.Biomes;
import se.llbit.json.*;
import se.llbit.log.Log;
import se.llbit.math.*;
import se.llbit.math.structures.Position2IntStructure;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.Tag;
import se.llbit.util.*;
import se.llbit.util.annotation.NotNull;
import se.llbit.util.mojangapi.MinecraftProfile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Encapsulates scene and render state.
 *
 * <p>Render state is stored in a sample buffer. Two frame buffers
 * are also kept for when a snapshot should be rendered.
 */
public class Scene implements JsonSerializable, Refreshable {
  public static final int DEFAULT_DUMP_FREQUENCY = 500;
  public static final String EXTENSION = ".json";

  /** The current Scene Description Format (SDF) version. */
  public static final int SDF_VERSION = 9;

  protected static final double fSubSurface = 0.3;

  /** Minimum canvas width. */
  public static final int MIN_CANVAS_WIDTH = 20;

  /** Minimum canvas height. */
  public static final int MIN_CANVAS_HEIGHT = 20;

  /**
   * Minimum exposure.
   */
  public static final double MIN_EXPOSURE = 0.001;

  /**
   * Maximum exposure.
   */
  public static final double MAX_EXPOSURE = 1000.0;

  /**
   * Default gamma for the gamma correction post process.
   */
  public static final float DEFAULT_GAMMA = 2.2f;

  public static final boolean DEFAULT_EMITTERS_ENABLED = false;

  /**
   * Default emitter intensity.
   */
  public static final double DEFAULT_EMITTER_INTENSITY = 13;

  /**
   * Minimum emitter intensity.
   */
  public static final double MIN_EMITTER_INTENSITY = 0.01;

  /**
   * Maximum emitter intensity.
   */
  public static final double MAX_EMITTER_INTENSITY = 1000;

  /**
   * Default transmissivity cap.
   */
  public static final double DEFAULT_TRANSMISSIVITY_CAP = 1;

  /**
   * Minimum transmissivity cap.
   */
  public static final double MIN_TRANSMISSIVITY_CAP = 1;

  /**
   * Maximum transmissivity cap.
   */
  public static final double MAX_TRANSMISSIVITY_CAP = 3;

  /**
   * Default exposure.
   */
  public static final double DEFAULT_EXPOSURE = 1.0;

  /**
   * Default fog density.
   */
  public static final double DEFAULT_FOG_DENSITY = 0.05;

  /**
   * Default post processing filter.
   */
  public static final PostProcessingFilter DEFAULT_POSTPROCESSING_FILTER = PostProcessingFilters
      .getPostProcessingFilterFromId("GAMMA").orElse(PostProcessingFilters.NONE);

  private static boolean invalidWarn = false;

  protected final Sky sky = new Sky(this);
  protected final Camera camera = new Camera(this);
  protected final Sun sun = new Sun(this);
  protected final Vector3 waterColor =
      new Vector3(PersistentSettings.getWaterColorRed(), PersistentSettings.getWaterColorGreen(),
          PersistentSettings.getWaterColorBlue());
  public int sdfVersion = -1;
  public String name = "default_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

  /**
   * Canvas width.
   */
  public int width;

  /**
   * Canvas height.
   */
  public int height;

  public int fullWidth = 0;
  public int fullHeight = 0;
  public int cropX = 0;
  public int cropY = 0;

  public PostProcessingFilter postProcessingFilter = DEFAULT_POSTPROCESSING_FILTER;
  public PictureExportFormat outputMode = PictureExportFormats.PNG;
  public long renderTime;
  /**
   * Current SPP for the scene.
   */
  public int spp = 0;
  protected double exposure = DEFAULT_EXPOSURE;
  /**
   * Target SPP for the scene.
   */
  protected int sppTarget = PersistentSettings.getSppTargetDefault();
  /**
   * Branch count for the scene.
   */
  protected int branchCount = PersistentSettings.getBranchCountDefault();
  /**
   * Recursive ray depth limit (not including Russian Roulette).
   */
  protected int rayDepth = PersistentSettings.getRayDepthDefault();
  protected String worldPath = "";
  protected int worldDimension = 0;
  protected RenderMode mode = RenderMode.PREVIEW;
  protected int dumpFrequency = DEFAULT_DUMP_FREQUENCY;
  protected boolean saveSnapshots = false;
  protected boolean emittersEnabled = DEFAULT_EMITTERS_ENABLED;
  protected double emitterIntensity = DEFAULT_EMITTER_INTENSITY;
  protected EmitterSamplingStrategy emitterSamplingStrategy = EmitterSamplingStrategy.NONE;
  protected boolean fancierTranslucency = true;
  protected double transmissivityCap = DEFAULT_TRANSMISSIVITY_CAP;

  protected SunSamplingStrategy sunSamplingStrategy = SunSamplingStrategy.FAST;

  /**
   * Water opacity modifier.
   */
  protected double waterOpacity = PersistentSettings.getWaterOpacity();
  protected double waterVisibility = PersistentSettings.getWaterVisibility();
  protected boolean stillWater = PersistentSettings.getStillWater();
  protected boolean useCustomWaterColor = PersistentSettings.getUseCustomWaterColor();

  protected boolean waterPlaneEnabled = false;
  protected double waterPlaneHeight = World.SEA_LEVEL;
  protected boolean waterPlaneOffsetEnabled = true;
  protected boolean waterPlaneChunkClip = true;
  protected WaterShader waterShading = new SimplexWaterShader();

  public final Fog fog = new Fog(this);

  protected boolean biomeColors = true;
  protected boolean biomeBlending = true;
  protected boolean transparentSky = false;
  protected Collection<ChunkPosition> chunks = new ArrayList<>();
  protected JsonObject cameraPresets = new JsonObject();
  /**
   * Indicates if the render should be forced to reset.
   */
  protected ResetReason resetReason = ResetReason.NONE;

  /**
   * World reference.
   */
  @NotNull private World loadedWorld = EmptyWorld.INSTANCE;

  /**
   * Octree origin.
   */
  protected Vector3i origin = new Vector3i();

  /**
   * Actual upper y bound (might be lower than yClipMax).
   */
  protected int yMax = 256;
  /**
   * Actual lower y bound (might be higher than yClipMin).
   */
  protected int yMin = 0;

  private BlockPalette palette;
  private Octree worldOctree;
  private Octree waterOctree;

  private SceneEntities entities = new SceneEntities();

  /** Material properties for this scene. */
  public Map<String, JsonValue> materials = new HashMap<>();

  /** Lower Y clip plane. */
  public int yClipMin = 0;

  /** Upper Y clip plane. */
  public int yClipMax = 256;

  /**
   * Current time in seconds. Adjusts animated blocks like fire.
   */
  private double animationTime = 0;

  private BiomeStructure grassTexture;
  private BiomeStructure foliageTexture;
  private BiomeStructure waterTexture;

  /** This is the 8-bit channel frame buffer. */
  protected BitmapImage frontBuffer;

  private BitmapImage backBuffer;

  /**
   * HDR sample buffer for the render output.
   *
   * <p>Note: the sample buffer is initially null, it is only
   * initialized if the scene will be used for rendering.
   * This avoids allocating new sample buffers each time
   * we want to copy the scene state to a temporary scene.
   *
   * <p>TODO: render buffers (sample buffer, alpha channel, etc.)
   * should really be moved somewhere else and not be so tightly
   * coupled to the scene settings.
   */
  protected double[] samples;

  private byte[] alphaChannel;

  private boolean finalized = false;

  private boolean finalizeBuffer = false;

  private boolean forceReset = false;

  private Grid emitterGrid;

  private int gridSize = PersistentSettings.getGridSizeDefault();

  private boolean preventNormalEmitterWithSampling = PersistentSettings.getPreventNormalEmitterWithSampling();

  /**
   * Hide unknown blocks. This is done at intersection time, the unknown blocks are still in the octree.
   */
  private boolean hideUnknownBlocks = false;

  /**
   * The octree implementation to use
   */
  private String octreeImplementation = PersistentSettings.getOctreeImplementation();

  /**
   * The BiomeStructure implementation to use
   */
  private String biomeStructureImplementation = PersistentSettings.getBiomeStructureImplementation();

  /**
   * Additional data that is associated with a scene, this can be used by plugins
   */
  private JsonObject additionalData = new JsonObject();

  private String renderer = DefaultRenderManager.ChunkyPathTracerID;
  private String previewRenderer = DefaultRenderManager.ChunkyPreviewID;

  protected volatile boolean isLoading = false;

  private boolean previewParticleFog = PersistentSettings.getPreviewParticleFog();

  /**
   * Creates a scene with all default settings.
   *
   * <p>Note: this does not initialize the render buffers for the scene!
   * Render buffers are initialized either by using loadDescription(),
   * fromJson(), or importFromJson(), or by calling initBuffers().
   */
  public Scene() {
    width = PersistentSettings.get3DCanvasWidth();
    height = PersistentSettings.get3DCanvasHeight();
    sppTarget = PersistentSettings.getSppTargetDefault();
    branchCount = PersistentSettings.getBranchCountDefault();

    palette = new BlockPalette();
    worldOctree = new Octree(octreeImplementation, 1);
    waterOctree = new Octree(octreeImplementation, 1);
    emitterGrid = null;
  }

  /**
   * Delete all scene files from the scene directory, leaving only
   * snapshots untouched.
   */
  public static void delete(String name, File sceneDir) {
    String[] extensions = {
        ".json", ".dump", ".octree2", ".emittergrid", ".foliage", ".grass", ".json.backup", ".dump.backup",
    };
    for (String extension : extensions) {
      File file = new File(sceneDir, name + extension);
      if (file.isFile()) {
        //noinspection ResultOfMethodCallIgnored
        file.delete();
      }
    }
  }

  /**
   * Export the scene to a zip file.
   */
  public static void exportToZip(File sceneDirectory, String name, File targetFile) {
    String[] extensions = { ".json", ".dump", ".octree2", ".foliage", ".grass", ".emittergrid", };
    ZipExport.zip(targetFile, sceneDirectory, name, extensions);
  }

  /**
   * This initializes the render buffers when initializing the
   * scene and after scene canvas size changes.
   */
  public synchronized void initBuffers() {
    frontBuffer = new BitmapImage(width, height);
    backBuffer = new BitmapImage(width, height);
    alphaChannel = new byte[width * height];
    samples = new double[width * height * 3];
  }

  /**
   * Creates a copy of another scene.
   */
  public Scene(Scene other) {
    copyState(other);
    copyTransients(other);
  }

  /**
   * Import scene state from another scene.
   */
  public synchronized void copyState(Scene other, boolean copyChunks) {
    if (copyChunks) {
      loadedWorld = other.loadedWorld;
      worldPath = other.worldPath;
      worldDimension = other.worldDimension;

      // The octree reference is overwritten to save time.
      // When the other scene is changed it must create a new octree.
      palette = other.palette;
      worldOctree = other.worldOctree;
      waterOctree = other.waterOctree;

      entities.copyState(other.entities);

      grassTexture = other.grassTexture;
      foliageTexture = other.foliageTexture;
      waterTexture = other.waterTexture;
      origin.set(other.origin);
      yMin = other.yMin;
      yMax = other.yMax;

      chunks = other.chunks;

      emitterGrid = other.emitterGrid;
    }

    // Copy material properties.
    materials = other.materials;

    exposure = other.exposure;

    stillWater = other.stillWater;
    waterOpacity = other.waterOpacity;
    waterVisibility = other.waterVisibility;
    useCustomWaterColor = other.useCustomWaterColor;
    waterColor.set(other.waterColor);
    fog.set(other.fog);
    biomeColors = other.biomeColors;
    sunSamplingStrategy = other.sunSamplingStrategy;
    emittersEnabled = other.emittersEnabled;
    emitterIntensity = other.emitterIntensity;
    emitterSamplingStrategy = other.emitterSamplingStrategy;
    preventNormalEmitterWithSampling = other.preventNormalEmitterWithSampling;
    fancierTranslucency = other.fancierTranslucency;
    transmissivityCap = other.transmissivityCap;
    previewParticleFog = other.previewParticleFog;
    transparentSky = other.transparentSky;
    yClipMin = other.yClipMin;
    yClipMax = other.yClipMax;

    camera.set(other.camera);
    sun.set(other.sun);
    sky.set(other.sky);

    waterPlaneEnabled = other.waterPlaneEnabled;
    waterPlaneHeight = other.waterPlaneHeight;
    waterPlaneOffsetEnabled = other.waterPlaneOffsetEnabled;
    waterPlaneChunkClip = other.waterPlaneChunkClip;
    waterShading = other.waterShading.clone();

    hideUnknownBlocks = other.hideUnknownBlocks;

    spp = other.spp;
    renderTime = other.renderTime;

    resetReason = other.resetReason;

    finalized = false;

    if (samples != other.samples) {
      width = other.width;
      height = other.height;
      backBuffer = other.backBuffer;
      frontBuffer = other.frontBuffer;
      alphaChannel = other.alphaChannel;
      samples = other.samples;
    }

    fullWidth = other.fullWidth;
    fullHeight = other.fullHeight;
    cropX = other.cropX;
    cropY = other.cropY;

    octreeImplementation = other.octreeImplementation;

    animationTime = other.animationTime;

    renderer = other.renderer;
    previewRenderer = other.previewRenderer;

    additionalData = other.additionalData;
  }

  /**
   * Import scene state from another scene.
   */
  public synchronized void copyState(Scene other) {
    copyState(other, true);
  }

  /**
   * Save the scene description, render dump, and foliage
   * and grass textures.
   *
   * @throws IOException
   * @throws InterruptedException
   */
  public synchronized void saveScene(SceneIOProvider context, TaskTracker taskTracker)
      throws IOException {
    try (TaskTracker.Task task = taskTracker.task("Saving scene", 2)) {
      task.update(1);

      try (BufferedOutputStream out = new BufferedOutputStream(context.getSceneDescriptionOutputStream(name))) {
        saveDescription(out);
      }

      saveOctree(context, taskTracker);
      saveDump(context, taskTracker);
      saveEmitterGrid(context, taskTracker);
    }
  }

  /**
   * Load a stored scene by file name.
   *
   * @param sceneName file name of the scene to load
   */
  public synchronized void loadScene(RenderContext context, String sceneName, TaskTracker taskTracker)
      throws IOException {
    isLoading = true;
    try {
      try {
        loadDescription(context.getSceneDescriptionInputStream(sceneName));
      } catch (FileNotFoundException e) {
        // scene.json not found, try loading the backup file
        Log.info("Scene description file not found, trying to load the backup file instead", e);
        loadDescription(context.getSceneFileInputStream(sceneName + Scene.EXTENSION + ".backup"));
      }

      if (sdfVersion < SDF_VERSION) {
        Log.warn("Old scene version detected! The scene may not have been loaded correctly.");
      } else if (sdfVersion > SDF_VERSION) {
        Log.warn("This scene was created with a newer version of Chunky! The scene may not have been loaded correctly.");
      }

      // Load the configured skymap file.
      sky.reloadSkymap(context.getSceneDirectory());

      loadedWorld = EmptyWorld.INSTANCE;
      if (!worldPath.isEmpty()) {
        File worldDirectory = new File(worldPath);
        if (World.isWorldDir(worldDirectory)) {
          loadedWorld = World.loadWorld(worldDirectory, worldDimension, World.LoggedWarnings.NORMAL);
        } else {
          Log.info("Could not load world: " + worldPath);
        }
      }

      loadDump(context, taskTracker);

      if (spp == 0) {
        mode = RenderMode.PREVIEW;
      } else if (mode == RenderMode.RENDERING) {
        mode = RenderMode.PAUSED;
      }

      boolean emitterGridNeedChunkReload = false;
      boolean octreeLoaded = loadOctree(context, taskTracker);
      if (emitterSamplingStrategy != EmitterSamplingStrategy.NONE)
        emitterGridNeedChunkReload = !loadEmitterGrid(context, taskTracker);
      if (emitterGridNeedChunkReload || !octreeLoaded) {
        // Could not load stored octree or emitter grid.
        // Load the chunks from the world.
        if (loadedWorld == EmptyWorld.INSTANCE) {
          Log.warn("Could not load chunks (no world found for scene)");
        } else {
          loadChunks(taskTracker, loadedWorld, chunks);
        }
      }

      notifyAll();
    } finally {
      isLoading = false;
    }
  }

  /**
   * Set the exposure value
   */
  public synchronized void setExposure(double value) {
    exposure = value;
    if (mode == RenderMode.PREVIEW) {
      // don't interrupt the render if we are currently rendering
      refresh();
    }
  }

  /**
   * @return Current exposure value
   */
  public double getExposure() {
    return exposure;
  }

  /**
   * Set still water mode.
   */
  public void setStillWater(boolean value) {
    if (value != stillWater) {
      stillWater = value;
      refresh();
    }
  }

  /**
   * Set emitters enable flag.
   */
  public synchronized void setEmittersEnabled(boolean value) {
    if (value != emittersEnabled) {
      emittersEnabled = value;
      refresh();
    }
  }

  /**
   * Set sun sampling strategy.
   */
  public synchronized void setSunSamplingStrategy(SunSamplingStrategy strategy) {
    if (strategy != this.sunSamplingStrategy) {
      this.sunSamplingStrategy = strategy;
      refresh();
    }
  }

  /**
   * Get sun sampling strategy.
   */
  public SunSamplingStrategy getSunSamplingStrategy() {
    return this.sunSamplingStrategy;
  }

  /**
   * @return <code>true</code> if emitters are enabled
   */
  public boolean getEmittersEnabled() {
    return emittersEnabled;
  }

  /**
   * @return The <code>BlockPallete</code> for the scene
   */
  public BlockPalette getPalette() { return palette; }

  /**
   * Trace a ray in this scene. This offsets the ray origin to
   * move it into the scene coordinate space.
   */
  public void rayTrace(RayTracer rayTracer, WorkerState state) {
    state.ray.o.x -= origin.x;
    state.ray.o.y -= origin.y;
    state.ray.o.z -= origin.z;

    if(camera.getProjectionMode() == ProjectionMode.PARALLEL) {
      ParallelProjector.fixRay(state.ray, this);
    }

    rayTracer.trace(this, state);
  }

  /**
   * Find closest intersection between ray and scene.
   * This advances the ray by updating the ray origin if an intersection is found.
   *
   * @param ray ray to test against scene
   * @return <code>true</code> if an intersection was found
   */
  public boolean intersect(Ray ray) {
    boolean hit = false;

    if (Double.isNaN(ray.d.x) || Double.isNaN(ray.d.y) || Double.isNaN(ray.d.z) ||
        (ray.d.x == 0 && ray.d.y == 0 && ray.d.z == 0)) {
      if (!invalidWarn) {
        Log.warnf("Invalid ray with direction (%f, %f, %f).\n" +
            "This is a bug! Please report it at:\n" +
            "    https://github.com/chunky-dev/chunky/issues/",
            ray.d.x, ray.d.y, ray.d.z);
        invalidWarn = true;
      }
      ray.d.set(0, 1, 0);
    }

    if (entities.intersect(ray)) {
      hit = true;
    }
    if (worldIntersection(ray)) {
      hit = true;
    }
    if (hit) {
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
      updateOpacity(ray);
      return true;
    }
    return false;
  }

  /**
   * Test whether the ray intersects any voxel before exiting the Octree.
   *
   * @param ray   the ray
   * @return {@code true} if the ray intersects a voxel
   */
  private boolean worldIntersection(Ray ray) {
    Ray start = new Ray(ray);
    start.setCurrentMaterial(ray.getPrevMaterial(), ray.getPrevData());
    boolean hit = false;
    Ray r = new Ray(start);
    r.setCurrentMaterial(start.getPrevMaterial(), start.getPrevData());
    if (worldOctree.enterBlock(this, r, palette) && r.distance < ray.t) {
      ray.t = r.distance;
      ray.setNormal(r.getNormal());
      ray.color.set(r.color);
      ray.setPrevMaterial(r.getPrevMaterial(), r.getPrevData());
      ray.setCurrentMaterial(r.getCurrentMaterial(), r.getCurrentData());
      hit = true;
    }
    if (start.getCurrentMaterial().isWater()) {
      r = new Ray(start);
      r.setCurrentMaterial(start.getPrevMaterial(), start.getPrevData());
      if(waterOctree.exitWater(this, r, palette) && r.distance < ray.t - Ray.EPSILON) {
        ray.t = r.distance;
        ray.setNormal(r.getNormal());
        ray.color.set(r.color);
        ray.setPrevMaterial(r.getPrevMaterial(), r.getPrevData());
        ray.setCurrentMaterial(r.getCurrentMaterial(), r.getCurrentData());
        hit = true;
      } else if(ray.getPrevMaterial() == Air.INSTANCE) {
        ray.setPrevMaterial(Water.INSTANCE, 1 << Water.FULL_BLOCK);
      }
    } else {
      r = new Ray(start);
      r.setCurrentMaterial(start.getPrevMaterial(), start.getPrevData());
      if (waterOctree.enterBlock(this, r, palette) && r.distance < ray.t) {
        ray.t = r.distance;
        ray.setNormal(r.getNormal());
        ray.color.set(r.color);
        ray.setPrevMaterial(r.getPrevMaterial(), r.getPrevData());
        ray.setCurrentMaterial(r.getCurrentMaterial(), r.getCurrentData());
        hit = true;
      }
    }
    return hit;
  }

  public void updateOpacity(Ray ray) {
    if (ray.getCurrentMaterial().isWater() || (ray.getCurrentMaterial() == Air.INSTANCE
        && ray.getPrevMaterial().isWater())) {
      if (useCustomWaterColor) {
        ray.color.x = waterColor.x;
        ray.color.y = waterColor.y;
        ray.color.z = waterColor.z;
      } else {
        float[] waterColor = ray.getBiomeWaterColor(this);
        ray.color.x *= waterColor[0];
        ray.color.y *= waterColor[1];
        ray.color.z *= waterColor[2];
      }
      ray.color.w = waterOpacity;
    }
  }

  /**
   * Reload all loaded chunks.
   */
  public synchronized void reloadChunks(TaskTracker taskTracker) {
    if (loadedWorld == EmptyWorld.INSTANCE) {
      Log.warn("Can not reload chunks for scene - world directory not found!");
      return;
    }
    loadedWorld = World.loadWorld(loadedWorld.getWorldDirectory(), worldDimension, World.LoggedWarnings.NORMAL);
    loadChunks(taskTracker, loadedWorld, chunks);
    refresh();
  }

  /**
   * Load chunks into the octree.
   *
   * <p>This is the main method loading all voxels into the octree.
   * The octree finalizer is then run to compute block properties like fence
   * connectedness.
   */
  public synchronized void loadChunks(TaskTracker taskTracker, World world, Collection<ChunkPosition> chunksToLoad) {
    if (world == null)
      return;

    isLoading = true;

    yMin = yClipMin;
    yMax = yClipMax;

    Set<ChunkPosition> loadedChunks = new HashSet<>();
    int numChunks = 0;

    BiomeStructure.Factory biomeStructureFactory = BiomeStructure.get(this.biomeStructureImplementation);

    Dimension dimension = world.currentDimension();

    try (TaskTracker.Task task = taskTracker.task("(1/6) Loading regions")) {
      task.update(2, 1);

      loadedWorld = world;
      worldPath = loadedWorld.getWorldDirectory().getAbsolutePath();
      worldDimension = world.currentDimensionId();

      if (chunksToLoad.isEmpty()) {
        return;
      }

      int requiredDepth = calculateOctreeOrigin(chunksToLoad, false);

      // Create new octree to fit all chunks.
      palette = new BlockPalette();
      worldOctree = new Octree(octreeImplementation, requiredDepth);
      waterOctree = new Octree(octreeImplementation, requiredDepth);

      grassTexture = biomeStructureFactory.create();
      foliageTexture = biomeStructureFactory.create();
      waterTexture = biomeStructureFactory.create();

      if(emitterSamplingStrategy != EmitterSamplingStrategy.NONE)
        emitterGrid = new Grid(gridSize);

      // Parse the regions first - force chunk lists to be populated!
      Set<ChunkPosition> regions = new HashSet<>();
      for (ChunkPosition cp : chunksToLoad) {
        regions.add(cp.getRegionPosition());
      }

      for (ChunkPosition region : regions) {
        dimension.getRegion(region).parse(yMin, yMax);
      }
    }

    try (TaskTracker.Task task = taskTracker.task("(2/6) Loading entities")) {
     entities.loadPlayers(task, dimension);
    }

    BiomePalette biomePalette = new ArrayBiomePalette();

    Set<ChunkPosition> nonEmptyChunks = new HashSet<>();
    Set<ChunkPosition> legacyChunks = new HashSet<>();

    Position2IntStructure biomePaletteIdxStructure = biomeStructureFactory.createIndexStructure();
    boolean use3dBiomes = biomeStructureFactory.is3d();

    final Mutable<ChunkData> loadingChunkData = new Mutable<>(null); // chunkData currently being used for loading from save
    final Mutable<ChunkData> activeChunkData = new Mutable<>(null); // chunkData for loading into the octree

    try (TaskTracker.Task task = taskTracker.task("(3/6) Loading chunks")) {
      int done = 1;
      int target = chunksToLoad.size();

      ChunkPosition[] chunkPositions = chunksToLoad.toArray(new ChunkPosition[0]);

      int[] cubeWorldBlocks = new int[16*16*16];
      int[] cubeWaterBlocks = new int[16*16*16];

      ExecutorService executor = Executors.newSingleThreadExecutor();
      Future<?> nextChunkDataTask = executor.submit(() -> { //Initialise first chunk data for the for loop
        dimension.getChunk(chunkPositions[0]).getChunkData(loadingChunkData, palette, biomePalette, yMin, yMax);
        return null; // runnable can't throw non-RuntimeExceptions, so we use a callable instead and have to return something
      });
      for (int i = 0; i < chunkPositions.length; i++) {
        ChunkPosition cp = chunkPositions[i];

        task.updateEta(target, done);
        done += 1;

        ChunkData chunkData;
        try {
          //ensure task is complete
          nextChunkDataTask.get();
          //swap the chunkData mutables
          ChunkData loadedChunkData = loadingChunkData.get();
          loadingChunkData.set(activeChunkData.get());
          activeChunkData.set(loadedChunkData);
          chunkData = loadedChunkData;
        } catch(InterruptedException logged) { // If interrupted, stop loading
          Log.warn("Chunky loading interrupted.", logged);
          return;
        } catch(ExecutionException e) {
          if (e.getCause() instanceof ChunkLoadingException) {
            Log.warn(String.format("Failed to load chunk %s", cp), e.getCause());
            continue;
          } else {
            throw new RuntimeException(e.getCause());
          }
        } finally { // we always want to schedule the next task even if the current one throws an exception
          if (i + 1 < chunkPositions.length) { // schedule next task if possible
            final int finalI = i;
            nextChunkDataTask = executor.submit(() -> { //request chunk data for the next iteration of the loop
              dimension.getChunk(chunkPositions[finalI + 1]).getChunkData(loadingChunkData, palette, biomePalette, yMin, yMax);
              return null; // runnable can't throw non-RuntimeExceptions, so we use a callable instead and have to return something
            });
          }
        }

        if (loadedChunks.contains(cp)) {
          continue;
        }

        loadedChunks.add(cp);

        if (chunkData == null) {
          chunkData = EmptyChunkData.INSTANCE;
        }

        numChunks += 1;

        int wx0 = cp.x * 16; // Start of this chunk in world coordinates.
        int wz0 = cp.z * 16;
        BiomeData biomeData = chunkData.getBiomeData();

        if (use3dBiomes) {
          for (int y = chunkData.minY(); y < chunkData.maxY(); y++) {
            for (int cz = 0; cz < 16; ++cz) {
              int wz = cz + wz0;
              for (int cx = 0; cx < 16; ++cx) {
                int wx = cx + wx0;
                int biomePaletteIdx = biomeData.getBiome(cx, y, cz);
                biomePaletteIdxStructure.set(wx, y, wz, biomePaletteIdx);
              }
            }
          }
        } else {
          for (int cz = 0; cz < 16; ++cz) {
            int wz = cz + wz0;
            for (int cx = 0; cx < 16; ++cx) {
              int wx = cx + wx0;
              int biomePaletteIdx = biomeData.getBiome(cx, chunkData.minY(), cz); // TODO: add an option to set the biome sample height?
              biomePaletteIdxStructure.set(wx, chunkData.minY(), wz, biomePaletteIdx);
            }
          }
        }

        entities.loadEntitiesInChunk(this, chunkData);

        int yCubeMin = yMin / 16;
        int yCubeMax = (yMax+15) / 16;
        for(int yCube = yCubeMin; yCube < yCubeMax; ++yCube) {
          // Reset the cubes
          Arrays.fill(cubeWorldBlocks, 0);
          Arrays.fill(cubeWaterBlocks, 0);
          for(int cy = 0; cy < 16; ++cy) { //Uses chunk min and max, rather than global - minor optimisation for pre1.13 worlds
            int y = yCube * 16 + cy;
            if(y < yMin || y >= yMax)
              continue;
            for(int cz = 0; cz < 16; ++cz) {
              int z = cz + cp.z * 16 - origin.z;
              for(int cx = 0; cx < 16; ++cx) {
                int x = cx + cp.x * 16 - origin.x;

                int cubeIndex = (cz * 16 + cy) * 16 + cx;

                // Change the type of hidden blocks to ANY_TYPE
                boolean onEdge = y <= yMin || y >= yMax - 1 || chunkData.isBlockOnEdge(cx, y, cz);
                boolean isHidden = !onEdge
                        && palette.get(chunkData.getBlockAt(cx + 1, y, cz)).opaque
                        && palette.get(chunkData.getBlockAt(cx - 1, y, cz)).opaque
                        && palette.get(chunkData.getBlockAt(cx, y + 1, cz)).opaque
                        && palette.get(chunkData.getBlockAt(cx, y - 1, cz)).opaque
                        && palette.get(chunkData.getBlockAt(cx, y, cz + 1)).opaque
                        && palette.get(chunkData.getBlockAt(cx, y, cz - 1)).opaque;

                if(isHidden) {
                  cubeWorldBlocks[cubeIndex] = Octree.ANY_TYPE;
                } else {
                  int currentBlock = chunkData.getBlockAt(cx, y, cz);
                  int octNode = currentBlock;
                  Block block = palette.get(currentBlock);

                  if(block.isEntity()) {
                    Vector3 position = new Vector3(cx + cp.x * 16, y, cz + cp.z * 16);
                    Entity entity = block.toEntity(position);

                    if (entities.shouldLoad(entity)) {
                      if(entity instanceof Poseable && !(entity instanceof Lectern && !((Lectern) entity).hasBook())) {
                        entities.addActor(entity);
                      } else {
                        entities.addEntity(entity);
                        if (emitterGrid != null) {
                          for (Grid.EmitterPosition emitterPos : entity.getEmitterPosition()) {
                            emitterPos.x -= origin.x;
                            emitterPos.y -= origin.y;
                            emitterPos.z -= origin.z;
                            emitterGrid.addEmitter(emitterPos);
                          }
                        }
                      }

                      if(!block.isBlockWithEntity()) {
                        if(block.waterlogged) {
                          block = palette.water;
                          octNode = palette.waterId;
                        } else {
                          block = Air.INSTANCE;
                          octNode = palette.airId;
                        }
                      }
                    }
                  }

                  if(block.isWaterFilled()) {
                    int waterNode = palette.waterId;
                    if(y + 1 < yMax) {
                      if(palette.get(chunkData.getBlockAt(cx, y + 1, cz)).isWaterFilled()) {
                        waterNode = palette.getWaterId(0, 1 << Water.FULL_BLOCK);
                      }
                    }
                    if(block.isWater()) {
                      // Move plain water blocks to the water octree.
                      octNode = palette.airId;

                      if(!onEdge) {
                        // Perform water computation now for water blocks that are not on th edge of the chunk
                        // Test if the block has not already be marked as full
                        if(((Water) palette.get(waterNode)).data == 0) {
                          int level0 = 8 - ((Water) block).level;
                          int corner0 = level0;
                          int corner1 = level0;
                          int corner2 = level0;
                          int corner3 = level0;

                          int level = Chunk.waterLevelAt(chunkData, palette, cx - 1, y, cz, level0);
                          corner3 += level;
                          corner0 += level;

                          level = Chunk.waterLevelAt(chunkData, palette, cx - 1, y, cz + 1, level0);
                          corner0 += level;

                          level = Chunk.waterLevelAt(chunkData, palette, cx, y, cz + 1, level0);
                          corner0 += level;
                          corner1 += level;

                          level = Chunk.waterLevelAt(chunkData, palette, cx + 1, y, cz + 1, level0);
                          corner1 += level;

                          level = Chunk.waterLevelAt(chunkData, palette, cx + 1, y, cz, level0);
                          corner1 += level;
                          corner2 += level;

                          level = Chunk.waterLevelAt(chunkData, palette, cx + 1, y, cz - 1, level0);
                          corner2 += level;

                          level = Chunk.waterLevelAt(chunkData, palette, cx, y, cz - 1, level0);
                          corner2 += level;
                          corner3 += level;

                          level = Chunk.waterLevelAt(chunkData, palette, cx - 1, y, cz - 1, level0);
                          corner3 += level;

                          corner0 = Math.min(7, 8 - (corner0 / 4));
                          corner1 = Math.min(7, 8 - (corner1 / 4));
                          corner2 = Math.min(7, 8 - (corner2 / 4));
                          corner3 = Math.min(7, 8 - (corner3 / 4));
                          waterNode = palette.getWaterId(((Water) block).level, (corner0 << Water.CORNER_0)
                                          | (corner1 << Water.CORNER_1)
                                          | (corner2 << Water.CORNER_2)
                                          | (corner3 << Water.CORNER_3));
                        }
                      } else {
                        // Water computation for water blocks on the edge of a chunk is done by the OctreeFinalizer but we need the water level information
                        waterNode = palette.getWaterId(((Water) block).level, 0);
                      }
                    }
                    cubeWaterBlocks[cubeIndex] = waterNode;
                  } else if(y + 1 < yMax && block instanceof Lava) {
                    if(palette.get(chunkData.getBlockAt(cx, y + 1, cz)) instanceof Lava) {
                      octNode = palette.getLavaId(0, 1 << Water.FULL_BLOCK);
                    } else if(!onEdge) {
                      // Compute lava level for blocks not on edge
                      Lava lava = (Lava) block;
                      int level0 = 8 - lava.level;
                      int corner0 = level0;
                      int corner1 = level0;
                      int corner2 = level0;
                      int corner3 = level0;

                      int level = Chunk.lavaLevelAt(chunkData, palette, cx - 1, y, cz, level0);
                      corner3 += level;
                      corner0 += level;

                      level = Chunk.lavaLevelAt(chunkData, palette, cx - 1, y, cz + 1, level0);
                      corner0 += level;

                      level = Chunk.lavaLevelAt(chunkData, palette, cx, y, cz + 1, level0);
                      corner0 += level;
                      corner1 += level;

                      level = Chunk.lavaLevelAt(chunkData, palette, cx + 1, y, cz + 1, level0);
                      corner1 += level;

                      level = Chunk.lavaLevelAt(chunkData, palette, cx + 1, y, cz, level0);
                      corner1 += level;
                      corner2 += level;

                      level = Chunk.lavaLevelAt(chunkData, palette, cx + 1, y, cz - 1, level0);
                      corner2 += level;

                      level = Chunk.lavaLevelAt(chunkData, palette, cx, y, cz - 1, level0);
                      corner2 += level;
                      corner3 += level;

                      level = Chunk.lavaLevelAt(chunkData, palette, cx - 1, y, cz - 1, level0);
                      corner3 += level;

                      corner0 = Math.min(7, 8 - (corner0 / 4));
                      corner1 = Math.min(7, 8 - (corner1 / 4));
                      corner2 = Math.min(7, 8 - (corner2 / 4));
                      corner3 = Math.min(7, 8 - (corner3 / 4));
                      octNode = palette.getLavaId(
                              lava.level,
                              (corner0 << Water.CORNER_0)
                                      | (corner1 << Water.CORNER_1)
                                      | (corner2 << Water.CORNER_2)
                                      | (corner3 << Water.CORNER_3)
                      );
                    }
                  }
                  cubeWorldBlocks[cubeIndex] = octNode;

                  if(emitterGrid != null && block.emittance > 1e-4) {
                    // X and Z are Chunky position but Y is world position
                    emitterGrid.addEmitter(new Grid.EmitterPosition(x, y - origin.y, z, block));
                  }
                }
              }
            }
          }
          worldOctree.setCube(4, cubeWorldBlocks, cp.x*16 - origin.x, yCube*16 - origin.y, cp.z*16 - origin.z);
          waterOctree.setCube(4, cubeWaterBlocks, cp.x*16 - origin.x, yCube*16 - origin.y, cp.z*16 - origin.z);
        }

        // Block entities are also called "tile entities". These are extra bits of metadata
        // about certain blocks or entities.
        // Block entities are loaded after the base block data so that metadata can be updated.
        for (CompoundTag entityTag : chunkData.getTileEntities()) {
          int y = entityTag.get("y").intValue(0);
          if (y >= yMin && y < yMax) {
            int x = entityTag.get("x").intValue(0) - wx0; // Chunk-local coordinates.
            int z = entityTag.get("z").intValue(0) - wz0;
            if (x < 0 || x > 15 || z < 0 || z > 15) {
              // Block entity is out of range (bad chunk data?), ignore it
              continue;
            }
            Block block = palette.get(chunkData.getBlockAt(x, y, z));
            // Metadata is the old block data (to be replaced in future Minecraft versions?).
            Vector3 position = new Vector3(x + wx0, y, z + wz0);
            if (block.isModifiedByBlockEntity()) {
              Tag newTag = block.getNewTagWithBlockEntity(palette.getBlockSpec(chunkData.getBlockAt(x, y, z)).getTag(), entityTag);
              if (newTag != null) {
                int id = palette.put(newTag);
                block = palette.get(id);
                chunkData.setBlockAt(x, y, z, id);
                worldOctree.set(id, cp.x * 16 + x - origin.x, y - origin.y, cp.z * 16 + z - origin.z);
              }
            }
            if (block.isBlockEntity()) {
              Entity blockEntity = block.toBlockEntity(position, entityTag);
              if (blockEntity == null) {
                continue;
              }

              if (entities.shouldLoad(blockEntity)) {
                if (blockEntity instanceof Poseable) {
                  entities.addActor(blockEntity);
                } else {
                  entities.addEntity(blockEntity);
                  if (emitterGrid != null) {
                    for (Grid.EmitterPosition emitterPos : blockEntity.getEmitterPosition()) {
                      emitterPos.x -= origin.x;
                      emitterPos.y -= origin.y;
                      emitterPos.z -= origin.z;
                      emitterGrid.addEmitter(emitterPos);
                    }
                  }
                }
              }
            }
          }
        }

        if (!chunkData.isEmpty()){
          nonEmptyChunks.add(cp);
          if (dimension.getChunk(cp).getVersion() == ChunkVersion.PRE_FLATTENING) {
            legacyChunks.add(cp);
          }
        }
      }
      executor.shutdown();
    }

    entities.finalizeLoading();
    palette.unsynchronize();

    try (TaskTracker.Task task = taskTracker.task("(4/6) Finalizing octree")) {

      worldOctree.startFinalization();
      waterOctree.startFinalization();

      int done = 0;
      int target = nonEmptyChunks.size();
      for (ChunkPosition cp : nonEmptyChunks) {
//        TODO: make this less special cased in some way, having 2 ifs for biomeBlending and use3dBiomes is quite awful to read and maintain

//        Finalize grass and foliage textures.
//        3x3 box blur.
        if (biomeBlending) {
          if (use3dBiomes) {
            for (int sectionY = yMin >> 4; sectionY < (yMax - 1 >> 4) + 1; sectionY++) {
              for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                  for (int y = 0; y < 16; y++) {
                    int nsum = 0;

                    float[] grassMix = {0, 0, 0};
                    float[] foliageMix = {0, 0, 0};
                    float[] waterMix = {0, 0, 0};
                    for (int sx = x - 1; sx <= x + 1; ++sx) {
                      int wx = cp.x * 16 + sx;
                      for (int sz = z - 1; sz <= z + 1; ++sz) {
                        int wz = cp.z * 16 + sz;
                        for (int sy = y - 1; sy < y + 1; sy++) {
                          int wy = sectionY * 16 + sy;
                          ChunkPosition ccp = new ChunkPosition(wx >> 4, wz >> 4);
                          if (nonEmptyChunks.contains(ccp)) {
                            nsum += 1;
                            Integer id = biomePaletteIdxStructure.get(wx, wy, wz);
                            if (id == null) {
                              continue;
                            }
                            Biome biome = biomePalette.get(id);
                            float[] grassColor = biome.grassColorLinear;
                            grassMix[0] += grassColor[0];
                            grassMix[1] += grassColor[1];
                            grassMix[2] += grassColor[2];
                            float[] foliageColor = biome.foliageColorLinear;
                            foliageMix[0] += foliageColor[0];
                            foliageMix[1] += foliageColor[1];
                            foliageMix[2] += foliageColor[2];
                            float[] waterColor = biome.waterColorLinear;
                            waterMix[0] += waterColor[0];
                            waterMix[1] += waterColor[1];
                            waterMix[2] += waterColor[2];
                          }
                        }
                      }
                    }
                    grassMix[0] /= nsum;
                    grassMix[1] /= nsum;
                    grassMix[2] /= nsum;
                    grassTexture.set(cp.x * 16 + x - origin.x, sectionY * 16 + y - origin.y, cp.z * 16 + z - origin.z, grassMix);

                    foliageMix[0] /= nsum;
                    foliageMix[1] /= nsum;
                    foliageMix[2] /= nsum;
                    foliageTexture.set(cp.x * 16 + x - origin.x, sectionY * 16 + y - origin.y, cp.z * 16 + z - origin.z, foliageMix);

                    waterMix[0] /= nsum;
                    waterMix[1] /= nsum;
                    waterMix[2] /= nsum;
                    waterTexture.set(cp.x * 16 + x - origin.x, sectionY * 16 + y - origin.y, cp.z * 16 + z - origin.z, waterMix);
                  }
                }
              }
            }
          } else {
            for (int x = 0; x < 16; ++x) {
              for (int z = 0; z < 16; ++z) {

                int nsum = 0;
                float[] grassMix = {0, 0, 0};
                float[] foliageMix = {0, 0, 0};
                float[] waterMix = {0, 0, 0};
                for (int sx = x - 1; sx <= x + 1; ++sx) {
                  int wx = cp.x * 16 + sx;
                  for (int sz = z - 1; sz <= z + 1; ++sz) {
                    int wz = cp.z * 16 + sz;

                    ChunkPosition ccp = new ChunkPosition(wx >> 4, wz >> 4);
                    if (nonEmptyChunks.contains(ccp)) {
                      nsum += 1;
                      Biome biome = biomePalette.get(biomePaletteIdxStructure.get(wx, 0, wz));
                      float[] grassColor = biome.grassColorLinear;
                      grassMix[0] += grassColor[0];
                      grassMix[1] += grassColor[1];
                      grassMix[2] += grassColor[2];
                      float[] foliageColor = biome.foliageColorLinear;
                      foliageMix[0] += foliageColor[0];
                      foliageMix[1] += foliageColor[1];
                      foliageMix[2] += foliageColor[2];
                      float[] waterColor = biome.waterColorLinear;
                      waterMix[0] += waterColor[0];
                      waterMix[1] += waterColor[1];
                      waterMix[2] += waterColor[2];
                    }
                  }
                }
                grassMix[0] /= nsum;
                grassMix[1] /= nsum;
                grassMix[2] /= nsum;
                grassTexture.set(cp.x * 16 + x - origin.x, 0, cp.z * 16 + z - origin.z, grassMix);

                foliageMix[0] /= nsum;
                foliageMix[1] /= nsum;
                foliageMix[2] /= nsum;
                foliageTexture.set(cp.x * 16 + x - origin.x, 0, cp.z * 16 + z - origin.z, foliageMix);

                waterMix[0] /= nsum;
                waterMix[1] /= nsum;
                waterMix[2] /= nsum;
                waterTexture.set(cp.x * 16 + x - origin.x, 0, cp.z * 16 + z - origin.z, waterMix);
              }
            }
          }
        } else {
          if (use3dBiomes) {
            for (int sectionY = yMin >> 4; sectionY < (yMax - 1 >> 4) + 1; sectionY++) {
              for (int y = 0; y < 16; y++) {
                int wy = sectionY * 16 + y;
                for (int x = 0; x < 16; ++x) {
                  int wx = cp.x * Chunk.X_MAX + x;
                  for (int z = 0; z < 16; ++z) {
                    int wz = cp.z * Chunk.Z_MAX + z;
                    int nsum = 0;

                    Integer id = biomePaletteIdxStructure.get(wx, wy, wz);
                    if (id == null) {
                      continue;
                    }
                    if(id != 0) {
                      int asd = 0;
                    }

                    Biome biome = biomePalette.get(id);
                    grassTexture.set(cp.x * 16 + x - origin.x, sectionY * 16 + y - origin.y, cp.z * 16 + z - origin.z, biome.grassColorLinear);
                    foliageTexture.set(cp.x * 16 + x - origin.x, sectionY * 16 + y - origin.y, cp.z * 16 + z - origin.z, biome.foliageColorLinear);
                    waterTexture.set(cp.x * 16 + x - origin.x, sectionY * 16 + y - origin.y, cp.z * 16 + z - origin.z, biome.waterColorLinear);
                  }
                }
              }
            }
          } else {
            for (int x = 0; x < 16; ++x) {
              int wx = cp.x * 16 + x;
              for (int z = 0; z < 16; ++z) {
                int wz = cp.z * 16 + z;

                int id = biomePaletteIdxStructure.get(wx, 0, wz);
                Biome biome = biomePalette.get(id);

                grassTexture.set(cp.x * 16 + x - origin.x, 0, cp.z * 16 + z - origin.z, biome.grassColorLinear);
                foliageTexture.set(cp.x * 16 + x - origin.x, 0, cp.z * 16 + z - origin.z, biome.foliageColorLinear);
                waterTexture.set(cp.x * 16 + x - origin.x, 0, cp.z * 16 + z - origin.z, biome.waterColorLinear);
              }
            }
          }
        }
        task.updateEta(target, done);
        done += 1;
        OctreeFinalizer.finalizeChunk(worldOctree, waterOctree, palette, origin, cp, yMin, yMax);
        if (legacyChunks.contains(cp)) {
          LegacyBlocksFinalizer
              .finalizeChunk(worldOctree, waterOctree, palette, origin, cp, yMin, yMax);
        }
      }

      worldOctree.endFinalization();
      waterOctree.endFinalization();

      grassTexture.compact();
      foliageTexture.compact();
      waterTexture.compact();
    }

    entities.loadDataFromOctree(worldOctree, palette, origin);

    if (emitterGrid != null)
      emitterGrid.prepare();

    chunks = loadedChunks;
    camera.setWorldSize(1 << worldOctree.getDepth());
    try (TaskTracker.Task task = taskTracker.task("(5/6) Building world BVH")) {
      buildBvh(task);
    }
    try (TaskTracker.Task task = taskTracker.task("(6/6) Building actor BVH")) {
      buildActorBvh(task);
    }
    Log.info(String.format("Loaded %d chunks", numChunks));

    isLoading = false;
  }

  private void buildBvh(TaskTracker.Task task) {
    entities.buildBvh(task, origin);
  }

  private void buildActorBvh(TaskTracker.Task task) {
    entities.buildActorBvh(task, origin);
  }

  /**
   * Rebuild the actors and the other blocks bounding volume hierarchy.
   */
  public void rebuildBvh() {
    buildBvh(TaskTracker.Task.NONE);
    buildActorBvh(TaskTracker.Task.NONE);
    refresh();
  }

  /**
   * Rebuild the actors bounding volume hierarchy.
   */
  public void rebuildActorBvh() {
    buildActorBvh(TaskTracker.Task.NONE);
    refresh();
  }

  private int calculateOctreeOrigin(Collection<ChunkPosition> chunksToLoad, boolean centerOctree) {
    int xmin = Integer.MAX_VALUE;
    int xmax = Integer.MIN_VALUE;
    int zmin = Integer.MAX_VALUE;
    int zmax = Integer.MIN_VALUE;
    for (ChunkPosition cp : chunksToLoad) {
      if (cp.x < xmin) {
        xmin = cp.x;
      }
      if (cp.x > xmax) {
        xmax = cp.x;
      }
      if (cp.z < zmin) {
        zmin = cp.z;
      }
      if (cp.z > zmax) {
        zmax = cp.z;
      }
    }

    xmax += 1;
    zmax += 1;
    xmin *= 16;
    xmax *= 16;
    zmin *= 16;
    zmax *= 16;

    int maxDimension = Math.max(yMax - yMin, Math.max(xmax - xmin, zmax - zmin));
    int requiredDepth = QuickMath.log2(QuickMath.nextPow2(maxDimension));

    if(centerOctree) {
      int xroom = (1 << requiredDepth) - (xmax - xmin);
      int yroom = (1 << requiredDepth) - (yMax - yMin);
      int zroom = (1 << requiredDepth) - (zmax - zmin);

      origin.set(xmin - xroom / 2, -yroom / 2, zmin - zroom / 2);
    } else {
      // Note: Math.floorDiv rather than integer division for round toward -infinity
      origin.set(xmin, Math.floorDiv(yMin, 16) * 16, zmin);
    }
    return requiredDepth;
  }

  /**
   * @return <code>true</code> if the scene has loaded chunks
   */
  public synchronized boolean haveLoadedChunks() {
    return !chunks.isEmpty();
  }

  /**
   * Calculate a camera position centered above all loaded chunks.
   *
   * @return The calculated camera position
   */
  public Vector3 calcCenterCamera() {
    if (chunks.isEmpty()) {
      return new Vector3(0, 128, 0);
    }

    int xmin = Integer.MAX_VALUE;
    int xmax = Integer.MIN_VALUE;
    int zmin = Integer.MAX_VALUE;
    int zmax = Integer.MIN_VALUE;
    for (ChunkPosition cp : chunks) {
      if (cp.x < xmin) {
        xmin = cp.x;
      }
      if (cp.x > xmax) {
        xmax = cp.x;
      }
      if (cp.z < zmin) {
        zmin = cp.z;
      }
      if (cp.z > zmax) {
        zmax = cp.z;
      }
    }
    xmax += 1;
    zmax += 1;
    xmin *= 16;
    xmax *= 16;
    zmin *= 16;
    zmax *= 16;
    int xcenter = (xmax + xmin) / 2;
    int zcenter = (zmax + zmin) / 2;
    int ycenter = (yMax + yMin) / 2;
    for (int y = Math.min(ycenter+127, yMax); y >= Math.max(ycenter-128, yMin); --y) {
      Material block = worldOctree.getMaterial(xcenter - origin.x, y - origin.y, zcenter - origin.z,
          palette);
      if (!(block instanceof Air)) {
        return new Vector3(xcenter, y + 5, zcenter);
      }
    }
    return new Vector3(xcenter, 128, zcenter);
  }

  /**
   * Set the biome colors flag.
   */
  public void setBiomeColorsEnabled(boolean value) {
    if (value != biomeColors) {
      biomeColors = value;
      refresh();
    }
  }

  public void setBiomeBlendingEnabled(boolean value) {
    if (value != biomeBlending) {
      biomeBlending = value;
      refresh();
    }
  }

  /**
   * Center the camera over the loaded chunks
   */
  public synchronized void moveCameraToCenter() {
    camera.setPosition(calcCenterCamera());
  }

  /**
   * @return The name of this scene
   */
  public String name() {
    return name;
  }

  /**
   * Start rendering. This wakes up threads waiting on a scene
   * state change, even if the scene state did not actually change.
   */
  public synchronized void startHeadlessRender() {
    mode = RenderMode.RENDERING;
    notifyAll();
  }

  /**
   * @return <code>true</code> if the rendering of this scene should be
   * restarted
   */
  public boolean shouldRefresh() {
    return resetReason != ResetReason.NONE;
  }

  /**
   * Start rendering the scene.
   */
  public synchronized void startRender() {
    if (mode == RenderMode.PAUSED) {
      mode = RenderMode.RENDERING;
      notifyAll();
    } else if (mode != RenderMode.RENDERING) {
      mode = RenderMode.RENDERING;
      refresh();
    }
  }

  /**
   * Pause the renderer.
   */
  public synchronized void pauseRender() {
    mode = RenderMode.PAUSED;

    // Wake up threads in awaitSceneStateChange().
    notifyAll();
  }

  /**
   * Halt the rendering process.
   * Puts the renderer back in preview mode.
   */
  public synchronized void haltRender() {
    if (mode != RenderMode.PREVIEW) {
      mode = RenderMode.PREVIEW;
      resetReason = ResetReason.MODE_CHANGE;
      forceReset = true;
      refresh();
    }
  }

  /**
   * @return <code>true</code> if still water is enabled
   */
  public boolean stillWaterEnabled() {
    return stillWater;
  }

  /**
   * @return <code>true</code> if biome colors are enabled
   */
  public boolean biomeColorsEnabled() {
    return biomeColors;
  }

  public boolean biomeBlendingEnabled() {
    return biomeBlending;
  }

  /**
   * Set the recursive ray depth limit
   */
  public synchronized void setRayDepth(int value) {
    value = Math.max(1, value);
    if (rayDepth != value) {
      rayDepth = value;
      PersistentSettings.setRayDepth(rayDepth);
      refresh();
    }
  }

  /**
   * @return Recursive ray depth limit
   */
  public int getRayDepth() {
    return rayDepth;
  }

  /**
   * Clear the scene refresh flag
   */
  synchronized public void clearResetFlags() {
    resetReason = ResetReason.NONE;
    forceReset = false;
  }

  /**
   * Trace a ray in the Octree towards the current view target.
   * The ray is displaced to the target position if it hits something.
   *
   * <p>The view target is defined by the current camera state.
   *
   * @return {@code true} if the ray hit something
   */
  public boolean traceTarget(Ray ray) {
    WorkerState state = new WorkerState();
    state.ray = ray;
    if (isInWater(ray)) {
      ray.setCurrentMaterial(Water.INSTANCE);
    } else {
      ray.setCurrentMaterial(Air.INSTANCE);
    }
    camera.getTargetDirection(ray);
    ray.o.x -= origin.x;
    ray.o.y -= origin.y;
    ray.o.z -= origin.z;
    while (PreviewRayTracer.nextIntersection(this, ray, null,
      new IntersectionConfig(true,
        false, waterPlaneEnabled, true))) {
      if (ray.getCurrentMaterial() != Air.INSTANCE) {
        return true;
      }
    }
    return false;
  }

  /**
   * Perform auto focus.
   */
  public void autoFocus() {
    camera.autoFocus(this::traceTarget);
  }

  /**
   * Find the current camera target position.
   *
   * @return {@code null} if the camera is not aiming at some intersectable object
   */
  public Vector3 getTargetPosition() {
    Ray ray = new Ray();
    if (!traceTarget(ray)) {
      return null;
    } else {
      Vector3 target = new Vector3(ray.o);
      target.add(origin.x, origin.y, origin.z);
      return target;
    }
  }

  /**
   * @return World origin in the Octree
   */
  public Vector3i getOrigin() {
    return origin;
  }

  /**
   * Set the scene name.
   */
  public void setName(String newName) {
    newName = AsynchronousSceneManager.sanitizedSceneName(newName);
    if (newName.length() > 0) {
      name = newName;
    }
  }

  /**
   * @return The current postprocessing filter
   */
  public PostProcessingFilter getPostProcessingFilter() {
    return postProcessingFilter;
  }

  /**
   * Change the postprocessing filter
   *
   * @param p The new postprocessing filter
   */
  public synchronized void setPostprocess(PostProcessingFilter p) {
    postProcessingFilter = p;
    if (mode == RenderMode.PREVIEW) {
      // Don't interrupt the render if we are currently rendering.
      refresh();
    }
  }

  /**
   * @return The current emitter intensity
   */
  public double getEmitterIntensity() {
    return emitterIntensity;
  }

  /**
   * Set the emitter intensity.
   */
  public void setEmitterIntensity(double value) {
    emitterIntensity = value;
    refresh();
  }

  /**
   * Set the transparent sky option.
   */
  public void setTransparentSky(boolean value) {
    if (value != transparentSky) {
      transparentSky = value;
      refresh();
    }
  }

  /**
   * @return {@code true} if transparent sky is enabled
   */
  public boolean transparentSky() {
    return transparentSky;
  }

  /**
   * Set the water world mode option.
   */
  public void setWaterPlaneEnabled(boolean enabled) {
    if (enabled != waterPlaneEnabled) {
      waterPlaneEnabled = enabled;
      refresh();
    }
  }

  /**
   * @return {@code true} if the water world mode is enabled
   */
  public boolean isWaterPlaneEnabled() {
    return waterPlaneEnabled;
  }

  /**
   * Set the water world mode ocean height.
   */
  public void setWaterPlaneHeight(double height) {
    if (height != waterPlaneHeight) {
      waterPlaneHeight = height;
      refresh();
    }
  }

  /**
   * @return The water world mode ocean height
   */
  public double getWaterPlaneHeight() {
    return waterPlaneHeight;
  }
  /**
   * @return The effective water world mode ocean height influenced by waterPlaneOffsetEnabled
   */
  public double getEffectiveWaterPlaneHeight() {
    if(waterPlaneOffsetEnabled) {
      return waterPlaneHeight - Water.TOP_BLOCK_GAP;
    } else {
      return waterPlaneHeight;
    }
  }

  /**
   * Set the water world mode height offset option.
   */
  public void setWaterPlaneOffsetEnabled(boolean enabled) {
    if (enabled != waterPlaneOffsetEnabled) {
      waterPlaneOffsetEnabled = enabled;
      refresh();
    }
  }

  /**
   * @return {@code true} if the water world mode height offset is enabled
   */
  public boolean isWaterPlaneOffsetEnabled() {
    return waterPlaneOffsetEnabled;
  }

  public void setWaterPlaneChunkClip(boolean enabled) {
    if (enabled != waterPlaneChunkClip) {
      waterPlaneChunkClip = enabled;
      refresh();
    }
  }

  /**
   * Check if water plane chunk clipping is enabled. If so, the water plane is hidden in loaded
   * chunks (i.e. it is ignored inside of loaded chunks).
   * @return {@code true} if the water plane chunk clipping is enabled
   */
  public boolean getWaterPlaneChunkClip() {
    return waterPlaneChunkClip;
  }

  /**
   * @return the dumpFrequency
   */
  public int getDumpFrequency() {
    return dumpFrequency;
  }

  /**
   * @param value the dumpFrequency to set, if value is zero then render dumps
   *              are disabled
   */
  public void setDumpFrequency(int value) {
    value = Math.max(0, value);
    if (value != dumpFrequency) {
      dumpFrequency = value;
    }
  }

  /**
   * @return the saveDumps
   */
  public boolean shouldSaveDumps() {
    return dumpFrequency > 0;
  }

  /**
   * Copy scene state that does not require a render restart.
   *
   * @param other scene to copy transient state from.
   */
  public synchronized void copyTransients(Scene other) {
    name = other.name;
    postProcessingFilter = other.postProcessingFilter;
    exposure = other.exposure;
    dumpFrequency = other.dumpFrequency;
    saveSnapshots = other.saveSnapshots;
    sppTarget = other.sppTarget;
    branchCount = other.branchCount;
    rayDepth = other.rayDepth;
    mode = other.mode;
    outputMode = other.outputMode;
    cameraPresets = other.cameraPresets;
    camera.copyTransients(other.camera);
    finalizeBuffer = other.finalizeBuffer;
    animationTime = other.animationTime;
    additionalData = other.additionalData;
  }

  /**
   * @return The target SPP
   */
  public int getTargetSpp() {
    return sppTarget;
  }

  /**
   * @param value Target SPP value
   */
  public void setTargetSpp(int value) {
    sppTarget = value;
  }

  /**
   * @return The "nominal" value of the branch count.
   */
  public int getBranchCount() {
      return branchCount;
  }

  /**
   * Usually the same as getBranchCount, but reduces branch count to 1 for first few SPP.
   *
   * @return The current "true" branch count
   */
  public int getCurrentBranchCount() {
    if(spp < branchCount) {
      if(spp <= Math.sqrt(branchCount)) { // This is arbitrary, but should be a good compromise in most cases
        return 1;
      } else {
        return branchCount - spp;
      }
    } else {
      return branchCount;
    }
  }

  /**
   * @param value Branch count value
   */
  public void setBranchCount(int value) {
    branchCount = value;
  }

  /**
   * Change the canvas size for this scene. This will refresh
   * the scene and reinitialize the sample buffers if the
   * new canvas size is not identical to the current canvas size.
   */
  public synchronized void setCanvasSize(int canvasWidth, int canvasHeight) {
    int newWidth = Math.max(MIN_CANVAS_WIDTH, canvasWidth);
    int newHeight = Math.max(MIN_CANVAS_HEIGHT, canvasHeight);
    if (newWidth != width || newHeight != height) {
      width = newWidth;
      height = newHeight;
      initBuffers();
      refresh();
    }
  }

  public synchronized void setCanvasCropSize(int canvasWidth, int canvasHeight, int fullWidth, int fullHeight, int cropX, int cropY) {
    canvasWidth = Math.max(MIN_CANVAS_WIDTH, canvasWidth);
    canvasHeight = Math.max(MIN_CANVAS_HEIGHT, canvasHeight);
    if (fullWidth == 0 || fullHeight == 0) {
      // Crop disabled
      fullWidth = 0;
      fullHeight = 0;
      cropX = 0;
      cropY = 0;
    } else {
      // Crop enabled
      fullWidth = Math.max(canvasWidth(), fullWidth);
      fullHeight = Math.max(canvasHeight(), fullHeight);
      cropX = QuickMath.clamp(cropX, 0, fullWidth-canvasWidth());
      cropY = QuickMath.clamp(cropY, 0, fullHeight-canvasHeight());
    }
    boolean changed = false;
    if (fullWidth != this.fullWidth || fullHeight != this.fullHeight || cropX != this.cropX || cropY != this.cropY) {
      changed = true;
      this.fullWidth = fullWidth;
      this.fullHeight = fullHeight;
      this.cropX = cropX;
      this.cropY = cropY;
    }
    if (canvasWidth != this.width || canvasHeight != this.height) {
      changed = true;
      this.width = canvasWidth;
      this.height = canvasHeight;
      initBuffers();
    }
    if (changed) {
      refresh();
    }
  }

  public boolean isCanvasCropped() {
    return fullWidth != 0 && fullHeight != 0;
  }

  /**
   * @return Canvas width
   */
  public int canvasWidth() {
    return width;
  }

  /**
   * @return Canvas height
   */
  public int canvasHeight() {
    return height;
  }

  public int getFullWidth() {
    if (isCanvasCropped()) {
      return fullWidth;
    }
    return width;
  }

  public int getFullHeight() {
    if (isCanvasCropped()) {
      return fullHeight;
    }
    return height;
  }

  public int getCropX() {
    if (isCanvasCropped()) {
      return cropX;
    }
    return 0;
  }

  public int getCropY() {
    if (isCanvasCropped()) {
      return cropY;
    }
    return 0;
  }

  /**
   * Save a snapshot
   */
  public void saveSnapshot(File directory, TaskTracker taskTracker) {
    if (directory == null) {
      Log.error("Can't save snapshot: bad output directory!");
      return;
    }
    String fileName = String.format("%s-%d%s", name, spp, getOutputMode().getExtension());
    File targetFile = new File(directory, fileName);
    if (!directory.exists()) {
      directory.mkdirs();
    }
    saveFrame(targetFile, taskTracker);
  }

  /**
   * Saves the current frame (e.g. as a PNG, TIFF, or PFM image), depending on this scene's outputMode.
   * Also see {@link #writeFrame(OutputStream, PictureExportFormat, TaskTracker)}
   */
  public synchronized void saveFrame(File targetFile, TaskTracker taskTracker) {
    saveFrame(targetFile, getOutputMode(), taskTracker);
  }

  /**
   * Saves the current frame into a file.
   * Also see {@link #writeFrame(OutputStream, PictureExportFormat, TaskTracker)}
   */
  public synchronized void saveFrame(File targetFile, PictureExportFormat mode, TaskTracker taskTracker) {
    try (FileOutputStream out = new FileOutputStream(targetFile)) {
      writeFrame(out, mode, taskTracker);
    } catch (IOException e) {
      Log.warn("Failed to write file: " + targetFile.getAbsolutePath(), e);
    }
  }

  /**
   * Saves the current frame into the given output stream, using the given PictureExportFormat.
   * Side effects: This will compute the alpha channel if supported by the export format
   * and may apply post-processing if the scene has not been finalized yet.
   */
  public synchronized void writeFrame(OutputStream out, PictureExportFormat mode, TaskTracker taskTracker) throws IOException {
    if (mode.isTransparencySupported()) {
      computeAlpha(taskTracker);
    }
    if (!finalized) {
      postProcessFrame(taskTracker);
    }
    mode.write(out, this, taskTracker);
  }

  /**
   * Compute the alpha channel.
   */
  private void computeAlpha(TaskTracker taskTracker) {
    if (transparentSky) {
      if (!this.getOutputMode().isTransparencySupported()) {
        Log.warn("Can not use transparent sky with " + this.getOutputMode().getName() +  " output mode. Use PNG instead.");
      } else {
        try (TaskTracker.Task task = taskTracker.task("Computing alpha channel")) {
          AtomicInteger done = new AtomicInteger(0);

          Chunky.getCommonThreads().submit(() -> {
            IntStream.range(0, width).parallel().forEach(x -> {
              WorkerState state = new WorkerState();
              state.ray = new Ray();

              for (int y = 0; y < height; y++) {
                computeAlpha(x, y, state);
              }

              task.update(width, done.incrementAndGet());
            });
          }).get();

        } catch (InterruptedException e) {
          Log.warn("Failed to compute alpha channel", e);
        } catch (ExecutionException e) {
          Log.error("Failed to compute alpha channel", e);
        }
      }
    }
  }

  /**
   * Post-process all pixels in the current frame.
   *
   * <p>This is normally done by the render workers during rendering,
   * but in some cases a separate post-processing pass is needed.
   */
  public void postProcessFrame(TaskTracker.Task task) {
    PostProcessingFilter filter = postProcessingFilter;
    if(mode == RenderMode.PREVIEW) {
      filter = PreviewFilter.INSTANCE;
    }
    filter.processFrame(
      width, height,
      samples, backBuffer,
      exposure,
      task
    );
    finalized = true;
  }

  public void postProcessFrame(TaskTracker taskTracker) {
    try (TaskTracker.Task task = taskTracker.task("Finalizing frame")) {
      postProcessFrame(task);
    }
  }

  private synchronized void saveEmitterGrid(SceneIOProvider context, TaskTracker taskTracker) {
    if (emitterGrid == null)
      return;

    String filename = name + ".emittergrid";
    // TODO Not save when unchanged?
    try (TaskTracker.Task task = taskTracker.task("Saving Grid")) {
      Log.info("Saving Grid " + filename);

      try (DataOutputStream out = new DataOutputStream(new GZIPOutputStream(context.getSceneFileOutputStream(filename)))) {
        emitterGrid.store(out, this);
      } catch (IOException e) {
        Log.warn("Couldn't save Grid", e);
      }
    }
  }

  private synchronized void saveOctree(SceneIOProvider context, TaskTracker taskTracker) {
    String fileName = name + ".octree2";
    if (context.fileUnchangedSince(fileName, worldOctree.getTimestamp())) {
      Log.info("Skipping redundant Octree write");
      return;
    }
    try (TaskTracker.Task task = taskTracker.task("Saving octree", 2)) {
      task.update(1);
      Log.info("Saving octree " + fileName);

      boolean saved = false;
      try (DataOutputStream out = new DataOutputStream(new FastBufferedOutputStream(new GZIPOutputStream(context.getSceneFileOutputStream(fileName))))) {
        OctreeFileFormat.store(out, worldOctree, waterOctree, palette,
            grassTexture, foliageTexture, waterTexture);
        saved = true;

        task.update(2);
        Log.info("Octree saved");
      } catch (IOException e) {
        Log.warn("Failed to save the octree", e);
      }

      if (saved) {
        worldOctree.setTimestamp(context.fileTimestamp(fileName));
      }
    }
  }

  public synchronized void saveDump(SceneIOProvider context, TaskTracker taskTracker) {
    File dumpFile = context.getSceneFile(name + ".dump");
    Log.info("Saving render dump: " + dumpFile);
    try (FileOutputStream outputStream = new FileOutputStream(dumpFile)) {
      RenderDump.save(outputStream, this, taskTracker);
    } catch (IOException e) {
      Log.warn("Failed to save the render dump", e);
    }
    Log.info("Render dump saved: " + dumpFile);
  }

  private synchronized boolean loadEmitterGrid(SceneIOProvider context, TaskTracker taskTracker) {
    String filename = name + ".emittergrid";
    try (TaskTracker.Task task = taskTracker.task("Loading grid")) {
      Log.info("Load grid " + filename);
      try (DataInputStream in = new DataInputStream(new FastBufferedInputStream(new GZIPInputStream(context.getSceneFileInputStream(filename))))) {
        emitterGrid = Grid.load(in, this);
        return true;
      } catch (Exception e) {
        Log.info("Failed to load the grid", e);
        return false;
      }
    }
  }

  private synchronized boolean loadOctree(SceneIOProvider context, TaskTracker taskTracker) {
    String fileName = name + ".octree2";
    try (TaskTracker.Task task = taskTracker.task("(1/3) Loading octree", 2)) {
      task.update(1);
      Log.info("Loading octree " + fileName);

      long length = context.getSceneFile(fileName).length();
      double progressScale = 1000.0 / length;
      task.update(1000, 0);

      try {
        long fileTimestamp = context.fileTimestamp(fileName);
        OctreeFileFormat.OctreeData data;
        try (DataInputStream in = new DataInputStream(new FastBufferedInputStream(new GZIPInputStream(new PositionalInputStream(context.getSceneFileInputStream(fileName), pos -> {
          task.updateInterval((int) (pos * progressScale), 1);
        }))))) {
          data = OctreeFileFormat.load(in, octreeImplementation, this.biomeStructureImplementation);
        } catch (PackedOctree.OctreeTooBigException e) {
          // Octree too big, reload file and force loading as NodeBasedOctree
          Log.warn("Octree was too big when loading dump, reloading with old (slower and bigger) implementation.");
          DataInputStream inRetry = new DataInputStream(new FastBufferedInputStream(new GZIPInputStream(new PositionalInputStream(context.getSceneFileInputStream(fileName), pos -> {
            task.updateInterval((int) (pos * progressScale), 1);
          }))));
          data = OctreeFileFormat.load(inRetry, "NODE", this.biomeStructureImplementation);
        }

        worldOctree = data.worldTree;
        worldOctree.setTimestamp(fileTimestamp);
        waterOctree = data.waterTree;
        grassTexture = data.grassColors;
        foliageTexture = data.foliageColors;
        waterTexture = data.waterColors;
        if (waterTexture == null) {
          // this dump is so old that it doesn't contain a water texture (Chunky 2.3.0, #691)
          waterTexture = BiomeStructure.get(this.biomeStructureImplementation).create();
        }
        palette = data.palette;
        palette.applyMaterials();
        Log.info("Octree loaded");
        calculateOctreeOrigin(chunks, data.version < 6);
        camera.setWorldSize(1 << worldOctree.getDepth());

        try (TaskTracker.Task bvhTask = taskTracker.task("(2/3) Building world BVH")) {
          buildBvh(bvhTask);
        }
        try (TaskTracker.Task bvhTask = taskTracker.task("(3/3) Building actor BVH")) {
          buildActorBvh(bvhTask);
        }

        return true;
      } catch (IOException e) {
        Log.error("Failed to load chunk data!", e);
        return false;
      }
    }
  }

  public synchronized boolean loadDump(SceneIOProvider context, TaskTracker taskTracker) {
    if (!tryLoadDump(context, name + ".dump", taskTracker)) {
      // Failed to load the default render dump - try the backup file.
      if (!tryLoadDump(context, name + ".dump.backup", taskTracker)) {
        // we don't have the old render state, so reset spp and render time
        spp = 0;
        renderTime = 0;
        return false;
      }
    }
    return true;
  }

  /**
   * @return {@code true} if the render dump was successfully loaded
   */
  private boolean tryLoadDump(SceneIOProvider context, String fileName, TaskTracker taskTracker) {
    File dumpFile = context.getSceneFile(fileName);
    if (!dumpFile.isFile()) {
      if (spp != 0) {
        // The scene state says the render had some progress, so we should warn
        // that the render dump does not exist.
        Log.warn("Render dump not found: " + fileName);
      }
      return false;
    }

    Log.info("Loading render dump: " + dumpFile);
    try (FileInputStream inputStream = new FileInputStream(dumpFile)) {
      RenderDump.load(inputStream, this, taskTracker);
    } catch (IOException | IllegalStateException e) {
      // The render dump was possibly corrupt.
      Log.warn("Failed to load the render dump", e);
      return false;
    }
    postProcessFrame(taskTracker);

    Log.info("Render dump loaded: " + fileName);
    return true;
  }

  /**
   * Compute the alpha channel based on sky visibility.
   */
  public void computeAlpha(int x, int y, WorkerState state) {
    Ray ray = state.ray;
    double halfWidth = width / (2.0 * height);
    double invHeight = 1.0 / height;

    // Rotated grid supersampling.
    double[][] offsets = new double[][] {
      { -3.0 / 8.0,  1.0 / 8.0 },
      {  1.0 / 8.0,  3.0 / 8.0 },
      { -1.0 / 8.0, -3.0 / 8.0 },
      {  3.0 / 8.0, -1.0 / 8.0 },
    };

    double occlusion = 0.0;
    for (double[] offset : offsets) {
      camera.calcViewRay(ray,
        -halfWidth + (x + offset[0]) * invHeight,
        -0.5 + (y + offset[1]) * invHeight);
      ray.o.x -= origin.x;
      ray.o.y -= origin.y;
      ray.o.z -= origin.z;

      if (camera.getProjectionMode() == ProjectionMode.PARALLEL) {
        ParallelProjector.fixRay(state.ray, this);
      }
      occlusion += PreviewRayTracer.skyOcclusion(this, state);
    }

    alphaChannel[y * width + x] = (byte) (255 * occlusion * 0.25 + 0.5);
  }

  /**
   * Copies a pixel in-buffer.
   */
  public void copyPixel(int jobId, int offset) {
    System.arraycopy(samples, jobId * 3, samples, (jobId + offset) * 3, 3);
  }

  /**
   * @return scene status text.
   */
  public synchronized String sceneStatus() {
    try {
      if (!haveLoadedChunks()) {
        return "No chunks loaded!";
      } else {
        StringBuilder buf = new StringBuilder();
        Ray ray = new Ray();
        if (traceTarget(ray) && ray.getCurrentMaterial() instanceof Block) {
          Block block = (Block) ray.getCurrentMaterial();
          buf.append(String.format("target: %.2f m\n", ray.distance));
          buf.append(block.name);
          String description = block.description();
          if (!description.isEmpty()) {
            buf.append(" (").append(description).append(")");
          }
          buf.append("\n");
        }
        Vector3 pos = camera.getPosition();
        buf.append(String.format("pos: (%.1f, %.1f, %.1f)\n", pos.x, pos.y, pos.z));

        buf.append("facing: ");
        double yaw = camera.getYaw();
        yaw = (yaw + Math.PI*2) % (Math.PI*2);
        int index = (int)Math.floor((yaw + Math.PI/8) / (Math.PI/4)) % 8;
        buf.append(new String[]{"west", "southwest", "south", "southeast", "east", "northeast", "north", "northwest"}[index]);
        index = (int)Math.floor((yaw + Math.PI/4) / (Math.PI/2)) % 4;
        buf.append(" (towards ");
        buf.append(new String[]{"negative X", "positive Z", "positive X", "negative Z"}[index]);
        buf.append(")");

        return buf.toString();
      }

    } catch (IllegalStateException e) {
      Log.error("Unexpected exception while rendering back buffer", e);
    }
    return "";
  }

  /**
   * Prepare the front buffer for rendering by flipping the back and front buffer.
   */
  public synchronized void swapBuffers() {
    finalized = false;
    BitmapImage tmp = frontBuffer;
    frontBuffer = backBuffer;
    backBuffer = tmp;
  }

  /**
   * Call the consumer with the current front frame buffer.
   */
  public synchronized void withBufferedImage(Consumer<BitmapImage> consumer) {
    if (frontBuffer != null) {
      consumer.accept(frontBuffer);
    }
  }

  /**
   * Get direct access to the sample buffer.
   *
   * @return The sample buffer for this scene
   */
  public double[] getSampleBuffer() {
    return samples;
  }

  /**
   * Get the back buffer of the current frame (in ARGB format).
   * @return Back buffer
   */
  public BitmapImage getBackBuffer() {
    return backBuffer;
  }

  /**
   * Get the alpha channel of the current frame.
   * @return Alpha channel of the current frame
   */
  public byte[] getAlphaChannel() {
    return alphaChannel;
  }

  /**
   * @return <code>true</code> if the rendered buffer should be finalized
   */
  public boolean shouldFinalizeBuffer() {
    return finalizeBuffer;
  }

  /**
   * Set the buffer update flag. The buffer update flag decides whether the
   * renderer should update the buffered image.
   */
  public void setBufferFinalization(boolean value) {
    finalizeBuffer = value;
  }

  /**
   * @param x X coordinate in octree space
   * @param z Z coordinate in octree space
   * @return Foliage color for the given coordinates
   */
  public float[] getFoliageColor(int x, int y, int z) {
    if (biomeColors) {
      float[] color = foliageTexture.get(x, y, z);
      if (color != null) {
        return color;
      }
    }
    return Biomes.biomesPrePalette[0].foliageColorLinear;
  }

  /**
   * @param x X coordinate in octree space
   * @param z Z coordinate in octree space
   * @return Grass color for the given coordinates
   */
  public float[] getGrassColor(int x, int y, int z) {
    if (biomeColors) {
      float[] color = grassTexture.get(x, y, z);
      if (color != null) {
        return color;
      }
    }
    return Biomes.biomesPrePalette[0].grassColorLinear;
  }

  /**
   * @param x X coordinate in octree space
   * @param z Z coordinate in octree space
   * @return Water color for the given coordinates
   */
  public float[] getWaterColor(int x, int y, int z) {
    if (biomeColors && waterTexture != null) {
      float[] color = waterTexture.get(x, y, z);

      if (color != null) {
        if (color[0] > 0 || color[1] > 0 || color[2] > 0) {
          return color;
        }
      }
    }
    return Biomes.biomesPrePalette[0].waterColorLinear;
  }

  /**
   * Query if a position is loaded.
   */
  public boolean isChunkLoaded(int x, int y, int z) {
    return waterTexture != null && waterTexture.get(x, y, z) != null;
  }

  /**
   * Merge a render dump into this scene.
   */
  public void mergeDump(File dumpFile, TaskTracker taskTracker) {
    Log.info("Merging render dump: " + dumpFile);
    try(FileInputStream inputStream = new FileInputStream(dumpFile)) {
      RenderDump.merge(inputStream, this, taskTracker);
      postProcessFrame(taskTracker);
      Log.info("Render dump merged: " + dumpFile);
    } catch (IOException e) {
      Log.warn("Failed to merge the render dump", e);
    }
  }

  public void setSaveSnapshots(boolean value) {
    saveSnapshots = value;
  }

  public boolean shouldSaveSnapshots() {
    return saveSnapshots;
  }

  public boolean isInWater(Ray ray) {
    if (isWaterPlaneEnabled() && ray.o.y + origin.y < getEffectiveWaterPlaneHeight()) {
      if (getWaterPlaneChunkClip()) {
        if (!isChunkLoaded((int)Math.floor(ray.o.x), (int)Math.floor(ray.o.y), (int)Math.floor(ray.o.z))) {
          return true;
        }
      } else {
        return true;
      }
    }
    if (waterOctree.isInside(ray.o)) {
      int x = (int) QuickMath.floor(ray.o.x);
      int y = (int) QuickMath.floor(ray.o.y);
      int z = (int) QuickMath.floor(ray.o.z);
      Material block = waterOctree.getMaterial(x, y, z, palette);
      return block.isWater()
          && ((ray.o.y - y) < 0.875 || ((Water) block).isFullBlock());
    }
    return false;
  }

  public boolean isInsideOctree(Vector3 vec) {
    return worldOctree.isInside(vec);
  }

  public double getWaterOpacity() {
    return waterOpacity;
  }

  public void setWaterOpacity(double opacity) {
    if (opacity != waterOpacity) {
      this.waterOpacity = opacity;
      refresh();
    }
  }

  public double getWaterVisibility() {
    return waterVisibility;
  }

  public void setWaterVisibility(double visibility) {
    if (visibility != waterVisibility) {
      this.waterVisibility = visibility;
      refresh();
    }
  }

  public Vector3 getWaterColor() {
    return waterColor;
  }

  public void setWaterColor(Vector3 color) {
    waterColor.set(color);
    refresh();
  }

  public void setFogColor(Vector3 color) {
    fog.getFogColor().set(color);
    refresh();
  }

  public boolean getUseCustomWaterColor() {
    return useCustomWaterColor;
  }

  public void setUseCustomWaterColor(boolean value) {
    if (value != useCustomWaterColor) {
      useCustomWaterColor = value;
      refresh();
    }
  }

  @Override public synchronized JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.add("sdfVersion", SDF_VERSION);
    json.add("name", name);
    json.add("width", width);
    json.add("height", height);
    json.add("fullWidth", fullWidth);
    json.add("fullHeight", fullHeight);
    json.add("cropX", cropX);
    json.add("cropY", cropY);
    json.add("yClipMin", yClipMin);
    json.add("yClipMax", yClipMax);
    json.add("yMin", yMin);
    json.add("yMax", yMax);
    json.add("exposure", exposure);
    json.add("postprocess", postProcessingFilter.getId());
    json.add("outputMode", outputMode.getName());
    json.add("renderTime", renderTime);
    json.add("spp", spp);
    json.add("sppTarget", sppTarget);
    json.add("branchCount", branchCount);
    json.add("rayDepth", rayDepth);
    json.add("pathTrace", mode != RenderMode.PREVIEW);
    json.add("dumpFrequency", dumpFrequency);
    json.add("saveSnapshots", saveSnapshots);
    json.add("emittersEnabled", emittersEnabled);
    json.add("emitterIntensity", emitterIntensity);
    json.add("fancierTranslucency", fancierTranslucency);
    json.add("transmissivityCap", transmissivityCap);
    json.add("sunSamplingStrategy", sunSamplingStrategy.getId());
    json.add("stillWater", stillWater);
    json.add("waterOpacity", waterOpacity);
    json.add("waterVisibility", waterVisibility);
    json.add("useCustomWaterColor", useCustomWaterColor);
    if (useCustomWaterColor) {
      json.add("waterColor", ColorUtil.rgbToJson(waterColor));
    }
    waterShading.save(json);
    json.add("fog", fog.toJson());
    json.add("biomeColorsEnabled", biomeColors);
    json.add("transparentSky", transparentSky);
    json.add("waterWorldEnabled", waterPlaneEnabled);
    json.add("waterWorldHeight", waterPlaneHeight);
    json.add("waterWorldHeightOffsetEnabled", waterPlaneOffsetEnabled);
    json.add("waterWorldClipEnabled", waterPlaneChunkClip);
    json.add("hideUnknownBlocks", hideUnknownBlocks);

    if (!worldPath.isEmpty()) {
      // Save world info.
      JsonObject world = new JsonObject();
      world.add("path", worldPath);
      world.add("dimension", worldDimension);
      json.add("world", world);
    }

    json.add("camera", camera.toJson());
    json.add("sun", sun.toJson());
    json.add("sky", sky.toJson());
    json.add("cameraPresets", cameraPresets.copy());
    JsonArray chunkList = new JsonArray();
    for (ChunkPosition pos : chunks) {
      JsonArray chunk = new JsonArray();
      chunk.add(pos.x);
      chunk.add(pos.z);
      chunkList.add(chunk);
    }

    // Save material settings.
    json.add("materials", mapToJson(materials));

    // TODO: add regionList to compress the scene description size.
    json.add("chunkList", chunkList);

    json.add("octreeImplementation", octreeImplementation);
    json.add("emitterSamplingStrategy", emitterSamplingStrategy.name());
    json.add("preventNormalEmitterWithSampling", preventNormalEmitterWithSampling);

    json.add("animationTime", animationTime);

    json.add("renderer", renderer);
    json.add("previewRenderer", previewRenderer);

    entities.writeJsonData(json);
    json.add("additionalData", additionalData);

    return json;
  }

  private JsonObject mapToJson(Map<String, JsonValue> map) {
    JsonObject object = new JsonObject(map.size());
    map.forEach(object::add);
    return object;
  }

  /**
   * Reset the scene settings and import from a JSON object.
   */
  public synchronized void fromJson(JsonObject json) {
    boolean finalizeBufferPrev = finalizeBuffer;  // Remember the finalize setting.
    Scene scene = new Scene();
    scene.importFromJson(json);
    copyState(scene);
    copyTransients(scene);
    finalizeBuffer = finalizeBufferPrev; // Restore the finalize setting.

    setResetReason(ResetReason.SCENE_LOADED);
    sdfVersion = json.get("sdfVersion").intValue(-1);
    name = json.get("name").stringValue("default");
  }

  public EntityLoadingPreferences getEntityLoadingPreferences() {
    return entities.getEntityLoadingPreferences();
  }

  public Collection<Entity> getEntities() {
    return entities.getEntities();
  }

  public Collection<Entity> getActors() {
    return entities.getActors();
  }

  public MinecraftProfile getPlayerProfile(PlayerEntity player) {
    return entities.getAssociatedProfile(player);
  }

  public void removeEntity(Entity entity) {
    entities.removeEntity(entity);
    rebuildActorBvh();
  }

  public void addPlayer(PlayerEntity player) {
    entities.addPlayer(player);
    rebuildActorBvh();
  }

  /**
   * Clears the scene, preparing to load fresh chunks.
   */
  public void clear() {
    cameraPresets = new JsonObject();
    entities.clear();
  }

  /** Create a backup of a scene file. */
  public void backupFile(File sceneDirectory, File file) {
    if (file.exists()) {
      // Try to create backup. It is not a problem if we fail this.
      String backupFileName = file.getName() + ".backup";
      File backup = new File(sceneDirectory, backupFileName);
      if (backup.exists()) {
        //noinspection ResultOfMethodCallIgnored
        backup.delete();
      }
      if (!file.renameTo(new File(sceneDirectory, backupFileName))) {
        Log.info("Could not create backup " + backupFileName);
      }
    }
  }

  public boolean getForceReset() {
    return forceReset;
  }

  public synchronized void setRenderMode(RenderMode renderMode) {
    this.mode = renderMode;
  }

  public synchronized void forceReset() {
    setResetReason(ResetReason.MODE_CHANGE);
    forceReset = true;

    // Wake up waiting threads.
    notifyAll();
  }

  /**
   * Resets the scene state to the default state.
   *
   * @param name sets the name for the scene
   */
  public synchronized void resetScene(String name, SceneFactory sceneFactory) {
    boolean finalizeBufferPrev = finalizeBuffer;  // Remember the finalize setting.
    Scene newScene = sceneFactory.newScene();
    newScene.initBuffers();
    if (name != null) {
      newScene.setName(name);
    }
    copyState(newScene, false);
    copyTransients(newScene);
    moveCameraToCenter();
    forceReset = true;
    resetReason = ResetReason.SETTINGS_CHANGED;
    mode = RenderMode.PREVIEW;
    finalizeBuffer = finalizeBufferPrev;
  }

  /**
   * Parse the scene description from a JSON file.
   *
   * <p>This initializes the sample buffers.
   *
   * @param in Input stream to read the JSON data from. The stream will
   * be closed when done.
   */
  public void loadDescription(InputStream in) throws IOException {
    try (JsonParser parser = new JsonParser(in)) {
      JsonObject json = parser.parse().object();
      fromJson(json);
    } catch (JsonParser.SyntaxError e) {
      throw new IOException("JSON syntax error");
    }
  }

  /**
   * Write the scene description as JSON.
   *
   * @param out Output stream to write the JSON data to.
   * The stream will not be closed when done.
   */
  public void saveDescription(OutputStream out) throws IOException {
    PrettyPrinter pp = new PrettyPrinter("  ", new PrintStream(out));
    JsonObject json = toJson();
    json.prettyPrint(pp);
  }

  /**
   * Replace the current settings from exported JSON settings.
   *
   * <p>This (re)initializes the sample buffers for the scene.
   */
  public synchronized void importFromJson(JsonObject json) {
    // The scene is refreshed so that any ongoing renders will restart.
    // We do this in case some setting that requires restart changes.
    // TODO: check if we actually need to reset the scene based on changed settings.
    refresh();

    int newWidth = json.get("width").intValue(width);
    int newHeight = json.get("height").intValue(height);
    if (width != newWidth || height != newHeight || samples == null) {
      width = newWidth;
      height = newHeight;
      initBuffers();
    }

    fullWidth = json.get("fullWidth").intValue(fullWidth);
    fullHeight = json.get("fullHeight").intValue(fullHeight);
    cropX = json.get("cropX").intValue(cropX);
    cropY = json.get("cropY").intValue(cropY);

    yClipMin = json.get("yClipMin").asInt(yClipMin);
    yClipMax = json.get("yClipMax").asInt(yClipMax);
    yMin = json.get("yMin").asInt(Math.max(yClipMin, yMin));
    yMax = json.get("yMax").asInt(Math.min(yClipMax, yMax));

    exposure = json.get("exposure").doubleValue(exposure);
    postProcessingFilter = PostProcessingFilters
            .getPostProcessingFilterFromId(json.get("postprocess").stringValue(postProcessingFilter.getId()))
            .orElseGet(() -> {
              if (json.get("postprocess").stringValue(null) != null) {
                Log.warn("The post processing filter " + json +
                        " is unknown. Maybe you're missing a plugin that was used to create this scene?");
              }
              return DEFAULT_POSTPROCESSING_FILTER;
            });
    outputMode = PictureExportFormats
      .getFormat(json.get("outputMode").stringValue(outputMode.getName()))
      .orElse(PictureExportFormats.PNG);
    sppTarget = json.get("sppTarget").intValue(sppTarget);
    branchCount = json.get("branchCount").intValue(branchCount);
    rayDepth = json.get("rayDepth").intValue(rayDepth);
    if (!json.get("pathTrace").isUnknown()) {
      boolean pathTrace = json.get("pathTrace").boolValue(false);
      if (pathTrace) {
        mode = RenderMode.PAUSED;
      } else {
        mode = RenderMode.PREVIEW;
      }
    }
    dumpFrequency = json.get("dumpFrequency").intValue(dumpFrequency);
    saveSnapshots = json.get("saveSnapshots").boolValue(saveSnapshots);
    emittersEnabled = json.get("emittersEnabled").boolValue(emittersEnabled);
    emitterIntensity = json.get("emitterIntensity").doubleValue(emitterIntensity);
    fancierTranslucency = json.get("fancierTranslucency").boolValue(fancierTranslucency);
    transmissivityCap = json.get("transmissivityCap").doubleValue(transmissivityCap);

    if (json.get("sunSamplingStrategy").isUnknown()) {
      boolean sunSampling = json.get("sunEnabled").boolValue(false);
      boolean drawSun = json.get("sun").asObject().get("drawTexture").boolValue(false);
      if (drawSun) {
        if (sunSampling) {
          sunSamplingStrategy = SunSamplingStrategy.FAST;
        } else {
          sunSamplingStrategy = SunSamplingStrategy.NON_LUMINOUS;
        }
      } else {
        sunSamplingStrategy = SunSamplingStrategy.FAST;
      }
    } else {
      sunSamplingStrategy = SunSamplingStrategy.valueOf(json.get("sunSamplingStrategy").asString(SunSamplingStrategy.FAST.getId()));
    }

    if (json.get("sunEnabled").boolValue(false)) {
      sunSamplingStrategy = SunSamplingStrategy.FAST;
    } else {
      sunSamplingStrategy = SunSamplingStrategy.valueOf(json.get("sunSamplingStrategy").asString(SunSamplingStrategy.FAST.getId()));
    }

    stillWater = json.get("stillWater").boolValue(stillWater);
    waterOpacity = json.get("waterOpacity").doubleValue(waterOpacity);
    waterVisibility = json.get("waterVisibility").doubleValue(waterVisibility);
    useCustomWaterColor = json.get("useCustomWaterColor").boolValue(useCustomWaterColor);
    if (useCustomWaterColor) {
      waterColor.set(ColorUtil.jsonToRGB(json.get("waterColor").asObject()));
    }
    String waterShader = json.get("waterShader").stringValue("SIMPLEX");
    if(waterShader.equals("LEGACY"))
      waterShading = new LegacyWaterShader();
    else if(waterShader.equals("SIMPLEX"))
      waterShading = new SimplexWaterShader();
    else {
      Log.infof("Unknown water shader %s, using SIMPLEX", waterShader);
      waterShading = new SimplexWaterShader();
    }
    waterShading.load(json);
    biomeColors = json.get("biomeColorsEnabled").boolValue(biomeColors);
    transparentSky = json.get("transparentSky").boolValue(transparentSky);
    JsonValue fogObj = json.get("fog");
    if (fogObj.isObject()) {
      fog.importFromJson(fogObj.asObject(), this);
    } else {
      fog.importFromLegacy(json);
    }

    if(!json.get("waterHeight").isUnknown()) {
      // fallback for older scene versions were waterPlane was enabled by using height = 0
      waterPlaneHeight = json.get("waterHeight").doubleValue(waterPlaneHeight);
      waterPlaneEnabled = waterPlaneHeight > 0;
      waterPlaneOffsetEnabled = true;
    } else {
      waterPlaneEnabled = json.get("waterWorldEnabled").boolValue(waterPlaneEnabled);
      waterPlaneHeight = json.get("waterWorldHeight").doubleValue(waterPlaneHeight);
      waterPlaneOffsetEnabled = json.get("waterWorldHeightOffsetEnabled")
        .boolValue(waterPlaneOffsetEnabled);
      waterPlaneChunkClip = json.get("waterWorldClipEnabled").boolValue(waterPlaneChunkClip);
    }

    hideUnknownBlocks = json.get("hideUnknownBlocks").boolValue(hideUnknownBlocks);
    materials = json.get("materials").object().copy().toMap();

    // Load world info.
    if (json.get("world").isObject()) {
      JsonObject world = json.get("world").object();
      worldPath = world.get("path").stringValue(worldPath);
      worldDimension = world.get("dimension").intValue(worldDimension);
    }

    if (json.get("camera").isObject()) {
      camera.importFromJson(json.get("camera").object());
    }

    if (json.get("sun").isObject()) {
      sun.importFromJson(json.get("sun").object());
    }

    if (json.get("sky").isObject()) {
      sky.importFromJson(json.get("sky").object());
    }

    if (json.get("cameraPresets").isObject()) {
      cameraPresets = json.get("cameraPresets").object();
    }

    // Current SPP and render time are read after loading
    // other settings which can reset the render status.
    spp = json.get("spp").intValue(spp);
    renderTime = json.get("renderTime").longValue(renderTime);

    if (json.get("chunkList").isArray()) {
      JsonArray chunkList = json.get("chunkList").array();
      chunks.clear();
      for (JsonValue elem : chunkList) {
        JsonArray chunk = elem.array();
        int x = chunk.get(0).intValue(Integer.MAX_VALUE);
        int z = chunk.get(1).intValue(Integer.MAX_VALUE);
        if (x != Integer.MAX_VALUE && z != Integer.MAX_VALUE) {
          chunks.add(new ChunkPosition(x, z));
        }
      }
    }

    octreeImplementation = json.get("octreeImplementation").asString(PersistentSettings.getOctreeImplementation());

    emitterSamplingStrategy = EmitterSamplingStrategy.valueOf(json.get("emitterSamplingStrategy").asString("NONE"));
    preventNormalEmitterWithSampling = json.get("preventNormalEmitterWithSampling").asBoolean(PersistentSettings.getPreventNormalEmitterWithSampling());

    animationTime = json.get("animationTime").doubleValue(animationTime);

    renderer = json.get("renderer").asString(renderer);
    previewRenderer = json.get("previewRenderer").asString(previewRenderer);

    entities.importJsonData(json);

    additionalData = json.get("additionalData").object();
  }

  /**
   * Called when the scene description has been altered in a way that
   * forces the rendering to restart.
   */
  @Override
  public synchronized void refresh() {
    refresh(ResetReason.SETTINGS_CHANGED);
  }

  /**
   * Called when the scene description has been altered in a way that
   * should be saved to disk.
   */
  public synchronized void softRefresh() {
    refresh(ResetReason.SETTINGS_CHANGED_SOFT);
  }

  private synchronized void refresh(ResetReason reason) {
    if (mode == RenderMode.PAUSED) {
      mode = RenderMode.RENDERING;
    }
    if (reason != ResetReason.NONE) {
      spp = 0;
      renderTime = 0;
    }
    setResetReason(reason);
    notifyAll();
  }

  /**
   * @return The sun state object.
   */
  public Sun sun() {
    return sun;
  }

  /**
   * @return The sky state object.
   */
  public Sky sky() {
    return sky;
  }

  /**
   * @return The camera state object.
   */
  public Camera camera() {
    return camera;
  }

  public void saveCameraPreset(String name) {
    camera.name = name;
    cameraPresets.set(name, camera.toJson());
  }

  public void loadCameraPreset(String name) {
    JsonValue value = cameraPresets.get(name);
    if (value.isObject()) {
      camera.importFromJson(value.object());
      refresh();
    }
  }

  public void deleteCameraPreset(String name) {
    for (int i = 0; i < cameraPresets.size(); ++i) {
      if (cameraPresets.get(i).name.equals(name)) {
        cameraPresets.remove(i);
        return;
      }
    }
  }

  public JsonObject getCameraPresets() {
    return cameraPresets;
  }

  public RenderMode getMode() {
    return mode;
  }

  public void setFogDensity(double newValue) {
    if (newValue != fog.uniformDensity) {
      fog.uniformDensity = newValue;
      refresh();
    }
  }

  public void setSkyFogDensity(double newValue) {
    if (newValue != fog.skyFogDensity) {
      fog.skyFogDensity = newValue;
      refresh();
    }
  }

  public void setFastFog(boolean value) {
    if (fog.fastFog != value) {
      fog.fastFog = value;
      refresh();
    }
  }

  public void setFogMode(FogMode mode) {
    if (fog.mode != mode) {
      fog.mode = mode;
      refresh();
    }
  }

  public PictureExportFormat getOutputMode() {
    return outputMode;
  }

  public void setOutputMode(PictureExportFormat mode) {
    outputMode = mode;
  }

  public int numberOfChunks() {
    return chunks.size();
  }

  public Collection<ChunkPosition> getChunks() {
    return Collections.unmodifiableCollection(chunks);
  }

  /**
   * Clears the reset reason and returns the previous reason.
   * @return the current reset reason
   */
  public synchronized ResetReason getResetReason() {
    return resetReason;
  }

  public void setResetReason(ResetReason resetReason) {
    if (this.resetReason != ResetReason.SCENE_LOADED) {
      this.resetReason = resetReason;
    }
  }

  public void importMaterials() {
    ExtraMaterials.loadDefaultMaterialProperties();
    MaterialStore.collections.forEach((name, coll) -> importMaterial(materials, name, coll));
    MaterialStore.blockIds.forEach((name) -> {
      JsonValue properties = materials.get(name);
      if (properties != null) {
        palette.updateProperties(name, block -> {
          block.loadMaterialProperties(properties.asObject());
        });
      }
    });
    ExtraMaterials.idMap.forEach((name, material) -> {
      JsonValue properties = materials.get(name);
      if (properties != null) {
        material.loadMaterialProperties(properties.asObject());
      }});
  }

  private void importMaterial(Map<String, JsonValue> propertyMap, String name,
      Collection<? extends Material> materials) {
    JsonValue value = propertyMap.get(name);
    if (value != null) {
      JsonObject properties = value.object();
      for (Material material : materials) {
        material.loadMaterialProperties(properties);
      }
    }
  }

  /**
   * Modifies the emittance property for the given material.
   */
  public void setEmittance(String materialName, float value) {
    JsonObject material = materials.getOrDefault(materialName, new JsonObject()).object();
    material.set("emittance", Json.of(value));
    materials.put(materialName, material);
    refresh(ResetReason.MATERIALS_CHANGED);
  }

  /**
   * Modifies the specular coefficient property for the given material.
   */
  public void setSpecular(String materialName, float value) {
    JsonObject material = materials.getOrDefault(materialName, new JsonObject()).object();
    material.set("specular", Json.of(value));
    materials.put(materialName, material);
    refresh(ResetReason.MATERIALS_CHANGED);
  }

  /**
   * Modifies the index of refraction property for the given material.
   */
  public void setIor(String materialName, float value) {
    JsonObject material = materials.getOrDefault(materialName, new JsonObject()).object();
    material.set("ior", Json.of(value));
    materials.put(materialName, material);
    refresh(ResetReason.MATERIALS_CHANGED);
  }

  /**
   * Modifies the roughness property for the given material.
   */
  public void setPerceptualSmoothness(String materialName, float value) {
    JsonObject material = materials.getOrDefault(materialName, new JsonObject()).object();
    material.set("roughness", Json.of(Math.pow(1 - value, 2)));
    materials.put(materialName, material);
    refresh(ResetReason.MATERIALS_CHANGED);
  }

  /**
   * Modifies the metalness property for the given material.
   */
  public void setMetalness(String materialName, float value) {
    JsonObject material = materials.getOrDefault(materialName, new JsonObject()).object();
    material.set("metalness", Json.of(value));
    materials.put(materialName, material);
    refresh(ResetReason.MATERIALS_CHANGED);
  }

  public void setAnisotropy(String materialName, float value) {
    JsonObject material = materials.getOrDefault(materialName, new JsonObject()).object();
    material.set("anisotropy", Json.of(value));
    materials.put(materialName, material);
    refresh(ResetReason.MATERIALS_CHANGED);
  }

  public int getYClipMin() {
    return yClipMin;
  }

  public void setYClipMin(int yClipMin) {
    this.yClipMin = yClipMin;
  }

  public int getYClipMax() {
    return yClipMax;
  }

  public void setYClipMax(int yClipMax) {
    this.yClipMax = yClipMax;
  }

  public Grid getEmitterGrid() {
    return emitterGrid;
  }

  public String getOctreeImplementation() {
    return octreeImplementation;
  }

  public void setOctreeImplementation(String octreeImplementation) {
    this.octreeImplementation = octreeImplementation;
  }

  public String getBvhImplementation() {
    return entities.getBvhImplementation();
  }

  public void setBvhImplementation(String bvhImplementation) {
    entities.setBvhImplementation(bvhImplementation);
  }

  public String getBiomeStructureImplementation() {
    return biomeStructureImplementation;
  }

  public void setBiomeStructureImplementation(String biomeStructureImplementation) {
    this.biomeStructureImplementation = biomeStructureImplementation;
  }


  @PluginApi
  public Octree getWorldOctree() {
    return worldOctree;
  }

  @PluginApi
  public Octree getWaterOctree() {
    return waterOctree;
  }

  public EmitterSamplingStrategy getEmitterSamplingStrategy() {
    return emitterSamplingStrategy;
  }

  public void setEmitterSamplingStrategy(EmitterSamplingStrategy emitterSamplingStrategy) {
    if(this.emitterSamplingStrategy != emitterSamplingStrategy) {
      this.emitterSamplingStrategy = emitterSamplingStrategy;
      refresh();
    }
  }

  public int getGridSize() {
    return gridSize;
  }

  public void setGridSize(int gridSize) {
    this.gridSize = gridSize;
  }

  public boolean isPreventNormalEmitterWithSampling() {
    return preventNormalEmitterWithSampling;
  }

  public void setPreventNormalEmitterWithSampling(boolean preventNormalEmitterWithSampling) {
    if (preventNormalEmitterWithSampling != this.preventNormalEmitterWithSampling) {
      this.preventNormalEmitterWithSampling = preventNormalEmitterWithSampling;
      refresh();
    }
  }

  public void setAnimationTime(double animationTime) {
    this.animationTime = animationTime;
    refresh();
  }

  public double getAnimationTime() {
    return animationTime;
  }

  public void setRenderer(String renderer) {
    if (!renderer.equals(this.renderer)) {
      this.renderer = renderer;
      refresh();
    }
  }

  public String getRenderer() {
    return renderer;
  }

  public void setPreviewRenderer(String previewRenderer) {
    if (!previewRenderer.equals(this.previewRenderer)) {
      this.previewRenderer = previewRenderer;
      refresh();
    }
  }

  public String getPreviewRenderer() {
    return previewRenderer;
  }

  public boolean isLoading() {
    return isLoading;
  }

  /**
   * Add additional data.
   * Additional data is not used by chunky but can be used by plugins
   */
  @PluginApi
  public void setAdditionalData(String name, JsonValue value) {
    additionalData.set(name, value);
  }

  /**
   * Retrieve additional data
   * Additional data is not used by chunky but can be used by plugins
   */
  @PluginApi
  public JsonValue getAdditionalData(String name) {
    return additionalData.get(name);
  }

  /**
   * Get the world of this scene, or EmptyWorld.INSTANCE if it is unknown.
   */
  public World getWorld() {
    return loadedWorld;
  }

  /**
   * Get the water shader
   */
  public WaterShader getWaterShading() {
    return waterShading;
  }

  /**
   * Set the Water shader
   */
  public void setWaterShading(WaterShader waterShading) {
    this.waterShading = waterShading;
  }

  public boolean getHideUnknownBlocks() {
    return this.hideUnknownBlocks;
  }

  public void setHideUnknownBlocks(boolean hideUnknownBlocks) {
    this.hideUnknownBlocks = hideUnknownBlocks;
  }
  public boolean getFancierTranslucency() {
    return fancierTranslucency;
  }

  public void setFancierTranslucency(boolean value) {
    fancierTranslucency = value;
    refresh();
  }

  public double getTransmissivityCap() {
    return transmissivityCap;
  }

  public void setTransmissivityCap(double value) {
    transmissivityCap = value;
    refresh();
  }

  public boolean getPreviewParticleFog() {
    return this.previewParticleFog;
  }

  public void setPreviewParticleFog(boolean value) {
    this.previewParticleFog = value;
    PersistentSettings.setPreviewParticleFog(value);
    refresh();
  }
}
