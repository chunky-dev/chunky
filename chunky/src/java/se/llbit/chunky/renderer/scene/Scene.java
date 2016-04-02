/* Copyright (c) 2012-2015 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.math3.util.FastMath;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.model.WaterModel;
import se.llbit.chunky.renderer.OutputMode;
import se.llbit.chunky.renderer.Postprocess;
import se.llbit.chunky.renderer.ProgressListener;
import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.renderer.RenderState;
import se.llbit.chunky.renderer.RenderStatusListener;
import se.llbit.chunky.renderer.WorkerState;
import se.llbit.chunky.renderer.projection.ProjectionMode;
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
import se.llbit.math.Color;
import se.llbit.math.Octree;
import se.llbit.math.OctreeVisitor;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector3i;
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.ListTag;
import se.llbit.png.IEND;
import se.llbit.png.ITXT;
import se.llbit.png.PngFileWriter;
import se.llbit.tiff.TiffFileWriter;
import se.llbit.util.MCDownloader;

/**
 * Scene description.
 */
public class Scene extends SceneDescription {

	protected static final int DEFAULT_DUMP_FREQUENCY = 500;

	protected static final double fSubSurface = 0.3;

	/**
	 * Minimum canvas width
	 */
	public static final int MIN_CANVAS_WIDTH = 20;

	/**
	 * Minimum canvas height
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

	/** Default emitter intensity. */
	public static final double DEFAULT_EMITTER_INTENSITY = 13;

	/** Minimum emitter intensity. */
	public static final double MIN_EMITTER_INTENSITY = 0.01;

	/** Maximum emitter intensity. */
	public static final double MAX_EMITTER_INTENSITY = 1000;

	//private static final double MIN_WATER_VISIBILITY = 0;
	//private static final double MAX_WATER_VISIBILITY = 62;

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

	/** Entities in the scene. */
	private Collection<Entity> entities = new LinkedList<Entity>();

	/** Poseable entities in the scene. */
	private Collection<Entity> actors = new LinkedList<Entity>();

	/** Poseable entities in the scene. */
	private Map<PlayerEntity, JsonObject> profiles = Collections.emptyMap();

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

	private BufferedImage buffer;

	private BufferedImage backBuffer;

	private double[] samples;

	private int[] bufferData;
	private byte[] alphaChannel;

	private boolean finalized = false;

	private boolean finalizeBuffer = false;

	/**
	 * Indicates if the render should be forced to reset. If false, the user may be
	 * asked to confirm the render reset.
	 */
	private boolean resetRender = false;

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

	private synchronized void initBuffers() {
		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		bufferData = ((DataBufferInt) backBuffer.getRaster().getDataBuffer()).getData();
		alphaChannel = new byte[width*height];
		samples = new double[width*height*3];
	}

	/**
	 * Clone other scene
	 * @param other
	 */
	public Scene(Scene other) {
		set(other);
		copyTransients(other);
	}

	/**
	 * Set scene equal to other
	 * @param other
	 */
	synchronized public void set(Scene other) {
		loadedWorld = other.loadedWorld;
		worldPath = other.worldPath;
		worldDimension = other.worldDimension;

		// The octree reference is overwritten to save time.
		// When the other scene is changed it must create a new octree.
		worldOctree = other.worldOctree;
		entities = other.entities;
		actors = other.actors;
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

		refresh = other.refresh;

		finalized = false;

		if (samples != other.samples) {
			width = other.width;
			height = other.height;
			backBuffer = other.backBuffer;
			buffer = other.buffer;
			alphaChannel = other.alphaChannel;
			samples = other.samples;
			bufferData = other.bufferData;
		}
	}

	/**
	 * Save the scene description, render dump, and foliage
	 * and grass textures.
	 * @param context
	 * @param progressListener
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public synchronized void saveScene(
			RenderContext context,
			RenderStatusListener progressListener)
		throws IOException, InterruptedException {

		String task = "Saving scene";
		progressListener.setProgress(task, 1, 0, 2);

		BufferedOutputStream out = new BufferedOutputStream(context.getSceneDescriptionOutputStream(name));
		saveDescription(out);

		saveOctree(context, progressListener);
		saveGrassTexture(context, progressListener);
		saveFoliageTexture(context, progressListener);

		saveDump(context, progressListener);

		progressListener.sceneSaved();
	}

	/**
	 * Load a stored scene by file name.
	 * @param context
	 * @param renderListener
	 * @param sceneName file name of the scene to load
	 * @throws IOException
	 * @throws SceneLoadingError
	 * @throws InterruptedException
	 */
	public synchronized void loadScene(RenderContext context,
			RenderStatusListener renderListener, String sceneName)
			throws IOException, SceneLoadingError, InterruptedException {

		loadDescription(context.getSceneDescriptionInputStream(sceneName));

		// Load the configured skymap file.
		sky.loadSkymap();

		if (sdfVersion < SDF_VERSION) {
			Log.warn("Old scene version detected! The scene may not have been loaded correctly.");
		} else if (sdfVersion > SDF_VERSION) {
			Log.warn("This scene was created with a newer version of Chunky! The scene may not have been loaded correctly.");
		}

		setCanvasSize(width, height);

		if (!worldPath.isEmpty()) {
			File worldDirectory = new File(worldPath);
			if (World.isWorldDir(worldDirectory)) {
				if (loadedWorld == null
						|| loadedWorld.getWorldDirectory() == null
						|| !loadedWorld.getWorldDirectory().getAbsolutePath().equals(worldPath)) {

					loadedWorld = new World(worldDirectory, true);
					loadedWorld.setDimension(worldDimension);

				} else if (loadedWorld.currentDimension() != worldDimension) {

					loadedWorld.setDimension(worldDimension);

				}
			} else {
				Log.info("Could not load world: " + worldPath);
			}
		}

		if (renderState == RenderState.RENDERING) {
			renderState = RenderState.PAUSED;
		}

		refresh = false;

		loadDump(context, renderListener);

		if (loadOctree(context, renderListener)) {
			boolean haveGrass = loadGrassTexture(context, renderListener);
			boolean haveFoliage = loadFoliageTexture(context, renderListener);
			if (!haveGrass || !haveFoliage) {
				biomeColors = false;
			}
		} else {
			// Could not load stored octree.
			// Load the chunks from the world.
			if (loadedWorld == null) {
				Log.warn("Could not load chunks (no world found for scene)");
			} else {
				loadChunks(renderListener, loadedWorld, chunks);
			}
		}
		notifyAll();
	}

	/**
	 * Set the exposure value
	 * @param value
	 */
	public synchronized void setExposure(double value) {
		exposure = value;
		if (renderState == RenderState.PREVIEW) {
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
	 * Set still water mode
	 * @param value
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
	 * Set emitters enable flag
	 * @param value
	 */
	public synchronized void setEmittersEnabled(boolean value) {
		if (value != emittersEnabled) {
			emittersEnabled = value;
			refresh();
		}
	}

	/**
	 * Set sunlight enable flag
	 * @param value
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
	 * Quick ray trace
	 * @param state
	 */
	public void quickTrace(WorkerState state) {
		state.ray.o.x -= origin.x;
		state.ray.o.y -= origin.y;
		state.ray.o.z -= origin.z;

		RayTracer.quickTrace(this, state);
	}

	/**
	 * Path trace the ray in this scene
	 * @param state
	 */
	public void pathTrace(WorkerState state) {
		state.ray.o.x -= origin.x;
		state.ray.o.y -= origin.y;
		state.ray.o.z -= origin.z;

		PathTracer.pathTrace(this, state);
	}

	/**
	 * Find closest intersection between ray and scene.
	 * This advances the ray by updating the ray origin if an intersection is found.
	 * @param ray ray to test against scene
	 * @return <code>true</code> if an intersection was found
	 */
	public boolean intersect(Ray ray) {
		boolean hit = false;
		if (bvh.closestIntersection(ray)) {
			hit = true;
		}
		if (actorBvh.closestIntersection(ray)) {
			hit = true;
		}
		Ray oct = new Ray(ray);
		oct.setCurrentMat(ray.getPrevMaterial(), ray.getPrevData());
		if (worldOctree.intersect(this, oct) && oct.distance < ray.t) {
			ray.distance += oct.distance;
			ray.o.set(oct.o);
			ray.n.set(oct.n);
			ray.color.set(oct.color);
			ray.setPrevMat(oct.getPrevMaterial(), oct.getPrevData());
			ray.setCurrentMat(oct.getCurrentMaterial(), oct.getCurrentData());
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
		if (ray.getCurrentMaterial() == Block.WATER ||
				(ray.getCurrentMaterial() == Block.AIR && ray.getPrevMaterial() == Block.WATER)) {
			if (useCustomWaterColor) {
				ray.color.x = waterColor.x;
				ray.color.y = waterColor.y;
				ray.color.z = waterColor.z;
			}
			ray.color.w = waterOpacity;
		}
	}

	/**
	 * Test if the ray should be killed (Russian Roulette)
	 * @param depth
	 * @param random
	 * @return {@code true} if the ray needs to die now
	 */
	protected final boolean kill(int depth, Random random) {
		return depth >= rayDepth && random.nextDouble() < .5f;
	}

	/**
	 * Reload all loaded chunks.
	 * @param progressListener
	 */
	public synchronized void reloadChunks(ProgressListener progressListener) {
		if (loadedWorld == null) {
			Log.warn("Can not reload chunks for scene - world directory not found!");
			return;
		}
		loadedWorld.setDimension(worldDimension);
		loadedWorld.reload();
		loadChunks(progressListener, loadedWorld, chunks);
		refresh();
	}

	/**
	 * Load chunks into the Octree.
	 * @param progressListener
	 * @param world
	 * @param chunksToLoad
	 */
	public synchronized void loadChunks(ProgressListener progressListener,
			World world, Collection<ChunkPosition> chunksToLoad) {

		if (world == null) {
			return;
		}

		String task = "Loading regions";
		progressListener.setProgress(task, 1, 0, 2);

		loadedWorld = world;
		worldPath = loadedWorld.getWorldDirectory().getAbsolutePath();
		worldDimension = world.currentDimension();

		int emitters = 0;
		int nchunks = 0;

		if (chunksToLoad.isEmpty()) {
			return;
		}

		Set<ChunkPosition> loadedChunks = new HashSet<ChunkPosition>();

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
		Set<ChunkPosition> regions = new HashSet<ChunkPosition>();
		for (ChunkPosition cp : chunksToLoad) {
			regions.add(cp.getRegionPosition());
		}

		for (ChunkPosition region : regions) {
			world.getRegion(region).parse();
		}

		entities = new LinkedList<Entity>();
		if (actors.isEmpty() && PersistentSettings.getLoadPlayers()) {
			// We don't load actor entities if some already exists. Loading actor entities
			// risks resetting posed actors when reloading chunks for an existing scene.
			actors = new LinkedList<Entity>();
			profiles = new HashMap<PlayerEntity, JsonObject>();
			Collection<PlayerEntity> players = world.playerEntities();
			task = "Loading entities";
			int done = 1;
			int target = players.size();
			for (PlayerEntity entity : players) {
				entity.randomPose();
				progressListener.setProgress(task, done, 0, target);
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

		int ycutoff = PersistentSettings.getYCutoff();
		ycutoff = Math.max(0, ycutoff);

		Heightmap biomeIdMap = new Heightmap();
		task = "Loading chunks";
		int done = 1;
		int target = chunksToLoad.size();
		for (ChunkPosition cp : chunksToLoad) {
			progressListener.setProgress(task, done, 0, target);
			done += 1;

			if (loadedChunks.contains(cp)) {
				continue;
			}

			loadedChunks.add(cp);

			Collection<CompoundTag> tileEntities = new LinkedList<CompoundTag>();
			Collection<CompoundTag> ents = new LinkedList<CompoundTag>();
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
					entities.add(new PaintingEntity(new Vector3d(x, y, z),
							tag.get("Motive").stringValue(), yaw));
				}
			}

			// Load tile entities.
			for (CompoundTag entityTag: tileEntities) {
				int x = entityTag.get("x").intValue(0) - wx0;
				int y = entityTag.get("y").intValue(0);
				int z = entityTag.get("z").intValue(0) - wz0;
				int index = Chunk.chunkIndex(x, y, z);
				int block = 0xFF & blocks[index];
				int metadata = 0xFF & data[index/2];
				metadata >>= (x % 2) * 4;
				metadata &= 0xF;
				Vector3d position = new Vector3d(x + wx0, y, z + wz0);
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
					int z = cz + cp.z*16 - origin.z;
					for (int cx = 0; cx < 16; ++cx) {
						int x = cx + cp.x*16 - origin.x;
						int index = Chunk.chunkIndex(cx, cy, cz);
						Block block = Block.get(blocks[index]);

						if (cx > 0 && cx < 15 && cz > 0 && cz < 15 && cy > 0 && cy < 255 &&
								block != Block.STONE && block.isOpaque) {

							// Set obscured blocks to stone.
							if (Block.get(blocks[index-1]).isOpaque &&
									Block.get(blocks[index+1]).isOpaque &&
									Block.get(blocks[index-Chunk.X_MAX]).isOpaque &&
									Block.get(blocks[index+Chunk.X_MAX]).isOpaque &&
									Block.get(blocks[index-Chunk.X_MAX*Chunk.Z_MAX]).isOpaque &&
									Block.get(blocks[index+Chunk.X_MAX*Chunk.Z_MAX]).isOpaque) {
								worldOctree.set(Block.STONE_ID, x, cy - origin.y, z);
								continue;
							}
						}

						int metadata = 0xFF & data[index/2];
						metadata >>= (cx % 2) * 4;
						metadata &= 0xF;

						int type = block.id;
						// Store metadata.
						switch (block.id) {
						case Block.VINES_ID:
							if (cy < 255) {
								// Is this the top vine block?
								index = Chunk.chunkIndex(cx, cy+1, cz);
								Block above = Block.get(blocks[index]);
								if (above.isSolid) {
									type = type | (1<<BlockData.VINE_TOP);
								}
							}
							break;

						case Block.STATIONARYWATER_ID:
							type = Block.WATER_ID;
						case Block.WATER_ID:
							if (cy < 255) {
								// Is there water above?
								index = Chunk.chunkIndex(cx, cy+1, cz);
								Block above = Block.get(blocks[index]);
								if (above.isWater()) {
									type |= (1<<WaterModel.FULL_BLOCK);
								} else if (above == Block.LILY_PAD) {
									type |= (1<<BlockData.LILY_PAD);
									long wx = cp.x * 16L + cx;
									long wy = cy + 1;
									long wz = cp.z * 16L + cz;
									long pr = (wx * 3129871L) ^ (wz * 116129781L) ^ (wy);
									pr = pr * pr * 42317861L + pr * 11L;
									int dir = 3 & (int)(pr >> 16);
									type |= (dir<<BlockData.LILY_PAD_ROTATION);
								}
							}
							break;

						case Block.FIRE_ID: {
							long wx = cp.x * 16L + cx;
							long wy = cy + 1;
							long wz = cp.z * 16L + cz;
							long pr = (wx * 3129871L) ^ (wz * 116129781L) ^ (wy);
							pr = pr * pr * 42317861L + pr * 11L;
							int dir = 0xF & (int)(pr >> 16);
							type |= (dir<<BlockData.LILY_PAD_ROTATION);
						}
						break;

						case Block.STATIONARYLAVA_ID:
							type = Block.LAVA_ID;
						case Block.LAVA_ID:
							if (cy < 255) {
								// Is there lava above?
								index = Chunk.chunkIndex(cx, cy+1, cz);
								Block above = Block.get(blocks[index]);
								if (above.isLava()) {
									type = type | (1<<WaterModel.FULL_BLOCK);
								}
							}
							break;

						case Block.GRASS_ID:
							if (cy < 255) {
								// Is it snow covered?
								index = Chunk.chunkIndex(cx, cy+1, cz);
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
						case Block.DARKOAKDOOR_ID:
						{
							int top = 0;
							int bottom = 0;
							if ((metadata & 8) != 0) {
								// This is the top part of the door.
								top = metadata;
								if (cy > 0) {
									bottom = 0xFF & data[Chunk.chunkIndex(cx, cy-1, cz)/2];
									bottom >>= (cx % 2) * 4; // Extract metadata.
									bottom &= 0xF;
								}
							} else {
								// This is the bottom part of the door.
								bottom = metadata;
								if (cy < 255) {
									top = 0xFF & data[Chunk.chunkIndex(cx, cy+1, cz)/2];
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

		grassTexture = new WorldTexture();
		foliageTexture = new WorldTexture();

		Set<ChunkPosition> chunkSet = new HashSet<ChunkPosition>(chunksToLoad);

		task = "Finalizing octree";
		done = 0;
		for (ChunkPosition cp : chunksToLoad) {

			// Finalize grass and foliage textures.
			// 3x3 box blur.
			for (int x = 0; x < 16; ++x) {
				for (int z = 0; z < 16; ++z) {

					int nsum = 0;
					float[] grassMix = { 0, 0, 0 };
					float[] foliageMix = { 0, 0, 0 };
					for (int sx = x-1; sx <= x+1; ++sx) {
						int wx = cp.x*16 + sx;
						for (int sz = z-1; sz <= z+1; ++sz) {
							int wz = cp.z*16 + sz;

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
					grassTexture.set(cp.x*16 + x - origin.x,
							cp.z*16 + z - origin.z, grassMix);

					foliageMix[0] /= nsum;
					foliageMix[1] /= nsum;
					foliageMix[2] /= nsum;
					foliageTexture.set(cp.x*16 + x - origin.x,
							cp.z*16 + z - origin.z, foliageMix);
				}
			}
			progressListener.setProgress(task, done, 0, target);
			done += 1;
			OctreeFinalizer.finalizeChunk(worldOctree, origin, cp);
		}
		chunks = loadedChunks;
		camera.setWorldSize(1 << worldOctree.depth);
		buildBvh();
		buildActorBvh();
		Log.info(String.format("Loaded %d chunks (%d emitters)", nchunks, emitters));
	}

	private void buildBvh() {
		final List<Primitive> primitives = new LinkedList<Primitive>();

		worldOctree.visit(new OctreeVisitor() {
			@Override
			public void visit(int data, int x, int y, int z, int size) {
				if ((data & 0xF) == Block.WATER_ID) {
					WaterModel.addPrimitives(primitives, data, x, y, z, 1<<size);
				}
			}
		});

		Vector3d worldOffset = new Vector3d(-origin.x, -origin.y, -origin.z);
		for (Entity entity : entities) {
			primitives.addAll(entity.primitives(worldOffset));
		}
		bvh = new BVH(primitives);
	}

	private void buildActorBvh() {
		final List<Primitive> actorPrimitives = new LinkedList<Primitive>();
		Vector3d worldOffset = new Vector3d(-origin.x, -origin.y, -origin.z);
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
		for (ChunkPosition cp: chunksToLoad) {
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

		int xroom = (1<<requiredDepth) - (xmax-xmin);
		int yroom = (1<<requiredDepth) - Chunk.Y_MAX;
		int zroom = (1<<requiredDepth) - (zmax-zmin);

		origin.set(xmin - xroom/2, -yroom/2, zmin - zroom/2);
		return requiredDepth;
	}

	/**
	 * @return The currently loaded chunks
	 */
	public Collection<ChunkPosition> loadedChunks() {
		return chunks;
	}

	/**
	 * @return <code>true</code> if the scene has loaded chunks
	 */
	public synchronized boolean haveLoadedChunks() {
		return !chunks.isEmpty();
	}

	/**
	 * Calculate a camera position centered above all loaded chunks.
	 * @return The calculated camera position
	 */
	public Vector3d calcCenterCamera() {
		if (chunks.isEmpty()) {
			return new Vector3d(0, 128, 0);
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
			if (Block.get(block) != Block.AIR) {
				return new Vector3d(xcenter, y + 5, zcenter);
			}
		}
		return new Vector3d(xcenter, 128, zcenter);
	}

	/**
	 * Set the biome colors flag
	 * @param value
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
	 * Start rendering
	 */
	public synchronized void startHeadlessRender() {
		renderState = RenderState.RENDERING;
		notifyAll();
	}

	/**
	 * @return {@code true} if the refresh happened
	 * @throws InterruptedException
	 */
	public synchronized boolean waitOnRefreshOrStateChange() throws InterruptedException {
		while (renderState != RenderState.RENDERING && !refresh) {
			wait();
		}
		if (refresh) {
			refresh = false;
			return true;
		}
		return false;
	}

	/**
	 * @return <code>true</code> if the rendering of this scene should be
	 * restarted
	 */
	public boolean shouldRefresh() {
		return refresh;
	}

	/**
	 * Wait while the rendering is paused
	 * @throws InterruptedException
	 */
	public synchronized void pauseWait() throws InterruptedException {
		while (renderState == RenderState.PAUSED) {
			wait();
		}
	}

	/**
	 * Start rendering the scene.
	 */
	public synchronized void startRender() {
		if (renderState != RenderState.RENDERING) {
			renderState = RenderState.RENDERING;
			refresh();
		}
	}

	/**
	 * Pause the renderer.
	 */
	public synchronized void pauseRender() {
		renderState = RenderState.PAUSED;
	}

	/**
	 * Resume a paused render.
	 */
	public synchronized void resumeRender() {
		renderState = RenderState.RENDERING;
		notifyAll();
	}

	/**
	 * Halt the rendering process.
	 * Puts the renderer back in preview mode.
	 */
	public synchronized void haltRender() {
		if (renderState != RenderState.PREVIEW) {
			renderState = RenderState.PREVIEW;
			resetRender = true;
			refresh();
		}
	}

	/**
	 * Move the camera to the player position, if available.
	 */
	public void moveCameraToPlayer() {
		for (Entity entity : entities) {
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
	 * @param value
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
	synchronized public void setRefreshed() {
		refresh = false;
	}

	/**
	 * Trace a ray in the Octree
	 * @param ray
	 * @return {@code true} if the ray hit something
	 */
	public boolean trace(Ray ray) {
		WorkerState state = new WorkerState();
		state.ray = ray;
		if (isInWater(ray)) {
			ray.setCurrentMat(Block.WATER, 0);
		} else {
			ray.setCurrentMat(Block.AIR, 0);
		}
		ray.d.set(0, 0, 1);
		ray.o.set(camera.getPosition());
		ray.o.x -= origin.x;
		ray.o.y -= origin.y;
		ray.o.z -= origin.z;
		camera.transform(ray.d);
		while (RayTracer.nextIntersection(this, ray, state)) {
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
	 * @return {@code null} if the camera is not aiming at some intersectable object
	 */
	public Vector3d getTargetPosition() {
		Ray ray = new Ray();
		if (!trace(ray)) {
			return null;
		} else {
			Vector3d target = new Vector3d(ray.o);
			target.add(origin.x, origin.y, origin.z);
			return target;
		}
	}

	/**
	 * @return The Octree object
	 */
	public Octree getOctree() {
		return worldOctree;
	}

	/**
	 * @return World origin in the Octree
	 */
	public Vector3i getOrigin() {
		return origin;
	}

	/**
	 * Set the scene name
	 * @param newName
	 */
	public void setName(String newName) {
		newName = SceneManager.sanitizedSceneName(newName);
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
	 * @param p The new postprocessing mode
	 */
	public synchronized void setPostprocess(Postprocess p) {
		postprocess = p;
		if (renderState == RenderState.PREVIEW) {
			// don't interrupt the render if we are currently rendering
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
	 * Set the emitter intensity
	 * @param value
	 */
	public void setEmitterIntensity(double value) {
		emitterIntensity = value;
		refresh();
	}

	/**
	 * Set the transparent sky option.
	 * @param value
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
	 * Set the ocean water height
	 * @param value
	 */
	public void setWaterHeight(int value) {
		value = Math.max(0, value);
		value = Math.min(256, value);
		if (value != waterHeight) {
			waterHeight = value;
			refresh();
		}
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
	 * are disabled
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
	 * Copy variables that do not require a render restart
	 * @param other
	 */
	public void copyTransients(Scene other) {
		name = other.name;
		postprocess = other.postprocess;
		exposure = other.exposure;
		dumpFrequency = other.dumpFrequency;
		saveSnapshots = other.saveSnapshots;
		sppTarget = other.sppTarget;
		cameraPresets = other.cameraPresets;
		rayDepth = other.rayDepth;
		renderState = other.renderState;
		outputMode = other.outputMode;
	}

	/**
	 * @return The target SPP
	 */
	public int getTargetSPP() {
		return sppTarget;
	}

	/**
	 * @param value Target SPP value
	 */
	public void setTargetSPP(int value) {
		sppTarget = value;
	}

	/**
	 * Change the canvas size
	 * @param canvasWidth
	 * @param canvasHeight
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
	 * @param directory
	 * @param progressListener
	 */
	public void saveSnapshot(File directory, ProgressListener progressListener) {

		if (directory == null) {
			Log.error("Fatal error: bad output directory!");
			return;
		}
		String fileName = String.format("%s-%d%s", name, spp, outputMode.getExtension());
		File targetFile = new File(directory, fileName);
		computeAlpha(progressListener);
		finalizeFrame(progressListener);
		writeImage(targetFile, progressListener);
	}

	/**
	 * @param targetFile
	 * @param progressListener
	 * @throws IOException
	 */
	public synchronized void saveFrame(File targetFile, ProgressListener progressListener)
			throws IOException {
		computeAlpha(progressListener);
		finalizeFrame(progressListener);
		writeImage(targetFile, progressListener);
	}

	/**
	 * Compute the alpha channel.
	 * @param progressListener
	 */
	private void computeAlpha(ProgressListener progressListener) {
		if (transparentSky) {
			if (outputMode == OutputMode.TIFF_32) {
				Log.warn("Can not use transparent sky with TIFF output mode.");
			} else {
				WorkerState state = new WorkerState();
				state.ray = new Ray();
				for (int x = 0; x < width; ++x) {
					progressListener.setProgress("Computing alpha channel", x + 1, 0, width);
					for (int y = 0; y < height; ++y) {
						computeAlpha(x, y, state);
					}
				}
			}
		}
	}

	public void finalizeFrame(ProgressListener progressListener) {
		if (!finalized) {
			for (int x = 0; x < width; ++x) {
				progressListener.setProgress("Finalizing frame", x + 1, 0, width);
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
	private void writeImage(File targetFile, ProgressListener progressListener) {
		if (outputMode == OutputMode.PNG) {
			writePng(targetFile, progressListener);
		} else if (outputMode == OutputMode.TIFF_32) {
			writeTiff(targetFile, progressListener);
		}
	}

	/**
	 * Write PNG image.
	 *
	 * @param targetFile file to write to.
	 */
	private void writePng(File targetFile, ProgressListener progressListener) {
		try {
			progressListener.setProgress("Writing PNG", 0, 0, 1);
			PngFileWriter writer = new PngFileWriter(targetFile);
			if (transparentSky) {
				writer.write(backBuffer, alphaChannel, progressListener);
			} else {
				writer.write(backBuffer, progressListener);
			}
			if (camera.getProjectionMode() == ProjectionMode.PANORAMIC
					&& camera.getFoV() >= 179 && camera.getFoV() <= 181) {
				int height = backBuffer.getHeight();
				int width = backBuffer.getWidth();
				StringBuilder xmp = new StringBuilder();
				xmp.append("<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>\n");
				xmp.append(" <rdf:Description rdf:about=''\n");
				xmp.append("   xmlns:GPano='http://ns.google.com/photos/1.0/panorama/'>\n");
				xmp.append(" <GPano:CroppedAreaImageHeightPixels>");
				xmp.append(height);
				xmp.append("</GPano:CroppedAreaImageHeightPixels>\n");
				xmp.append(" <GPano:CroppedAreaImageWidthPixels>");
				xmp.append(width);
				xmp.append("</GPano:CroppedAreaImageWidthPixels>\n");
				xmp.append(" <GPano:CroppedAreaLeftPixels>0</GPano:CroppedAreaLeftPixels>\n");
				xmp.append(" <GPano:CroppedAreaTopPixels>0</GPano:CroppedAreaTopPixels>\n");
				xmp.append(" <GPano:FullPanoHeightPixels>");
				xmp.append(height);
				xmp.append("</GPano:FullPanoHeightPixels>\n");
				xmp.append(" <GPano:FullPanoWidthPixels>");
				xmp.append(width);
				xmp.append("</GPano:FullPanoWidthPixels>\n");
				xmp.append(" <GPano:ProjectionType>equirectangular</GPano:ProjectionType>\n");
				xmp.append(" <GPano:UsePanoramaViewer>True</GPano:UsePanoramaViewer>\n");
				xmp.append(" </rdf:Description>\n");
				xmp.append(" </rdf:RDF>");
				ITXT iTXt = new ITXT("XML:com.adobe.xmp", xmp.toString());
				writer.writeChunk(iTXt);
			}
			writer.writeChunk(new IEND());
			writer.close();
		} catch (IOException e) {
			Log.warn("Failed to write PNG file: " + targetFile.getAbsolutePath(), e);
		}
	}

	/**
	 * Write TIFF image.
	 *
	 * @param targetFile file to write to.
	 */
	private void writeTiff(File targetFile, ProgressListener progressListener) {
		try {
			progressListener.setProgress("Writing TIFF", 0, 0, 1);
			TiffFileWriter writer = new TiffFileWriter(targetFile);
			writer.write32(this, progressListener);
			writer.close();
		} catch (IOException e) {
			Log.warn("Failed to write TIFF file: " + targetFile.getAbsolutePath(), e);
		}
	}

	private synchronized void saveOctree(RenderContext context, ProgressListener progressListener) {
		String fileName = name + ".octree";
		DataOutputStream out = null;
		try {
			if (context.fileUnchangedSince(fileName, worldOctree.getTimestamp())) {
				Log.info("Skipping redundant Octree write");
				return;
			}
			String task = "Saving octree";
			progressListener.setProgress(task, 1, 0, 2);
			Log.info("Saving octree " + fileName);
			out = new DataOutputStream(new GZIPOutputStream(
					context.getSceneFileOutputStream(fileName)));

			worldOctree.store(out);
			out.close();
			out = null;
			worldOctree.setTimestamp(context.fileTimestamp(fileName));

			progressListener.setProgress(task, 2, 0, 2);
			Log.info("Octree saved");
		} catch (IOException e) {
			Log.warn("IO exception while saving octree!", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private synchronized void saveGrassTexture(RenderContext context,
			ProgressListener progressListener) {
		String fileName = name + ".grass";
		DataOutputStream out = null;
		try {
			if (context.fileUnchangedSince(fileName, grassTexture.getTimestamp())) {
				Log.info("Skipping redundant grass texture write");
				return;
			}
			String task = "Saving grass texture";
			progressListener.setProgress(task, 1, 0, 2);
			Log.info("Saving grass texture " + fileName);
			out = new DataOutputStream(new GZIPOutputStream(
					context.getSceneFileOutputStream(fileName)));

			grassTexture.store(out);
			out.close();
			out = null;
			grassTexture.setTimestamp(context.fileTimestamp(fileName));

			progressListener.setProgress(task, 2, 0, 2);
			Log.info("Grass texture saved");
		} catch (IOException e) {
			Log.warn("IO exception while saving octree!", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private synchronized void saveFoliageTexture(RenderContext context,
			ProgressListener progressListener) {
		String fileName = name + ".foliage";
		DataOutputStream out = null;
		try {
			if (context.fileUnchangedSince(fileName, foliageTexture.getTimestamp())) {
				Log.info("Skipping redundant foliage texture write");
				return;
			}
			String task = "Saving foliage texture";
			progressListener.setProgress(task, 1, 0, 2);
			Log.info("Saving foliage texture " + fileName);
			out = new DataOutputStream(new GZIPOutputStream(
					context.getSceneFileOutputStream(fileName)));

			foliageTexture.store(out);
			out.close();
			out = null;
			foliageTexture.setTimestamp(context.fileTimestamp(fileName));

			progressListener.setProgress(task, 2, 0, 2);
			Log.info("Foliage texture saved");
		} catch (IOException e) {
			Log.warn("IO exception while saving octree!", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private synchronized void saveDump(RenderContext context, ProgressListener progressListener) {
		String fileName = name + ".dump";
		DataOutputStream out = null;
		try {
			String task = "Saving render dump";
			progressListener.setProgress(task, 1, 0, 2);
			Log.info("Saving render dump " + fileName);
			out = new DataOutputStream(new GZIPOutputStream(
					context.getSceneFileOutputStream(fileName)));
			out.writeInt(width);
			out.writeInt(height);
			out.writeInt(spp);
			out.writeLong(renderTime);
			for (int x = 0; x < width; ++x) {
				progressListener.setProgress(task, x + 1, 0, width);
				for (int y = 0; y < height; ++y) {
					out.writeDouble(samples[(y * width + x) * 3 + 0]);
					out.writeDouble(samples[(y * width + x) * 3 + 1]);
					out.writeDouble(samples[(y * width + x) * 3 + 2]);
				}
			}
			Log.info("Render dump saved");
		} catch (IOException e) {
			Log.warn("IO exception while saving render dump!", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private synchronized boolean loadOctree(RenderContext context,
			RenderStatusListener renderListener) {
		String fileName = name + ".octree";
		DataInputStream in = null;
		try {
			String task = "Loading octree";
			renderListener.setProgress(task, 1, 0, 2);
			Log.info("Loading octree " + fileName);
			in = new DataInputStream(new GZIPInputStream(
					context.getSceneFileInputStream(fileName)));
			worldOctree = Octree.load(in);
			in.close();
			in = null;
			worldOctree.setTimestamp(context.fileTimestamp(fileName));
			renderListener.setProgress(task, 2, 0, 2);
			Log.info("Octree loaded");
			calculateOctreeOrigin(chunks);
			camera.setWorldSize(1 << worldOctree.depth);
			buildBvh();
			buildActorBvh();
			return true;
		} catch (IOException e) {
			Log.info("Failed to load chunk octree: missing file or incorrect format!", e);
			return false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private synchronized boolean loadGrassTexture(RenderContext context,
			RenderStatusListener renderListener) {
		String fileName = name + ".grass";
		DataInputStream in = null;
		try {
			String task = "Loading grass texture";
			renderListener.setProgress(task, 1, 0, 2);
			Log.info("Loading grass texture " + fileName);
			in = new DataInputStream(new GZIPInputStream(
					context.getSceneFileInputStream(fileName)));
			grassTexture = WorldTexture.load(in);
			in.close();
			in = null;
			grassTexture.setTimestamp(context.fileTimestamp(fileName));
			renderListener.setProgress(task, 2, 0, 2);
			Log.info("Grass texture loaded");
			return true;
		} catch (IOException e) {
			Log.info("Failed to load grass texture!");
			return false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private synchronized boolean loadFoliageTexture(RenderContext context,
			RenderStatusListener renderListener) {
		String fileName = name + ".foliage";
		DataInputStream in = null;
		try {
			String task = "Loading foliage texture";
			renderListener.setProgress(task, 1, 0, 2);
			Log.info("Loading foliage texture " + fileName);
			in = new DataInputStream(new GZIPInputStream(
					context.getSceneFileInputStream(fileName)));
			foliageTexture = WorldTexture.load(in);
			in.close();
			in = null;
			foliageTexture.setTimestamp(context.fileTimestamp(fileName));
			renderListener.setProgress(task, 2, 0, 2);
			Log.info("Foliage texture loaded");
			return true;
		} catch (IOException e) {
			Log.info("Failed to load foliage texture!");
			return false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public synchronized void loadDump(RenderContext context,
			RenderStatusListener renderListener) {
		String fileName = name + ".dump";
		DataInputStream in = null;
		try {
			in = new DataInputStream(new GZIPInputStream(
					context.getSceneFileInputStream(fileName)));
			String task = "Loading render dump";
			renderListener.setProgress(task, 1, 0, 2);
			Log.info("Loading render dump " + fileName);
			int dumpWidth = in.readInt();
			int dumpHeight= in.readInt();
			if (dumpWidth != width || dumpHeight != height) {
				Log.warn("Render dump discarded: incorrect width or height!");
				in.close();
				return;
			}
			spp = in.readInt();
			renderTime = in.readLong();

			// Update render status.
			renderListener.setSPP(spp);
			renderListener.setRenderTime(renderTime);
			long totalSamples = spp * ((long) (width * height));
			renderListener.setSamplesPerSecond(
					(int) (totalSamples / (renderTime / 1000.0)));

			for (int x = 0; x < width; ++x) {
				renderListener.setProgress(task, x+1, 0, width);
				for (int y = 0; y < height; ++y) {
					samples[(y*width+x)*3+0] = in.readDouble();
					samples[(y*width+x)*3+1] = in.readDouble();
					samples[(y*width+x)*3+2] = in.readDouble();
					finalizePixel(x, y);
				}
			}
			Log.info("Render dump loaded");
		} catch (IOException e) {
			Log.info("Render dump not loaded");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Finalize a pixel. Calculates the resulting RGB color values for
	 * the pixel and sets these in the bitmap image.
	 * @param x
	 * @param y
	 */
	public void finalizePixel(int x, int y) {
		finalized = true;
		double[] result = new double[3];
		postProcessPixel(x, y, result);
		bufferData[y*width + x] = Color.getRGB(
				QuickMath.min(1, result[0]),
				QuickMath.min(1, result[1]),
				QuickMath.min(1, result[2]));
	}

	/**
	 * Postprocess a pixel. This applies gamma correction and clamps the color value to [0,1].
	 * @param x
	 * @param y
	 * @param result the resulting color values are written to this array
	 */
	public void postProcessPixel(int x, int y, double[] result) {
		double r = samples[(y * width + x) * 3 + 0];
		double g = samples[(y * width + x) * 3 + 1];
		double b = samples[(y * width + x) * 3 + 2];

		r *= exposure;
		g *= exposure;
		b *= exposure;

		if (renderState != RenderState.PREVIEW) {
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
				r = FastMath.pow(r, 1/DEFAULT_GAMMA);
				g = FastMath.pow(g, 1/DEFAULT_GAMMA);
				b = FastMath.pow(b, 1/DEFAULT_GAMMA);
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
	 * @param x
	 * @param y
	 */
	public void computeAlpha(int x, int y, WorkerState state) {
		Ray ray = state.ray;
		double halfWidth = width/(2.0*height);
		double invHeight = 1.0 / height;

		// Rotated grid supersampling.

		camera.calcViewRay(ray,
				-halfWidth + (x - 3/8.0) * invHeight,
				-.5 + (y + 1/8.0) * invHeight);
		ray.o.x -= origin.x;
		ray.o.y -= origin.y;
		ray.o.z -= origin.z;

		double occlusion = RayTracer.skyOcclusion(this, state);

		camera.calcViewRay(ray,
				-halfWidth + (x + 1/8.0) * invHeight,
				-.5 + (y + 3/8.0) * invHeight);
		ray.o.x -= origin.x;
		ray.o.y -= origin.y;
		ray.o.z -= origin.z;

		occlusion += RayTracer.skyOcclusion(this, state);

		camera.calcViewRay(ray,
				-halfWidth + (x - 1/8.0) * invHeight,
				-.5 + (y - 3/8.0) * invHeight);
		ray.o.x -= origin.x;
		ray.o.y -= origin.y;
		ray.o.z -= origin.z;

		occlusion += RayTracer.skyOcclusion(this, state);

		camera.calcViewRay(ray,
				-halfWidth + (x + 3/8.0) * invHeight,
				-.5 + (y - 1/8.0) * invHeight);
		ray.o.x -= origin.x;
		ray.o.y -= origin.y;
		ray.o.z -= origin.z;

		occlusion += RayTracer.skyOcclusion(this, state);

		alphaChannel[y*width + x] = (byte) (255 * occlusion*0.25 + 0.5);
	}

	/**
	 * Copies a pixel in-buffer
	 * @param jobId
	 * @param offset
	 */
	public void copyPixel(int jobId, int offset) {
		bufferData[jobId + offset] = bufferData[jobId];
	}

	/**
	 * Update the canvas - draw the latest rendered frame
	 * @param warningText
	 */
	public synchronized String sceneStatus(String warningText) {
		try {
			if (!warningText.isEmpty()) {
				return warningText;
			} else {
				StringBuilder buf = new StringBuilder();
				buf.append("<html>");
				Ray ray = new Ray();
				if (trace(ray) && ray.getCurrentMaterial() instanceof Block) {
					Block block = (Block) ray.getCurrentMaterial();
					buf.append(String.format("target: %.2f m<br>", ray.distance));
					buf.append(String.format("[0x%08X] %s (%s)<br>",
							ray.getCurrentData(),
							block,
							block.description(ray.getBlockData())));
				}
				Vector3d pos = camera.getPosition();
				buf.append(String.format("pos: (%.1f, %.1f, %.1f)",
						pos.x, pos.y, pos.z));
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
	public synchronized void updateCanvas() {
		finalized = false;
		BufferedImage tmp = buffer;
		buffer = backBuffer;
		backBuffer = tmp;
		bufferData = ((DataBufferInt) backBuffer.getRaster().getDataBuffer()).getData();
	}

	/** @return scene status text. */
	public String sceneStatus() {
		return sceneStatus(haveLoadedChunks() ? "" : "No chunks loaded!");
	}

	/**
	 * Draw the buffered image to a canvas
	 * @param g The graphics object of the canvas to draw on
	 * @param canvasWidth The canvas width
	 * @param canvasHeight The canvas height
	 */
	public synchronized void drawBufferedImage(Graphics g, int offsetX, int offsetY,
			int canvasWidth, int canvasHeight) {
		g.drawImage(buffer, offsetX, offsetY, canvasWidth, canvasHeight, null);
	}

	/**
	 * Get direct access to the sample buffer
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
	 * Enable or disable buffer finalization
	 * @param value
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
	 * Merge a render dump into this scene
	 * @param dumpFile
	 * @param renderListener
	 */
	public void mergeDump(File dumpFile, RenderStatusListener renderListener) {
		int dumpSpp;
		long dumpTime;
		DataInputStream in = null;
		try {
			in = new DataInputStream(new GZIPInputStream(
					new FileInputStream(dumpFile)));

			String task = "Merging render dump";
			renderListener.setProgress(task, 1, 0, 2);
			Log.info("Loading render dump " + dumpFile.getAbsolutePath());
			int dumpWidth = in.readInt();
			int dumpHeight= in.readInt();
			if (dumpWidth != width || dumpHeight != height) {
				Log.warn("Render dump discarded: incorrect widht or height!");
				in.close();
				return;
			}
			dumpSpp = in.readInt();
			dumpTime = in.readLong();

			double sa = spp / (double) (spp + dumpSpp);
			double sb = 1 - sa;

			for (int x = 0; x < width; ++x) {
				renderListener.setProgress(task, x+1, 0, width);
				for (int y = 0; y < height; ++y) {
					samples[(y*width+x)*3+0] = samples[(y*width+x)*3+0] * sa
							+ in.readDouble() * sb;
					samples[(y*width+x)*3+1] = samples[(y*width+x)*3+1] * sa
							+ in.readDouble() * sb;
					samples[(y*width+x)*3+2] = samples[(y*width+x)*3+2] * sa
							+ in.readDouble() * sb;
					finalizePixel(x, y);
				}
			}
			Log.info("Render dump loaded");

			// Update render status
			spp += dumpSpp;
			renderTime += dumpTime;
			renderListener.setSPP(spp);
			renderListener.setRenderTime(renderTime);
			long totalSamples = spp * ((long) (width * height));
			renderListener.setSamplesPerSecond(
					(int) (totalSamples / (renderTime / 1000.0)));

		} catch (IOException e) {
			Log.info("Render dump not loaded");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public void setSaveSnapshots(boolean value) {
		saveSnapshots = value;
	}

	public boolean shouldSaveSnapshots() {
		return saveSnapshots;
	}

	public boolean shouldReset() {
		if (resetRender) {
			resetRender = false;
			return true;
		}
		return false;
	}

	synchronized public void resetRender() {
		resetRender = true;
		refresh();
	}

	public boolean isInWater(Ray ray) {
		if (worldOctree.isInside(ray.o)) {
			int x = (int) QuickMath.floor(ray.o.x);
			int y = (int) QuickMath.floor(ray.o.y);
			int z = (int) QuickMath.floor(ray.o.z);
			int block = worldOctree.get(x, y, z);
			if ((block & 0xF) != Block.WATER_ID) {
				return false;
			}
			return (ray.o.y - y) < 0.875 || block == (Block.WATER_ID | (1<<WaterModel.FULL_BLOCK));
		} else {
			return waterHeight > 0 && ray.o.y < waterHeight - 0.125;
		}
	}

	public boolean isInsideOctree(Vector3d vec) {
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

	public Vector3d getWaterColor() {
		return waterColor;
	}

	public void setWaterColor(Vector3d color) {
		waterColor.set(color);
		refresh();
	}

	public Vector3d getFogColor() {
		return fogColor;
	}

	public void setFogColor(Vector3d color) {
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

	@Override
	public synchronized JsonObject toJson() {
		JsonObject obj = super.toJson();
		JsonArray entityArray = new JsonArray();
		for (Entity entity : actors) {
			entityArray.add(entity.toJson());
		}
		for (Entity entity : entities) {
			entityArray.add(entity.toJson());
		}
		if (entityArray.getNumElement() > 0) {
			obj.add("entities", entityArray);
		}
		return obj;
	}

	@Override
	public synchronized void fromJson(JsonObject desc) {
		super.fromJson(desc);

		entities = new LinkedList<Entity>();
		actors = new LinkedList<Entity>();
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
}
