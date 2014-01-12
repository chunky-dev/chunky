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
import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import se.llbit.chunky.map.RenderBuffer;
import se.llbit.nbt.AnyTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.SpecificTag;

/**
 * This class represents a loaded or not-yet-loaded chunk in the world.
 *
 * If the chunk is not yet loaded the loadedLayer field is equal to -1.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class Chunk {

	private static final String LEVEL_HEIGHT_MAP = ".Level.HeightMap";
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
	private static final int SECTION_HALF_BYTES = SECTION_BYTES / 2;

	private final ChunkPosition position;
	private int loadedLayer = -1;
	protected Layer layer = Layer.unknownLayer;
	protected Layer surface = Layer.unknownLayer;
	protected Layer caves = Layer.unknownLayer;
	protected Layer biomes = Layer.unknownLayer;

	private final World world;

	private int timestamp = 0;

	private SoftReference<Map<String, AnyTag>> chunkData =
			new SoftReference<Map<String,AnyTag>>(null);

	/**
	 * A chunk renderer
	 */
	public interface Renderer {
		/**
		 * Render the chunk
		 * @param chunk
		 * @param rbuff
		 * @param cx
		 * @param cz
		 */
		public void render(Chunk chunk, RenderBuffer rbuff, int cx, int cz);
	}

	/**
	 * Renders caves and other underground caverns
	 */
	public static Renderer caveRenderer = new Renderer() {
		@Override
		public void render(Chunk chunk, RenderBuffer rbuff, int cx, int cz) {
			chunk.renderCaves(rbuff, cx, cz);
		}
	};

	/**
	 * Renders caves and other underground caverns
	 */
	public static Renderer biomeRenderer = new Renderer() {
		@Override
		public void render(Chunk chunk, RenderBuffer rbuff, int cx, int cz) {
			chunk.renderBiomes(rbuff, cx, cz);
		}
	};

	/**
	 * Renders the default surface view
	 */
	public static Renderer surfaceRenderer = new Renderer() {
		@Override
		public void render(Chunk chunk, RenderBuffer rbuff, int cx, int cz) {
			chunk.renderSurface(rbuff, cx, cz);
		}
	};

	/**
	 * Renders a single layer
	 */
	public static Renderer layerRenderer = new Renderer() {
		@Override
		public void render(Chunk chunk, RenderBuffer rbuff, int cx, int cz) {
			chunk.renderLayer(rbuff, cx, cz);
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

	protected void renderLayer(RenderBuffer rbuff, int cx, int cz) {
		layer.render(rbuff, cx, cz);
	}

	protected void renderSurface(RenderBuffer rbuff, int cx, int cz) {
		surface.render(rbuff, cx, cz);
	}

	protected void renderCaves(RenderBuffer rbuff, int cx, int cz) {
		caves.render(rbuff, cx, cz);
	}

	protected void renderBiomes(RenderBuffer rbuff, int cx, int cz) {
		biomes.render(rbuff, cx, cz);
	}

	private Map<String, AnyTag> getChunkData() {
		Region region = world.getRegion(position.getRegionPosition());
		ChunkData data = region.getChunkData(position);
		DataInputStream in = data.inputStream;
		if (in == null) {
			layer = Layer.corruptLayer;
			return null;
		}
		timestamp = data.timestamp;
		Set<String> request = new HashSet<String>();
		request.add(LEVEL_SECTIONS);
		request.add(LEVEL_BIOMES);
		request.add(LEVEL_HEIGHT_MAP);

		Map<String, AnyTag> result = NamedTag.quickParse(in, request);
		chunkData = new SoftReference<Map<String,AnyTag>>(result);
		try {
			in.close();
		} catch (IOException e) {
		}
		return result;
	}

	/**
	 * Reset the rendered layers in this chunk.
	 */
	public synchronized void reset() {
		layer = Layer.unknownLayer;
		caves = Layer.unknownLayer;
		surface = Layer.unknownLayer;
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
	public void renderHighlight(RenderBuffer rbuff, int cx, int cz,
			Block hlBlock, Color highlightColor) {
		layer.renderHighlight(rbuff, cx, cz, hlBlock, highlightColor);
	}

	/**
	 * Parse the chunk from the region file and render the current
	 * layer, surface and cave maps.
	 */
	public synchronized void parse() {

		int requestedLayer = world.currentLayer();
		Heightmap heightmap = world.heightmap();

		boolean chunkChanged = chunkHasChanged();
		if (requestedLayer == loadedLayer && !chunkChanged) {
			return;
		}

		loadedLayer = requestedLayer;

		Map<String, AnyTag> result = chunkChanged ? getChunkData() : getChunkDataCached();
		if (result == null) {
			// Chunk failed to load
			layer = Layer.corruptLayer;
			surface = Layer.corruptLayer;
			caves = Layer.corruptLayer;
			return;
		}

		AnyTag sections = result.get(LEVEL_SECTIONS);
		if (sections.isList()) {
			byte[] chunkData = new byte[X_MAX * Y_MAX * Z_MAX];
			byte[] blockData = new byte[X_MAX * Y_MAX * Z_MAX];

			for (SpecificTag section : ((ListTag) sections).getItemList()) {
				AnyTag yTag = section.get("Y");
				AnyTag blocks = section.get("Blocks");
				AnyTag data = section.get("Data");
				int yOffset = yTag.byteValue() & 0xFF;
				if (blocks.isByteArray(SECTION_BYTES) && data.isByteArray(SECTION_HALF_BYTES)) {
					System.arraycopy(blocks.byteArray(), 0, chunkData, SECTION_BYTES*yOffset, SECTION_BYTES);
					System.arraycopy(data.byteArray(), 0, blockData, SECTION_HALF_BYTES*yOffset, SECTION_HALF_BYTES);
				}
			}

			AnyTag heightmapTag = result.get(LEVEL_HEIGHT_MAP);
			AnyTag biomesTag = result.get(LEVEL_BIOMES);
			if (surface == Layer.unknownLayer
					&& heightmapTag.isIntArray(X_MAX*Z_MAX)
					&& biomesTag.isByteArray(X_MAX*Z_MAX)) {

				int[] chunkHeightmap = heightmapTag.intArray();
				byte[] chunkBiomes = biomesTag.byteArray();
				caves = Layer.loadCaves(chunkData, chunkHeightmap);
				Layer.updateHeightmap(heightmap, position, chunkData, chunkHeightmap);
				surface = Layer.loadSurface(world.currentDimension(), position,
						chunkData, chunkBiomes, blockData);
				layer = Layer.loadLayer(chunkData, requestedLayer);
				biomes = Layer.loadBiomes(chunkBiomes);
				queueTopography();
			} else if (surface == Layer.unknownLayer) {
				layer = Layer.emptyLayer;
				caves = Layer.emptyLayer;
				surface = Layer.emptyLayer;
			} else {
				layer = Layer.loadLayer(chunkData, requestedLayer);
			}

		} else {
			layer = Layer.corruptLayer;
			surface = Layer.corruptLayer;
			caves = Layer.corruptLayer;
		}

		world.chunkUpdated(position);
	}

	private boolean chunkHasChanged() {
		if (timestamp == 0) {
			return true;
		}
		Region region = world.getRegion(position.getRegionPosition());
		return region.chunkHasChanged(position, timestamp);
	}

	private Map<String, AnyTag> getChunkDataCached() {
		Map<String, AnyTag> result = chunkData.get();
		if (result == null) {
			result = getChunkData();
		}
		return result;
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
	 * @param data
	 * @param biomes
	 */
	public synchronized void getBlockData(
			byte[] blocks, byte[] data, byte[] biomes) {

		for (int i = 0; i < X_MAX * Y_MAX * Z_MAX; ++i) {
			blocks[i] = 0;
		}

		for (int i = 0; i < X_MAX * Z_MAX; ++i) {
			biomes[i] = 0;
		}

		for (int i = 0; i < (X_MAX * Y_MAX * Z_MAX) / 2; ++i) {
			data[i] = 0;
		}

		boolean chunkChanged = chunkHasChanged();
		Map<String, AnyTag> result = chunkChanged ? getChunkData() : getChunkDataCached();
		if (result == null) {
			return;
		}

		AnyTag sections = result.get(LEVEL_SECTIONS);
		AnyTag biomesTag = result.get(LEVEL_BIOMES);
		if (sections.isList() && biomesTag.isByteArray(X_MAX*Z_MAX)) {

			byte[] chunkBiomes = biomesTag.byteArray();
			System.arraycopy(chunkBiomes, 0, biomes, 0, chunkBiomes.length);

			for (SpecificTag section : ((ListTag) sections).getItemList()) {
				AnyTag yTag = section.get("Y");
				AnyTag blocksTag = section.get("Blocks");
				AnyTag dataTag = section.get("Data");
				int yOffset = yTag.byteValue() & 0xFF;
				if (blocksTag.isByteArray(SECTION_BYTES) && dataTag.isByteArray(SECTION_HALF_BYTES)) {
					System.arraycopy(blocksTag.byteArray(), 0, blocks, SECTION_BYTES*yOffset, SECTION_BYTES);
					System.arraycopy(dataTag.byteArray(), 0, data, SECTION_HALF_BYTES*yOffset, SECTION_HALF_BYTES);
				}
			}
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
		return "Chunk " + position.toString();
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
}
