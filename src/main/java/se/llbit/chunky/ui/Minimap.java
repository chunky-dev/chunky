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
import java.util.Collection;

import javax.swing.JPanel;

import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.map.RenderBuffer;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.ChunkSelectionTracker;
import se.llbit.chunky.world.ChunkView;
import se.llbit.chunky.world.World;
import se.llbit.chunky.world.WorldRenderer;
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
	
	private RenderBuffer renderBuffer;
	private Chunky chunky;
	private ChunkView view;

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
		renderBuffer = new RenderBuffer(view);
		
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
		
		ChunkView mapView = chunky.getMapView();
		
		WorldRenderer renderer = chunky.getWorldRenderer();
		World world = chunky.getWorld();
		ChunkSelectionTracker selection = chunky.getChunkSelection();
		
		renderBuffer.updateView(view, null, 0);
		renderer.renderMinimap(world, renderBuffer, selection);
		renderBuffer.renderBuffered(g);
		
		// draw view rectangle
		g.setColor(Color.orange);
		g.drawRect(
				(int) Math.round(mapView.x0 - view.x0),
				(int) Math.round(mapView.z0 - view.z0),
				(int) Math.round(mapView.width / (float) mapView.chunkScale),
				(int) Math.round(mapView.height / (float) mapView.chunkScale));
		
		// draw North indicator
		g.setFont(font);
		g.setColor(Color.red);
		g.drawString("N", view.width/2-4, 12);
		
		g.setColor(Color.black);
		g.drawString(world.levelName(), 10, view.height-10);
	}

	/**
	 * @return <code>true</code> if some of the visible chunks have been updated since
	 * the render buffer was last rendered
	 */
	public boolean haveUpdatedChunks() {
		return renderBuffer.haveUpdatedChunks();
	}

	/**
	 * Redraw all visible chunks.
	 */
	public void redraw() {
		renderBuffer.flushCache();
		repaint();
	}

	@Override
	public void chunksUpdated(Collection<ChunkPosition> chunks) {
		renderBuffer.chunksUpdated(chunks);
	}
	
	/**
	 * Called when the minimap view has changed.
	 * @param newView
	 */
	public synchronized void viewUpdated(ChunkView newView) {
		view = newView;
		renderBuffer.updateView(view, chunky.getChunkRenderer(),
				chunky.getWorld().currentLayer());
		repaint();
	}

}
