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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import se.llbit.json.JsonParser;
import se.llbit.json.JsonParser.SyntaxError;

/**
 * Check for update and run update dialog (or just update in headless mode)
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class UpdateChecker extends Thread {
	private final LauncherSettings settings;
	private final UpdateListener listener;

	public UpdateChecker(LauncherSettings settings, UpdateListener listener) {
		this.settings = settings;
		this.listener = listener;
	}

	@Override public void run() {
		try {
			if (!tryUpdate()) {
				listener.noUpdateAvailable();
			}
		} catch (MalformedURLException e1) {
			System.err.println("Malformed version info url");
			listener.updateError("Failed to fetch latest version info online.");
		} catch (IOException e1) {
			System.err.println("Failed to fetch version info " + e1.getMessage());
			listener.updateError("Failed to fetch latest version info online. The server may be down right now.");
		} catch (SyntaxError e1) {
			System.err.println("Version info JSON error " + e1.getMessage());
			listener.updateError("The downloaded version info was corrupt. Can not update at this time.");
		} catch (Throwable e1) {
			System.err.println("Uncaught exception: " + e1.getMessage());
			listener.updateError("Can not update at this time.");
		}
	}

	private boolean tryUpdate() throws IOException, SyntaxError {
		List<VersionInfo> candidates = new LinkedList<VersionInfo>();

		candidates.add(getVersion("http://chunkyupdate.llbit.se/latest.json"));

		if (settings.downloadSnapshots) {
			candidates.add(getVersion("http://chunkyupdate.llbit.se/snapshot.json"));
		}

		// filter out corrupt versions
		Iterator<VersionInfo> iter = candidates.iterator();
		while (iter.hasNext()) {
			if (!iter.next().isValid()) {
				iter.remove();
			}
		}

		if (candidates.isEmpty()) {
			listener.updateError("The downloaded version info was corrupt. Can not update at this moment.");
			return false;
		} else {
			// find latest candidate
			VersionInfo latest = candidates.get(0);
			for (VersionInfo candidate: candidates) {
				if (candidate.compareTo(latest) < 0) {
					latest = candidate;
				}
			}

			// check if more recent version than candidate is already installed
			List<VersionInfo> versions = ChunkyDeployer.availableVersions();
			iter = versions.iterator();
			while (iter.hasNext()) {
				VersionInfo available = iter.next();
				if (available.compareTo(latest) <= 0 &&
					ChunkyDeployer.checkVersionIntegrity(available.name)) {
					// more recent version already installed and not corrupt
					return false;
				}
			}

			// install the candidate!
			listener.updateAvailable(latest);
			return true;
		}
	}

	private VersionInfo getVersion(String url) throws IOException, SyntaxError {
		URL latestJson = new URL(url);
		InputStream in = latestJson.openStream();
		JsonParser parser = new JsonParser(in);
		VersionInfo version = new VersionInfo(parser.parse().object());
		in.close();
		return version;
	}
}
