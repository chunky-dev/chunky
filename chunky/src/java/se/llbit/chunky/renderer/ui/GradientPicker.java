/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import se.llbit.math.Constants;
import se.llbit.math.Vector4d;

@SuppressWarnings("serial")
abstract public class GradientPicker extends JPanel {

	private static final int MARKER_HEIGHT = 20;
	private static final int MARKER_WIDTH = 7;

	protected final List<Vector4d> gradient = new ArrayList<Vector4d>();
	private BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	private int width = 1;
	private int height = 1;
	private double markerPos = 0;

	protected GradientPicker(final ColorPicker colorPicker) {
		setPreferredSize(new Dimension(300, 40));
		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
			}
			@Override
			public void componentResized(ComponentEvent e) {
				updateGradient();
			}
			@Override
			public void componentMoved(ComponentEvent e) {
			}
			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				setMarkerAt(Math.max(0, Math.min(1, e.getX()/(double)width)));
				onMarkerMoved();
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				colorPicker.onColorEditFinished();
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				setMarkerAt(Math.max(0, Math.min(1, e.getX()/(double)width)));
				onMarkerMoved();
			}
		};
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
	}

	abstract protected void onMarkerMoved();

	protected void setMarkerAt(double position) {
		markerPos = position;
		repaint();
	}

	protected double getPickerValue() {
		return markerPos;
	}

	protected void updateGradient() {
		int newWidth = getWidth();
		int newHeight = getHeight();
		if (newWidth != 0 && newHeight != 0 && isVisible()) {
			width = newWidth;
			height = newHeight;
			image = GradientUI.gradientImage(gradient, width, height);
			repaint();
		}
	}

	protected java.awt.Color getMarkerColor(double pos) {
		int x = 0;
		Vector4d c0 = gradient.get(x);
		Vector4d c1 = gradient.get(x+1);
		double xx = (pos - c0.w) / (c1.w-c0.w);
		while (x+2 < gradient.size() && xx > 1) {
			x += 1;
			c0 = gradient.get(x);
			c1 = gradient.get(x+1);
			xx = (pos - c0.w) / (c1.w-c0.w);
		}
		xx = 0.5*(Math.sin(Math.PI*xx-Constants.HALF_PI)+1);
		double a = 1-xx;
		double b = xx;
		return new java.awt.Color((float) (a*c0.x+b*c1.x), (float) (a*c0.y+b*c1.y), (float) (a*c0.z+b*c1.z), 1.f);
	}

	@Override
	protected void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//Rectangle bounds = g.getClipBounds();
		g.drawImage(image, 0, 0, null);

		// fill color
		g.setColor(java.awt.Color.WHITE);
		int x = Math.min(width-1, (int) (markerPos * width));
		int[] xPoints = { x-MARKER_WIDTH, x, x+MARKER_WIDTH };
		int[] yPoints = { 0, MARKER_HEIGHT, 0 };
		g.fillPolygon(xPoints, yPoints, 3);

		g.setColor(java.awt.Color.BLACK);
		g.drawLine(x-MARKER_WIDTH, 0, x, MARKER_HEIGHT);
		g.drawLine(x, MARKER_HEIGHT, x+MARKER_WIDTH, 0);
		g.drawLine(x-MARKER_WIDTH, 0, x+MARKER_WIDTH, 0);
	}

}
