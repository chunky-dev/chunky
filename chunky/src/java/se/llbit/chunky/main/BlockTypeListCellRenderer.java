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
package se.llbit.chunky.main;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import se.llbit.chunky.world.Block;

/**
 * Renderer for cells in a block type list.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class BlockTypeListCellRenderer extends JLabel implements ListCellRenderer {

	private static final long serialVersionUID = -4916737793036321488L;

	/**
	 * Create new list cell renderer
	 */
	public BlockTypeListCellRenderer() {
		setOpaque(true);
		setHorizontalAlignment(LEFT);
		setVerticalAlignment(CENTER);
		setPreferredSize(new Dimension(200, 34));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean hasFocus) {

		Block selected = ((Block) value);

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		ImageIcon icon = new ImageIcon(selected.getTexture().getImage());
		setIcon(icon);
		setText(selected.toString());
		setFont(list.getFont());

		return this;
	}

}
