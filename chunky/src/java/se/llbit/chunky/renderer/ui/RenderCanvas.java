/* Copyright (c) 2012-2016 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.RenderableCanvas;
import se.llbit.chunky.renderer.Renderer;

/**
 * Canvas that draws rendered images.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class RenderCanvas extends JPanel implements RenderableCanvas {

	private Renderer renderer;
	private int preferredWidth = 200;
	private int preferredHeight = 200;

	public RenderCanvas() {
		setBackground(Color.black);

		// It seems like setting the same preferred size twice will cancel all
		// other calls to setPreferredSize. This was observed on Windows 7,
		// does it behave differently on different platforms? /llbit
		setPreferredSize(PersistentSettings.get3DCanvasWidth(),
				PersistentSettings.get3DCanvasHeight(), 1);
		setMinimumSize(new Dimension(100, 100));
		setIgnoreRepaint(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int width = getWidth();
		int height = getHeight();
		int offsetX = Math.max(width - preferredWidth, 0) / 2;
		int offsetY = Math.max(height - preferredHeight, 0) / 2;
		if (renderer != null) {
			renderer.drawBufferedImage(g, offsetX, offsetY, preferredWidth, preferredHeight);
		}
	}

	public void setRenderer(Renderer newRenderer) {
		renderer = newRenderer;
		renderer.setBufferFinalization(isShowing());
	}

	/**
	 * Set the update buffer flag for the current renderer.
	 * @param flag
	 */
	public void setBufferFinalization(final boolean flag) {
		if (renderer != null) {
			new Thread("Finalization changer") {
				@Override
				public void run() {
					renderer.setBufferFinalization(flag);
				}
			}.start();
		}
	}

	public void setPreferredSize(int width, int height, double scale) {
		preferredWidth = (int) (width * scale);
		preferredHeight = (int) (height * scale);
		setPreferredSize(new Dimension(preferredWidth, preferredHeight));
	}
}
