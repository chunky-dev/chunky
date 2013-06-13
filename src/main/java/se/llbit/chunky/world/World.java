/* Copyright (c) 2010-2013 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.world;
import org.apache.commons.math3.util.FastMath;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import se.llbit.chunky.ui.ProgressPanel;
import se.llbit.chunky.world.listeners.ChunkDeletionListener;
import se.llbit.chunky.world.listeners.ChunkDiscoveryListener;
import se.llbit.chunky.world.listeners.ChunkUpdateListener;
import se.llbit.chunky.world.listeners.RegionDiscoveryListener;
import se.llbit.chunky.world.storage.RegionFile;
import se.llbit.chunky.world.storage.RegionFileCache;
import se.llbit.nbt.AnyTag;
import se.llbit.nbt.NamedTag;
import se.llbit.util.Pair;

/**
 * The World class contains information about the currently viewed world.
 * It has a map of all chunks in the world and is responsible for parsing
 * chunks when needed. All rendering is done through the WorldRenderer class.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class World implements Comparable<World> {

	private static final Logger logger =
			Logger.getLogger(World.class);

	/**
	 * The currently supported NBT version of level.dat files.
	 */
	public static final int NBT_VERSION = 19133;

	/**
	 * Overworld dimension index
	 */
	public static final int OVERWORLD_DIMENSION = 0;

	/**
	 * Nether dimension index
	 */
	public static final int NETHER_DIMENSION = -1;

	/**
	 * End dimension index
	 */
	public static final int END_DIMENSION = 1;

	/**
	 * Default sea level. Used to be 64.
	 */
    public static final int SEA_LEVEL = 63;
	private static final int DEFAULT_LAYER = SEA_LEVEL;

	@SuppressWarnings("unused")
	private static final int PIXELS_PER_IDAT_CHUNK = 10000;

	private Map<ChunkPosition, Region> regionMap = new HashMap<ChunkPosition, Region>();

	private int currentLayer = DEFAULT_LAYER;
	private File worldDirectory = null;
	private boolean havePlayerPos = false;
	private boolean haveSpawnPos = false;
	private double playerX;
	private double playerY;
	private double playerZ;
	private double playerYaw;
	private double playerPitch;
	private int playerDimension = 0;
	private int dimension;

	private Heightmap heightmap = new Heightmap();

	private String levelName = "unknown";

	private Collection<ChunkDeletionListener> chunkDeletionListeners =
			new LinkedList<ChunkDeletionListener>();
	private Collection<ChunkUpdateListener> chunkUpdateListeners =
			new LinkedList<ChunkUpdateListener>();
	private Collection<ChunkDiscoveryListener> chunkDiscoveryListeners =
			new LinkedList<ChunkDiscoveryListener>();
	private Collection<RegionDiscoveryListener> regionDiscoveryListeners =
			new LinkedList<RegionDiscoveryListener>();
    private int spawnX;
    private int spawnY;
    private int spawnZ;

	private int gameMode = 0;

	/**
	 * Create new world.
	 * @param worldDir
	 * @param logWarnings
	 */
	public World(File worldDir, boolean logWarnings) {
		this.worldDirectory = worldDir;
		this.levelName = worldDir.getName();
		loadAdditionalData(logWarnings);
	}

	protected World() {
	}

	/**
	 * Add a chunk deletion listener
	 * @param listener
	 */
	public void addChunkDeletionListener(ChunkDeletionListener listener) {
		synchronized (chunkDeletionListeners) {
			chunkDeletionListeners.add(listener);
		}
	}

	/**
	 * Add a chunk update listener
	 * @param listener
	 */
    public void addChunkUpdateListener(ChunkUpdateListener listener) {
    	synchronized (chunkUpdateListeners) {
    		chunkUpdateListeners.add(listener);
    	}
    }

    /**
     * Add a region discovery listener
     * @param listener
     */
    public void addRegionDiscoveryListener(RegionDiscoveryListener listener) {
    	synchronized (regionDiscoveryListeners) {
    		regionDiscoveryListeners.add(listener);
    	}
    }

    private void fireChunkDeleted(ChunkPosition chunk) {
    	synchronized (chunkDeletionListeners) {
	        for (ChunkDeletionListener listener : chunkDeletionListeners)
	            listener.chunkDeleted(chunk);
    	}
    }

    /**
     * Notify the chunk update listeners that a chunk has been updated.
     * @param chunk the updated chunk
     */
    private void fireChunkUpdated(Chunk chunk) {
    	Collection<Chunk> chunks = new LinkedList<Chunk>();
        chunks.add(chunk);
    	fireChunksUpdated(chunks);
    }

    /**
     * Set current dimension
     * @param dimension
     */
	public synchronized void setDimension(int dimension) {
		this.dimension = dimension;
	}

	/**
	 * Parse player location and level name.
	 */
	private void loadAdditionalData(boolean logWarnings) {
		havePlayerPos = false;
		haveSpawnPos = false;
		try {
			DataInputStream in = new DataInputStream(new GZIPInputStream(
				new FileInputStream(new File(worldDirectory, "level.dat")))); //$NON-NLS-1$
			Set<String> request = new HashSet<String>();
			request.add(".Data.version");
			request.add(".Data.Player.Dimension");
			request.add(".Data.Player.Pos.0");
			request.add(".Data.Player.Pos.1");
			request.add(".Data.Player.Pos.2");
			request.add(".Data.Player.Rotation.0");
			request.add(".Data.Player.Rotation.1");
			request.add(".Data.Player.SpawnX");
            request.add(".Data.Player.SpawnY");
            request.add(".Data.Player.SpawnZ");
			request.add(".Data.LevelName");
			request.add(".Data.GameType");
			Map<String, AnyTag> result = NamedTag.quickParse(in, request);

			AnyTag dim = result.get(".Data.Player.Dimension");
			playerDimension = dim.intValue();

			AnyTag version = result.get(".Data.version");
			if (logWarnings && version.intValue() != NBT_VERSION) {
				logger.warn("The world format for the world " + levelName +
						" is not supported by Chunky.\n" +
						"Will attempt to load the world anyway.");
			}
			AnyTag posX = result.get(".Data.Player.Pos.0");
			AnyTag posY = result.get(".Data.Player.Pos.1");
			AnyTag posZ = result.get(".Data.Player.Pos.2");
			AnyTag yaw = result.get(".Data.Player.Rotation.0");
			AnyTag pitch = result.get(".Data.Player.Rotation.1");
			AnyTag spawnX = result.get(".Data.Player.SpawnX");
            AnyTag spawnY = result.get(".Data.Player.SpawnY");
            AnyTag spawnZ = result.get(".Data.Player.SpawnZ");
            AnyTag gameType = result.get(".Data.GameType");

            gameMode  = gameType.intValue(0);

			playerX = posX.doubleValue();
			playerY = posY.doubleValue();
			playerZ = posZ.doubleValue();
			playerYaw = yaw.floatValue();
			playerPitch = pitch.floatValue();
			this.spawnX = spawnX.intValue();
            this.spawnY = spawnY.intValue();
            this.spawnZ = spawnZ.intValue();
			havePlayerPos = ! (posX.isError() || posY.isError() || posZ.isError());
			haveSpawnPos = ! (spawnX.isError() || spawnY.isError() || spawnZ.isError());
			if (havePlayerPos())
			    currentLayer = playerLocY();

			levelName = result.get(".Data.LevelName").stringValue(levelName);

			in.close();

		} catch (FileNotFoundException e) {
			if (logWarnings)
				logger.warn("Could not find level.dat file for the world " + levelName + "!");
		} catch (IOException e) {
			if (logWarnings)
				logger.warn("Could not read the level.dat file for the world " + levelName + "!");
		}
	}

	/**
	 * @param pos
	 * @return The chunk at the given position
	 */
	public synchronized Chunk getChunk(ChunkPosition pos) {
		return getRegion(pos.getRegionPosition()).getChunk(pos);
	}

	/**
	 * @param pos Region position
	 * @return The region at the given position
	 */
	public synchronized Region getRegion(ChunkPosition pos) {
		if (regionMap.containsKey(pos)) {
			return regionMap.get(pos);
		} else {
			// check if the region is present in the world directory
			Region region = EmptyRegion.instance;
			if (regionExists(pos))
				region = new Region(pos, this);
			regionMap.put(pos, region);
			return region;
		}
	}

	private boolean regionExists(ChunkPosition pos) {
		File regionFile = new File(getRegionDirectory(), Region.getFileName(pos));
		return regionFile.exists();
	}

	/**
	 * Set the current layer
	 * @param layer
	 */
	public synchronized void setCurrentLayer(int layer) {
		if (layer != currentLayer) {
			currentLayer = layer;
		}
	}

	/**
	 * @return The current layer
	 */
	public synchronized int currentLayer() {
		return currentLayer;
	}

	/**
	 * Get the data directory for the given dimension
	 * @param dimension the dimension
	 * @return File object pointing to the data directory
	 */
	public synchronized File getDataDirectory(int dimension) {
		return dimension == 0 ? worldDirectory : new File(worldDirectory, "DIM"+dimension); //$NON-NLS-1$
	}

	/**
	 * Get the data directory for the current dimension
	 * @return File object pointing to the data directory
	 */
	public synchronized File getDataDirectory() {
        return getDataDirectory(dimension);
    }

	/**
	 * @return File object pointing to the region file directory
	 */
	public synchronized File getRegionDirectory() {
		return new File(getDataDirectory(), "region");
	}

	/**
	 * @param dimension
	 * @return File object pointing to the region file directory for
	 * the given dimension
	 */
	public synchronized File getRegionDirectory(int dimension) {
		return new File(getDataDirectory(dimension), "region");
	}

	/**
	 * @return <code>true</code> if there is player position information
	 */
	public synchronized boolean havePlayerPos() {
		return havePlayerPos && playerDimension == dimension;
	}

	/**
	 * @return <code>true</code> if there is spawn position information
	 */
    public synchronized boolean haveSpawnPos() {
        return haveSpawnPos && playerDimension == 0;
    }

    /**
     * @return The current dimension
     */
	public synchronized int currentDimension() {
		return dimension;
	}

	/**
	 * @return Player X position
	 */
	public synchronized double playerPosX() {
		return playerX;
	}

	/**
	 * @return Player Y position
	 */
	public synchronized double playerPosY() {
		return playerY;
	}

	/**
	 * @return Player Z position
	 */
	public synchronized double playerPosZ() {
		return playerZ;
	}

	/**
	 * @return Player view yaw
	 */
	public synchronized double playerYaw() {
		return playerYaw;
	}

	/**
	 * @return Player view pitch
	 */
	public synchronized double playerPitch() {
		return playerPitch;
	}

	/**
	 * @return Player Y location
	 */
	public synchronized int playerLocY() {
		if (havePlayerPos())
			return (int) (playerY - 0.5);
		return -1;
	}

	/**
	 * @return The chunk heightmap
	 */
	public Heightmap heightmap() {
		return heightmap;
	}

	/**
	 * @return The world director
	 */
	public File getWorldDirectory() {
		return worldDirectory;
	}

	/**
	 * Called when a new region has been discovered by the region parser
	 * @param pos
	 */
	public void regionDiscovered(ChunkPosition pos) {
		// set to non-null if this is a new region!
		Region region = null;
		synchronized (this) {
			if (!regionMap.containsKey(pos)) {
				region = new Region(pos, this);
				regionMap.put(pos, region);
			}
		}

		if (region != null)
			fireRegionDiscovered(region);
	}

	/**
	 * Notify region discovery listeners
	 * @param region
	 */
	private void fireRegionDiscovered(Region region) {
		synchronized (regionDiscoveryListeners) {
			for (RegionDiscoveryListener listener: regionDiscoveryListeners) {
				listener.regionDiscovered(region);
			}
		}
	}

	/**
	 * Clear the region map and remove all listeners.
	 */
	public synchronized void dispose() {
    	regionMap.clear();

    	synchronized (chunkUpdateListeners) {
    		chunkUpdateListeners.clear();
    	}
    	synchronized (chunkDeletionListeners) {
    		chunkDeletionListeners.clear();
    	}
    	synchronized (chunkDiscoveryListeners) {
    		chunkDiscoveryListeners.clear();
    	}
    	synchronized (regionDiscoveryListeners) {
    		regionDiscoveryListeners.clear();
    	}
	}

	/**
	 * Export the given chunks to a Zip archive.
	 * The Zip arhive is written without compression since the chunks are
	 * already compressed with GZip.
	 *
	 * @param target
	 * @param chunks
	 * @param dimension
	 * @param progress
	 * @throws IOException
	 */
	public synchronized void exportChunksToZip(File target,
	        Collection<ChunkPosition> chunks,
	        int dimension,
	        ProgressPanel progress) throws IOException {

	    Map<ChunkPosition, Set<ChunkPosition>> regionMap =
	            new HashMap<ChunkPosition, Set<ChunkPosition>>();

	    for (ChunkPosition chunk : chunks) {

	        ChunkPosition regionPosition = chunk.regionPosition();
	        Set<ChunkPosition> chunkSet = regionMap.get(regionPosition);
	        if (chunkSet == null) {
	            chunkSet = new HashSet<ChunkPosition>();
	            regionMap.put(regionPosition, chunkSet);
	        }
	        chunkSet.add(ChunkPosition.get(chunk.x & 31, chunk.z & 31));
	    }

	    int work = 0;
        progress.setJobSize(regionMap.size()+1);

        ZipOutputStream zout = null;

        String regionDirectory = dimension == 0 ? worldDirectory.getName()
                : worldDirectory.getName() + "/DIM" + dimension;
        regionDirectory += "/region";

        try {
            zout = new ZipOutputStream(new FileOutputStream(target));
            writeLevelDatToZip(zout);
            progress.setProgress(++work);

    	    for (Map.Entry<ChunkPosition, Set<ChunkPosition>> entry : regionMap.entrySet()) {

    	    	if (progress.isInterrupted())
    	    		break;

    	        ChunkPosition region = entry.getKey();

    	        RegionFile regionFile = RegionFileCache.getRegionFile(
    	                getRegionDirectory(dimension),
                        region.x << 5, region.z << 5);
    	        appendRegionToZip(zout, regionFile,
    	                regionDirectory + "/" + region.getMcaName(),
    	                entry.getValue());

                progress.setProgress(++work);
    	    }

    	} finally {
    	    try {
        	    if (zout != null)
        	        zout.close();
    	    } catch (IOException e) {
    	    }
    	}
	}

    /**
	 * Export the world to a zip file. The chunks which are included
	 * depends on the selected chunks. If any chunks are selected, then
	 * only those chunks are exported. If no chunks are selected then all
	 * chunks are exported.
	 *
	 * @param target
	 * @param progress
	 * @throws IOException
	 */
	public synchronized void exportWorldToZip(File target, ProgressPanel progress) throws IOException {

	    System.out.println("exporting all dimensions to " + target.getName());

	    final Collection<Pair<File, ChunkPosition>> regions =
                new LinkedList<Pair<File, ChunkPosition>>();
	    regions.clear();

	    WorldScanner.Operator operator = new WorldScanner.Operator() {
            @Override
            public void foundRegion(File regionDirectory, int x, int z) {
                regions.add(new Pair<File, ChunkPosition>(
                        regionDirectory, ChunkPosition.get(x, z)));
            }
        };
        // TODO make this more dynamic
        File overworld = getRegionDirectory(OVERWORLD_DIMENSION);
	    WorldScanner.findExistingChunks(overworld, operator);
	    WorldScanner.findExistingChunks(getRegionDirectory(NETHER_DIMENSION), operator);
	    WorldScanner.findExistingChunks(getRegionDirectory(END_DIMENSION), operator);

        int work = 0;
        progress.setJobSize(regions.size()+1);

        ZipOutputStream zout = null;
        try {
            zout = new ZipOutputStream(new FileOutputStream(target));
            writeLevelDatToZip(zout);
            progress.setProgress(++work);

            for (Pair<File, ChunkPosition> region : regions) {

            	if (progress.isInterrupted())
            		break;

                String regionDirectory = (region.thing1 == overworld) ?
                        worldDirectory.getName() :
                        worldDirectory.getName() + "/" + region.thing1.getParentFile().getName();
                regionDirectory += "/region";
                RegionFile regionFile = RegionFileCache.getRegionFile(
                        region.thing1,
                        region.thing2.x * 32, region.thing2.z * 32);
                appendRegionToZip(zout, regionFile,
                        regionDirectory + "/" + region.thing2.getMcaName(),
                        null);

                progress.setProgress(++work);
            }

        } finally {
            try {
                if (zout != null)
                    zout.close();
            } catch (IOException e) {
            }
        }
	}

	/**
	 * Write this worlds level.dat file to a ZipOutputStream.
	 * @param zout
	 * @throws IOException
	 */
	private void writeLevelDatToZip(ZipOutputStream zout) throws IOException {
	    File levelDat = new File(worldDirectory, "level.dat");
        FileInputStream in = new FileInputStream(levelDat);
        zout.putNextEntry(new ZipEntry(worldDirectory.getName()+"/"+"level.dat"));
        byte[] buf = new byte[4096];
        int len;
        while ((len = in.read(buf)) > 0) {
            zout.write(buf, 0, len);
        }
        zout.closeEntry();
        in.close();
    }

	private void appendRegionToZip(ZipOutputStream zout, RegionFile regionFile,
	        String regionZipFileName, Set<ChunkPosition> chunks) throws IOException {

	    zout.putNextEntry(new ZipEntry(regionZipFileName));
        regionFile.writeRegion(new DataOutputStream(zout), chunks);
        zout.closeEntry();
    }

	/**
	 * @return <code>true</code> if this is an empty or non-existent world
	 */
	public boolean isEmptyWorld() {
	    return false;
	}

	@Override
	public String toString() {
	    return levelName  + " (" + worldDirectory.getName() + ")";
	}

	/**
	 * @return The name of the world, not the actual world directory
	 */
	public String levelName() {
	    return levelName;
	}

	/**
	 * Called by the chunk parser when a new chunk has been discovered
	 * @param chunk
	 */
	public void chunkUpdated(Chunk chunk) {
		fireChunkUpdated(chunk);
	}

	/**
	 * @param i
	 * @return <code>true</code> if a data directory exists for the
	 * given dimension
	 */
	public synchronized boolean haveDimension(int i) {
		File dir = getDataDirectory(i);
		return dir.exists() && dir.isDirectory();
	}

    /**
     * @return The spawn Z position
     */
    public double spawnPosZ() {
        return spawnZ;
    }

    /**
     * @return The spawn Y position
     */
    public double spawnPosY() {
        return spawnY;
    }

    /**
     * @return The spawn X position
     */
    public double spawnPosX() {
        return spawnX;
    }

	/*public synchronized void renderPng(File targetFile, ProgressPanel progress) throws InterruptedException {

		progress.setJobName("Refreshing Chunks");

	    Set<ChunkPosition> needRefresh = new HashSet<ChunkPosition>();

		int x0 = Integer.MAX_VALUE;// bottom chunk
		int x1 = Integer.MIN_VALUE;// top chunk
		int z0 = Integer.MAX_VALUE;// right chunk
		int z1 = Integer.MIN_VALUE;// left chunk
		for (Map.Entry<ChunkPosition, Chunk> entry : chunkMap.entrySet()) {
		    ChunkPosition pos = entry.getKey();

			if (pos.x < x0)
				x0 = pos.x;
			if (pos.x > x1)
				x1 = pos.x;
			if (pos.z < z0)
				z0 = pos.z;
			if (pos.z > z1)
				z1 = pos.z;

			if (!entry.getValue().haveTopography()) {
			    needRefresh.add(pos.regionPosition());
			}
		}
		int width = (z1-z0+1) * 16;
		int height = (x1-x0+1) * 16;

		int numRefresh = 0;
		for (ChunkPosition region : needRefresh) {
		    for (int x = 0; x < 32; ++x) {
		        for (int z = 0; z < 32; ++z) {
		        	ChunkPosition pos = ChunkPosition.get(
		        			region.x * 32 + x, region.z * 32 + z);
		            Chunk chunk = chunkMap.get(pos);
		            if (chunk != null && !chunk.haveTopography()) {
		                parseQueue.add(chunk);
		                numRefresh++;
		            }
		        }
		    }
		}

		progress.setJobSize(numRefresh);

		while (!parseQueue.isEmpty() || !topoQueue.isEmpty()) {
		    if (progress.isInterrupted()) {
		        parseQueue.clear();
		        topoQueue.clear();
		        return;
		    }
		    progress.setProgress(numRefresh - parseQueue.size());
		    wait();
		}
		progress.finishJob();
		progress.setJobName("Rendering PNG");
		progress.setJobSize(height);

		try {
			PngFileWriter pngWriter = new PngFileWriter(targetFile);
			pngWriter.writeChunk(new IHDR(width, height));

			IDAT idat = new IDAT();
			IDATOutputStream idatOut = idat.getIDATOutputStream();

			int pixels = 0;

			render_loop:
			for (int x = x0; x <= x1; ++x) {
				for (int line = 0; line < 16; ++line) {
					progress.setProgress((x-x0)*16 + line);
					idatOut.write(IDAT.FILTER_TYPE_NONE);

					for (int z = z1; z >= z0; --z) {
					    if (progress.isInterrupted())
					        break render_loop;


					    if (pixels >= PIXELS_PER_IDAT_CHUNK) {
	                        pixels = 0;
	                        idatOut.finishChunk();
	                        pngWriter.writeChunk(idat);
	                        idatOut.reset();
	                    }

						Chunk chunk = getChunk(ChunkPosition.get(x, z));
						chunk.writePngLine(line, idatOut);

						pixels += 1;
					}
				}
			}

			if (pixels > 0) {
				idatOut.close();
				pngWriter.writeChunk(idat);
			}

			pngWriter.writeChunk(new IEND());
			pngWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}*/

	/**
	 * @return The number of regions currently in the region map
	 */
	public synchronized int numRegions() {
		return regionMap.size();
	}

	/**
	 * @param worldDir
	 * @return <code>true</code> if the given directory exists and
	 * contains a level.dat file
	 */
	public static boolean isWorldDir(File worldDir) {
		if (worldDir.isDirectory()) {
			File levelDat = new File(worldDir, "level.dat");
			return levelDat.exists() && levelDat.isFile();
		}
		return false;
	}

	/**
	 * Add a chunk discovery listener
	 * @param listener
	 */
	public void addChunkDiscoveryListener(ChunkDiscoveryListener listener) {
		synchronized (chunkDiscoveryListeners) {
			chunkDiscoveryListeners.add(listener);
		}
	}

	/**
	 * Notify listeners that chunks have been discovered
	 * @param chunks
	 */
	public void chunksDiscovered(Collection<Chunk> chunks) {
		fireChunksDiscovered(chunks);
		fireChunksUpdated(chunks);
	}

	/**
	 * Notify chunk update listeners
	 * @param chunks
	 */
	private void fireChunksUpdated(Collection<Chunk> chunks) {
		Collection<ChunkPosition> cplist = new LinkedList<ChunkPosition>();
		for (Chunk chunk: chunks)
			cplist.add(chunk.getPosition());

		synchronized (chunkUpdateListeners) {
	        for (ChunkUpdateListener listener : chunkUpdateListeners)
	            listener.chunksUpdated(cplist);
    	}
	}

	/**
	 * Notify chunk discovery listeners
	 * @param chunks
	 */
	private void fireChunksDiscovered(Collection<Chunk> chunks) {
		synchronized (chunkDiscoveryListeners) {
			for (ChunkDiscoveryListener listener: chunkDiscoveryListeners) {
				listener.chunksDiscovered(chunks);
			}
		}
	}

	/**
	 * Called when chunks have been deleted from this world.
	 * Triggers the chunk deletion listeners.
	 * @param pos Position of deleted chunk
	 */
	public void chunkDeleted(ChunkPosition pos) {
		fireChunkDeleted(pos);
	}

	/**
	 * Clear the chunk map and reload the additional data.
	 */
	public void reload() {
		regionMap.clear();
		loadAdditionalData(true);
	}

	/**
	 * @return String describing the game-mode of this world
	 */
	public String gameMode() {
		switch (gameMode) {
		case 0:
			return "Survival";
		case 1:
			return "Creative";
		case 2:
			return "Adventure";
		default:
			return "Unknown";
		}
	}

	@Override
	public int compareTo(World o) {
		// just compare the world names
		return toString().compareToIgnoreCase(o.toString());
	}
}
