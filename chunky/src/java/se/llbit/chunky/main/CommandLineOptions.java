package se.llbit.chunky.main;

import java.io.File;

import org.apache.log4j.Logger;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.resources.MinecraftFinder;
import se.llbit.chunky.resources.TexturePackLoader;
import se.llbit.chunky.resources.TexturePackLoader.TextureLoadingError;

public class CommandLineOptions {
	enum Mode {
		DEFAULT,
		NO_OP,
		HEADLESS_RENDER,
		HEADLESS_BENCHMARK,
	}
	private static Logger logger = Logger.getLogger(CommandLineOptions.class);

	/**
	 * The help string
	 */
	private static final String USAGE =
		"Usage: chunky [OPTIONS] [WORLD DIRECTORY]\n" +
		"Options:\n" +
		"  -texture <FILE>        use FILE as the texture pack (must be a zip file)\n" +
		"  -render <SCENE.json>   render the specified scene (see notes)\n" +
		"  -scene-dir <DIR>       use the directory DIR for loading/saving scenes\n" +
		"  -benchmark             run the benchmark and exit\n" +
		"  -threads <NUM>         use the specified number of threads for rendering\n" +
		"  -tile-width <NUM>      use the specified job tile width\n" +
		"  -target <NUM>          override target SPP to be NUM in headless mode\n" +
		"  -opencl                enables OpenCL rendering in the GUI\n" +
		"  -config <NAME> <VALUE> set a configuration option and exit\n" +
		"  -help                  show this text\n" +
		"\n" +
		"Notes:\n" +
		"If -render <SCENE> is specified and SCENE is a path to an existing file\n" +
		"and -scene-dir <DIR> is not given, SCENEs parent directory will be used\n" +
		"as the scene directory.  Otherwise, SCENE is interpreted as the name of\n" +
		"a .json file within the scene directory.";

	protected boolean confError = false;

	protected Mode mode = Mode.DEFAULT;

	protected ChunkyOptions options = new ChunkyOptions();

	public CommandLineOptions(String[] args) {
		boolean selectedWorld = false;

		// parse arguments
		for (int i = 0; i < args.length; ++i) {
			if (args[i].equals("-texture") && args.length > i+1) {
				options.texturePack = args[++i];
			} else if (args[i].equals("-scene-dir")) {
				if (i+1 == args.length) {
					logger.error("Missing argument for -scene-dir option");
					confError = true;
					break;
				} else {
					options.sceneDir = new File(args[i+1]);
					i += 1;
				}
			} else if (args[i].equals("-render")) {
				if (i+1 == args.length) {
					logger.error("Missing argument for -render option");
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
					logger.error("Missing argument for -target option");
					confError = true;
					break;
				} else {
					options.target = Math.max(1, Integer.parseInt(args[i+1]));
					i += 1;
				}
			} else if (args[i].equals("-threads")) {
				if (i+1 == args.length) {
					logger.error("Missing argument for -threads option");
					confError = true;
					break;
				} else {
					options.renderThreads = Math.max(1, Integer.parseInt(args[i+1]));
					i += 1;
				}
			} else if (args[i].equals("-tile-width")) {
				if (i+1 == args.length) {
					logger.error("Missing argument for -tile-width option");
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
				if (i+2 >= args.length) {
					logger.error("Too few arguments for -set option!");
					confError = true;
					break;
				} else {
					PersistentSettings.setStringOption(args[i+1], args[i+2]);
					i += 2;
					mode = Mode.NO_OP;
				}
			} else if (args[i].equals("-reset")) {
				if (i+1 >= args.length) {
					logger.error("Too few arguments for -reset option!");
					confError = true;
					break;
				} else {
					PersistentSettings.resetOption(args[i+1]);
					i += 1;
					mode = Mode.NO_OP;
				}
			} else if (!args[i].startsWith("-") && !selectedWorld) {
				options.worldDir = new File(args[i]);
			} else {
				System.err.println("Unrecognised argument: "+args[i]);
				System.err.println(USAGE);
				confError = true;
				break;
			}
		}

		if (options.sceneDir == null && options.sceneName != null) {
			File possibleSceneFile = new File(options.sceneName);
			if (possibleSceneFile.isFile()) {
				options.sceneDir = possibleSceneFile.getParentFile();
				options.sceneName = possibleSceneFile.getName();
			} else {
				options.sceneDir = PersistentSettings.getSceneDirectory();
			}
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
				logger.error("Failed to load texture pack!");
			}
		}

		if (options.renderThreads == -1) {
			options.renderThreads = PersistentSettings.getNumThreads();
		}

		if (options.sceneName != null) {
			mode = Mode.HEADLESS_RENDER;
		}
	}

}
