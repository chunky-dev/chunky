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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jastadd.util.PrettyPrinter;

import se.llbit.chunky.renderer.RenderState;
import se.llbit.chunky.renderer.ui.RenderControls;
import se.llbit.chunky.world.entity.Entity;
import se.llbit.chunky.world.entity.PlayerEntity;
import se.llbit.ui.Adjuster;

public class EntitiesTab extends RenderControlsTab {

	static abstract class EntityAdjuster extends Adjuster {
		public EntityAdjuster(String name, String tip,
			double min, double max) {
			super(name, tip, min, max);
		}
		abstract public void update(PlayerEntity player);
	}

	private final JTable entityTable;
	private final ListSelectionModel selectionModel;
	private final DefaultTableModel tableModel =
			new DefaultTableModel(new String[] { "Kind", "Id" }, 0);

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
				int row = entityTable.getSelectedRow();
				if (row != -1) {
					PlayerEntity player = (PlayerEntity) tableModel.getValueAt(row, 1);
					if (player != null) {
						headYaw.update(player);
						leftLeg.update(player);
						rightLeg.update(player);
						leftArm.update(player);
						rightArm.update(player);
					}
				}
			}
		});

		JLabel propertiesLbl = new JLabel("Entity Properties");

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup()
			.addComponent(scrollPane)
			.addComponent(propertiesLbl)
			.addGroup(headYaw.horizontalGroup(layout))
			.addGroup(leftArm.horizontalGroup(layout))
			.addGroup(rightArm.horizontalGroup(layout))
			.addGroup(leftLeg.horizontalGroup(layout))
			.addGroup(rightLeg.horizontalGroup(layout))
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(scrollPane)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(propertiesLbl)
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

	@Override
	public void refreshSettings() {
		tableModel.setRowCount(0);
		Collection<Entity> entities = getScene().getEntities();
		for (Entity entity : entities) {
			if (entity instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) entity;
				tableModel.addRow(new Object[] { "Player", player });
			}
		}
	}

}
