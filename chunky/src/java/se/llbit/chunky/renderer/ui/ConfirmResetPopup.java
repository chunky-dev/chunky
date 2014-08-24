/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * Helper dialog for asking the user if the render should be reset.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class ConfirmResetPopup extends JDialog {
	protected AcceptOrRejectListener listener;
	protected final WindowFocusListener windowFocusListener = new WindowFocusListener() {
		@Override
		public void windowLostFocus(WindowEvent e) {
			onReject();
		}

		@Override
		public void windowGainedFocus(WindowEvent e) {
		}
	};
	private boolean closing = false;

	public ConfirmResetPopup(Component parent, final AcceptOrRejectListener listener) {
		this.listener = listener;
		setTitle("Reset render?");
		setLocationRelativeTo(parent);

		//setModalExclusionType(ModalExclusionType.NO_EXCLUDE);

		addWindowFocusListener(windowFocusListener);


		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Close Dialog");
		getRootPane().getActionMap().put("Close Dialog", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onReject();
			}
		});

		JLabel description = new JLabel("<html>Something in the scene settings changed which requires a render reset.<br>" +
				"Are you sure you wish to reset the render? The current render progress will be lost.");
		JButton okBtn = new JButton("Reset");
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onAccept();
			}
		});
		JButton cancelBtn = new JButton("Do not reset");
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onReject();
			}
		});

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
			.addComponent(description)
			.addGroup(layout.createSequentialGroup()
				.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(cancelBtn)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(okBtn)
			)
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addComponent(description)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(cancelBtn)
				.addComponent(okBtn)
			)
		);
		setContentPane(panel);
		getRootPane().setDefaultButton(cancelBtn);
		cancelBtn.requestFocus();

		pack();
		setVisible(true);
	}

	protected void onAccept() {
		if (!closing) {
			listener.onAccept();
			closeDialog();
		}
	}

	protected void onReject() {
		if (!closing) {
			listener.onReject();
			closeDialog();
		}
	}

	protected void closeDialog() {
		closing = true;
		setVisible(false);
		dispose();
	}
}
