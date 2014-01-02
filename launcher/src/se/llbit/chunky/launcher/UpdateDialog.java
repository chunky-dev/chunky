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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.launcher.VersionInfo.Library;
import se.llbit.chunky.launcher.VersionInfo.LibraryStatus;

/**
 * Asks user if new version should be downloaded.
 * Displays release notes and download status.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class UpdateDialog extends JDialog {
	protected final ExecutorService threadPool = Executors.newFixedThreadPool(4);

	public class Finalizer extends Thread {
		private final Collection<Future<DownloadStatus>> results;
		public Finalizer(Collection<Future<DownloadStatus>> resultFutures) {
			results = resultFutures;
		}
		@Override
		public void run() {
			try {
				boolean failed = false;
				for (Future<DownloadStatus> result: results) {
					try {
						if (result.get() != DownloadStatus.SUCCESS) {
							failed = true;
						}
					} catch (InterruptedException e) {
						failed = true;
					} catch (ExecutionException e) {
						failed = true;
					}
				}
				if (failed) {
					updateFailed("Failed to download some required libraries. Please try again later.");
					return;
				}
				try {
					File versionFile = new File(versionsDir, version.name + ".json");
					version.writeTo(versionFile);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							cancelBtn.setText("Close");
							parent.updateVersionList();
						}
					});
				} catch (IOException e) {
					updateFailed("Failed to update version info. Please try again later.");
				}
			} finally {
				updateCompleted();
			}
		}
		private void updateFailed(final String message) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					progress.setStringPainted(true);
					progress.setString(message);
					progress.setForeground(Color.red);
				}
			});
		}
		private void updateCompleted() {
			busyLbl.setVisible(false);
			synchronized (updateLock) {
				busy = false;
				updateLock.notifyAll();
			}
		}
	}

	static class StatusCellRenderer extends DefaultTableCellRenderer {
		private static ImageIcon cached;
		private static ImageIcon failed;
		private static ImageIcon refresh;
		static {
			URL url = StatusCellRenderer.class.getResource("/cached.png");
			if (url != null) {
				cached = new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
			}
			url = StatusCellRenderer.class.getResource("/failed.png");
			if (url != null) {
				failed = new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
			}
			url = StatusCellRenderer.class.getResource("/refresh.png");
			if (url != null) {
				refresh = new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
			}
		}

		@Override
		protected void setValue(Object value) {
			if (value instanceof LibraryStatus) {
				LibraryStatus status = (LibraryStatus) value;
				setText(status.downloadStatus());
				if (status == LibraryStatus.PASSED || status == LibraryStatus.DOWNLOADED_OK) {
					setIcon(cached);
				} else {
					switch (status) {
					case MD5_MISMATCH:
					case MISSING:
						setIcon(refresh);
						break;
					default:
						setIcon(failed);
					}
				}
			}
		}
	}

	private final JButton okBtn;
	private final JButton cancelBtn;
	private final JProgressBar progress = new JProgressBar();
	private final ChunkyLauncher parent;
	private final VersionInfo version;
	private final File libDir;
	private final File versionsDir;
	private final Object updateLock = new Object();
	private boolean busy = false;
	private final JLabel busyLbl = new JLabel();
	private final JTable status;
	private final Collection<VersionInfo.Library> neededLibraries = new LinkedList<VersionInfo.Library>();
	private int downloadBytes = 0;
	private final DefaultTableModel tableModel;

	public UpdateDialog(final ChunkyLauncher parent, final VersionInfo version) {
		super(parent, "Update Available!");

		this.parent = parent;
		this.version = version;

		File chunkyDir = PersistentSettings.getSettingsDirectory();
		libDir = new File(chunkyDir, "lib");
		if (!libDir.isDirectory()) {
			libDir.mkdirs();
		}
		versionsDir = new File(chunkyDir, "versions");
		if (!versionsDir.isDirectory()) {
			versionsDir.mkdirs();
		}

		setModalityType(ModalityType.MODELESS);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}
			@Override
			public void windowIconified(WindowEvent e) {
			}
			@Override
			public void windowDeiconified(WindowEvent e) {
			}
			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			@Override
			public void windowClosing(WindowEvent e) {
				close();
			}
			@Override
			public void windowClosed(WindowEvent e) {
			}
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});

		URL url = getClass().getResource("/chunky-cfg.png");
		if (url != null) {
			setIconImage(Toolkit.getDefaultToolkit().getImage(url));
		}

		url = getClass().getResource("/busy.gif");
		if (url != null) {
			busyLbl.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(url)));
		}
		busyLbl.setVisible(false);

		JLabel infoLbl = new JLabel(
				"<html>A new version of Chunky is available for download!<br>" +
				"<br>Version <b>" + version.name + "</b>, released on " + version.date() +
				"<br>Release notes:");

		okBtn = new JButton("Update to New Version");
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				downloadUpdate();
			}
		});

		cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});

		JTextPane changeLog = new JTextPane();
		changeLog.setCaretPosition(0);
		changeLog.setMargin(new Insets(0, 0, 0, 0));
		if (version.notes.isEmpty()) {
			changeLog.setText("No release notes available.");
		} else {
			changeLog.setText(version.notes);
		}

		tableModel = new DefaultTableModel(version.libraries.size(), 3);
		int i = 0;
		for (Library lib: version.libraries) {
			LibraryStatus libStatus = lib.testIntegrity(libDir);
			if (libStatus != LibraryStatus.PASSED && libStatus != LibraryStatus.INCOMPLETE_INFO) {
				neededLibraries.add(lib);
				downloadBytes += lib.size;
			}

			// pretty print library size
			float size = lib.size;
			String unit = "B";
			if (size >= 1024*1024) {
				size /= 1024*1024;
				unit = "MiB";
			} else if (size >= 1024) {
				size /= 1024;
				unit = "KiB";
			}
			String libSize;
			if (size >= 10) {
				libSize = String.format("%d %s", (int) size, unit);
			} else {
				libSize = String.format("%.1f %s", size, unit);
			}

			tableModel.setValueAt(lib, i, 0);
			tableModel.setValueAt(libStatus, i, 1);
			tableModel.setValueAt(libSize, i, 2);
			i += 1;
		}
		tableModel.setColumnIdentifiers(new String[] { "Library", "Status", "Size" });
		final StatusCellRenderer statusRenderer = new StatusCellRenderer();
		status = new JTable(tableModel) {
			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {
				if (column == 1) {
					return statusRenderer;
				}
				return super.getCellRenderer(row, column);
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		JScrollPane changeLogScrollPane = new JScrollPane(changeLog);

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);

		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addComponent(infoLbl)
				.addComponent(changeLogScrollPane)
				.addComponent(status.getTableHeader())
				.addComponent(status)
				.addComponent(progress)
				.addGroup(layout.createSequentialGroup()
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(busyLbl)
					.addComponent(okBtn)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(cancelBtn)
				)
			)
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(infoLbl)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(changeLogScrollPane)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(status.getTableHeader())
			.addComponent(status)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(progress)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(busyLbl)
				.addComponent(okBtn)
				.addComponent(cancelBtn)
			)
			.addContainerGap()
		);

		setContentPane(panel);

		setPreferredSize(new Dimension(500, 500));
		pack();

		setLocationRelativeTo(parent);
		//setLocationByPlatform(true);
	}

	protected void downloadUpdate() {
		synchronized (updateLock) {
			busy = true;
		}
		okBtn.setVisible(false);
		busyLbl.setVisible(true);
		if (!libDir.isDirectory()) {
			// TODO ERROR
		}
		if (!versionsDir.isDirectory()) {
			// TODO ERROR
		}
		progress.setMaximum(downloadBytes);
		progress.setValue(0);
		final List<Future<DownloadStatus>> results = new LinkedList<Future<DownloadStatus>>();
		for (VersionInfo.Library lib: neededLibraries) {
			results.add(threadPool.submit(new DownloadJob(lib, new ProgressIncrementer(lib.size))));
		}
		new Finalizer(results).start();
	}

	protected void close() {
		threadPool.shutdownNow();
		awaitUpdate();
		setVisible(false);
		dispose();
		parent.setBusyEDT(false);
	}

	private void awaitUpdate() {
		// TODO abort download threads!
		synchronized (updateLock) {
			try {
				while (busy) {
					updateLock.wait();
				}
			} catch (InterruptedException e) {
				// not critical
			}
		}
	}

	class DownloadJob implements Callable<DownloadStatus> {

		private final Library lib;
		private final Runnable callback;

		public DownloadJob(VersionInfo.Library lib, Runnable callback) {
			this.lib = lib;
			this.callback = callback;
		}

		@Override
		public DownloadStatus call() throws Exception {
			DownloadStatus result = null;
			if (!lib.url.isEmpty()) {
				result = tryDownload(lib, lib.url);
				switch (result) {
				case MALFORMED_URL:
					System.err.println("Malformed URL: " + lib.url);
					break;
				case FILE_NOT_FOUND:
					System.err.println("File not found: " + lib.url);
					break;
				case DOWNLOAD_FAILED:
					System.err.println("Download failed: " + lib.url);
					break;
				default:
					break;
				}
			}
			String defaultUrl = "http://chunkyupdate.llbit.se/lib/" + lib.name;
			if (result != DownloadStatus.SUCCESS) {
				result = tryDownload(lib, defaultUrl);
			}
			switch (result) {
			case SUCCESS:
				updateStatus(lib, LibraryStatus.DOWNLOADED_OK);
				break;
			case MALFORMED_URL:
				updateStatus(lib, LibraryStatus.MALFORMED_URL);
				System.err.println("Malformed URL: " + defaultUrl);
				break;
			case FILE_NOT_FOUND:
				updateStatus(lib, LibraryStatus.FILE_NOT_FOUND);
				System.err.println("File not found: " + defaultUrl);
				break;
			case DOWNLOAD_FAILED:
				updateStatus(lib, LibraryStatus.DOWNLOAD_FAILED);
				System.err.println("Download failed: " + defaultUrl);
				break;
			}
			SwingUtilities.invokeLater(callback);
			return result;
		}

	}

	class ProgressIncrementer implements Runnable {
		private final int value;

		public ProgressIncrementer(int value) {
			this.value = value;
		}

		@Override
		public void run() {
			progress.setValue(progress.getValue() + value);
		}
	}

	public void updateStatus(final Library library, final LibraryStatus status) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < tableModel.getRowCount(); ++i) {
					if (tableModel.getValueAt(i, 0) == library) {
						tableModel.setValueAt(status, i, 1);
						break;
					}
				}
			}
		});
	}

	public DownloadStatus tryDownload(Library lib, String theUrl) {
		try {
			URL url = new URL(theUrl);
			ReadableByteChannel inChannel = Channels.newChannel(url.openStream());
			FileOutputStream out = new FileOutputStream(lib.getFile(libDir));
			out.getChannel().transferFrom(inChannel, 0, Long.MAX_VALUE);
			out.close();
			LibraryStatus status = lib.testIntegrity(libDir);
			if (status == LibraryStatus.PASSED) {
				return DownloadStatus.SUCCESS;
			} else {
				return DownloadStatus.DOWNLOAD_FAILED;
			}
		} catch (MalformedURLException e) {
			return DownloadStatus.MALFORMED_URL;
		} catch (FileNotFoundException e) {
			return DownloadStatus.FILE_NOT_FOUND;
		} catch (IOException e) {
			return DownloadStatus.DOWNLOAD_FAILED;
		}

	}

}
