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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import se.llbit.chunky.renderer.Postprocess;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.ui.RenderControls;
import se.llbit.ui.Adjuster;

public class PostProcessingTab extends RenderControlsTab {

	private final JComboBox postprocessCB = new JComboBox(Postprocess.values());

	private final Adjuster exposure = new Adjuster(
			"exposure",
			"exposure",
			Scene.MIN_EXPOSURE,
			Scene.MAX_EXPOSURE) {
		{
			setLogarithmicMode();
		}
		@Override
		public void valueChanged(double newValue) {
			renderMan.scene().setExposure(newValue);
		}

		@Override
		public void update() {
			set(renderMan.scene().getExposure());
		}
	};

	public PostProcessingTab(RenderControls renderControls) {
		super(renderControls);

		exposure.update();

		JLabel postprocessDescLbl = new JLabel("<html>Post processing affects rendering performance<br>when the preview window is visible");
		JLabel postprocessLbl = new JLabel("Post-processing mode:");
		updatePostprocessCB();
		postprocessCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox source = (JComboBox) e.getSource();
				renderMan.scene().setPostprocess((Postprocess) source.getSelectedItem());
			}
		});

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addGroup(exposure.horizontalGroup(layout))
				.addGroup(layout.createSequentialGroup()
					.addComponent(postprocessLbl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(postprocessCB)
				)
				.addComponent(postprocessDescLbl)
			)
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(exposure.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(postprocessLbl)
				.addComponent(postprocessCB)
			)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(postprocessDescLbl)
			.addContainerGap()
		);
	}

	@Override
	public void refreshSceneData() {
		exposure.update();
		updatePostprocessCB();
	}

	protected void updatePostprocessCB() {
		postprocessCB.setSelectedItem(renderMan.scene().getPostprocess());
	}

}
