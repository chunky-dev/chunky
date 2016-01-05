/* Copyright (c) 2012-2015 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer.ui.tabs;

import javax.swing.GroupLayout;
import javax.swing.JLabel;

import se.llbit.chunky.renderer.ui.RenderControls;

public class HelpTab extends RenderControlsTab {
	private static final long serialVersionUID = -1L;

	public HelpTab(RenderControls renderControls) {
		super(renderControls);

		JLabel helpLbl = new JLabel(
				"<html>Render Preview Controls:<br>"
				+ "<b>W</b> move camera forward<br>"
				+ "<b>S</b> move camera backward<br>"
				+ "<b>A</b> strafe camera left<br>"
				+ "<b>D</b> strafe camera right<br>"
				+ "<b>R</b> move camera up<br>"
				+ "<b>F</b> move camera down<br>"
				+ "<b>U</b> toggle fullscreen mode<br>"
				+ "<b>K</b> move camera forward x100<br>"
				+ "<b>J</b> move camera backward x100<br>"
				+ "<br>"
				+ "Holding <b>SHIFT</b> makes the basic movement keys so move "
				+ "1/10th of the normal distance.");

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(helpLbl)
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(helpLbl)
			.addContainerGap()
		);
	}

	@Override
	public void refreshSettings() {
	}

}
