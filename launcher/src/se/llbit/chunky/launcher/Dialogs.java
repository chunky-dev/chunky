/* Copyright (c) 2013-2016 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.launcher;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

/**
 * Utility class for showing warning and error dialogs.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public abstract class Dialogs {

  public static void alert(String title, String content, Alert.AlertType alertType) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setContentText(content);
    // We have to do some adjustments to make the warning dialog resize to
    // the text content on Linux. Source: http://stackoverflow.com/a/33905734
    alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label)
        .forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
    alert.show();
  }

  public static void error(String title, String content) {
    alert(title, content, Alert.AlertType.ERROR);
  }
  public static void warning(String title, String content) {
    alert(title, content, Alert.AlertType.WARNING);
  }
}
