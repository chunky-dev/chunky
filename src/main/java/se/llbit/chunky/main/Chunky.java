/* Copyright (c) 2010-2013 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import se.llbit.chunky.renderer.AbstractRenderManager;
import se.llbit.chunky.renderer.BenchmarkManager;
import se.llbit.chunky.renderer.ConsoleRenderListener;
import se.llbit.chunky.renderer.PlaceholderRenderCanvas;
import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.renderer.RenderManager;
import se.llbit.chunky.renderer.scene.SceneLoadingError;
import se.llbit.chunky.renderer.scene.SceneManager;
import se.llbit.chunky.renderer.ui.BenchmarkDialog;
import se.llbit.chunky.renderer.ui.CLDeviceSelector;
import se.llbit.chunky.renderer.ui.NewSceneDialog;
import se.llbit.chunky.renderer.ui.RenderControls;
import se.llbit.chunky.renderer.ui.SceneDirectoryPicker;
import se.llbit.chunky.renderer.ui.SceneSelector;
import se.llbit.chunky.resources.TexturePackLoader;
import se.llbit.chunky.ui.ChunkMap;
import se.llbit.chunky.ui.ChunkyFrame;
import se.llbit.chunky.ui.Controls;
import se.llbit.chunky.ui.Minimap;
import se.llbit.chunky.ui.ProgressPanel;
import se.llbit.chunky.world.Block;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.Chunk.Renderer;
import se.llbit.chunky.world.ChunkParser;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.ChunkSelectionTracker;
import se.llbit.chunky.world.ChunkView;
import se.llbit.chunky.world.DeleteChunksJob;
import se.llbit.chunky.world.EmptyChunk;
import se.llbit.chunky.world.EmptyChunkView;
import se.llbit.chunky.world.EmptyWorld;
import se.llbit.chunky.world.Region;
import se.llbit.chunky.world.RegionParser;
import se.llbit.chunky.world.World;
import se.llbit.chunky.world.WorldRenderer;
import se.llbit.chunky.world.listeners.ChunkDiscoveryListener;
import se.llbit.util.ProgramProperties;

/**
 * Chunky is a Minecraft mapping and rendering tool created by
 * Jesper Öqvist.
 *
 * There is a Wiki for Chunky at http://chunky.llbit.se
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class Chunky implements ChunkDiscoveryListener {

	/**
	 * Minimum block scale for the map view
	 */
	public static final int BLOCK_SCALE_MIN = 0;

	/**
	 * Maximum block scale for the map view
	 */
	public static final int BLOCK_SCALE_MAX = 32;

	/**
	 * Default block scale for the map view
	 */
	public static final int DEFAULT_BLOCK_SCALE = 4;

	private int chunkScale = DEFAULT_BLOCK_SCALE*16;

	private World world = EmptyWorld.instance;

	private final ChunkParser chunkParser = new ChunkParser();
	private final RegionParser regionParser = new RegionParser();

	private int currentDimension = 0;
	private Chunk.Renderer chunkRenderer = Chunk.surfaceRenderer;
	private final WorldRenderer worldRenderer = new WorldRenderer();
	protected ChunkSelectionTracker chunkSelection = new ChunkSelectionTracker();

	protected boolean ctrlModifier = false;
	protected boolean shiftModifier = false;

	private RenderControls renderControls = null;
	private ChunkyFrame frame;

	private ChunkView map = EmptyChunkView.instance;
	private ChunkView minimap = EmptyChunkView.instance;

	private int mapWidth = ChunkMap.DEFAULT_WIDTH;
	private int mapHeight = ChunkMap.DEFAULT_HEIGHT;

	private int minimapWidth = Minimap.DEFAULT_WIDTH;
	private int minimapHeight = Minimap.DEFAULT_HEIGHT;

	private Chunk hoveredChunk = EmptyChunk.instance;

	/**
	 * Whether or not OpenCL rendering is enabled
	 */
	public boolean openCLEnabled = false;

	/**
	 * Logger object.
	 */
	private static Logger logger = Logger.getLogger(Chunky.class);

	/**
	 * @return The name of this application
	 */
	public static final String getAppName() {
		return String.format("%s %s", //$NON-NLS-1$
			Messages.getString("Chunky.appname"), //$NON-NLS-1$
			Messages.getString("Chunky.version")); //$NON-NLS-1$
	}

	/**
	 * @return Chunky revision
	 */
	public static final String getRevision() {
		return Messages.getString("Chunky.revision"); //$NON-NLS-1$
	}

	static {
		// Configure the logger
		PropertyConfigurator.configure(
				Chunky.class.getResource("/log4j.properties"));
	}

	private int tileWidth = AbstractRenderManager.TILE_WIDTH_DEFAULT;

	/**
	 * The help string
	 */
	private static final String USAGE =
		"Usage: chunky [OPTIONS] [WORLD DIRECTORY]\n" +
		"Options:\n" +
		"  -texture <FILE>        use FILE as the texture pack (must be a zip file)\n" +
		"  -watermark             add a watermark to saved frame\n" +
		"  -render <SCENE.cvf>    render the specified scene (see notes)\n" +
		"  -scene-dir <DIR>       use the directory DIR for loading/saving scenes\n" +
		"  -benchmark             run the benchmark and exit\n" +
		"  -threads <NUM>         use the specified number of threads for rendering\n" +
		"  -tile-width <NUM>      use the specified job tile width\n" +
		"  -target <NUM>          override target SPP to be NUM in headless mode\n" +
		"  -opencl                enables OpenCL rendering in the GUI\n" +
		"  -help                  show this text\n" +
		"\n" +
		"Notes:\n" +
		"If -render <SCENE> is specified and SCENE is a path to an existing file\n" +
		"and -scene-dir <DIR> is not given, SCENE's parent directory will be used\n" +
		"as the scene directory.  Otherwise, SCENE is interpreted as the name of\n" +
		"a .cvf file within the scene directory.";

	/**
	 * Constructor
	 */
	public Chunky() {
	}

	/**
	 * Create a new instance of the application GUI.
	 * @param args
	 * @return Program exit code (0 = success)
	 */
	public int run(String[] args) {
		boolean selectedWorld = false;
		File sceneDir = null;
		String sceneName = null;
		String texturePack = null;
		int renderThreads = Runtime.getRuntime().availableProcessors();
		File worldDir = null;
		boolean doBench = false;
		int target = -1;
		for (int i = 0; i < args.length; ++i) {
			if (args[i].equals("-texture") && args.length > i+1) {
				texturePack = args[++i];
			} else if (args[i].equals("-watermark")) {
				RenderManager.useWatermark = true;
			} else if (args[i].equals("-scene-dir")) {
				if (i+1 == args.length) {
					logger.error("Missing argument for -scene-dir option");
					return 1;
				} else {
					sceneDir = new File(args[++i]);
				}
			} else if (args[i].equals("-render")) {
				if (i+1 == args.length) {
					logger.error("Missing argument for -render option");
					return 1;
				} else {
					sceneName = args[++i];
				}
			} else if (args[i].equals("-benchmark")) {
				doBench = true;
			} else if (args[i].equals("-target")) {
				if (i+1 == args.length) {
					logger.error("Missing argument for -target option");
					return 1;
				} else {
					target = Math.max(1, Integer.parseInt(args[++i]));
				}
			} else if (args[i].equals("-threads")) {
				if (i+1 == args.length) {
					logger.error("Missing argument for -threads option");
					return 1;
				} else {
					renderThreads = Math.max(1, Integer.parseInt(args[++i]));
				}
			} else if (args[i].equals("-tile-width")) {
				if (i+1 == args.length) {
					logger.error("Missing argument for -tile-width option");
					return 1;
				} else {
					tileWidth = Math.max(1, Integer.parseInt(args[++i]));
				}
			} else if (args[i].equals("-opencl")) {
				openCLEnabled = true;
			} else if (args[i].equals("-h") || args[i].equals("-?") || args[i].equals("-help") || args[i].equals("--help")) {
				System.out.println(USAGE);
				System.out.println();
				System.out.println("The default scene directory is " + ProgramProperties.getSceneDirectory());
				return 0;
			} else if (!args[i].startsWith("-") && !selectedWorld) {
				worldDir = new File(args[i]);
			} else {
				System.err.println("Unrecognised argument: "+args[i]);
				System.err.println(USAGE);
				return 1;
			}
		}

		if (sceneDir == null && sceneName != null) {
			File possibleSceneFile = new File(sceneName);
			if (possibleSceneFile.isFile()) {
				sceneDir = possibleSceneFile.getParentFile();
				sceneName = possibleSceneFile.getName();
			} else {
				sceneDir = ProgramProperties.getSceneDirectory();
			}
		}

		if (texturePack != null) {
			TexturePackLoader.loadTexturePack(new File(texturePack), false);
		} else {
			String lastTexturePack = ProgramProperties.getProperty("lastTexturePack");
			if (lastTexturePack != null) {
				TexturePackLoader.loadTexturePack(new File(lastTexturePack), false);
			} else {
				TexturePackLoader.loadTexturePack(getMinecraftJar(), false);
			}
		}

		if (doBench) {
			doBenchmark(renderThreads);
			return 0;
		}

		if (sceneName != null) {
			// start headless mode
			System.setProperty("java.awt.headless", "true");

			RenderContext renderContext = new RenderContext(sceneDir,
					renderThreads, tileWidth);
			RenderManager renderManager = new RenderManager(
					new PlaceholderRenderCanvas(),
					renderContext, new ConsoleRenderListener(), true);
			renderManager.start();

			try {
				renderManager.loadScene(sceneName);
				if (target != -1) {
					renderManager.scene().setTargetSPP(target);
				}
				if (!renderManager.scene().pathTrace()) {
					renderManager.scene().startRender();
				} else {
					renderManager.scene().resumeRender();
				}
			} catch (IOException e) {
				logger.error("IO error while loading scene", e);
			} catch (SceneLoadingError e) {
				logger.error("Scene loading error", e);
			} catch (InterruptedException e) {
				logger.error("Interrupted while loading scene", e);
			}
			return 0;
		}

		// load the world
		if (worldDir != null && World.isWorldDir(worldDir)) {
			loadWorld(new World(worldDir, false));
		} else {
			String lastWorld = ProgramProperties.getProperty("lastWorld");
			File lastWorldDir = lastWorld != null ? new File(lastWorld) : null;
			if (lastWorldDir != null && World.isWorldDir(lastWorldDir)) {
				loadWorld(new World(lastWorldDir, false));
			}
		}

		// Start the worker threads
		chunkParser.start();
		regionParser.start();

		// Create UI in the event dispatch thread
		try {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				UIManager.put("Slider.paintValue", Boolean.FALSE);
			} catch (Exception e) {
				logger.warn("Failed to set native Look and Feel");
			}

			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					buildUI();
				}
			});
		} catch (InterruptedException e) {
			logger.fatal("Failed to initialize UI", e);
		} catch (InvocationTargetException e) {
			logger.fatal("Failed to initialize UI", e);
		}

		refreshLoop();
		return 0;
	}

	/**
	 * Perform a benchmark in headless mode
	 * @param renderThreads
	 */
	private void doBenchmark(int renderThreads) {
		System.setProperty("java.awt.headless", "true");

		File sceneDir = ProgramProperties.getSceneDirectory();
		RenderContext renderContext = new RenderContext(sceneDir,
				renderThreads, tileWidth);
		BenchmarkManager benchmark = new BenchmarkManager(renderContext,
				new ConsoleRenderListener());
		benchmark.start();

		try {
			benchmark.join();
			BenchmarkDialog.recordBenchmarkScore(benchmark.getSceneName(),
					benchmark.getScore());
			System.out.println("Benchmark completed with score " + benchmark.getScore() +
					" (" + benchmark.getSceneName() + ")");
		} catch (InterruptedException e) {
			logger.warn("Benchmarking interrupted");
		}
	}

	protected void buildUI() {
		frame = new ChunkyFrame(this);
		frame.initComponents();
		frame.setVisible(true);

		if (world.isEmptyWorld())
			getControls().openWorldSelector();
		else
			updateView();

	}

	/**
	 * Flush all cached chunks and regions, forcing them to be reloaded
	 * for the current world.
	 */
	public synchronized void reloadWorld() {
		world.reload();

		if (frame != null) {
			viewUpdated();
			frame.getMap().redraw();
			frame.getMinimap().redraw();
		}
	}

	/**
	 * Load a new world.
	 *
	 * @param newWorld
	 */
	public synchronized void loadWorld(World newWorld) {

		newWorld.reload();

		regionParser.clearQueue();
		chunkParser.clearQueue();

		chunkSelection.clearSelection();

		// dispose old world
		world.dispose();

		world = newWorld;
		world.addChunkDeletionListener(chunkSelection);
		world.addChunkDiscoveryListener(this);

		// dimension must be set before chunks are loaded
		world.setDimension(currentDimension);

		updateView();

		ProgramProperties.setProperty("lastWorld",
				world.getWorldDirectory().getAbsolutePath());

		if (frame != null) {
			frame.worldLoaded(world);
			viewUpdated();
			frame.getMap().redraw();
			frame.getMinimap().redraw();
		}
	}

	private void updateView() {
		if (world.havePlayerPos()) {
			setView(
					world.playerPosX() / 16,
					world.playerPosZ() / 16);
		} else {
			setView(0, 0);
		}
	}

	/**
	 * Called when the map view has changed.
	 */
	public synchronized void viewUpdated() {
		minimap = new ChunkView(map.x, map.z, minimapWidth, minimapHeight, 1);

		// clear the region and chunk parse queues
		regionParser.clearQueue();
		chunkParser.clearQueue();

		// enqueue visible regions and chunks
		for (int rx = Math.min(minimap.rx0, map.prx0);
				rx <= Math.max(minimap.rx1, map.prx1); ++rx) {

			for (int rz = Math.min(minimap.rz0, map.prz0);
					rz <= Math.max(minimap.rz1, map.prz1); ++rz) {

				Region region = world.getRegion(ChunkPosition.get(rx, rz));
				if (!region.isEmpty()
						&& (map.isVisible(region) || minimap.isVisible(region))) {
					if (!region.isParsed()) {
						regionParser.addRegion(region);
					} else {
						// parse visible chunks
						region.preloadChunks(map, chunkParser);
					}
				}
			}
		}

		if (frame != null) {
			frame.getMap().viewUpdated(map);
			frame.getMinimap().viewUpdated(minimap);
		}
	}

	/**
	 * Entry point for Chunky
	 *
	 * @param args
	 */
	public static void main(final String[] args) {
		Chunky chunky = new Chunky();
		int exitVal = chunky.run(args);
		if (exitVal != 0) {
			System.exit(exitVal);
		}
	}

	/**
	 * Periodically check for changes in the world state and
	 * refresh the view if necessary.
	 */
	private void refreshLoop() {
		try {
			while (!Thread.interrupted()) {
				frame.refresh();
				Thread.sleep(125);
			}
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Set the current map renderer
	 *
	 * @param renderer
	 */
	public synchronized void setRenderer(Chunk.Renderer renderer) {
		this.chunkRenderer = renderer;
		getMap().repaint();
	}

	/**
	 * Open the 3D chunk view
	 */
	public synchronized void open3DView() {
		if (renderControls == null || !renderControls.isDisplayable()) {
			File sceneDir = SceneDirectoryPicker.getSceneDirectory(frame);
			RenderContext context = new RenderContext(sceneDir,
					getControls().getNumThreads(), tileWidth);
			if (sceneDir != null) {
				String name = world.levelName();
				String preferredName = SceneManager.preferredSceneName(
														context, name);

				if (SceneManager.sceneNameAvailable(context, preferredName)) {
					create3DScene(context, preferredName);
				} else {
					NewSceneDialog dialog = new NewSceneDialog(getFrame(),
							context, world.levelName());
					dialog.setVisible(true);
					if (dialog.isAccepted()) {
						create3DScene(context, dialog.getSceneName());
					}
				}
			}
		}
	}

	private void create3DScene(RenderContext context, String sceneName) {
		renderControls = new RenderControls(Chunky.this, context);
		renderControls.setSceneName(sceneName);
		Collection<ChunkPosition> selection =
				chunkSelection.getSelection();
		if (!selection.isEmpty()) {
			renderControls.loadChunks(world, selection);
		} else {
			renderControls.show3DView();
		}
	}

	/**
	 * Set the hovered chunk by chunk coordinates
	 * @param cx
	 * @param cz
	 */
	public synchronized void setHoveredChunk(int cx, int cz) {
		hoveredChunk = world.getChunk(ChunkPosition.get(cx, cz));
	}

	/**
	 * @return The currently hovered chunk
	 */
	public synchronized Chunk getHoveredChunk() {
		return hoveredChunk;
	}

	/**
	 * Select specific chunk
	 * @param cx
	 * @param cz
	 */
	public synchronized void selectChunk(int cx, int cz) {
		chunkSelection.selectChunk(world, cx, cz);
		getControls().setChunksSelected(chunkSelection.numSelectedChunks() > 0);
	}

	/**
	 * Set the map view
	 * @param cx
	 * @param cz
	 */
	public synchronized void setView(final double cx, final double cz) {
		map = new ChunkView(cx, cz, mapWidth, mapHeight, chunkScale);
		if (frame != null) {
			getControls().setPosition(cx, getLayer(), cz);
		}
		viewUpdated();
	}

	/**
	 * Set the currently viewed layer
	 * @param value
	 */
	public synchronized void setLayer(int value) {
		int layerNew = Math.max(0, Math.min(Chunk.Y_MAX-1, value));
		if (layerNew != world.currentLayer()) {
			chunkParser.clearQueue();
			world.setCurrentLayer(layerNew);
			if (chunkRenderer == Chunk.layerRenderer)
				getMap().redraw();

			// force the chunks to redraw
			viewUpdated();
		}
		getControls().setLayer(world.currentLayer());
	}

	/**
	 * @return The currently viewed layer
	 */
	public synchronized int getLayer() {
		return world.currentLayer();
	}

	/**
	 * Delete the currently selected chunks from the current world.
	 */
	public void deleteSelectedChunks() {
		Object[] options = {Messages.getString("Chunky.Cancel_lbl"), //$NON-NLS-1$
				Messages.getString("Chunky.AcceptDelete_lbl")}; //$NON-NLS-1$
		int n = JOptionPane.showOptionDialog(null,
				Messages.getString("Chunky.DeleteDialog_msg"), //$NON-NLS-1$
				Messages.getString("Chunky.DeleteDialog_title"), //$NON-NLS-1$
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE,
				null,
				options,
				options[0]);
		if (n == 1) {

			Collection<ChunkPosition> selected = chunkSelection.getSelection();
			ProgressPanel progress = getControls().getProgressPanel();
			if (!selected.isEmpty() && !progress.isBusy()) {
				DeleteChunksJob job = new DeleteChunksJob(world, selected, progress);
				job.start();
			}
		}
	}

	/**
	 * Set the current dimension
	 * @param value Must be a valid dimension index
	 */
	public void setDimension(int value) {
		if (value != currentDimension) {
			currentDimension = value;
			loadWorld(world);
		}
	}

	/**
	 * Clears the chunk selection
	 */
	public synchronized void clearSelectedChunks() {
		chunkSelection.clearSelection();
		getControls().setChunksSelected(chunkSelection.numSelectedChunks() > 0);
	}

	/**
	 * @return The directory of the local Minecraft installation
	 */
	public static File getMinecraftDirectory() {
		String home = System.getProperty("user.home", ".");   //$NON-NLS-1$ //$NON-NLS-2$
		String os = System.getProperty("os.name").toLowerCase();  //$NON-NLS-1$

		if (os.contains("win")) {  //$NON-NLS-1$
			String appdata = System.getenv("APPDATA");	//$NON-NLS-1$
			if (appdata != null)
				return new File(appdata, ".minecraft");	//$NON-NLS-1$
			else
				return new File(home, ".minecraft");  //$NON-NLS-1$
		} else if (os.contains("mac")) {  //$NON-NLS-1$
			return new File(home, "Library/Application Support/minecraft");	//$NON-NLS-1$
		} else {
			return new File(home, ".minecraft");  //$NON-NLS-1$
		}
	}

	/**
	 * @return The saves directory of the local Minecraft installation
	 */
	public static File getSavesDirectory() {
		return new File(getMinecraftDirectory(), "saves");
	}

	/**
	 * @return The texture pack directory of the local Minecraft installation
	 */
	public static File getTexturePacksDirectory() {
		return new File(getMinecraftDirectory(), "texturepacks");
	}

	/**
	 * @return The current highlight color
	 */
	public Color getHighlightColor() {
		return worldRenderer.getHighlightColor();
	}

	/**
	 * @return The currently highlighted block type
	 */
	public Block getHighlightBlock() {
		return worldRenderer.getHighlightBlock();
	}

	/**
	 * Set block type highlighting
	 * @param value
	 */
	public void setHighlightEnable(boolean value) {
		if (value != worldRenderer.isHighlightEnabled()) {

			worldRenderer.setHighlightEnabled(value);
			getMap().redraw();
		}
	}

	/**
	 * @return <code>true</code> if block type highlighting is currently active
	 */
	public boolean isHighlightEnabled() {
		return worldRenderer.isHighlightEnabled();
	}

	/**
	 * Set a new block type to highlight
	 * @param hlBlock
	 */
	public void highlightBlock(Block hlBlock) {
		worldRenderer.highlightBlock(hlBlock);
		if (worldRenderer.isHighlightEnabled())
			getMap().redraw();
	}

	/**
	 * Set a new highlight color
	 * @param newColor
	 */
	public void setHighlightColor(Color newColor) {
		worldRenderer.setHighlightColor(newColor);
		if (worldRenderer.isHighlightEnabled())
			getMap().redraw();
	}

	/**
	 * @return The name of the current world
	 */
	public String getWorldName() {
		return world.levelName();
	}

	/**
	 * Export the selected chunks to a zip file
	 * @param targetFile
	 * @param progress
	 */
	public synchronized void exportZip(File targetFile, ProgressPanel progress) {
		if (!progress.isBusy()) {
			if (targetFile.exists()) {
				Object[] options = {Messages.getString("Chunky.Cancel_lbl"), //$NON-NLS-1$
						Messages.getString("Chunky.AcceptOverwrite_lbl")}; //$NON-NLS-1$
				int n = JOptionPane.showOptionDialog(null,
						String.format(Messages.getString("Chunky.Confirm_overwrite_msg"), //$NON-NLS-1$
								targetFile.getName()),
						Messages.getString("Chunky.Confirm_overwrite_title"), //$NON-NLS-1$
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE,
						null,
						options,
						options[0]);
				if (n != 1)
					return;
			}
			new ZipExportJob(world, chunkSelection.getSelection(), targetFile, progress).start();
		}
	}

	/**
	 * Render the current view to a PNG image
	 * @param targetFile
	 * @param progress
	 */
	public void renderView(File targetFile, ProgressPanel progress) {
		if (!progress.isBusy()) {
			if (targetFile.exists()) {
				Object[] options = {Messages.getString("Chunky.Cancel_lbl"), //$NON-NLS-1$
						Messages.getString("Chunky.AcceptOverwrite_lbl")}; //$NON-NLS-1$
				int n = JOptionPane.showOptionDialog(null,
						String.format(Messages.getString("Chunky.Confirm_overwrite_msg"), //$NON-NLS-1$
								targetFile.getName()),
						Messages.getString("Chunky.Confirm_overwrite_title"), //$NON-NLS-1$
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE,
						null,
						options,
						options[0]);
				if (n != 1)
					return;
			}

			if (progress.tryStartJob()) {
				progress.setJobName("PNG export");
				progress.setJobSize(1);
				getMap().renderPng(targetFile);
				progress.finishJob();
			}
		}
	}

	/**
	 * @return The current world
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * @return The currently selected chunks
	 */
	public Collection<ChunkPosition> getSelectedChunks() {
		return chunkSelection.getSelection();
	}

	/**
	 * @return <code>true</code> if the Shift key is pressed
	 */
	public boolean getShiftModifier() {
		return shiftModifier;
	}

	/**
	 * @return <code>true</code> if the Ctrl key is pressed
	 */
	public boolean getCtrlModifier() {
		return ctrlModifier;
	}

	/**
	 * Select chunks within a rectangle
	 * @param cx0
	 * @param cx1
	 * @param cz0
	 * @param cz1
	 */
	public void selectChunks(int cx0, int cx1, int cz0, int cz1) {
		if (!ctrlModifier)
			chunkSelection.selectChunks(world, cx0, cz0, cx1, cz1);
		else
			chunkSelection.deselectChunks(world, cx0, cz0, cx1, cz1);

		getControls().setChunksSelected(chunkSelection.numSelectedChunks() > 0);
	}

	/**
	 * @return The Controls UI element
	 */
	public Controls getControls() {
		return frame.getControls();
	}

	/**
	 * @return The chunk selection tracker
	 */
	public ChunkSelectionTracker getChunkSelection() {
		return chunkSelection;
	}

	/**
	 * @return The world renderer
	 */
	public WorldRenderer getWorldRenderer() {
		return worldRenderer;
	}

	/**
	 * Update the Ctrl key modifier
	 * @param value
	 */
	public void setCtrlModifier(boolean value) {
		ctrlModifier = value;
	}

	/**
	 * Update the Shift key modifier
	 * @param value
	 */
	public void setShiftModifier(boolean value) {
		shiftModifier = value;
	}

	/**
	 * @return The main Chunky frame UI element
	 */
	public ChunkyFrame getFrame() {
		return frame;
	}

	/**
	 * @return The main chunk map
	 */
	public ChunkMap getMap() {
		return frame.getMap();
	}

	/**
	 * @return The current map renderer
	 */
	public Renderer getChunkRenderer() {
		return chunkRenderer;
	}

	/**
	 * @return The minimap UI element
	 */
	public Minimap getMinimap() {
		return frame.getMinimap();
	}

	@Override
	public void chunksDiscovered(Collection<Chunk> chunks) {
		for (Chunk chunk: chunks) {
			if (map.shouldPreload(chunk))
				chunkParser.addChunk(chunk);
		}
	}

	/**
	 * @return <code>true</code> if chunks or regions are currently being parsed
	 */
	public boolean isLoading() {
		return chunkParser.isWorking() || regionParser.isWorking();
	}

	/**
	 * Modify the block scale of the map view
	 * @param blockScale
	 */
	public synchronized void setScale(int blockScale) {
		int scaleNew = Math.max(BLOCK_SCALE_MIN,
				Math.min(BLOCK_SCALE_MAX, blockScale));
		scaleNew = scaleNew == 0 ? 1 : scaleNew*16;
		if (scaleNew != chunkScale) {
			chunkScale = scaleNew;
			getControls().setScale(getScale());
			setView(map.x, map.z);
		}
	}

	/**
	 * @return The current block scale of the map view
	 */
	public int getScale() {
		return chunkScale/16;
	}

	/**
	 * Called when the map view has been dragged by the user
	 * @param dx
	 * @param dy
	 */
	public void viewDragged(int dx, int dy) {
		moveView(dx / (double) chunkScale,
				dy / (double) chunkScale);
	}

	/**
	 * Move the map view
	 * @param dx
	 * @param dz
	 */
	public synchronized void moveView(double dx, double dz) {
		setView(map.x + dx, map.z + dz);
	}

	/**
	 * @return The current map view
	 */
	public ChunkView getMapView() {
		return map;
	}

	/**
	 * Called when the map has been resized
	 * @param width
	 * @param height
	 */
	public void mapResized(int width, int height) {
		if (width != mapWidth || height != mapHeight) {
			mapWidth = width;
			mapHeight = height;
			setView(map.x, map.z);
		}
	}

	/**
	 * Called when the user has moved the mouse wheel
	 * @param diff
	 */
	public synchronized void onMouseWheelMotion(int diff) {
		if (!ctrlModifier) {
			setLayer(getLayer() + diff);
		} else {
			setScale(getScale() - diff);
		}
	}

	/**
	 * @return The current minimap view
	 */
	public ChunkView getMinimapView() {
		return minimap;
	}

	/**
	 * Called when the minimap has been resized
	 * @param width
	 * @param height
	 */
	public void minimapResized(int width, int height) {
		if (width != minimapWidth || height != minimapHeight) {
			minimapWidth = width;
			minimapHeight = height;
			viewUpdated();
		}
	}

	/**
	 * @return The minecraft.jar of the local Minecraft installation
	 */
	public static final File getMinecraftJar() {
		return new File(Chunky.getMinecraftDirectory(), "bin/minecraft.jar");
	}

	/**
	 * Show the scene selector dialog.
	 */
	public void loadScene() {
		if (renderControls == null || !renderControls.isDisplayable()) {
			File sceneDir = SceneDirectoryPicker.getSceneDirectory(frame);
			if (sceneDir != null) {
				RenderContext context = new RenderContext(sceneDir,
						getControls().getNumThreads(), tileWidth);
				SceneSelector sceneSelector = new SceneSelector(null, context);
				sceneSelector.setLocationRelativeTo(frame);
				if (sceneSelector.isAccepted()) {
					String scene = sceneSelector.getSelectedScene();
					renderControls = new RenderControls(Chunky.this, context);
					renderControls.loadScene(scene);
				}
			}
		} else {
				SceneSelector sceneSelector = new SceneSelector(null, renderControls.getContext());
				sceneSelector.setLocationRelativeTo(frame);
				if (sceneSelector.isAccepted()) {
					String scene = sceneSelector.getSelectedScene();
					renderControls.loadScene(scene);
				}
		}
	}

	/**
	 * Open the OpenCL test renderer
	 */
	public void openCLTestRenderer() {
		new CLDeviceSelector(getFrame(),
				getWorld(), getSelectedChunks());
	}

	/**
	 * Benchmark the path tracing renderer.
	 */
	public void runBenchmark() {
		RenderContext context = new RenderContext(null,
				getControls().getNumThreads(), tileWidth);
		new BenchmarkDialog(getFrame(), context);
	}
}
