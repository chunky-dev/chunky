/* Copyright (c) 2010-2014 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.Color;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.map.AbstractLayer;
import se.llbit.chunky.map.BiomeLayer;
import se.llbit.chunky.map.BlockLayer;
import se.llbit.chunky.map.CaveLayer;
import se.llbit.chunky.map.CorruptLayer;
import se.llbit.chunky.map.MapBuffer;
import se.llbit.chunky.map.SurfaceLayer;
import se.llbit.chunky.map.UnknownLayer;
import se.llbit.nbt.AnyTag;
import se.llbit.nbt.ErrorTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.SpecificTag;
import se.llbit.util.NotNull;

/**
 * This class represents a loaded or not-yet-loaded chunk in the world.
 *
 * If the chunk is not yet loaded the loadedLayer field is equal to -1.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class Chunk {

	private static final String LEVEL_HEIGHTMAP = ".Level.HeightMap";
	private static final String LEVEL_SECTIONS = ".Level.Sections";
	private static final String LEVEL_BIOMES = ".Level.Biomes";

	/**
	 * Chunk width
	 */
	public static final int X_MAX = 16;

	/**
	 * Chunk height
	 */
	public static final int Y_MAX = 256;

	/**
	 * Chunk depth
	 */
	public static final int Z_MAX = 16;

	private static final int SECTION_Y_MAX = 16;
	private static final int SECTION_BYTES = X_MAX*SECTION_Y_MAX*Z_MAX;
	private static final int SECTION_HALF_NIBBLES = SECTION_BYTES / 2;
	private static final int CHUNK_BYTES = X_MAX*Y_MAX*Z_MAX;

	private static final int BLOCK_LAYER = 1<<0;
	private static final int SURFACE_LAYER = 1<<1;
	private static final int CAVE_LAYER = 1<<2;
	private static final int BIOME_LAYER = 1<<3;

	private final ChunkPosition position;
	private int loadedLayer = -1;
	protected volatile AbstractLayer layer = UnknownLayer.INSTANCE;
	protected volatile AbstractLayer surface = UnknownLayer.INSTANCE;
	protected volatile AbstractLayer caves = UnknownLayer.INSTANCE;
	protected volatile AbstractLayer biomes = UnknownLayer.INSTANCE;

	private final World world;

	private int dataTimestamp = 0;
	private int layerTimestamp = 0;
	private int surfaceTimestamp = 0;
	private int cavesTimestamp = 0;
	private int biomesTimestamp = 0;

	/**
	 * A chunk renderer
	 */
	abstract static public class Renderer {
		/**
		 * Render the chunk
		 * @param chunk
		 * @param rbuff
		 * @param cx
		 * @param cz
		 */
		abstract public void render(Chunk chunk, MapBuffer rbuff, int cx, int cz);

		/**
		 * Layers to be loaded for this renderer
		 * @param view
		 * @return
		 */
		abstract public int getLayers(ChunkView view);

		public Set<String> getRequest(ChunkView view) {
			int layers = getLayers(view);
			Set<String> request = new HashSet<String>();
			request.add(LEVEL_SECTIONS);
			if ((layers&BLOCK_LAYER) != 0 || (layers&SURFACE_LAYER) != 0 ||
					(layers&BIOME_LAYER) != 0) {
				request.add(LEVEL_BIOMES);
			}
			if ((layers&SURFACE_LAYER) != 0 || (layers&CAVE_LAYER) != 0) {
				request.add(LEVEL_HEIGHTMAP);
			}
			return request;
		}

		/**
		 * @param view
		 * @param newView
		 * @param buffLayer
		 * @param layer
		 * @return {@code true} if the render buffer is still valid
		 */
		public boolean bufferValid(ChunkView oldView, ChunkView newView,
				int oldLayer, int newLayer) {
			return oldView.chunkScale == newView.chunkScale;
		}
	}

	/**
	 * Renders caves and other underground caverns
	 */
	public static Renderer caveRenderer = new Renderer() {
		@Override
		public void render(Chunk chunk, MapBuffer rbuff, int cx, int cz) {
			chunk.renderCaves(rbuff, cx, cz);
		}

		@Override
		public int getLayers(ChunkView view) {
			return CAVE_LAYER;
		}
	};

	/**
	 * Renders caves and other underground caverns
	 */
	public static Renderer biomeRenderer = new Renderer() {
		@Override
		public void render(Chunk chunk, MapBuffer rbuff, int cx, int cz) {
			chunk.renderBiomes(rbuff, cx, cz);
		}

		@Override
		public int getLayers(ChunkView view) {
			return BIOME_LAYER;
		}
	};

	/**
	 * Renders the default surface view
	 */
	public static Renderer surfaceRenderer = new Renderer() {
		@Override
		public void render(Chunk chunk, MapBuffer rbuff, int cx, int cz) {
			chunk.renderSurface(rbuff, cx, cz);
		}

		@Override
		public int getLayers(ChunkView view) {
			return SURFACE_LAYER;
		}
	};

	/**
	 * Switch between surface, layer and biome modes
	 */
	public static Renderer autoRenderer = new Renderer() {
		@Override
		public void render(Chunk chunk, MapBuffer rbuff, int cx, int cz) {
			if (rbuff.getView().scale >= 10) {
				chunk.renderSurface(rbuff, cx, cz);
			} else {
				chunk.renderBiomes(rbuff, cx, cz);
			}
		}

		@Override
		public int getLayers(ChunkView view) {
			if (view.scale >= 10) {
				return SURFACE_LAYER | BIOME_LAYER;
			} else {
				return BIOME_LAYER;
			}
		}

		@Override
		public boolean bufferValid(ChunkView oldView, ChunkView newView,
				int oldLayer, int newLayer) {
			return super.bufferValid(oldView, newView, oldLayer, newLayer) &&
					(oldView.scale >= 10 && newView.scale >= 10 ||
					oldView.scale < 10 && newView.scale < 10);
		}
	};

	/**
	 * Renders a single layer
	 */
	public static Renderer layerRenderer = new Renderer() {
		@Override
		public void render(Chunk chunk, MapBuffer rbuff, int cx, int cz) {
			chunk.renderLayer(rbuff, cx, cz);
		}

		@Override
		public int getLayers(ChunkView view) {
			return BLOCK_LAYER;
		}

		@Override
		public boolean bufferValid(ChunkView oldView, ChunkView newView,
				int oldLayer, int newLayer) {
			return super.bufferValid(oldView, newView, oldLayer, newLayer) &&
					oldLayer == newLayer;
		}
	};

	/**
	 * Create a new chunk
	 * @param pos
	 * @param world
	 */
	public Chunk(ChunkPosition pos, World world) {
		this.world = world;
		this.position = pos;
	}

	protected void renderLayer(MapBuffer rbuff, int cx, int cz) {
		layer.render(rbuff, cx, cz);
	}

	protected void renderSurface(MapBuffer rbuff, int cx, int cz) {
		surface.render(rbuff, cx, cz);
	}

	protected void renderCaves(MapBuffer rbuff, int cx, int cz) {
		caves.render(rbuff, cx, cz);
	}

	protected void renderBiomes(MapBuffer rbuff, int cx, int cz) {
		biomes.render(rbuff, cx, cz);
	}

	/**
	 * @param request fresh request set
	 * @return loaded data, or null if something went wrong
	 */
	private Map<String, AnyTag> getChunkData(Set<String> request) {
		Region region = world.getRegion(position.getRegionPosition());
		ChunkData data = region.getChunkData(position);
		if (data == null) {
			return null;
		}
		DataInputStream in = data.inputStream;
		if (in == null) {
			return null;
		}
		dataTimestamp = data.timestamp;
		Map<String, AnyTag> result = NamedTag.quickParse(in, request);
		try {
			in.close();
		} catch (IOException e) {
		}
		for (String key: request) {
			if (!result.containsKey(key)) {
				result.put(key, new ErrorTag());
			}
		}
		return result;
	}

	/**
	 * Reset the rendered layers in this chunk.
	 */
	public synchronized void reset() {
		layer = UnknownLayer.INSTANCE;
		caves = UnknownLayer.INSTANCE;
		surface = UnknownLayer.INSTANCE;
	}

	/**
	 * @return The position of this chunk
	 */
	public ChunkPosition getPosition() {
		return position;
	}

	/**
	 * Render block highlight
	 * @param rbuff
	 * @param cx
	 * @param cz
	 * @param hlBlock
	 * @param highlightColor
	 */
	public void renderHighlight(MapBuffer rbuff, int cx, int cz,
			Block hlBlock, Color highlightColor) {
		layer.renderHighlight(rbuff, cx, cz, hlBlock, highlightColor);
	}

	/**
	 * Parse the chunk from the region file and render the current
	 * layer, surface and cave maps.
	 * @param chunky
	 */
	public synchronized void loadChunk(Chunky chunky) {

		int requestedLayer = world.currentLayer();
		Chunk.Renderer renderer = chunky.getChunkRenderer();
		ChunkView view = chunky.getMapView();

		if (!shouldReloadChunk(renderer, view, requestedLayer)) {
			return;
		}

		loadedLayer = requestedLayer;

		Map<String, AnyTag> data = getChunkData(renderer.getRequest(view));

		int layers = renderer.getLayers(view);
		if ((layers&BLOCK_LAYER) != 0) {
			layerTimestamp = dataTimestamp;
			loadLayer(data, requestedLayer);
		}
		if ((layers&SURFACE_LAYER) != 0) {
			surfaceTimestamp = dataTimestamp;
			loadSurface(data);
		}
		if ((layers&BIOME_LAYER) != 0) {
			biomesTimestamp = dataTimestamp;
			loadBiomes(data);
		}
		if ((layers&CAVE_LAYER) != 0) {
			cavesTimestamp = dataTimestamp;
			loadCaves(data);
		}

		world.chunkUpdated(position);
	}

	private void loadSurface(Map<String, AnyTag> data) {
		if (data == null) {
			surface = CorruptLayer.INSTANCE;
			return;
		}

		Heightmap heightmap = world.heightmap();
		AnyTag sections = data.get(LEVEL_SECTIONS);
		if (sections.isList()) {
			int[] heightmapData = extractHeightmapData(data);
			byte[] biomeData = extractBiomeData(data);
			byte[] chunkData = new byte[CHUNK_BYTES];
			byte[] blockData = new byte[CHUNK_BYTES];
			extractChunkData(data, chunkData, blockData);
			updateHeightmap(heightmap, position, chunkData, heightmapData);
			surface = new SurfaceLayer(world.currentDimension(), position,
					chunkData, biomeData, blockData);
			queueTopography();
		} else {
			surface = CorruptLayer.INSTANCE;
		}
	}

	private void loadBiomes(Map<String, AnyTag> data) {
		if (data == null) {
			biomes = CorruptLayer.INSTANCE;
		} else {
			biomes = new BiomeLayer(extractBiomeData(data));
		}
	}

	private void loadLayer(Map<String, AnyTag> data, int requestedLayer) {
		if (data == null) {
			layer = CorruptLayer.INSTANCE;
			return;
		}

		AnyTag sections = data.get(LEVEL_SECTIONS);
		if (sections.isList()) {
			byte[] biomeData = extractBiomeData(data);
			byte[] chunkData = new byte[CHUNK_BYTES];
			extractChunkData(data, chunkData, new byte[CHUNK_BYTES]);
			layer = new BlockLayer(chunkData, biomeData, requestedLayer);
		} else {
			layer = CorruptLayer.INSTANCE;
		}
	}

	private void loadCaves(Map<String, AnyTag> data) {
		if (data == null) {
			caves = CorruptLayer.INSTANCE;
			return;
		}

		AnyTag sections = data.get(LEVEL_SECTIONS);
		if (sections.isList()) {
			int[] heightmapData = extractHeightmapData(data);
			byte[] chunkData = new byte[CHUNK_BYTES];
			extractChunkData(data, chunkData, new byte[CHUNK_BYTES]);
			caves = new CaveLayer(chunkData, heightmapData);
		} else {
			caves = CorruptLayer.INSTANCE;
		}
	}

	private byte[] extractBiomeData(@NotNull Map<String, AnyTag> data) {
		AnyTag biomesTag = data.get(LEVEL_BIOMES);
		if (biomesTag.isByteArray(X_MAX*Z_MAX)) {
			return biomesTag.byteArray();
		} else {
			return new byte[X_MAX*Z_MAX];
		}
	}

	private int[] extractHeightmapData(@NotNull Map<String, AnyTag> data) {
		AnyTag heightmapTag = data.get(LEVEL_HEIGHTMAP);
		if (heightmapTag.isIntArray(X_MAX*Z_MAX)) {
			return heightmapTag.intArray();
		} else {
			int[] fallback = new int[X_MAX*Z_MAX];
			for (int i = 0; i < fallback.length; ++i) {
				fallback[i] = Y_MAX-1;
			}
			return fallback;
		}
	}

	private void extractChunkData(@NotNull Map<String, AnyTag> data, @NotNull byte[] blocks, @NotNull byte[] blockData) {
		AnyTag sections = data.get(LEVEL_SECTIONS);
		if (sections.isList()) {
			for (SpecificTag section : ((ListTag) sections).getItemList()) {
				AnyTag yTag = section.get("Y");
				int yOffset = yTag.byteValue() & 0xFF;
				AnyTag blocksTag = section.get("Blocks");
				if (blocksTag.isByteArray(SECTION_BYTES)) {
					System.arraycopy(blocksTag.byteArray(), 0, blocks, SECTION_BYTES*yOffset, SECTION_BYTES);
				}
				AnyTag dataTag = section.get("Data");
				if (dataTag.isByteArray(SECTION_HALF_NIBBLES)) {
					System.arraycopy(dataTag.byteArray(), 0, blockData, SECTION_HALF_NIBBLES*yOffset, SECTION_HALF_NIBBLES);
				}
			}
		}
	}

	/**
	 * Load heightmap information from a chunk heightmap array
	 * and insert into a quadtree.
	 *
	 * @param heightmap
	 * @param pos
	 * @param blocksArray
	 * @param chunkHeightmap
	 */
	public static void updateHeightmap(Heightmap heightmap, ChunkPosition pos,
			byte[] blocksArray, int[] chunkHeightmap) {
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				int y = chunkHeightmap[z*16+x];
				y = Math.max(1, y-1);
				for (; y > 1; --y) {
					Block block = Block.get(blocksArray[Chunk.chunkIndex(x, y, z)]);
					if (block != Block.AIR && !block.isWater())
						break;
				}
				heightmap.set(y, pos.x*16+x, pos.z*16+z);
			}
		}
	}

	private boolean shouldReloadChunk(Renderer renderer, ChunkView view, int requestedLayer) {
		int timestamp = Integer.MAX_VALUE;
		int layers = renderer.getLayers(view);
		if ((layers&BLOCK_LAYER) != 0) {
			if (requestedLayer != loadedLayer) {
				return true;
			}
			timestamp = layerTimestamp;
		}
		if ((layers&SURFACE_LAYER) != 0) {
			timestamp = Math.min(timestamp, surfaceTimestamp);
		}
		if ((layers&BIOME_LAYER) != 0) {
			timestamp = Math.min(timestamp, biomesTimestamp);
		}
		if ((layers&CAVE_LAYER) != 0) {
			timestamp = Math.min(timestamp, cavesTimestamp);
		}
		if (timestamp == 0) {
			return true;
		}
		Region region = world.getRegion(position.getRegionPosition());
		return region.chunkChangedSince(position, timestamp);
	}

	private void queueTopography() {
		for (int x = -1; x <= 1; ++x) {
			for (int z = -1; z <= 1; ++z) {
				ChunkPosition pos = ChunkPosition.get(
						position.x + x,
						position.z + z);
				Chunk chunk = world.getChunk(pos);
				if (!chunk.isEmpty()) {
					world.chunkTopographyUpdated(chunk);
				}
			}
		}
	}

	/**
	 * @return <code>true</code> if this is an empty (non-existing) chunk
	 */
	public boolean isEmpty() {
		return false;
	}

	/**
	 * Render the topography of this chunk.
	 */
	public synchronized void renderTopography() {
		surface.renderTopography(position, world.heightmap());
		world.chunkUpdated(position);
	}

	/**
	 * @return The average color of the surface in this chunk
	 */
	public synchronized int avgColor() {
		return surface.getAvgColor();
	}

	/**
	 * Load the block data for this chunk
	 * @param blocks
	 * @param blockData
	 * @param biomes
	 */
	public synchronized void getBlockData(
			byte[] blocks, byte[] blockData, byte[] biomes) {

		for (int i = 0; i < CHUNK_BYTES; ++i) {
			blocks[i] = 0;
		}

		for (int i = 0; i < X_MAX * Z_MAX; ++i) {
			biomes[i] = 0;
		}

		for (int i = 0; i < (CHUNK_BYTES) / 2; ++i) {
			blockData[i] = 0;
		}

		Set<String> request = new HashSet<String>();
		request.add(LEVEL_SECTIONS);
		request.add(LEVEL_BIOMES);
		Map<String, AnyTag> data = getChunkData(request);
		AnyTag sections = data.get(LEVEL_SECTIONS);
		AnyTag biomesTag = data.get(LEVEL_BIOMES);
		if (sections.isList() && biomesTag.isByteArray(X_MAX*Z_MAX)) {
			byte[] chunkBiomes = extractBiomeData(data);
			System.arraycopy(chunkBiomes, 0, biomes, 0, chunkBiomes.length);
			extractChunkData(data, blocks, blockData);
		}
	}

	/**
	 * Write a PNG scanline
	 * @param scanline
	 * @param out
	 * @throws IOException
	 */
	public void writePngLine(int scanline, OutputStream out) throws IOException {
		surface.writePngLine(scanline, out);
	}


	/**
	 * @param x
	 * @param y
	 * @param z
	 * @return Integer index into a chunk YXZ array
	 */
	public static final int chunkIndex(int x, int y, int z) {
		return x + Chunk.X_MAX * ( z + Chunk.Z_MAX * y );
	}

	/**
	 * @param x
	 * @param z
	 * @return Integer index into a chunk XZ array
	 */
	public static final int chunkXZIndex(int x, int z) {
		return x + Chunk.X_MAX * z;
	}

	@Override
	public String toString() {
		return "Chunk: " + position.toString();
	}

	/**
	 * @return <code>true</code> if this chunk has been parsed
	 */
	public synchronized boolean isParsed() {
		return loadedLayer != -1;
	}

	/**
	 * @return If the currently visible layer has been parsed
	 */
	public synchronized boolean isLayerParsed() {
		return loadedLayer == world.currentLayer();
	}

	public String biomeAt(int blockX, int blockZ) {
		if (biomes instanceof BiomeLayer) {
			BiomeLayer biomeLayer = (BiomeLayer) biomes;
			return biomeLayer.biomeAt(blockX, blockZ);
		} else {
			return "unknown";
		}
	}
}
