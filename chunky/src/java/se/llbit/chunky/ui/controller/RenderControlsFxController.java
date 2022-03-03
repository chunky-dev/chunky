/* Copyright (c) 2016-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2016-2021 Chunky contributors
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
package se.llbit.chunky.ui.controller;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import se.llbit.chunky.renderer.RenderController;
import se.llbit.chunky.renderer.RenderManager;
import se.llbit.chunky.renderer.scene.AsynchronousSceneManager;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.RenderCanvasFx;
import se.llbit.chunky.ui.render.tabs.AdvancedTab;
import se.llbit.chunky.ui.render.tabs.CameraTab;
import se.llbit.chunky.ui.render.tabs.EntitiesTab;
import se.llbit.chunky.ui.render.tabs.GeneralTab;
import se.llbit.chunky.ui.render.tabs.HelpTab;
import se.llbit.chunky.ui.render.tabs.LightingTab;
import se.llbit.chunky.ui.render.tabs.MaterialsTab;
import se.llbit.chunky.ui.render.tabs.PostprocessingTab;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.chunky.ui.render.tabs.SkyTab;
import se.llbit.chunky.ui.render.tabs.WaterTab;
import se.llbit.fx.ToolPane;
import se.llbit.fx.ToolTab;
import se.llbit.log.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Controller for the Render Controls dialog.
 */
public class RenderControlsFxController {
  private AsynchronousSceneManager asyncSceneManager;

  public ChunkyFxController getChunkyController() {
    return controller;
  }

  public RenderController getRenderController() {
    return controller.getChunky().getRenderController();
  }

  private Scene scene;

  private final RenderCanvasFx canvas;
  private final RenderManager renderManager;

  private Collection<RenderControlsTab> tabs = new ArrayList<>();

  /** Maps JavaFX tabs to tab controllers. */
  private final Tooltip tooltip;

  private final ToolPane toolPane;

  private ChunkyFxController controller;

  private final Map<RenderControlsTab, ToolTab> tabMap = new IdentityHashMap<>();

  public RenderControlsFxController(ChunkyFxController controller, ToolPane toolPane,
      RenderCanvasFx canvas, RenderManager renderManager) {
    this.controller = controller;
    this.toolPane = toolPane;
    this.canvas = canvas;
    this.renderManager = renderManager;
    tooltip = new Tooltip();
    tooltip.setConsumeAutoHidingEvents(false);
    tooltip.setAutoHide(true);

    RenderController renderController = controller.getChunky().getRenderController();
    scene = renderController.getSceneManager().getScene();
    buildTabs();
    tabs.forEach(tab -> tab.setController(this));
    asyncSceneManager =
        (AsynchronousSceneManager) renderController.getSceneManager();
    asyncSceneManager.setOnChunksLoaded(() -> Platform.runLater(() -> {
      tabs.forEach(RenderControlsTab::onChunksLoaded);
      controller.showRenderPreview();
      this.refreshSettings();
    }));
  }

  private void buildTabs() {
    try {
      // Create the default tabs:
      tabs.add(new GeneralTab());
      tabs.add(new LightingTab());
      tabs.add(new SkyTab());
      tabs.add(new WaterTab());
      tabs.add(new CameraTab());
      tabs.add(new EntitiesTab());
      tabs.add(new MaterialsTab());
      tabs.add(new PostprocessingTab());
      tabs.add(new AdvancedTab());
      tabs.add(new HelpTab());

      // Transform tabs (allows plugin hooks to modify the tabs):
      tabs = controller.getChunky().getRenderControlsTabTransformer().apply(tabs);

      if (tabs.contains(null)) {
        Log.error("Null tabs inserted in tab collection (possible plugin error).");
        tabs = tabs.stream().filter(Objects::nonNull).collect(Collectors.toList());
      }

      Platform.runLater(() -> {
        // We run this in runLater because it fails if run directly, for unknown reasons.
        // TODO(llbit): check why adding tabs has to happen inside runLater!
        for (RenderControlsTab tab : tabs) {
          ToolTab toolTab = new ToolTab(tab.getTabTitle(), tab.getTabContent());
          tabMap.put(tab, toolTab);
          toolPane.getTabs().add(toolTab);
          toolTab.selectedProperty().addListener((observable, oldValue, selected) -> {
            if (selected) {
              tab.update(scene);
            }
          });
        }
      });
    } catch (Throwable e) {
      Log.error("Failed to build render controls tabs.", e);
    }
  }

  public void refreshSettings() {
    for (Map.Entry<RenderControlsTab, ToolTab> entry : tabMap.entrySet()) {
      if (entry.getValue().getSelected()) {
        entry.getKey().update(scene);
      }
    }
  }

  public RenderCanvasFx getCanvas() {
    return canvas;
  }

  /**
   * Shows a simple popup with a tooltip type message.
   * The popup is displayed directly below the given scene node.
   *
   * @param node the scene node to display the popup below
   */
  public void showPopup(String message, Region node) {
    if (node.getScene() != null && node.getScene().getWindow() != null) {
      Point2D offset = node.localToScene(0, 0);
      tooltip.setText(message);
      tooltip.show(node,
          offset.getX() + node.getScene().getX() + node.getScene().getWindow().getX(),
          offset.getY() + node.getScene().getY() + node.getScene().getWindow().getY()
              + node.getHeight());
    }
  }
}
