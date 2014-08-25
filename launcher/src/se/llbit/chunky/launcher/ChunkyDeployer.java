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

import java.awt.Component;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.launcher.VersionInfo.Library;
import se.llbit.chunky.launcher.VersionInfo.LibraryStatus;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonParser.SyntaxError;
import se.llbit.json.JsonValue;

/**
 * Deploys the embedded Chunky version, or launches an existing local version.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChunkyDeployer {

	/**
	 * Check the integrity of an installed version.
	 * @param version
	 * @return <code>true</code> if the version is installed locally
	 */
	public static boolean checkVersionIntegrity(String version) {
		File chunkyDir = PersistentSettings.getSettingsDirectory();
		File versionsDir = new File(chunkyDir, "versions");
		File libDir = new File(chunkyDir, "lib");
		if (!versionsDir.isDirectory() || !libDir.isDirectory()) {
			return false;
		}

		File versionFile = new File(versionsDir, version + ".json");
		if (!versionFile.isFile()) {
			return false;
		}

		// check version
		try {
			FileInputStream in = new FileInputStream(versionFile);
			JsonParser parser = new JsonParser(in);
			JsonObject obj = parser.parse().object();
			in.close();
			String versionName = obj.get("name").stringValue("");
			if (!versionName.equals(version)) {
				System.err.println("Stored version name does not match file name");
				return false;
			}
			JsonArray array = obj.get("libraries").array();
			for (JsonValue value: array.getElementList()) {
				VersionInfo.Library lib = new VersionInfo.Library(value.object());
				switch (lib.testIntegrity(libDir)) {
				case INCOMPLETE_INFO:
					System.err.println("Missing library name or checksum");
					return false;
				case MD5_MISMATCH:
					System.err.println("Library MD5 checksum mismatch");
					return false;
				case MISSING:
					System.err.println("Missing library " + lib.name);
					return false;
				default:
					break;
				}
			}
			return true;
		} catch (IOException e) {
			System.err.println("Could not read version info file: " + e.getMessage());
		} catch (SyntaxError e) {
			System.err.println("Corrupted version info file: " + e.getMessage());
		}
		return false;
	}

	/**
	 * Unpacks the embedded Chunky jar files.
	 */
	public void deploy() {
		List<VersionInfo> versions = availableVersions();
		VersionInfo embedded = embeddedVersion();
		if (embedded != null && (!versions.contains(embedded) || !checkVersionIntegrity(embedded.name))) {
			if (System.getProperty("log4j.logLevel", "WARN").equals("INFO")) {
				System.out.println("Deploying embedded version: " + embedded.name);
			}
			deployEmbeddedVersion(embedded);
		}
	}

	public static List<VersionInfo> availableVersions() {
		File chunkyDir = PersistentSettings.getSettingsDirectory();
		File versionsDir = new File(chunkyDir, "versions");
		if (!versionsDir.isDirectory()) {
			return Collections.emptyList();
		}

		List<VersionInfo> versions = new ArrayList<VersionInfo>();

		for (File versionFile: versionsDir.listFiles()) {
			if (versionFile.getName().endsWith(".json")) {
				try {
					FileInputStream in = new FileInputStream(versionFile);
					JsonParser parser = new JsonParser(in);
					versions.add(new VersionInfo(parser.parse().object()));
					in.close();
				} catch (IOException e) {
					System.err.println("Could not read version info file: " + e.getMessage());
				} catch (SyntaxError e) {
					System.err.println("Corrupted version info file: " + e.getMessage());
				}
			}
		}

		Collections.sort(versions);
		return versions;
	}

	/**
	 * Unpack embedded libraries and deploy the embedded Chunky version.
	 * @param version
	 */
	private static void deployEmbeddedVersion(VersionInfo version) {
		File chunkyDir = PersistentSettings.getSettingsDirectory();
		File versionsDir = new File(chunkyDir, "versions");
		if (!versionsDir.isDirectory()) {
			versionsDir.mkdirs();
		}
		File libDir = new File(chunkyDir, "lib");
		if (!libDir.isDirectory()) {
			libDir.mkdirs();
		}
		try {
			File versionJson = new File(versionsDir, version.name + ".json");
			version.writeTo(versionJson);

			ClassLoader parentCL = ChunkyLauncher.class.getClassLoader();

			// deploy libraries that were not already installed correctly
			for (Library lib: version.libraries) {
				if (lib.testIntegrity(libDir) != LibraryStatus.PASSED) {
					unpackLibrary(parentCL, "lib/" + lib.name,
							new File(libDir, lib.name));
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Unpack the jar file to the target directory.
	 * @param parentCL
	 * @param name
	 * @param dest destination file
	 * @return the unpacked Jar file
	 * @throws IOException
	 */
	private static void unpackLibrary(ClassLoader parentCL, String name, File dest)
			throws IOException {

		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
		InputStream in = parentCL.getResourceAsStream(name);
		byte[] buffer = new byte[4096];
		int len;
		while ((len = in.read(buffer)) != -1) {
			out.write(buffer, 0, len);
		}
		out.close();
	}

	private static VersionInfo embeddedVersion() {
		try {
			ClassLoader parentCL = ChunkyLauncher.class.getClassLoader();
			InputStream in = parentCL.getResourceAsStream("version.json");
			try {
				if (in != null) {
					JsonParser parser = new JsonParser(in);
					return new VersionInfo(parser.parse().object());
				}
			} catch (IOException e) {
			} catch (SyntaxError e) {
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
			}
		} catch (SecurityException e) {
		}
		return null;
	}

	/**
	 * Launch a specific Chunky version
	 * @param parentComponent
	 * @param settings
	 * @return zero on success, non-zero if there is any problem
	 * launching Chunky (waits 200ms to see if everything launched)
	 */
	public int launchChunky(Component parentComponent, LauncherSettings settings, VersionInfo version,
			ChunkyMode mode) {
		List<String> command = buildCommandLine(version, settings);
		if (settings.verboseLauncher ||
				System.getProperty("log4j.logLevel", "WARN").equals("INFO")) {
			System.out.println(commandString(command));
		}
		ProcessBuilder procBuilder = new ProcessBuilder(command);
		final Logger logger;
		if (!settings.headless && settings.debugConsole) {
			DebugConsole console = new DebugConsole(null, settings.closeConsoleOnExit);
			console.setVisible(true);
			logger = console;
		} else {
			logger = new ConsoleLogger();
		}
		try {
			final Process proc = procBuilder.start();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					// kill the subprocess
					proc.destroy();
				}
			});
			final Thread outputScanner = new Thread("Output Logger") {
				@Override
				public void run() {
					InputStream is = proc.getInputStream();
					try {
						byte[] buffer = new byte[4096];
						while (true) {
							int size = is.read(buffer, 0, buffer.length);
							if (size == -1) {
								break;
							}
							logger.appendStdout(buffer, size);
						}
					} catch (IOException e) {
						try {
							is.close();
						} catch (IOException e1) {
						}
					}
				}
			};
			outputScanner.start();
			final Thread errorScanner = new Thread("Error Logger") {
				@Override
				public void run() {
					InputStream is = proc.getErrorStream();
					try {
						byte[] buffer = new byte[4096];
						while (true) {
							int size = is.read(buffer, 0, buffer.length);
							if (size == -1) {
								break;
							}
							logger.appendStderr(buffer, size);
						}
					} catch (IOException e) {
						try {
							is.close();
						} catch (IOException e1) {
						}
					}
				}
			};
			errorScanner.start();
			ShutdownThread shutdownThread = new ShutdownThread(proc, logger, outputScanner, errorScanner);
			shutdownThread.start();
			try {
				if (mode == ChunkyMode.GUI) {
					// just wait a little while to check for startup errors
					Thread.sleep(3000);
					return shutdownThread.exitValue;
				} else {
					// wait until completion so we can return correct exit code
					return shutdownThread.exitValue();
				}
			} catch (InterruptedException e) {
			}
			return 0;
		} catch (IOException e) {
			logger.appendErrorLine(e.getMessage());
			// 3 indicates launcher error
			return 3;
		}
	}

	/**
	 * @param command
	 * @return command in string form
	 */
	public static String commandString(List<String> command) {
		StringBuilder sb = new StringBuilder();
		for (String part: command) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(part);
		}
		return sb.toString();
	}

	public static List<String> buildCommandLine(VersionInfo version, LauncherSettings settings) {
		List<String> cmd = new LinkedList<String>();

		cmd.add(JreUtil.javaCommand(settings.javaDir));
		cmd.add("-Xmx" + settings.memoryLimit + "m");

		String[] parts = settings.javaOptions.split(" ");
		for (String part: parts) {
			if (!part.isEmpty()) {
				cmd.add(part);
			}
		}

		cmd.add("-classpath");
		cmd.add(classpath(version,settings));

		if (settings.verboseLogging) {
			cmd.add("-Dlog4j.logLevel=INFO");
		}

		cmd.add("se.llbit.chunky.main.Chunky");

		parts = settings.chunkyOptions.split(" ");
		for (String part: parts) {
			if (!part.isEmpty()) {
				cmd.add(part);
			}
		}

		return cmd;
	}

	private static String classpath(VersionInfo version, LauncherSettings settings) {
		File chunkyDir = PersistentSettings.getSettingsDirectory();
		File libDir = new File(chunkyDir, "lib");
		List<File> jars = new ArrayList<File>();
		for (VersionInfo.Library library: version.libraries) {
			jars.add(library.getFile(libDir));
		}
		String classpath = "";
		for (File file : jars) {
			if (!classpath.isEmpty()) {
				classpath += File.pathSeparator;
			}
			classpath += file.getAbsolutePath();
		}
		return classpath;
	}

	private static class ShutdownThread extends Thread {
		public volatile int exitValue = 0;
		private final Thread outputScanner;
		private final Thread errorScanner;
		private final Process proc;
		private final Logger logger;
		private boolean finished = false;

		public ShutdownThread(Process proc, Logger logger, Thread output, Thread error) {
			this.proc = proc;
			this.logger = logger;
			this.outputScanner = output;
			this.errorScanner = error;
		}

		public synchronized int exitValue() throws InterruptedException {
			while (!finished) {
				wait();
			}
			return exitValue;
		}

		@Override
		public void run() {
			try {
				outputScanner.join();
			} catch (InterruptedException e) {
			}
			try {
				errorScanner.join();
			} catch (InterruptedException e) {
			}
			try {
				proc.waitFor();
				exitValue = proc.exitValue();
				logger.processExited(exitValue);
			} catch (InterruptedException e) {
			}
			synchronized (this) {
				finished = true;
				notifyAll();
			}
		}
	}

	public static VersionInfo resolveVersion(String name) {
		List<VersionInfo> versions = availableVersions();
		VersionInfo version = VersionInfo.LATEST;
		for (VersionInfo info: versions) {
			if (info.name.equals(name)) {
				version = info;
				break;
			}
		}
		if (version == VersionInfo.LATEST) {
			if (versions.size() > 0) {
				return versions.get(0);
			} else {
				return VersionInfo.NONE;
			}
		} else {
			return version;
		}
	}

	public static boolean canLaunch(VersionInfo version, ChunkyLauncher launcher, boolean reportErrors) {
		if (version == VersionInfo.NONE) {
			// version not available!
			System.err.println("No version installed");
			if (reportErrors) {
				Dialogs.error(launcher,
						"Failed to launch Chunky - there is no local version installed. Try updating.",
						"Failed to Launch");
			}
			return false;
		}
		if (!ChunkyDeployer.checkVersionIntegrity(version.name)) {
			// TODO add some way to fix this??
			System.err.println("Version integrity check failed for version " + version.name);
			if (reportErrors) {
				Dialogs.error(launcher,
						"Version integrity check failed for version " + version.name + ". Try selecting another version.",
						"Failed to Launch");
			}
			return false;
		}
		return true;
	}
}
