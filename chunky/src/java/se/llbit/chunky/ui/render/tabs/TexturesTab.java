/* Copyright (c) 2016 - 2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2016 - 2021 Chunky contributors
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
package se.llbit.chunky.ui.render.tabs;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.scene.SceneManager;
import se.llbit.chunky.ui.builder.CheckboxInput;
import se.llbit.chunky.ui.builder.UiBuilder;
import se.llbit.chunky.ui.dialogs.ResourcePackChooser;
import se.llbit.chunky.ui.render.AbstractRenderControlsTab;
import se.llbit.chunky.world.Icon;
import se.llbit.fxutil.Dialogs;
import se.llbit.log.Log;

import java.io.IOException;

public class TexturesTab extends AbstractRenderControlsTab {

  public TexturesTab() {
    super("Textures & Resource Packs");
  }

  @Override
  public void build(UiBuilder builder) {
    CheckboxInput biomeColors = builder.checkbox();
    CheckboxInput biomeBlending = builder.checkbox();

    biomeColors
      .setName("Enable biome colors")
      .setTooltip("Color grass and tree leaves according to the biome.")
      .set(scene.biomeColorsEnabled())
      .addCallback(scene::setBiomeColorsEnabled)
      .addCallback(value -> {
        boolean enabled = scene.biomeColorsEnabled();
        scene.setBiomeColorsEnabled(enabled);
        biomeBlending.setDisable(!value);

        if (!scene.haveLoadedChunks()) {
          return;
        }
        if (enabled != value && value) {
          alertIfReloadNeeded("biome colors");
        }
      });

    biomeBlending
      .setName("Enable biome blending")
      .setTooltip("Blend edges of biomes (looks better but loads slower).")
      .set(scene.biomeBlendingEnabled())
      .setDisable(!scene.biomeColorsEnabled())
      .addCallback(value -> {
        boolean enabled = scene.biomeBlendingEnabled();
        scene.setBiomeBlendingEnabled(value);
        if (enabled != value) {
          alertIfReloadNeeded("biome blending");
        }
      });

    builder.checkbox()
      .setName("Single color textures")
      .setTooltip("Set block textures to a single color which is the average of all color values of its current texture.")
      .set(PersistentSettings.getSingleColorTextures())
      .addCallback(PersistentSettings::setSingleColorTextures)
      .addCallback(e -> {
        scene.refresh();
        scene.rebuildBvh();
      });

    builder.button()
      .setText("Edit resource packs")
      .setTooltip("Select resource packs Chunky uses to load textures.")
      .setGraphic(Icon.pencil)
      .addCallback(btn -> {
        try {
          SceneManager sceneManager = controller.getSceneManager();
          ResourcePackChooser resourcePackChooser = new ResourcePackChooser(
            sceneManager.getScene(),
            sceneManager.getTaskTracker()
          );
          resourcePackChooser.show();
        } catch (IOException ex) {
          Log.error("Failed to create resource pack chooser window.", ex);
        }
      });
  }

  private void alertIfReloadNeeded(String changedFeature) {
    if(!scene.haveLoadedChunks()) {
      return;
    }
    Alert warning = Dialogs.createAlert(Alert.AlertType.CONFIRMATION);
    warning.setContentText("The selected chunks need to be reloaded in order for "+changedFeature+" to update.");
    warning.getButtonTypes().setAll(
      ButtonType.CANCEL,
      new ButtonType("Reload chunks", ButtonBar.ButtonData.FINISH));
    warning.setTitle("Chunk reload required");
    ButtonType result = warning.showAndWait().orElse(ButtonType.CANCEL);
    if (result.getButtonData() == ButtonBar.ButtonData.FINISH) {
      controller.getSceneManager().reloadChunks();
    }
  }
}
