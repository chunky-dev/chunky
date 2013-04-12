/* Copyright (c) 2012-2013 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer.scene;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import se.llbit.chunky.main.Messages;
import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.renderer.RenderManager;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.World;

/**
 * Perform synchronized scene actions without locking the GUI.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class SceneManager extends Thread {

	/**
	 * Describes which action is required of the scene manager
	 */
	public enum Action {
		/**
		 * Do nothing
		 */
		NONE,
		/**
		 * Load scene
		 */
		LOAD_SCENE,
		/**
		 * Save scene
		 */
		SAVE_SCENE,
		/**
		 * Load chunks
		 */
		LOAD_CHUNKS,
		/**
		 * Reload chunks
		 */
		RELOAD_CHUNKS,
		/**
		 * Merge render dump
		 */
		MERGE_DUMP
	}

	private static final Logger logger =
			Logger.getLogger(SceneManager.class);

	private final RenderManager renderManager;
	private String sceneName = "";
	private File renderDump;
	private Action action = Action.NONE;
	private Collection<ChunkPosition> chunksToLoad;
	private World world;

	/**
	 * Create new scene manager
	 * @param manager
	 */
	public SceneManager(RenderManager manager) {
		super("Scene Manager");

		this.renderManager = manager;
	}

	@Override
	public void run() {
		try {
			while (!isInterrupted()) {
				synchronized (this) {

					while (action == Action.NONE)
						wait();

					Action currentAction = action;
					action = Action.NONE;

					switch (currentAction) {
					case LOAD_SCENE:
						try {
							renderManager.loadScene(sceneName + ".cvf");
						} catch (IOException e) {
							logger.warn("Could not load scene.\n" +
									"Reason: " + e.getMessage());
						} catch (SceneLoadingError e) {
							logger.warn("Could not open scene description.\n" +
									"Reason: " + e.getMessage());
						} catch (InterruptedException e) {
							logger.warn("Scene loading was interrupted.");
						}
						break;
					case SAVE_SCENE:
						try {
							renderManager.saveScene(sceneName + ".cvf");
						} catch (InterruptedException e1) {
							logger.warn("Scene saving was interrupted.");
						}
						break;
					case LOAD_CHUNKS:
						renderManager.loadChunks(world, chunksToLoad);
						break;
					case RELOAD_CHUNKS:
						renderManager.reloadChunks();
						break;
					case MERGE_DUMP:
						renderManager.mergeDump(renderDump);
						break;
					default:
						break;
					}
				}
			}
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Load the given scene
	 * @param name The name of the scene to load
	 */
	public synchronized void loadScene(String name) {
		sceneName = name;
		action = Action.LOAD_SCENE;
		notify();
	}

	/**
	 * Save the current scene with the given name
	 * @param name The name to save the scene with
	 */
	public synchronized void saveScene(String name) {
		sceneName = name;
		action = Action.SAVE_SCENE;
		notify();
	}

	/**
	 * @param world
	 * @param chunks
	 */
	public synchronized void loadChunks(World world, Collection<ChunkPosition> chunks) {
		chunksToLoad = chunks;
		this.world = world;
		action = Action.LOAD_CHUNKS;
		notify();
	}

	/**
	 * Reload all chunks
	 */
	public synchronized void reloadChunks() {
		action = Action.RELOAD_CHUNKS;
		notify();
	}

	/**
	 * Merge render dump
	 * @param renderDump
	 */
	public synchronized void mergeRenderDump(File renderDump) {
		this.renderDump = renderDump;
		action = Action.MERGE_DUMP;
		notify();
	}

	/**
 	 * Find a preferred scene name by attempting to avoid name collisions
 	 * @param name
 	 * @return the preferred scene name
 	 */
	public static String preferredSceneName(RenderContext context, String name) {
		String suffix = "";
		int count = 0;
		do {
			String targetName = name + suffix;
			File targetFile = new File(
					context.getSceneDirectory(),
					targetName + ".cvf");
			if (!targetFile.exists()) {
				return targetName;
			}
			count += 1;
			suffix = ""+count;
		} while (count < 256);
		// give up
		return name;
	}

	/**
	 * Lets the user decide whether or not to overwrite existing scenes
	 * @param context
	 * @param sceneName
	 * @return <code>true</code> if the user accepts a possible overwrite of an existing scene
	 */
	public static boolean acceptSceneName(RenderContext context, String sceneName) {
		File targetFile = new File(
				context.getSceneDirectory(),
				sceneName + ".cvf");

		if (targetFile.exists()) {

			if (targetFile.isDirectory()) {
				logger.warn(String.format("Can not create a scene with the name %s.\n" +
						"A directory with that name already exists!", sceneName));
				return false;
			}
			Object[] options = { Messages.getString("Chunky.Cancel_lbl"), //$NON-NLS-1$
					Messages.getString("Chunky.AcceptOverwrite_lbl") }; //$NON-NLS-1$
			int n = JOptionPane.showOptionDialog(null,
					String.format("A scene already exists with the name %s. " +
							"Are you sure you want to overwrite this scene?",
							targetFile.getName()),
					"Confirm Scene Overwrite",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE,
					null,
					options,
					options[0]);

			return n == 1;

		} else {

			return true;
		}
	}
}
