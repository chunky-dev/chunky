package se.llbit.chunky.launcher;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.launcher.VersionInfo.Library;
import se.llbit.chunky.launcher.VersionInfo.LibraryStatus;

public class ConsoleUpdater {
	public static void update(VersionInfo version) {
		File chunkyDir = PersistentSettings.getSettingsDirectory();
		File libDir = new File(chunkyDir, "lib");
		if (!libDir.isDirectory()) {
			libDir.mkdirs();
		}
		File versionsDir = new File(chunkyDir, "versions");
		if (!versionsDir.isDirectory()) {
			versionsDir.mkdirs();
		}
		Collection<VersionInfo.Library> neededLibraries =
				new LinkedList<VersionInfo.Library>();
		for (Library lib: version.libraries) {
			LibraryStatus libStatus = lib.testIntegrity(libDir);
			if (libStatus != LibraryStatus.PASSED && libStatus != LibraryStatus.INCOMPLETE_INFO) {
				neededLibraries.add(lib);

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
				System.out.println("Downloading " + lib + " [" + libSize + "]...");

				if (!downloadLibrary(libDir, lib)) {
					return;
				}
			}
		}
		try {
			File versionFile = new File(versionsDir, version.name + ".json");
			version.writeTo(versionFile);
		} catch (IOException e) {
			System.err.println("Failed to update version info. Please try again later.");
		}
	}

	private static boolean downloadLibrary(File libDir, Library lib) {
		DownloadStatus result = DownloadStatus.DOWNLOAD_FAILED;
		if (!lib.url.isEmpty()) {
			result = UpdateDialog.tryDownload(libDir, lib, lib.url);
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
			result = UpdateDialog.tryDownload(libDir, lib, defaultUrl);
		}
		switch (result) {
		case SUCCESS:
			return true;
		case MALFORMED_URL:
			System.err.println("Malformed URL: " + defaultUrl);
			return false;
		case FILE_NOT_FOUND:
			System.err.println("File not found: " + defaultUrl);
			return false;
		case DOWNLOAD_FAILED:
			System.err.println("Download failed: " + defaultUrl);
			return false;
		default:
			return false;
		}
	}

}
