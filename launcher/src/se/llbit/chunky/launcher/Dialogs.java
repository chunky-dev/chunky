/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * Utility class for showing dialogs.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public abstract class Dialogs {
  private static final String DEFAULT_WARNING_TITLE = "Warning";
  private static final String DEFAULT_ERROR_TITLE = "Error";
  private static final String DEFAULT_MESSAGE_TITLE = "Message";

  public static final void message(Component component, String message) {
    error(component, message, DEFAULT_MESSAGE_TITLE);
  }

  public static final void message(Component component, String message, String title) {
    JOptionPane.showMessageDialog(component, message, title, JOptionPane.PLAIN_MESSAGE);
  }

  public static final void error(Component component, String message) {
    error(component, message, DEFAULT_ERROR_TITLE);
  }

  public static final void error(Component component, String message, String title) {
    JOptionPane.showMessageDialog(component, message, title, JOptionPane.ERROR_MESSAGE);
  }

  public static final void warning(Component component, String message) {
    warning(component, message, DEFAULT_WARNING_TITLE);
  }

  public static final void warning(Component component, String message, String title) {
    JOptionPane.showMessageDialog(component, message, title, JOptionPane.WARNING_MESSAGE);
  }
}
