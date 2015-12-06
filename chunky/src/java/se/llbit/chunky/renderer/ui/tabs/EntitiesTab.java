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

import se.llbit.chunky.renderer.ui.RenderControls;
import se.llbit.chunky.world.entity.Entity;
import se.llbit.chunky.world.entity.PlayerEntity;

public class EntitiesTab extends RenderControlsTab {

	private final JTable entityTable;
	private final DefaultTableModel tableModel =
			new DefaultTableModel(new String[] { "Kind", "Id" }, 0);

	public EntitiesTab(RenderControls renderControls) {
		super(renderControls);

		final JLabel entityData = new JLabel("");

		entityTable = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(entityTable);
		entityTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final ListSelectionModel selectionModel = entityTable.getSelectionModel();
		selectionModel.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int first = e.getFirstIndex();
				int last = e.getLastIndex();
				if (first >= 0 && last < tableModel.getRowCount()) {
					for (int index = first; index <= last; index += 1) {
						if (selectionModel.isSelectedIndex(index)) {
							PlayerEntity player = (PlayerEntity) tableModel.getValueAt(index, 1);
							ByteArrayOutputStream out = new ByteArrayOutputStream();
							PrettyPrinter printer = new PrettyPrinter("  ", new PrintStream(out));
							player.toJson().prettyPrint(printer);
							entityData.setText(out.toString());
						}
					}
				}
			}
		});

		JLabel dataLbl = new JLabel("Entity Data");

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup()
			.addComponent(scrollPane)
			.addComponent(dataLbl)
			.addComponent(entityData)
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(scrollPane)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(dataLbl)
			.addComponent(entityData)
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
