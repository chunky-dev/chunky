package se.llbit.chunky.world;

import java.util.Collections;

public class EmptyDimension extends Dimension {
  public static final EmptyDimension INSTANCE = new EmptyDimension();

  private EmptyDimension() {
    super(EmptyWorld.INSTANCE, 0, null, Collections.emptySet(), -1);
  }

  @Override public String toString() {
    return "[empty dimension]";
  }

}