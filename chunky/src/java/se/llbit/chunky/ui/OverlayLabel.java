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
package se.llbit.chunky.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * Label for showing currently hovered chunk and biome information
 * in the 2D map.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class OverlayLabel extends JPanel {
	private final JComponent parentComponent;
	private final JLabel lbl = new JLabel();
	private JLayeredPane layeredPane;
	private final Color errBGColor = new Color(0xfff5bc);
	private boolean showLabel = false;

	public OverlayLabel(JComponent parent) {
		this.add(lbl);
		setOpaque(true);
		setBackground(errBGColor);
		this.parentComponent = parent;
		attachTooltip();
		setVisible(false);
	}

	private void attachTooltip() {
		JRootPane rootPane = parentComponent.getRootPane();
		if (rootPane == null) {
			parentComponent.addAncestorListener(new AncestorListener() {
				@Override
				public void ancestorRemoved(AncestorEvent event) {
				}
				@Override
				public void ancestorMoved(AncestorEvent event) {
				}
				@Override
				public void ancestorAdded(AncestorEvent event) {
					attachTooltip();
					parentComponent.removeAncestorListener(this);
				}
			});
		} else {
			parentComponent.addComponentListener(new ComponentListener() {
				@Override
				public void componentShown(ComponentEvent e) {
				}
				@Override
				public void componentResized(ComponentEvent e) {
					updatePosition();
				}
				@Override
				public void componentMoved(ComponentEvent e) {
					updatePosition();
				}
				@Override
				public void componentHidden(ComponentEvent e) {
				}
			});
			layeredPane = rootPane.getLayeredPane();
			layeredPane.setLayer(this, JLayeredPane.POPUP_LAYER);
			layeredPane.add(this);
			Container container = parentComponent;
			while (container != null) {
				if (container.getParent() instanceof JTabbedPane) {
					container.addComponentListener(new ComponentAdapter() {
						@Override
						public void componentHidden(ComponentEvent e) {
							ancestorComponentHidden();
						}
						@Override
						public void componentShown(ComponentEvent e) {
							ancestorComponentShown();
						}
					});
				}
				container = container.getParent();
			}
			updatePosition();
		}
	}

	private void updatePosition() {
		Dimension parentSize = parentComponent.getSize();
		Dimension size = getPreferredSize();
		Point loc = SwingUtilities.convertPoint(parentComponent,
				getLocation(), this);
		int x = loc.x;
		int y = loc.y+parentSize.height-size.height;
		setBounds(x, y, size.width, size.height);
		super.setVisible(showLabel);
	}

	public void setText(String message) {
		lbl.setText(message);
		showLabel = true;
		if (layeredPane != null) {
			updatePosition();
		}
	}

	protected void ancestorComponentHidden() {
		super.setVisible(false);
	}

	protected void ancestorComponentShown() {
		updatePosition();
	}

	@Override
	public void setVisible(boolean aFlag) {
		super.setVisible(aFlag);
		showLabel = aFlag;
	}
}
