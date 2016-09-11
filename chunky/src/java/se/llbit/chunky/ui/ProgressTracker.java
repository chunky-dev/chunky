/* Copyright (c) 2010-2016 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.ui;

/**
 * A panel to display job progress.
 *
 * <p>Jobs that use the progress panel can be interrupted
 * if they use the isInterrupted method to check the interrupted
 * status of the progress panel.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public interface ProgressTracker {

  ProgressTracker NONE = new ProgressTracker() {

    @Override public boolean isInterrupted() {
      return false;
    }

    @Override public void setJobName(String jobName) {
    }

    @Override public void setJobSize(int size) {
    }

    @Override public void setProgress(int value) {
    }

    @Override public boolean tryStartJob() {
      return true;
    }

    @Override public void finishJob() {
    }

    @Override public boolean isBusy() {
      return false;
    }
  };

  boolean isInterrupted();

  void setJobName(String jobName);

  void setJobSize(int size);

  void setProgress(int value);

  boolean tryStartJob();

  void finishJob();

  boolean isBusy();
}
