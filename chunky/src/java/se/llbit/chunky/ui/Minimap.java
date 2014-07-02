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
package se.llbit.chunky.ui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import org.apache.commons.math3.util.FastMath;

import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.map.MapBuffer;
import se.llbit.chunky.map.WorldRenderer;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.ChunkSelectionTracker;
import se.llbit.chunky.world.ChunkView;
import se.llbit.chunky.world.World;
import se.llbit.chunky.world.listeners.ChunkUpdateListener;

/**
 * @author Jesper Öqvist (jesper@llbit.se)
 */
@SuppressWarnings("serial")
public class Minimap extends JPanel implements ChunkUpdateListener {

	/**
	 * Default width of the minimap
	 */
	public static final int DEFAULT_WIDTH = 300;

	/**
	 * Default height of the minimap
	 */
	public static final int DEFAULT_HEIGHT = 150;

	private static final Font font = new Font("Sans serif", Font.BOLD, 11);

	private final MapBuffer mapBuffer;
	private final Chunky chunky;
	private volatile ChunkView view;

	/**
	 * @param parent
	 */
	public Minimap(Chunky parent) {
		this.chunky = parent;
		setBackground(Color.white);
		setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setMinimumSize(new Dimension(100, 100));
		setIgnoreRepaint(false);

		view = chunky.getMinimapView();
		mapBuffer = new MapBuffer(view);

		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
				chunky.moveView(e.getX()-getWidth()/2,
					e.getY()-getHeight()/2);
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
			}
			@Override
			public void componentResized(ComponentEvent e) {
				chunky.minimapResized(getWidth(), getHeight());
			}
			@Override
			public void componentMoved(ComponentEvent e) {
			}
			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// NB lock the lock ordering here is critical!
		// we access ChunkMap via Chunky but here we also need to lock Chunky
		synchronized (chunky) {
		synchronized (this) {
			ChunkView mapView = chunky.getMapView();

			WorldRenderer renderer = chunky.getWorldRenderer();
			World world = chunky.getWorld();
			ChunkSelectionTracker selection = chunky.getChunkSelection();

			renderer.render(world, mapBuffer, Chunk.surfaceRenderer, selection);
			mapBuffer.renderBuffered(g);

			renderer.renderPlayer(world, g, view, true);
			renderer.renderSpawn(world, g, view, true);

			// draw view rectangle
			g.setColor(Color.orange);
			g.drawRect(
					(int) FastMath.round(mapView.x0 - view.x0),
					(int) FastMath.round(mapView.z0 - view.z0),
					FastMath.round(mapView.width / (float) mapView.scale),
					FastMath.round(mapView.height / (float) mapView.scale));

			// draw North indicator
			g.setFont(font);
			g.setColor(Color.red);
			g.drawString("N", view.width/2-4, 12);

			g.setColor(Color.black);
			g.drawString(world.levelName(), 10, view.height-10);
		}
		}
	}

	/**
	 * Redraw all visible chunks.
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
		repaint();
	}

	/**
	 * Called when the minimap view has changed.
	 * @param newView
	 */
	public synchronized void viewUpdated(ChunkView newView) {
		view = newView;
		mapBuffer.updateView(view, null, 0);
		repaint();
	}

}
