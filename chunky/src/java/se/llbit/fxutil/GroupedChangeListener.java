/*
 * Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.fxutil;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * A change listener which is non-recursive within a group
 * of other change listeners. Any recursive
 * change events do not trigger the nested change listener
 * on any other of the change listeners in the same group.
 *
 * <p>The change listeners in a single group must all be invoked in the
 * same thread. The grouped change listeners do not mutually exclude
 * each other if they are called from different threads.
 *
 * <p>This class is not thread-safe: concurrent changes are not supported!
 *
 * @param <T>
 */
public class GroupedChangeListener<T> implements ChangeListener<T> {
  private final ChangeListener<T> listener;
  private final ListenerGroup group;


  /**
   * Represents a group of change listeners which should not be invoked recursively.
   */
  public static class ListenerGroup {
    private boolean recursive = false;
  }

  /** Creates a new change listener group. */
  public static ListenerGroup newGroup() {
    return new ListenerGroup();
  }

  /**
   * @param group the group which this listener is linked to
   * @param listener the nested change listener - called when the change listener
   * should trigger (non-recursively only)
   */
  public GroupedChangeListener(ListenerGroup group, ChangeListener<T> listener) {
    this.listener = listener;
    this.group = group;
  }

  @Override public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
    if (!group.recursive) {
      try {
        group.recursive = true;
        listener.changed(observable, oldValue, newValue);
      } finally {
        group.recursive = false;
      }
    }
  }
}
