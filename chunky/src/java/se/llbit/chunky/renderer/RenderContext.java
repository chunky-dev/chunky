/* Copyright (c) 2012-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2012-2021 Chunky contributors
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

import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.main.ChunkyOptions;

import java.io.File;

/**
 * Rendering context - keeps track of the Chunky configuration used for rendering.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class RenderContext implements SceneIOProvider {

    protected final Chunky chunky;
    protected final ChunkyOptions config;
    public RenderWorkerPool.Factory renderPoolFactory = RenderWorkerPool::new;
    private File sceneDirectory;

    /**
     * Construct a new render context.
     */
    public RenderContext(Chunky chunky) {
        this.chunky = chunky;
        this.config = chunky.options;
        this.sceneDirectory = config.sceneDir;
    }

    public Chunky getChunky() {
        return chunky;
    }

    /**
     * Set the saving/loading directory of the current scene.
     *
     * @param sceneDirectory The directory the scene should be saved/loaded to/from
     */
    public void setSceneDirectory(File sceneDirectory) {
        this.sceneDirectory = sceneDirectory;
    }

    /**
     * @return File handle to the scene directory.
     */
    @Override
    public File getSceneDirectory() {
        return sceneDirectory;
    }

    /**
     * @return The preferred number of rendering threads.
     */
    public int numRenderThreads() {
        return config.renderThreads;
    }

    /**
     * Gets a File object of the given scene file. If the scene directory doesn't exist, it will be created.
     *
     * @param fileName the filename with the extension
     * @return File object of the given scene file
     */
    @Override
    public File getSceneFile(String fileName) {
        if (!sceneDirectory.exists()) {
            sceneDirectory.mkdirs();
        }
        return new File(sceneDirectory, fileName);
    }

    /**
     * @return The tile width.
     */
    public int tileWidth() {
        return config.tileWidth;
    }

    /**
     * @return The samples per pixel per pass
     */
    public int sppPerPass() {
        return config.sppPerPass;
    }
}
