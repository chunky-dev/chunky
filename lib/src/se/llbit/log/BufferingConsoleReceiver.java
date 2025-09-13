package se.llbit.log;

import java.util.ArrayList;
import java.util.List;

public class BufferingConsoleReceiver extends Receiver {
  public static final BufferingConsoleReceiver INSTANCE = new BufferingConsoleReceiver();

  private final List<Event> bufferedEvents = new ArrayList<>();

  protected BufferingConsoleReceiver() {
  }

  @Override
  public void logEvent(Level level, String message) {
    this.bufferedEvents.add(new Event(level, message));
  }

  @Override
  public boolean isBuffered() {
    return true;
  }

  @Override
  public List<Event> getBufferedEvents() {
    return this.bufferedEvents;
  }
}
