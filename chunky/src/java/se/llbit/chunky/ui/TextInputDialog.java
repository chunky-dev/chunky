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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * Dedicated error reporting dialog.
 *
 * Used to display critical errors in a nicer way.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class TextInputDialog extends JDialog {

	/**
	 * Initialize the error dialog.
	 */
	public TextInputDialog(String title, String message, final TextInputListener listener) {
		super();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);

		setTitle(title);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		JLabel lbl = new JLabel(message);
		final JTextField textField = new JTextField(40);

		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});

		JButton okBtn = new JButton("Ok");
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.onTextInput(textField.getText());
				closeDialog();
			}
		});

		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addComponent(lbl)
				.addComponent(textField)
				.addGroup(layout.createSequentialGroup()
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(cancelBtn)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(okBtn)
				)
			)
			.addContainerGap());
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(lbl)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(textField)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(cancelBtn)
				.addComponent(okBtn)
			)
			.addContainerGap()
		);

		getRootPane().setDefaultButton(okBtn);
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Close Dialog");
		getRootPane().getActionMap().put("Close Dialog", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});

		getContentPane().add(panel);
		pack();
	}

	protected void closeDialog() {
		setVisible(false);
		dispose();
	}

}
