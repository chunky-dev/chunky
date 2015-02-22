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
package se.llbit.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

@SuppressWarnings("serial")
public class ErrorLabel extends JPanel implements MouseListener {
	private final JComponent parentComponent;
	private final JLabel lbl = new JLabel();
	private JLayeredPane layeredPane;
	private final Color errBGColor = new Color(0xfff5bc);
	private boolean showLabel = false;

	public ErrorLabel(JComponent parent) {
		this.add(lbl);
		setOpaque(true);
		setBackground(errBGColor);
		this.parentComponent = parent;
		addMouseListener(this);
		attachTooltip();
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
			updatePosition(layeredPane);
		}
	}

	private void updatePosition(JComponent container) {
		Dimension containerSize = container.getSize();
		Dimension parentSize = parentComponent.getSize();
		Dimension size = getPreferredSize();
		Point loc = SwingUtilities.convertPoint(parentComponent,
				getLocation(), this);
		int x = loc.x+parentSize.width/2-size.width/2;
		int y = loc.y+parentSize.height;
		if (x+size.width >= containerSize.width) {
			x = containerSize.width-size.width;
		}
		setBounds(x, y, size.width, size.height);
	}

	public void setText(String message) {
		lbl.setText(message);
		if (layeredPane != null) {
			updatePosition(layeredPane);
		}
	}

	protected void ancestorComponentHidden() {
		super.setVisible(false);
	}

	protected void ancestorComponentShown() {
		super.setVisible(showLabel);
		updatePosition(layeredPane);
	}

	@Override
	public void setVisible(boolean aFlag) {
		super.setVisible(aFlag);
		showLabel = aFlag;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// clicking on the error message dismisses it
		setVisible(false);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
