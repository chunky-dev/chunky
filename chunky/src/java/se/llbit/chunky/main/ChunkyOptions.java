package se.llbit.chunky.main;

import java.io.File;

import se.llbit.chunky.renderer.RenderConstants;

/**
 * Current configuration
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChunkyOptions {
	public File sceneDir = null;
	public String sceneName = null;
	public String texturePack = null;
	public int renderThreads = -1;
	public File worldDir = null;
	public int target = -1;

	/**
	 * Whether or not OpenCL rendering is enabled
	 */
	public boolean openCLEnabled;

	public int tileWidth = RenderConstants.TILE_WIDTH_DEFAULT;

	@Override
	public ChunkyOptions clone() {
		ChunkyOptions clone = new ChunkyOptions();
		clone.sceneDir = sceneDir;
		clone.sceneName = sceneName;
		clone.texturePack = texturePack;
		clone.renderThreads = renderThreads;
		clone.worldDir = worldDir;
		return clone;
	}
}
