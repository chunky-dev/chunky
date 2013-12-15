/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.launcher;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;

import se.llbit.chunky.ChunkySettings;
import se.llbit.chunky.resources.SettingsDirectory;

@SuppressWarnings("serial")
public class FirstTimeSetupDialog extends JFrame {
	private final JButton okBtn;
	private final JButton cancelBtn;
	private final CountDownLatch endLatch;
	private boolean accepted = false;
	private final JRadioButton homeDirectoryBtn;
	private final JRadioButton programDirectoryBtn;
	private final JRadioButton workingDirectoryBtn;

	public FirstTimeSetupDialog(CountDownLatch latch) {
		super("Chunky First-Time Setup");

		endLatch = latch;

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Close Dialog");
		getRootPane().getActionMap().put("Close Dialog", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});

		okBtn = new JButton("Use selected directory");
		okBtn.setOpaque(false);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File settingsDir;
				if (homeDirectoryBtn.isSelected()) {
					settingsDir = SettingsDirectory.getHomeDirectory();
				} else if (programDirectoryBtn.isSelected()) {
					settingsDir = SettingsDirectory.getProgramDirectory();
				} else {
					settingsDir = SettingsDirectory.getWorkingDirectory();
				}
				boolean initialized = false;
				try {
					if (!settingsDir.isDirectory()) {
						settingsDir.mkdirs();
					}
					File settingsFile = new File(settingsDir,
							ChunkySettings.SETTINGS_FILE);
					if (settingsFile.isFile() || settingsFile.createNewFile()) {
						initialized = true;
					}
				} catch (IOException e1) {
					System.err.println(e1.getMessage());
				}
				if (!initialized) {
					Dialogs.error(
							FirstTimeSetupDialog.this,
							"Failed to initialize Chunky configuration directory! You may need administrative permissions on the computer to do this.",
							"Failed to Initialize");
				} else {
					accepted = true;
					close();
				}
			}
		});

		cancelBtn = new JButton("Cancel");
		cancelBtn.setOpaque(false);
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});

		JLabel description = new JLabel(
				"<html>It looks like this is your first time starting Chunky!<br>"
				+ "(or the previous settings could not be found)<br>"
				+ "<br>Please select which directory to store Chunky configuration in:");

		homeDirectoryBtn = new JRadioButton("<html><b>Home Directory (Recommended):</b><br>"
				+ SettingsDirectory.getHomeDirectory());
		homeDirectoryBtn.setOpaque(false);
		programDirectoryBtn = new JRadioButton("<html><b>Program Directory (for portable/thumb drive installations):</b><br>"
				+ SettingsDirectory.getProgramDirectory());
		programDirectoryBtn.setOpaque(false);
		workingDirectoryBtn = new JRadioButton("<html>Working Directory:<br>"
				+ SettingsDirectory.getWorkingDirectory());
		workingDirectoryBtn.setOpaque(false);

		homeDirectoryBtn.setSelected(true);

		ButtonGroup group = new ButtonGroup();
		group.add(homeDirectoryBtn);
		group.add(workingDirectoryBtn);
		group.add(programDirectoryBtn);

		JLabel icon = new JLabel("");

		URL url = getClass().getResource("/chunky-cfg.png");
		if (url != null) {
			Image img = Toolkit.getDefaultToolkit().getImage(url);
			setIconImage(img);
			icon.setIcon(new ImageIcon(img));
		}

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);

		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(icon)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(description)
				.addComponent(homeDirectoryBtn)
				.addComponent(programDirectoryBtn)
				.addComponent(workingDirectoryBtn)
				.addGroup(layout.createSequentialGroup()
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(okBtn)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(cancelBtn)
				)
			)
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(description)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(icon)
				.addGroup(layout.createSequentialGroup()
					.addComponent(homeDirectoryBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(programDirectoryBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(workingDirectoryBtn)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(layout.createParallelGroup()
						.addComponent(okBtn)
						.addComponent(cancelBtn)
					)
				)
			)
			.addContainerGap()
		);

		setContentPane(panel);

		pack();

		setLocationByPlatform(true);
	}

	protected void close() {
		setVisible(false);
		dispose();
		endLatch.countDown();
	}

	public boolean accepted() {
		return accepted;
	}
}
