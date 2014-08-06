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

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JPanel;

import se.llbit.math.Constants;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

@SuppressWarnings("serial")
public class GradientUI extends JPanel {

	private final List<Vector4d> gradient = new ArrayList<Vector4d>();
	private BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	private int width = 1;
	private int height = 1;
	private int selected = 0;

	private final Collection<GradientListener> listeners = new ArrayList<GradientListener>();

	public GradientUI(Collection<Vector4d> gradient) {
		if (gradient.size() < 2) {
			throw new IllegalArgumentException("Too few gradient stops!");
		}
		this.gradient.addAll(gradient);

		setPreferredSize(new Dimension(400, 60));
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
				double x = e.getX() / (double) width;
				closestStop(x);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				moveStop(e.getX() / (double)width);
			}
		};
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
	}

	/**
	 * Helper method to create a gradient image.
	 * @param gradient
	 * @param width
	 * @param height
	 * @return gradiant image
	 */
	public static final BufferedImage gradientImage(List<Vector4d> gradient, int width, int height) {
		if (width <= 0 || height <= 0 || gradient.size() < 2) {
			throw new IllegalArgumentException();
		}
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int x = 0;
		for (int i = 0; i < width; ++i) {
			double weight = i/(double)width;
			Vector4d c0 = gradient.get(x);
			Vector4d c1 = gradient.get(x+1);
			double xx = (weight - c0.w) / (c1.w-c0.w);
			while (x+2 < gradient.size() && xx > 1) {
				x += 1;
				c0 = gradient.get(x);
				c1 = gradient.get(x+1);
				xx = (weight - c0.w) / (c1.w-c0.w);
			}
			xx = 0.5*(Math.sin(Math.PI*xx-Constants.HALF_PI)+1);
			double a = 1-xx;
			double b = xx;
			int argb = se.llbit.math.Color.getRGBA(a*c0.x+b*c1.x, a*c0.y+b*c1.y, a*c0.z+b*c1.z, 1);
			for (int j = 0; j < height; ++j) {
				image.setRGB(i, j, argb);
			}
		}
		return image;
	}

	public static final BufferedImage gradientImageLinear(List<Vector4d> gradient, int width, int height) {
		if (width <= 0 || height <= 0 || gradient.size() < 2) {
			throw new IllegalArgumentException();
		}
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int x = 0;
		for (int i = 0; i < width; ++i) {
			double weight = i/(double)width;
			Vector4d c0 = gradient.get(x);
			Vector4d c1 = gradient.get(x+1);
			double xx = (weight - c0.w) / (c1.w-c0.w);
			while (x+2 < gradient.size() && xx > 1) {
				x += 1;
				c0 = gradient.get(x);
				c1 = gradient.get(x+1);
				xx = (weight - c0.w) / (c1.w-c0.w);
			}
			double a = 1-xx;
			double b = xx;
			int argb = se.llbit.math.Color.getRGBA(a*c0.x+b*c1.x, a*c0.y+b*c1.y, a*c0.z+b*c1.z, 1);
			for (int j = 0; j < height; ++j) {
				image.setRGB(i, j, argb);
			}
		}
		return image;
	}

	public void updateGradient() {
		int newWidth = getWidth();
		int newHeight = getHeight();
		if (newWidth > 0 && newHeight > 0) {
			width = newWidth;
			height = newHeight;
			image = gradientImage(gradient, width, height);
			int x = 0;
			if (gradient.size() < 2) {
				return;
			}
			for (int i = 0; i < newWidth; ++i) {
				double weight = i/(double)newWidth;
				Vector4d c0 = gradient.get(x);
				Vector4d c1 = gradient.get(x+1);
				double xx = (weight - c0.w) / (c1.w-c0.w);
				while (x+2 < gradient.size() && xx > 1) {
					x += 1;
					c0 = gradient.get(x);
					c1 = gradient.get(x+1);
					xx = (weight - c0.w) / (c1.w-c0.w);
				}
				xx = 0.5*(Math.sin(Math.PI*xx-Constants.HALF_PI)+1);
				double a = 1-xx;
				double b = xx;
				int argb = se.llbit.math.Color.getRGBA(a*c0.x+b*c1.x, a*c0.y+b*c1.y, a*c0.z+b*c1.z, 1);
				for (int j = 0; j < newHeight; ++j) {
					image.setRGB(i, j, argb);
				}
			}
		}
	}

	private static Stroke thickLine = new BasicStroke(2);
	private static java.awt.Color lightGray = new java.awt.Color(220, 220, 220);

	@Override
	protected void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//Rectangle bounds = g.getClipBounds();
		g.drawImage(image, 0, 0, null);
		g.setColor(java.awt.Color.WHITE);
		g.fillRect(0, 0, width, 15);
		int min = 0, max = width;
		if (selected > 0) {
			min = (int) (gradient.get(selected-1).w * width);
		}
		if (selected < gradient.size()-1) {
			max = (int) (gradient.get(selected+1).w * width);
		}
		g.setColor(lightGray);
		g.fillRect(min, 0, max-min, 15);
		int index = 0;
		for (Vector4d stop: gradient) {
			if (index == selected) {
				index += 1;
				continue;
			}

			// fill color
			g.setColor(java.awt.Color.WHITE);
			int x = Math.min(width-1, (int) (stop.w*width));
			int[] xPoints = { x-5, x, x+5 };
			int[] yPoints = { 0, 15, 0 };
			g.fillPolygon(xPoints, yPoints, 3);

			// border color
			boolean isEndpoint = index == 0 || index == gradient.size()-1;
			if (isEndpoint) {
				g.setColor(java.awt.Color.GRAY);
			} else {
				g.setColor(java.awt.Color.BLACK);
			}
			g.drawLine(x-5, 0, x, 15);
			g.drawLine(x, 15, x+5, 0);
			g.drawLine(x-5, 0, x+5, 0);
			index += 1;
		}
		g.setStroke(thickLine);
		Vector4d stop = gradient.get(selected);
		// fill color
		boolean isEndpoint = selected == 0 || selected == gradient.size()-1;
		if (isEndpoint) {
			g.setColor(java.awt.Color.GRAY);
		} else {
			g.setColor(java.awt.Color.BLACK);
		}
		int x = Math.min(width-1, (int) (stop.w*width));
		int[] xPoints = { x-5, x, x+5 };
		int[] yPoints = { 0, 15, 0 };
		g.fillPolygon(xPoints, yPoints, 3);

		// border color
		g.setColor(java.awt.Color.WHITE);
		g.drawLine(x-5, 0, x, 15);
		g.drawLine(x, 15, x+5, 0);
		g.drawLine(x-5, 0, x+5, 0);
		index += 1;
	}

	protected void moveStop(double d) {
		if (selected > 0 && selected < gradient.size()-1) {
			double min = gradient.get(selected-1).w;
			double max = gradient.get(selected+1).w;
			gradient.get(selected).w = Math.max(min, Math.min(max, d));
			gradientChanged();
			repaint();
			fireStopModifiedNotification();
		}
	}

	private synchronized void fireStopModifiedNotification() {
		for (GradientListener listener: listeners) {
			listener.stopModified(selected, gradient.get(selected));
		}
	}

	private void gradientChanged() {
		updateGradient();
		repaint();
		fireGradientChangedNotification();
	}

	private synchronized void fireGradientChangedNotification() {
		for (GradientListener listener: listeners) {
			listener.gradientChanged(gradient);
		}

	}

	protected int closestStop(double x) {
		double closest = Double.MAX_VALUE;
		int stop = 0;
		int index = 0;
		for (Vector4d m: gradient) {
			double distance = Math.abs(m.w - x);
			if (distance < closest) {
				stop = index;
				closest = distance;
			}
			index += 1;
		}
		setSelectedIndex(stop);
		return stop;
	}

	public synchronized void addGradientListener(GradientListener listener) {
		this.listeners.add(listener);
	}

	public synchronized void removeGradientListener(GradientListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * Set selected gradient stop.
	 * @param index
	 */
	public void setSelectedIndex(int index) {
		if (index >= 0 && index < gradient.size()) {
			selected = index;
			repaint();
			fireStopSelectedNotification();
		}
	}

	private synchronized void fireStopSelectedNotification() {
		for (GradientListener listener: listeners) {
			listener.stopSelected(selected);
		}
	}

	/**
	 * @return currently selected stop index
	 */
	public int getSelctedIndex() {
		return selected;
	}

	/**
	 * @return number of gradient stops
	 */
	public int getNumStop() {
		return gradient.size();
	}

	/**
	 * @param index
	 * @return stop with index 'index'
	 */
	public Vector4d getStop(int index) {
		return gradient.get(index);
	}

	public void setColor(Vector3d newColor) {
		Vector4d stop = gradient.get(selected);
		stop.x = newColor.x;
		stop.y = newColor.y;
		stop.z = newColor.z;
		gradientChanged();
	}

	public void setPosition(double newPosition) {
		Vector4d stop = gradient.get(selected);
		stop.w = newPosition;
		gradientChanged();
	}

	public void addStop() {
		int i0;
		int i1;
		if (selected == gradient.size()-1) {
			i0 = selected-1;
			i1 = selected;
		} else {
			i0 = selected;
			i1 = selected+1;
		}
		gradient.add(i1, blend(gradient.get(i0), gradient.get(i1), 0.5));
		selected = i1;
		fireStopSelectedNotification();
		gradientChanged();
	}

	private Vector4d blend(Vector4d s0, Vector4d s1, double d) {
		double xx = 0.5*(Math.sin(Math.PI*d-Constants.HALF_PI)+1);
		double a = 1-xx;
		double b = xx;
		return new Vector4d(a*s0.x+b*s1.x, a*s0.y+b*s1.y, a*s0.z+b*s1.z, a*s0.w+b*s1.w);
	}

	public void removeStop() {
		if (gradient.size() > 2) {
			gradient.remove(selected);
			if (selected == 0) {
				gradient.get(0).w = 0;
			} else if (selected == gradient.size()) {
				gradient.get(selected-1).w = 1;
			}
			if (selected > 0) {
				selected -= 1;
			}
			fireStopSelectedNotification();
			gradientChanged();
		}
	}

	public void setGradient(List<Vector4d> newGradient) {
		gradient.clear();
		gradient.addAll(newGradient);
		gradientChanged();
	}

	public Collection<Vector4d> getGradient() {
		return gradient;
	}

}
