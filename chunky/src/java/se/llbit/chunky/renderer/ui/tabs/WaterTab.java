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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.ui.ColorListener;
import se.llbit.chunky.renderer.ui.ColorPicker;
import se.llbit.chunky.renderer.ui.RenderControls;
import se.llbit.chunky.world.Icon;
import se.llbit.chunky.world.World;
import se.llbit.math.Vector3d;
import se.llbit.ui.Adjuster;

public class WaterTab extends RenderControlsTab {

	private final JCheckBox stillWaterCB = new JCheckBox();
	private final JTextField waterHeightField = new JTextField();
	private final JButton applyWaterHeightBtn = new JButton("Apply");
	private final JCheckBox waterWorldCB = new JCheckBox();
	private final JCheckBox waterColorCB = new JCheckBox("Use custom water color");
	private final JButton waterColorBtn = new JButton("Change Water Color");

	private final Adjuster waterVisibility = new Adjuster(
			"Water Visibility",
			"Visibility depth under water",
			0.0, 20.0) {
		{
			setClampMax(false);
		}
		@Override
		public void valueChanged(double newValue) {
			renderMan.scene().setWaterVisibility(newValue);
		}

		@Override
		public void update() {
			set(renderMan.scene().getWaterVisibility());
		}
	};

	private final Adjuster waterOpacity = new Adjuster(
			"Water Opacity",
			"Decides how opaque the water surface appears",
			0.0, 1.0) {
		@Override
		public void valueChanged(double newValue) {
			renderMan.scene().setWaterOpacity(newValue);
		}

		@Override
		public void update() {
			set(renderMan.scene().getWaterOpacity());
		}
	};

	private final ActionListener stillWaterListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			renderMan.scene().setStillWater(stillWaterCB.isSelected());
		}
	};

	private final ActionListener waterHeightListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				int waterHeight = Integer.parseInt(waterHeightField.getText());
				renderMan.scene().setWaterHeight(waterHeight);
				sceneManager().reloadChunks();
				updateWaterHeight();
			} catch (Error thrown) {
				// ignore number format exceptions
			}
		}
	};

	private final ActionListener waterWorldListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox source = (JCheckBox) e.getSource();
			if (source.isSelected()) {
				renderMan.scene().setWaterHeight(
						Integer.parseInt(waterHeightField.getText()));
			} else {
				waterHeightField.removeActionListener(waterHeightListener);
				waterHeightField.setText("" + renderMan.scene().getWaterHeight());
				waterHeightField.addActionListener(waterHeightListener);
				renderMan.scene().setWaterHeight(0);
			}
			sceneManager().reloadChunks();
			updateWaterHeight();
		}
	};

	private final ActionListener customWaterColorListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox source = (JCheckBox) e.getSource();
			boolean useCustomWaterColor = source.isSelected();
			if (useCustomWaterColor) {
				renderMan.scene().setWaterColor(new Vector3d(
						PersistentSettings.DEFAULT_WATER_RED,
						PersistentSettings.DEFAULT_WATER_GREEN,
						PersistentSettings.DEFAULT_WATER_BLUE));
			}
			renderMan.scene().setUseCustomWaterColor(useCustomWaterColor);
			updateWaterColor();
		}
	};

	public WaterTab(RenderControls renderControls) {
		super(renderControls);

		JButton storeDefaultsBtn = new JButton("Store as defaults");
		storeDefaultsBtn.setToolTipText("Store the current water settings as new defaults");
		storeDefaultsBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PersistentSettings.setStillWater(renderMan.scene().stillWaterEnabled());
				PersistentSettings.setWaterOpacity(renderMan.scene().getWaterOpacity());
				PersistentSettings.setWaterVisibility(renderMan.scene().getWaterVisibility());
				PersistentSettings.setWaterHeight(renderMan.scene().getWaterHeight());
				boolean useCustomWaterColor = renderMan.scene().getUseCustomWaterColor();
				PersistentSettings.setUseCustomWaterColor(useCustomWaterColor);
				if (useCustomWaterColor) {
					Vector3d color = renderMan.scene().getWaterColor();
					PersistentSettings.setWaterColor(color.x, color.y, color.z);
				}
			}
		});

		stillWaterCB.setText("still water");
		stillWaterCB.addActionListener(stillWaterListener);
		updateStillWater();

		waterVisibility.update();
		waterOpacity.update();

		JLabel waterWorldLbl = new JLabel(
				"Note: All chunks will be reloaded after changing the water world options!");
		JLabel waterHeightLbl = new JLabel("Water height: ");
		waterHeightField.setColumns(5);
		waterHeightField.setText("" + World.SEA_LEVEL);
		waterHeightField.setEnabled(renderMan.scene().getWaterHeight() != 0);
		waterHeightField.addActionListener(waterHeightListener);

		applyWaterHeightBtn.setToolTipText("Use this water height");
		applyWaterHeightBtn.addActionListener(waterHeightListener);

		waterWorldCB.setText("Water World Mode");
		waterWorldCB.addActionListener(waterWorldListener);
		updateWaterHeight();

		waterColorCB.addActionListener(customWaterColorListener);
		updateWaterColor();

		waterColorBtn.setIcon(Icon.colors.imageIcon());
		waterColorBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ColorPicker picker = new ColorPicker(waterColorBtn, renderMan.scene().getWaterColor());
				picker.addColorListener(new ColorListener() {
					@Override
					public void onColorPicked(Vector3d color) {
						renderMan.scene().setWaterColor(color);
					}
				});
			}
		});

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addComponent(stillWaterCB)
				.addGroup(waterVisibility.horizontalGroup(layout))
				.addGroup(waterOpacity.horizontalGroup(layout))
				.addComponent(waterWorldLbl)
				.addComponent(waterWorldCB)
				.addGroup(layout.createSequentialGroup()
					.addComponent(waterHeightLbl)
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addComponent(waterHeightField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(applyWaterHeightBtn)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(waterColorCB)
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addComponent(waterColorBtn)
				)
				.addGroup(layout.createSequentialGroup()
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addComponent(storeDefaultsBtn)
				)
			)
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(stillWaterCB)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(waterVisibility.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(waterOpacity.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(waterWorldLbl)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(waterWorldCB)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(waterHeightLbl)
				.addComponent(waterHeightField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(applyWaterHeightBtn)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(waterColorCB)
				.addComponent(waterColorBtn)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
			.addComponent(storeDefaultsBtn)
			.addContainerGap()
		);
	}

	@Override
	public void refreshSettings() {
		updateStillWater();
		waterVisibility.update();
		waterOpacity.update();
		updateWaterHeight();
		updateWaterColor();
	}

	protected void updateStillWater() {
		stillWaterCB.removeActionListener(stillWaterListener);
		stillWaterCB.setSelected(renderMan.scene().stillWaterEnabled());
		stillWaterCB.addActionListener(stillWaterListener);
	}

	protected void updateWaterHeight() {
		int height = renderMan.scene().getWaterHeight();
		boolean waterWorld = height > 0;
		if (waterWorld) {
			waterHeightField.removeActionListener(waterHeightListener);
			waterHeightField.setText("" + height);
			waterHeightField.addActionListener(waterHeightListener);
		}
		waterWorldCB.setSelected(waterWorld);
		waterHeightField.setEnabled(waterWorld);
		applyWaterHeightBtn.setEnabled(waterWorld);
	}

	protected void updateWaterColor() {
		waterColorCB.removeActionListener(customWaterColorListener);
		boolean useCustomWaterColor = renderMan.scene().getUseCustomWaterColor();
		waterColorCB.setSelected(useCustomWaterColor);
		waterColorBtn.setEnabled(useCustomWaterColor);
		waterColorCB.addActionListener(customWaterColorListener);
	}
}
