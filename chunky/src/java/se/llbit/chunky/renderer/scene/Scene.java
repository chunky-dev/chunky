/* Copyright (c) 2012-2013 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.math3.util.FastMath;
import org.apache.log4j.Logger;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.model.WaterModel;
import se.llbit.chunky.renderer.Postprocess;
import se.llbit.chunky.renderer.ProgressListener;
import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.renderer.RenderStatusListener;
import se.llbit.chunky.renderer.WorkerState;
import se.llbit.chunky.world.Biomes;
import se.llbit.chunky.world.Block;
import se.llbit.chunky.world.BlockData;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.Heightmap;
import se.llbit.chunky.world.World;
import se.llbit.chunky.world.WorldTexture;
import se.llbit.math.Color;
import se.llbit.math.Octree;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Ray.RayPool;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector3i;
import se.llbit.png.PngFileWriter;

/**
 * Scene description.
 */
public class Scene extends SceneDescription {

	private static final Logger logger =
			Logger.getLogger(Scene.class);

	private static final Font infoFont = new Font("Sans serif", Font.BOLD, 11);
	private static FontMetrics fontMetrics;

	private final RayPool rayPool = new RayPool();

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

	/**
	 * Default emitter intensity
	 */
	public static final double DEFAULT_EMITTER_INTENSITY = 13;

	/**
	 * Minimum emitter intensity
	 */
	public static final double MIN_EMITTER_INTENSITY = 0.01;

	/**
	 * Maximum emitter intensity
	 */
	public static final double MAX_EMITTER_INTENSITY = 1000;

	/**
	 * Current CVF file format version
	 */
	public static final int CVF_VERSION = 1;

	private static final double DEFAULT_WATER_VISIBILITY = 9;

	//private static final double MIN_WATER_VISIBILITY = 0;
	//private static final double MAX_WATER_VISIBILITY = 62;

	/**
	 * Default exposure
	 */
	public static final double DEFAULT_EXPOSURE = 1.0;

	protected double waterVisibility = DEFAULT_WATER_VISIBILITY;

	/**
	 * World
	 */
	private World loadedWorld;

	/**
	 * Octree origin
	 */
	protected Vector3i origin = new Vector3i();

	/**
	 * Octree
	 */
	protected Octree octree;


	// chunk loading buffers
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

	private boolean finalized = false;

	private boolean finalizeBuffer = false;

	private boolean resetRender = false;

	/**
	 * Create an empty scene with default canvas width and height.
	 */
	public Scene() {
		octree = new Octree(1);// empty octree

		width = PersistentSettings.get3DCanvasWidth();
		height = PersistentSettings.get3DCanvasHeight();

		sppTarget = PersistentSettings.getSppTargetDefault();

		initBuffers();
	}

	private synchronized void initBuffers() {
		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		bufferData = ((DataBufferInt) backBuffer.getRaster().getDataBuffer()).getData();
		samples = new double[width*height*3];
	}

	/**
	 * Clone other scene
	 * @param other
	 */
	public Scene(Scene other) {
		set(other);
		copyRenderState(other);
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

		// the octree reference is overwritten to save time
		// when the other scene is changed it must create a new octree
		octree = other.octree;
		grassTexture = other.grassTexture;
		foliageTexture = other.foliageTexture;
		origin.set(other.origin);

		chunks = other.chunks;

		exposure = other.exposure;
		name = other.name;

		stillWater = other.stillWater;
		clearWater = other.clearWater;
		biomeColors = other.biomeColors;
		sunEnabled = other.sunEnabled;
		emittersEnabled = other.emittersEnabled;
		emitterIntensity = other.emitterIntensity;
		atmosphereEnabled = other.atmosphereEnabled;
		volumetricFogEnabled = other.volumetricFogEnabled;

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
			samples = other.samples;
			bufferData = other.bufferData;
		}
	}

	/**
	 * Copy the current rendering state.
	 * @param other
	 */
	public void copyRenderState(Scene other) {
		pathTrace = other.pathTrace;
		pauseRender = other.pauseRender;
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

		saveDescription(context.getSceneDescriptionOutputStream(name));

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
	public synchronized void loadScene(
			RenderContext context,
			RenderStatusListener renderListener,
			String sceneName)
			throws IOException, SceneLoadingError, InterruptedException {

		loadDescription(context.getSceneDescriptionInputStream(sceneName));

		// load the configured skymap file
		sky.loadSkymap();

		if (sdfVersion < SDF_VERSION) {
			logger.warn("Old scene version detected! The scene may not have loaded correctly.");
		}

		setCanvasSize(width, height);

		if (!worldPath.isEmpty()) {
			File worldDirectory = new File(worldPath);
			if (World.isWorldDir(worldDirectory)) {
				if (loadedWorld == null ||
						loadedWorld.getWorldDirectory() == null ||
						!loadedWorld.getWorldDirectory().getAbsolutePath().equals(worldPath)) {

					loadedWorld = new World(worldDirectory, true);
					loadedWorld.setDimension(worldDimension);

				} else if (loadedWorld.currentDimension() != worldDimension) {

					loadedWorld.setDimension(worldDimension);

				}
			} else {
				logger.debug("Could not load world: " + worldPath);
			}
		}

		if (pathTrace) {
			pauseRender = true;
		}

		refresh = false;

		loadDump(context, renderListener);

		if (loadOctree(context, renderListener)) {
			calculateOctreeOrigin(chunks);
			camera.setWorldSize(1<<octree.depth);

			boolean haveGrass = loadGrassTexture(context, renderListener);
			boolean haveFoliage = loadFoliageTexture(context, renderListener);
			if (!haveGrass || !haveFoliage) {
				biomeColors = false;
			}
		} else {
			// Could not load stored octree
			// Load the chunks from the world
			if (loadedWorld == null) {
				logger.warn("Could not load chunks (no world found for scene)");
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
		if (!pathTrace)
			refresh();
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
	 * @param ray
	 * @param rayPool
	 */
	public void quickTrace(WorkerState state) {
		state.ray.x.x -= origin.x;
		state.ray.x.y -= origin.y;
		state.ray.x.z -= origin.z;

		RayTracer.quickTrace(this, state);
	}

	/**
	 * Path trace the ray in this scene
	 * @param state
	 */
	public void pathTrace(WorkerState state) {

		state.ray.x.x -= origin.x;
		state.ray.x.y -= origin.y;
		state.ray.x.z -= origin.z;

		PathTracer.pathTrace(this, state);
	}

	/**
	 * @param ray
	 * @return <code>true</code> if an intersection was found
	 */
	public boolean intersect(Ray ray) {
		return octree.intersect(this, ray);
	}

	protected final boolean kill(Ray ray, Random random) {
		return ray.depth >= rayDepth && random.nextDouble() < .5f;
	}

	/**
	 * Reload all loaded chunks.
	 * @param progressListener
	 */
	public synchronized void reloadChunks(ProgressListener progressListener) {
		if (loadedWorld == null) {
			logger.warn("Can not reload chunks for scene - world directory not found!");
			return;
		}

		loadedWorld.setDimension(worldDimension);
		loadedWorld.reload();
		loadChunks(progressListener, loadedWorld, chunks);
		refresh();
	}

	/**
	 * Load chunks into the Octree
	 * @param progressListener
	 * @param world
	 * @param chunksToLoad
	 */
	public synchronized void loadChunks(
			ProgressListener progressListener,
			World world,
			Collection<ChunkPosition> chunksToLoad) {

		if (world == null)
			return;

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

		// create new octree to fit all chunks
		octree = new Octree(requiredDepth);

		if (waterHeight > 0) {
			for (int x = 0; x < (1<<octree.depth); ++x) {
				for (int z = 0; z < (1<<octree.depth); ++z) {
					for (int y = -origin.y; y < (-origin.y)+waterHeight-1; ++y) {
						octree.set(Block.WATER.id | (1<<WaterModel.FULL_BLOCK), x, y, z);
					}
				}
			}
			for (int x = 0; x < (1<<octree.depth); ++x) {
				for (int z = 0; z < (1<<octree.depth); ++z) {
					octree.set(Block.WATER.id, x, (-origin.y)+waterHeight-1, z);
				}
			}
		}

		// parse the regions first - force chunk lists to be populated!
		Set<ChunkPosition> regions = new HashSet<ChunkPosition>();
		for (ChunkPosition cp: chunksToLoad) {
			regions.add(cp.getRegionPosition());
		}

		for (ChunkPosition region: regions) {
			world.regionDiscovered(region);
			world.getRegion(region).parse();
		}

		Heightmap biomeIdMap = new Heightmap();
		task = "Loading chunks";
		int done = 1;
		int target = chunksToLoad.size();
		for (ChunkPosition cp : chunksToLoad) {

			progressListener.setProgress(task, done, 0, target);
			done += 1;

			if (loadedChunks.contains(cp))
				continue;

			loadedChunks.add(cp);

			world.getChunk(cp).getBlockData(blocks, data, biomes);
			nchunks += 1;

			for (int cz = 0; cz < 16; ++cz) {
				int wz = cz + cp.z*16;
				for (int cx = 0; cx < 16; ++cx) {
					int wx = cx + cp.x*16;
					int biomeId = 0xFF & biomes[Chunk.chunkXZIndex(cx, cz)];
					biomeIdMap.set(biomeId, wx, wz);
				}
			}

			for (int cy = 0; cy < 256; ++cy) {
				for (int cz = 0; cz < 16; ++cz) {
					int z = cz + cp.z*16 - origin.z;
					for (int cx = 0; cx < 16; ++cx) {
						int x = cx + cp.x*16 - origin.x;
						int index = Chunk.chunkIndex(cx, cy, cz);
						Block block = Block.get(blocks[index]);

						if (cx > 0 && cx < 15 && cz > 0 && cz < 15 && cy > 0 && cy < 255 &&
								block != Block.STONE && block.isOpaque) {

							// set obscured blocks to stone
							if (Block.get(blocks[index-1]).isOpaque &&
									Block.get(blocks[index+1]).isOpaque &&
									Block.get(blocks[index-Chunk.X_MAX]).isOpaque &&
									Block.get(blocks[index+Chunk.X_MAX]).isOpaque &&
									Block.get(blocks[index-Chunk.X_MAX*Chunk.Z_MAX]).isOpaque &&
									Block.get(blocks[index+Chunk.X_MAX*Chunk.Z_MAX]).isOpaque) {
								octree.set(Block.STONE.id, x, cy - origin.y, z);
								continue;
							}
						}

						int metadata = 0xFF & data[index/2];
						metadata >>= (cx % 2) * 4;
						metadata &= 0xF;

						if (block == Block.STATIONARYWATER)
							block = Block.WATER;
						else if (block == Block.STATIONARYLAVA)
							block = Block.LAVA;

						int type = block.id;
						// store metadata
						switch (block.id) {
						case Block.VINES_ID:
							if (cy < 255) {
								// is this the top vine block?
								index = Chunk.chunkIndex(cx, cy+1, cz);
								Block above = Block.get(blocks[index]);
								if (above.isSolid) {
									type = type | (1<<BlockData.VINE_TOP);
								}
							}
							break;

						case Block.WATER_ID:
							if (cy < 255) {
								// is there water above?
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

						case Block.LAVA_ID:
							if (cy < 255) {
								// is there lava above?
								index = Chunk.chunkIndex(cx, cy+1, cz);
								Block above = Block.get(blocks[index]);
								if (above.isLava()) {
									type = type | (1<<WaterModel.FULL_BLOCK);
								}
							}
							break;

						case Block.GRASS_ID:
							if (cy < 255) {
								// is it snow covered?
								index = Chunk.chunkIndex(cx, cy+1, cz);
								int blockAbove = 0xFF & blocks[index];
								if (blockAbove == Block.SNOW.id) {
									type = type | (1 << 8);// 9th bit is the snow bit
								}
							}
							// fallthrough!

						case Block.WOODENDOOR_ID:
						case Block.IRONDOOR_ID:
						{
							int top = 0;
							int bottom = 0;
							if ((metadata & 8) != 0) {
								// this is the top part of the door
								top = metadata;
								if (cy > 0) {
									bottom = 0xFF & data[Chunk.chunkIndex(cx, cy-1, cz)/2];
									bottom >>= (cx % 2) * 4;// extract metadata
									bottom &= 0xF;
								}
							} else {
								// this is the bottom part of the door
								bottom = metadata;
								if (cy < 255) {
									top = 0xFF & data[Chunk.chunkIndex(cx, cy+1, cz)/2];
									top >>= (cx % 2) * 4;// extract metadata
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
						if (block.isEmitter)
							emitters += 1;
						if (block.isInvisible)
							type = 0;
						octree.set(type, cx + cp.x*16 - origin.x,
								cy - origin.y, cz + cp.z*16 - origin.z);
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

			// finalize grass and foliage textures
			// box blur 3x3
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

			OctreeFinalizer.finalizeChunk(octree, origin, cp);
		}

		chunks = loadedChunks;

		camera.setWorldSize(1<<octree.depth);

		logger.info(String.format("Loaded %d chunks (%d emitters)",
				nchunks, emitters));
	}

	private int calculateOctreeOrigin(Collection<ChunkPosition> chunksToLoad) {

		int xmin = Integer.MAX_VALUE;
		int xmax = Integer.MIN_VALUE;
		int zmin = Integer.MAX_VALUE;
		int zmax = Integer.MIN_VALUE;
		for (ChunkPosition cp: chunksToLoad) {
			if (cp.x < xmin)
				xmin = cp.x;
			if (cp.x > xmax)
				xmax = cp.x;
			if (cp.z < zmin)
				zmin = cp.z;
			if (cp.z > zmax)
				zmax = cp.z;
		}

		xmax += 1;
		zmax += 1;
		xmin *= 16;
		xmax *= 16;
		zmin *= 16;
		zmax *= 16;

		int maxDimension = Math.max(Chunk.Y_MAX, Math.max(xmax-xmin, zmax-zmin));
		int requiredDepth = QuickMath.log2(QuickMath.nextPow2(maxDimension));

		int xroom = (1<<requiredDepth)-(xmax-xmin);
		int yroom = (1<<requiredDepth)-Chunk.Y_MAX;
		int zroom = (1<<requiredDepth)-(zmax-zmin);

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
		if (chunks.isEmpty())
			return new Vector3d(0, 128, 0);

		int xmin = Integer.MAX_VALUE;
		int xmax = Integer.MIN_VALUE;
		int zmin = Integer.MAX_VALUE;
		int zmax = Integer.MIN_VALUE;
		for (ChunkPosition cp: chunks) {
			if (cp.x < xmin)
				xmin = cp.x;
			if (cp.x > xmax)
				xmax = cp.x;
			if (cp.z < zmin)
				zmin = cp.z;
			if (cp.z > zmax)
				zmax = cp.z;
		}
		xmax += 1;
		zmax += 1;
		xmin *= 16;
		xmax *= 16;
		zmin *= 16;
		zmax *= 16;
		int xcenter = (xmax + xmin)/2;
		int zcenter = (zmax + zmin)/2;
		for (int y = Chunk.Y_MAX-1; y >= 0; --y) {
			int block = octree.get(xcenter - origin.x, y - origin.y,
					zcenter - origin.z);
			if (Block.get(block) != Block.AIR) {
				return new Vector3d(xcenter, y+5, zcenter);
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
	 * @return <code>true</code> if this scene is to be Monte Carlo path traced
	 */
	public boolean pathTrace() {
		return pathTrace;
	}

	/**
	 * Toggle Monte Carlo path tracing.
	 */
	public synchronized void toggleMonteCarlo() {
		pathTrace = !pathTrace;
		refresh();
	}

	/**
	 * Start or resume path tracing
	 */
	public synchronized void goHeadless() {
		pathTrace = true;
		pauseRender = false;
		notifyAll();
	}

	/**
	 * @return {@code true} if the refresh happened
	 * @throws InterruptedException
	 */
	public synchronized boolean waitOnRefreshOrStateChange() throws InterruptedException {
		while ((!pathTrace || pauseRender) && !refresh)
			wait();
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
		while (pauseRender) {
			wait();
		}
	}

	/**
	 * @return <code>true</code> if the rendering is paused
	 */
	public synchronized boolean isPaused() {
		return pauseRender;
	}

	/**
	 * Start rendering the scene.
	 */
	public synchronized void startRender() {
		if (!pathTrace) {
			pathTrace = true;
			pauseRender = false;
			refresh();
		}
	}

	/**
	 * Pause the renderer.
	 */
	public synchronized void pauseRender() {
		pauseRender = true;
	}

	/**
	 * Resume a paused render.
	 */
	public synchronized void resumeRender() {
		pauseRender = false;
		notifyAll();
	}

	/**
	 * Halt the rendering process.
	 * Puts the renderer back in preview mode.
	 */
	public synchronized void haltRender() {
		if (pathTrace) {
			pathTrace = false;
			pauseRender = false;
			resetRender = true;
			refresh();
		}
	}

	/**
	 * Move the camera to the player position, if available.
	 */
	public void moveCameraToPlayer() {
		camera.moveToPlayer(loadedWorld);
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
	 */
	public void trace(Ray ray) {
		ray.d.set(0, 0, 1);
		ray.x.set(camera.getPosition());
		ray.x.x -= origin.x;
		ray.x.y -= origin.y;
		ray.x.z -= origin.z;
		camera.transform(ray.d);
		while (intersect(ray)) {
			if (ray.currentMaterial != 0) {
				ray.hit = true;
				break;
			}
		}
	}

	/**
	 * Perform auto focus
	 */
	public void autoFocus() {
		Ray ray = RayPool.getDefaultRayPool().get();
		trace(ray);
		if (!ray.hit) {
			camera.setDof(Double.POSITIVE_INFINITY);
		} else {
			camera.setSubjectDistance(ray.distance);
			camera.setDof(ray.distance*ray.distance);
		}
		RayPool.getDefaultRayPool().dispose(ray);
	}

	/**
	 * @return The Octree object
	 */
	public Octree getOctree() {
		return octree;
	}

	/**
	 * @return World origin in the Octree
	 */
	public Vector3i getOrigin() {
		return origin;
	}

	/**
	 * Set the clear water flag
	 * @param value
	 */
	public void setClearWater(boolean value) {
		if (value != clearWater) {
			clearWater	= value;
			refresh();
		}
	}

	/**
	 * @return <code>true</code> if clear water is enabled
	 */
	public boolean getClearWater() {
		return clearWater;
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
		if (!pathTrace)
			refresh();
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
	 * Set the atmospheric scattering flag
	 * @param value
	 */
	public void setAtmosphereEnabled(boolean value) {
		if (value != atmosphereEnabled) {
			atmosphereEnabled = value;
			refresh();
		}
	}

	/**
	 * Set the volumetric fog flag
	 * @param value
	 */
	public void setVolumetricFogEnabled(boolean value) {
		if (value != volumetricFogEnabled) {
			volumetricFogEnabled = value;
			refresh();
		}
	}

	/**
	 * @return <code>true</code> if atmospheric scattering is enabled
	 */
	public boolean atmosphereEnabled() {
		return atmosphereEnabled;
	}

	/**
	 * @return <code>true</code> if volumetric fog is enabled
	 */
	public boolean volumetricFogEnabled() {
		return volumetricFogEnabled;
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
	 */
	public void saveSnapshot(File directory) {

		try {
			if (directory == null) {
				logger.error("Fatal error: bad scene directory!");
				return;
			}
			String fileName = name + "-" + spp + ".png";
			logger.info("Saving frame " + fileName);
			PngFileWriter.write(buffer, new File(directory, fileName));
			logger.info("Frame saved");
		} catch (IOException e) {
			logger.warn("Failed to save current frame. Reason: " +
				e.getMessage(), e);
		}
	}

	/**
	 * @param targetFile
	 * @param progressListener
	 * @throws IOException
	 */
	public synchronized void saveFrame(File targetFile,
			ProgressListener progressListener) throws IOException {

		for (int x = 0; x < width; ++x) {
			progressListener.setProgress("Finalizing frame", x+1, 0, width);
			for (int y = 0; y < height; ++y) {
				finalizePixel(x, y);
			}
		}

		ImageIO.write(backBuffer, "png", targetFile);
	}

	private synchronized void saveOctree(
			RenderContext context,
			ProgressListener progressListener) {

		String fileName = name + ".octree";
		DataOutputStream out = null;
		try {
			String task = "Saving octree";
			progressListener.setProgress(task, 1, 0, 2);
			logger.info("Saving octree " + fileName);
			out = new DataOutputStream(new GZIPOutputStream(
					context.getSceneFileOutputStream(fileName)));

			octree.store(out);

			progressListener.setProgress(task, 2, 0, 2);
			logger.info("Octree saved");
		} catch (IOException e) {
			logger.warn("IO exception while saving octree!", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private synchronized void saveGrassTexture(
			RenderContext context,
			ProgressListener progressListener) {

		String fileName = name + ".grass";
		DataOutputStream out = null;
		try {
			String task = "Saving grass texture";
			progressListener.setProgress(task, 1, 0, 2);
			logger.info("Saving grass texture " + fileName);
			out = new DataOutputStream(new GZIPOutputStream(
					context.getSceneFileOutputStream(fileName)));

			grassTexture.store(out);

			progressListener.setProgress(task, 2, 0, 2);
			logger.info("Grass texture saved");
		} catch (IOException e) {
			logger.warn("IO exception while saving octree!", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private synchronized void saveFoliageTexture(
			RenderContext context,
			ProgressListener progressListener) {

		String fileName = name + ".foliage";
		DataOutputStream out = null;
		try {
			String task = "Saving foliage texture";
			progressListener.setProgress(task, 1, 0, 2);
			logger.info("Saving foliage texture " + fileName);
			out = new DataOutputStream(new GZIPOutputStream(
					context.getSceneFileOutputStream(fileName)));

			foliageTexture.store(out);

			progressListener.setProgress(task, 2, 0, 2);
			logger.info("Foliage texture saved");
		} catch (IOException e) {
			logger.warn("IO exception while saving octree!", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private synchronized void saveDump(
			RenderContext context,
			ProgressListener progressListener) {

		String fileName = name + ".dump";
		DataOutputStream out = null;
		try {
			String task = "Saving render dump";
			progressListener.setProgress(task, 1, 0, 2);
			logger.info("Saving render dump " + fileName);
			out = new DataOutputStream(new GZIPOutputStream(
					context.getSceneFileOutputStream(fileName)));
			out.writeInt(width);
			out.writeInt(height);
			out.writeInt(spp);
			out.writeLong(renderTime);
			for (int x = 0; x < width; ++x) {
				progressListener.setProgress(task, x+1, 0, width);
				for (int y = 0; y < height; ++y) {
					out.writeDouble(samples[(y*width+x)*3+0]);
					out.writeDouble(samples[(y*width+x)*3+1]);
					out.writeDouble(samples[(y*width+x)*3+2]);
				}
			}
			logger.info("Render dump saved");
		} catch (IOException e) {
			logger.warn("IO exception while saving render dump!", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private synchronized boolean loadOctree(
			RenderContext context,
			RenderStatusListener renderListener) {

		String fileName = name + ".octree";

		DataInputStream in = null;
		try {
			String task = "Loading octree";
			renderListener.setProgress(task, 1, 0, 2);
			logger.info("Loading octree " + fileName);
			in = new DataInputStream(new GZIPInputStream(
					context.getSceneFileInputStream(fileName)));

			octree = Octree.load(in);

			renderListener.setProgress(task, 2, 0, 2);
			logger.info("Octree loaded");
			return true;
		} catch (IOException e) {
			logger.info("Failed to load chunk octree: Missing file or incorrect format!", e);
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

	private synchronized boolean loadGrassTexture(
			RenderContext context,
			RenderStatusListener renderListener) {

		String fileName = name + ".grass";

		DataInputStream in = null;
		try {
			String task = "Loading grass texture";
			renderListener.setProgress(task, 1, 0, 2);
			logger.info("Loading grass texture " + fileName);
			in = new DataInputStream(new GZIPInputStream(
					context.getSceneFileInputStream(fileName)));

			grassTexture = WorldTexture.load(in);

			renderListener.setProgress(task, 2, 0, 2);
			logger.info("Grass texture loaded");
			return true;
		} catch (IOException e) {
			logger.info("Failed to load grass texture!");
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

	private synchronized boolean loadFoliageTexture(
			RenderContext context,
			RenderStatusListener renderListener) {

		String fileName = name + ".foliage";

		DataInputStream in = null;
		try {
			String task = "Loading foliage texture";
			renderListener.setProgress(task, 1, 0, 2);
			logger.info("Loading foliage texture " + fileName);
			in = new DataInputStream(new GZIPInputStream(
					context.getSceneFileInputStream(fileName)));

			foliageTexture = WorldTexture.load(in);

			renderListener.setProgress(task, 2, 0, 2);
			logger.info("Foliage texture loaded");
			return true;
		} catch (IOException e) {
			logger.info("Failed to load foliage texture!");
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

	public synchronized void loadDump(
			RenderContext context,
			RenderStatusListener renderListener) {

		String fileName = name + ".dump";

		DataInputStream in = null;
		try {
			in = new DataInputStream(new GZIPInputStream(
					context.getSceneFileInputStream(fileName)));

			String task = "Loading render dump";
			renderListener.setProgress(task, 1, 0, 2);
			logger.info("Loading render dump " + fileName);
			int dumpWidth = in.readInt();
			int dumpHeight= in.readInt();
			if (dumpWidth != width || dumpHeight != height) {
				logger.warn("Render dump discarded: incorrect width or height!");
				in.close();
				return;
			}
			spp = in.readInt();
			renderTime = in.readLong();

			// Update render status
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
			logger.info("Render dump loaded");
		} catch (IOException e) {
			logger.info("Render dump not loaded");
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

		double r = samples[(y*width+x)*3+0];
		double g = samples[(y*width+x)*3+1];
		double b = samples[(y*width+x)*3+2];

		r *= exposure;
		g *= exposure;
		b *= exposure;

		if (pathTrace()) {
			switch (postprocess) {
			case NONE:
				break;
			case TONEMAP1:
				// http://filmicgames.com/archives/75
				r = QuickMath.max(0, r-0.004);
				r = (r*(6.2*r + .5)) / (r * (6.2*r + 1.7) + 0.06);
				g = QuickMath.max(0, g-0.004);
				g = (g*(6.2*g + .5)) / (g * (6.2*g + 1.7) + 0.06);
				b = QuickMath.max(0, b-0.004);
				b = (b*(6.2*b + .5)) / (b * (6.2*b + 1.7) + 0.06);
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

		r = QuickMath.min(1, r);
		g = QuickMath.min(1, g);
		b = QuickMath.min(1, b);

		bufferData[y*width + x] = Color.getRGB(r, g, b);
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
	public synchronized void updateCanvas(String warningText) {
		finalized = false;

		try {
			// flip buffers
			BufferedImage tmp = buffer;
			buffer = backBuffer;
			backBuffer = tmp;

			bufferData = ((DataBufferInt) backBuffer.getRaster().getDataBuffer()).getData();

			Graphics g = buffer.getGraphics();

			if (!warningText.isEmpty()) {
				g.setColor(java.awt.Color.red);
				int x0 = width/2;
				int y0 = height/2;
				g.setFont(infoFont);
				if (fontMetrics == null) {
					fontMetrics = g.getFontMetrics();
				}
				g.drawString(warningText,
						x0 - fontMetrics.stringWidth(warningText)/2, y0);
			} else {

				if (!pathTrace()) {
					int x0 = width/2;
					int y0 = height/2;
					g.setColor(java.awt.Color.white);
					g.drawLine(x0, y0-4, x0, y0+4);
					g.drawLine(x0-4, y0, x0+4, y0);
					Ray ray = rayPool.get();
					trace(ray);
					g.setFont(infoFont);
					if (ray.hit) {
						Block block = ray.getCurrentBlock();
						g.drawString(String.format("target: %.2f m", ray.distance), 5, height-18);
						g.drawString(String.format("[0x%08X] %s (%s)",
								ray.currentMaterial,
								block,
								block.description(ray.getBlockData())),
								5, height-5);
					}
					Vector3d pos = camera.getPosition();
					g.drawString(String.format("(%.1f, %.1f, %.1f)",
							pos.x, pos.y, pos.z), 5, 11);
					rayPool.dispose(ray);
				}
			}

			g.dispose();
		} catch (IllegalStateException e) {
			logger.error("Unexpected exception while rendering back buffer", e);
		}
	}

	/**
	 * Prepare the front buffer for rendering by flipping the back and front buffer.
	 * Draw status text on the front buffer.
	 */
	public void updateCanvas() {
		if (!finalized)
			return;
		updateCanvas(haveLoadedChunks() ? "" : "No chunks loaded!");
	}

	/**
	 * Draw the buffered image to a canvas
	 * @param g The graphics object of the canvas to draw on
	 * @param canvasWidth The canvas width
	 * @param canvasHeight The canvas height
	 */
	public synchronized void drawBufferedImage(Graphics g, int canvasWidth, int canvasHeight) {
		g.drawImage(buffer, 0, 0, canvasWidth, canvasHeight, null);
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
	public boolean finalizeBuffer() {
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
			logger.info("Loading render dump " + dumpFile.getAbsolutePath());
			int dumpWidth = in.readInt();
			int dumpHeight= in.readInt();
			if (dumpWidth != width || dumpHeight != height) {
				logger.warn("Render dump discarded: incorrect widht or height!");
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
			logger.info("Render dump loaded");

			// Update render status
			spp += dumpSpp;
			renderTime += dumpTime;
			renderListener.setSPP(spp);
			renderListener.setRenderTime(renderTime);
			long totalSamples = spp * ((long) (width * height));
			renderListener.setSamplesPerSecond(
					(int) (totalSamples / (renderTime / 1000.0)));

		} catch (IOException e) {
			logger.info("Render dump not loaded");
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

}
