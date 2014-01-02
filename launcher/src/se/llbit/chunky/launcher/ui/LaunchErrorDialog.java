/* Copyright (c) 2012-2014 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.launcher.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultEditorKit;

/**
 * Error report dialog.
 *
 * Used to display critical errors in a nicer way.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class LaunchErrorDialog extends JDialog {

	private final JTextArea textArea;

	/**
	 * Initialize the error dialog.
	 */
	public LaunchErrorDialog(String command) {
		super();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);

		setTitle("Launch Error");
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		JLabel lbl = new JLabel(
				"<html>Chunky failed to start! See the Debug Console for error messages.<br><br>" +
				"The following command was used to start Chunky:");

		textArea = new JTextArea(10, 60);
		textArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		textArea.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
				Action[] actions = textArea.getActions();
				for (Action action: actions) {
					if (action.getValue(Action.NAME).equals(
							DefaultEditorKit.selectAllAction))

						action.actionPerformed(null);
				}
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(byteOut);
		out.println(command);
		out.println();
		out.close();

		textArea.setText(new String(byteOut.toByteArray()));
		JScrollPane scrollPane = new JScrollPane(textArea);

		JButton dismissBtn = new JButton("Dismiss");
		dismissBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LaunchErrorDialog.this.setVisible(false);
				LaunchErrorDialog.this.dispose();
			}
		});

		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addComponent(lbl)
				.addComponent(scrollPane)
				.addGroup(layout.createSequentialGroup()
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(dismissBtn)
				)
			)
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(lbl)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(scrollPane)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(dismissBtn)
			.addContainerGap()
		);

		getContentPane().add(panel);
		pack();
	}
}
