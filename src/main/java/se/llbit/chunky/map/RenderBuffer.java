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
package se.llbit.chunky.map;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.imageio.ImageIO;

import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.Chunk.Renderer;
import se.llbit.chunky.world.listeners.ChunkUpdateListener;
import se.llbit.chunky.world.ChunkIterator;
import se.llbit.chunky.world.ChunkListIterator;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.ChunkView;
import se.llbit.chunky.world.ChunkViewIterator;

/**
 * Keeps a buffered image of rendered chunks. Only re-render chunks when
 * they are not buffered. The buffer contains all visible chunks, plus some
 * outside of the view.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class RenderBuffer implements ChunkUpdateListener {

	private BufferedImage buffer;
	private int buffW;
	private int buffH;
	private Chunk.Renderer buffMode;
	private int buffLayer;
	private ChunkView view;
	private int x_offset = 0;
	private int y_offset = 0;
	private int[] data;
	private Graphics graphics;
	private ChunkIterator iterator = new ChunkListIterator();

	/**
	 * Create a new render buffer for the provided view
	 * @param view
	 */
	public RenderBuffer(ChunkView view) {
		this.view = view;
		buffW = (int) (view.chunkScale * (view.ix1 - view.ix0 + 1));
		buffH = (int) (view.chunkScale * (view.iz1 - view.iz0 + 1));
		buffer = new BufferedImage(buffW, buffH,
				BufferedImage.TYPE_INT_RGB);
		graphics = buffer.getGraphics();
		DataBufferInt dataBuffer = (DataBufferInt) buffer.getRaster().getDataBuffer();
		data = dataBuffer.getData();
		flushCache();
	}

	/**
	 * Force all visible chunks to be redrawn
	 */
	public void flushCache() {
		graphics.setColor(java.awt.Color.white);
		graphics.fillRect(0, 0, buffW, buffH);
		redrawAllChunks(view);
	}

	/**
	 * Called when this render buffer should buffer another view.
	 * @param newView
	 * @param renderer
	 * @param layer
	 */
	public synchronized void updateView(ChunkView newView, Renderer renderer,
			int layer) {

		boolean bufferedMode = buffMode == renderer
				&& (buffMode != Chunk.layerRenderer || buffLayer == layer);

		if (!newView.equals(view) || !bufferedMode) {

			BufferedImage prev = buffer;
			buffW = (int) (newView.chunkScale * (newView.ix1 - newView.ix0 + 1));
			buffH = (int) (newView.chunkScale * (newView.iz1 - newView.iz0 + 1));
			buffer = new BufferedImage(buffW, buffH,
					BufferedImage.TYPE_INT_RGB);
			graphics = buffer.getGraphics();
			graphics.setColor(java.awt.Color.white);
			graphics.fillRect(0, 0, buffW, buffH);
			DataBufferInt dataBuffer = (DataBufferInt) buffer.getRaster().getDataBuffer();
			data = dataBuffer.getData();

			int ix0 = newView.chunkScale * (view.ix0 - newView.ix0);
			int iz0 = newView.chunkScale * (view.iz0 - newView.iz0);

			if (newView.chunkScale == view.chunkScale)
				graphics.drawImage(prev, ix0, iz0,
						(int) (newView.chunkScale * (view.ix1 - view.ix0 + 1)),
						(int) (newView.chunkScale * (view.iz1 - view.iz0 + 1)), null);
			else
				graphics.drawImage(prev, ix0, iz0, null);

			if (!bufferedMode || view.chunkScale != newView.chunkScale)
				redrawAllChunks(newView);
			else
				redrawNewChunks(newView);
		}

		buffMode = renderer;
		buffLayer = layer;

		view = newView;
		x_offset = (int) (view.chunkScale * (view.ix0 - view.x0));
		y_offset = (int) (view.chunkScale * (view.iz0 - view.z0));
	}

	private synchronized void redrawAllChunks(ChunkView newView) {
		iterator = new ChunkViewIterator(newView);
	}

	private synchronized void redrawNewChunks(ChunkView newView) {
		for (int x = newView.ix0; x <= newView.ix1; ++x) {
			for (int z = newView.iz0; z <= newView.iz1; ++z) {
				if (!view.isChunkVisible(x, z)) {
					iterator.addChunk(ChunkPosition.get(x, z));
				}
			}
		}
	}

	/**
	 * Render the currently buffered view.
	 * @param g
	 */
	public final synchronized void renderBuffered(Graphics g) {
		renderBuffered1(g);
	}

	/**
	 * Debug method
	 * @param g
	 */
	public final synchronized void renderBuffered2(Graphics g) {
		if (buffer != null) {
			graphics.dispose();
			int margin = 200;
			double iw = view.chunkScale * (view.x1 - view.x0);
			double ih = view.chunkScale * (view.z1 - view.z0);
			double xscale = (view.width-margin*2) / iw;
			double yscale = (view.height-margin*2) / ih;
			g.setColor(java.awt.Color.gray);
			g.fillRect(0, 0, view.width, view.height);
			g.drawImage(buffer, (int) (margin + x_offset*xscale),
					(int) (margin + y_offset*yscale),
					(int) (buffW*xscale), (int) (buffH*yscale), null);
			g.setColor(java.awt.Color.red);
			g.drawRect(margin, margin, view.width-margin*2, view.height-margin*2);
			graphics = buffer.getGraphics();
		}
	}

	/**
	 * Default buffer rendering
	 * @param g
	 */
	public final synchronized void renderBuffered1(Graphics g) {
		if (buffer != null) {
			graphics.dispose();
			g.drawImage(buffer, x_offset, y_offset, null);
			graphics = buffer.getGraphics();
		}
	}

	/**
	 * @return The graphics object for this buffer
	 */
	public synchronized Graphics getGraphics() {
		return graphics;
	}

	/**
	 * @return The width of the buffer
	 */
	public synchronized int getWidth() {
		return buffW;
	}

	/**
	 * @return The height of the buffer
	 */
	public synchronized int getHeight() {
		return buffH;
	}

	/**
	 * Set a pixel in the buffer to a specific color
	 * @param x
	 * @param y
	 * @param rgb
	 */
	public synchronized void setRGB(int x, int y, int rgb) {
		data[x + buffW * y] = rgb;
	}

	/**
	 * @param x
	 * @param y
	 * @return The pixel color at (x, y)
	 */
	public synchronized int getRGB(int x, int y) {
		return buffer.getRGB(x, y);
	}

	/**
	 * @return The buffered view
	 */
	public ChunkView getView() {
		return view;
	}

	/**
	 * Save the buffered view as a PNG image
	 * @param targetFile
	 */
	public synchronized void renderPng(File targetFile) {
		try {
			BufferedImage crop = new BufferedImage(view.width, view.height,
					BufferedImage.TYPE_INT_RGB);
			crop.getGraphics().drawImage(buffer, x_offset, y_offset, null);
			ImageIO.write(crop, "png", targetFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return An iterator for chunks that need to be redrawn
	 */
	public synchronized ChunkIterator getChunkIterator() {
		ChunkIterator iter = iterator;
		iterator = new ChunkListIterator();
		return iter;
	}

	/**
	 * Fill rect
	 * @param x0
	 * @param y0
	 * @param w
	 * @param h
	 * @param rgb
	 */
	public void fillRect(int x0, int y0, int w, int h, int rgb) {
		for (int y = 0; y < h; ++y) {
			for (int x = 0; x < w; ++x) {
				data[x0 + x + buffW * (y0 + y)] = rgb;
			}
		}
	}

	/**
	 * Fill rect with alpha blending
	 * @param x0
	 * @param y0
	 * @param w
	 * @param h
	 * @param rgb
	 */
	public void fillRectAlpha(int x0, int y0, int w, int h, int rgb) {
		float[] src = new float[4];
		float[] dst = new float[4];
		se.llbit.math.Color.getRGBAComponents(rgb, src);
		for (int y = 0; y < h; ++y) {
			for (int x = 0; x < w; ++x) {
				se.llbit.math.Color.getRGBComponents(data[x0 + x + buffW * (y0 + y)], dst);
				dst[0] = dst[0] * (1-src[3]) + src[0] * src[3];
				dst[1] = dst[1] * (1-src[3]) + src[1] * src[3];
				dst[2] = dst[2] * (1-src[3]) + src[2] * src[3];
				dst[3] = 1;
				data[x0 + x + buffW * (y0 + y)] = se.llbit.math.Color.getRGB(dst);
			}
		}
	}

	@Override
	public synchronized void chunksUpdated(Collection<ChunkPosition> chunks) {
		for (ChunkPosition chunk: chunks) {
			if (view.isChunkVisible(chunk.x, chunk.z))
				iterator.addChunk(chunk);
		}
	}

	/**
	 * @return <code>true</code> if any of the visible chunks need to
	 * be redrawn
	 */
	public synchronized boolean haveUpdatedChunks() {
		return iterator.hasNext();
	}

	/**
	 * Force the given chunks to be redrawn
	 * @param chunks
	 */
	public synchronized void updateChunks(Collection<ChunkPosition> chunks) {
		for (ChunkPosition chunk: chunks) {
			iterator.addChunk(chunk);
		}
	}

	/**
	 * Force the given chunk to be redrawn
	 * @param cx
	 * @param cz
	 */
	public synchronized void updateChunk(int cx, int cz) {
		iterator.addChunk(ChunkPosition.get(cx, cz));
	}

	/**
	 * Force the chunks within a rectangle to be redrawn
	 * @param x0
	 * @param x1
	 * @param z0
	 * @param z1
	 */
	public synchronized void updateChunks(int x0, int x1, int z0, int z1) {
		for (int x = x0; x <= x1; ++x) {
			for (int z = z0; z <= z1; ++z)
				iterator.addChunk(ChunkPosition.get(x, z));
		}
	}

}
