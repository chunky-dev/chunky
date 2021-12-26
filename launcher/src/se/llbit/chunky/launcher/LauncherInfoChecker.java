/* Copyright (c) 2021 Chunky contributors
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

import se.llbit.json.JsonParser;
import se.llbit.json.JsonParser.SyntaxError;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Consumer;

public class LauncherInfoChecker extends Thread {
    private final LauncherSettings settings;
    private final Consumer<String> errorListener;
    private final Consumer<LauncherInfo> completionListener;

    public LauncherInfoChecker(LauncherSettings settings,
                               Consumer<String> errorListener,
                               Consumer<LauncherInfo> completionListener) {
        this.settings = settings;
        this.errorListener = errorListener;
        this.completionListener = completionListener;
    }

    @Override
    public void run() {
        String url = settings.getResourceUrl("launcher.json");
        try {
            URL launcherJson = new URL(url);
            LauncherInfo info = null;
            try (
                InputStream in = launcherJson.openStream();
                JsonParser parser = new JsonParser(in);
            ) {
                info = new LauncherInfo(parser.parse().object());
            }
            completionListener.accept(info);
        } catch (MalformedURLException e) {
            System.err.println("Malformed launcher info URL.");
            errorListener.accept("Malformed launcher info/update site URL: " + url);
        } catch (IOException e) {
            System.err.println("Failed to fetch launcher info: " + e.getMessage());
            errorListener.accept("Failed to fetch launcher info from URL: " + url);
        } catch (SyntaxError e) {
            System.err.println("Version info JSON error: " + e.getMessage());
            errorListener.accept("Downloaded corrupt launcher info.");
        } catch (Exception e) {
            System.err.println("Unhandled exception: " + e.getMessage());
            errorListener.accept("Could not update release channels.");
        }
    }
}
