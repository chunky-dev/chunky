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
package se.llbit.chunky.renderer.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import se.llbit.chunky.renderer.RenderableCanvas;
import se.llbit.chunky.renderer.Renderer;
import se.llbit.util.ProgramProperties;

/**
 * Canvas that draws rendered images.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class RenderCanvas extends JPanel implements RenderableCanvas {
	
	private Renderer renderer;
	
    /**
     * Create new canvas
     */
    public RenderCanvas() {
    	
        setBackground(Color.white);
        // apparently setting the same preferred size twice will cancel all
        // other calls to setPreferredSize
        setPreferredSize(new Dimension(
        		ProgramProperties.getIntProperty("3dcanvas.width", RenderableCanvas.DEFAULT_WIDTH),
        		ProgramProperties.getIntProperty("3dcanvas.height", RenderableCanvas.DEFAULT_HEIGHT)));
        setMinimumSize(new Dimension(100, 100));
        setIgnoreRepaint(false);
    }

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (renderer != null) {
			renderer.drawBufferedImage(g, getWidth(), getHeight());
		}
	}
	
	/**
	 * Set new renderer
	 * @param newRenderer
	 */
	public void setRenderer(Renderer newRenderer) {
		renderer = newRenderer;
		renderer.setBufferFinalization(isShowing());
	}

	/**
	 * Set the update buffer flag for the renderer
	 * @param flag 
	 */
	public void setBufferFinalization(final boolean flag) {
		if (renderer != null) {
			new Thread("Finalization changer") {
				public void run() {
					renderer.setBufferFinalization(flag);
				}
			}.start();
		}
	}
}
