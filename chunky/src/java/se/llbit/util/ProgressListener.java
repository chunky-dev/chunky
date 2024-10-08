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
package se.llbit.util;

import java.time.Duration;

/**
 * Progress listener.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public interface ProgressListener {
  ProgressListener NONE = (task, done, start, target, duration) -> {};

  /**
   * Update progress without ETA.
   */
  void setProgress(String task, int done, int start, int target, Duration elapsedTime);

  /**
   * Update progress with ETA.
   */
  default void setProgress(String task, int done, int start, int target, Duration elapsedTime, Duration remainingTime) {
    setProgress(task, done, start, target, elapsedTime);
  }
}
