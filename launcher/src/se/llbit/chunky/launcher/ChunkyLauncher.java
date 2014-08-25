/* Copyright (c) 2013-2014 Jesper Öqvist <jesper@llbit.se>
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
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.launcher.ui.LaunchErrorDialog;
import se.llbit.chunky.resources.MinecraftFinder;
import se.llbit.chunky.resources.SettingsDirectory;
import se.llbit.ui.Adjuster;

/**
 * The main Chunky Launcher window.
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
@SuppressWarnings("serial")
public class ChunkyLauncher extends JFrame implements UpdateListener {

	private static final String LAUNCHER_VERSION = "v1.8.7";

	protected String java;
	private final ChunkyDeployer deployer;
	private JTextField minecraftDirField;
	private final LauncherSettings settings;

	private final JCheckBox debugConsoleCB = new JCheckBox("Enable debug console");
	private final JCheckBox verboseLoggingCB = new JCheckBox("Verbose logging");
	private final JCheckBox closeConsoleOnExitCB = new JCheckBox("Close console when Chunky exits");
	private final JCheckBox alwaysShowLauncherCB = new JCheckBox("Always show the launcher");
	private final JTextField jreDirField = new JTextField(20);
	private final JTextField javaOptions = new JTextField(20);
	private final JTextField chunkyOptions = new JTextField(20);
	private final JComboBox versionCB = new JComboBox();
	private final JButton checkForUpdateBtn = new JButton("Check for Update");
	private final JLabel busyLbl = new JLabel();
	private final JCheckBox advancedSettingsToggle = new JCheckBox("Advanced settings");

	private final Object updateLock = new Object();
	private volatile boolean isBusy = false;

	private final Adjuster memoryLimitAdjuster = new Adjuster("Memory limit (MiB)",
			"<html>Maximum Java heap space in megabytes (MiB)<br>"
			+ "Do not set this greater than the available memory in your computer!",
			1<<9, 1<<14) {
		{
			setLogarithmicMode();
			setClampMax(false);
		}

		@Override
		public void valueChanged(double newValue) {
			settings.memoryLimit = (int) newValue;
		}

		@Override
		public void update() {
			set(settings.memoryLimit);
		}
	};
	protected UpdateChecker updateThread;


	public ChunkyLauncher(ChunkyDeployer deployer, LauncherSettings settings) {
		super("Chunky Launcher");

		this.deployer = deployer;

		this.settings = settings;
		memoryLimitAdjuster.update();

		buildUI();
	}

	/**
	 * Set the busy indicator
	 * @param busy
	 */
	public void setBusy(final boolean busy) {
		synchronized (updateLock) {
			isBusy = busy;
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				busyLbl.setVisible(isBusy);
			}
		});
	}

	/**
	 * Set the busy indicator on Event Dispatch Thread.
	 * @param busy
	 */
	public void setBusyEDT(final boolean busy) {
		synchronized (updateLock) {
			isBusy = busy;
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				busyLbl.setVisible(isBusy);
			}
		});
	}

	private void buildUI() {
		memoryLimitAdjuster.update();

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Close Dialog");
		getRootPane().getActionMap().put("Close Dialog", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				closeDialog();
			}
		});

		alwaysShowLauncherCB.setSelected(settings.showLauncher);

		JLabel info = new JLabel("<html><b>Chunky startup settings:");

		JLabel versionLbl = new JLabel("Chunky version:");

		updateVersionList();

		final File minecraftDir = MinecraftFinder.getMinecraftDirectory();
		JLabel minecraftDirLbl = new JLabel("Minecraft directory:");
		minecraftDirField = new JTextField(20);
		minecraftDirField.setText(minecraftDir.getAbsolutePath());
		minecraftDirField.setToolTipText("The Minecraft directory is used to find the default textures");
		JButton browseMcBtn = new JButton("Browse...");
		browseMcBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Select Minecraft Directory");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (minecraftDir.isDirectory()) {
					fileChooser.setCurrentDirectory(minecraftDir);
				}
				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedDirectory = fileChooser.getSelectedFile();
					if (MinecraftFinder.getMinecraftJar(selectedDirectory, false) != null) {
						setMinecraftDir(selectedDirectory);
					} else {
						Dialogs.message(ChunkyLauncher.this,
								"<html>Warning: could not find a valid Minecraft installation in the selected directory.<br>"
								+ "The default directory will be used.");
					}
				}
			}
		});

		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.setMnemonic(KeyEvent.VK_C);
		cancelBtn.setToolTipText("Close the Chunky launcher");
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				closeDialog();
			}
		});

		JButton launchBtn = new JButton("Launch");
		launchBtn.setMnemonic(KeyEvent.VK_L);
		launchBtn.setToolTipText("Launch Chunky using these settings");
		launchBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				settings.javaDir = jreDirField.getText();
				settings.debugConsole = debugConsoleCB.isSelected();
				settings.verboseLogging = verboseLoggingCB.isSelected();
				settings.closeConsoleOnExit = closeConsoleOnExitCB.isSelected();
				settings.javaOptions = javaOptions.getText();
				settings.chunkyOptions = chunkyOptions.getText();
				settings.version = ((VersionInfo) versionCB.getSelectedItem()).name;
				settings.showLauncher = alwaysShowLauncherCB.isSelected();
				settings.showAdvancedSettings = advancedSettingsToggle.isSelected();

				// resolve specific version
				VersionInfo version = ChunkyDeployer.resolveVersion(settings.version);
				if (!ChunkyDeployer.canLaunch(version, ChunkyLauncher.this, true)) {
					return;
				}

				PersistentSettings.setMinecraftDirectory(minecraftDirField.getText());
				if (deployer.launchChunky(ChunkyLauncher.this, settings, version,
						ChunkyMode.GUI) == 0) {
					settings.save();
					setVisible(false);
					dispose();
				} else {
					launchError(settings, version);
				}
			}
		});

		checkForUpdateBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (updateLock) {
					if (!isBusy) {
						setBusyEDT(true);
						updateThread = new UpdateChecker(settings, ChunkyLauncher.this);
						updateThread.start();
					}
				}
			}
		});

		JLabel logo = new JLabel();
		URL url = getClass().getResource("/chunky-cfg.png");
		if (url != null) {
			Image img = Toolkit.getDefaultToolkit().getImage(url);
			setIconImage(img);
			logo.setIcon(new ImageIcon(img));
		}

		url = getClass().getResource("/busy.gif");
		if (url != null) {
			busyLbl.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(url)));
		}
		busyLbl.setVisible(false);

		getRootPane().setDefaultButton(launchBtn);

		final JPanel advancedPanel = buildAdvancedPanel();
		advancedPanel.setVisible(settings.showAdvancedSettings);

		advancedSettingsToggle.setSelected(settings.showAdvancedSettings);
		advancedSettingsToggle.setIcon(Icons.expand);
		advancedSettingsToggle.setRolloverIcon(Icons.expandHover);
		advancedSettingsToggle.setSelectedIcon(Icons.collapse);
		advancedSettingsToggle.setRolloverSelectedIcon(Icons.collapseHover);
		advancedSettingsToggle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox source = (JCheckBox) e.getSource();
				advancedPanel.setVisible(source.isSelected());
				ChunkyLauncher.this.pack();
			}
		});

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);

		JLabel launcherVersionLbl = new JLabel(LAUNCHER_VERSION);

		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup()
				.addComponent(logo)
				.addComponent(launcherVersionLbl)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(info)
					.addGap(20, 40, Short.MAX_VALUE)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(versionLbl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(versionCB)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(checkForUpdateBtn)
					.addComponent(busyLbl)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(minecraftDirLbl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(minecraftDirField)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(browseMcBtn)
				)
				.addGroup(memoryLimitAdjuster.horizontalGroup(layout))
				.addComponent(advancedSettingsToggle)
				.addComponent(advancedPanel)
				.addGroup(layout.createSequentialGroup()
					.addComponent(alwaysShowLauncherCB)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(cancelBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(launchBtn)
				)
			)
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(logo)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(launcherVersionLbl)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(info)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(versionLbl)
						.addComponent(versionCB)
						.addComponent(checkForUpdateBtn)
						.addComponent(busyLbl)
					)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(minecraftDirLbl)
						.addComponent(minecraftDirField)
						.addComponent(browseMcBtn)
					)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(memoryLimitAdjuster.verticalGroup(layout))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(advancedSettingsToggle)
					.addComponent(advancedPanel)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(layout.createParallelGroup()
						.addComponent(alwaysShowLauncherCB)
						.addComponent(cancelBtn)
						.addComponent(launchBtn)
					)
				)
			)
		);

		setContentPane(panel);
		pack();
	}

	/**
	 * Opens the launch error dialog.
	 * @param settings
	 * @param version
	 */
	protected static void launchError(final LauncherSettings settings,
			final VersionInfo version) {
		if (settings.debugConsole) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					LaunchErrorDialog dialog = new LaunchErrorDialog(
						ChunkyDeployer.commandString(
								ChunkyDeployer.buildCommandLine(version, settings)));
					dialog.setVisible(true);
				}
			});
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					LaunchErrorDialog dialog = new LaunchErrorDialog(
						ChunkyDeployer.commandString(
								ChunkyDeployer.buildCommandLine(version, settings)));
					dialog.setVisible(true);
				}
			});
		}

	}

	private JPanel buildAdvancedPanel() {
		JLabel jreDirLbl = new JLabel("Java Runtime:");
		jreDirField.setText(getConfiguredJre());
		JButton browseJreBtn = new JButton("Browse...");

		JLabel javaOptionsLbl = new JLabel("Java options:");

		JLabel chunkyOptionsLbl = new JLabel("Chunky options:");

		JCheckBox downloadSnapshotsCB = new JCheckBox("Download snapshots");
		downloadSnapshotsCB.setSelected(settings.downloadSnapshots);
		downloadSnapshotsCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox source = (JCheckBox) e.getSource();
				settings.downloadSnapshots = source.isSelected();
				settings.save();
			}
		});

		browseJreBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Select Java Installation");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				File jreDir = new File(jreDirField.getText());
				if (jreDir.isDirectory()) {
					fileChooser.setCurrentDirectory(jreDir);
				}
				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedDirectory = fileChooser.getSelectedFile();
					File jreSubDir = new File(selectedDirectory, "jre");
					if (JreUtil.isJreDir(selectedDirectory)) {
						jreDirField.setText(selectedDirectory.getAbsolutePath());
					} else if (JreUtil.isJreDir(jreSubDir)) {
						jreDirField.setText(jreSubDir.getAbsolutePath());
					} else {
						Dialogs.message(ChunkyLauncher.this,
								"<html>Warning: could not find a valid Java installation in the selected directory.<br>"
								+ "The default directory will be used.");
					}
				}
			}
		});

		debugConsoleCB.setToolTipText("Displays a debug console when Chunky is started");
		debugConsoleCB.setSelected(settings.debugConsole);

		verboseLoggingCB.setToolTipText("Enable verbose log output");
		verboseLoggingCB.setSelected(settings.verboseLogging);

		closeConsoleOnExitCB.setSelected(settings.closeConsoleOnExit);

		JLabel settingsDirLbl = new JLabel("Settings directory: ");
		JTextField settingsDir = new JTextField();
		settingsDir.setText(SettingsDirectory.defaultSettingsDirectory().getAbsolutePath());
		settingsDir.setEditable(false);

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jreDirLbl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(jreDirField)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(browseJreBtn)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(javaOptionsLbl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(javaOptions)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(chunkyOptionsLbl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chunkyOptions)
				)
				.addComponent(debugConsoleCB)
				.addComponent(verboseLoggingCB)
				.addComponent(closeConsoleOnExitCB)
				.addComponent(downloadSnapshotsCB)
				.addGroup(layout.createSequentialGroup()
					.addComponent(settingsDirLbl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(settingsDir)
				)
			)
			.addContainerGap()
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addContainerGap()
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(jreDirLbl)
					.addComponent(jreDirField)
					.addComponent(browseJreBtn)
				)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(javaOptionsLbl)
					.addComponent(javaOptions)
				)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(chunkyOptionsLbl)
					.addComponent(chunkyOptions)
				)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(debugConsoleCB)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(verboseLoggingCB)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(closeConsoleOnExitCB)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(downloadSnapshotsCB)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(settingsDir)
					.addComponent(settingsDirLbl)
				)
				.addContainerGap()
		);
		panel.setBorder(BorderFactory.createTitledBorder("Advanced"));
		return panel;
	}

	private String getConfiguredJre() {
		String configuredJre = settings.javaDir;
		if (!configuredJre.isEmpty() && JreUtil.isJreDir(new File(configuredJre))) {
			return configuredJre;
		} else {
			return System.getProperty("java.home");
		}
	}

	protected void setMinecraftDir(File directory) {
		if (directory.isDirectory()) {
			minecraftDirField.setText(directory.getAbsolutePath());
			PersistentSettings.setMinecraftDirectory(directory.getAbsolutePath());
		}
	}

	protected void closeDialog() {
		dispose();
	}

	public static void main(String[] args) {
		LauncherSettings settings = new LauncherSettings();
		settings.load();

		/*
		 * If there are command line arguments then we assume that Chunky should run
		 * in headless mode, unless the --nolauncher command is present in which case
		 * we strip that and start regularly, but without launcher. The --launcher
		 * option overrides everything else and forces the launcher to appear.
		 */

		boolean forceLauncher = false;
		ChunkyMode mode = ChunkyMode.GUI;
		String headlessOptions = "";

		if (args.length > 0) {
			mode = ChunkyMode.HEADLESS;
			for (String arg: args) {
				if (arg.equals("--nolauncher")) {
					mode = ChunkyMode.GUI;
				} else if (arg.equals("--launcher")) {
					forceLauncher = true;
				} else if (arg.equals("--version")) {
					System.out.println("Chunky Launcher " + LAUNCHER_VERSION);
					return;
				} else if (arg.equals("--verbose")) {
					settings.verboseLauncher = true;
				} else if (arg.equals("--update")) {
					UpdateChecker updateThread = new UpdateChecker(settings,
							new UpdateListener() {
								@Override
								public void updateError(String message) {
								}

								@Override
								public void updateAvailable(VersionInfo latest) {
									System.out.println("Downloading Chunky " + latest + "...");
									ConsoleUpdater.update(latest);
								}

								@Override
								public void noUpdateAvailable() {
									System.out.println("Chunky is up to date");
								}
							});
					updateThread.start();
					return;
				} else if (arg.equals("--setup")) {
					// configure launcher settings
					doSetup(settings);
					settings.save();
					return;
				} else {
					if (!headlessOptions.isEmpty()) {
						headlessOptions += " ";
					}
					headlessOptions += arg;
				}
			}
			if (forceLauncher) {
				mode = ChunkyMode.GUI;
			}
		}

		if (mode == ChunkyMode.HEADLESS) {
			// Chunky is being run from the console => headless
			settings.debugConsole = true;
			settings.headless = true;
			settings.chunkyOptions = headlessOptions;
			ChunkyDeployer deployer = new ChunkyDeployer();
			deployer.deploy();
			VersionInfo version = ChunkyDeployer.resolveVersion(settings.version);
			if (ChunkyDeployer.canLaunch(version, null, false)) {
				int exitCode = deployer.launchChunky(null, settings, version,
						ChunkyMode.HEADLESS);
				if (exitCode != 0) {
					System.exit(exitCode);
				}
			} else {
				System.err.println("Failed to start Chunky. Command used:");
				System.err.println(ChunkyDeployer.commandString(
						ChunkyDeployer.buildCommandLine(version, settings)));
				System.exit(1);
			}
		} else {
			// Set up Look and Feel
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				UIManager.put("Slider.paintValue", Boolean.FALSE);
			} catch (Exception e) {
				System.out.println("Failed to set native Look and Feel");
			}

			if (firstTimeSetup()) {
				ChunkyDeployer deployer = new ChunkyDeployer();
				boolean showLauncher = true;
				if (!forceLauncher && !settings.showLauncher) {
					// skip launcher only if we can launch this version
					VersionInfo version = ChunkyDeployer.resolveVersion(settings.version);
					if (ChunkyDeployer.canLaunch(version, null, false)) {
						if (deployer.launchChunky(null, settings, version,
								ChunkyMode.GUI) == 0) {
							showLauncher = false;
							return;
						} else {
							launchError(settings, version);
						}
					}
				}
				if (showLauncher) {
					deployer.deploy();
					JFrame launcher = new ChunkyLauncher(deployer, settings);
					//launcher.setLocationByPlatform(true);
					launcher.setLocationRelativeTo(null);
					launcher.setVisible(true);
				}
			}
		}
	}

	private static void doSetup(LauncherSettings settings) {
		System.out.print("memory limit (MiB): ");
		Scanner in = new Scanner(System.in);
		settings.memoryLimit = in.nextInt();
		in.nextLine();
		System.out.print("Java options: ");
		settings.javaOptions = in.nextLine();
	}

	private static boolean firstTimeSetup() {
		if (!SettingsDirectory.findSettingsDirectory()) {
			CountDownLatch latch = new CountDownLatch(1);
			FirstTimeSetupDialog picker = new FirstTimeSetupDialog(latch);
			picker.setVisible(true);
			try {
				latch.await();
			} catch (InterruptedException e) {
			}
			return picker.accepted();
		}
		return true;
	}

	/**
	 * Must be called from the Event Dispatch Thread.
	 */
	public void updateVersionList() {
		versionCB.removeAllItems();
		versionCB.addItem(VersionInfo.LATEST);
		for (VersionInfo version: ChunkyDeployer.availableVersions()) {
			versionCB.addItem(version);
			if (version.name.equals(settings.version)) {
				versionCB.setSelectedIndex(versionCB.getItemCount()-1);
			}
		}

	}

	/**
	 * Select the latest version (always the first element of the version list).
	 */
	public void selectLatestVersion() {
		versionCB.setSelectedIndex(0);
	}

	@Override
	public void updateError(String message) {
		Dialogs.error(this, message);
		setBusy(false);
	}

	@Override
	public void updateAvailable(VersionInfo latest) {
		UpdateDialog dialog = new UpdateDialog(this, latest);
		dialog.setVisible(true);
	}

	@Override
	public void noUpdateAvailable() {
		setBusy(false);
	}
}
