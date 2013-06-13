/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.map.RenderBuffer;
import se.llbit.chunky.resources.MiscImages;

/**
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class WorldRenderer {

	private static final Font font = new Font("Sans serif", Font.BOLD, 11);

	private boolean highlightEnabled;
	private Block hlBlock = Block.DIAMONDORE;
	private Color hlColor = Color.red;

	private boolean mapUpdated = false;

	private void renderEmpty(Graphics g, int width, int height) {
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		g.setFont(new Font("Sans serif", Font.BOLD, 11)); //$NON-NLS-1$
	}

	/**
	 * Render the minimap
	 * @param world
	 * @param renderBuffer
	 * @param selection
	 */
	public void renderMinimap(World world, RenderBuffer renderBuffer,
			ChunkSelectionTracker selection) {

		ChunkView view = renderBuffer.getView();
		int width = view.width;
		int height = view.height;
		Graphics g = renderBuffer.getGraphics();

		if (world.isEmptyWorld()) {
			renderEmpty(g, width, height);
			return;
		}

		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);

		float[] selectionColor = new float[4];
		float[] color = new float[4];
		se.llbit.math.Color.getRGBAComponents(Layer.selectionColor, selectionColor);
		for (int y = 0; y < height; y++) {
			int cz = y + view.iz0;
			for (int x = 0; x < width; x++) {
				int cx = x + view.ix0;
				ChunkPosition pos = ChunkPosition.get(cx, cz);
			    Chunk chunk = world.getChunk(pos);
				if (!chunk.isEmpty()) {
					if (selection.isSelected(pos)) {
						se.llbit.math.Color.getRGBComponents(chunk.avgColor(),
								color);
						color[0] = color[0] * (1-selectionColor[3]) + selectionColor[0] * selectionColor[3];
						color[1] = color[1] * (1-selectionColor[3]) + selectionColor[1] * selectionColor[3];
						color[2] = color[2] * (1-selectionColor[3]) + selectionColor[2] * selectionColor[3];
						color[3] = 1;
						renderBuffer.setRGB(x, y, se.llbit.math.Color.getRGB(color));
					} else {
						renderBuffer.setRGB(x, y, chunk.avgColor());
					}
				}
			}
		}

		if (world.havePlayerPos())
			renderPlayer(world, g, view, true);

		if (world.haveSpawnPos())
			renderSpawn(world, g, view, true);
	}

	/**
	 * Render the map
	 * @param world
	 * @param renderBuffer
	 * @param renderer
	 * @param selection
	 */
	public void render(World world, RenderBuffer renderBuffer,
			Chunk.Renderer renderer, ChunkSelectionTracker selection) {

		int width = renderBuffer.getWidth();
		int height = renderBuffer.getHeight();

		Graphics g = renderBuffer.getGraphics();
		if (world.isEmptyWorld()) {
			renderEmpty(g, width, height);
			return;
		}

		ChunkView view = renderBuffer.getView();

		ChunkIterator iter = renderBuffer.getChunkIterator();
		while (iter.hasNext()) {
			ChunkPosition pos = iter.next();
			int x = pos.x;
			int z = pos.z;
			if (!view.isChunkVisible(x, z))
				continue;

			Chunk chunk = world.getChunk(pos);

			renderer.render(chunk, renderBuffer, x, z);
			if (highlightEnabled)
				chunk.renderHighlight(renderBuffer, x, z,
						hlBlock, hlColor);
			if (selection.isSelected(pos)) {
				renderBuffer.fillRectAlpha(view.chunkScale * (x - view.ix0),
						view.chunkScale * (z - view.iz0), view.chunkScale, view.chunkScale,
						Layer.selectionColor);
			}
		}
	}

	/**
	 * Render overlay icons
	 * @param world
	 * @param chunky
	 * @param g
	 * @param renderBuffer
	 */
	public void renderHUD(World world, Chunky chunky,
			Graphics g, RenderBuffer renderBuffer) {

		boolean loadIndicator = chunky.isLoading();
		Chunk.Renderer renderer = chunky.getChunkRenderer();

		ChunkView view = renderBuffer.getView();

		if (loadIndicator) {
			g.drawImage(MiscImages.clock, view.width-32, 0, 32, 32, null);
		}

		if (world.havePlayerPos()) {
			renderPlayer(world, g, view,
					renderer == Chunk.surfaceRenderer
					|| world.playerLocY() == world.currentLayer());
		}

		if (world.haveSpawnPos()) {
			renderSpawn(world, g, view,
			        renderer == Chunk.surfaceRenderer
			        || world.spawnPosY() == world.currentLayer());
		}

		Chunk hoveredChunk = chunky.getHoveredChunk();
		if (!hoveredChunk.isEmpty()) {

			g.setFont(font);
			g.setColor(Color.white);
			g.drawString("Chunk: " + hoveredChunk.getPosition(),
					5, view.height - 5);
		}
	}

	private void renderPlayer(World world, Graphics g, ChunkView view, boolean sameLayer) {
		double blockScale = view.chunkScale / 16.;
		int px = (int) FastMath.floor(world.playerPosX() * blockScale);
		int pz = (int) FastMath.floor(world.playerPosZ() * blockScale);
		int ppx = px - (int) FastMath.floor(view.x0 * view.chunkScale);
		int ppy = pz - (int) FastMath.floor(view.z0 * view.chunkScale);
		int pw = (int) FastMath.max(8, FastMath.min(16, blockScale * 2));
		ppx = FastMath.min(view.width-pw, FastMath.max(0, ppx-pw/2));
		ppy = FastMath.min(view.height-pw, FastMath.max(0, ppy-pw/2));

		if (sameLayer)
			g.drawImage(MiscImages.face, ppx, ppy, pw, pw, null);
		else
			g.drawImage(MiscImages.face_t, ppx, ppy, pw, pw, null);
	}

	private void renderSpawn(World world, Graphics g, ChunkView view, boolean sameLayer) {
		double blockScale = view.chunkScale / 16.;
		int px = (int) FastMath.floor(world.spawnPosX() * blockScale);
		int pz = (int) FastMath.floor(world.spawnPosZ() * blockScale);
		int ppx = px - (int) FastMath.floor(view.x0 * view.chunkScale);
		int ppy = pz - (int) FastMath.floor(view.z0 * view.chunkScale);
        int pw = (int) FastMath.max(8, FastMath.min(16, blockScale * 2));
        ppx = FastMath.min(view.width-pw, FastMath.max(0, ppx-pw/2));
		ppy = FastMath.min(view.height-pw, FastMath.max(0, ppy-pw/2));

        if (sameLayer)
            g.drawImage(MiscImages.home, ppx, ppy, pw, pw, null);
        else
            g.drawImage(MiscImages.home_t, ppx, ppy, pw, pw, null);
    }

	/**
	 * Set the highlight enable flag
	 * @param value
	 */
	public synchronized void setHighlightEnabled(boolean value) {
		highlightEnabled = value;
	}

	/**
	 * Set the highlight block type
	 * @param bt
	 */
	public synchronized void highlightBlock(Block bt) {
		hlBlock = bt;
	}

	/**
	 * Set the highlight color
	 * @param newColor
	 */
	public synchronized void setHighlightColor(Color newColor) {
		hlColor = newColor;
	}

	/**
	 * @return <code>true</code> if block highlighting is enabled
	 */
	public synchronized boolean isHighlightEnabled() {
		return highlightEnabled;
	}

	/**
	 * @return The current highlighted block type
	 */
	public synchronized Block getHighlightBlock() {
		return hlBlock;
	}

	/**
	 * @return The current highlight color
	 */
	public synchronized Color getHighlightColor() {
		return hlColor;
	}

	/**
	 * @return <code>true</code> if the map has been updated
	 */
	public synchronized boolean mapUpdated() {
		boolean res = mapUpdated;
		mapUpdated = false;
		return res;
	}
}
