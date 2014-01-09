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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;

import org.jastadd.util.PrettyPrinter;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.scene.SceneDescription;
import se.llbit.chunky.renderer.ui.SceneSelector;
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

public class CommandLineOptions {
	enum Mode {
		DEFAULT,
		NO_OP,
		HEADLESS_RENDER,
		HEADLESS_BENCHMARK,
	}

	/**
	 * The help string
	 */
	private static final String USAGE =
		"Usage: chunky [OPTIONS] [WORLD DIRECTORY]\n" +
		"Options:\n" +
		"  -texture <FILE>        use FILE as the texture pack (must be a Zip file)\n" +
		"  -render <SCENE>        render the specified scene (see notes)\n" +
		"  -scene-dir <DIR>       use the directory DIR for loading/saving scenes\n" +
		"  -benchmark             run the benchmark and exit\n" +
		"  -threads <NUM>         use the specified number of threads for rendering\n" +
		"  -tile-width <NUM>      use the specified job tile width\n" +
		"  -target <NUM>          override target SPP to be NUM in headless mode\n" +
		"  -opencl                enables OpenCL rendering in the GUI\n" +
		"  -set <NAME> <VALUE>    set a global configuration option and exit\n" +
		"  -reset <NAME>          reset a global configuration option and exit\n" +
		"  -set <NAME> <VALUE> <SCENE>\n" +
		"                         set a configuration option for a scene and exit\n" +
		"  -reset <NAME> <SCENE>  reset a configuration option for a scene and exit\n" +
		"  -reset <NAME> <SCENE>  reset a configuration option for a scene and exit\n" +
		"  -download-mc <VERSION> download the given Minecraft version and exit\n" +
		"  -help                  show this text\n" +
		"\n" +
		"Notes:\n" +
		"<SCENE> can be either the path to a Scene Description File (" + SceneDescription.SCENE_DESCRIPTION_EXTENSION + "),\n" +
		"*OR* the name of a scene relative to the scene directory (excluding extension).\n" +
		"If the scene name is an absolute path then the scene directory will be the\n" +
		"parent directory of the Scene Description File, otherwise the scene directory\n" +
		"can be overridden temporarily by the -scene-dir option.\n" +
		"\n" +
		"Launcher options:\n" +
		"  --nolauncher          start Chunky as normal, but without opening launcher\n" +
		"  --launcher            forces the launcher window to be displayed\n" +
		"  --verbose             verbose logging in the launcher\n";

	protected boolean confError = false;

	protected Mode mode = Mode.DEFAULT;

	protected ChunkyOptions options = new ChunkyOptions();

	public CommandLineOptions(String[] args) {
		boolean selectedWorld = false;

		// parse arguments
		// TODO in serious need of refactoring
		for (int i = 0; i < args.length; ++i) {
			if (args[i].equals("-texture") && args.length > i+1) {
				options.texturePack = args[++i];
			} else if (args[i].equals("-scene-dir")) {
				if (i+1 == args.length) {
					System.err.println("Missing argument for -scene-dir option");
					confError = true;
					break;
				} else {
					options.sceneDir = new File(args[i+1]);
					i += 1;
				}
			} else if (args[i].equals("-render")) {
				if (i+1 == args.length) {
					System.err.println("Missing argument for -render option");
					File sceneDir = PersistentSettings.getSceneDirectory();
					System.err.println("Scene directory: " +
							sceneDir.getAbsolutePath());
					List<File> fileList = SceneSelector.getAvailableSceneFiles(sceneDir);
					Collections.sort(fileList);
					if (!fileList.isEmpty()) {
						System.err.println("Available scenes:");
						for (File file: fileList) {
							String name = file.getName();
							name = name.substring(0, name.length() -
								SceneDescription.SCENE_DESCRIPTION_EXTENSION.length());
							System.err.println("\t" + name);
						}
					}
					confError = true;
					break;
				} else {
					options.sceneName = args[i+1];
					i += 1;
				}
			} else if (args[i].equals("-benchmark")) {
				mode = Mode.HEADLESS_BENCHMARK;
			} else if (args[i].equals("-target")) {
				if (i+1 == args.length) {
					System.err.println("Missing argument for -target option");
					confError = true;
					break;
				} else {
					options.target = Math.max(1, Integer.parseInt(args[i+1]));
					i += 1;
				}
			} else if (args[i].equals("-threads")) {
				if (i+1 == args.length) {
					System.err.println("Missing argument for -threads option");
					confError = true;
					break;
				} else {
					options.renderThreads = Math.max(1, Integer.parseInt(args[i+1]));
					i += 1;
				}
			} else if (args[i].equals("-tile-width")) {
				if (i+1 == args.length) {
					System.err.println("Missing argument for -tile-width option");
					confError = true;
					break;
				} else {
					options.tileWidth = Math.max(1, Integer.parseInt(args[i+1]));
					i += 1;
				}
			} else if (args[i].equals("-opencl")) {
				options.openCLEnabled = true;
			} else if (args[i].equals("-h") || args[i].equals("-?") || args[i].equals("-help") || args[i].equals("--help")) {
				System.out.println("Chunky " + Version.getVersion());
				System.out.println(USAGE);
				System.out.println();
				System.out.println("The default scene directory is " + PersistentSettings.getSceneDirectory());
				mode = Mode.NO_OP;
			} else if (args[i].equals("-set")) {
				mode = Mode.NO_OP;
				if (args.length > i+3) {
					options.sceneName = args[i+3];
					try {
						File file = getSceneFile(options);
						JsonObject desc = readSceneJson(file);
						String name = args[i+1];
						String value = args[i+2];
						System.out.println(name + " <- " + value);
						String[] path = name.split("\\.");
						JsonObject obj = desc;
						for (int j = 0; j < path.length-1; ++j) {
							obj = obj.get(path[j]).object();
						}
						JsonValue jsonValue;
						try {
							jsonValue = new JsonNumber(Integer.parseInt(value));
						} catch (Exception e) {
							jsonValue = new JsonString(value);
						}
						obj.set(path[path.length-1], jsonValue);
						writeSceneJson(file, desc);
						System.out.println("Updated scene " + file.getAbsolutePath());
					} catch (SyntaxError e) {
						System.err.println("JSON syntax error");
					} catch (IOException e) {
						System.err.println("Failed to write/load Scene Description File: " +
								e.getMessage());
					}
					i += 3;
					return;
				} else if (args.length > i+2) {
					String name = args[i+1];
					String value = args[i+2];
					try {
						PersistentSettings.setIntOption(name, Integer.parseInt(value));
					} catch (Exception e) {
						PersistentSettings.setStringOption(name, value);
					}
					i += 2;
					return;
				} else {
					System.err.println("Too few arguments for -set option!");
					confError = true;
					break;
				}
			} else if (args[i].equals("-reset")) {
				mode = Mode.NO_OP;
				if (args.length > i+2) {
					options.sceneName = args[i+2];
					try {
						File file = getSceneFile(options);
						JsonObject desc = readSceneJson(file);
						String name = args[i+1];
						System.out.println("- " + name);
						String[] path = name.split("\\.");
						JsonObject obj = desc;
						for (int j = 0; j < path.length-1; ++j) {
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
						System.err.println("Failed to write/load Scene Description File: " +
								e.getMessage());
					}
					i += 2;
					return;
				} else if (args.length > i+1) {
					PersistentSettings.resetOption(args[i+1]);
					i += 1;
					return;
				} else {
					System.err.println("Too few arguments for -reset option!");
					confError = true;
					break;
				}
			} else if (args[i].equals("-download-mc")) {
				mode = Mode.NO_OP;
				if (args.length > i+1) {
					String version = args[i+1];
					try {
						File dir = new File(PersistentSettings.getSettingsDirectory(), "resources");
						if (!dir.exists()) {
							dir.mkdir();
						}
						if (!dir.isDirectory()) {
							System.err.println("Failed to create destination directory " +
											dir.getAbsolutePath());
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
				System.err.println("Unrecognized argument: "+args[i]);
				System.err.println(USAGE);
				confError = true;
				break;
			}
		}

		if (options.sceneName != null &&
				options.sceneName.endsWith(SceneDescription.SCENE_DESCRIPTION_EXTENSION)) {
			File possibleSceneFile = new File(options.sceneName);
			if (possibleSceneFile.isFile()) {
				options.sceneDir = possibleSceneFile.getParentFile();
				options.sceneName = possibleSceneFile.getName();
			}
		}

		if (options.sceneDir == null) {
			options.sceneDir = PersistentSettings.getSceneDirectory();
		}

		if (!confError) {
			try {
				if (options.texturePack != null) {
					TexturePackLoader.loadTexturePack(new File(options.texturePack), false);
				} else {
					String lastTexturePack = PersistentSettings.getLastTexturePack();
					if (!lastTexturePack.isEmpty()) {
						TexturePackLoader.loadTexturePack(new File(lastTexturePack), false);
					} else {
						TexturePackLoader.loadTexturePack(MinecraftFinder.getMinecraftJar(), false);
					}
				}
			} catch (TextureLoadingError e) {
				System.err.println("Failed to load texture pack!");
			}
		}

		if (options.renderThreads == -1) {
			options.renderThreads = PersistentSettings.getNumThreads();
		}

		if (options.sceneName != null) {
			mode = Mode.HEADLESS_RENDER;
		}
	}

	private static File getSceneFile(ChunkyOptions options) {
		if (options.sceneName.endsWith(
				SceneDescription.SCENE_DESCRIPTION_EXTENSION)) {
			File possibleSceneFile = new File(options.sceneName);
			return possibleSceneFile;
		} else {
			if (options.sceneDir != null) {
				return new File(options.sceneDir, options.sceneName +
						SceneDescription.SCENE_DESCRIPTION_EXTENSION);
			} else {
				return new File(PersistentSettings.getSceneDirectory(),
						options.sceneName +
						SceneDescription.SCENE_DESCRIPTION_EXTENSION);
			}
		}
	}

	private static JsonObject readSceneJson(File file) throws IOException, SyntaxError {
		FileInputStream in = new FileInputStream(file);
		try {
			JsonParser parser = new JsonParser(in);
			return parser.parse().object();
		} finally {
			in.close();
		}
	}

	private static void writeSceneJson(File file, JsonObject desc) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		try {
			PrettyPrinter pp = new PrettyPrinter("  ", new PrintStream(out));
			desc.prettyPrint(pp);
		} finally {
			out.close();
		}

	}

}
