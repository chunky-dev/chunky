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
package se.llbit.chunky.renderer.scene;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.model.WaterModel;
import se.llbit.chunky.renderer.OutputMode;
import se.llbit.chunky.renderer.Postprocess;
import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.renderer.RenderMode;
import se.llbit.chunky.renderer.ResetReason;
import se.llbit.chunky.renderer.WorkerState;
import se.llbit.chunky.renderer.projection.ProjectionMode;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.world.Biomes;
import se.llbit.chunky.world.Block;
import se.llbit.chunky.world.BlockData;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.Heightmap;
import se.llbit.chunky.world.World;
import se.llbit.chunky.world.WorldTexture;
import se.llbit.chunky.world.entity.Entity;
import se.llbit.chunky.world.entity.PaintingEntity;
import se.llbit.chunky.world.entity.PlayerEntity;
import se.llbit.chunky.world.entity.SignEntity;
import se.llbit.chunky.world.entity.SkullEntity;
import se.llbit.chunky.world.entity.WallSignEntity;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.math.BVH;
import se.llbit.math.ColorUtil;
import se.llbit.math.Octree;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector3i;
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.ListTag;
import se.llbit.png.IEND;
import se.llbit.png.ITXT;
import se.llbit.png.PngFileWriter;
import se.llbit.tiff.TiffFileWriter;
import se.llbit.util.MCDownloader;
import se.llbit.util.TaskTracker;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Encapsulates scene and render state.
 *
 * <p>Render state is stored in a sample buffer. Two frame buffers
 * are also kept for when a snapshot should be rendered.
 */
public class Scene extends SceneDescription {

  public static final int DEFAULT_DUMP_FREQUENCY = 500;

  protected static final double fSubSurface = 0.3;

  /**
   * Minimum canvas width.
   */
  public static final int MIN_CANVAS_WIDTH = 20;

  /**
   * Minimum canvas height.
   */
  public static final int MIN_CANVAS_HEIGHT = 20;


  /**
   * Default specular reflection coefficient
   */
  protected static final float SPECULAR_COEFF = 0.31f;

  /**
   * Default water specular reflection coefficient
   */
  public static final float WATER_SPECULAR = 0.46f;

  /**
   * Minimum exposure
   */
  public static final double MIN_EXPOSURE = 0.001;

  /**
   * Maximum exposure
   */
  public static final double MAX_EXPOSURE = 1000.0;

  /**
   * Default gamma
   */
  public static final float DEFAULT_GAMMA = 2.2f;

  /**
   * One over gamma
   */
  public static final float DEFAULT_GAMMA_INV = 1 / DEFAULT_GAMMA;

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
   * Default exposure.
   */
  public static final double DEFAULT_EXPOSURE = 1.0;

  /**
   * Default fog density.
   */
  public static final double DEFAULT_FOG_DENSITY = 0.0;

  /**
   * World reference.
   */
  private World loadedWorld;

  /**
   * Octree origin.
   */
  protected Vector3i origin = new Vector3i();

  /**
   * Octree
   */
  private Octree worldOctree;

  /**
   * Entities in the scene.
   */
  private Collection<Entity> entities = new LinkedList<>();

  /**
   * Poseable entities in the scene.
   */
  private Collection<Entity> actors = new LinkedList<>();

  /**
   * Poseable entities in the scene.
   */
  private Map<PlayerEntity, JsonObject> profiles = new HashMap<>();

  private BVH bvh = new BVH(Collections.<Primitive>emptyList());
  private BVH actorBvh = new BVH(Collections.<Primitive>emptyList());

  // Chunk loading buffers.
  private final byte[] blocks = new byte[Chunk.X_MAX * Chunk.Y_MAX * Chunk.Z_MAX];
  private final byte[] biomes = new byte[Chunk.X_MAX * Chunk.Z_MAX];
  private final byte[] data = new byte[(Chunk.X_MAX * Chunk.Y_MAX * Chunk.Z_MAX) / 2];

  /**
   * Preview frame interlacing counter.
   */
  public int previewCount;

  private WorldTexture grassTexture = new WorldTexture();
  private WorldTexture foliageTexture = new WorldTexture();

  /** This is the 8-bit channel frame buffer. */
  protected BitmapImage frontBuffer;

  private BitmapImage backBuffer;

  /** HDR sample buffer for the render output. */
  protected double[] samples;

  private byte[] alphaChannel;

  private boolean finalized = false;

  private boolean finalizeBuffer = false;

  private boolean forceReset = false;

  /**
   * Create an empty scene with default canvas width and height.
   */
  public Scene() {
    worldOctree = new Octree(1);

    width = PersistentSettings.get3DCanvasWidth();
    height = PersistentSettings.get3DCanvasHeight();

    sppTarget = PersistentSettings.getSppTargetDefault();

    initBuffers();
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
   * Clone other scene
   */
  public Scene(Scene other) {
    copyState(other);
    copyTransients(other);
  }

  /**
   * Set scene equal to other
   */
  public synchronized void copyState(Scene other) {
    loadedWorld = other.loadedWorld;
    worldPath = other.worldPath;
    worldDimension = other.worldDimension;

    // The octree reference is overwritten to save time.
    // When the other scene is changed it must create a new octree.
    worldOctree = other.worldOctree;
    entities = other.entities;
    actors = new LinkedList<>(
        other.actors); // Have to create a copy so that changes to entities can be reset.
    profiles = other.profiles;
    bvh = other.bvh;
    actorBvh = other.actorBvh;
    grassTexture = other.grassTexture;
    foliageTexture = other.foliageTexture;
    origin.set(other.origin);

    chunks = other.chunks;

    exposure = other.exposure;
    name = other.name;

    stillWater = other.stillWater;
    waterOpacity = other.waterOpacity;
    waterVisibility = other.waterVisibility;
    useCustomWaterColor = other.useCustomWaterColor;
    waterColor.set(other.waterColor);
    fogColor.set(other.fogColor);
    biomeColors = other.biomeColors;
    sunEnabled = other.sunEnabled;
    emittersEnabled = other.emittersEnabled;
    emitterIntensity = other.emitterIntensity;
    transparentSky = other.transparentSky;
    fogDensity = other.fogDensity;
    fastFog = other.fastFog;

    camera.set(other.camera);
    sky.set(other.sky);
    sun.set(other.sun);

    waterHeight = other.waterHeight;

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
  }

  /**
   * Save the scene description, render dump, and foliage
   * and grass textures.
   *
   * @throws IOException
   * @throws InterruptedException
   */
  public synchronized void saveScene(RenderContext context, TaskTracker taskTracker)
      throws IOException, InterruptedException {
    try (TaskTracker.Task task = taskTracker.task("Saving scene", 2)) {
      task.update(1);

      BufferedOutputStream out = new BufferedOutputStream(context.getSceneDescriptionOutputStream(name));
      saveDescription(out);

      saveOctree(context, taskTracker);
      saveGrassTexture(context, taskTracker);
      saveFoliageTexture(context, taskTracker);
      saveDump(context, taskTracker);
    }
  }

  /**
   * Load a stored scene by file name.
   *
   * @param sceneName      file name of the scene to load
   * @throws IOException
   * @throws SceneLoadingError
   * @throws InterruptedException
   */
  public synchronized void loadScene(RenderContext context, String sceneName,
      TaskTracker taskTracker) throws IOException, SceneLoadingError, InterruptedException {
    loadDescription(context.getSceneDescriptionInputStream(sceneName));

    if (sdfVersion < SDF_VERSION) {
      Log.warn("Old scene version detected! The scene may not have been loaded correctly.");
    } else if (sdfVersion > SDF_VERSION) {
      Log.warn(
          "This scene was created with a newer version of Chunky! The scene may not have been loaded correctly.");
    }

    // Load the configured skymap file.
    sky.loadSkymap();

    initBuffers(); // Re-initialize the render buffers.

    if (!worldPath.isEmpty()) {
      File worldDirectory = new File(worldPath);
      if (World.isWorldDir(worldDirectory)) {
        if (loadedWorld == null || loadedWorld.getWorldDirectory() == null || !loadedWorld
            .getWorldDirectory().getAbsolutePath().equals(worldPath)) {

          loadedWorld = new World(worldDirectory, true);
          loadedWorld.setDimension(worldDimension);

        } else if (loadedWorld.currentDimension() != worldDimension) {

          loadedWorld.setDimension(worldDimension);

        }
      } else {
        Log.info("Could not load world: " + worldPath);
      }
    }

    if (loadDump(context, taskTracker)) {
      postProcessFrame(taskTracker);
    }

    if (spp == 0) {
      mode = RenderMode.PREVIEW;
    } else if (mode == RenderMode.RENDERING) {
      mode = RenderMode.PAUSED;
    }

    if (loadOctree(context, taskTracker)) {
      boolean haveGrass = loadGrassTexture(context, taskTracker);
      boolean haveFoliage = loadFoliageTexture(context, taskTracker);
      if (!haveGrass || !haveFoliage) {
        biomeColors = false;
      }
    } else {
      // Could not load stored octree.
      // Load the chunks from the world.
      if (loadedWorld == null) {
        Log.warn("Could not load chunks (no world found for scene)");
      } else {
        loadChunks(taskTracker, loadedWorld, chunks);
      }
    }
    notifyAll();
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
   * @return <code>true</code> if sunlight is enabled
   */
  public boolean getDirectLight() {
    return sunEnabled;
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
   * Set sunlight enable flag.
   */
  public synchronized void setDirectLight(boolean value) {
    if (value != sunEnabled) {
      sunEnabled = value;
      refresh();
    }
  }

  /**
   * @return <code>true</code> if emitters are enabled
   */
  public boolean getEmittersEnabled() {
    return emittersEnabled;
  }

  /**
   * Trace a ray in this scene. This offsets the ray origin to
   * move it into the scene coordinate space.
   */
  public void rayTrace(RayTracer rayTracer, WorkerState state) {
    state.ray.o.x -= origin.x;
    state.ray.o.y -= origin.y;
    state.ray.o.z -= origin.z;

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
    if (bvh.closestIntersection(ray)) {
      hit = true;
    }
    if (renderActors) {
      if (actorBvh.closestIntersection(ray)) {
        hit = true;
      }
    }
    Ray oct = new Ray(ray);
    oct.setCurrentMaterial(ray.getPrevMaterial(), ray.getPrevData());
    if (worldOctree.intersect(this, oct) && oct.distance < ray.t) {
      ray.distance += oct.distance;
      ray.o.set(oct.o);
      ray.n.set(oct.n);
      ray.color.set(oct.color);
      ray.setPrevMaterial(oct.getPrevMaterial(), oct.getPrevData());
      ray.setCurrentMaterial(oct.getCurrentMaterial(), oct.getCurrentData());
      updateOpacity(ray);
      return true;
    }
    if (hit) {
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
      updateOpacity(ray);
      return true;
    }
    return false;
  }

  public void updateOpacity(Ray ray) {
    if (ray.getCurrentMaterial().isWater() || (ray.getCurrentMaterial() == Block.AIR
        && ray.getPrevMaterial().isWater())) {
      if (useCustomWaterColor) {
        ray.color.x = waterColor.x;
        ray.color.y = waterColor.y;
        ray.color.z = waterColor.z;
      }
      ray.color.w = waterOpacity;
    }
  }

  /**
   * Test if the ray should be killed (using Russian Roulette).
   *
   * @return {@code true} if the ray needs to die now
   */
  public final boolean kill(int depth, Random random) {
    return depth >= rayDepth && random.nextDouble() < .5f;
  }

  /**
   * Reload all loaded chunks.
   */
  public synchronized void reloadChunks(TaskTracker progress) {
    if (loadedWorld == null) {
      Log.warn("Can not reload chunks for scene - world directory not found!");
      return;
    }
    loadedWorld.setDimension(worldDimension);
    loadedWorld.reload();
    loadChunks(progress, loadedWorld, chunks);
    refresh();
  }

  /**
   * Load chunks into the Octree.
   */
  public synchronized void loadChunks(TaskTracker progress, World world,
      Collection<ChunkPosition> chunksToLoad) {

    if (world == null) {
      return;
    }

    Set<ChunkPosition> loadedChunks = new HashSet<>();
    int emitters = 0;
    int nchunks = 0;

    try (TaskTracker.Task task = progress.task("Loading regions")) {
      task.update(2, 1);

      loadedWorld = world;
      worldPath = loadedWorld.getWorldDirectory().getAbsolutePath();
      worldDimension = world.currentDimension();

      if (chunksToLoad.isEmpty()) {
        return;
      }

      int requiredDepth = calculateOctreeOrigin(chunksToLoad);

      // Create new octree to fit all chunks.
      worldOctree = new Octree(requiredDepth);

      if (waterHeight > 0) {
        // Water world mode enabled, fill in water in empty blocks.
        // The water blocks are replaced later when the world chunks are loaded.
        for (int x = 0; x < (1 << worldOctree.depth); ++x) {
          for (int z = 0; z < (1 << worldOctree.depth); ++z) {
            for (int y = -origin.y; y < (-origin.y) + waterHeight - 1; ++y) {
              worldOctree.set(Block.WATER_ID | (1 << WaterModel.FULL_BLOCK), x, y, z);
            }
          }
        }
        for (int x = 0; x < (1 << worldOctree.depth); ++x) {
          for (int z = 0; z < (1 << worldOctree.depth); ++z) {
            worldOctree.set(Block.WATER_ID, x, (-origin.y) + waterHeight - 1, z);
          }
        }
      }

      // Parse the regions first - force chunk lists to be populated!
      Set<ChunkPosition> regions = new HashSet<>();
      for (ChunkPosition cp : chunksToLoad) {
        regions.add(cp.getRegionPosition());
      }

      for (ChunkPosition region : regions) {
        world.getRegion(region).parse();
      }
    }

    try (TaskTracker.Task task = progress.task("Loading entities")) {
      entities = new LinkedList<>();
      if (actors.isEmpty() && PersistentSettings.getLoadPlayers()) {
        // We don't load actor entities if some already exists. Loading actor entities
        // risks resetting posed actors when reloading chunks for an existing scene.
        actors = new LinkedList<>();
        profiles = new HashMap<>();
        Collection<PlayerEntity> players = world.playerEntities();
        int done = 1;
        int target = players.size();
        for (PlayerEntity entity : players) {
          entity.randomPose();
          task.update(target, done);
          done += 1;
          JsonObject profile;
          try {
            profile = MCDownloader.fetchProfile(entity.uuid);
          } catch (IOException e) {
            Log.error(e);
            profile = new JsonObject();
          }
          profiles.put(entity, profile);
          actors.add(entity);
        }
      }
    }

    int ycutoff = PersistentSettings.getYCutoff();
    ycutoff = Math.max(0, ycutoff);

    Heightmap biomeIdMap = new Heightmap();

    try (TaskTracker.Task task = progress.task("Loading chunks")) {
      int done = 1;
      int target = chunksToLoad.size();
      for (ChunkPosition cp : chunksToLoad) {
        task.update(target, done);
        done += 1;

        if (loadedChunks.contains(cp)) {
          continue;
        }

        loadedChunks.add(cp);

        Collection<CompoundTag> tileEntities = new LinkedList<>();
        Collection<CompoundTag> ents = new LinkedList<>();
        world.getChunk(cp).getBlockData(blocks, data, biomes, tileEntities, ents);
        nchunks += 1;

        int wx0 = cp.x * 16;
        int wz0 = cp.z * 16;
        for (int cz = 0; cz < 16; ++cz) {
          int wz = cz + wz0;
          for (int cx = 0; cx < 16; ++cx) {
            int wx = cx + wx0;
            int biomeId = 0xFF & biomes[Chunk.chunkXZIndex(cx, cz)];
            biomeIdMap.set(biomeId, wx, wz);
          }
        }

        // Load entities.
        for (CompoundTag tag : ents) {
          if (tag.get("id").stringValue("").equals("Painting")) {
            ListTag pos = (ListTag) tag.get("Pos");
            double x = pos.getItem(0).doubleValue();
            double y = pos.getItem(1).doubleValue();
            double z = pos.getItem(2).doubleValue();
            ListTag rot = (ListTag) tag.get("Rotation");
            double yaw = rot.getItem(0).floatValue();
            //double pitch = rot.getItem(1).floatValue();
            entities.add(
                new PaintingEntity(new Vector3(x, y, z), tag.get("Motive").stringValue(), yaw));
          }
        }

        // Load tile entities.
        for (CompoundTag entityTag : tileEntities) {
          int x = entityTag.get("x").intValue(0) - wx0;
          int y = entityTag.get("y").intValue(0);
          int z = entityTag.get("z").intValue(0) - wz0;
          int index = Chunk.chunkIndex(x, y, z);
          int block = 0xFF & blocks[index];
          int metadata = 0xFF & data[index / 2];
          metadata >>= (x % 2) * 4;
          metadata &= 0xF;
          Vector3 position = new Vector3(x + wx0, y, z + wz0);
          switch (block) {
            case Block.WALLSIGN_ID:
              entities.add(new WallSignEntity(position, entityTag, metadata));
              break;
            case Block.SIGNPOST_ID:
              entities.add(new SignEntity(position, entityTag, metadata));
              break;
            case Block.HEAD_ID:
              entities.add(new SkullEntity(position, entityTag, metadata));
              break;
          }
        }

        for (int cy = ycutoff; cy < 256; ++cy) {
          for (int cz = 0; cz < 16; ++cz) {
            int z = cz + cp.z * 16 - origin.z;
            for (int cx = 0; cx < 16; ++cx) {
              int x = cx + cp.x * 16 - origin.x;
              int index = Chunk.chunkIndex(cx, cy, cz);
              int blockId = blocks[index];
              Block block = Block.get(blockId);

              if (cx > 0 && cx < 15 && cz > 0 && cz < 15 && cy > 0 && cy < 255 &&
                  blockId != Block.STONE_ID && block.isOpaque) {

                // Set obscured blocks to stone. This makes adjacent obscured
                // blocks be able to be merged into larger octree nodes
                // even if they had different block types originally.
                if (Block.get(blocks[index - 1]).isOpaque &&
                    Block.get(blocks[index + 1]).isOpaque &&
                    Block.get(blocks[index - Chunk.X_MAX]).isOpaque &&
                    Block.get(blocks[index + Chunk.X_MAX]).isOpaque &&
                    Block.get(blocks[index - Chunk.X_MAX * Chunk.Z_MAX]).isOpaque &&
                    Block.get(blocks[index + Chunk.X_MAX * Chunk.Z_MAX]).isOpaque) {
                  worldOctree.set(Block.STONE_ID, x, cy - origin.y, z);
                  continue;
                }
              }

              int metadata = 0xFF & data[index / 2];
              metadata >>= (cx % 2) * 4;
              metadata &= 0xF;

              int type = block.id;
              // Store metadata.
              switch (block.id) {
                case Block.VINES_ID:
                  if (cy < 255) {
                    // Is this the top vine block?
                    index = Chunk.chunkIndex(cx, cy + 1, cz);
                    Block above = Block.get(blocks[index]);
                    if (above.isSolid) {
                      type = type | (1 << BlockData.VINE_TOP);
                    }
                  }
                  break;

                case Block.STATIONARYWATER_ID:
                  type = Block.WATER_ID;
                case Block.WATER_ID:
                  if (cy < 255) {
                    // Is there water above?
                    index = Chunk.chunkIndex(cx, cy + 1, cz);
                    Block above = Block.get(blocks[index]);
                    if (above.isWater()) {
                      type |= (1 << WaterModel.FULL_BLOCK);
                    } else if (above == Block.get(Block.LILY_PAD_ID)) {
                      type |= (1 << BlockData.LILY_PAD);
                      long wx = cp.x * 16L + cx;
                      long wy = cy + 1;
                      long wz = cp.z * 16L + cz;
                      long pr = (wx * 3129871L) ^ (wz * 116129781L) ^ (wy);
                      pr = pr * pr * 42317861L + pr * 11L;
                      int dir = 3 & (int) (pr >> 16);
                      type |= (dir << BlockData.LILY_PAD_ROTATION);
                    }
                  }
                  break;

                case Block.FIRE_ID: {
                  long wx = cp.x * 16L + cx;
                  long wy = cy + 1;
                  long wz = cp.z * 16L + cz;
                  long pr = (wx * 3129871L) ^ (wz * 116129781L) ^ (wy);
                  pr = pr * pr * 42317861L + pr * 11L;
                  int dir = 0xF & (int) (pr >> 16);
                  type |= (dir << BlockData.LILY_PAD_ROTATION);
                }
                break;

                case Block.STATIONARYLAVA_ID:
                  type = Block.LAVA_ID;
                case Block.LAVA_ID:
                  if (cy < 255) {
                    // Is there lava above?
                    index = Chunk.chunkIndex(cx, cy + 1, cz);
                    Block above = Block.get(blocks[index]);
                    if (above.isLava()) {
                      type = type | (1 << WaterModel.FULL_BLOCK);
                    }
                  }
                  break;

                case Block.GRASS_ID:
                  if (cy < 255) {
                    // Is it snow covered?
                    index = Chunk.chunkIndex(cx, cy + 1, cz);
                    int blockAbove = 0xFF & blocks[index];
                    if (blockAbove == Block.SNOW_ID) {
                      type = type | (1 << 8);// 9th bit is the snow bit
                    }
                  }
                  // Fallthrough!

                case Block.WOODENDOOR_ID:
                case Block.IRONDOOR_ID:
                case Block.SPRUCEDOOR_ID:
                case Block.BIRCHDOOR_ID:
                case Block.JUNGLEDOOR_ID:
                case Block.ACACIADOOR_ID:
                case Block.DARKOAKDOOR_ID: {
                  int top = 0;
                  int bottom = 0;
                  if ((metadata & 8) != 0) {
                    // This is the top part of the door.
                    top = metadata;
                    if (cy > 0) {
                      bottom = 0xFF & data[Chunk.chunkIndex(cx, cy - 1, cz) / 2];
                      bottom >>= (cx % 2) * 4; // Extract metadata.
                      bottom &= 0xF;
                    }
                  } else {
                    // This is the bottom part of the door.
                    bottom = metadata;
                    if (cy < 255) {
                      top = 0xFF & data[Chunk.chunkIndex(cx, cy + 1, cz) / 2];
                      top >>= (cx % 2) * 4; // Extract metadata.
                      top &= 0xF;
                    }
                  }
                  type |= (top << BlockData.DOOR_TOP);
                  type |= (bottom << BlockData.DOOR_BOTTOM);
                  break;
                }

                default:
                  break;
              }
              type |= metadata << 8;
              if (block.isEmitter) {
                emitters += 1;
              }
              if (block.isInvisible) {
                type = 0;
              }
              worldOctree.set(type, cx + cp.x * 16 - origin.x, cy - origin.y, cz + cp.z * 16 - origin.z);
            }
          }
        }
      }
    }

    grassTexture = new WorldTexture();
    foliageTexture = new WorldTexture();

    Set<ChunkPosition> chunkSet = new HashSet<>(chunksToLoad);

    try (TaskTracker.Task task = progress.task("Finalizing octree")) {
      int done = 0;
      int target = chunksToLoad.size();
      for (ChunkPosition cp : chunksToLoad) {

        // Finalize grass and foliage textures.
        // 3x3 box blur.
        for (int x = 0; x < 16; ++x) {
          for (int z = 0; z < 16; ++z) {

            int nsum = 0;
            float[] grassMix = {0, 0, 0};
            float[] foliageMix = {0, 0, 0};
            for (int sx = x - 1; sx <= x + 1; ++sx) {
              int wx = cp.x * 16 + sx;
              for (int sz = z - 1; sz <= z + 1; ++sz) {
                int wz = cp.z * 16 + sz;

                ChunkPosition ccp = ChunkPosition.get(wx >> 4, wz >> 4);
                if (chunkSet.contains(ccp)) {
                  nsum += 1;
                  int biomeId = biomeIdMap.get(wx, wz);
                  float[] grassColor = Biomes.getGrassColorLinear(biomeId);
                  grassMix[0] += grassColor[0];
                  grassMix[1] += grassColor[1];
                  grassMix[2] += grassColor[2];
                  float[] foliageColor = Biomes.getFoliageColorLinear(biomeId);
                  foliageMix[0] += foliageColor[0];
                  foliageMix[1] += foliageColor[1];
                  foliageMix[2] += foliageColor[2];
                }
              }
            }
            grassMix[0] /= nsum;
            grassMix[1] /= nsum;
            grassMix[2] /= nsum;
            grassTexture.set(cp.x * 16 + x - origin.x, cp.z * 16 + z - origin.z, grassMix);

            foliageMix[0] /= nsum;
            foliageMix[1] /= nsum;
            foliageMix[2] /= nsum;
            foliageTexture.set(cp.x * 16 + x - origin.x, cp.z * 16 + z - origin.z, foliageMix);
          }
        }
        task.update(target, done);
        done += 1;
        OctreeFinalizer.finalizeChunk(worldOctree, origin, cp);
      }
    }

    chunks = loadedChunks;
    camera.setWorldSize(1 << worldOctree.depth);
    buildBvh();
    buildActorBvh();
    Log.info(String.format("Loaded %d chunks (%d emitters)", nchunks, emitters));
  }

  private void buildBvh() {
    final List<Primitive> primitives = new LinkedList<>();

    worldOctree.visit((data1, x, y, z, size) -> {
      if ((data1 & 0xF) == Block.WATER_ID) {
        WaterModel.addPrimitives(primitives, data1, x, y, z, 1 << size);
      }
    });

    Vector3 worldOffset = new Vector3(-origin.x, -origin.y, -origin.z);
    for (Entity entity : entities) {
      primitives.addAll(entity.primitives(worldOffset));
    }
    bvh = new BVH(primitives);
  }

  private void buildActorBvh() {
    final List<Primitive> actorPrimitives = new LinkedList<>();
    Vector3 worldOffset = new Vector3(-origin.x, -origin.y, -origin.z);
    for (Entity entity : actors) {
      actorPrimitives.addAll(entity.primitives(worldOffset));
    }
    actorBvh = new BVH(actorPrimitives);
  }

  /**
   * Rebuild the actors bounding volume hierarchy.
   */
  public void rebuildActorBvh() {
    buildActorBvh();
    refresh();
  }

  private int calculateOctreeOrigin(Collection<ChunkPosition> chunksToLoad) {
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

    int maxDimension = Math.max(Chunk.Y_MAX, Math.max(xmax - xmin, zmax - zmin));
    int requiredDepth = QuickMath.log2(QuickMath.nextPow2(maxDimension));

    int xroom = (1 << requiredDepth) - (xmax - xmin);
    int yroom = (1 << requiredDepth) - Chunk.Y_MAX;
    int zroom = (1 << requiredDepth) - (zmax - zmin);

    origin.set(xmin - xroom / 2, -yroom / 2, zmin - zroom / 2);
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
    for (int y = Chunk.Y_MAX - 1; y >= 0; --y) {
      int block = worldOctree.get(xcenter - origin.x, y - origin.y, zcenter - origin.z);
      if (block != Block.AIR_ID) {
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
   * Move the camera to the player position, if available.
   */
  public void moveCameraToPlayer() {
    for (Entity entity : actors) {
      if (entity instanceof PlayerEntity) {
        camera.moveToPlayer((PlayerEntity) entity);
      }
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

  /**
   * Set the recursive ray depth limit
   */
  public synchronized void setRayDepth(int value) {
    value = Math.max(1, value);
    if (rayDepth != value) {
      rayDepth = value;
      PersistentSettings.setRayDepth(rayDepth);
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
   * Trace a ray in the Octree.
   * The ray is displaced to the target position if it hits something.
   *
   * @return {@code true} if the ray hit something
   */
  public boolean trace(Ray ray) {
    WorkerState state = new WorkerState();
    state.ray = ray;
    if (isInWater(ray)) {
      ray.setCurrentMaterial(Block.get(Block.WATER_ID), 0);
    } else {
      ray.setCurrentMaterial(Block.AIR, 0);
    }
    ray.d.set(0, 0, 1);
    ray.o.set(camera.getPosition());
    ray.o.x -= origin.x;
    ray.o.y -= origin.y;
    ray.o.z -= origin.z;
    camera.transform(ray.d);
    while (PreviewRayTracer.nextIntersection(this, ray)) {
      if (ray.getCurrentMaterial() != Block.AIR) {
        return true;
      }
    }
    return false;
  }

  /**
   * Perform auto focus.
   */
  public void autoFocus() {
    Ray ray = new Ray();
    if (!trace(ray)) {
      camera.setDof(Double.POSITIVE_INFINITY);
    } else {
      camera.setSubjectDistance(ray.distance);
      camera.setDof(ray.distance * ray.distance);
    }
  }

  /**
   * Find the current camera target position.
   *
   * @return {@code null} if the camera is not aiming at some intersectable object
   */
  public Vector3 getTargetPosition() {
    Ray ray = new Ray();
    if (!trace(ray)) {
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
   * @return The current postprocessing mode
   */
  public Postprocess getPostprocess() {
    return postprocess;
  }

  /**
   * Change the postprocessing mode
   *
   * @param p The new postprocessing mode
   */
  public synchronized void setPostprocess(Postprocess p) {
    postprocess = p;
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
   * Set the ocean water height.
   *
   * @return {@code true} if the water height value was changed.
   */
  public boolean setWaterHeight(int value) {
    value = Math.max(0, value);
    value = Math.min(256, value);
    if (value != waterHeight) {
      waterHeight = value;
      refresh();
      return true;
    }
    return false;
  }

  /**
   * @return The ocean water height
   */
  public int getWaterHeight() {
    return waterHeight;
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
    postprocess = other.postprocess;
    exposure = other.exposure;
    dumpFrequency = other.dumpFrequency;
    saveSnapshots = other.saveSnapshots;
    sppTarget = other.sppTarget;
    rayDepth = other.rayDepth;
    mode = other.mode;
    outputMode = other.outputMode;
    cameraPresets = other.cameraPresets;
    camera.name = other.camera.name;
    finalizeBuffer = other.finalizeBuffer;
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
   * Change the canvas size.
   */
  public synchronized void setCanvasSize(int canvasWidth, int canvasHeight) {
    width = Math.max(MIN_CANVAS_WIDTH, canvasWidth);
    height = Math.max(MIN_CANVAS_HEIGHT, canvasHeight);
    initBuffers();
    refresh();
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

  /**
   * Save a snapshot
   */
  public void saveSnapshot(File directory, TaskTracker progress) {
    if (directory == null) {
      Log.error("Can't save snapshot: bad output directory!");
      return;
    }
    String fileName = String.format("%s-%d%s", name, spp, outputMode.getExtension());
    File targetFile = new File(directory, fileName);
    computeAlpha(progress);
    if (!finalized) {
      postProcessFrame(progress);
    }
    writeImage(targetFile, progress);
  }

  /**
   * Save the current frame as a PNG image.
   * @throws IOException
   */
  public synchronized void saveFrame(File targetFile, TaskTracker progress)
      throws IOException {
    computeAlpha(progress);
    if (!finalized) {
      postProcessFrame(progress);
    }
    writeImage(targetFile, progress);
  }

  /**
   * Compute the alpha channel.
   */
  private void computeAlpha(TaskTracker progress) {
    if (transparentSky) {
      if (outputMode == OutputMode.TIFF_32) {
        Log.warn("Can not use transparent sky with TIFF output mode.");
      } else {
        try (TaskTracker.Task task = progress.task("Computing alpha channel")) {
          WorkerState state = new WorkerState();
          state.ray = new Ray();
          for (int x = 0; x < width; ++x) {
            task.update(width, x + 1);
            for (int y = 0; y < height; ++y) {
              computeAlpha(x, y, state);
            }
          }
        }
      }
    }
  }

  /**
   * Post-process all pixels in the current frame.
   *
   * <p>This is normally done by the render workers during rendering,
   * but in some cases an separate post processing pass is needed.
   */
  public void postProcessFrame(TaskTracker progress) {
    try (TaskTracker.Task task = progress.task("Finalizing frame")) {
      for (int x = 0; x < width; ++x) {
        task.update(width, x + 1);
        for (int y = 0; y < height; ++y) {
          finalizePixel(x, y);
        }
      }
    }
  }

  /**
   * Write buffer data to image.
   *
   * @param targetFile file to write to.
   */
  private void writeImage(File targetFile, TaskTracker progress) {
    if (outputMode == OutputMode.PNG) {
      writePng(targetFile, progress);
    } else if (outputMode == OutputMode.TIFF_32) {
      writeTiff(targetFile, progress);
    }
  }

  /**
   * Write PNG image.
   *
   * @param targetFile file to write to.
   */
  private void writePng(File targetFile, TaskTracker progress) {
    try (TaskTracker.Task task = progress.task("Writing PNG");
        PngFileWriter writer = new PngFileWriter(targetFile)) {
      if (transparentSky) {
        writer.write(backBuffer.data, alphaChannel, width, height, task);
      } else {
        writer.write(backBuffer.data, width, height, task);
      }
      if (camera.getProjectionMode() == ProjectionMode.PANORAMIC && camera.getFov() >= 179
          && camera.getFov() <= 181) {
        String xmp = "";
        xmp += "<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>\n";
        xmp += " <rdf:Description rdf:about=''\n";
        xmp += "   xmlns:GPano='http://ns.google.com/photos/1.0/panorama/'>\n";
        xmp += " <GPano:CroppedAreaImageHeightPixels>";
        xmp += height;
        xmp += "</GPano:CroppedAreaImageHeightPixels>\n";
        xmp += " <GPano:CroppedAreaImageWidthPixels>";
        xmp += width;
        xmp += "</GPano:CroppedAreaImageWidthPixels>\n";
        xmp += " <GPano:CroppedAreaLeftPixels>0</GPano:CroppedAreaLeftPixels>\n";
        xmp += " <GPano:CroppedAreaTopPixels>0</GPano:CroppedAreaTopPixels>\n";
        xmp += " <GPano:FullPanoHeightPixels>";
        xmp += height;
        xmp += "</GPano:FullPanoHeightPixels>\n";
        xmp += " <GPano:FullPanoWidthPixels>";
        xmp += width;
        xmp += "</GPano:FullPanoWidthPixels>\n";
        xmp += " <GPano:ProjectionType>equirectangular</GPano:ProjectionType>\n";
        xmp += " <GPano:UsePanoramaViewer>True</GPano:UsePanoramaViewer>\n";
        xmp += " </rdf:Description>\n";
        xmp += " </rdf:RDF>";
        ITXT iTXt = new ITXT("XML:com.adobe.xmp", xmp);
        writer.writeChunk(iTXt);
      }
      writer.writeChunk(new IEND());
    } catch (IOException e) {
      Log.warn("Failed to write PNG file: " + targetFile.getAbsolutePath(), e);
    }
  }

  /**
   * Write TIFF image.
   *
   * @param targetFile file to write to.
   */
  private void writeTiff(File targetFile, TaskTracker progress) {
    try (TaskTracker.Task task = progress.task("Writing TIFF");
        TiffFileWriter writer = new TiffFileWriter(targetFile)) {
      writer.write32(this, task);
    } catch (IOException e) {
      Log.warn("Failed to write TIFF file: " + targetFile.getAbsolutePath(), e);
    }
  }

  private synchronized void saveOctree(RenderContext context, TaskTracker progress) {
    String fileName = name + ".octree";
    if (context.fileUnchangedSince(fileName, worldOctree.getTimestamp())) {
      Log.info("Skipping redundant Octree write");
      return;
    }
    try (TaskTracker.Task task = progress.task("Saving octree", 2)) {
      task.update(1);
      Log.info("Saving octree " + fileName);

      try (DataOutputStream out = new DataOutputStream(new GZIPOutputStream(context.getSceneFileOutputStream(fileName)))) {
        worldOctree.store(out);
        worldOctree.setTimestamp(context.fileTimestamp(fileName));

        task.update(2);
        Log.info("Octree saved");
      } catch (IOException e) {
        Log.warn("IO exception while saving octree!", e);
      }
    }
  }

  private synchronized void saveGrassTexture(RenderContext context,
      TaskTracker progress) {
    String fileName = name + ".grass";
    if (context.fileUnchangedSince(fileName, grassTexture.getTimestamp())) {
      Log.info("Skipping redundant grass texture write");
      return;
    }
    try (TaskTracker.Task task = progress.task("Saving grass texture", 2)) {
      task.update(1);
      Log.info("Saving grass texture " + fileName);
      try (DataOutputStream out = new DataOutputStream(new GZIPOutputStream(context.getSceneFileOutputStream(fileName)))) {
        grassTexture.store(out);
        grassTexture.setTimestamp(context.fileTimestamp(fileName));
        task.update(2);
        Log.info("Grass texture saved");
      } catch (IOException e) {
        Log.warn("IO exception while saving octree!", e);
      }
    }
  }

  private synchronized void saveFoliageTexture(RenderContext context, TaskTracker progress) {
    String fileName = name + ".foliage";
    if (context.fileUnchangedSince(fileName, foliageTexture.getTimestamp())) {
      Log.info("Skipping redundant foliage texture write");
      return;
    }
    try (TaskTracker.Task task = progress.task("Saving foliage texture", 2)) {
      task.update(1);
      Log.info("Saving foliage texture " + fileName);
      try (DataOutputStream out = new DataOutputStream(new GZIPOutputStream(context.getSceneFileOutputStream(fileName)))) {
        foliageTexture.store(out);
        foliageTexture.setTimestamp(context.fileTimestamp(fileName));
        task.update(2);
        Log.info("Foliage texture saved");
      } catch (IOException e) {
        Log.warn("IO exception while saving octree!", e);
      }
    }
  }

  public synchronized void saveDump(RenderContext context, TaskTracker progress) {
    String fileName = name + ".dump";
    try (TaskTracker.Task task = progress.task("Saving render dump", 2)) {
      task.update(1);
      Log.info("Saving render dump " + fileName);
      try (DataOutputStream out = new DataOutputStream(
          new GZIPOutputStream(context.getSceneFileOutputStream(fileName)))) {
        out.writeInt(width);
        out.writeInt(height);
        out.writeInt(spp);
        out.writeLong(renderTime);
        for (int x = 0; x < width; ++x) {
          task.update(width, x + 1);
          for (int y = 0; y < height; ++y) {
            out.writeDouble(samples[(y * width + x) * 3 + 0]);
            out.writeDouble(samples[(y * width + x) * 3 + 1]);
            out.writeDouble(samples[(y * width + x) * 3 + 2]);
          }
        }
        Log.info("Render dump saved");
      } catch (IOException e) {
        Log.warn("IO exception while saving render dump!", e);
      }
    }
  }

  private synchronized boolean loadOctree(RenderContext context, TaskTracker progress) {
    String fileName = name + ".octree";
    try (TaskTracker.Task task = progress.task("Loading octree", 2)) {
      task.update(1);
      Log.info("Loading octree " + fileName);
      try (DataInputStream in = new DataInputStream(new GZIPInputStream(context.getSceneFileInputStream(fileName)))) {
        worldOctree = Octree.load(in);
        worldOctree.setTimestamp(context.fileTimestamp(fileName));
        task.update(2);
        Log.info("Octree loaded");
        calculateOctreeOrigin(chunks);
        camera.setWorldSize(1 << worldOctree.depth);
        buildBvh();
        buildActorBvh();
        return true;
      } catch (IOException e) {
        Log.info("Failed to load chunk octree: missing file or incorrect format!", e);
        return false;
      }
    }
  }

  private synchronized boolean loadGrassTexture(RenderContext context, TaskTracker progress) {
    String fileName = name + ".grass";
    try (TaskTracker.Task task = progress.task("Loading grass texture", 2)) {
      task.update(1);
      Log.info("Loading grass texture " + fileName);
      try (DataInputStream in = new DataInputStream(new GZIPInputStream(context.getSceneFileInputStream(fileName)))) {
        grassTexture = WorldTexture.load(in);
        grassTexture.setTimestamp(context.fileTimestamp(fileName));
        task.update(2);
        Log.info("Grass texture loaded");
        return true;
      } catch (IOException e) {
        Log.info("Failed to load grass texture!");
        return false;
      }
    }
  }

  private synchronized boolean loadFoliageTexture(RenderContext context, TaskTracker progress) {
    String fileName = name + ".foliage";
    try (TaskTracker.Task task = progress.task("Loading foliage texture", 2)) {
      task.update(1);
      Log.info("Loading foliage texture " + fileName);
      try (DataInputStream in = new DataInputStream(new GZIPInputStream(context.getSceneFileInputStream(fileName)))) {
        foliageTexture = WorldTexture.load(in);
        foliageTexture.setTimestamp(context.fileTimestamp(fileName));
        task.update(2);
        Log.info("Foliage texture loaded");
        return true;
      } catch (IOException e) {
        Log.info("Failed to load foliage texture!");
        return false;
      }
    }
  }

  public synchronized boolean loadDump(RenderContext context, TaskTracker taskTracker) {
    if (!tryLoadDump(context, name + ".dump", taskTracker)) {
      // Failed to load the default render dump - try the backup file.
      if (!tryLoadDump(context, name + ".dump.backup", taskTracker)) {
        spp = 0;  // Set spp = 0 because we don't have the old render state.
        return false;
      }
    }
    return true;
  }

  /**
   * @return {@code true} if the render dump was successfully loaded
   */
  private boolean tryLoadDump(RenderContext context, String fileName, TaskTracker taskTracker) {
    File dumpFile = context.getSceneFile(fileName);
    if (!dumpFile.isFile()) {
      if (spp != 0) {
        // The scene state says the render had some progress, so we should warn
        // that the render dump does not exist.
        Log.warn("Render dump not found: " + fileName);
      }
      return false;
    }
    try (DataInputStream in = new DataInputStream(new GZIPInputStream(new FileInputStream(dumpFile)));
        TaskTracker.Task task = taskTracker.task("Loading render dump", 2)) {
      task.update(1);
      Log.info("Reading render dump " + fileName);
      int dumpWidth = in.readInt();
      int dumpHeight = in.readInt();
      if (dumpWidth != width || dumpHeight != height) {
        Log.warn("Render dump discarded: incorrect width or height!");
        return false;
      }
      spp = in.readInt();
      renderTime = in.readLong();

      for (int x = 0; x < width; ++x) {
        task.update(width, x + 1);
        for (int y = 0; y < height; ++y) {
          samples[(y * width + x) * 3 + 0] = in.readDouble();
          samples[(y * width + x) * 3 + 1] = in.readDouble();
          samples[(y * width + x) * 3 + 2] = in.readDouble();
          finalizePixel(x, y);
        }
      }
      Log.info("Render dump loaded: " + fileName);
      return true;
    } catch (IOException e) {
      // The render dump was possibly corrupt.
      Log.warn("Failed to load render dump", e);
      return false;
    }
  }

  /**
   * Finalize a pixel. Calculates the resulting RGB color values for
   * the pixel and sets these in the bitmap image.
   */
  public void finalizePixel(int x, int y) {
    finalized = true;
    double[] result = new double[3];
    postProcessPixel(x, y, result);
    backBuffer.data[y * width + x] = ColorUtil
        .getRGB(QuickMath.min(1, result[0]), QuickMath.min(1, result[1]),
            QuickMath.min(1, result[2]));
  }

  /**
   * Postprocess a pixel. This applies gamma correction and clamps the color value to [0,1].
   *
   * @param result the resulting color values are written to this array
   */
  public void postProcessPixel(int x, int y, double[] result) {
    double r = samples[(y * width + x) * 3 + 0];
    double g = samples[(y * width + x) * 3 + 1];
    double b = samples[(y * width + x) * 3 + 2];

    r *= exposure;
    g *= exposure;
    b *= exposure;

    if (mode != RenderMode.PREVIEW) {
      switch (postprocess) {
        case NONE:
          break;
        case TONEMAP1:
          // http://filmicgames.com/archives/75
          r = QuickMath.max(0, r - 0.004);
          r = (r * (6.2 * r + .5)) / (r * (6.2 * r + 1.7) + 0.06);
          g = QuickMath.max(0, g - 0.004);
          g = (g * (6.2 * g + .5)) / (g * (6.2 * g + 1.7) + 0.06);
          b = QuickMath.max(0, b - 0.004);
          b = (b * (6.2 * b + .5)) / (b * (6.2 * b + 1.7) + 0.06);
          break;
        case GAMMA:
          r = FastMath.pow(r, 1 / DEFAULT_GAMMA);
          g = FastMath.pow(g, 1 / DEFAULT_GAMMA);
          b = FastMath.pow(b, 1 / DEFAULT_GAMMA);
          break;
      }
    } else {
      r = FastMath.sqrt(r);
      g = FastMath.sqrt(g);
      b = FastMath.sqrt(b);
    }

    result[0] = r;
    result[1] = g;
    result[2] = b;
  }

  /**
   * Compute the alpha channel based on sky visibility.
   */
  public void computeAlpha(int x, int y, WorkerState state) {
    Ray ray = state.ray;
    double halfWidth = width / (2.0 * height);
    double invHeight = 1.0 / height;

    // Rotated grid supersampling.

    camera
        .calcViewRay(ray, -halfWidth + (x - 3 / 8.0) * invHeight, -.5 + (y + 1 / 8.0) * invHeight);
    ray.o.x -= origin.x;
    ray.o.y -= origin.y;
    ray.o.z -= origin.z;

    double occlusion = PreviewRayTracer.skyOcclusion(this, state);

    camera
        .calcViewRay(ray, -halfWidth + (x + 1 / 8.0) * invHeight, -.5 + (y + 3 / 8.0) * invHeight);
    ray.o.x -= origin.x;
    ray.o.y -= origin.y;
    ray.o.z -= origin.z;

    occlusion += PreviewRayTracer.skyOcclusion(this, state);

    camera
        .calcViewRay(ray, -halfWidth + (x - 1 / 8.0) * invHeight, -.5 + (y - 3 / 8.0) * invHeight);
    ray.o.x -= origin.x;
    ray.o.y -= origin.y;
    ray.o.z -= origin.z;

    occlusion += PreviewRayTracer.skyOcclusion(this, state);

    camera
        .calcViewRay(ray, -halfWidth + (x + 3 / 8.0) * invHeight, -.5 + (y - 1 / 8.0) * invHeight);
    ray.o.x -= origin.x;
    ray.o.y -= origin.y;
    ray.o.z -= origin.z;

    occlusion += PreviewRayTracer.skyOcclusion(this, state);

    alphaChannel[y * width + x] = (byte) (255 * occlusion * 0.25 + 0.5);
  }

  /**
   * Copies a pixel in-buffer.
   */
  public void copyPixel(int jobId, int offset) {
    backBuffer.data[jobId + offset] = backBuffer.data[jobId];
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
        if (trace(ray) && ray.getCurrentMaterial() instanceof Block) {
          Block block = (Block) ray.getCurrentMaterial();
          buf.append(String.format("target: %.2f m\n", ray.distance));
          buf.append(String.format("[0x%08X] %s (%s)\n", ray.getCurrentData(), block,
              block.description(ray.getBlockData())));
        }
        Vector3 pos = camera.getPosition();
        buf.append(String.format("pos: (%.1f, %.1f, %.1f)", pos.x, pos.y, pos.z));
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
    consumer.accept(frontBuffer);
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
  public float[] getFoliageColor(int x, int z) {
    if (biomeColors) {
      return foliageTexture.get(x, z);
    } else {
      return Biomes.getFoliageColorLinear(0);
    }
  }

  /**
   * @param x X coordinate in octree space
   * @param z Z coordinate in octree space
   * @return Grass color for the given coordinates
   */
  public float[] getGrassColor(int x, int z) {
    if (biomeColors) {
      return grassTexture.get(x, z);
    } else {
      return Biomes.getGrassColorLinear(0);
    }
  }

  /**
   * Merge a render dump into this scene.
   */
  public void mergeDump(File dumpFile, TaskTracker taskTracker) {
    int dumpSpp;
    long dumpTime;
    try (TaskTracker.Task task = taskTracker.task("Merging render dump", 2);
        DataInputStream in = new DataInputStream(
            new GZIPInputStream(new FileInputStream(dumpFile)))) {
      task.update(1);
      Log.info("Loading render dump " + dumpFile.getAbsolutePath());
      int dumpWidth = in.readInt();
      int dumpHeight = in.readInt();
      if (dumpWidth != width || dumpHeight != height) {
        Log.warn("Render dump discarded: incorrect width or height!");
        return;
      }
      dumpSpp = in.readInt();
      dumpTime = in.readLong();

      double sa = spp / (double) (spp + dumpSpp);
      double sb = 1 - sa;

      for (int x = 0; x < width; ++x) {
        task.update(width, x + 1);
        for (int y = 0; y < height; ++y) {
          samples[(y * width + x) * 3 + 0] =
              samples[(y * width + x) * 3 + 0] * sa + in.readDouble() * sb;
          samples[(y * width + x) * 3 + 1] =
              samples[(y * width + x) * 3 + 1] * sa + in.readDouble() * sb;
          samples[(y * width + x) * 3 + 2] =
              samples[(y * width + x) * 3 + 2] * sa + in.readDouble() * sb;
          finalizePixel(x, y);
        }
      }
      Log.info("Render dump loaded");

      // Update render status.
      spp += dumpSpp;
      renderTime += dumpTime;
    } catch (IOException e) {
      Log.info("Render dump not loaded");
    }
  }

  public void setSaveSnapshots(boolean value) {
    saveSnapshots = value;
  }

  public boolean shouldSaveSnapshots() {
    return saveSnapshots;
  }

  public boolean isInWater(Ray ray) {
    if (worldOctree.isInside(ray.o)) {
      int x = (int) QuickMath.floor(ray.o.x);
      int y = (int) QuickMath.floor(ray.o.y);
      int z = (int) QuickMath.floor(ray.o.z);
      int block = worldOctree.get(x, y, z);
      return (block & 0xF) == Block.WATER_ID
          && ((ray.o.y - y) < 0.875 || block == (Block.WATER_ID | (1 << WaterModel.FULL_BLOCK)));
    } else {
      return waterHeight > 0 && ray.o.y < waterHeight - 0.125;
    }
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

  public Vector3 getFogColor() {
    return fogColor;
  }

  public void setFogColor(Vector3 color) {
    fogColor.set(color);
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
    JsonObject obj = super.toJson();
    JsonArray entityArray = new JsonArray();
    for (Entity entity : entities) {
      entityArray.add(entity.toJson());
    }
    if (entityArray.getNumElement() > 0) {
      obj.add("entities", entityArray);
    }
    JsonArray actorArray = new JsonArray();
    for (Entity entity : actors) {
      actorArray.add(entity.toJson());
    }
    if (actorArray.getNumElement() > 0) {
      obj.add("actors", actorArray);
    }
    return obj;
  }

  @Override public synchronized void fromJson(JsonObject desc) {
    super.fromJson(desc);

    entities = new LinkedList<>();
    actors = new LinkedList<>();
    for (JsonValue element : desc.get("entities").array().getElementList()) {
      Entity entity = Entity.fromJson(element.object());
      if (entity != null) {
        if (entity instanceof PlayerEntity) {
          actors.add(entity);
        } else {
          entities.add(entity);
        }
      }
    }
    for (JsonValue element : desc.get("actors").array().getElementList()) {
      Entity entity = Entity.fromJson(element.object());
      actors.add(entity);
    }
  }

  public Collection<Entity> getEntities() {
    return entities;
  }

  public Collection<Entity> getActors() {
    return actors;
  }

  public JsonObject getPlayerProfile(PlayerEntity entity) {
    if (profiles.containsKey(entity)) {
      return profiles.get(entity);
    } else {
      return new JsonObject();
    }
  }

  public void removePlayer(PlayerEntity player) {
    profiles.remove(player);
    actors.remove(player);
    rebuildActorBvh();
  }

  public void addPlayer(PlayerEntity player) {
    if (!actors.contains(player)) {
      profiles.put(player, new JsonObject());
      actors.add(player);
      rebuildActorBvh();
    } else {
      Log.warn("Failed to add player: entity already exists (" + player + ")");
    }
  }

  /**
   * Clears the scene, preparing to load fresh chunks.
   */
  public void clear() {
    cameraPresets = new JsonObject();
    entities.clear();
    actors.clear();
  }

  /** Create a backup of a scene file. */
  public void backupFile(RenderContext context, String fileName) {
    File renderDir = context.getSceneDirectory();
    File file = new File(renderDir, fileName);
    backupFile(context, file);
  }

  /** Create a backup of a scene file. */
  public void backupFile(RenderContext context, File file) {
    if (file.exists()) {
      // Try to create backup. It is not a problem if we fail this.
      String backupFileName = file.getName() + ".backup";
      File renderDir = context.getSceneDirectory();
      File backup = new File(renderDir, backupFileName);
      if (backup.exists()) {
        //noinspection ResultOfMethodCallIgnored
        backup.delete();
      }
      if (!file.renameTo(new File(renderDir, backupFileName))) {
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
    forceReset = true;

    // Wake up waiting threads.
    notifyAll();
  }

  /**
   * Resets the scene state to the default state.
   *
   * @param name sets the name for the scene
   */
  public synchronized void initializeNewScene(String name, SceneFactory sceneFactory) {
    boolean finalizeBufferPrev = finalizeBuffer;  // Remember the finalize setting.
    Scene newScene = sceneFactory.newScene();
    newScene.setName(name);
    copyState(newScene);
    copyTransients(newScene);
    forceReset = true;
    resetReason = ResetReason.SETTINGS_CHANGED;
    mode = RenderMode.PREVIEW;
    finalizeBuffer = finalizeBufferPrev;
  }
}
