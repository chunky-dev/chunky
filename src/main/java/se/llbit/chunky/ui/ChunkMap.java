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
package se.llbit.chunky.ui;
import org.apache.commons.math3.util.FastMath;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.Collection;

import javax.swing.JPanel;

import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.map.RenderBuffer;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.ChunkView;
import se.llbit.chunky.world.World;
import se.llbit.chunky.world.listeners.ChunkUpdateListener;

/**
 * UI component that draws a 2D Minecraft map.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class ChunkMap extends JPanel implements ChunkUpdateListener {

	/**
	 * Default width of the chunk map.
	 */
	public static final int DEFAULT_WIDTH = 800;

	/**
	 * Default height of the chunk map.
	 */
	public static final int DEFAULT_HEIGHT = 600;

	private Chunky chunky;
	private boolean selectRect = false;

	// selection rectangle
	private int rx;
	private int ry;
	private int rw;
	private int rh;

	private RenderBuffer renderBuffer;
	private ChunkView view;

	/**
	 * Mouse listener for the chunk map UI element.
	 */
	public class MapMouseListener implements MouseListener,
			MouseMotionListener, MouseWheelListener {

		private int ox = 0;
		private int oy = 0;
		private boolean dragging = false;

		private void setMotionOrigin(MouseEvent e) {
			ox = e.getX();
			oy = e.getY();
		}

		private void onDrag(MouseEvent e) {
			int dx = ox - e.getX();
			int dy = oy - e.getY();

			if (dx == 0 && dy == 0)
				return;

			if (selectRect || !dragging && chunky.getShiftModifier()) {
				setSelectionRect(ox, oy, -dx, -dy);
				repaint();
			} else {
				if (!dragging && FastMath.abs(dx) < 5 && FastMath.abs(dy) < 5)
					return;
				dragging = true;
				setMotionOrigin(e);
				chunky.viewDragged(dx, dy);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			setMotionOrigin(e);
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3)
				chunky.open3DView();
			else
				setMotionOrigin(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3)
				return;

			if (!selectRect) {
				if (!dragging) {
					int x = e.getX();
					int y = e.getY();
					ChunkView view = chunky.getMapView();
					double scale = (double) view.chunkScale;
					int cx = (int) FastMath.floor(view.x + (x - getWidth()/2) / scale);
					int cz = (int) FastMath.floor(view.z + (y - getHeight()/2) / scale);

					if (view.chunkScale > 1) {
						chunky.selectChunk(cx, cz);
						renderBuffer.updateChunk(cx, cz);
					} else {
						int rx = cx >> 5;
						int rz = cz >> 5;
						chunky.selectChunks(rx*32, (rx+1)*32, rz*32, (rz+1)*32);
						renderBuffer.updateChunks(rx*32, (rx+1)*32, rz*32, (rz+1)*32);
					}
				}

			} else {
				selectWithinRect();
				clearSelectionRect();
			}
			dragging = false;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			onDrag(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			double scale = (double) view.chunkScale;
			int cx = (int) FastMath.floor(view.x + (e.getX() - getWidth()/2) / scale);
			int cz = (int) FastMath.floor(view.z + (e.getY() - getHeight()/2) / scale);
			Chunk prevHovered = chunky.getHoveredChunk();
			chunky.setHoveredChunk(cx, cz);
			Chunk hovered = chunky.getHoveredChunk();
			if (prevHovered != hovered)
				repaint();
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int diff = e.getWheelRotation();

			chunky.onMouseWheelMotion(diff);
		}
	}

	/**
	 * @param chunky
	 */
	public ChunkMap(final Chunky chunky) {
		this.chunky = chunky;

		setMinimumSize(new Dimension(10, 10));
		setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setBackground(Color.white);
		setIgnoreRepaint(false);

		MapMouseListener listener = new MapMouseListener();
		addMouseListener(listener);
		addMouseMotionListener(listener);
		addMouseWheelListener(listener);
		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
			}
			@Override
			public void componentResized(ComponentEvent e) {
				chunky.mapResized(getWidth(), getHeight());
			}
			@Override
			public void componentMoved(ComponentEvent e) {
			}
			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});

		view = chunky.getMapView();
		renderBuffer = new RenderBuffer(view);
	}

	/**
	 * Redraw the map view.
	 */
	public synchronized void paintComponent(Graphics g) {

		super.paintComponent(g);

		World world = chunky.getWorld();

		renderBuffer.updateView(view, chunky.getChunkRenderer(),
				world.currentLayer());
		chunky.getWorldRenderer().render(world, renderBuffer,
						chunky.getChunkRenderer(), chunky.getChunkSelection());
		renderBuffer.renderBuffered(g);
		chunky.getWorldRenderer().renderHUD(world, chunky, g, renderBuffer);
		drawSelectionRect(g);
	}

	protected void setSelectionRect(int ox, int oy, int dx, int dy) {
		rx = FastMath.min(ox, ox+dx);
		ry = FastMath.min(oy, oy+dy);
		rw = FastMath.abs(dx);
		rh = FastMath.abs(dy);
		selectRect = true;
	}

	protected synchronized void selectWithinRect() {
		if (selectRect) {
			ChunkView view = chunky.getMapView();
			int width = getWidth();
			int height = getHeight();
			double scale = (double) view.chunkScale;
			int cx0 = (int) FastMath.floor(view.x + (rx - width/2) / scale);
			int cx1 = (int) FastMath.floor(view.x + (rx - width/2 + rw) / scale);
			int cz0 = (int) FastMath.floor(view.z + (ry - height/2) / scale);
			int cz1 = (int) FastMath.floor(view.z + (ry - height/2 + rh) / scale);

			chunky.selectChunks(cx0, cx1, cz0, cz1);
			renderBuffer.updateChunks(cx0, cx1, cz0, cz1);
		}
	}

	protected void clearSelectionRect() {
		if (selectRect) {
			selectRect = false;
			repaint();
		}
	}

	/**
	 * Draw the selection rectangle
	 * @param g
	 */
	private void drawSelectionRect(Graphics g) {
		if (selectRect) {
			g.setColor(Color.red);
			g.drawRect(rx, ry, rw, rh);
		}
	}

	/**
	 * Render the map to a PNG file
	 * @param targetFile
	 */
	public void renderPng(File targetFile) {
		renderBuffer.renderPng(targetFile);
	}

	@Override
	public void chunksUpdated(Collection<ChunkPosition> chunks) {
		renderBuffer.chunksUpdated(chunks);
	}

	/**
	 * Do a complete redraw of the visible chunks.
	 */
	public void redraw() {
		renderBuffer.flushCache();
		repaint();
	}

	/**
	 * @return <code>true</code> if some of the visible chunks have been updated
	 * since the render buffer was last rendered
	 */
	public boolean haveUpdatedChunks() {
		return renderBuffer.haveUpdatedChunks();
	}

	/**
	 * Called when the map view has changed.
	 * @param newView
	 */
	public synchronized void viewUpdated(ChunkView newView) {
		view = newView;
		renderBuffer.updateView(view, chunky.getChunkRenderer(),
				chunky.getWorld().currentLayer());
		repaint();
	}
}
