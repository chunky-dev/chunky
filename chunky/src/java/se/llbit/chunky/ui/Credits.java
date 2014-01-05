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
package se.llbit.chunky.ui;

import java.awt.Toolkit;
import java.net.URL;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * Credits dialog.
 * Displays credits for Chunky contributions.
 */
@SuppressWarnings("serial")
public class Credits extends JDialog {

	/**
	 * Constructor
	 * @param parent
	 */
	public Credits(JFrame parent) {
		super(parent, "Chunky Credits");

		JLabel chunkyIcon = new JLabel();
		URL url = getClass().getResource("/chunky.png");
		if (url != null) {
			chunkyIcon.setIcon(new ImageIcon(
					Toolkit.getDefaultToolkit().getImage(url)));
		}
		JLabel creditsLbl = new JLabel(
			"<html><b>Programming:</b><br>" +
			"Jesper Öqvist<br>" +
			"TOGoS (projection & misc.)<br>" +
			"twirrim (FastMath)<br>" +
			"AEnterprise (scene Zip export)<br>" +
			"<br>" +
			"<b>OSX Bundling:</b><br>" +
			"Ben Phelps<br>" +
			"<br>" +
			"<b>Special Thanks:</b><br>" +
			"oldshoesbro<br>" +
			"SmilyOrg<br>" +
			"r/Minecraft<br>" +
			"<br>" +
			"<b>Tools and Libraries:</b><br>" +
			"Apache log4j<br>" +
			"Apache Commons Math<br>" +
			"JOCL (http://www.jocl.org)<br>" +
			"MWC64X<br>" +
			"Markdown<br>"
		);
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(chunkyIcon)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(creditsLbl)
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addComponent(chunkyIcon)
				.addComponent(creditsLbl)
			)
			.addContainerGap()
		);
		setContentPane(panel);
		pack();
		setLocationRelativeTo(parent);
	}
}
