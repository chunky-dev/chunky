/* Copyright (c) 2012-2013 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.text.AbstractDocument;

import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.renderer.scene.SceneManager;
import se.llbit.chunky.ui.SelectAllFocusListener;

/**
 * A dialog box that asks the user to name a new 3D scene.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class NewSceneDialog extends JDialog {

	private boolean accepted;
	private String sceneName;
	private final RenderContext renderContext;

	/**
	 * Constructor
	 * @param parent
	 * @param context
	 * @param defaultSceneName
	 */
	public NewSceneDialog(JFrame parent, RenderContext context,
			String defaultSceneName) {

		renderContext = context;

		setTitle("Create New Scene");

		setModalityType(ModalityType.APPLICATION_MODAL);

		JLabel lbl = new JLabel("Enter name for the new 3D scene:");
		final JTextField sceneNameField = new JTextField(20);
		sceneNameField.setText(SceneManager.preferredSceneName(context, defaultSceneName));
		AbstractDocument document = (AbstractDocument) sceneNameField.getDocument();
		document.setDocumentFilter(new SceneNameFilter());
		sceneNameField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField source = (JTextField) e.getSource();
				tryAccept(source.getText());
			}
		});
		sceneNameField.addFocusListener(new SelectAllFocusListener());
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		JButton okBtn = new JButton("OK");
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tryAccept(sceneNameField.getText());
			}
		});

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addComponent(lbl)
				.addComponent(sceneNameField)
				.addGroup(layout.createSequentialGroup()
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addComponent(okBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(cancelBtn)
				)
			)
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(lbl)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(sceneNameField)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(okBtn)
				.addComponent(cancelBtn)
			)
			.addContainerGap()
		);
		setContentPane(panel);

		pack();
		setLocationRelativeTo(parent);
	}

	protected void tryAccept(String text) {
		if (SceneManager.acceptSceneName(renderContext, sceneName)) {
			accepted = true;
			setVisible(false);
			dispose();
		}
	}

	/**
	 * @return <code>true</code> if the user accepted the new scene creation
	 */
	public boolean isAccepted() {
		return accepted;
	}

	/**
	 * @return The user selected scene name
	 */
	public String getSceneName() {
		return sceneName;
	}

}
