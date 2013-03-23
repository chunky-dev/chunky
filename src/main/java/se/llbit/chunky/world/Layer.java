/* Copyright (c) 2010-2012 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.Graphics;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.OutputStream;

import se.llbit.chunky.map.RenderBuffer;
import se.llbit.chunky.resources.MiscImages;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Color;
import se.llbit.util.ImageTools;

/**
 * A layer describes the visible part of a chunk.
 *
 * A chunk typically stores three layers;
 * current layer, cave layer and surface layer.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class Layer {

	private byte[] blocks = null;
	private int[] bitmap = null;
	private int avgColor = 0xFF;
	static int selectionColor = 0x75FF0000;

	/**
	 * Singleton instance of empty layer.
	 */
	public static final Layer emptyLayer = new Layer() {

		@Override
		public synchronized void renderTopography(ChunkPosition position,
				Heightmap heightmap) {
		}

		@Override
		public synchronized void render(RenderBuffer rbuff, int cx, int cz) {
			ChunkView view = rbuff.getView();
			int x0 = view.chunkScale * (cx - view.ix0);
			int z0 = view.chunkScale * (cz - view.iz0);

			if (view.chunkScale == 1) {
				rbuff.setRGB(x0, z0, averageColor);
			} else {
				rbuff.fillRect(x0, z0, view.chunkScale, view.chunkScale, averageColor);
			}
		}

		private int averageColor = 0xFF000000;

		@Override
		public int getAvgColor() {
			return averageColor;
		}

	};

	/**
	 * An unknown layer is a layer that has not yet been parsed
	 */
	public static final Layer unknownLayer = new Layer() {

		@Override
		public synchronized void renderTopography(ChunkPosition position,
				Heightmap heightmap) {
		}

		@Override
		public synchronized void render(RenderBuffer rbuff, int cx, int cz) {
			ChunkView view = rbuff.getView();
			int x0 = view.chunkScale * (cx - view.ix0);
			int z0 = view.chunkScale * (cz - view.iz0);

			if (view.chunkScale == 1) {
				rbuff.setRGB(x0, z0, averageColor);
			} else {
				Graphics g = rbuff.getGraphics();
				g.drawImage(MiscImages.load, x0, z0,
						view.chunkScale, view.chunkScale, null);
			}
		}

		private int averageColor = ImageTools.calcAvgColor(MiscImages.load);

		@Override
		public int getAvgColor() {
			return averageColor;
		}
	};

	/**
	 * Represents corrupt chunk data
	 */
	public static final Layer corruptLayer = new Layer() {
		@Override
		public synchronized void renderTopography(ChunkPosition position,
				Heightmap heightmap) {
		}

		@Override
		public synchronized void render(RenderBuffer rbuff, int cx, int cz) {
			ChunkView view = rbuff.getView();
			int x0 = view.chunkScale * (cx - view.ix0);
			int z0 = view.chunkScale * (cz - view.iz0);

			if (view.chunkScale == 1) {
				rbuff.setRGB(x0, z0, averageColor);
			} else {
				rbuff.getGraphics().drawImage(MiscImages.corruptLayer,
						x0, z0, view.chunkScale, view.chunkScale, null);
			}
		}

		private int averageColor = ImageTools.calcAvgColor(MiscImages.corruptLayer);

		@Override
		public int getAvgColor() {
			return averageColor;
		}
	};

	/**
	 * Creates a new tile layer
	 * @param data tile data
	 */
	private Layer(byte[] data) {
		blocks = data;
	}

	/**
	 * Creates a new bitmap layer
	 * @param data bitmap data
	 */
	private Layer(int[] data) {
		bitmap = data;
		avgColor = avgBitmapColor();
	}

	private int avgBitmapColor() {
		float[] avg = new float[3];
		float[] frgb = new float[3];
		for (int i = 0; i < 16*16; ++i) {
			Color.getRGBComponents(bitmap[i], frgb);
			avg[0] += frgb[0];
			avg[1] += frgb[1];
			avg[2] += frgb[2];
		}
		return Color.getRGB(
				avg[0] / (16 * 16),
				avg[1] / (16 * 16),
				avg[2] / (16 * 16));
	}

	/**
	 * Create an empty layer.
	 */
	public Layer() {
	}

	/**
	 * Render block highlight
	 * @param rbuff
	 * @param cx
	 * @param cz
	 * @param hlBlock
	 * @param highlight
	 */
	public synchronized void renderHighlight(RenderBuffer rbuff, int cx, int cz,
			Block hlBlock, java.awt.Color highlight) {

		ChunkView view = rbuff.getView();
		int x0 = view.chunkScale * (cx - view.ix0);
		int z0 = view.chunkScale * (cz - view.iz0);

		if (blocks == null)
			return;

		Graphics g = rbuff.getGraphics();
		g.setColor(new java.awt.Color(1,1,1,0.35f));
		g.fillRect(x0, z0, view.chunkScale, view.chunkScale);
		g.setColor(highlight);

		if (view.chunkScale == 16) {

			for (int x = 0; x < 16; ++x) {
				int xp = x0 + x;

				for (int z = 0; z < 16; ++z) {
					int yp = z0 + z;

					if (hlBlock.id == (0xFF & blocks[x * 16 + z])) {
						rbuff.setRGB(xp, yp, highlight.getRGB());
					}
				}
			}
		} else {
			int blockScale = view.chunkScale / 16;

			for (int x = 0; x < 16; ++x) {
				int xp0 = x0 + x * blockScale;
				int xp1 = xp0 + blockScale;

				for (int z = 0; z < 16; ++z) {
					int yp0 = z0 + z * blockScale;
					int yp1 = yp0 + blockScale;

					if (hlBlock.id == (0xFF & blocks[x * 16 + z])) {
						g.fillRect(xp0, yp0, xp1 - xp0, yp1 - yp0);
					}
				}
			}
		}
	}

	/**
	 * Render this layer
	 * @param rbuff
	 * @param cx
	 * @param cz
	 */
	public synchronized void render(RenderBuffer rbuff, int cx, int cz) {

		if (blocks != null) {
			renderTiles(rbuff, cx, cz);
		} else if (bitmap != null) {
			renderBitmap(rbuff, cx, cz);
		}
	}

	private void renderTiles(RenderBuffer rbuff, int cx, int cz) {

		ChunkView view = rbuff.getView();
		int blockScale = view.chunkScale / 16;
		int x0 = view.chunkScale * (cx - view.ix0);
		int z0 = view.chunkScale * (cz - view.iz0);

		if (view.chunkScale == 1) {

		} else if (blockScale == 1) {

			for (int z = 0; z < 16; ++z) {
				int yp = z0 + z;

				for (int x = 0; x < 16; ++x) {
					int xp = x0 + x;

					byte block = blocks[x * 16 + z];
					if (block == Block.AIR.id) {
						rbuff.setRGB(xp, yp, 0xFFFFFFFF);
					} else {
						rbuff.setRGB(xp, yp, Block.get(block).getAvgRGB());
					}
				}
			}
		} else if (blockScale < 12) {


			for (int z = 0; z < 16; ++z) {
				int yp0 = z0 + z * blockScale;

				for (int x = 0; x < 16; ++x) {
					int xp0 = x0 + x * blockScale;

					byte block = blocks[x * 16 + z];
					if (block == Block.AIR.id) {
						rbuff.fillRect(xp0, yp0, blockScale, blockScale, 0xFFFFFFFF);
					} else {
						rbuff.fillRect(xp0, yp0, blockScale, blockScale,
								Block.get(block).getAvgRGB());
					}
				}
			}
		} else {
			for (int z = 0; z < 16; ++z) {
				int yp0 = z0 + z * blockScale;

				for (int x = 0; x < 16; ++x) {
					int xp0 = x0 + x * blockScale;

					byte block = blocks[x * 16 + z];
					if (block == Block.AIR.id) {
						rbuff.fillRect(xp0, yp0, blockScale, blockScale, 0xFFFFFFFF);
						continue;
					}

					int[] tex = ((DataBufferInt) Block.get(block).getTexture().getScaledImage(blockScale).getRaster().getDataBuffer()).getData();
					for (int i = 0; i < blockScale; ++i) {
						for (int j = 0; j < blockScale; ++j) {
							rbuff.setRGB(xp0 + j, yp0 + i, tex[j + blockScale * i]);
						}
					}
				}
			}
		}
	}

	private void renderBitmap(RenderBuffer rbuff, int cx, int cz) {

		ChunkView view = rbuff.getView();
		int x0 = view.chunkScale * (cx - view.ix0);
		int z0 = view.chunkScale * (cz - view.iz0);

		if (view.chunkScale == 1) {
			rbuff.setRGB(x0, z0, avgColor);
		} else if (view.chunkScale == 16) {

			for (int z = 0; z < 16; ++z) {
				for (int x = 0; x < 16; ++x) {
					rbuff.setRGB(x0 + x, z0 + z, bitmap[z + x*16]);
				}
			}
		} else {

			int blockScale = view.chunkScale / 16;

			for (int z = 0; z < 16; ++z) {
				int yp0 = z0 + z * blockScale;

				for (int x = 0; x < 16; ++x) {
					int xp0 = x0 + x * blockScale;

					rbuff.fillRect(xp0, yp0, blockScale, blockScale, bitmap[z + x*16]);
				}
			}
		}
	}

	/**
	 * Load layer from block data
	 * @param blockData
	 * @param layer
	 * @return The loaded layer
	 */
	public static Layer loadLayer(byte[] blockData, int layer) {
		byte[] data = new byte[16*16];
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				data[x*16+z] = blockData[Chunk.chunkIndex(x, layer, z)];
			}
		}
		return new Layer(data);
	}

	/**
	 * Load biome IDs into layer
	 * @param chunkBiomes
	 * @return The loaded layer
	 */
	public static Layer loadBiomes(byte[] chunkBiomes) {
		int[]	surface = new int[16*16];
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				surface[x*16+z] = Biomes.getColor(0xFF & chunkBiomes[Chunk.chunkXZIndex(x, z)]);
			}
		}
		return new Layer(surface);
	}

	/**
	 * Generate the surface bitmap.
	 *
	 * @param dim current dimension
	 * @param position Chunk position
	 * @param blocksArray block id array
	 * @param biomes
	 * @param blockData
	 * @return the generated bitmap layer
	 */
	public static Layer loadSurface(int dim, ChunkPosition position,
			byte[] blocksArray, byte[] biomes, byte[] blockData) {

		int[] surface = new int[16*16];
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {

				// find the topmost non-empty block
				int y = Chunk.Y_MAX-1;
				if (dim != -1) {
					for (; y > 0; --y) {
						int block = 0xFF & blocksArray[Chunk.chunkIndex(x, y, z)];
						if (block != Block.AIR.id)
							break;
					}
				} else {
					// nether worlds have a ceiling that we want to skip
					for (; y > 1; --y) {
						int block = 0xFF & blocksArray[Chunk.chunkIndex(x, y, z)];
						if (block != Block.AIR.id)
							break;
					}
					for (; y > 1; --y) {
						int block = 0xFF & blocksArray[Chunk.chunkIndex(x, y, z)];
						if (block == Block.AIR.id)
							break;
					}
					for (; y > 1; --y) {
						int block = 0xFF & blocksArray[Chunk.chunkIndex(x, y, z)];
						if (block != Block.AIR.id)
							break;
					}
				}

				float[] color = new float[4];

				colorloop:
				for (; y >= 0 && color[3] < 1.f;) {
					Block block = Block.get(blocksArray[Chunk.chunkIndex(x, y, z)]);
					float[] blockColor = new float[4];
					int biomeId = 0xFF & biomes[Chunk.chunkXZIndex(x, z)];

					switch (block.id) {

					case Block.LEAVES_ID:
						Color.getRGBComponents(Biomes.getFoliageColor(biomeId), blockColor);
						blockColor[3] = 1.f;// foliage colors don't include alpha

						y -= 1;
						break;

					case Block.GRASS_ID:
					case Block.VINES_ID:
					case Block.TALLGRASS_ID:
						Color.getRGBComponents(Biomes.getGrassColor(biomeId), blockColor);
						blockColor[3] = 1.f;// grass colors don't include alpha

						y -= 1;
						break;

					case Block.WOOL_ID:
						int woolType = 0xFF & blockData[Chunk.chunkIndex(x, y, z)/2];
						woolType >>= (x % 2) * 4;
						woolType &= 0xF;

						Color.getRGBComponents(Texture.wool[woolType].getAvgColor(), blockColor);
						blockColor[3] = 1.f;// wool colors don't include alpha

						y -= 1;
						break;

					case Block.ICE_ID:
						Color.getRGBAComponents(Block.ICE.getAvgTopRGB(), blockColor);
						color = blend(color, blockColor);
						y -= 1;

						for (; y >= 0; --y) {
							if (Block.get(blocksArray[Chunk.chunkIndex(x, y, z)]).isOpaque) {
								Color.getRGBAComponents(block.getAvgTopRGB(), blockColor);
								break;
							}
						}
						break;

					case Block.WATER_ID:
					case Block.STATIONARYWATER_ID:
						int depth = 1;
						y -= 1;
						for (; y >= 0; --y) {
							Block block1 = Block.get(blocksArray[Chunk.chunkIndex(x, y, z)]);
							if (!block1.isWater())
								break;
							depth += 1;
						}

						Color.getRGBAComponents(Block.WATER.getAvgTopRGB(), blockColor);
						blockColor[3] = Math.max(.5f, 1.f - depth/32.f);
						break;

					default:
						Color.getRGBAComponents(block.getAvgTopRGB(), blockColor);

						if (block.isOpaque && y > 64) {
							float fade = Math.min(0.6f, (y-World.SEA_LEVEL) / 60.f);
							fade = Math.max(0.f, fade);
							blockColor[0] = (1-fade)*blockColor[0] + fade;
							blockColor[1] = (1-fade)*blockColor[1] + fade;
							blockColor[2] = (1-fade)*blockColor[2] + fade;
						}

						y -= 1;
						break;
					}

					color = blend(color, blockColor);

					if (block.isOpaque)
						break colorloop;
				}

				surface[x*16+z] = Color.getRGBA(color[0], color[1], color[2], color[3]);
			}
		}

		return new Layer(surface);
	}

	/**
	 * Blend the two argb colors a and b. Result is stored in the array a.
	 * @param src
	 * @param dst
	 */
	private static final float[] blend(float[] src, float[] dst) {
		float[] out = new float[4];
		out[3] = src[3] + dst[3] * (1-src[3]);
		out[0] = ( src[0] * src[3] + dst[0] * dst[3] * (1-src[3]) ) / out[3];
		out[1] = ( src[1] * src[3] + dst[1] * dst[3] * (1-src[3]) ) / out[3];
		out[2] = ( src[2] * src[3] + dst[2] * dst[3] * (1-src[3]) ) / out[3];
		return out;
	}

	/**
	 * Add topographical gradient to this chunk and calculate average color
	 * @param position
	 * @param heightmap
	 */
	public synchronized void renderTopography(ChunkPosition position,
			Heightmap heightmap) {

		int cx = position.x * Chunk.X_MAX;
		int cz = position.z * Chunk.Z_MAX;

		float[] rgb = new float[3];
		for (int x = 0; x < 16; ++x) {

			for (int z = 0; z < 16; ++z) {

				Color.getRGBComponents(bitmap[x*16 + z], rgb);

				float gradient = (
						  heightmap.get(cx+x,	cz+z)
						+ heightmap.get(cx+x+1, cz+z)
						+ heightmap.get(cx+x,	cz+z+1)
						- heightmap.get(cx+x-1, cz+z)
						- heightmap.get(cx+x,	cz+z-1)
						- heightmap.get(cx+x-1, cz+z-1)
						);
				gradient = (float) ( (Math.atan(gradient / 15) / (Math.PI/1.7) ) + 1 );

				rgb[0] *= gradient;
				rgb[1] *= gradient;
				rgb[2] *= gradient;

				// clip the result
				rgb[0] = Math.max(0.f, rgb[0]);
				rgb[0] = Math.min(1.f, rgb[0]);
				rgb[1] = Math.max(0.f, rgb[1]);
				rgb[1] = Math.min(1.f, rgb[1]);
				rgb[2] = Math.max(0.f, rgb[2]);
				rgb[2] = Math.min(1.f, rgb[2]);

				bitmap[x*16 + z] = Color.getRGB(rgb[0], rgb[1], rgb[2]);
			}
		}
	}

	/**
	 * Generate the cave map. Only holes which are large enough to have mobs spawn
	 * in them are counted towards the color of a cave. The color is then determined
	 * by the number of unoccupied blocks beneath the topmost (surface) block.
	 *
	 * The more empty space, the deeper the cave color.
	 *
	 * @param blocksArray
	 * @param heightmap
	 * @return The loaded layer
	 */
	public static Layer loadCaves(byte[] blocksArray, int[] heightmap) {
		int[] caves = new int[16*16];
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				int y = heightmap[z*16+x];
				y = Math.max(0, y-1);

				// find ground level
				for (; y > 1; --y) {
					int block = blocksArray[Chunk.chunkIndex(x, y, z)] & 0xFF;
					if (block != Block.AIR.id
							&& block != Block.LEAVES.id
							&& block != Block.WOOD.id)
						break;
				}

				// find caves
				int luftspalt = 0;
				for (; y > 1; --y) {
					Block block = Block.get(blocksArray[Chunk.chunkIndex(x, y, z)]);
					if (block.isCave()) {
						y -= 1;
						Block block1 = Block.get(blocksArray[Chunk.chunkIndex(x, y, z)]);
						if (block1.isCave()) {
							luftspalt++;
							y -= 1;
						}
					}
				}

				if (luftspalt == 0) {
					caves[x*16+z] = 0xFFFFFFFF;
				} else {
					luftspalt = Math.min(64, luftspalt*3+5);
					float fade = Math.max(0, 1.f - luftspalt/64.f);
					caves[x*16+z] = Color.getRGB(1.f*fade, 1.f*fade, 1.f);
				}
			}
		}
		return new Layer(caves);
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

	/**
	 * @return The average color of this layer
	 */
	public int getAvgColor() {
		return avgColor;
	}

	/**
	 * Write a PNG scanline
	 * @param scanline
	 * @param out
	 * @throws IOException
	 */
	public void writePngLine(int scanline, OutputStream out) throws IOException {
		if (bitmap != null) {
			byte[] rgb = new byte[] {0, 0, 0};
			for (int x = 15; x >= 0; --x) {
				int rgbInt = bitmap[x + scanline * 16];
				rgb[0] = (byte) (rgbInt >> 16);
				rgb[1] = (byte) (rgbInt >> 8);
				rgb[2] = (byte) rgbInt;
				out.write(rgb);
			}
		} else {
			byte[] white = new byte[] {-1, -1, -1};
			byte[] black = new byte[] {0, 0, 0};
			for (int x = 0; x < 16; ++x) {
				if (x == scanline)
					out.write(black);
				else
					out.write(white);
			}
		}
	}

}
