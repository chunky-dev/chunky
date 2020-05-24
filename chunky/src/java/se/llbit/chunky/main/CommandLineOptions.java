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
package se.llbit.chunky.main;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.ConsoleProgressListener;
import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.TexturePackLoader;
import se.llbit.json.JsonNumber;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonParser.SyntaxError;
import se.llbit.json.JsonString;
import se.llbit.json.JsonValue;
import se.llbit.json.PrettyPrinter;
import se.llbit.log.Log;
import se.llbit.util.MCDownloader;
import se.llbit.util.NotNull;
import se.llbit.util.StringUtil;
import se.llbit.util.TaskTracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CommandLineOptions {
  enum Mode {
    DEFAULT,
    NOTHING,
    HEADLESS_RENDER,
    SNAPSHOT,
  }

  /**
   * This is the usage output generated for the --help flag.
   */
  private static final String USAGE = StringUtil
      .join("\n", "Usage: mapLoader [OPTIONS] [WORLD DIRECTORY]", "Options:",
          "  -texture <FILE>        use FILE as the texture pack (must be a Zip file)",
          "  -render <SCENE>        render the specified scene (see notes)",
          "  -snapshot <SCENE> [PNG] create a snapshot of the specified scene",
          "  -scene-dir <DIR>       use the directory DIR for loading/saving scenes",
          "  -threads <NUM>         use the specified number of threads for rendering",
          "  -tile-width <NUM>      use the specified job tile width",
          "  -target <NUM>          override target SPP to be NUM in headless mode",
          "  -set <NAME> <VALUE>    set a global configuration option and exit",
          "  -reset <NAME>          reset a global configuration option and exit",
          "  -set <NAME> <VALUE> <SCENE>",
          "                         set a configuration option for a scene and exit",
          "  -reset <NAME>          reset a global option to its default value",
          "  -reset <NAME> <SCENE>  reset an option for a particular scene",
          "  -download-mc <VERSION> download the given Minecraft version and exit",
          "  -list-scenes           print a list of all scenes in the scene directory",
          "  -merge-dump <SCENE> <PATH>",
          "                         merge a render dump into the given scene",
          "  -help                  show this text", "", "Notes:",
          "<SCENE> can be either the path to a Scene Description File ("
              + Scene.EXTENSION + "),",
          "*OR* the name of a scene relative to the scene directory (excluding extension).",
          "If the scene name is an absolute path then the scene directory will be the",
          "parent directory of the Scene Description File, otherwise the scene directory",
          "can be overridden temporarily by the -scene-dir option.", "", "Launcher options:",
          "  --update              download the latest version of Chunky and exit",
          "  --setup               configure memory limit and Java options for Chunky",
          "  --nolauncher          start Chunky as normal, but without opening launcher",
          "  --launcher            forces the launcher window to be displayed",
          "  --version             print the launcher version and exit",
          "  --verbose             verbose logging in the launcher",
          "  --console             show the GUI console in headless mode");

  protected boolean configurationError = false;

  protected Mode mode = Mode.DEFAULT;

  protected ChunkyOptions options = ChunkyOptions.getDefaults();

  static class Range {
    public final int start;
    public final int end;

    public Range(int value) {
      this(value, value);
    }

    public Range(int start, int end) {
      this.start = start;
      this.end = end;
    }
  }

  static class ArgumentError extends Exception {
    public ArgumentError(String message) {
      super(message);
    }
  }

  static class OptionHandler {
    private final String flag;
    private final Range numOptions;
    private final Consumer<List<String>> consumer;
    private final Runnable errorHandler;

    public OptionHandler(String flag, Range numOptions, Consumer<List<String>> consumer) {
      this.flag = flag;
      this.numOptions = numOptions;
      this.consumer = consumer;
      errorHandler = null;
    }

    public OptionHandler(String flag, Range numOptions, Consumer<List<String>> consumer,
        Runnable errorHandler) {
      this.flag = flag;
      this.numOptions = numOptions;
      this.consumer = consumer;
      this.errorHandler = errorHandler;
    }

    /**
     * Run the handler for this option.
     *
     * @param args the arguments after the current option
     * @return the remaining arguments after removing those used by this option
     * @throws ArgumentError
     */
    List<String> handle(List<String> args) throws ArgumentError {
      List<String> arguments = new LinkedList<>(args);  // Create local copy to avoid side effects.
      List<String> optionArguments = new ArrayList<>();
      for (int i = 0; i < numOptions.end; i += 1) {
        if (arguments.isEmpty() || arguments.get(0).startsWith("-")) {
          if (i >= numOptions.start) {
            // We don't need to have the maximum number of options.
            break;
          }
          if (errorHandler != null) {
            errorHandler.run();
            // Skip handling the rest of the command line.
            return Collections.emptyList();
          } else {
            throw new ArgumentError(String.format(
                "Missing argument for %s option. Found %d arguments, expected %d.",
                flag, i + 1, numOptions.start));
          }
        }
        optionArguments.add(arguments.remove(0));
      }
      consumer.accept(new ArrayList<>(optionArguments));  // Create copy to avoid side effects.
      return arguments;
    }
  }

  private Map<String, OptionHandler> optionHandlers = new HashMap<>();

  public CommandLineOptions(String[] args) {
    boolean selectedWorld = false;

    options.sceneDir = PersistentSettings.getSceneDirectory();

    registerOption("-texture", new Range(1),
        arguments -> options.texturePack = arguments.get(0));

    registerOption("-scene-dir", new Range(1),
        arguments -> options.sceneDir = new File(arguments.get(0)));

    registerOption("-render", new Range(1),
        arguments -> {
          mode = Mode.HEADLESS_RENDER;
          options.sceneName = arguments.get(0);
        },
        () -> {
          System.err.println("You must specify a scene name for the -render command");
          printAvailableScenes();
          configurationError = true;
        });

    registerOption("-f", new Range(0), arguments -> {
      options.force = true;
    });

    registerOption("-target", new Range(1),
        arguments -> options.target = Math.max(1, Integer.parseInt(arguments.get(0))));

    registerOption("-threads", new Range(1),
        arguments -> options.renderThreads = Math.max(1, Integer.parseInt(arguments.get(0))));

    registerOption("-tile-width", new Range(1),
        arguments -> options.tileWidth = Math.max(1, Integer.parseInt(arguments.get(0))));

    registerOption("-version", new Range(0), arguments -> {
      mode = Mode.NOTHING;
      System.out.println("Chunky " + Version.getVersion());
    });

    registerOption(new String[] {"-help", "-h", "-?", "--help"}, new Range(0), arguments -> {
      mode = Mode.NOTHING;
      printUsage();
      System.out.println("The default scene directory is " + PersistentSettings.getSceneDirectory());
    });

    registerOption("-snapshot", new Range(1, 2), arguments -> {
      mode = Mode.SNAPSHOT;
      options.sceneName = arguments.get(0);
      if (arguments.size() == 2) {
        options.imageOutputFile = arguments.get(1);
      }
    }, () -> {
      System.err.println("You must specify a scene name for the -snapshot command!");
      printAvailableScenes();
      configurationError = true;
    });

    registerOption("-list-scenes", new Range(0), arguments -> {
      mode = Mode.NOTHING;
      printAvailableScenes();
    });

    registerOption("-set", new Range(2, 3), arguments -> {
      mode = Mode.NOTHING;
      if (arguments.size() == 3) {
        options.sceneName = arguments.get(2);
        try {
          File file = options.getSceneDescriptionFile();
          JsonObject desc = readSceneJson(file);
          String name = arguments.get(0);
          String value = arguments.get(1);
          System.out.format("%s <- %s%n", name, value);
          String[] path = name.split("\\.");
          JsonObject obj = desc;
          for (int j = 0; j < path.length - 1; ++j) {
            obj = obj.get(path[j]).object();
          }
          JsonValue jsonValue;
          try {
            jsonValue = new JsonNumber(Integer.parseInt(value));
          } catch (Exception e) {
            jsonValue = new JsonString(value);
          }
          obj.set(path[path.length - 1], jsonValue);
          writeSceneJson(file, desc);
          System.out.println("Updated scene " + file.getAbsolutePath());
        } catch (SyntaxError e) {
          System.err.println("JSON syntax error");
        } catch (IOException e) {
          System.err.println("Failed to write/load Scene Description File: " + e.getMessage());
        }
      } else if (arguments.size() == 2) {
        String name = arguments.get(0);
        String value = arguments.get(1);
        try {
          PersistentSettings.setIntOption(name, Integer.parseInt(value));
        } catch (Exception e) {
          PersistentSettings.setStringOption(name, value);
        }
      } else {
        throw new Error("Wrong number of option arguments.");
      }
    });

    registerOption("-reset", new Range(1), arguments -> {
      mode = Mode.NOTHING;
      if (arguments.size() == 2) {
        options.sceneName = arguments.get(1);
        try {
          File file = options.getSceneDescriptionFile();
          JsonObject desc = readSceneJson(file);
          String name = arguments.get(0);
          System.out.println("- " + name);
          String[] path = name.split("\\.");
          JsonObject obj = desc;
          for (int j = 0; j < path.length - 1; ++j) {
            obj = obj.get(path[j]).object();
          }
          obj.remove(name);
          writeSceneJson(file, desc);
          System.out.println("Updated scene " + file.getAbsolutePath());
        } catch (SyntaxError e) {
          System.err.println("JSON syntax error");
        } catch (IOException e) {
          System.err.println("Failed to write/load Scene Description File: " + e.getMessage());
        }
      } else if (arguments.size() == 1) {
        PersistentSettings.resetOption(arguments.get(0));
      } else {
        throw new Error("Wrong number of option arguments.");
      }
    });

    registerOption("-download-mc", new Range(1), arguments -> {
      mode = Mode.NOTHING;
      String version = arguments.get(0);
      try {
        File resourcesDir = new File(PersistentSettings.settingsDirectory(), "resources");
        if (!resourcesDir.exists()) {
          //noinspection ResultOfMethodCallIgnored
          resourcesDir.mkdir();
        }
        if (!resourcesDir.isDirectory()) {
          System.err.println("Failed to create destination directory " + resourcesDir.getAbsolutePath());
        }
        System.out.println("Downloading Minecraft " + version + "...");
        MCDownloader.downloadMC(version, new File(resourcesDir, "minecraft.jar"));
        System.out.println("Done!");
      } catch (MalformedURLException e) {
        System.err.println("Malformed URL (" + e.getMessage() + ")");
      } catch (FileNotFoundException e) {
        System.err.println("File not found (" + e.getMessage() + ")");
      } catch (IOException e) {
        System.err.println("Download failed (" + e.getMessage() + ")");
      }
    });

    registerOption("-merge-dump", new Range(2), arguments -> {
      mode = Mode.NOTHING;
      options.sceneName = arguments.get(0);
      String dumpPath = arguments.get(1);
      File dumpfile = new File(dumpPath);
      if (!dumpfile.isFile()) {
        Log.error("Not a valid render dump file: " + dumpPath);
        configurationError = true;
        return;
      }
      File sceneFile = options.getSceneDescriptionFile();
      if (!sceneFile.isFile()) {
        Log.error("Not a valid scene: " + options.sceneName);
        configurationError = true;
        return;
      }
      try {
        Scene scene = new Scene();
        try (FileInputStream in = new FileInputStream(sceneFile)) {
          scene.loadDescription(in);
        }
        RenderContext context = new RenderContext(new Chunky(options));
        TaskTracker taskTracker = new TaskTracker(new ConsoleProgressListener(),
            TaskTracker.Task::new,
            (tracker, previous, name, size) -> new TaskTracker.Task(tracker, previous, name, size) {
              @Override public void update() {
                // Don't report task state to progress listener.
              }
            });
        scene.loadDump(context, taskTracker); // Load the render dump.
        Log.info("Original scene SPP: " + scene.spp);
        scene.mergeDump(dumpfile, taskTracker);
        Log.info("Current scene SPP: " + scene.spp);
        scene.saveDump(context, taskTracker);
        try (FileOutputStream out = new FileOutputStream(sceneFile)) {
          scene.saveDescription(out);
        }
      } catch (IOException e) {
        Log.error("Failed to merge render dump.", e);
      }
    });

    // When mode is set to Mode.NOTHING, then an option handler has performed
    // something and we should quit.
    // If configurationError is set to true then an option handler encountered an
    // error and we should quit.
    List<String> arguments = new LinkedList<>(Arrays.asList(args));
    while (!arguments.isEmpty() && !configurationError && mode != Mode.NOTHING) {
      // Remove first argument.
      String argument = arguments.remove(0);
      if (optionHandlers.containsKey(argument)) {
        try {
          arguments = optionHandlers.get(argument).handle(arguments);
        } catch (ArgumentError error) {
          System.err.println(error.getMessage());
          printUsage();
          configurationError = true;
        }
      } else if (!argument.startsWith("-") && !selectedWorld) {
        options.worldDir = new File(argument);
        selectedWorld = true;
      } else {
        System.err.println("Unrecognized option: " + argument);
        printUsage();
        configurationError = true;
      }
    }

    if (options.sceneName != null
        && options.sceneName.endsWith(Scene.EXTENSION)) {
      File possibleSceneFile = new File(options.sceneName);
      if (possibleSceneFile.isFile()) {
        options.sceneDir = possibleSceneFile.getParentFile();
        String sceneName = possibleSceneFile.getName();
        options.sceneName =
            sceneName.substring(0, sceneName.length() - Scene.EXTENSION.length());
      }
    }

    if (!configurationError && mode != Mode.NOTHING && mode != Mode.SNAPSHOT) {
      if (options.texturePack == null || options.texturePack.isEmpty()) {
        options.texturePack = PersistentSettings.getLastTexturePack();
      }
      TexturePackLoader.setTexturePacks(options.texturePack);
      TexturePackLoader.loadTexturePacks(options.texturePack, false);
    }
  }

  /** Register an option handler for a single flag. */
  private void registerOption(String flag, Range numOptions, Consumer<List<String>> consumer) {
    OptionHandler handler = new OptionHandler(flag, numOptions, consumer);
    optionHandlers.put(flag, handler);
  }

  /** Register an option handler for multiple flags. */
  private void registerOption(String[] flags, Range numOptions, Consumer<List<String>> consumer) {
    OptionHandler handler = new OptionHandler(flags[0], numOptions, consumer);
    for (String flag : flags) {
      optionHandlers.put(flag, handler);
    }
  }

  /** Register an option handler with special error handling. */
  private void registerOption(String flag, Range numOptions, Consumer<List<String>> consumer,
      Runnable errorHandler) {
    OptionHandler handler = new OptionHandler(flag, numOptions, consumer, errorHandler);
    optionHandlers.put(flag, handler);
  }

  private void printAvailableScenes() {
    System.err.println("Scene directory: " + options.sceneDir.getAbsolutePath());
    List<File> fileList = SceneHelper.getAvailableSceneFiles(options.sceneDir);
    Collections.sort(fileList);
    if (!fileList.isEmpty()) {
      System.err.println("Available scenes:");
      for (File file : fileList) {
        String name = file.getName();
        name = name.substring(0, name.length() - Scene.EXTENSION.length());
        System.err.println("\t" + name);
      }
    } else {
      System.err.println("No scenes found. Is the scene directory correct?");
    }
  }

  private void printUsage() {
    System.out.println("Chunky " + Version.getVersion());
    System.out.println(USAGE);
    System.out.println();
  }

  private static JsonObject readSceneJson(File file) throws IOException, SyntaxError {
    try (FileInputStream in = new FileInputStream(file)) {
      JsonParser parser = new JsonParser(in);
      return parser.parse().object();
    }
  }

  private static void writeSceneJson(File file, JsonObject desc) throws IOException {
    try (FileOutputStream out = new FileOutputStream(file)) {
      PrettyPrinter pp = new PrettyPrinter("  ", new PrintStream(out));
      desc.prettyPrint(pp);
    }
  }
}
