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
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

@SuppressWarnings("serial")
public class ErrorLabel extends JPanel {
	private final JComponent parentComponent;
	private final JLabel lbl;
	private JLayeredPane container;
	private final Color errBGColor = new Color(0xfff5bc);

	public ErrorLabel(JComponent parent) {
		this.lbl = new JLabel("HELLO");
		this.add(lbl);
		setOpaque(true);
		setBackground(errBGColor);
		this.parentComponent = parent;
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
			container = rootPane.getLayeredPane();
			container.setLayer(this, JLayeredPane.POPUP_LAYER);
			container.add(this);
			updatePosition(container);
		}
	}

	private void updatePosition(JComponent container) {
		Dimension containerSize = container.getSize();
		Dimension parentSize = parentComponent.getSize();
		Dimension size = getPreferredSize();
		Point loc = parentComponent.getLocation();
		int x = loc.x+parentSize.width/2-size.width/2;
		int y = loc.y+size.height;
		if (x+size.width >= containerSize.width) {
			x = containerSize.width-size.width;
		}
		setBounds(x, y, size.width, size.height);
	}

	public void setText(String message) {
		lbl.setText(message);
		if (container != null) {
			updatePosition(container);
		}
	}
}
