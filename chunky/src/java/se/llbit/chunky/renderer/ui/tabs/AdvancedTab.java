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

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.LayoutStyle.ComponentPlacement;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.OutputMode;
import se.llbit.chunky.renderer.RenderConstants;
import se.llbit.chunky.renderer.ui.RenderControls;
import se.llbit.chunky.renderer.ui.ShutdownAlert;
import se.llbit.chunky.ui.CenteredFileDialog;
import se.llbit.ui.Adjuster;

public class AdvancedTab extends RenderControlsTab {
	private static final long serialVersionUID = -1L;

	private final JComboBox outputMode = new JComboBox(OutputMode.values());
	private final JCheckBox fastFogCB = new JCheckBox("Fast fog");
	private final JButton mergeDumpBtn = new JButton("Merge Render Dump");
	private final JCheckBox shutdownWhenDoneCB = new JCheckBox("Shutdown computer when render completes");

	private final Adjuster rayDepth = new Adjuster(
			"Ray depth",
			"Sets the recursive ray depth",
			1, 25) {
		@Override
		public void valueChanged(double newValue) {
			renderMan.scene().setRayDepth((int) newValue);
		}

		@Override
		public void update() {
			set(renderMan.scene().getRayDepth());
		}
	};

	private final Adjuster numThreads = new Adjuster(
			"Render threads",
			"Number of rendering threads",
			RenderConstants.NUM_RENDER_THREADS_MIN,
			20) {
		{
			setClampMax(false);
		}
		@Override
		public void valueChanged(double newValue) {
			int value = (int) newValue;
			PersistentSettings.setNumThreads(value);
			renderMan.setNumThreads(value);
		}

		@Override
		public void update() {
			set(PersistentSettings.getNumThreads());
		}
	};

	private final Adjuster cpuLoad = new Adjuster(
			"CPU load",
			"CPU load percentage",
			1, 100) {
		@Override
		public void valueChanged(double newValue) {
			int value = (int) newValue;
			PersistentSettings.setCPULoad(value);
			renderMan.setCPULoad(value);
		}

		@Override
		public void update() {
			set(PersistentSettings.getCPULoad());
		}
	};

	private final ActionListener fastFogListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			renderMan.scene().setFastFog(fastFogCB.isSelected());
		}
	};

	public AdvancedTab(RenderControls renderControls) {
		super(renderControls);

		if (!ShutdownAlert.canShutdown()) {
			// Disable the computer shutdown checkbox if we can't shutdown.
			shutdownWhenDoneCB.setEnabled(false);
		}

		JLabel outputModeLbl = new JLabel("Output mode: ");
		outputMode.setModel(new DefaultComboBoxModel(OutputMode.values()));
		outputMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox source = (JComboBox) e.getSource();
				renderMan.scene().setOutputMode((OutputMode) source.getSelectedItem());
			}
		});

		JSeparator sep1 = new JSeparator();
		JSeparator sep2 = new JSeparator();

		fastFogCB.setToolTipText("Enable faster fog algorithm");
		fastFogCB.addActionListener(fastFogListener);

		mergeDumpBtn.setToolTipText("Merge an existing render dump with the current render");
		mergeDumpBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CenteredFileDialog fileDialog =
						new CenteredFileDialog(null,
								"Select Render Dump", FileDialog.LOAD);
				fileDialog.setFilenameFilter(
						new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name) {
								return name.toLowerCase().endsWith(".dump");
							}
						});
				fileDialog.setDirectory(PersistentSettings.
						getSceneDirectory().getAbsolutePath());
				fileDialog.setVisible(true);
				File selectedFile = fileDialog.getSelectedFile();
				if (selectedFile != null) {
					sceneManager().mergeRenderDump(selectedFile);
				}
			}
		});

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addGroup(numThreads.horizontalGroup(layout))
				.addGroup(cpuLoad.horizontalGroup(layout))
				.addComponent(sep1)
				.addGroup(rayDepth.horizontalGroup(layout))
				.addComponent(sep2)
				.addComponent(mergeDumpBtn)
				.addComponent(shutdownWhenDoneCB)
				.addComponent(fastFogCB)
				.addGroup(layout.createSequentialGroup()
					.addComponent(outputModeLbl)
					.addComponent(outputMode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			)
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(numThreads.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(cpuLoad.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(sep1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(rayDepth.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(sep2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(mergeDumpBtn)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(shutdownWhenDoneCB)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(fastFogCB)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(outputModeLbl)
					.addComponent(outputMode))
			.addContainerGap()
		);
	}

	@Override
	public void refreshSettings() {
		updateOutputMode();
		updateFastFog();
		rayDepth.update();
		cpuLoad.update();
		numThreads.update();
	}

	protected void updateOutputMode() {
		outputMode.setSelectedItem(renderMan.scene().getOutputMode());
	}

	protected void updateFastFog() {
		fastFogCB.removeActionListener(fastFogListener);
		fastFogCB.setSelected(renderMan.scene().fastFog());
		fastFogCB.addActionListener(fastFogListener);
	}

	public boolean shutdownAfterCompletedRender() {
		return shutdownWhenDoneCB.isSelected();
	}
}
