/* Copyright (c) 2012-2015 Jesper Öqvist <jesper@llbit.se>
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
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.SceneLoadingError;
import se.llbit.chunky.ui.CenteredFileDialog;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.World;
import se.llbit.log.Log;

/**
 * Manages render worker threads.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class RenderManager extends AbstractRenderManager implements Renderer {

	/**
	 * Milliseconds until the reset confirmation must be shown when trying to edit
	 * a scene parameter.
	 */
	private static final long SCENE_EDIT_GRACE_PERIOD = 30000;

	private boolean updateBuffer = false;
	private boolean dumpNextFrame = false;

	private final RenderableCanvas canvas;
	private Thread[] workers = {};

	private int numJobs;

	/** The modifiable scene. */
	private final Scene mutableScene;

	/** The buffered scene is only updated between render jobs. */
	private final Scene bufferedScene;

	/** Next job on the job queue. */
	private final AtomicInteger nextJob;

	/** Number of completed jobs. */
	private final AtomicInteger finishedJobs;

	private final RenderContext context;

	private final RenderStatusListener renderListener;

	private final Object bufferMonitor = new Object();

	/** Selects if render threads shut down after reaching the target SPP. */
	private final boolean oneshot;

	/** Current renderer state/mode. */
	private RenderState state = RenderState.PREVIEW;

	private final Collection<SceneStatusListener> sceneListeners =
			new ArrayList<SceneStatusListener>();

	public RenderManager(RenderableCanvas canvas, RenderContext context,
			RenderStatusListener statusListener) {
		this(canvas, context, statusListener, false);
	}

	/**
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

		mutableScene = new Scene();
		bufferedScene = new Scene(mutableScene);

		numJobs = 0;
		nextJob = new AtomicInteger(0);
		finishedJobs = new AtomicInteger(0);

		manageWorkers();
	}

	private void manageWorkers() {
		if (numThreads != workers.length) {
			long seed = System.currentTimeMillis();
			Thread[] pool = new Thread[numThreads];
			int i;
			for (i = 0; i < workers.length && i < numThreads; ++i) {
				pool[i] = workers[i];
			}
			// Start additional workers.
			for (; i < numThreads; ++i) {
				pool[i] = new RenderWorker(this, i, seed+i);
				pool[i].start();
			}
			// Stop extra workers.
			for (; i < workers.length; ++i) {
				workers[i].interrupt();
			}
			workers = pool;
		}
	}

	@Override
	public void run() {
		try {

			while (!isInterrupted()) {

				// PAUSED/PREVIEW/FINISHED

				boolean refreshed = mutableScene.waitOnRefreshOrStateChange();

				// RESUME/EDIT

				synchronized (bufferMonitor) {
					synchronized (mutableScene) {
						updateRenderState();
						if (refreshed) {
							if (!mutableScene.shouldReset() &&
								bufferedScene.renderTime > SCENE_EDIT_GRACE_PERIOD) {
								renderListener.renderResetRequested();
							} else {
								bufferedScene.set(mutableScene);
							}
						} else {
							bufferedScene.copyTransients(mutableScene);
						}
					}
				}

				if (state == RenderState.PREVIEW) {
					// PREVIEW
					previewLoop();
				} else {
					if (bufferedScene.spp < bufferedScene.getTargetSPP()) {
						updateRenderProgress();

						// RENDERING
						pathTraceLoop();
					} else {
						// PAUSED
						mutableScene.pauseRender();
						updateRenderState();
					}
				}

				if (oneshot) {
					break;
				}
			}
		} catch (InterruptedException e) {
			// 3D view was closed.
		} catch (Throwable e) {
			Log.error("Uncaught exception in render manager", e);
		}

		stopWorkers();
	}

	private void updateRenderState() {
		if (state != mutableScene.getRenderState()) {
			state = mutableScene.getRenderState();
			renderListener.renderStateChanged(state);
		}
	}

	private void pathTraceLoop() throws InterruptedException {
		while (true) {

			updateRenderState();
			if (state == RenderState.PAUSED || mutableScene.shouldRefresh()) {
				return;
			}

			synchronized (bufferMonitor) {
				long frameStart = System.currentTimeMillis();
				giveTickets();
				waitOnWorkers();
				bufferedScene.updateCanvas();
				sendSceneStatus(bufferedScene.sceneStatus());
				bufferedScene.renderTime += System.currentTimeMillis() - frameStart;
			}

			// Repaint canvas.
			canvas.repaint();

			bufferedScene.spp += RenderConstants.SPP_PER_PASS;

			if (dumpNextFrame) {
				// Save the current frame.
				if (mutableScene.shouldSaveSnapshots()
						|| bufferedScene.spp >= bufferedScene.getTargetSPP()) {
					bufferedScene.outputMode = mutableScene.outputMode;
					bufferedScene.saveSnapshot(context.getSceneDirectory(), renderListener);
				}

				// Save scene description and render dump.
				saveScene();
			}

			updateRenderProgress();

			if (bufferedScene.spp >= bufferedScene.getTargetSPP()) {
				renderListener.renderJobFinished(bufferedScene.renderTime, samplesPerSecond());
				return;
			}
		}
	}

	/**
	 * @return the current rendering speed in samples per second (SPS)
	 */
	private int samplesPerSecond() {
		int canvasWidth = bufferedScene.canvasWidth();
		int canvasHeight = bufferedScene.canvasHeight();
		long pixelsPerFrame = canvasWidth * canvasHeight;
		double renderTime = bufferedScene.renderTime / 1000.0;
		return (int) ((bufferedScene.spp * pixelsPerFrame) / renderTime);
	}

	private void updateRenderProgress() {
		double renderTime = bufferedScene.renderTime / 1000.0;

		// Notify progress listener
		int target = mutableScene.getTargetSPP();
		long etaSeconds = (long) (((target-bufferedScene.spp) * renderTime) / bufferedScene.spp);
		if (etaSeconds > 0) {
			int seconds = (int) ((etaSeconds) % 60);
			int minutes = (int) ((etaSeconds / 60) % 60);
			int hours = (int) (etaSeconds / 3600);
			String eta = String.format("%d:%02d:%02d", hours, minutes, seconds);
			renderListener.setProgress("Rendering", bufferedScene.spp, 0, target, eta);
		} else {
			renderListener.setProgress("Rendering", bufferedScene.spp, 0, target);
		}

		synchronized (this) {
			// Update render status display.
			renderListener.setRenderTime(bufferedScene.renderTime);
			renderListener.setSamplesPerSecond((int) samplesPerSecond());
			renderListener.setSPP(bufferedScene.spp);
		}
	}

	private void previewLoop() throws InterruptedException {
		long frameStart;

		renderListener.setProgress("Preview", 0, 0, 2);
		bufferedScene.previewCount = 2;

		while (true) {
			if (!updateBuffer ||
					bufferedScene.previewCount <= 0 ||
					mutableScene.shouldRefresh()) {

				return;
			}

			synchronized (bufferMonitor) {
				frameStart = System.currentTimeMillis();
				giveTickets();
				waitOnWorkers();
				bufferedScene.updateCanvas();
				sendSceneStatus(bufferedScene.sceneStatus());
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
		File renderDir = context.getSceneDirectory();
		File file = new File(renderDir, fileName);
		backupFile(file);
	}

	private void backupFile(File file) {
		if (file.exists()) {
			// Try to create backup. It is not a problem if we fail this.
			String backupFileName = file.getName() + ".backup";
			File renderDir = context.getSceneDirectory();
			File backup = new File(renderDir, backupFileName);
			if (backup.exists()) {
				backup.delete();
			}
			if (!file.renameTo(new File(renderDir, backupFileName))) {
				Log.info("Could not create backup " + backupFileName);
			}
		}

	}

	private synchronized void waitOnWorkers() throws InterruptedException {
		while (finishedJobs.get() < numJobs)
			wait();
		// All workers finished - we can now change the number of worker threads!
		manageWorkers();
	}

	private synchronized void giveTickets() {
		bufferedScene.copyTransients(mutableScene);
		int nextSpp = bufferedScene.spp + RenderConstants.SPP_PER_PASS;
		dumpNextFrame = nextSpp >= bufferedScene.getTargetSPP() ||
				bufferedScene.shouldSaveDumps() &&
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
	 * @throws InterruptedException
	 */
	public void saveScene() throws InterruptedException {

		try {
			synchronized (bufferMonitor) {
				String sceneName = bufferedScene.name();
				Log.info("Saving scene " + sceneName);

				// Create backup of scene description and current render dump.
				backupFile(context.getSceneDescriptionFile(mutableScene.name()));
				backupFile(mutableScene.name() + ".dump");

				// Synchronize transient scene parameters.
				bufferedScene.copyTransients(mutableScene);

				bufferedScene.saveScene(context, renderListener);

				Log.info("Scene saved");
			}

			renderListener.sceneSaved();
		} catch (IOException e) {
			Log.warn("Failed to save scene. Reason: " + e.getMessage(), e);
		}
	}

	/**
	 * Load a saved scene
	 * @param sceneName
	 * @throws IOException
	 * @throws SceneLoadingError
	 * @throws InterruptedException
	 */
	public void loadScene(String sceneName)
			throws IOException, SceneLoadingError, InterruptedException {

		synchronized (bufferMonitor) {
			renderListener.setProgress("Loading scene", 0, 0, 1);
			try {
				bufferedScene.loadScene(context, renderListener, sceneName);
			} catch (InterruptedException e) {
				renderListener.taskFailed("Loading scene");
				throw e;
			} catch (SceneLoadingError e) {
				renderListener.taskFailed("Loading scene");
				throw e;
			} catch (IOException e) {
				renderListener.taskFailed("Loading scene");
				throw e;
			}

			synchronized (this) {
				int canvasWidth = bufferedScene.canvasWidth();
				int canvasHeight = bufferedScene.canvasHeight();
				numJobs = canvasWidth * canvasHeight;
			}

			// Update progress bar.
			renderListener.setProgress("Rendering",
					bufferedScene.spp, 0,
					bufferedScene.getTargetSPP());

			synchronized (mutableScene) {
				// Synchronized to ensure that refresh flag is never visibly true.
				mutableScene.set(bufferedScene);
				mutableScene.copyTransients(bufferedScene);
				mutableScene.setRefreshed();
				state = bufferedScene.getRenderState();
			}
			bufferedScene.updateCanvas();
			sendSceneStatus(bufferedScene.sceneStatus());
			canvas.repaint();

			renderListener.sceneLoaded();
			renderListener.renderStateChanged(mutableScene.getRenderState());
		}
	}

	/** Default directory for "save current frame" file dialog. */
	private static String defaultDirectory = System.getProperty("user.dir");

	/**
	 * Save the current frame as a PNG image.
	 * @param progressListener
	 */
	public synchronized void saveSnapshot(ProgressListener progressListener) {

		CenteredFileDialog fileDialog = new CenteredFileDialog(null,
				"Save Current Frame", FileDialog.SAVE);
		String directory;
		synchronized (RenderManager.class) {
			directory = defaultDirectory;
		}
		fileDialog.setDirectory(directory);
		fileDialog.setFile(bufferedScene.name()+"-"+bufferedScene.spp+".png");
		fileDialog.setFilenameFilter(
			new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".png");
				}
			}
		);
		fileDialog.setVisible(true);
		File selectedFile = fileDialog.getSelectedFile(".png");
		if (selectedFile != null) {
			synchronized (RenderManager.class) {
				File parent = selectedFile.getParentFile();
				if (parent != null) {
					defaultDirectory = parent.getAbsolutePath();
				}
			}
			try {
				bufferedScene.outputMode = mutableScene.outputMode;
				bufferedScene.saveFrame(selectedFile, progressListener);
			} catch (IOException e) {
				Log.error("Failed to save snapshot", e);
			}
		}
	}

	/**
	 * @return The current scene object
	 */
	public Scene scene() {
		return mutableScene;
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
				synchronized (mutableScene) {
					if (mutableScene.getRenderState() == RenderState.PREVIEW) {
						mutableScene.refresh();
					}
				}
				Log.info("buffer finalization enabled");
			} else {
				Log.info("buffer finalization disabled");
			}
		}
	}

	/**
	 * Load chunks and reset camera
	 * @param world
	 * @param chunksToLoad
	 */
	public void loadFreshChunks(World world, Collection<ChunkPosition> chunksToLoad) {
		mutableScene.loadChunks(renderListener, world, chunksToLoad);
		mutableScene.moveCameraToCenter();
		renderListener.chunksLoaded();
	}

	/**
	 * Load chunks without moving the camera.
	 * @param world
	 * @param chunksToLoad
	 */
	public void loadChunks(World world, Collection<ChunkPosition> chunksToLoad) {
		mutableScene.loadChunks(renderListener, world, chunksToLoad);
		mutableScene.refresh();
		renderListener.chunksLoaded();
	}

	/** Attempt to reload all loaded chunks. */
	public void reloadChunks() {
		mutableScene.reloadChunks(renderListener);
		renderListener.chunksLoaded();
	}

	@Override
	public Scene bufferedScene() {
		return bufferedScene;
	}

	/**
	 * Merge a render dump into the current render.
	 * @param dumpFile the file to be merged.
	 */
	public void mergeDump(File dumpFile) {
		bufferedScene.mergeDump(dumpFile, renderListener);
		bufferedScene.updateCanvas();
		canvas.repaint();
	}

	/**
	 * Change number of render workers.
	 * @param threads new required thread count.
	 */
	public void setNumThreads(int threads) {
		numThreads = Math.max(1, threads);
	}

	/**
	 * Set CPU load percentage.
	 * @param value new load percentage.
	 */
	public void setCPULoad(int value) {
		cpuLoad  = value;
	}

	/** Stop render workers. */
	private synchronized void stopWorkers() {
		// Halt all worker threads.
		for (int i = 0; i < numThreads; ++i) {
			workers[i].interrupt();
		}
	}

	public void revertPendingSceneChanges() {
		synchronized (mutableScene) {
			// Synchronized to ensure that refresh flag is never visibly true.
			mutableScene.set(bufferedScene);
			mutableScene.setRefreshed();// Clear refresh flag.
		}
	}

	public int getCurrentSPP() {
		return bufferedScene.spp;
	}

	public void setTargetSPP(int target) {
		mutableScene.setTargetSPP(target);
		updateRenderProgress();
	}

	@Override
	public synchronized void addSceneStatusListener(SceneStatusListener listener) {
		sceneListeners.add(listener);
	}

	@Override
	public synchronized void removeSceneStatusListener(SceneStatusListener listener) {
		sceneListeners.remove(listener);
	}

	private synchronized void sendSceneStatus(String status) {
		for (SceneStatusListener listener : sceneListeners) {
			listener.sceneStatus(status);
		}
	}
}
