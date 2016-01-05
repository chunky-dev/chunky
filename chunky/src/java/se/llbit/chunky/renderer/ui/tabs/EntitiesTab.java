/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
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
import java.util.Collection;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.ui.RenderControls;
import se.llbit.chunky.renderer.ui.tabs.SkyTab.SkyboxTextureLoader;
import se.llbit.chunky.ui.CenteredFileDialog;
import se.llbit.chunky.world.entity.Entity;
import se.llbit.chunky.world.entity.PlayerEntity;
import se.llbit.json.JsonObject;
import se.llbit.ui.Adjuster;

public class EntitiesTab extends RenderControlsTab {

	static class PlayerData {
		public final PlayerEntity entity;
		public final String name;

		public PlayerData(PlayerEntity entity, Scene scene) {
			this.entity = entity;

			JsonObject profile = scene.getPlayerProfile(entity);
			name = getName(profile);
		}

		private static String getName(JsonObject profile) {
			return profile.get("name").stringValue("Unknown");
		}

		@Override
		public String toString() {
			return entity.uuid;
		}
	}

	static abstract class EntityAdjuster extends Adjuster {
		public EntityAdjuster(String name, String tip,
			double min, double max) {
			super(name, tip, min, max);
		}
		abstract public void update(PlayerEntity player);
	}

	private final JTable entityTable;
	private final JTextField skinField = new JTextField();
	private final JButton selectSkinBtn = new JButton("Select Skin");
	private final JButton moveToPlayer = new JButton("Move Camera to Player");
	private final JButton moveToCamera = new JButton("Move Player to Camera");
	private final ListSelectionModel selectionModel;
	private final DefaultTableModel tableModel =
			new DefaultTableModel(new String[] { "Name", "Id" }, 0);

	private final EntityAdjuster headYaw = new EntityAdjuster(
			"Head Yaw",
			"",
			100, 100000) {

		{
			setClampMax(false);
			setClampMin(false);
			setLogarithmicMode();
		}

		@Override
		public void valueChanged(double newValue) {
			// TODO(llbit): update entity property.
		}

		@Override
		public void update() {
			// Use update(PlayerEntity) instead.
		}

		@Override
		public void update(PlayerEntity player) {
			set(player.headYaw);
		}
	};

	private final EntityAdjuster leftLeg = new EntityAdjuster(
			"Left Leg Pose",
			"",
			100, 100000) {

		{
			setClampMax(false);
			setClampMin(false);
			setLogarithmicMode();
		}

		@Override
		public void valueChanged(double newValue) {
			// TODO(llbit): update entity property.
		}

		@Override
		public void update() {
			// Use update(PlayerEntity) instead.
		}

		@Override
		public void update(PlayerEntity player) {
			set(player.leftLegPose);
		}
	};

	private final EntityAdjuster rightLeg = new EntityAdjuster(
			"Right Leg Pose",
			"",
			100, 100000) {

		{
			setClampMax(false);
			setClampMin(false);
			setLogarithmicMode();
		}

		@Override
		public void valueChanged(double newValue) {
			// TODO(llbit): update entity property.
		}

		@Override
		public void update() {
			// Use update(PlayerEntity) instead.
		}

		@Override
		public void update(PlayerEntity player) {
			set(player.rightLegPose);
		}
	};

	private final EntityAdjuster leftArm = new EntityAdjuster(
			"Left Arm Pose",
			"",
			100, 100000) {

		{
			setClampMax(false);
			setClampMin(false);
			setLogarithmicMode();
		}

		@Override
		public void valueChanged(double newValue) {
			// TODO(llbit): update entity property.
		}

		@Override
		public void update() {
			// Use update(PlayerEntity) instead.
		}

		@Override
		public void update(PlayerEntity player) {
			set(player.leftArmPose);
		}
	};

	private final EntityAdjuster rightArm = new EntityAdjuster(
			"Right Arm Pose",
			"",
			100, 100000) {

		{
			setClampMax(false);
			setClampMin(false);
			setLogarithmicMode();
		}

		@Override
		public void valueChanged(double newValue) {
			// TODO(llbit): update entity property.
		}

		@Override
		public void update() {
			// Use update(PlayerEntity) instead.
		}

		@Override
		public void update(PlayerEntity player) {
			set(player.rightArmPose);
		}
	};

	public EntitiesTab(RenderControls renderControls) {
		super(renderControls);

		final JLabel entityData = new JLabel("");

		entityTable = new JTable(tableModel) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		JScrollPane scrollPane = new JScrollPane(entityTable);
		entityTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectionModel = entityTable.getSelectionModel();
		selectionModel.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateSkin();
				PlayerEntity player = getSelectedPlayer();
				if (player != null) {
					headYaw.update(player);
					leftLeg.update(player);
					rightLeg.update(player);
					leftArm.update(player);
					rightArm.update(player);
				}
			}
		});

		JLabel poseLbl = new JLabel("Pose");
		JLabel skinLbl = new JLabel("Skin");

		skinField.setEnabled(false);
		skinField.setText("Steve");

		moveToPlayer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlayerEntity player = getSelectedPlayer();
				if (player != null) {
					scene().camera().moveToPlayer(player);
				}
			}
		});

		moveToCamera.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlayerEntity player = getSelectedPlayer();
				if (player != null) {
					player.position.set(scene().camera().getPosition());
					scene().refreshActors();
				}
			}
		});

		selectSkinBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CenteredFileDialog fileDialog =
						new CenteredFileDialog(null, "Select Skin Texture", FileDialog.LOAD);
				String directory;
				synchronized (SkyboxTextureLoader.class) {
					directory = PersistentSettings.getSkinDirectory();
				}
				fileDialog.setDirectory(directory);
				fileDialog.setFilenameFilter(
						new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name) {
								return name.toLowerCase().endsWith(".png");
							}
						});
				fileDialog.setVisible(true);
				File selectedFile = fileDialog.getSelectedFile();
				if (selectedFile != null) {
					File parent = selectedFile.getParentFile();
					if (parent != null) {
						PersistentSettings.setSkinDirectory(parent);
					}
					PlayerEntity player = getSelectedPlayer();
					player.setTexture(selectedFile.getAbsolutePath());
					skinField.setText(player.skin);
				}
			}
		});

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
					.addComponent(moveToPlayer)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(moveToCamera))
			.addComponent(scrollPane)
			.addComponent(skinLbl)
			.addGroup(layout.createSequentialGroup()
					.addComponent(skinField)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(selectSkinBtn))
			.addComponent(poseLbl)
			.addGroup(headYaw.horizontalGroup(layout))
			.addGroup(leftArm.horizontalGroup(layout))
			.addGroup(rightArm.horizontalGroup(layout))
			.addGroup(leftLeg.horizontalGroup(layout))
			.addGroup(rightLeg.horizontalGroup(layout))
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup()
					.addComponent(moveToPlayer)
					.addComponent(moveToCamera))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(skinLbl)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(layout.createParallelGroup()
					.addComponent(skinField)
					.addComponent(selectSkinBtn))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(poseLbl)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(headYaw.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(leftArm.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(rightArm.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(leftLeg.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(rightLeg.verticalGroup(layout))
			.addContainerGap()
		);
	}

	protected void updateSkin() {
		PlayerEntity player = getSelectedPlayer();
		if (player == null) {
			skinField.setText("");
		} else {
			skinField.setText(player.skin);
		}
	}

	protected PlayerEntity getSelectedPlayer() {
		int row = entityTable.getSelectedRow();
		if (row != -1) {
			return ((PlayerData) tableModel.getValueAt(row, 1)).entity;
		} else {
			return null;
		}
	}

	@Override
	public void refreshSettings() {
		tableModel.setRowCount(0);
		Collection<Entity> entities = scene().getEntities();
		for (Entity entity : entities) {
			if (entity instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) entity;
				PlayerData data = new PlayerData(player, scene());
				tableModel.addRow(new Object[] { data.name, data });
			}
		}
	}

}
