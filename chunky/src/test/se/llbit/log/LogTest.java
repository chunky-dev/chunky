/*
 * Copyright (c) 2017 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.log;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IllegalFormatConversionException;
import java.util.MissingFormatArgumentException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for the logging utility class.
 */
public class LogTest {
  private final static Receiver DO_NOTHING_RECEIVER = new Receiver() {
    @Override public void logEvent(Level level, String message) {
    }
  };

  @Test
  public void testReceiver1() {
    assertThrows(IllegalArgumentException.class, () ->
      Log.setReceiver(DO_NOTHING_RECEIVER)
    );
  }

  @Test public void testReceiver2() {
    assertThrows(IllegalArgumentException.class, () ->
      Log.setReceiver(null, Level.INFO)
    );
  }

  @Test public void testReceiver3() {
    Log.setLevel(Level.INFO);
    Collection<String> messages = new ArrayList<>();
    Log.setReceiver(new Receiver() {
      @Override public void logEvent(Level level, String message) {
        messages.add(message);
      }
    }, Level.INFO);
    Log.info("Foo %s");
    Log.infof("Bar %s", "Baz");
    assertThat(messages).containsExactly("Foo %s", "Bar Baz");
  }

  @Test public void testInfo1() {
    Log.setLevel(Level.INFO);
    Log.setReceiver(DO_NOTHING_RECEIVER, Level.INFO);
    Log.info("Not a format specifier: %s");
    Log.info("Not a format specifier: %d");
  }

  @Test public void testInfo2() {
    Log.setLevel(Level.INFO);
    Log.setReceiver(DO_NOTHING_RECEIVER, Level.INFO);
    assertThrows(MissingFormatArgumentException.class, () ->
      Log.infof("Missing argument %s")
    );
  }

  @Test public void testInfo3() {
    Log.setLevel(Level.INFO);
    Log.setReceiver(DO_NOTHING_RECEIVER, Level.INFO);
    assertThrows(IllegalFormatConversionException.class, () ->
      Log.infof("Wrong argument type %d", 0.1)
    );
  }

  @Test public void testWarning1() {
    Log.setLevel(Level.WARNING);
    Log.setReceiver(DO_NOTHING_RECEIVER, Level.WARNING);
    Log.warn("Not a format specifier: %s");
    Log.warn("Not a format specifier: %d");
  }

  @Test public void testWarning2() {
    Log.setLevel(Level.WARNING);
    Log.setReceiver(DO_NOTHING_RECEIVER, Level.WARNING);
    assertThrows(MissingFormatArgumentException.class, () ->
      Log.warnf("Missing argument %s")
    );
  }

  @Test public void testWarning3() {
    Log.setLevel(Level.WARNING);
    Log.setReceiver(DO_NOTHING_RECEIVER, Level.WARNING);
    assertThrows(IllegalFormatConversionException.class, () ->
      Log.warnf("Wrong argument type %d", 0.1)
    );
  }

  @Test public void testError1() {
    Log.setLevel(Level.ERROR);
    Log.setReceiver(DO_NOTHING_RECEIVER, Level.ERROR);
    Log.error("Not a format specifier: %s");
    Log.error("Not a format specifier: %d");
  }

  @Test public void testError2() {
    Log.setLevel(Level.ERROR);
    Log.setReceiver(DO_NOTHING_RECEIVER, Level.ERROR);
    assertThrows(MissingFormatArgumentException.class, () ->
      Log.errorf("Missing argument %s")
    );
  }

  @Test public void testError3() {
    Log.setLevel(Level.ERROR);
    Log.setReceiver(DO_NOTHING_RECEIVER, Level.ERROR);
    assertThrows(IllegalFormatConversionException.class, () ->
      Log.errorf("Wrong argument type %d", 0.1)
    );
  }
}
