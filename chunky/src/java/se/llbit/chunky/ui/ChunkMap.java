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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.map.MapBuffer;
import se.llbit.chunky.renderer.ui.RenderControls;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.ChunkView;
import se.llbit.chunky.world.Icon;
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

	protected final Chunky chunky;

	/**
	 * Indicates whether or not the selection rectangle should be drawn.
	 */
	protected volatile boolean selectRect = false;

	protected volatile boolean mouseInsideWindow = false;

	private final MapBuffer mapBuffer;
	private volatile ChunkView view;
	private final OverlayLabel mapLabel = new OverlayLabel(this);
	private final JPopupMenu contextMenu = new JPopupMenu();

	private volatile ChunkPosition start = ChunkPosition.get(0, 0);
	private volatile ChunkPosition end = ChunkPosition.get(0, 0);

	public int lastX;

	public int lastY;

	/**
	 * Mouse listener for the chunk map UI element.
	 */
	public class MapMouseListener implements MouseListener,
			MouseMotionListener, MouseWheelListener {

		private int ox = 0;
		private int oy = 0;
		private boolean dragging = false;
		private boolean mouseDown = false;

		private void setMotionOrigin(MouseEvent e) {
			ox = e.getX();
			oy = e.getY();
		}

		private void onDrag(MouseEvent e) {
			int dx = ox - e.getX();
			int dy = oy - e.getY();

			if (dx == 0 && dy == 0) {
				return;
			}

			ChunkPosition chunk = getChunk(e);
			if (chunk != end) {
				end = chunk;
				repaint();
			}

			if (selectRect || !dragging && chunky.getShiftModifier()) {
				selectRect = true;
			} else if (dragging || Math.abs(dx) >= 5 || Math.abs(dy) >= 5) {
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
			mouseInsideWindow = true;
			setMotionOrigin(e);
			repaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			mouseInsideWindow = false;
			mapLabel.setVisible(false);
			repaint();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				lastX = e.getX();
				lastY = e.getY();
				contextMenu.show((Component) e.getSource(), e.getX(), e.getY());
			} else {
				setMotionOrigin(e);
				mouseDown = true;
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (!mouseDown) {
				return;
			}
			mouseDown = false;

			if (!selectRect) {
				if (!dragging) {
					int x = e.getX();
					int y = e.getY();
					ChunkView theView = chunky.getMapView();
					double scale = theView.scale;
					int cx = (int) QuickMath.floor(theView.x + (x - getWidth()/2) / scale);
					int cz = (int) QuickMath.floor(theView.z + (y - getHeight()/2) / scale);

					if (theView.scale >= 16) {
						chunky.toggleChunkSelection(cx, cz);
						mapBuffer.updateChunk(cx, cz);
					} else {
						chunky.selectRegion(cx, cz);
						mapBuffer.updateRegion(cx, cz);
					}
				}

			} else {
				selectWithinRect();
				clearSelectionRect();
			}
			start = end;
			dragging = false;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (mouseDown == true) {
				onDrag(e);
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			ChunkPosition chunk = getChunk(e);
			if (chunk != start) {
				start = chunk;
				end = chunk;
				repaint();
			}
		}

		private ChunkPosition getChunk(MouseEvent e) {
			ChunkView theView = view;
			double scale = theView.scale;
			double x = theView.x + (e.getX() - getWidth()/2) / scale;
			double z = theView.z + (e.getY() - getHeight()/2) / scale;
			int cx = (int) QuickMath.floor(x);
			int cz = (int) QuickMath.floor(z);
			int bx = (int) QuickMath.floor((x-cx)*16);
			int bz = (int) QuickMath.floor((z-cz)*16);
			bx = Math.max(0, Math.min(Chunk.X_MAX-1, bx));
			bz = Math.max(0, Math.min(Chunk.Z_MAX-1, bz));
			ChunkPosition cp = ChunkPosition.get(cx, cz);
			Chunk hoveredChunk = chunky.getWorld().getChunk(cp);
			if (!hoveredChunk.isEmpty()) {
				mapLabel.setText(String.format("%s, biome: %s",
						""+hoveredChunk.toString(),
						hoveredChunk.biomeAt(bx, bz)));
			} else {
				mapLabel.setText(hoveredChunk.toString());
			}
			return cp;
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

		JMenuItem createScene = new JMenuItem("New 3D scene...");
		createScene.setIcon(Icon.sky.imageIcon());
		createScene.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chunky.open3DView();
			}
		});
		JMenuItem loadScene = new JMenuItem("Load scene...");
		loadScene.setIcon(Icon.load.imageIcon());
		loadScene.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chunky.loadScene();
			}
		});
		JMenuItem clearSelection = new JMenuItem("Clear selection");
		clearSelection.setIcon(Icon.clear.imageIcon());
		clearSelection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chunky.clearSelectedChunks();
			}
		});
		JMenuItem moveCameraHere = new JMenuItem("Move camera here");
		moveCameraHere.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO atomic view copy
				ChunkView theView = new ChunkView(view);
				double scale = theView.scale;
				double x = theView.x + (lastX - getWidth()/2) / scale;
				double z = theView.z + (lastY - getHeight()/2) / scale;
				chunky.moveCameraTo(x*16, z*16);
			}
		});
		JMenuItem selectVisible = new JMenuItem("Select visible chunks");
		selectVisible.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RenderControls controls = chunky.getRenderControls();
				if (controls != null && controls.isVisible()) {
					// TODO atomic view copy
					ChunkView theView = new ChunkView(view);
					controls.selectVisibleChunks(theView, chunky);
				}
			}
		});
		contextMenu.add(createScene);
		contextMenu.add(loadScene);
		contextMenu.addSeparator();
		contextMenu.add(clearSelection);
		contextMenu.add(moveCameraHere);
		contextMenu.add(selectVisible);

		setView(chunky.getMapView());
		mapBuffer = new MapBuffer(view);
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

			chunky.getWorldRenderer().render(world, mapBuffer,
							chunky.getChunkRenderer(), chunky.getChunkSelection());
			mapBuffer.renderBuffered(g);
			chunky.getWorldRenderer().renderHUD(world, chunky, g, mapBuffer);
			drawSelectionRect(g);
			drawViewBounds(g);
		}
		}
	}

	private void drawViewBounds(Graphics g) {
		RenderControls controls = chunky.getRenderControls();
		if (controls != null && controls.isVisible()) {
			controls.drawViewBounds(g, view);
		}
	}

	protected synchronized void selectWithinRect() {
		if (selectRect) {
			ChunkPosition cp0 = start;
			ChunkPosition cp1 = end;
			int x0 = Math.min(cp0.x, cp1.x);
			int x1 = Math.max(cp0.x, cp1.x);
			int z0 = Math.min(cp0.z, cp1.z);
			int z1 = Math.max(cp0.z, cp1.z);
			chunky.selectChunks(x0, x1, z0, z1);
			mapBuffer.updateChunks(x0, x1, z0, z1);
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
		if (!mouseInsideWindow) {
			return;
		}
		ChunkView cv = view;

		ChunkPosition cp = end;
		g.setFont(font);
		g.setColor(Color.red);

		if (selectRect) {
			ChunkPosition cp0 = start;
			ChunkPosition cp1 = end;
			int x0 = Math.min(cp0.x, cp1.x);
			int x1 = Math.max(cp0.x, cp1.x);
			int z0 = Math.min(cp0.z, cp1.z);
			int z1 = Math.max(cp0.z, cp1.z);
			x0 = (int) (cv.scale * (x0 - cv.x0));
			z0 = (int) (cv.scale * (z0 - cv.z0));
			x1 = (int) (cv.scale * (x1 - cv.x0 + 1));
			z1 = (int) (cv.scale * (z1 - cv.z0 + 1));
			g.drawRect(x0, z0, x1-x0, z1-z0);
		} else {
			// test if hovered chunk is visible
			if (cv.isChunkVisible(cp)) {

				if (cv.scale >= 16) {
					int x0 = (int) (cv.scale * (cp.x - cv.x0));
					int y0 = (int) (cv.scale * (cp.z - cv.z0));
					int blockScale = cv.scale;
					g.drawRect(x0, y0, blockScale, blockScale);
				} else {
					// hovered region
					int rx = cp.x >> 5;
					int rz = cp.z >> 5;
					int x0 = (int) (cv.scale * (rx*32 - cv.x0));
					int y0 = (int) (cv.scale * (rz*32 - cv.z0));
					g.drawRect(x0, y0, cv.scale*32, cv.scale*32);
				}
			}
		}
	}

	/**
	 * Render the map to a PNG file
	 * @param targetFile
	 */
	public void renderPng(File targetFile) {
		mapBuffer.renderPng(targetFile);
	}

	/**
	 * Do a complete redraw of the visible chunks.
	 */
	public void redraw() {
		mapBuffer.flushCache();
		repaint();
	}

	@Override
	public void chunkUpdated(ChunkPosition chunk) {
		mapBuffer.chunkUpdated(chunk);
		repaint();
	}

	@Override
	public void regionUpdated(ChunkPosition region) {
		mapBuffer.regionUpdated(region);
		chunky.regionUpdated(region);
		repaint();
	}

	/**
	 * Called when the map view has changed.
	 * @param newView
	 */
	public synchronized void viewUpdated(ChunkView newView) {
		setView(newView);
		mapBuffer.updateView(view, chunky.getChunkRenderer(),
				chunky.getWorld().currentLayer());
		repaint();
	}

	private void setView(ChunkView newView) {
		view = newView;
	}
}
