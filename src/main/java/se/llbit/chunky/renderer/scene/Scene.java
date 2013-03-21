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
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import se.llbit.chunky.model.WaterModel;
import se.llbit.chunky.renderer.Postprocess;
import se.llbit.chunky.renderer.ProgressListener;
import se.llbit.chunky.renderer.Refreshable;
import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.renderer.RenderManager;
import se.llbit.chunky.renderer.RenderStatusListener;
import se.llbit.chunky.renderer.RenderableCanvas;
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
import se.llbit.nbt.AnyTag;
import se.llbit.nbt.ByteTag;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.DoubleTag;
import se.llbit.nbt.IntTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.LongTag;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;
import se.llbit.util.ProgramProperties;
import se.llbit.util.VectorPool;

/**
 * Scene description.
 */
public class Scene implements Refreshable {
	
	private static final Logger logger =
			Logger.getLogger(Scene.class);
	
	private static final Font infoFont = new Font("Sans serif", Font.BOLD, 11);
	private static FontMetrics fontMetrics;
	
	private RayPool rayPool = new RayPool();
	
	private static final int DEFAULT_DUMP_FREQUENCY = 500;
	
	private static final int DEFAULT_SPP_TARGET = 1000;
	
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
	 * Default exposure
	 */
	public static final double DEFAULT_EXPOSURE = 1.0;
	
	/**
	 * Default gamma
	 */
	public static final double DEFAULT_GAMMA = 2.2;
	
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
	
	private static final int DEFAULT_CLOUD_HEIGHT = 128;
	
	int cloudHeight = DEFAULT_CLOUD_HEIGHT;
	
	protected double waterVisibility = DEFAULT_WATER_VISIBILITY;

	/**
	 * Recursive ray depth limit (not including RR)
	 */
	public static int rayDepth = 5;
	
	private String name = "default";
	private String skymapFileName;

	protected final Sky sky = new Sky(this);
	private final Camera camera = new Camera(this);
	protected final Sun sun = new Sun(this);
	
	/**
 	 * World
 	 */
	private World loadedWorld;
	private int loadedDimension;

	/**
 	 * Octree origin
 	 */
	protected Vector3i origin = new Vector3i();
	
	/**
 	 * Octree
 	 */
	protected Octree octree;

	private double exposure = DEFAULT_EXPOSURE;
	protected boolean stillWater = false;
	private boolean biomeColors = true;
	protected boolean sunEnabled = true;
	protected boolean cloudsEnabled = false;
	protected boolean emittersEnabled = true;

	private Set<ChunkPosition> loadedChunks = new HashSet<ChunkPosition>();

	// chunk loading buffers
	private byte[] blocks = new byte[Chunk.X_MAX * Chunk.Y_MAX * Chunk.Z_MAX];
	private byte[] biomes = new byte[Chunk.X_MAX * Chunk.Z_MAX];
	private byte[] data = new byte[(Chunk.X_MAX * Chunk.Y_MAX * Chunk.Z_MAX) / 2];

	private boolean refresh = false;
	private boolean pathTrace = false;
	private boolean pauseRender = true;
	
	private Postprocess postprocess = Postprocess.GAMMA;
	
	/**
 	 * Preview frame interlacing counter.
 	 */
	public int previewCount;
	
	protected boolean clearWater = false;
	
	protected double emitterIntensity = DEFAULT_EMITTER_INTENSITY;
	protected boolean atmosphereEnabled = false;
	protected boolean volumetricFogEnabled = false;
	
	private boolean saveDumps = true;
	
	private int dumpFrequency = DEFAULT_DUMP_FREQUENCY;
	
	protected int waterHeight = 0;
	
	private WorldTexture grassTexture = new WorldTexture();
	private WorldTexture foliageTexture = new WorldTexture();

	/**
	 * Current SPP for the scene
	 */
	public int spp = 0;
	
	/**
	 * Target SPP for the scene
	 */
	private int sppTarget;
	
	/**
	 * Total rendering time in milliseconds.
	 */
	public long renderTime = 0;
	
	private BufferedImage buffer;
	
	private BufferedImage backBuffer;
	
	private double[][][] samples;
	
	private int[] bufferData;
	
	private int width;
	
	private int height;
	
	private boolean finalized = false;
	
	private boolean finalizeBuffer = false;
	
	static {
		rayDepth = ProgramProperties.getIntProperty("rayDepth", rayDepth);
	}
	
	/**
	 * Create an empty scene with default canvas width and height.
	 */
	public Scene() {
		octree = new Octree(1);// empty octree
		
        width = ProgramProperties.getIntProperty("3dcanvas.width",
        		RenderableCanvas.DEFAULT_WIDTH);
        height = ProgramProperties.getIntProperty("3dcanvas.height",
        		RenderableCanvas.DEFAULT_HEIGHT);
        
        sppTarget = ProgramProperties.getIntProperty("sppTargetDefault",
        		DEFAULT_SPP_TARGET);
		
		initBuffers();
		
		if (ProgramProperties.getProperty("skymap") != null)
			sky.loadSkyMap(ProgramProperties.getProperty("skymap"));
	}
	
	private synchronized void initBuffers() {
		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		bufferData = ((DataBufferInt) backBuffer.getRaster().getDataBuffer()).getData();
		samples = new double[width][height][3];
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
	public void set(Scene other) {
		loadedWorld = other.loadedWorld;

		// the octree reference is overwritten to save time
		// when the other scene is changed it must create a new octree
		octree = other.octree;
		grassTexture = other.grassTexture;
		foliageTexture = other.foliageTexture;
		origin.set(other.origin);
		
		loadedChunks = other.loadedChunks;

		exposure = other.exposure;
		name = other.name;
		skymapFileName = other.skymapFileName;
		
		stillWater = other.stillWater;
		clearWater = other.clearWater;
		biomeColors = other.biomeColors;
		sunEnabled = other.sunEnabled;
		cloudsEnabled = other.cloudsEnabled;
		cloudHeight = other.cloudHeight;
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
		
		pathTrace = other.pathTrace;
		pauseRender = other.pauseRender;
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
	
	private String getFrameName(String fileName) {
		return fileName.split("\\.")[0];
	}

	/**
	 * Save the scene description
	 * @param context 
	 * @param progressListener 
	 * @param fileName 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public synchronized void saveSceneDescription(
			RenderContext context,
			RenderStatusListener progressListener,
			String fileName)
		throws IOException, InterruptedException {
		
		String task = "Saving scene";
		progressListener.setProgress(task, 0, 0, 2);
		
		name = getFrameName(fileName);
		DataOutputStream out = new DataOutputStream(
				context.getSceneFileOutputStream(fileName));
		
		CompoundTag chunkView = new CompoundTag();
		chunkView.addItem(new NamedTag("version", new IntTag(CVF_VERSION)));
		
		CompoundTag canvas = new CompoundTag();
		CompoundTag worldTag = new CompoundTag();
		
		canvas.addItem("width", new IntTag(width));
		canvas.addItem("height", new IntTag(height));
		canvas.addItem("exposure", new DoubleTag(exposure));
		canvas.addItem("postprocess", new IntTag(postprocess.ordinal()));
		
		if (loadedWorld != null) {
			worldTag.addItem("worldDirectoryPath",
					new StringTag(loadedWorld.getWorldDirectory().getAbsolutePath()));
		}
		worldTag.addItem("dimension",
				new IntTag(loadedDimension));
		worldTag.addItem("emittersEnabled",
				new ByteTag(emittersEnabled ? 1 : 0));
		worldTag.addItem("sunEnabled",
				new ByteTag(sunEnabled ? 1 : 0));
		worldTag.addItem("cloudsEnabled",
				new ByteTag(cloudsEnabled ? 1 : 0));
		worldTag.addItem("cloudHeight", new IntTag(cloudHeight));
		worldTag.addItem("stillWater",
				new ByteTag(stillWater ? 1 : 0));
		worldTag.addItem("clearWater",
				new ByteTag(clearWater ? 1 : 0));
		worldTag.addItem("biomeColorsEnabled",
				new ByteTag(biomeColors ? 1 : 0));
		worldTag.addItem("atmosphereEnabled",
				new ByteTag(atmosphereEnabled ? 1 : 0));
		worldTag.addItem("volumetricFogEnabled",
				new ByteTag(volumetricFogEnabled ? 1 : 0));
		worldTag.addItem("pathTrace",
				new ByteTag(pathTrace ? 1 : 0));
		worldTag.addItem("saveDumps",
				new ByteTag(saveDumps ? 1 : 0));
		worldTag.addItem("emitterIntensity", new DoubleTag(emitterIntensity));
		worldTag.addItem("waterHeight", new IntTag(waterHeight));
		worldTag.addItem("dumpFrequency", new IntTag(dumpFrequency));
		worldTag.addItem("sppTarget", new IntTag(sppTarget));
		sky.save(worldTag);

		ListTag chunkList = new ListTag();
		chunkList.setType(Tag.TAG_LONG);
		for (ChunkPosition cp : loadedChunks) {
			chunkList.addItem(new LongTag(cp.getLong()));
		}
		
		chunkView.addItem("canvas", canvas);
		chunkView.addItem("camera", camera.store());
		chunkView.addItem("sun", sun.store());
		chunkView.addItem("world", worldTag);
		chunkView.addItem("chunkList", chunkList);
		
		NamedTag rootTag = new NamedTag("chunkView", chunkView);
		rootTag.write(out);
		out.close();
		
		saveOctree(context, progressListener);
		saveGrassTexture(context, progressListener);
		saveFoliageTexture(context, progressListener);
		
		saveDump(context, progressListener);
		
		progressListener.sceneSaved();
	}
	
	/**
	 * Load scene description
	 * @param context 
	 * @param renderListener
	 * @param fileName 
	 * @throws IOException
	 * @throws SceneLoadingError
	 * @throws InterruptedException
	 */
	public synchronized void loadSceneDescription(
			RenderContext context,
			RenderStatusListener renderListener,
			String fileName)
			throws IOException, SceneLoadingError, InterruptedException {
		
		DataInputStream in = new DataInputStream(context.getSceneFileInputStream(fileName));
		
		String cvf_ver = "chunkView.version";
		String cvf_canvas = "chunkView.canvas";
		String cvf_camera = "chunkView.camera";
		String cvf_world = "chunkView.world";
		String cvf_sun = "chunkView.sun";
		String cvf_chunkList = "chunkView.chunkList";
		
		Set<String> request = new HashSet<String>();
		request.add(cvf_ver);
		request.add(cvf_canvas);
		request.add(cvf_camera);
		request.add(cvf_world);
		request.add(cvf_sun);
		request.add(cvf_chunkList);
		Map<String, AnyTag> result = NamedTag.quickParse(in, request);
		in.close();
		
		if (!result.containsKey(cvf_ver) ||
				result.get(cvf_ver).isError() ||
				result.get(cvf_ver).intValue() != CVF_VERSION) {
			
			if (result.containsKey(cvf_ver) &&
					!result.get(cvf_ver).isError())
				throw new SceneLoadingError("Incorrect CVF version! " +
						"Expected " + CVF_VERSION + " but fund " +
						result.get(cvf_ver).intValue() + ".");
			else
				throw new SceneLoadingError("Could not read CVF version!");
		}
		
		name = getFrameName(fileName);
		
		if (result.containsKey(cvf_canvas)) {
			CompoundTag canvasTag = (CompoundTag) result.get(cvf_canvas);
			
			int width = canvasTag.get("width").intValue();
			int height = canvasTag.get("height").intValue();
			setCanvasSize(width, height);
			exposure = canvasTag.get("exposure").doubleValue(DEFAULT_EXPOSURE);
			postprocess = Postprocess.get(canvasTag.get("postprocess"));
		}
		
		if (result.containsKey(cvf_camera))  {
			
			camera.load((CompoundTag) result.get(cvf_camera));
		}
		
		if (result.containsKey(cvf_world))  {
			CompoundTag worldTag = (CompoundTag) result.get(cvf_world);
			
			String worldDirectoryPath = worldTag.get("worldDirectoryPath").stringValue();
			int dimension = worldTag.get("dimension").intValue();
			if (!worldDirectoryPath.isEmpty()) {
				File worldDirectory = new File(worldDirectoryPath);
				if (World.isWorldDir(worldDirectory)) {
					if (loadedWorld == null ||
							loadedWorld.getWorldDirectory() == null ||
							!loadedWorld.getWorldDirectory().getAbsolutePath().equals(worldDirectoryPath)) {
						
						loadedWorld = new World(worldDirectory, true);
						loadedWorld.setDimension(dimension);
						
					} else if (loadedWorld.currentDimension() != dimension) {
						
						loadedWorld.setDimension(dimension);
						
					}
				} else {
					logger.debug("Could not load world: " + worldDirectoryPath);
				}
			}
			emittersEnabled =
				worldTag.get("emittersEnabled").byteValue() != 0;
			sunEnabled = worldTag.get("sunEnabled").byteValue() != 0;
			cloudsEnabled = worldTag.get("cloudsEnabled").byteValue(0) != 0;
			cloudHeight = worldTag.get("cloudHeight").intValue(DEFAULT_CLOUD_HEIGHT);
			stillWater = worldTag.get("stillWater").byteValue() != 0;
			clearWater = worldTag.get("clearWater").byteValue() != 0;
			biomeColors =
				worldTag.get("biomeColorsEnabled").byteValue() != 0;
			atmosphereEnabled = worldTag.get("atmosphereEnabled").byteValue() != 0;
			volumetricFogEnabled = worldTag.get("volumetricFogEnabled").byteValue() != 0;
			pathTrace = worldTag.get("pathTrace").byteValue(1) != 0;
			saveDumps = worldTag.get("saveDumps").byteValue(1) != 0;
			emitterIntensity = worldTag.get("emitterIntensity").
					doubleValue(DEFAULT_EMITTER_INTENSITY);
			waterHeight = worldTag.get("waterHeight").intValue(0);
			dumpFrequency = worldTag.get("dumpFrequency").
					intValue(DEFAULT_DUMP_FREQUENCY);
			sppTarget = worldTag.get("sppTarget").intValue(
					ProgramProperties.getIntProperty("sppTargetDefault",
							DEFAULT_SPP_TARGET));
			sky.load(worldTag);
		}
		
		if (result.containsKey(cvf_sun))  {
			
			sun.load((CompoundTag) result.get(cvf_sun));
		}

		if (pathTrace)
			pauseRender = true;
		
		refresh = false;
		
		loadDump(context, renderListener);
		
		Collection<ChunkPosition> chunksToLoad =
				new LinkedList<ChunkPosition>();
		
		if (result.containsKey(cvf_chunkList)) {
			ListTag chunkList = (ListTag) result.get(cvf_chunkList);
			
				for (int i = 0; i < chunkList.getNumItem(); ++i) {
					chunksToLoad.add(ChunkPosition.get(
								chunkList.getItem(i).longValue()));
				}
		}
		
		if (loadOctree(context, renderListener)) {
			calculateOctreeOrigin(chunksToLoad);
			loadedChunks = new HashSet<ChunkPosition>(chunksToLoad);
			
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
				loadChunks(renderListener, loadedWorld, chunksToLoad);
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
	public void quickTrace(Ray ray, RayPool rayPool) {
		ray.x.x -= origin.x;
		ray.x.y -= origin.y;
		ray.x.z -= origin.z;
		
		RayTracer.quickTrace(this, ray, rayPool);
	}

	/**
	 * Path trace the ray in this scene
	 * @param ray
	 * @param pool
	 * @param vectorPool
	 * @param random
	 */
	public void pathTrace(Ray ray, RayPool pool,
			VectorPool vectorPool, Random random) {

		ray.x.x -= origin.x;
		ray.x.y -= origin.y;
		ray.x.z -= origin.z;
		
		PathTracer.pathTrace(this, ray, pool, vectorPool, random);
	}

	/**
	 * @param ray
	 */
	public static void getIntersectionColor(Ray ray) {

		if (ray.currentMaterial == 0) {
			ray.color.x = 1;
			ray.color.y = 1;
			ray.color.z = 1;
			ray.color.w = 0;
			return;
		}

		int x = QuickMath.floor(ray.x.x);
		int y = QuickMath.floor(ray.x.y);
		int z = QuickMath.floor(ray.x.z);
		ray.calcUVCoords(x, y, z);

		Block block = ray.getCurrentBlock();
		block.getTexture().getColor(ray.u, ray.v, ray.color);
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
			logger.warn("Can not reload chunks -- unknown world file!");
		}
		
		loadedWorld.setDimension(loadedDimension);
		loadedWorld.reload();
		loadChunks(progressListener, loadedWorld, loadedChunks);
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
		progressListener.setProgress(task, 0, 0, 1);
		
		loadedWorld = world;
		loadedDimension = world.currentDimension();
		
		int emitters = 0;
		int chunks = 0;
		
		if (chunksToLoad.isEmpty()) {
			return;
		}
		
		loadedChunks = new HashSet<ChunkPosition>();
		
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
		int done = 0;
		int target = chunksToLoad.size()-1;
		for (ChunkPosition cp : chunksToLoad) {
			
			progressListener.setProgress(task, done, 0, target);
			done += 1;
			
			if (loadedChunks.contains(cp))
				continue;

			loadedChunks.add(cp);

			world.getChunk(cp).getBlockData(blocks, data, biomes);
			chunks += 1;
			
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
									long pr = (wx * 3129871L) ^ (wz * 116129781L) ^ ((long) wy);
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
								int biomeId = 0xFF & biomeIdMap.get(wx, wz);
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
		
		camera.setWorldSize(1<<octree.depth);
		
		logger.info(String.format("Loaded %d chunks (%d emitters)",
				chunks, emitters));
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
		return loadedChunks;
	}
	
	/**
	 * @return <code>true</code> if the scene has loaded chunks
	 */
	public synchronized boolean haveLoadedChunks() {
		return !loadedChunks.isEmpty();
	}

	/**
	 * Calculate a camera position centered above all loaded chunks.
	 * @return The calculated camera position
	 */
	public Vector3d calcCenterCamera() {
		if (loadedChunks.isEmpty())
			return new Vector3d(0, 128, 0);
		
		int xmin = Integer.MAX_VALUE;
		int xmax = Integer.MIN_VALUE;
		int zmin = Integer.MAX_VALUE;
		int zmax = Integer.MIN_VALUE;
		for (ChunkPosition cp: loadedChunks) {
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
	 * @return The camera object
	 */
	public Camera camera() {
		return camera;
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
	 * @return The sky object
	 */
	public Sky sky() {
		return sky;
	}

	/**
	 * @return The sun object
	 */
	public Sun sun() {
		return sun;
	}
	
	/**
	 * Toggle Monte Carlo path tracing.
	 */
	public synchronized void toggleMonteCarlo() {
		pathTrace = !pathTrace;
		refresh();
	}
	
	/**
	 * @throws InterruptedException
	 */
	public synchronized void waitOnRefreshRequest() throws InterruptedException {
		while ((!pathTrace || pauseRender) && !refresh)
			wait();
		refresh = false;
	}
	
	/**
	 * @return <code>true</code> if the rendering of this scene should be
	 * restarted
	 */
	public boolean shouldRefresh() {
		return refresh;
	}
	
	/**
	 * Called when the scene description has been altered in a way that
	 * forces the rendering to restart.
	 */
	@Override
	public synchronized void refresh() {
		refresh = true;
		pauseRender = false;
		spp = 0;
		renderTime = 0;
		notifyAll();
	}

	/**
	 * Wait while the rendering is paused
	 * @throws InterruptedException
	 */
	public synchronized void pauseWait() throws InterruptedException {
		while (pauseRender)
			wait();
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
			ProgramProperties.setProperty("rayDepth", "" + value);
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
	public void setRefreshed() {
		refresh = false;
	}

	/**
	 * Trace a ray in the Octree
	 * @param ray
	 */
	public void trace(Ray ray) {
		ray.d.set(0, -1, 0);
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
			camera.setInfDof(true);
		} else {
			camera.setFocalOffset(ray.distance);
			camera.setDof(ray.distance);
			camera.setInfDof(false);
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
			clearWater  = value;
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
	 * @param text
	 */
	public void setName(String text) {
		if (text.length() > 0)
			name = text;
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
		emitterIntensity  = value;
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
	 * @param value the dumpFrequency to set
	 */
	public void setDumpFrequency(int value) {
		value = Math.max(1, value);
		if (value != dumpFrequency) {
			dumpFrequency = value;
		}
	}

	/**
	 * @return the saveDumps
	 */
	public boolean saveDumps() {
		return saveDumps;
	}

	/**
	 * @param saveDumps the saveDumps to set
	 */
	public void setSaveDumps(boolean saveDumps) {
		this.saveDumps = saveDumps;
	}

	/**
	 * @param other
	 */
	public void copyTransients(Scene other) {
		postprocess = other.postprocess;
		exposure = other.exposure;
		saveDumps = other.saveDumps;
		dumpFrequency = other.dumpFrequency;
		sppTarget = other.sppTarget;
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
	 * @param watermark 
	 */
	public void saveSnapshot(File directory, boolean watermark) {
		
		try {
			String fileName = name + "-" + spp + ".png";
			logger.info("Saving frame " + fileName);
			if (watermark)
				addWatermark();
			ImageIO.write(buffer, "png", new File(directory, fileName));
			logger.info("Frame saved");
		} catch (IOException e) {
			logger.warn("Failed to save current frame. Reason: " +
				e.getMessage(), e);
		}
	}
	
	/**
	 * @param targetFile
	 * @param watermark 
	 * @param progressListener 
	 * @throws IOException 
	 */
	public synchronized void saveFrame(File targetFile, boolean watermark,
			ProgressListener progressListener) throws IOException {
		
		for (int x = 0; x < width; ++x) {
			progressListener.setProgress("Finalizing frame", x, 0, width-1);
			for (int y = 0; y < height; ++y) {
				finalizePixel(x, y);
			}
		}
		
		if (watermark)
			addWatermark();
		ImageIO.write(backBuffer, "png", targetFile);
	}

	/**
	 * Add a watermark to the image buffer.
	 */
	private void addWatermark() {
		Graphics g = buffer.getGraphics();
		BufferedImage watermark = RenderManager.watermark;
		g.drawImage(watermark, buffer.getWidth() - watermark.getWidth(),
				buffer.getHeight() - watermark.getHeight(), null);
		g.dispose();
	}

	private synchronized void saveOctree(
			RenderContext context,
			ProgressListener progressListener) {
		
		String fileName = name + ".octree";
		DataOutputStream out = null;
		try {
			String task = "Saving octree";
			progressListener.setProgress(task, 0, 0, 1);
			logger.info("Saving octree " + fileName);
			out = new DataOutputStream(new GZIPOutputStream(
					context.getSceneFileOutputStream(fileName)));
			
			octree.store(out);
			
			progressListener.setProgress(task, 1, 0, 1);
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
			progressListener.setProgress(task, 0, 0, 1);
			logger.info("Saving grass texture " + fileName);
			out = new DataOutputStream(new GZIPOutputStream(
					context.getSceneFileOutputStream(fileName)));
			
			grassTexture.store(out);
			
			progressListener.setProgress(task, 1, 0, 1);
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
			progressListener.setProgress(task, 0, 0, 1);
			logger.info("Saving foliage texture " + fileName);
			out = new DataOutputStream(new GZIPOutputStream(
					context.getSceneFileOutputStream(fileName)));
			
			foliageTexture.store(out);
			
			progressListener.setProgress(task, 1, 0, 1);
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
			progressListener.setProgress(task, 0, 0, 1);
			logger.info("Saving render dump " + fileName);
			out = new DataOutputStream(new GZIPOutputStream(
					context.getSceneFileOutputStream(fileName)));
			out.writeInt(width);
			out.writeInt(height);
			out.writeInt(spp);
			out.writeLong(renderTime);
			for (int x = 0; x < width; ++x) {
				progressListener.setProgress(task, x, 0, width-1);
				for (int y = 0; y < height; ++y) {
					out.writeDouble(samples[x][y][0]);
					out.writeDouble(samples[x][y][1]);
					out.writeDouble(samples[x][y][2]);
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
			renderListener.setProgress(task, 0, 0, 1);
			logger.info("Loading octree " + fileName);
			in = new DataInputStream(new GZIPInputStream(
					context.getSceneFileInputStream(fileName)));
			
			octree = Octree.load(in);
			
			renderListener.setProgress(task, 1, 0, 1);
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
			renderListener.setProgress(task, 0, 0, 1);
			logger.info("Loading grass texture " + fileName);
			in = new DataInputStream(new GZIPInputStream(
					context.getSceneFileInputStream(fileName)));
			
			grassTexture = WorldTexture.load(in);
			
			renderListener.setProgress(task, 1, 0, 1);
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
			renderListener.setProgress(task, 0, 0, 1);
			logger.info("Loading foliage texture " + fileName);
			in = new DataInputStream(new GZIPInputStream(
					context.getSceneFileInputStream(fileName)));
			
			foliageTexture = WorldTexture.load(in);
			
			renderListener.setProgress(task, 1, 0, 1);
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

	private synchronized void loadDump(
			RenderContext context,
			RenderStatusListener renderListener) {
		
		String fileName = name + ".dump";
		
		DataInputStream in = null;
		try {
			in = new DataInputStream(new GZIPInputStream(
					context.getSceneFileInputStream(fileName)));
			
			String task = "Loading render dump";
			renderListener.setProgress(task, 0, 0, 1);
			logger.info("Loading render dump " + fileName);
			int dumpWidth = in.readInt();
			int dumpHeight= in.readInt();
			if (dumpWidth != width || dumpHeight != height) {
				logger.warn("Render dump discarded: incorrect widht or height!");
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
				renderListener.setProgress(task, x, 0, width-1);
				for (int y = 0; y < height; ++y) {
					samples[x][y][0] = in.readDouble();
					samples[x][y][1] = in.readDouble();
					samples[x][y][2] = in.readDouble();
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
		
		double r = samples[x][y][0];
		double g = samples[x][y][1];
		double b = samples[x][y][2];
		
		r *= exposure;
		g *= exposure;
		b *= exposure;
		
		if (pathTrace()) {
			switch (postprocess) {
			case NONE:
				break;
			case TONEMAP1:
				// http://filmicgames.com/archives/75
				r = Math.max(0, r-0.004);
				r = (r*(6.2*r + .5)) / (r * (6.2*r + 1.7) + 0.06);
				g = Math.max(0, g-0.004);
				g = (g*(6.2*g + .5)) / (g * (6.2*g + 1.7) + 0.06);
				b = Math.max(0, b-0.004);
				b = (b*(6.2*b + .5)) / (b * (6.2*b + 1.7) + 0.06);
				break;
			case GAMMA:
				r = Math.pow(r, 1/DEFAULT_GAMMA);
				g = Math.pow(g, 1/DEFAULT_GAMMA);
				b = Math.pow(b, 1/DEFAULT_GAMMA);
				break;
			}
		} else {
			r = Math.sqrt(r);
			g = Math.sqrt(g);
			b = Math.sqrt(b);
		}
		
		r = Math.min(1, r);
		g = Math.min(1, g);
		b = Math.min(1, b);
		
		bufferData[x + y * width] = Color.getRGB(r, g, b);
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
						g.drawString(String.format("target: %.2f m", ray.distance), 5, height-18);
						g.drawString(String.format("[0x%08X] %s (%s)",
								ray.currentMaterial,
								ray.getCurrentBlock(),
								ray.getBlockExtraInfo()), 5, height-5);
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
	public double[][][] getSampleBuffer() {
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
	 * Refresh without resetting SPP count.
	 */
	public void softRefresh() {
		refresh = true;
	}

	/**
	 * @param x X coordinate in octree space
	 * @param z Z coordinate in octree space
	 * @return Foliage color for the given coordinates
	 */
	public float[] getFoliageColor(int x, int z) {
		if (biomeColors)
			return foliageTexture.get(x, z);
		else
			return Biomes.getFoliageColorLinear(0);
	}

	/**
	 * @param x X coordinate in octree space
	 * @param z Z coordinate in octree space
	 * @return Grass color for the given coordinates
	 */
	public float[] getGrassColor(int x, int z) {
		if (biomeColors)
			return grassTexture.get(x, z);
		else
			return Biomes.getGrassColorLinear(0);
	}

	/**
	 * @return <code>true</code> if cloud rendering is enabled
	 */
	public boolean cloudsEnabled() {
		return cloudsEnabled;
	}

	/**
	 * Enable/disable clouds rendering
	 * @param value
	 */
	public void setCloudsEnabled(boolean value) {
		if (value != cloudsEnabled) {
			cloudsEnabled = value;
			refresh();
		}
	}
	
	/**
	 * Change the cloud height
	 * @param value
	 */
	public void setCloudHeight(int value) {
		if (value != cloudHeight) {
			cloudHeight = value;
			if (cloudsEnabled)
				refresh();
		}
	}

	/**
	 * @return The current cloud height
	 */
	public int getCloudHeight() {
		return cloudHeight;
	}

}
