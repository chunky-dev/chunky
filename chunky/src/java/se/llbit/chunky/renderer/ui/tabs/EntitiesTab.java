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
import java.util.HashSet;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import se.llbit.chunky.renderer.scene.PlayerModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.ui.RenderControls;
import se.llbit.chunky.renderer.ui.tabs.SkyTab.SkyboxTextureLoader;
import se.llbit.chunky.ui.CenteredFileDialog;
import se.llbit.chunky.world.entity.Entity;
import se.llbit.chunky.world.entity.PlayerEntity;
import se.llbit.json.JsonObject;
import se.llbit.math.QuickMath;
import se.llbit.math.Vector3d;
import se.llbit.ui.Adjuster;

public class EntitiesTab extends RenderControlsTab {
	private static final long serialVersionUID = -1L;

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
	private final JComboBox playerModel = new JComboBox();
	private final JTextField skinField = new JTextField();
	private final JButton selectSkinBtn = new JButton("Select Skin");
	private final JButton moveToPlayer = new JButton("Camera to Player");
	private final JButton moveToCamera = new JButton("Player to Camera");
	private final JButton moveToTarget = new JButton("Player to Target");
	private final JButton faceCamera = new JButton("Face Camera");
	private final JButton addPlayer = new JButton("Add Player");
	private final JButton removePlayer = new JButton("Remove Player");
	private final ListSelectionModel selectionModel;
	private final DefaultTableModel tableModel =
			new DefaultTableModel(new String[] { "Name", "Id" }, 0);

	private final EntityAdjuster direction = new EntityAdjuster(
			"Direction",
			"",
			-Math.PI, Math.PI) {

		{
			setClampMax(false);
			setClampMin(false);
		}

		@Override
		public void valueChanged(double newValue) {
			PlayerEntity player = getSelectedPlayer();
			if (player != null) {
				player.yaw = newValue;
				scene().rebuildActorBvh();
				// TODO start entity refresh in utility thread
			}
		}

		@Override
		public void update() {
			// Use update(PlayerEntity) instead.
		}

		@Override
		public void update(PlayerEntity player) {
			set(player.yaw);
		}
	};

	private final EntityAdjuster headYaw = new EntityAdjuster(
			"Head Yaw",
			"",
			-QuickMath.HALF_PI, QuickMath.HALF_PI) {

		{
			setClampMax(false);
			setClampMin(false);
		}

		@Override
		public void valueChanged(double newValue) {
			PlayerEntity player = getSelectedPlayer();
			if (player != null) {
				player.headYaw = newValue;
				scene().rebuildActorBvh();
				// TODO start entity refresh in utility thread
			}
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

	private final EntityAdjuster headPitch = new EntityAdjuster(
			"Head Pitch",
			"",
			-QuickMath.HALF_PI, QuickMath.HALF_PI) {

		{
			setClampMax(false);
			setClampMin(false);
		}

		@Override
		public void valueChanged(double newValue) {
			PlayerEntity player = getSelectedPlayer();
			if (player != null) {
				player.pitch = newValue;
				scene().rebuildActorBvh();
				// TODO start entity refresh in utility thread
			}
		}

		@Override
		public void update() {
			// Use update(PlayerEntity) instead.
		}

		@Override
		public void update(PlayerEntity player) {
			set(player.pitch);
		}
	};

	private final EntityAdjuster leftLeg = new EntityAdjuster(
			"Left Leg Pose",
			"",
			-QuickMath.HALF_PI, QuickMath.HALF_PI) {

		{
			setClampMax(false);
			setClampMin(false);
		}

		@Override
		public void valueChanged(double newValue) {
			PlayerEntity player = getSelectedPlayer();
			if (player != null) {
				player.leftLegPose = newValue;
				scene().rebuildActorBvh();
			}
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
			-QuickMath.HALF_PI, QuickMath.HALF_PI) {

		{
			setClampMax(false);
			setClampMin(false);
		}

		@Override
		public void valueChanged(double newValue) {
			PlayerEntity player = getSelectedPlayer();
			if (player != null) {
				player.rightLegPose = newValue;
				scene().rebuildActorBvh();
			}
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
			-Math.PI, Math.PI) {

		{
			setClampMax(false);
			setClampMin(false);
		}

		@Override
		public void valueChanged(double newValue) {
			PlayerEntity player = getSelectedPlayer();
			if (player != null) {
				player.leftArmPose = newValue;
				scene().rebuildActorBvh();
			}
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
			-Math.PI, Math.PI) {

		{
			setClampMax(false);
			setClampMin(false);
		}

		@Override
		public void valueChanged(double newValue) {
			PlayerEntity player = getSelectedPlayer();
			if (player != null) {
				player.rightArmPose = newValue;
				scene().rebuildActorBvh();
			}
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

	private final ActionListener playerModelListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			PlayerEntity player = getSelectedPlayer();
			if (player != null) {
				JComboBox source = (JComboBox) e.getSource();
				PlayerModel model = (PlayerModel) source.getSelectedItem();
				if (player.model != model) {
					player.model = model;
					scene().rebuildActorBvh();
				}
			}
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
				updateCurrentPlayer();
			}
		});

		JLabel poseLbl = new JLabel("Pose");

		JLabel playerModelLbl = new JLabel("Player model: ");

		for (PlayerModel model : PlayerModel.values()) {
			playerModel.addItem(model);
		}
		playerModel.setToolTipText("Reload chunks after changing this option.");
		playerModel.addActionListener(playerModelListener);

		JLabel skinLbl = new JLabel("Skin");

		skinField.setEnabled(false);

		removePlayer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlayerEntity player = getSelectedPlayer();
				if (player != null) {
					scene().removePlayer(player);
				}
			}
		});

		addPlayer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Collection<Entity> entities = scene().getActors();
				Set<String> ids = new HashSet<String>();
				for (Entity entity : entities) {
					if (entity instanceof PlayerEntity) {
						ids.add(((PlayerEntity) entity).uuid);
					}
				}
				// Pick a new UUID for the new entity.
				long id = System.currentTimeMillis();
				while (ids.contains(String.format("%016X%016X", 0, id))) {
					id += 1;
				}
				Vector3d position = scene().getTargetPosition();
				if (position == null) {
					position = new Vector3d(scene().camera().getPosition());
				}
				PlayerEntity player = new PlayerEntity(String.format("%016X%016X", 0, id),
						position, 0, 0);
				PlayerEntity selected = getSelectedPlayer();
				if (selected != null) {
					player.skin = selected.skin;
					player.model = selected.model;
				}
				player.randomPoseAndLook();
				scene().addPlayer(player);
				PlayerData data = new PlayerData(player, scene());
				tableModel.addRow(new Object[] { data.name, data });
			}
		});

		moveToPlayer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlayerEntity player = getSelectedPlayer();
				if (player != null) {
					scene().camera().moveToPlayer(player);
					scene().rebuildActorBvh();
				}
			}
		});

		moveToCamera.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlayerEntity player = getSelectedPlayer();
				if (player != null) {
					player.position.set(scene().camera().getPosition());
					scene().rebuildActorBvh();
				}
			}
		});

		moveToTarget.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlayerEntity player = getSelectedPlayer();
				if (player != null) {
					Vector3d target = scene().getTargetPosition();
					if (target != null) {
						player.position.set(target);
						scene().rebuildActorBvh();
					}
				}
			}
		});

		faceCamera.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlayerEntity player = getSelectedPlayer();
				if (player != null) {
					player.lookAt(scene().camera().getPosition());
					scene().rebuildActorBvh();
				}
			}
		});

		selectSkinBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlayerEntity player = getSelectedPlayer();
				if (player != null) {
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
						player.setTexture(selectedFile.getAbsolutePath());
						skinField.setText(player.skin);
						scene().rebuildActorBvh();
					}
				}
			}
		});

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
					.addComponent(addPlayer)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(removePlayer))
			.addGroup(layout.createSequentialGroup()
					.addComponent(moveToPlayer)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(moveToCamera)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(moveToTarget)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(faceCamera))
			.addComponent(scrollPane)
			.addGroup(layout.createSequentialGroup()
					.addComponent(playerModelLbl)
					.addComponent(playerModel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
			.addGroup(layout.createSequentialGroup()
					.addComponent(skinLbl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(skinField)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(selectSkinBtn))
			.addComponent(poseLbl)
			.addGroup(direction.horizontalGroup(layout))
			.addGroup(headYaw.horizontalGroup(layout))
			.addGroup(headPitch.horizontalGroup(layout))
			.addGroup(leftArm.horizontalGroup(layout))
			.addGroup(rightArm.horizontalGroup(layout))
			.addGroup(leftLeg.horizontalGroup(layout))
			.addGroup(rightLeg.horizontalGroup(layout))
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup()
					.addComponent(addPlayer)
					.addComponent(removePlayer))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup()
					.addComponent(moveToPlayer)
					.addComponent(moveToCamera)
					.addComponent(moveToTarget)
					.addComponent(faceCamera))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(playerModelLbl)
					.addComponent(playerModel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup()
					.addComponent(skinLbl)
					.addComponent(skinField)
					.addComponent(selectSkinBtn))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(poseLbl)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(direction.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(headYaw.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(headPitch.verticalGroup(layout))
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
		Collection<Entity> entities = scene().getActors();
		for (Entity entity : entities) {
			if (entity instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) entity;
				PlayerData data = new PlayerData(player, scene());
				tableModel.addRow(new Object[] { data.name, data });
			}
		}
		updateCurrentPlayer();
	}

	protected void updateCurrentPlayer() {
		updateSkin();
		PlayerEntity player = getSelectedPlayer();
		if (player != null) {
			direction.update(player);
			headYaw.update(player);
			headPitch.update(player);
			leftLeg.update(player);
			rightLeg.update(player);
			leftArm.update(player);
			rightArm.update(player);
		}
		updatePlayerModel();
	}

	protected void updatePlayerModel() {
		PlayerEntity player = getSelectedPlayer();
		if (player != null) {
			playerModel.removeActionListener(playerModelListener);
			playerModel.setSelectedItem(player.model);
			playerModel.addActionListener(playerModelListener);
		}
	}

}
