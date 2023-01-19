/*
 * Copyright (c) 2023 Chunky contributors
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

package se.llbit.chunky.ui.render;

import javafx.scene.Node;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.builder.Configurable;
import se.llbit.chunky.ui.builder.UiBuilder;
import se.llbit.chunky.ui.builder.javafx.FxBuildableUi;
import se.llbit.chunky.ui.controller.RenderControlsFxController;

public abstract class AbstractRenderControlsTab implements RenderControlsTab, Configurable {
  protected RenderControlsFxController controller;
  protected Scene scene;

  protected final FxBuildableUi ui;
  protected final String tabTitle;

  public AbstractRenderControlsTab(String tabTitle) {
    this.tabTitle = tabTitle;
    this.ui = new FxBuildableUi();
  }

  @Override
  public abstract void build(UiBuilder builder);

  public void update() {
    this.ui.build(this);
  }

  @Override
  public String getTabTitle() {
    return tabTitle;
  }

  @Override
  public Node getTabContent() {
    return ui;
  }

  @Override
  public void update(Scene scene) {
    this.scene = scene;
    update();
  }

  @Override
  public void setController(RenderControlsFxController controller) {
    this.controller = controller;
  }
}
