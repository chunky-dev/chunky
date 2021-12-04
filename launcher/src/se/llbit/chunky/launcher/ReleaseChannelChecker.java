package se.llbit.chunky.launcher;

import se.llbit.json.JsonParser;
import se.llbit.json.JsonParser.SyntaxError;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Consumer;

public class ReleaseChannelChecker extends Thread {
    private final LauncherSettings settings;
    private final Consumer<String> errorListener;
    private final Runnable completionListener;

    public ReleaseChannelChecker(LauncherSettings settings,
                                 Consumer<String> errorListener,
                                 Runnable completionListener) {
        this.settings = settings;
        this.errorListener = errorListener;
        this.completionListener = completionListener;
    }

    @Override
    public void run() {
        String url = settings.getResourceUrl("launcher.json");
        try {
            URL launcherJson = new URL(url);
            try (
                InputStream in = launcherJson.openStream();
                JsonParser parser = new JsonParser(in);
            ) {
                LauncherInfo info = new LauncherInfo(parser.parse().object());
                settings.releaseChannels = info.channels;
                int index = settings.releaseChannels.indexOf(settings.selectedChannel);
                if (index == -1) index = 0;
                settings.selectedChannel = settings.releaseChannels.get(index);
            }
            settings.save();
            completionListener.run();
        } catch (MalformedURLException e) {
            System.err.println("Malformed launcher info URL.");
            errorListener.accept("Malformed launcher info/update site URL: " + url);
        } catch (IOException e) {
            System.err.println("Failed to fetch launcher info  " + e.getMessage());
            errorListener.accept("Failed to fetch launcher info from URL: " + url);
        } catch (SyntaxError e) {
            System.err.println("Version info JSON error: " + e.getMessage());
            errorListener.accept("Downloaded corrupt launcher info.");
        } catch (Throwable e) {
            System.err.println("Unhandled exception: " + e.getMessage());
            errorListener.accept("Could not update release channels.");
        }
    }
}
