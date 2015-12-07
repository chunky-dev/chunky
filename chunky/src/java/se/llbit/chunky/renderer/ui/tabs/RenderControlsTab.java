/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer.ui.tabs;

import javax.swing.JPanel;

import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.renderer.RenderManager;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.SceneManager;
import se.llbit.chunky.renderer.ui.RenderControls;

@SuppressWarnings("serial")
public abstract class RenderControlsTab extends JPanel {
	protected final RenderManager renderMan;
	protected final RenderControls renderControls;
	protected final RenderContext context;

	public RenderControlsTab(RenderControls renderControls) {
		this.renderControls = renderControls;
		this.renderMan = renderControls.getRenderManager();
		this.context = renderControls.getContext();
	}

	public abstract void refreshSettings();

	protected SceneManager sceneManager() {
		return renderControls.getSceneManager();
	}

	protected Chunky getChunky() {
		return renderControls.getChunky();
	}

	protected Scene getScene() {
		return renderMan.scene();
	}
}
