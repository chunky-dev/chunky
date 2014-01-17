/* Copyright (c) 2012-2014 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;

import javax.swing.JPanel;

import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.map.RenderBuffer;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.ChunkView;
import se.llbit.chunky.world.EmptyChunk;
import se.llbit.chunky.world.World;
import se.llbit.chunky.world.listeners.ChunkUpdateListener;
import se.llbit.math.QuickMath;

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

	private static final Font font = new Font("Sans serif", Font.BOLD, 11);

	private final Chunky chunky;
	private boolean selectRect = false;

	// selection rectangle
	private int rx;
	private int ry;
	private int rw;
	private int rh;

	private final RenderBuffer renderBuffer;
	private ChunkView view;

	private volatile Chunk hovered = EmptyChunk.INSTANCE;

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
				if (!dragging && Math.abs(dx) < 5 && Math.abs(dy) < 5) {
					return;
				}
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
					double scale = view.chunkScale;
					int cx = (int) QuickMath.floor(view.x + (x - getWidth()/2) / scale);
					int cz = (int) QuickMath.floor(view.z + (y - getHeight()/2) / scale);

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
			double scale = view.chunkScale;
			int cx = (int) QuickMath.floor(view.x + (e.getX() - getWidth()/2) / scale);
			int cz = (int) QuickMath.floor(view.z + (e.getY() - getHeight()/2) / scale);
			Chunk newHovered = chunky.getWorld().getChunk(ChunkPosition.get(cx, cz));
			if (newHovered != hovered) {
				hovered = newHovered;
				repaint();
			}
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

		setView(chunky.getMapView());
		renderBuffer = new RenderBuffer(view);
	}

	/**
	 * Redraw the map view.
	 */
	@Override
	public void paintComponent(Graphics g) {

		// NB lock the lock ordering here is critical!
		// we access ChunkMap via Chunky but here we also need to lock Chunky
		synchronized (chunky) {
		synchronized (this) {
			super.paintComponent(g);

			World world = chunky.getWorld();

			chunky.getWorldRenderer().render(world, renderBuffer,
							chunky.getChunkRenderer(), chunky.getChunkSelection());
			renderBuffer.renderBuffered(g);
			chunky.getWorldRenderer().renderHUD(world, chunky, g, renderBuffer);
			drawSelectionRect(g);
		}
		}
	}

	protected void setSelectionRect(int ox, int oy, int dx, int dy) {
		rx = Math.min(ox, ox+dx);
		ry = Math.min(oy, oy+dy);
		rw = Math.abs(dx);
		rh = Math.abs(dy);
		selectRect = true;
	}

	protected synchronized void selectWithinRect() {
		if (selectRect) {
			ChunkView view = chunky.getMapView();
			int width = getWidth();
			int height = getHeight();
			double scale = view.chunkScale;
			int cx0 = (int) QuickMath.floor(view.x + (rx - width/2) / scale);
			int cx1 = (int) QuickMath.floor(view.x + (rx - width/2 + rw) / scale);
			int cz0 = (int) QuickMath.floor(view.z + (ry - height/2) / scale);
			int cz1 = (int) QuickMath.floor(view.z + (ry - height/2 + rh) / scale);

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
	 * Draw the selection rectangle or chunk hover rectangle.
	 * @param g
	 */
	private void drawSelectionRect(Graphics g) {
		if (selectRect) {
			g.setColor(Color.red);
			g.drawRect(rx, ry, rw, rh);
		} else {
			Chunk hoveredChunk = hovered;
			if (!hoveredChunk.isEmpty()) {

				ChunkPosition cp = hoveredChunk.getPosition();

				g.setFont(font);
				g.setColor(Color.red);
				g.drawString("Chunk: " + cp,
						5, view.height - 5);

				if (view.chunkScale >= 16) {
					int x0 = (int) (view.chunkScale * (cp.x - view.x0));
					int y0 = (int) (view.chunkScale * (cp.z - view.z0));
					int blockScale = view.chunkScale;
					g.drawRect(x0, y0, blockScale, blockScale);
				}
				//g.drawString("Chunk: " + hoveredChunk.getPosition() + ", biome: " + hoveredChunk.biomeAt(),
						//5, view.height - 5);
			}
		}
	}

	/**
	 * Render the map to a PNG file
	 * @param targetFile
	 */
	public void renderPng(File targetFile) {
		renderBuffer.renderPng(targetFile);
	}

	/**
	 * Do a complete redraw of the visible chunks.
	 */
	public void redraw() {
		renderBuffer.flushCache();
		repaint();
	}

	@Override
	public void chunkUpdated(ChunkPosition chunk) {
		renderBuffer.chunkUpdated(chunk);
		repaint();
	}

	@Override
	public void regionUpdated(ChunkPosition region) {
		renderBuffer.regionUpdated(region);
		repaint();
	}

	/**
	 * Called when the map view has changed.
	 * @param newView
	 */
	public synchronized void viewUpdated(ChunkView newView) {
		setView(newView);
		renderBuffer.updateView(view, chunky.getChunkRenderer(),
				chunky.getWorld().currentLayer());
		repaint();
	}

	private void setView(ChunkView newView) {
		view = newView;
	}
}
