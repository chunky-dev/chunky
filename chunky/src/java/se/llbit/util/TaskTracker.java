/*
 * Copyright (c) 2016-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2016-2021 Chunky Contributors
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

/**
 * A task tracker is used to update a progress listener with current task progress.
 * The task tracker has a stack of tasks. When a new task is created the previous
 * task is remembered so when the new task is finished the old task will be the
 * current one.
 */
public class TaskTracker {
  public final static TaskTracker NONE = new TaskTracker(null) {
    @Override
    public Task task(String taskName, int taskSize) {
      return Task.NONE;
    }

    @Override
    public Task backgroundTask() {
      return Task.NONE;
    }
  };

  public interface TaskBuilder {
    Task newTask(TaskTracker tracker, Task previous, String name, int size);
  }

  private final ProgressListener progress;
  private final TaskBuilder taskBuilder;
  private final Task backgroundTask;
  private Task currentTask;

  public TaskTracker(ProgressListener progress) {
    this(progress, Task::new);
  }

  public TaskTracker(ProgressListener progress, TaskBuilder taskBuilder) {
    this(progress, taskBuilder, taskBuilder);
  }

  public TaskTracker(ProgressListener progress, TaskBuilder taskBuilder,
      TaskBuilder backgroundTaskBuilder) {
    this.progress = progress;
    this.taskBuilder = taskBuilder;
    this.backgroundTask = backgroundTaskBuilder.newTask(this, null, "N/A", 1);
    currentTask = backgroundTask;
  }

  public static class Task implements AutoCloseable {
    public static final Task NONE = new Task(null, null, "None", 1) {
      @Override protected void update() { }
      @Override protected void updateEta() { }
      @Override public void close() { }
    };

    private String taskName;
    private int target;
    private int done;
    protected final TaskTracker tracker;
    protected final Task previous;
    private String eta = "";
    protected long startTime;

    public Task(TaskTracker tracker, Task previous, String taskName, int size) {
      this.tracker = tracker;
      this.previous = previous;
      this.taskName = taskName;
      this.done = 0;
      this.target = size;
      this.startTime = System.currentTimeMillis();
    }

    @Override public void close() {
      tracker.currentTask = previous;
      previous.update();
    }

    protected void update() {
      tracker.updateProgress(taskName, target, done, eta);
    }

    /** Set the current progress. */
    public void update(int done) {
      this.done = done;
      update();
    }

    /** Set the current progress. */
    public void update(int target, int done) {
      this.done = done;
      this.target = target;
      update();
    }

    /** Changes the task name and state. */
    public void update(String task, int target, int done) {
      update(task, target, done, "");
    }

    /** Changes the task name and state. */
    public void update(String task, int target, int done, String eta) {
      this.taskName = task;
      this.done = done;
      this.target = target;
      this.eta = eta;
      update();
    }

    protected void updateEta() {
      long etaSeconds = 0;
      if (done > 0) {
        etaSeconds = ((target - done) * (System.currentTimeMillis() - startTime) / 1000) / done;
      }
      if (etaSeconds > 0) {
        int seconds = (int) ((etaSeconds) % 60);
        int minutes = (int) ((etaSeconds / 60) % 60);
        int hours = (int) (etaSeconds / 3600);
        eta = String.format("%d:%02d:%02d", hours, minutes, seconds);
      } else {
        eta = "N/A";
      }
      update();
    }

    /** Set the current progress and calculate an ETA. */
    public void updateEta(int done) {
      this.done = done;
      updateEta();
    }

    /** Set the current progress and calculate an ETA. */
    public void updateEta(int target, int done) {
      this.done = done;
      this.target = target;
      updateEta();
    }

    /** Reset the ETA start time. */
    public void updateStartTime() {
      this.startTime = System.currentTimeMillis();
    }

    /** Ratelimited update. Only update when the new progress is greater than the old progress + {@code interval} */
    public void updateInterval(int target, int done, int interval) {
      if (target != this.target || done > this.done + interval) {
        this.updateEta(target, done);
      }
    }

    public void updateInterval(int done, int interval) {
      if (done > this.done + interval) {
        this.updateEta(done);
      }
    }
  }

  private void updateProgress(String taskName, int target, int done, String eta) {
    if (!eta.isEmpty()) {
      progress.setProgress(taskName, done, 0, target, eta);
    } else {
      progress.setProgress(taskName, done, 0, target);
    }
  }

  public final Task task(String taskName) {
    return task(taskName, 1);
  }

  public Task task(String taskName, int taskSize) {
    Task task = taskBuilder.newTask(this, currentTask, taskName, taskSize);
    currentTask = task;
    task.update();
    return task;
  }

  public Task backgroundTask() {
    return backgroundTask;
  }
}
