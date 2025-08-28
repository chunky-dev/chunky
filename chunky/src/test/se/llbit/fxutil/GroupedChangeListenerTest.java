/* Copyright (c) 2019 Jesper Öqvist <jesper@llbit.se>
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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GroupedChangeListenerTest {
  @Test public void testRecursive() {
    BooleanProperty p1 = new SimpleBooleanProperty(false);
    BooleanProperty p2 = new SimpleBooleanProperty(false);
    BooleanProperty p3 = new SimpleBooleanProperty(false);

    p1.addListener((observable, oldValue, newValue) -> p2.setValue(true));
    p2.addListener((observable, oldValue, newValue) -> p3.setValue(true));

    assertFalse(p3.getValue());
    p1.setValue(true);
    assertTrue(p3.getValue());
  }

  @Test public void testGrouped() {
    BooleanProperty p1 = new SimpleBooleanProperty(false);
    BooleanProperty p2 = new SimpleBooleanProperty(false);
    BooleanProperty p3 = new SimpleBooleanProperty(false);

    GroupedChangeListener.ListenerGroup group = GroupedChangeListener.newGroup();

    p1.addListener(
        new GroupedChangeListener<>(group, (observable, oldValue, newValue) -> p2.setValue(true)));
    p2.addListener(
        new GroupedChangeListener<>(group, (observable, oldValue, newValue) -> p3.setValue(true)));

    p1.setValue(true);
    assertFalse(p3.getValue());
  }
}
