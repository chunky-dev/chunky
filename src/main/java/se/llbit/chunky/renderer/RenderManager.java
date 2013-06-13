/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer;

import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import se.llbit.chunky.main.Messages;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.SceneLoadingError;
import se.llbit.chunky.ui.CenteredFileDialog;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.World;
import se.llbit.resources.ImageLoader;

/**
 * Manages the 3D render worker threads.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class RenderManager extends AbstractRenderManager implements Renderer {

	/**
	 * Default number of worker threads.
	 * Is set to the number of available CPU cores.
	 */
	public static final int NUM_RENDER_THREADS_DEFAULT =
			Runtime.getRuntime().availableProcessors();

	static final int SPP = 1;

	private static final Logger logger =
			Logger.getLogger(RenderManager.class);

	private boolean updateBuffer = false;
	private boolean dumpNextFrame = false;

	private final RenderableCanvas canvas;
	private final Thread[] workers;

	private int numJobs;

	/**
 	 * The modifiable scene.
 	 */
	private final Scene scene;

	/**
 	 * The buffered scene is only updated between
 	 * render jobs.
 	 */
	private final Scene bufferedScene;

	/**
	 * Next job on the job queue.
	 */
	private final AtomicInteger nextJob;

	/**
	 * Number of completed jobs.
	 */
	private final AtomicInteger finishedJobs;

	/**
	 * Render watermark image
	 */
	public static BufferedImage watermark = ImageLoader.get("watermark.png");

	/**
	 * Whether to use the watermark in the saved frames
	 */
	public static boolean useWatermark = false;

	private final RenderContext context;

	private final RenderStatusListener renderListener;

	private final Object bufferMonitor = new Object();

	private final boolean oneshot;

	/**
	 * Constructor
	 * @param canvas
	 * @param context
	 * @param statusListener
	 */
	public RenderManager(RenderableCanvas canvas, RenderContext context,
			RenderStatusListener statusListener) {
		this(canvas, context, statusListener, false);
	}

	/**
	 * Constructor
	 * @param canvas
	 * @param context
	 * @param statusListener
	 * @param oneshot <code>true</code> if rendering threads should be shut
	 * down after meeting the render target
	 */
	public RenderManager(RenderableCanvas canvas, RenderContext context,
			RenderStatusListener statusListener, boolean oneshot) {

		super(context);

		this.canvas = canvas;
		this.context = context;
		this.oneshot = oneshot;
		renderListener = statusListener;

		scene = new Scene();
		bufferedScene = new Scene(scene);

		numJobs = 0;
		nextJob = new AtomicInteger(0);
		finishedJobs = new AtomicInteger(0);

		// start worker threads
		long seed = System.currentTimeMillis();
		workers = new Thread[numThreads];
		for (int i = 0; i < numThreads; ++i) {
			workers[i] = new RenderWorker(this, i, seed + i);
			workers[i].start();
		}
	}

	@Override
	public void run() {
		try {

			while (!isInterrupted()) {

				scene.waitOnRefreshRequest();

				synchronized (scene) {
					renderListener.renderStateChanged(scene.pathTrace(), scene.isPaused());
					bufferedScene.set(scene);
				}

				if (bufferedScene.pathTrace()) {
					pathTraceLoop();
				} else {
					previewLoop();
				}

				if (oneshot) {
					break;
				}
			}
		} catch (InterruptedException e) {
			// 3D view was closed
		} catch (Throwable e) {
			logger.error("Uncaught exception in render manager", e);
		}

		// Halt all worker threads
		for (int i = 0; i < numThreads; ++i) {
			workers[i].interrupt();
		}
	}

	private void pathTraceLoop() throws InterruptedException {
		// enable JIT for the first frame
		java.lang.Compiler.enable();

		while (true) {

			if (scene.isPaused()) {
				renderListener.renderStateChanged(scene.pathTrace(), scene.isPaused());
				scene.pauseWait();
				renderListener.renderStateChanged(scene.pathTrace(), scene.isPaused());
			}

			if (scene.shouldRefresh()) {
				return;
			}

			synchronized (bufferMonitor) {
				long frameStart = System.currentTimeMillis();
				giveTickets();
				waitOnWorkers();
				bufferedScene.updateCanvas();
				bufferedScene.renderTime += System.currentTimeMillis() - frameStart;
			}

			// repaint canvas
			canvas.repaint();

			// disable JIT for subsequent frames
			java.lang.Compiler.disable();

			bufferedScene.spp += SPP;

			int canvasWidth = bufferedScene.canvasWidth();
			int canvasHeight = bufferedScene.canvasHeight();
			long pixelsPerFrame = canvasWidth * canvasHeight;
			double samplesPerSecond = (bufferedScene.spp * pixelsPerFrame) /
					(bufferedScene.renderTime / 1000.0);

			// Update render status display
			renderListener.setRenderTime(bufferedScene.renderTime);
			renderListener.setSamplesPerSecond((int) samplesPerSecond);
			renderListener.setSPP(bufferedScene.spp);

			// Notify progress listener
			int target = bufferedScene.getTargetSPP();
			long etaSeconds = (long) (((target-bufferedScene.spp) *
					pixelsPerFrame) / samplesPerSecond);
			int seconds = (int) ((etaSeconds) % 60);
			int minutes = (int) ((etaSeconds / 60) % 60);
			int hours = (int) (etaSeconds / 3600);
			String eta = String.format("%d:%02d:%02d", hours, minutes, seconds);
			renderListener.setProgress("Rendering", bufferedScene.spp,
					0, target, eta);

			if (dumpNextFrame) {
				// save the current frame
				bufferedScene.saveSnapshot(context.getSceneDirectory(), useWatermark);

				backupFile(scene.name() + ".dump");

				// save scene description and render dump
				saveScene(scene.name() + ".cvf");
			}

			if (bufferedScene.spp >= bufferedScene.getTargetSPP()) {
				scene.pauseRender();
				renderListener.renderStateChanged(scene.pathTrace(), scene.isPaused());
				renderListener.renderJobFinished(bufferedScene.renderTime,
						(int) samplesPerSecond);
				if (oneshot) {
					return;
				}
			}
		}


	}

	private void previewLoop() throws InterruptedException {
		long frameStart;

		renderListener.setProgress("Preview", 0, 0, 2);
		bufferedScene.previewCount = 2;

		while (true) {
			if (!updateBuffer ||
					bufferedScene.previewCount <= 0 ||
					scene.shouldRefresh()) {

				return;
			}

			synchronized (bufferMonitor) {
				frameStart = System.currentTimeMillis();
				giveTickets();
				waitOnWorkers();
				bufferedScene.updateCanvas();
				bufferedScene.renderTime += System.currentTimeMillis() - frameStart;
			}

			// repaint canvas
			canvas.repaint();

			bufferedScene.previewCount -= 1;
			bufferedScene.spp = 0;

			// Update render status display
			renderListener.setRenderTime(bufferedScene.renderTime);
			renderListener.setSamplesPerSecond(0);
			renderListener.setSPP(0);

			// Notify progress listener
			renderListener.setProgress("Preview", 2 - bufferedScene.previewCount, 0, 2);
		}
	}

	private void backupFile(String fileName) {
		String backupFileName = fileName + ".backup";
		File renderDir = context.getSceneDirectory();
		File file = new File(renderDir, fileName);
		if (file.exists()) {
			// try to create backup
			// it's not a biggie if we can't
			File backup = new File(renderDir, backupFileName);
			if (backup.exists())
				backup.delete();
			if (!file.renameTo(new File(renderDir, backupFileName)))
				logger.info("Could not create backup " + backupFileName);
		}
	}

	private synchronized void waitOnWorkers() throws InterruptedException {
		while (finishedJobs.get() < numJobs)
			wait();
	}

	private synchronized void giveTickets() {
		bufferedScene.copyTransients(scene);
		int nextSpp = bufferedScene.spp + SPP;
		dumpNextFrame = nextSpp >= bufferedScene.getTargetSPP() ||
				bufferedScene.saveDumps() &&
				(nextSpp % bufferedScene.getDumpFrequency() == 0);
		bufferedScene.setBufferFinalization(updateBuffer || dumpNextFrame);

		int canvasWidth = bufferedScene.canvasWidth();
		int canvasHeight = bufferedScene.canvasHeight();
		numJobs = ((canvasWidth+(tileWidth-1)) / tileWidth) *
				((canvasHeight+(tileWidth-1)) / tileWidth);
		nextJob.set(0);
		finishedJobs.set(0);
		notifyAll();
	}

	@Override
	public int getNextJob() throws InterruptedException {
		int jobId = nextJob.getAndIncrement();
		if (jobId >= numJobs) {
			synchronized (this) {
				do {
					wait();
					jobId = nextJob.getAndIncrement();
				} while (jobId >= numJobs);
			}
		}
		return jobId;
	}

	@Override
	public void jobDone() {
		int finished = finishedJobs.incrementAndGet();
		if (finished >= numJobs) {
			synchronized (this) {
				notifyAll();
			}
		}
	}

	/**
	 * Save the current scene
	 * @param fileName
	 * @throws InterruptedException
	 */
	public void saveScene(String fileName) throws InterruptedException {

		try {
			synchronized (bufferMonitor) {
				logger.info("Saving scene description " + fileName);

				// create backup of scene description
				backupFile(fileName);

				// synchronize the transients
				bufferedScene.copyTransients(scene);

				bufferedScene.saveSceneDescription(context, renderListener, fileName);

				logger.info("Scene saved");
			}

			renderListener.sceneSaved();
		} catch (IOException e) {
			logger.warn("Failed to save scene. Reason: " + e.getMessage(), e);
		}
	}

	/**
	 * Load a saved scene
	 * @param fileName
	 * @throws IOException
	 * @throws SceneLoadingError
	 * @throws InterruptedException
	 */
	public void loadScene(String fileName)
			throws IOException, SceneLoadingError, InterruptedException {

		synchronized (bufferMonitor) {
			renderListener.setProgress("Loading scene", 0, 0, 1);

			bufferedScene.loadSceneDescription(context, renderListener, fileName);

			synchronized (this) {
				int canvasWidth = bufferedScene.canvasWidth();
				int canvasHeight = bufferedScene.canvasHeight();
				numJobs = canvasWidth * canvasHeight;
			}

			// Update progress bar
			renderListener.setProgress("Rendering",
					bufferedScene.spp, 0,
					bufferedScene.getTargetSPP());

			scene.set(bufferedScene);
			scene.copyTransients(bufferedScene);
			scene.softRefresh();
			bufferedScene.updateCanvas();
			canvas.repaint();

			renderListener.sceneLoaded();
			renderListener.renderStateChanged(scene.pathTrace(), scene.isPaused());
		}
	}

	/**
	 * Save the current frame as a PNG image.
	 * @param progressListener
	 */
	public synchronized void saveFrame(ProgressListener progressListener) {

		CenteredFileDialog fileDialog =
				new CenteredFileDialog(null, "Save Current Frame", FileDialog.SAVE);
		fileDialog.setDirectory(context.getSceneDirectory().getAbsolutePath());
		fileDialog.setFile(bufferedScene.name()+".png");
		fileDialog.setFilenameFilter(
				new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.toLowerCase().endsWith(".png");
					}
				});
		fileDialog.setVisible(true);
		File targetFile = fileDialog.getSelectedFile(".png");
		if (targetFile != null) {
			if (!targetFile.getName().endsWith(".png"))
				targetFile = new File(targetFile.getPath()+".png");
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
			try {
				bufferedScene.saveFrame(targetFile, useWatermark,
						progressListener);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return The current scene object
	 */
	public Scene scene() {
		return scene;
	}

	@Override
	public void drawBufferedImage(Graphics g, int width, int height) {
		bufferedScene.drawBufferedImage(g, width, height);
	}

	@Override
	public synchronized void setBufferFinalization(boolean flag) {
		if (flag != updateBuffer) {
			updateBuffer = flag;
			if (flag) {
				synchronized (scene) {
					if (!scene.pathTrace())
						scene.refresh();
				}
				logger.debug("buffer finalization enabled");
			} else {
				logger.debug("buffer finalization disabled");
			}
		}
	}

	/**
	 * @param world
	 * @param chunksToLoad
	 */
	public void loadChunks(World world, Collection<ChunkPosition> chunksToLoad) {
		scene.loadChunks(renderListener, world, chunksToLoad);
		scene.moveCameraToCenter();
		renderListener.chunksLoaded();
	}

	/**
	 * Attempts to reload all loaded chunks
	 */
	public void reloadChunks() {
		scene.reloadChunks(renderListener);
		renderListener.chunksLoaded();
	}

	@Override
	public Scene bufferedScene() {
		return bufferedScene;
	}

	/**
	 * Merge a render dump into the current render
	 * @param dumpFile
	 */
	public void mergeDump(File dumpFile) {
		bufferedScene.mergeDump(dumpFile, renderListener);
		bufferedScene.updateCanvas();
		canvas.repaint();
	}
}
