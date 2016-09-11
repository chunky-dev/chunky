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

import org.jastadd.util.PrettyPrinter;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.ConsoleProgressListener;
import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.renderer.SimpleRenderListener;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.SceneDescription;
import se.llbit.chunky.resources.MinecraftFinder;
import se.llbit.chunky.resources.TexturePackLoader;
import se.llbit.chunky.resources.TexturePackLoader.TextureLoadingError;
import se.llbit.json.JsonNumber;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonParser.SyntaxError;
import se.llbit.json.JsonString;
import se.llbit.json.JsonValue;
import se.llbit.util.MCDownloader;
import se.llbit.util.StringUtil;
import se.llbit.util.TaskTracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;

public class CommandLineOptions {
  enum Mode {
    DEFAULT,
    NOTHING,
    HEADLESS_RENDER,
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
          "  -reset <NAME> <SCENE>  reset a configuration option for a scene and exit",
          "  -reset <NAME> <SCENE>  reset a configuration option for a scene and exit",
          "  -download-mc <VERSION> download the given Minecraft version and exit",
          "  -help                  show this text", "", "Notes:",
          "<SCENE> can be either the path to a Scene Description File ("
              + SceneDescription.SCENE_DESCRIPTION_EXTENSION + "),",
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

  protected boolean confError = false;

  protected Mode mode = Mode.DEFAULT;

  protected ChunkyOptions options = ChunkyOptions.getDefaults();

  public CommandLineOptions(String[] args) {
    boolean selectedWorld = false;

    options.sceneDir = PersistentSettings.getSceneDirectory();

    // TODO: The command-line argument parsing should be refactored.
    for (int i = 0; i < args.length; ++i) {
      if (args[i].equals("-texture") && args.length > i + 1) {
        options.texturePack = args[++i];
      } else if (args[i].equals("-scene-dir")) {
        if (i + 1 == args.length) {
          System.err.println("Missing argument for -scene-dir option");
          confError = true;
          break;
        } else {
          options.sceneDir = new File(args[i + 1]);
          i += 1;
        }
      } else if (args[i].equals("-render")) {
        if (i + 1 == args.length) {
          System.err.println("You must specify a scene name for the -render command");
          printAvailableScenes();
          confError = true;
          break;
        } else {
          options.sceneName = args[i + 1];
          i += 1;
        }
      } else if (args[i].equals("-target")) {
        if (i + 1 == args.length) {
          System.err.println("Missing argument for -target option");
          confError = true;
          break;
        } else {
          options.target = Math.max(1, Integer.parseInt(args[i + 1]));
          i += 1;
        }
      } else if (args[i].equals("-threads")) {
        if (i + 1 == args.length) {
          System.err.println("Missing argument for -threads option");
          confError = true;
          break;
        } else {
          options.renderThreads = Math.max(1, Integer.parseInt(args[i + 1]));
          i += 1;
        }
      } else if (args[i].equals("-tile-width")) {
        if (i + 1 == args.length) {
          System.err.println("Missing argument for -tile-width option");
          confError = true;
          break;
        } else {
          options.tileWidth = Math.max(1, Integer.parseInt(args[i + 1]));
          i += 1;
        }
      } else if (args[i].equals("-version")) {
        System.out.println("Chunky " + Version.getVersion());
        mode = Mode.NOTHING;
        break;
      } else if (args[i].equals("-h") || args[i].equals("-?") || args[i].equals("-help") || args[i]
          .equals("--help")) {
        printUsage();
        System.out
            .println("The default scene directory is " + PersistentSettings.getSceneDirectory());
        mode = Mode.NOTHING;
      } else if (args[i].equals("-snapshot")) {
        mode = Mode.NOTHING;
        if (args.length > i + 1) {
          options.sceneName = args[i + 1];
          String pngFileName = "";
          if (args.length > i + 2) {
            pngFileName = args[i + 2];
          }
          try {
            File file = getSceneDescriptionFile(options);
            Scene scene = new Scene();
            FileInputStream in = new FileInputStream(file);
            scene.loadDescription(in);
            RenderContext context = new RenderContext(options);
            SimpleRenderListener listener = new SimpleRenderListener(new ConsoleProgressListener());
            scene.setCanvasSize(scene.width, scene.height);
            scene.loadDump(context, listener);
            if (pngFileName.isEmpty()) {
              pngFileName = scene.name + "-" + scene.spp + ".png";
            }
            scene.saveFrame(new File(pngFileName), new TaskTracker(listener.progressListener()));
            System.out.println("Saved snapshot to " + pngFileName);
          } catch (IOException e) {
            System.err.println("Failed to dump snapshot: " + e.getMessage());
          }
          return;
        } else {
          System.err.println("You must specify a scene name for the -snapshot command!");
          printAvailableScenes();
          confError = true;
          break;
        }
      } else if (args[i].equals("-set")) {
        mode = Mode.NOTHING;
        if (args.length > i + 3) {
          options.sceneName = args[i + 3];
          try {
            File file = getSceneDescriptionFile(options);
            JsonObject desc = readSceneJson(file);
            String name = args[i + 1];
            String value = args[i + 2];
            System.out.println(name + " <- " + value);
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
          return;
        } else if (args.length > i + 2) {
          String name = args[i + 1];
          String value = args[i + 2];
          try {
            PersistentSettings.setIntOption(name, Integer.parseInt(value));
          } catch (Exception e) {
            PersistentSettings.setStringOption(name, value);
          }
          return;
        } else {
          System.err.println("Too few arguments for -set option!");
          confError = true;
          break;
        }
      } else if (args[i].equals("-reset")) {
        mode = Mode.NOTHING;
        if (args.length > i + 2) {
          options.sceneName = args[i + 2];
          try {
            File file = getSceneDescriptionFile(options);
            JsonObject desc = readSceneJson(file);
            String name = args[i + 1];
            System.out.println("- " + name);
            String[] path = name.split("\\.");
            JsonObject obj = desc;
            for (int j = 0; j < path.length - 1; ++j) {
              obj = obj.get(path[j]).object();
            }
            for (int j = 0; j < obj.getNumMember(); ++j) {
              if (obj.getMember(j).getName().equals(name)) {
                obj.getMemberList().removeChild(j);
                break;
              }
            }
            writeSceneJson(file, desc);
            System.out.println("Updated scene " + file.getAbsolutePath());
          } catch (SyntaxError e) {
            System.err.println("JSON syntax error");
          } catch (IOException e) {
            System.err.println("Failed to write/load Scene Description File: " + e.getMessage());
          }
          return;
        } else if (args.length > i + 1) {
          PersistentSettings.resetOption(args[i + 1]);
          return;
        } else {
          System.err.println("Too few arguments for -reset option!");
          confError = true;
          break;
        }
      } else if (args[i].equals("-download-mc")) {
        mode = Mode.NOTHING;
        if (args.length > i + 1) {
          String version = args[i + 1];
          try {
            File dir = new File(PersistentSettings.settingsDirectory(), "resources");
            if (!dir.exists()) {
              //noinspection ResultOfMethodCallIgnored
              dir.mkdir();
            }
            if (!dir.isDirectory()) {
              System.err.println("Failed to create destination directory " + dir.getAbsolutePath());
            }
            System.out.println("Downloading Minecraft " + version + "...");
            MCDownloader.downloadMC(version, dir);
            System.out.println("Done!");
          } catch (MalformedURLException e) {
            System.err.println("Malformed URL (" + e.getMessage() + ")");
          } catch (FileNotFoundException e) {
            System.err.println("File not found (" + e.getMessage() + ")");
          } catch (IOException e) {
            System.err.println("Download failed (" + e.getMessage() + ")");
          }
          i += 1;
        } else {
          System.err.println("Missing argument for -download-mc option");
          confError = true;
          break;
        }
      } else if (!args[i].startsWith("-") && !selectedWorld) {
        options.worldDir = new File(args[i]);
      } else {
        System.err.println("Unrecognized argument: " + args[i]);
        printUsage();
        confError = true;
        break;
      }
    }

    if (options.sceneName != null && options.sceneName
        .endsWith(SceneDescription.SCENE_DESCRIPTION_EXTENSION)) {
      File possibleSceneFile = new File(options.sceneName);
      if (possibleSceneFile.isFile()) {
        options.sceneDir = possibleSceneFile.getParentFile();
        options.sceneName = possibleSceneFile.getName();
      }
    }

    if (!confError && mode != Mode.NOTHING) {
      try {
        if (options.texturePack != null) {
          TexturePackLoader.loadTexturePack(new File(options.texturePack), false);
        } else {
          String lastTexturePack = PersistentSettings.getLastTexturePack();
          if (!lastTexturePack.isEmpty()) {
            try {
              TexturePackLoader.loadTexturePack(new File(lastTexturePack), false);
            } catch (TextureLoadingError e) {
              System.err.println(e.getMessage());
              System.err.println("Loading default Minecraft textures");
              try {
                TexturePackLoader.loadTexturePack(MinecraftFinder.getMinecraftJarNonNull(), false);
              } catch (FileNotFoundException e1) {
                System.err.println("Minecraft Jar not found! Using placeholder textures.");
              }
            }
          } else {
            try {
              TexturePackLoader.loadTexturePack(MinecraftFinder.getMinecraftJarNonNull(), false);
            } catch (FileNotFoundException e1) {
              System.err.println("Minecraft Jar not found! Using placeholder textures.");
            }
          }
        }
      } catch (TextureLoadingError e) {
        System.err.println(e.getMessage());
      }
    }

    if (options.sceneName != null) {
      mode = Mode.HEADLESS_RENDER;
    }
  }

  private void printAvailableScenes() {
    File sceneDir = PersistentSettings.getSceneDirectory();
    System.err.println("Scene directory: " + sceneDir.getAbsolutePath());
    List<File> fileList = SceneHelper.getAvailableSceneFiles(sceneDir);
    Collections.sort(fileList);
    if (!fileList.isEmpty()) {
      System.err.println("Available scenes:");
      for (File file : fileList) {
        String name = file.getName();
        name = name.substring(0,
            name.length() - SceneDescription.SCENE_DESCRIPTION_EXTENSION.length());
        System.err.println("\t" + name);
      }
    }
  }

  private void printUsage() {
    System.out.println("Chunky " + Version.getVersion());
    System.out.println(USAGE);
    System.out.println();
  }

  /**
   * Retrieve the scene description file for a specific scene.
   *
   * @return the scene description file handle
   */
  private static File getSceneDescriptionFile(ChunkyOptions options) {
    if (options.sceneName.endsWith(SceneDescription.SCENE_DESCRIPTION_EXTENSION)) {
      return new File(options.sceneName);
    } else {
      if (options.sceneDir != null) {
        return new File(options.sceneDir,
            options.sceneName + SceneDescription.SCENE_DESCRIPTION_EXTENSION);
      } else {
        return new File(PersistentSettings.getSceneDirectory(),
            options.sceneName + SceneDescription.SCENE_DESCRIPTION_EXTENSION);
      }
    }
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
